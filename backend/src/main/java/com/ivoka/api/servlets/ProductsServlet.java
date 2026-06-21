package com.ivoka.api.servlets;

import com.ivoka.api.dao.ProductDAO;
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
import java.util.List;

@WebServlet("/api/products/*")
public class ProductsServlet extends HttpServlet {
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        super.init();
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
            String category = request.getParameter("category");
            
            if (category != null && !category.isEmpty()) {
                // Récupérer les produits par catégorie
                List<Product> products = productDAO.getProductsByCategory(category);
                JsonArrayBuilder jsonArray = Json.createArrayBuilder();
                
                for (Product product : products) {
                    jsonArray.add(createProductJson(product));
                }
                
                out.print(jsonArray.build().toString());
            } else {
                // Récupérer tous les produits
                List<Product> products = productDAO.getAllProducts();
                JsonArrayBuilder jsonArray = Json.createArrayBuilder();
                
                for (Product product : products) {
                    jsonArray.add(createProductJson(product));
                }
                
                out.print(jsonArray.build().toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la récupération des produits")
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
            
            Product product = new Product();
            product.setName(jsonInput.getString("name"));
            product.setDescription(jsonInput.getString("description", ""));
            product.setPrice(new BigDecimal(jsonInput.getString("price")));
            product.setCategory(jsonInput.getString("category"));
            product.setImageUrl(jsonInput.getString("imageUrl", ""));
            product.setAvailable(jsonInput.getBoolean("available", true));
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            
            boolean created = productDAO.createProduct(product);
            
            if (created) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(createProductJson(product).build().toString());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Erreur lors de la création du produit")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la création du produit")
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
                        .add("error", "ID du produit requis")
                        .build();
                out.print(error.toString());
                return;
            }
            
            int productId = Integer.parseInt(pathInfo.substring(1));
            
            JsonObject jsonInput = Json.createReader(request.getReader()).readObject();
            
            Product product = new Product();
            product.setId(productId);
            product.setName(jsonInput.getString("name"));
            product.setDescription(jsonInput.getString("description", ""));
            product.setPrice(new BigDecimal(jsonInput.getString("price")));
            product.setCategory(jsonInput.getString("category"));
            product.setImageUrl(jsonInput.getString("imageUrl", ""));
            product.setAvailable(jsonInput.getBoolean("available", true));
            product.setUpdatedAt(LocalDateTime.now());
            
            boolean updated = productDAO.updateProduct(product);
            
            if (updated) {
                out.print(createProductJson(product).build().toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Produit non trouvé")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la mise à jour du produit")
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
                        .add("error", "ID du produit requis")
                        .build();
                out.print(error.toString());
                return;
            }
            
            int productId = Integer.parseInt(pathInfo.substring(1));
            
            boolean deleted = productDAO.deleteProduct(productId);
            
            if (deleted) {
                JsonObject success = Json.createObjectBuilder()
                        .add("message", "Produit supprimé avec succès")
                        .build();
                out.print(success.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Produit non trouvé")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la suppression du produit")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    private JsonObjectBuilder createProductJson(Product product) {
        return Json.createObjectBuilder()
                .add("id", product.getId())
                .add("name", product.getName())
                .add("description", product.getDescription())
                .add("price", product.getPrice().toString())
                .add("category", product.getCategory())
                .add("imageUrl", product.getImageUrl() != null ? product.getImageUrl() : "")
                .add("available", product.isAvailable())
                .add("createdAt", product.getCreatedAt().toString())
                .add("updatedAt", product.getUpdatedAt().toString());
    }
}