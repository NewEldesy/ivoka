package com.ivoka.api.controllers;

import com.ivoka.api.dao.ProductDAO;
import com.ivoka.api.models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);
    private final ProductDAO productDAO = new ProductDAO();

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(required = false) String category) {
        try {
            List<Product> products = (category != null && !category.isEmpty())
                    ? productDAO.getProductsByCategory(category)
                    : productDAO.getAllProducts();
            return ResponseEntity.ok(products.stream().map(this::toMap).toList());
        } catch (Exception e) {
            logger.error("Erreur getProducts: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des produits");
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> body) {
        try {
            Product p = new Product();
            p.setName(str(body, "name"));
            p.setDescription(str(body, "description"));
            p.setPrice(new BigDecimal(str(body, "price")));
            p.setCategory(str(body, "category"));
            p.setImageUrl(str(body, "imageUrl"));
            p.setAvailable(boolDef(body, "available", true));
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());

            if (!productDAO.createProduct(p))
                return error(HttpStatus.BAD_REQUEST, "Erreur lors de la création du produit");
            return ResponseEntity.status(HttpStatus.CREATED).body(toMap(p));
        } catch (Exception e) {
            logger.error("Erreur createProduct: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du produit");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            Product p = new Product();
            p.setId(id);
            p.setName(str(body, "name"));
            p.setDescription(str(body, "description"));
            p.setPrice(new BigDecimal(str(body, "price")));
            p.setCategory(str(body, "category"));
            p.setImageUrl(str(body, "imageUrl"));
            p.setAvailable(boolDef(body, "available", true));
            p.setUpdatedAt(LocalDateTime.now());

            if (!productDAO.updateProduct(p))
                return error(HttpStatus.NOT_FOUND, "Produit non trouvé");
            return ResponseEntity.ok(toMap(p));
        } catch (Exception e) {
            logger.error("Erreur updateProduct: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du produit");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        try {
            if (!productDAO.deleteProduct(id))
                return error(HttpStatus.NOT_FOUND, "Produit non trouvé");
            return ResponseEntity.ok(Map.of("message", "Produit supprimé avec succès"));
        } catch (Exception e) {
            logger.error("Erreur deleteProduct: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression du produit");
        }
    }

    Map<String, Object> toMap(Product p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("name", p.getName());
        m.put("description", p.getDescription());
        m.put("price", p.getPrice().toString());
        m.put("category", p.getCategory());
        m.put("imageUrl", p.getImageUrl() != null ? p.getImageUrl() : "");
        m.put("available", p.isAvailable());
        m.put("createdAt", p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
        m.put("updatedAt", p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : "");
        return m;
    }

    private String str(Map<String, Object> m, String k)  { return m.getOrDefault(k, "").toString(); }
    private boolean boolDef(Map<String, Object> m, String k, boolean def) {
        Object v = m.get(k); return v instanceof Boolean b ? b : def;
    }
    private ResponseEntity<?> error(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("error", msg));
    }
}
