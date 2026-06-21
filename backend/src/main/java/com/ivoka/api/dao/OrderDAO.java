package com.ivoka.api.dao;

import com.ivoka.api.models.Order;
import com.ivoka.api.models.OrderItem;
import com.ivoka.api.models.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /**
     * Crée une nouvelle commande avec ses éléments
     */
    public Order createOrder(Order order) throws SQLException {
        String orderSql = "INSERT INTO orders (user_id, order_number, total_amount, status, shipping_address, billing_address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {

                if (order.getUserId() > 0) {
                    stmt.setInt(1, order.getUserId());
                } else {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                }
                stmt.setString(2, order.getOrderNumber());
                stmt.setBigDecimal(3, order.getTotalAmount());
                stmt.setString(4, order.getStatus() != null ? order.getStatus() : "pending");
                stmt.setString(5, order.getShippingAddress());
                stmt.setString(6, order.getBillingAddress());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    throw new SQLException("La création de la commande a échoué.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        order.setId(orderId);

                        // Créer les éléments de la commande
                        if (order.getItems() != null && !order.getItems().isEmpty()) {
                            createOrderItems(conn, orderId, order.getItems());
                        }

                        conn.commit();
                        return order;
                    } else {
                        conn.rollback();
                        throw new SQLException("Impossible de récupérer l'ID de la commande.");
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Crée les éléments d'une commande
     */
    private void createOrderItems(Connection conn, int orderId, List<OrderItem> items) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (OrderItem item : items) {
                if (item.getProduct() != null) {
                    stmt.setInt(1, orderId);
                    stmt.setInt(2, item.getProduct().getId());
                    stmt.setInt(3, item.getQuantity());
                    stmt.setBigDecimal(4, item.getUnitPrice());
                    stmt.setBigDecimal(5, item.getTotalPrice());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }

    /**
     * Récupère une commande par ID avec ses éléments
     */
    public Order getOrderById(int id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(getOrderItems(conn, id));
                    return order;
                }
            }
        }

        return null;
    }

    /**
     * Récupère toutes les commandes d'un utilisateur
     */
    public List<Order> getOrdersByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(getOrderItems(conn, order.getId()));
                    orders.add(order);
                }
            }
        }

        return orders;
    }

    /**
     * Récupère toutes les commandes (pour admin)
     */
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(getOrderItems(conn, order.getId()));
                orders.add(order);
            }
        }

        return orders;
    }

    /**
     * Récupère les commandes par statut (pour admin)
     */
    public List<Order> getOrdersByStatus(String status) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(getOrderItems(conn, order.getId()));
                    orders.add(order);
                }
            }
        }

        return orders;
    }

    /**
     * Récupère les éléments d'une commande
     */
    private List<OrderItem> getOrderItems(Connection conn, int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.* FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = mapResultSetToOrderItem(rs);
                    items.add(item);
                }
            }
        }

        return items;
    }

    /**
     * Met à jour le statut d'une commande
     */
    public boolean updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, orderId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour une commande
     */
    public boolean updateOrder(Order order) throws SQLException {
        String sql = "UPDATE orders SET total_amount = ?, status = ?, shipping_address = ?, " +
                "billing_address = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, order.getTotalAmount());
            stmt.setString(2, order.getStatus());
            stmt.setString(3, order.getShippingAddress());
            stmt.setString(4, order.getBillingAddress());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, order.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Annule une commande
     */
    public boolean cancelOrder(int orderId) throws SQLException {
        Order order = getOrderById(orderId);
        if (order != null && order.canBeCancelled()) {
            return updateOrderStatus(orderId, "cancelled");
        }
        return false;
    }

    /**
     * Supprime une commande (et ses éléments)
     */
    public boolean deleteOrder(int orderId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM orders WHERE id = ?")) {

            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Récupère le numéro de commande suivant
     */
    public String generateOrderNumber() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM orders";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt("total") + 1;
                return String.format("IVK%d%05d", java.time.Year.now().getValue(), count);
            }
        }

        return "IVK202400001";
    }

    /**
     * Mappe un ResultSet à un objet Order
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setBillingAddress(rs.getString("billing_address"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            order.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return order;
    }

    /**
     * Mappe un ResultSet à un objet OrderItem
     */
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getInt("oi.id"));
        item.setOrderId(rs.getInt("oi.order_id"));
        item.setQuantity(rs.getInt("oi.quantity"));
        item.setUnitPrice(rs.getBigDecimal("oi.unit_price"));
        item.setTotalPrice(rs.getBigDecimal("oi.total_price"));

        // Mapper le produit
        Product product = new Product();
        product.setId(rs.getInt("p.id"));
        product.setName(rs.getString("p.name"));
        product.setDescription(rs.getString("p.description"));
        product.setPrice(rs.getBigDecimal("p.price"));
        product.setCategory(rs.getString("p.category"));
        product.setImageUrl(rs.getString("p.image_url"));
        item.setProduct(product);

        return item;
    }
}
