package com.ivoka.api.servlets;

import com.ivoka.api.dao.ProductDAO;
import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.Product;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.utils.SessionUtils;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/admin")
public class AdminServlet extends HttpServlet {
    private ProductDAO productDAO;
    private UserAuthDAO userAuthDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAO();
        userAuthDAO = new UserAuthDAO();
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
            // Vérifier que l'utilisateur est admin
            String sessionToken = request.getHeader("Authorization");
            if (sessionToken != null && sessionToken.startsWith("Bearer ")) {
                sessionToken = sessionToken.substring(7);
            }
            
            if (!SessionUtils.isAdmin(sessionToken)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Accès refusé - Administrateur requis")
                        .build();
                out.print(error.toString());
                return;
            }
            
            String pathInfo = request.getPathInfo();
            
            if ("/products".equals(pathInfo)) {
                handleGetProducts(out, response);
            } else if ("/users".equals(pathInfo)) {
                handleGetUsers(out, response);
            } else if ("/orders".equals(pathInfo)) {
                handleGetOrders(out, response);
            } else {
                // Dashboard stats
                handleGetDashboardStats(out, response);
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la récupération des données admin")
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
            // Vérifier que l'utilisateur est admin
            String sessionToken = request.getHeader("Authorization");
            if (sessionToken != null && sessionToken.startsWith("Bearer ")) {
                sessionToken = sessionToken.substring(7);
            }
            
            if (!SessionUtils.isAdmin(sessionToken)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Accès refusé - Administrateur requis")
                        .build();
                out.print(error.toString());
                return;
            }
            
            String pathInfo = request.getPathInfo();
            
            if ("/products".equals(pathInfo)) {
                handleCreateProduct(request, out, response);
            } else if ("/users".equals(pathInfo)) {
                handleCreateUser(request, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Endpoint non reconnu")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors du traitement de la requête admin")
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
            // Vérifier que l'utilisateur est admin
            String sessionToken = request.getHeader("Authorization");
            if (sessionToken != null && sessionToken.startsWith("Bearer ")) {
                sessionToken = sessionToken.substring(7);
            }
            
            if (!SessionUtils.isAdmin(sessionToken)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Accès refusé - Administrateur requis")
                        .build();
                out.print(error.toString());
                return;
            }
            
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null && pathInfo.startsWith("/products/")) {
                int productId = Integer.parseInt(pathInfo.substring("/products/".length()));
                handleUpdateProduct(productId, request, out, response);
            } else if (pathInfo != null && pathInfo.startsWith("/users/")) {
                int userId = Integer.parseInt(pathInfo.substring("/users/".length()));
                handleUpdateUser(userId, request, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Endpoint non reconnu")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la mise à jour")
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
            // Vérifier que l'utilisateur est admin
            String sessionToken = request.getHeader("Authorization");
            if (sessionToken != null && sessionToken.startsWith("Bearer ")) {
                sessionToken = sessionToken.substring(7);
            }
            
            if (!SessionUtils.isAdmin(sessionToken)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Accès refusé - Administrateur requis")
                        .build();
                out.print(error.toString());
                return;
            }
            
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null && pathInfo.startsWith("/products/")) {
                int productId = Integer.parseInt(pathInfo.substring("/products/".length()));
                handleDeleteProduct(productId, out, response);
            } else if (pathInfo != null && pathInfo.startsWith("/users/")) {
                int userId = Integer.parseInt(pathInfo.substring("/users/".length()));
                handleDeleteUser(userId, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Endpoint non reconnu")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la suppression")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    private void handleGetDashboardStats(PrintWriter out, HttpServletResponse response) throws SQLException {
        JsonObjectBuilder stats = Json.createObjectBuilder();
        
        // Statistiques produits
        List<Product> allProducts = productDAO.getAllProducts();
        int totalProducts = allProducts.size();
        int availableProducts = (int) allProducts.stream().filter(Product::isAvailable).count();
        
        // Statistiques utilisateurs
        List<UserAuth> allUsers = userAuthDAO.getAllUsers();
        int totalUsers = allUsers.size();
        int adminUsers = (int) allUsers.stream().filter(u -> "admin".equals(u.getRole())).count();
        int customerUsers = (int) allUsers.stream().filter(u -> "customer".equals(u.getRole())).count();
        
        // Construire la réponse
        stats.add("products", Json.createObjectBuilder()
                .add("total", totalProducts)
                .add("available", availableProducts)
                .add("unavailable", totalProducts - availableProducts));
        
        stats.add("users", Json.createObjectBuilder()
                .add("total", totalUsers)
                .add("admins", adminUsers)
                .add("customers", customerUsers));
        
        response.setStatus(HttpServletResponse.SC_OK);
        out.print(stats.build().toString());
    }

    private void handleGetProducts(PrintWriter out, HttpServletResponse response) throws SQLException {
        List<Product> products = productDAO.getAllProducts();
        
        JsonArrayBuilder productsArray = Json.createArrayBuilder();
        for (Product product : products) {
            productsArray.add(createProductJson(product));
        }
        
        response.setStatus(HttpServletResponse.SC_OK);
        out.print(Json.createObjectBuilder()
                .add("products", productsArray)
                .build().toString());
    }

    private void handleGetUsers(PrintWriter out, HttpServletResponse response) throws SQLException {
        List<UserAuth> users = userAuthDAO.getAllUsers();
        
        JsonArrayBuilder usersArray = Json.createArrayBuilder();
        for (UserAuth user : users) {
            JsonObjectBuilder userJson = Json.createObjectBuilder()
                    .add("id", user.getId())
                    .add("firstName", user.getFirstName())
                    .add("lastName", user.getLastName())
                    .add("email", user.getEmail())
                    .add("phone", user.getPhone() != null ? user.getPhone() : "")
                    .add("role", user.getRole())
                    .add("newsletter", user.isNewsletter())
                    .add("createdAt", user.getCreatedAt().toString());
            usersArray.add(userJson);
        }
        
        response.setStatus(HttpServletResponse.SC_OK);
        out.print(Json.createObjectBuilder()
                .add("users", usersArray)
                .build().toString());
    }

    private void handleGetOrders(PrintWriter out, HttpServletResponse response) {
        // TODO: Implémenter la récupération des commandes
        JsonObject success = Json.createObjectBuilder()
                .add("orders", Json.createArrayBuilder())
                .build();
        out.print(success.toString());
    }

    private void handleCreateProduct(HttpServletRequest request, PrintWriter out, HttpServletResponse response) 
            throws IOException, SQLException {
        JsonObject jsonInput = Json.createReader(request.getReader()).readObject();
        
        String name = jsonInput.getString("name", "");
        String description = jsonInput.getString("description", "");
        BigDecimal price = new BigDecimal(jsonInput.getString("price", "0"));
        String category = jsonInput.getString("category", "");
        String imageUrl = jsonInput.getString("imageUrl", "");
        int stockQuantity = jsonInput.getInt("stockQuantity", 0);
        boolean available = jsonInput.getBoolean("available", true);
        
        if (name.isEmpty() || price.compareTo(BigDecimal.ZERO) <= 0 || category.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Nom, prix et catégorie requis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setAvailable(available);
        
        boolean created = productDAO.createProduct(product);
        
        if (created) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Produit créé avec succès")
                    .add("product", createProductJson(product))
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la création du produit")
                    .build();
            out.print(error.toString());
        }
    }

    private void handleUpdateProduct(int productId, HttpServletRequest request, PrintWriter out, HttpServletResponse response) 
            throws IOException, SQLException {
        JsonObject jsonInput = Json.createReader(request.getReader()).readObject();
        
        Product existingProduct = productDAO.getProductById(productId);
        if (existingProduct == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Produit non trouvé")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Mettre à jour les champs
        if (jsonInput.containsKey("name")) {
            existingProduct.setName(jsonInput.getString("name"));
        }
        if (jsonInput.containsKey("description")) {
            existingProduct.setDescription(jsonInput.getString("description"));
        }
        if (jsonInput.containsKey("price")) {
            existingProduct.setPrice(new BigDecimal(jsonInput.getString("price")));
        }
        if (jsonInput.containsKey("category")) {
            existingProduct.setCategory(jsonInput.getString("category"));
        }
        if (jsonInput.containsKey("imageUrl")) {
            existingProduct.setImageUrl(jsonInput.getString("imageUrl"));
        }
        if (jsonInput.containsKey("available")) {
            existingProduct.setAvailable(jsonInput.getBoolean("available"));
        }
        
        boolean updated = productDAO.updateProduct(existingProduct);
        
        if (updated) {
            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Produit mis à jour avec succès")
                    .add("product", createProductJson(existingProduct))
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la mise à jour du produit")
                    .build();
            out.print(error.toString());
        }
    }

    private void handleDeleteProduct(int productId, PrintWriter out, HttpServletResponse response) throws SQLException {
        boolean deleted = productDAO.deleteProduct(productId);
        
        if (deleted) {
            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Produit supprimé avec succès")
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la suppression du produit")
                    .build();
            out.print(error.toString());
        }
    }

    private void handleCreateUser(HttpServletRequest request, PrintWriter out, HttpServletResponse response) 
            throws IOException, SQLException {
        // TODO: Implémenter la création d'utilisateurs par admin
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        JsonObject error = Json.createObjectBuilder()
                .add("error", "Fonctionnalité non implémentée")
                .build();
        out.print(error.toString());
    }

    private void handleUpdateUser(int userId, HttpServletRequest request, PrintWriter out, HttpServletResponse response) 
            throws IOException, SQLException {
        // TODO: Implémenter la mise à jour d'utilisateurs par admin
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        JsonObject error = Json.createObjectBuilder()
                .add("error", "Fonctionnalité non implémentée")
                .build();
        out.print(error.toString());
    }

    private void handleDeleteUser(int userId, PrintWriter out, HttpServletResponse response) throws SQLException {
        boolean deleted = userAuthDAO.deleteUser(userId);
        
        if (deleted) {
            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Utilisateur supprimé avec succès")
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la suppression de l'utilisateur")
                    .build();
            out.print(error.toString());
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

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}