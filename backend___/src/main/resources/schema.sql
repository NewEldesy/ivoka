-- Updated schema for IVOKA (removed user creation and added session table)
-- Base de données IVOKA
CREATE DATABASE IF NOT EXISTS ivoka_db;
USE ivoka_db;

-- Table des produits
CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    image_url VARCHAR(500),
    available BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_available (available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des messages de contact
CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    read_flag BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_read (read_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des utilisateurs (mise à jour: password_hash, role, updated_at)
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL DEFAULT 'customer',
    newsletter BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_newsletter (newsletter)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des sessions utilisateurs
CREATE TABLE IF NOT EXISTS user_sessions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_session_token (session_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion de données de test (produits seulement)
INSERT IGNORE INTO products (name, description, price, category, image_url) VALUES
('Thé Vert Bio', 'Thé vert biologique cultivé localement, riche en antioxydants. Parfait pour vos moments de détente.', 
 12.90, 'tea', '/resources/tea-leaves-product.png'),
('Thé Noir Premium', 'Thé noir de qualité supérieure avec des notes épicées et maltées. Idéal pour le petit-déjeuner.', 
 15.50, 'tea', '/resources/tea-leaves-product.png'),
('Thé Blanc Délicat', 'Thé blanc aux arômes délicats et floraux. Une expérience gustative raffinée.', 
 18.90, 'tea', '/resources/tea-leaves-product.png'),
('Huile d''Avocat Pressée à Froid', 'Huile d''avocat pure pressée à froid. Parfaite pour la cuisine et les soins cosmétiques.', 
 24.90, 'avocado_oil', '/resources/avocado-oil-bottle.png'),
('Huile d''Avocat Cosmétique', 'Huile d''avocat spécialement conçue pour les soins de la peau et des cheveux. Riche en vitamines E et K.', 
 29.90, 'avocado_oil', '/resources/avocado-oil-bottle.png');

-- NOTE: DB user creation and GRANTs are removed from versioned schema. Manage DB users/privileges outside of schema in provisioning scripts or manually.
