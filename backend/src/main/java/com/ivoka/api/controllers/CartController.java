package com.ivoka.api.controllers;

import com.ivoka.api.dao.CartDAO;
import com.ivoka.api.dao.ProductDAO;
import com.ivoka.api.models.Cart;
import com.ivoka.api.models.CartItem;
import com.ivoka.api.models.Product;
import com.ivoka.api.models.UserSession;
import com.ivoka.api.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(value = "Authorization", required = false) String auth,
                                     @RequestParam(required = false) String guestSessionId) {
        try {
            Cart cart = resolveCart(auth, guestSessionId, false);
            if (cart == null) {
                cart = new Cart();
                cart.setItems(List.of());
            }
            return ResponseEntity.ok(cartToMap(cart));
        } catch (Exception e) {
            logger.error("Erreur getCart: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération du panier");
        }
    }

    @PostMapping
    public ResponseEntity<?> handlePost(@RequestHeader(value = "Authorization", required = false) String auth,
                                        @RequestBody Map<String, Object> body) {
        String action = body.getOrDefault("action", "").toString();
        try {
            return switch (action) {
                case "add"    -> handleAdd(auth, body);
                case "update" -> handleUpdate(body);
                case "remove" -> handleRemove(body);
                case "clear"  -> handleClear(body);
                default -> error(HttpStatus.BAD_REQUEST, "Action non reconnue");
            };
        } catch (Exception e) {
            logger.error("Erreur CartController: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du traitement du panier");
        }
    }

    private ResponseEntity<?> handleAdd(String auth, Map<String, Object> body) throws Exception {
        int productId = intVal(body, "productId");
        int quantity  = intVal(body, "quantity");
        String guestId = body.getOrDefault("guestSessionId", "").toString();

        if (productId <= 0 || quantity <= 0)
            return error(HttpStatus.BAD_REQUEST, "ID du produit et quantité requis");

        Product product = productDAO.getProductById(productId);
        if (product == null || !product.isAvailable())
            return error(HttpStatus.BAD_REQUEST, "Produit non disponible");

        Cart cart = resolveCart(auth, guestId, true);
        if (cart == null)
            return error(HttpStatus.BAD_REQUEST, "Session utilisateur ou ID invité requis");

        if (!cartDAO.addItemToCart(cart.getId(), productId, quantity))
            return error(HttpStatus.BAD_REQUEST, "Erreur lors de l'ajout au panier");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Produit ajouté au panier", "cartId", cart.getId()));
    }

    private ResponseEntity<?> handleUpdate(Map<String, Object> body) throws Exception {
        int itemId   = intVal(body, "itemId");
        int quantity = intVal(body, "quantity");
        if (itemId <= 0 || quantity <= 0)
            return error(HttpStatus.BAD_REQUEST, "ID de l'élément et quantité requis");
        if (!cartDAO.updateItemQuantity(itemId, quantity))
            return error(HttpStatus.BAD_REQUEST, "Erreur lors de la mise à jour");
        return ResponseEntity.ok(Map.of("success", true, "message", "Quantité mise à jour"));
    }

    private ResponseEntity<?> handleRemove(Map<String, Object> body) throws Exception {
        int itemId = intVal(body, "itemId");
        if (itemId <= 0)
            return error(HttpStatus.BAD_REQUEST, "ID de l'élément requis");
        if (!cartDAO.removeItemFromCart(itemId))
            return error(HttpStatus.BAD_REQUEST, "Erreur lors de la suppression");
        return ResponseEntity.ok(Map.of("success", true, "message", "Produit retiré du panier"));
    }

    private ResponseEntity<?> handleClear(Map<String, Object> body) throws Exception {
        int cartId = intVal(body, "cartId");
        if (cartId <= 0)
            return error(HttpStatus.BAD_REQUEST, "ID du panier requis");
        if (!cartDAO.clearCart(cartId))
            return error(HttpStatus.BAD_REQUEST, "Erreur lors du vidage du panier");
        return ResponseEntity.ok(Map.of("success", true, "message", "Panier vidé"));
    }

    private Cart resolveCart(String authHeader, String guestId, boolean createIfMissing) throws Exception {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            UserSession session = SessionUtils.validateSession(token);
            if (session != null) {
                Cart cart = cartDAO.getCartByUserId(session.getUserId());
                if (cart == null && createIfMissing)
                    cart = cartDAO.createCart(session.getUserId(), null);
                return cart;
            }
        }
        if (guestId != null && !guestId.isEmpty()) {
            Cart cart = cartDAO.getCartBySessionId(guestId);
            if (cart == null && createIfMissing)
                cart = cartDAO.createCart(null, guestId);
            return cart;
        }
        return null;
    }

    private Map<String, Object> cartToMap(Cart cart) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", cart.getId());
        m.put("userId", cart.getUserId() != null ? cart.getUserId() : 0);
        m.put("sessionId", cart.getSessionId() != null ? cart.getSessionId() : "");
        m.put("totalAmount", cart.getTotalAmount());
        m.put("totalItems", cart.getTotalItems());
        List<Map<String, Object>> items = cart.getItems() == null ? List.of() :
                cart.getItems().stream().map(this::itemToMap).toList();
        m.put("items", items);
        return m;
    }

    private Map<String, Object> itemToMap(CartItem item) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", item.getId());
        m.put("productId", item.getProduct().getId());
        m.put("productName", item.getProduct().getName());
        m.put("productPrice", item.getProduct().getPrice().toString());
        m.put("productImage", item.getProduct().getImageUrl() != null ? item.getProduct().getImageUrl() : "");
        m.put("quantity", item.getQuantity());
        m.put("totalPrice", item.getTotalPrice().toString());
        return m;
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
