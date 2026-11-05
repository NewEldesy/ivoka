package com.ivoka.api.controllers;

import com.ivoka.api.dao.ProductDAO;
import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.Product;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final ProductDAO productDAO = new ProductDAO();
    private final UserAuthDAO userAuthDAO = new UserAuthDAO();
    private final ProductsController productsController = new ProductsController();

    // ---- Vérification admin ----
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer "))
            return authHeader.substring(7);
        return authHeader;
    }

    private boolean isAdmin(String authHeader) {
        return SessionUtils.isAdmin(extractToken(authHeader));
    }

    // ---- Dashboard ----
    @GetMapping
    public ResponseEntity<?> dashboard(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (!isAdmin(auth)) return forbidden();
        try {
            List<Product> products = productDAO.getAllProducts();
            List<UserAuth> users   = userAuthDAO.getAllUsers();

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("products", Map.of(
                "total",       products.size(),
                "available",   products.stream().filter(Product::isAvailable).count(),
                "unavailable", products.stream().filter(p -> !p.isAvailable()).count()
            ));
            resp.put("users", Map.of(
                "total",     users.size(),
                "admins",    users.stream().filter(u -> "admin".equals(u.getRole())).count(),
                "customers", users.stream().filter(u -> "customer".equals(u.getRole())).count()
            ));
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.error("Erreur dashboard: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des statistiques");
        }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (!isAdmin(auth)) return forbidden();
        try {
            List<Product> products = productDAO.getAllProducts();
            return ResponseEntity.ok(Map.of("products", products.stream().map(productsController::toMap).toList()));
        } catch (Exception e) {
            logger.error("Erreur admin getProducts: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (!isAdmin(auth)) return forbidden();
        try {
            List<UserAuth> users = userAuthDAO.getAllUsers();
            return ResponseEntity.ok(Map.of("users", users.stream().map(this::userMap).toList()));
        } catch (Exception e) {
            logger.error("Erreur admin getUsers: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur");
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@RequestHeader(value = "Authorization", required = false) String auth,
                                           @PathVariable int id,
                                           @RequestBody Map<String, Object> body) {
        if (!isAdmin(auth)) return forbidden();
        try {
            Product p = productDAO.getProductById(id);
            if (p == null) return error(HttpStatus.NOT_FOUND, "Produit non trouvé");

            if (body.containsKey("name"))        p.setName(body.get("name").toString());
            if (body.containsKey("description")) p.setDescription(body.get("description").toString());
            if (body.containsKey("price"))       p.setPrice(new BigDecimal(body.get("price").toString()));
            if (body.containsKey("category"))    p.setCategory(body.get("category").toString());
            if (body.containsKey("imageUrl"))    p.setImageUrl(body.get("imageUrl").toString());
            if (body.containsKey("available"))   p.setAvailable((Boolean) body.get("available"));

            if (!productDAO.updateProduct(p))
                return error(HttpStatus.BAD_REQUEST, "Erreur lors de la mise à jour");
            return ResponseEntity.ok(Map.of("success", true, "message", "Produit mis à jour", "product", productsController.toMap(p)));
        } catch (Exception e) {
            logger.error("Erreur admin updateProduct: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour");
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@RequestHeader(value = "Authorization", required = false) String auth,
                                           @PathVariable int id) {
        if (!isAdmin(auth)) return forbidden();
        try {
            if (!productDAO.deleteProduct(id))
                return error(HttpStatus.BAD_REQUEST, "Erreur lors de la suppression");
            return ResponseEntity.ok(Map.of("success", true, "message", "Produit supprimé"));
        } catch (Exception e) {
            logger.error("Erreur admin deleteProduct: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression");
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader(value = "Authorization", required = false) String auth,
                                        @PathVariable int id) {
        if (!isAdmin(auth)) return forbidden();
        try {
            if (!userAuthDAO.deleteUser(id))
                return error(HttpStatus.BAD_REQUEST, "Erreur lors de la suppression");
            return ResponseEntity.ok(Map.of("success", true, "message", "Utilisateur supprimé"));
        } catch (Exception e) {
            logger.error("Erreur admin deleteUser: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression");
        }
    }

    private Map<String, Object> userMap(UserAuth u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("firstName", u.getFirstName());
        m.put("lastName", u.getLastName());
        m.put("email", u.getEmail());
        m.put("phone", u.getPhone() != null ? u.getPhone() : "");
        m.put("role", u.getRole());
        m.put("newsletter", u.isNewsletter());
        m.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
        return m;
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Accès refusé - Administrateur requis"));
    }
    private ResponseEntity<?> error(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("error", msg));
    }
}
