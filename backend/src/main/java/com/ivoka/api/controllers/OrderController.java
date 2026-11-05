package com.ivoka.api.controllers;

import com.ivoka.api.dao.OrderDAO;
import com.ivoka.api.models.Order;
import com.ivoka.api.models.OrderItem;
import com.ivoka.api.models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderDAO orderDAO = new OrderDAO();

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(required = false) String status,
                                       @RequestParam(required = false) String userId) {
        try {
            List<Order> orders;
            if (userId != null && !userId.isEmpty())
                orders = orderDAO.getOrdersByUserId(Integer.parseInt(userId));
            else if (status != null && !status.isEmpty())
                orders = orderDAO.getOrdersByStatus(status);
            else
                orders = orderDAO.getAllOrders();
            return ResponseEntity.ok(orders.stream().map(this::toMap).toList());
        } catch (Exception e) {
            logger.error("Erreur getOrders: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des commandes");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable int id) {
        try {
            Order order = orderDAO.getOrderById(id);
            if (order == null) return error(HttpStatus.NOT_FOUND, "Commande non trouvée");
            return ResponseEntity.ok(toMap(order));
        } catch (Exception e) {
            logger.error("Erreur getOrder: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération de la commande");
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
        try {
            Order order = new Order();
            if (body.containsKey("userId"))
                order.setUserId(intVal(body, "userId"));
            else
                order.setUserId(0);
            order.setTotalAmount(new BigDecimal(body.getOrDefault("totalAmount", "0").toString()));
            order.setShippingAddress(body.getOrDefault("shippingAddress", "").toString());
            order.setBillingAddress(body.getOrDefault("billingAddress", "").toString());
            order.setStatus("pending");
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.setOrderNumber(orderDAO.generateOrderNumber());
            order.setItems(parseItems(body));

            Order created = orderDAO.createOrder(order);
            if (created == null || created.getId() <= 0)
                return error(HttpStatus.BAD_REQUEST, "Erreur lors de la création de la commande");
            return ResponseEntity.status(HttpStatus.CREATED).body(toMap(created));
        } catch (Exception e) {
            logger.error("Erreur createOrder: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de la commande");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            if (body.containsKey("status")) {
                String newStatus = body.get("status").toString();
                if (!orderDAO.updateOrderStatus(id, newStatus))
                    return error(HttpStatus.NOT_FOUND, "Commande non trouvée");
            } else {
                Order order = new Order();
                order.setId(id);
                order.setTotalAmount(new BigDecimal(body.getOrDefault("totalAmount", "0").toString()));
                order.setShippingAddress(body.getOrDefault("shippingAddress", "").toString());
                order.setBillingAddress(body.getOrDefault("billingAddress", "").toString());
                order.setStatus(body.getOrDefault("status", "pending").toString());
                order.setUpdatedAt(LocalDateTime.now());
                if (!orderDAO.updateOrder(order))
                    return error(HttpStatus.NOT_FOUND, "Commande non trouvée");
            }
            Order updated = orderDAO.getOrderById(id);
            return ResponseEntity.ok(toMap(updated));
        } catch (Exception e) {
            logger.error("Erreur updateOrder: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour de la commande");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id,
                                         @RequestParam(required = false) String action) {
        try {
            if ("cancel".equals(action)) {
                if (!orderDAO.cancelOrder(id))
                    return error(HttpStatus.BAD_REQUEST, "La commande ne peut pas être annulée");
                return ResponseEntity.ok(Map.of("message", "Commande annulée avec succès"));
            }
            if (!orderDAO.deleteOrder(id))
                return error(HttpStatus.NOT_FOUND, "Commande non trouvée");
            return ResponseEntity.ok(Map.of("message", "Commande supprimée avec succès"));
        } catch (Exception e) {
            logger.error("Erreur deleteOrder: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression de la commande");
        }
    }

    private Map<String, Object> toMap(Order order) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", order.getId());
        m.put("userId", order.getUserId());
        m.put("orderNumber", order.getOrderNumber());
        m.put("totalAmount", order.getTotalAmount().toString());
        m.put("status", order.getStatus());
        m.put("statusDisplay", order.getStatusDisplayName());
        m.put("shippingAddress", order.getShippingAddress() != null ? order.getShippingAddress() : "");
        m.put("billingAddress", order.getBillingAddress() != null ? order.getBillingAddress() : "");
        m.put("createdAt", order.getCreatedAt().toString());
        m.put("updatedAt", order.getUpdatedAt().toString());
        m.put("canBeCancelled", order.canBeCancelled());
        m.put("canBeModified", order.canBeModified());
        List<Map<String, Object>> items = order.getItems() == null ? List.of() :
                order.getItems().stream().map(this::itemToMap).toList();
        m.put("items", items);
        return m;
    }

    private Map<String, Object> itemToMap(OrderItem item) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", item.getId());
        m.put("productId", item.getProduct().getId());
        m.put("productName", item.getProduct().getName());
        m.put("productImage", item.getProduct().getImageUrl() != null ? item.getProduct().getImageUrl() : "");
        m.put("quantity", item.getQuantity());
        m.put("unitPrice", item.getUnitPrice().toString());
        m.put("totalPrice", item.getTotalPrice().toString());
        return m;
    }

    @SuppressWarnings("unchecked")
    private List<OrderItem> parseItems(Map<String, Object> body) {
        List<OrderItem> items = new ArrayList<>();
        if (!body.containsKey("items")) return items;
        List<Map<String, Object>> arr = (List<Map<String, Object>>) body.get("items");
        for (Map<String, Object> i : arr) {
            OrderItem item = new OrderItem();
            Product p = new Product();
            p.setId(intVal(i, "id"));
            p.setName(i.getOrDefault("name", "").toString());
            p.setPrice(new BigDecimal(i.getOrDefault("price", "0").toString()));
            p.setImageUrl(i.getOrDefault("imageUrl", "").toString());
            item.setProduct(p);
            item.setQuantity(intVal(i, "quantity"));
            BigDecimal unitPrice = new BigDecimal(i.getOrDefault("price", "0").toString());
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(intVal(i, "quantity"))));
            items.add(item);
        }
        return items;
    }

    private int intVal(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v instanceof Integer i) return i;
        if (v instanceof Number n) return n.intValue();
        return 0;
    }
    private ResponseEntity<?> error(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("error", msg));
    }
}
