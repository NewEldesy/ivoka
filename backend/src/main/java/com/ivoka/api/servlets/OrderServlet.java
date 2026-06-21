package com.ivoka.api.servlets;

import com.ivoka.api.dao.OrderDAO;
import com.ivoka.api.models.Order;
import com.ivoka.api.models.OrderItem;
import com.ivoka.api.models.Product;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/orders/*")
public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            String status = request.getParameter("status");
            String userId = request.getParameter("userId");

            if (pathInfo != null && pathInfo.length() > 1) {
                // Récupérer une commande par ID
                int orderId = Integer.parseInt(pathInfo.substring(1));
                Order order = orderDAO.getOrderById(orderId);

                if (order != null) {
                    out.print(createOrderJson(order).build().toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Commande non trouvée")
                            .build();
                    out.print(error.toString());
                }
            } else if (userId != null && !userId.isEmpty()) {
                // Récupérer les commandes d'un utilisateur
                int userIdInt = Integer.parseInt(userId);
                List<Order> orders = orderDAO.getOrdersByUserId(userIdInt);
                JsonArrayBuilder jsonArray = Json.createArrayBuilder();

                for (Order order : orders) {
                    jsonArray.add(createOrderJson(order).build());
                }

                out.print(jsonArray.build().toString());
            } else if (status != null && !status.isEmpty()) {
                // Récupérer les commandes par statut (admin)
                List<Order> orders = orderDAO.getOrdersByStatus(status);
                JsonArrayBuilder jsonArray = Json.createArrayBuilder();

                for (Order order : orders) {
                    jsonArray.add(createOrderJson(order).build());
                }

                out.print(jsonArray.build().toString());
            } else {
                // Récupérer toutes les commandes (admin)
                List<Order> orders = orderDAO.getAllOrders();
                JsonArrayBuilder jsonArray = Json.createArrayBuilder();

                for (Order order : orders) {
                    jsonArray.add(createOrderJson(order).build());
                }

                out.print(jsonArray.build().toString());
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la récupération des commandes")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        PrintWriter out = response.getWriter();

        try {
            JsonObject jsonInput = Json.createReader(request.getReader()).readObject();

            Order order = new Order();
            
            // userId est optionnel - peut être 0 ou non fourni pour les commandes anonymes
            if (jsonInput.containsKey("userId")) {
                order.setUserId(jsonInput.getInt("userId"));
            } else {
                order.setUserId(0); // 0 = commande anonyme
            }
            
            order.setTotalAmount(new BigDecimal(jsonInput.getString("totalAmount")));
            order.setShippingAddress(jsonInput.getString("shippingAddress", ""));
            order.setBillingAddress(jsonInput.getString("billingAddress", ""));
            order.setStatus("pending");
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());

            // Générer le numéro de commande
            String orderNumber = orderDAO.generateOrderNumber();
            order.setOrderNumber(orderNumber);

            // Créer les éléments de la commande
            List<OrderItem> items = parseOrderItems(jsonInput);
            order.setItems(items);

            System.out.println("DEBUG: Creating order with userId=" + order.getUserId() + ", " + items.size() + " items");
            for (OrderItem item : items) {
                System.out.println("  - Product ID: " + item.getProduct().getId() + ", Qty: " + item.getQuantity());
            }

            // Créer la commande
            Order createdOrder = orderDAO.createOrder(order);

            if (createdOrder != null && createdOrder.getId() > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(createOrderJson(createdOrder).build().toString());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Erreur lors de la création de la commande")
                        .build();
                out.print(error.toString());
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            System.out.println("ERROR creating order: " + e.getMessage());
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la création de la commande")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "ID de la commande requis")
                        .build();
                out.print(error.toString());
                return;
            }

            int orderId = Integer.parseInt(pathInfo.substring(1));
            JsonObject jsonInput = Json.createReader(request.getReader()).readObject();

            // Vérifier si c'est une mise à jour de statut uniquement
            if (jsonInput.containsKey("status")) {
                String newStatus = jsonInput.getString("status");
                boolean updated = orderDAO.updateOrderStatus(orderId, newStatus);

                if (updated) {
                    Order updatedOrder = orderDAO.getOrderById(orderId);
                    out.print(createOrderJson(updatedOrder).build().toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Commande non trouvée")
                            .build();
                    out.print(error.toString());
                }
            } else {
                // Mise à jour complète de la commande
                Order order = new Order();
                order.setId(orderId);
                order.setTotalAmount(new BigDecimal(jsonInput.getString("totalAmount")));
                order.setShippingAddress(jsonInput.getString("shippingAddress", ""));
                order.setBillingAddress(jsonInput.getString("billingAddress", ""));
                order.setStatus(jsonInput.getString("status", "pending"));
                order.setUpdatedAt(LocalDateTime.now());

                boolean updated = orderDAO.updateOrder(order);

                if (updated) {
                    Order updatedOrder = orderDAO.getOrderById(orderId);
                    out.print(createOrderJson(updatedOrder).build().toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Commande non trouvée")
                            .build();
                    out.print(error.toString());
                }
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la mise à jour de la commande")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "ID de la commande requis")
                        .build();
                out.print(error.toString());
                return;
            }

            int orderId = Integer.parseInt(pathInfo.substring(1));

            // Vérifier si la commande peut être annulée ou supprimée
            String action = request.getParameter("action");
            if ("cancel".equals(action)) {
                boolean cancelled = orderDAO.cancelOrder(orderId);
                if (cancelled) {
                    JsonObject success = Json.createObjectBuilder()
                            .add("message", "Commande annulée avec succès")
                            .build();
                    out.print(success.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "La commande ne peut pas être annulée")
                            .build();
                    out.print(error.toString());
                }
            } else {
                // Suppression complète (admin seulement)
                boolean deleted = orderDAO.deleteOrder(orderId);
                if (deleted) {
                    JsonObject success = Json.createObjectBuilder()
                            .add("message", "Commande supprimée avec succès")
                            .build();
                    out.print(success.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Commande non trouvée")
                            .build();
                    out.print(error.toString());
                }
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la suppression de la commande")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Crée un JSON pour une commande
     */
    private JsonObjectBuilder createOrderJson(Order order) {
        JsonArrayBuilder itemsArray = Json.createArrayBuilder();

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                itemsArray.add(Json.createObjectBuilder()
                        .add("id", item.getId())
                        .add("productId", item.getProduct().getId())
                        .add("productName", item.getProduct().getName())
                        .add("productImage", item.getProduct().getImageUrl() != null ? item.getProduct().getImageUrl() : "")
                        .add("quantity", item.getQuantity())
                        .add("unitPrice", item.getUnitPrice().toString())
                        .add("totalPrice", item.getTotalPrice().toString())
                        .build());
            }
        }

        return Json.createObjectBuilder()
                .add("id", order.getId())
                .add("userId", order.getUserId())
                .add("orderNumber", order.getOrderNumber())
                .add("totalAmount", order.getTotalAmount().toString())
                .add("status", order.getStatus())
                .add("statusDisplay", order.getStatusDisplayName())
                .add("shippingAddress", order.getShippingAddress() != null ? order.getShippingAddress() : "")
                .add("billingAddress", order.getBillingAddress() != null ? order.getBillingAddress() : "")
                .add("items", itemsArray.build())
                .add("createdAt", order.getCreatedAt().toString())
                .add("updatedAt", order.getUpdatedAt().toString())
                .add("canBeCancelled", order.canBeCancelled())
                .add("canBeModified", order.canBeModified());
    }

    /**
     * Parse les éléments de la commande depuis le JSON
     */
    private List<OrderItem> parseOrderItems(JsonObject jsonInput) {
        List<OrderItem> items = new ArrayList<>();

        if (jsonInput.containsKey("items")) {
            var itemsArray = jsonInput.getJsonArray("items");

            for (int i = 0; i < itemsArray.size(); i++) {
                JsonObject itemJson = itemsArray.getJsonObject(i);

                OrderItem item = new OrderItem();

                Product product = new Product();
                product.setId(itemJson.getInt("id"));
                product.setName(itemJson.getString("name"));
                product.setPrice(new BigDecimal(itemJson.getString("price")));
                product.setImageUrl(itemJson.getString("imageUrl", ""));

                item.setProduct(product);
                item.setQuantity(itemJson.getInt("quantity"));
                item.setUnitPrice(new BigDecimal(itemJson.getString("price")));
                item.setTotalPrice(new BigDecimal(itemJson.getString("price"))
                        .multiply(BigDecimal.valueOf(itemJson.getInt("quantity"))));

                items.add(item);
            }
        }

        return items;
    }
}
