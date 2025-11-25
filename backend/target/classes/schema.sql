-- Base de données IVOKA
-- Script de création des tables

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
    read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_read (read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    newsletter BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_newsletter (newsletter)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion de données de test
INSERT INTO products (name, description, price, category, image_url) VALUES
('Thé Vert Bio', 'Thé vert biologique cultivé localement, riche en antioxydants. Parfait pour vos moments de détente.', 
 12.90, 'tea', '/resources/tea-leaves-product.png'),
('Thé Noir Premium', 'Thé noir de qualité supérieure avec des notes épicées et maltées. Idéal pour le petit-déjeuner.', 
 15.50, 'tea', '/resources/tea-leaves-product.png'),
('Thé Blanc Délicat', 'Thé blanc aux arômes délicats et floraux. Une expérience gustative raffinée.', 
 18.90, 'tea', '/resources/tea-leaves-product.png'),
('Huile d\'Avocat Pressée à Froid', 'Huile d\'avocat pure pressée à froid. Parfaite pour la cuisine et les soins cosmétiques.', 
 24.90, 'avocado_oil', '/resources/avocado-oil-bottle.png'),
('Huile d\'Avocat Cosmétique', 'Huile d\'avocat spécialement conçue pour les soins de la peau et des cheveux. Riche en vitamines E et K.', 
 29.90, 'avocado_oil', '/resources/avocado-oil-bottle.png');

-- Création d'un utilisateur pour l'application
CREATE USER IF NOT EXISTS 'ivoka_user'@'localhost' IDENTIFIED BY 'ivoka_password';
GRANT ALL PRIVILEGES ON ivoka_db.* TO 'ivoka_user'@'localhost';
FLUSH PRIVILEGES;