package com.ivoka.api.servlets;

import com.ivoka.api.dao.CartDAO;
import com.ivoka.api.dao.ProductDAO;
import com.ivoka.api.models.Cart;
import com.ivoka.api.models.CartItem;
import com.ivoka.api.models.Product;
import com.ivoka.api.models.UserSession;
import com.ivoka.api.utils.SessionUtils;

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
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/cart")
public class CartServlet extends HttpServlet {
    private CartDAO cartDAO;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        cartDAO = new CartDAO();
        productDAO = new ProductDAO();
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
            // Récupérer la session utilisateur
            String sessionToken = request.getHeader("Authorization");
            UserSession userSession = null;
            int userId = 0;
            
            if (sessionToken != null && sessionToken.startsWith("Bearer ")) {
                sessionToken = sessionToken.substring(7);
                userSession = SessionUtils.validateSession(sessionToken);
                if (userSession != null) {
                    userId = userSession.getUserId();
                }
            }
            
            // Récupérer le panier
            Cart cart = null;
            if (userId > 0) {
                cart = cartDAO.getCartByUserId(userId);
            } else {
                // Panier invité basé sur la session
                String guestSessionId = request.getParameter("guestSessionId");
                if (guestSessionId != null && !guestSessionId.isEmpty()) {
                    cart = cartDAO.getCartBySessionId(guestSessionId);
                }
            }
            
            if (cart == null) {
                cart = new Cart();
                cart.setItems(List.of());
            }
            
            // Construire la réponse JSON
            JsonObjectBuilder cartJson = Json.createObjectBuilder()
                    .add("id", cart.getId())
                    .add("userId", cart.getUserId() != null ? cart.getUserId() : 0)
                    .add("sessionId", cart.getSessionId() != null ? cart.getSessionId() : "")
                    .add("totalAmount", cart.getTotalAmount())
                    .add("totalItems", cart.getTotalItems());
            
            JsonArrayBuilder itemsArray = Json.createArrayBuilder();
            if (cart.getItems() != null) {
                for (CartItem item : cart.getItems()) {
                    JsonObjectBuilder itemJson = Json.createObjectBuilder()
                            .add("id", item.getId())
                            .add("productId", item.getProduct().getId())
                            .add("productName", item.getProduct().getName())
                            .add("productPrice", item.getProduct().getPrice().toString())
                            .add("productImage", item.getProduct().getImageUrl() != null ? item.getProduct().getImageUrl() : "")
                            .add("quantity", item.getQuantity())
                            .add("totalPrice", item.getTotalPrice().toString());
                    itemsArray.add(itemJson);
                }
            }
            
            cartJson.add("items", itemsArray);
            
            out.print(cartJson.build().toString());
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la récupération du panier")
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
            String action = jsonInput.getString("action", "");
            
            switch (action) {
                case "add":
                    handleAddToCart(jsonInput, out, response, request);
                    break;
                case "update":
                    handleUpdateQuantity(jsonInput, out, response, request);
                    break;
                case "remove":
                    handleRemoveFromCart(jsonInput, out, response, request);
                    break;
                case "clear":
                    handleClearCart(jsonInput, out, response, request);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Action non reconnue")
                            .build();
                    out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors du traitement du panier")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    private void handleAddToCart(JsonObject jsonInput, PrintWriter out, HttpServletResponse response, 
                                HttpServletRequest request) throws SQLException {
        int productId = jsonInput.getInt("productId", 0);
        int quantity = jsonInput.getInt("quantity", 1);
        String guestSessionId = jsonInput.getString("guestSessionId", "");
        
        if (productId <= 0 || quantity <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "ID du produit et quantité requis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Vérifier que le produit existe
        Product product = productDAO.getProductById(productId);
        if (product == null || !product.isAvailable()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Produit non disponible")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Récupérer ou créer le panier
        Cart cart = null;
        String sessionToken = request.getHeader("Authorization");
        UserSession userSession = null;
        
        if (sessionToken != null && sessionToken.startsWith("Bearer ")) {
            sessionToken = sessionToken.substring(7);
            userSession = SessionUtils.validateSession(sessionToken);
        }
        
        if (userSession != null) {
            // Utilisateur connecté
            cart = cartDAO.getCartByUserId(userSession.getUserId());
            if (cart == null) {
                cart = cartDAO.createCart(userSession.getUserId(), null);
            }
        } else if (!guestSessionId.isEmpty()) {
            // Invité
            cart = cartDAO.getCartBySessionId(guestSessionId);
            if (cart == null) {
                cart = cartDAO.createCart(null, guestSessionId);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Session utilisateur ou ID invité requis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Ajouter l'élément au panier
        boolean added = cartDAO.addItemToCart(cart.getId(), productId, quantity);
        
        if (added) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Produit ajouté au panier")
                    .add("cartId", cart.getId())
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de l'ajout au panier")
                    .build();
            out.print(error.toString());
        }
    }

    private void handleUpdateQuantity(JsonObject jsonInput, PrintWriter out, HttpServletResponse response, 
                                    HttpServletRequest request) throws SQLException {
        int itemId = jsonInput.getInt("itemId", 0);
        int quantity = jsonInput.getInt("quantity", 1);
        
        if (itemId <= 0 || quantity <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "ID de l'élément et quantité requis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        boolean updated = cartDAO.updateItemQuantity(itemId, quantity);
        
        if (updated) {
            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Quantité mise à jour")
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la mise à jour")
                    .build();
            out.print(error.toString());
        }
    }

    private void handleRemoveFromCart(JsonObject jsonInput, PrintWriter out, HttpServletResponse response, 
                                    HttpServletRequest request) throws SQLException {
        int itemId = jsonInput.getInt("itemId", 0);
        
        if (itemId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "ID de l'élément requis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        boolean removed = cartDAO.removeItemFromCart(itemId);
        
        if (removed) {
            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Produit retiré du panier")
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la suppression")
                    .build();
            out.print(error.toString());
        }
    }

    private void handleClearCart(JsonObject jsonInput, PrintWriter out, HttpServletResponse response, 
                               HttpServletRequest request) throws SQLException {
        int cartId = jsonInput.getInt("cartId", 0);
        
        if (cartId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "ID du panier requis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        boolean cleared = cartDAO.clearCart(cartId);
        
        if (cleared) {
            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Panier vidé")
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors du vidage du panier")
                    .build();
            out.print(error.toString());
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
}