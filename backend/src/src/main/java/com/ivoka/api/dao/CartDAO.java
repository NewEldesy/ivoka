package com.ivoka.api.dao;

import com.ivoka.api.models.Cart;
import com.ivoka.api.models.CartItem;
import com.ivoka.api.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    /**
     * Retrieve a cart by user ID
     */
    public Cart getCartByUserId(int userId) throws SQLException {
        String sql = "SELECT id, user_id, session_id, created_at, updated_at FROM carts WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cart cart = mapResultSetToCart(rs);
                    // Load cart items
                    cart.setItems(getCartItems(cart.getId()));
                    return cart;
                }
            }
        }

        return null;
    }

    /**
     * Retrieve a cart by session ID (for guest users)
     */
    public Cart getCartBySessionId(String sessionId) throws SQLException {
        String sql = "SELECT id, user_id, session_id, created_at, updated_at FROM carts WHERE session_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cart cart = mapResultSetToCart(rs);
                    // Load cart items
                    cart.setItems(getCartItems(cart.getId()));
                    return cart;
                }
            }
        }

        return null;
    }

    /**
     * Create a new cart for a user or guest
     */
    public Cart createCart(Integer userId, String sessionId) throws SQLException {
        String sql = "INSERT INTO carts (user_id, session_id, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (userId != null) {
                stmt.setInt(1, userId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, sessionId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Cart cart = new Cart();
                        cart.setId(generatedKeys.getInt(1));
                        cart.setUserId(userId);
                        cart.setSessionId(sessionId);
                        cart.setItems(new ArrayList<>());
                        return cart;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Add an item to cart (or update quantity if already exists)
     */
    public boolean addItemToCart(int cartId, int productId, int quantity) throws SQLException {
        // First check if item already exists in cart
        String checkSql = "SELECT id, quantity FROM cart_items WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, cartId);
            checkStmt.setInt(2, productId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Item exists, update quantity
                    int existingId = rs.getInt("id");
                    int existingQuantity = rs.getInt("quantity");
                    return updateItemQuantity(existingId, existingQuantity + quantity);
                }
            }
        }

        // Item doesn't exist, insert new
        String insertSql = "INSERT INTO cart_items (cart_id, product_id, quantity, added_at) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            insertStmt.setInt(1, cartId);
            insertStmt.setInt(2, productId);
            insertStmt.setInt(3, quantity);

            return insertStmt.executeUpdate() > 0;
        }
    }

    /**
     * Update quantity of a cart item
     */
    public boolean updateItemQuantity(int itemId, int quantity) throws SQLException {
        if (quantity <= 0) {
            return removeItemFromCart(itemId);
        }

        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, itemId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Remove an item from cart
     */
    public boolean removeItemFromCart(int itemId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Clear all items from a cart
     */
    public boolean clearCart(int cartId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Get all items in a cart
     */
    private List<CartItem> getCartItems(int cartId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ci.id, ci.cart_id, ci.product_id, ci.quantity, ci.added_at, " +
                    "p.id, p.name, p.description, p.price, p.category, p.image_url, p.available, " +
                    "p.created_at, p.updated_at " +
                    "FROM cart_items ci " +
                    "JOIN products p ON ci.product_id = p.id " +
                    "WHERE ci.cart_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("ci.id"));
                    item.setCartId(rs.getInt("ci.cart_id"));
                    item.setQuantity(rs.getInt("ci.quantity"));

                    if (rs.getTimestamp("ci.added_at") != null) {
                        item.setAddedAt(rs.getTimestamp("ci.added_at").toLocalDateTime());
                    }

                    // Map product
                    Product product = new Product();
                    product.setId(rs.getInt("p.id"));
                    product.setName(rs.getString("p.name"));
                    product.setDescription(rs.getString("p.description"));
                    product.setPrice(rs.getBigDecimal("p.price"));
                    product.setCategory(rs.getString("p.category"));
                    product.setImageUrl(rs.getString("p.image_url"));
                    product.setAvailable(rs.getBoolean("p.available"));

                    if (rs.getTimestamp("p.created_at") != null) {
                        product.setCreatedAt(rs.getTimestamp("p.created_at").toLocalDateTime());
                    }
                    if (rs.getTimestamp("p.updated_at") != null) {
                        product.setUpdatedAt(rs.getTimestamp("p.updated_at").toLocalDateTime());
                    }

                    item.setProduct(product);
                    items.add(item);
                }
            }
        }

        return items;
    }

    private Cart mapResultSetToCart(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setId(rs.getInt("id"));

        int userId = rs.getInt("user_id");
        if (rs.wasNull()) {
            cart.setUserId(null);
        } else {
            cart.setUserId(userId);
        }

        cart.setSessionId(rs.getString("session_id"));

        if (rs.getTimestamp("created_at") != null) {
            cart.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            cart.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return cart;
    }
}
