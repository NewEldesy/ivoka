-- Base de données IVOKA - Version Complète avec E-commerce
-- Script de création des tables avec authentification, paniers et commandes

CREATE DATABASE IF NOT EXISTS ivoka_db;
USE ivoka_db;

-- Table des utilisateurs avec authentification
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('customer', 'admin') DEFAULT 'customer',
    newsletter BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des produits
CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    image_url VARCHAR(500),
    stock_quantity INT DEFAULT 0,
    available BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_available (available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des paniers
CREATE TABLE IF NOT EXISTS carts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    session_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des éléments de panier
CREATE TABLE IF NOT EXISTS cart_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_cart_id (cart_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des commandes
CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('pending', 'confirmed', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
    shipping_address TEXT,
    billing_address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des éléments de commande
CREATE TABLE IF NOT EXISTS order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des messages de contact
CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_email (email),
    INDEX idx_read (read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des sessions utilisateur
CREATE TABLE IF NOT EXISTS user_sessions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_session_token (session_token),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion de données de test - Utilisateurs
INSERT INTO users (first_name, last_name, email, password_hash, role, newsletter) VALUES
('Admin', 'IVOKA', 'admin@ivoka.fr', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin', true),
('Jean', 'Dupont', 'jean.dupont@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'customer', true),
('Marie', 'Martin', 'marie.martin@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'customer', false);

-- Insertion de données de test - Produits
INSERT INTO products (name, description, price, category, image_url, stock_quantity) VALUES
('Thé Vert Bio', 'Thé vert biologique cultivé localement, riche en antioxydants. Parfait pour vos moments de détente.', 
 12.90, 'tea', '/resources/tea-leaves-product.png', 50),
('Thé Noir Premium', 'Thé noir de qualité supérieure avec des notes épicées et maltées. Idéal pour le petit-déjeuner.', 
 15.50, 'tea', '/resources/tea-leaves-product.png', 30),
('Thé Blanc Délicat', 'Thé blanc aux arômes délicats et floraux. Une expérience gustative raffinée.', 
 18.90, 'tea', '/resources/tea-leaves-product.png', 25),
('Huile d\'Avocat Pressée à Froid', 'Huile d\'avocat pure pressée à froid. Parfaite pour la cuisine et les soins cosmétiques.', 
 24.90, 'avocado_oil', '/resources/avocado-oil-bottle.png', 40),
('Huile d\'Avocat Cosmétique', 'Huile d\'avocat spécialement conçue pour les soins de la peau et des cheveux. Riche en vitamines E et K.', 
 29.90, 'avocado_oil', '/resources/avocado-oil-bottle.png', 35);

-- Insertion de données de test - Commandes
INSERT INTO orders (user_id, order_number, total_amount, status) VALUES
(2, 'IVK2024001', 38.80, 'delivered'),
(3, 'IVK2024002', 24.90, 'confirmed');

-- Insertion de données de test - Éléments de commande
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES
(1, 1, 2, 12.90, 25.80),
(1, 4, 1, 24.90, 24.90),
(2, 4, 1, 24.90, 24.90);

-- Création d'un utilisateur pour l'application
CREATE USER IF NOT EXISTS 'ivoka_user'@'localhost' IDENTIFIED BY 'ivoka_password';
GRANT ALL PRIVILEGES ON ivoka_db.* TO 'ivoka_user'@'localhost';
FLUSH PRIVILEGES;

-- Vues pour faciliter les requêtes
CREATE VIEW product_details AS
SELECT 
    p.id, p.name, p.description, p.price, p.category, 
    p.image_url, p.stock_quantity, p.available,
    p.created_at, p.updated_at
FROM products p;

CREATE VIEW user_orders AS
SELECT 
    o.id, o.user_id, o.order_number, o.total_amount, o.status,
    o.shipping_address, o.billing_address, o.created_at,
    u.first_name, u.last_name, u.email
FROM orders o
JOIN users u ON o.user_id = u.id;

CREATE VIEW cart_details AS
SELECT 
    c.id as cart_id, c.user_id, c.session_id,
    ci.id as item_id, ci.product_id, ci.quantity, ci.added_at,
    p.name as product_name, p.price, p.image_url
FROM carts c
LEFT JOIN cart_items ci ON c.id = ci.cart_id
LEFT JOIN products p ON ci.product_id = p.id;