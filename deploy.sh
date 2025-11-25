#!/bin/bash

# Script de déploiement pour IVOKA
# Ce script automatise le déploiement local du site web

echo "🌿 IVOKA - Script de Déploiement"
echo "================================"

# Vérification des prérequis
echo "📋 Vérification des prérequis..."

# Vérifier Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js n'est pas installé. Veuillez installer Node.js 16+"
    exit 1
fi

# Vérifier Java
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé. Veuillez installer Java 11+"
    exit 1
fi

# Vérifier Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé. Veuillez installer Maven"
    exit 1
fi

# Vérifier MySQL
if ! command -v mysql &> /dev/null; then
    echo "⚠️  MySQL n'est pas installé localement. Assurez-vous d'avoir accès à une base de données MySQL"
fi

echo "✅ Prérequis vérifiés"

# Configuration de la base de données
echo ""
echo "🗄️  Configuration de la base de données..."
read -p "Hôte MySQL (localhost): " DB_HOST
read -p "Port MySQL (3306): " DB_PORT
read -p "Utilisateur MySQL (root): " DB_USER
read -s -p "Mot de passe MySQL: " DB_PASS
echo ""
read -p "Nom de la base de données (ivoka_db): " DB_NAME

# Valeurs par défaut
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-3306}
DB_USER=${DB_USER:-root}
DB_NAME=${DB_NAME:-ivoka_db}

# Création de la base de données
echo "🔄 Création de la base de données..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;"

if [ $? -eq 0 ]; then
    echo "✅ Base de données créée avec succès"
    
    # Exécution du script SQL
    echo "🔄 Exécution du script SQL..."
    mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < backend/src/main/resources/schema.sql
    
    if [ $? -eq 0 ]; then
        echo "✅ Schéma de base de données créé avec succès"
    else
        echo "❌ Erreur lors de l'exécution du script SQL"
        exit 1
    fi
else
    echo "❌ Erreur lors de la création de la base de données"
    exit 1
fi

# Configuration du backend
echo ""
echo "🔧 Configuration du backend..."
cd backend

# Mise à jour de la configuration de la base de données
sed -i.bak "s|db.url=.*|db.url=jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME?useSSL=false\&serverTimezone=UTC\&allowPublicKeyRetrieval=true|" src/main/resources/database.properties
sed -i.bak "s|db.user=.*|db.user=$DB_USER|" src/main/resources/database.properties
sed -i.bak "s|db.password=.*|db.password=$DB_PASS|" src/main/resources/database.properties

echo "✅ Configuration du backend mise à jour"

# Compilation et packaging du backend
echo "🔄 Compilation du backend..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Backend compilé avec succès"
else
    echo "❌ Erreur lors de la compilation du backend"
    exit 1
fi

cd ..

# Configuration du frontend
echo ""
echo "🔧 Configuration du frontend..."
cd frontend

# Mise à jour de la configuration de l'API
if [ ! -f .env.local ]; then
    cp .env .env.local
fi

sed -i.bak "s|REACT_APP_API_URL=.*|REACT_APP_API_URL=http://localhost:8080/api|" .env.local

echo "✅ Configuration du frontend mise à jour"

# Installation des dépendances
echo "🔄 Installation des dépendances frontend..."
npm install

if [ $? -eq 0 ]; then
    echo "✅ Dépendances frontend installées"
else
    echo "❌ Erreur lors de l'installation des dépendances frontend"
    exit 1
fi

# Build du frontend
echo "🔄 Build du frontend..."
npm run build

if [ $? -eq 0 ]; then
    echo "✅ Frontend buildé avec succès"
else
    echo "❌ Erreur lors du build du frontend"
    exit 1
fi

cd ..

# Instructions de démarrage
echo ""
echo "🚀 Démarrage des services..."
echo ""
echo "Pour démarrer l'application :"
echo ""
echo "1. Démarrer le serveur backend (Tomcat):"
echo "   cd backend"
echo "   mvn tomcat7:run"
echo ""
echo "2. Dans un autre terminal, démarrer le serveur frontend :"
echo "   cd frontend"
echo "   npm start"
echo ""
echo "3. Ouvrir votre navigateur et accéder à :"
echo "   Frontend : http://localhost:3000"
echo "   Backend API : http://localhost:8080/api"
echo ""
echo "🌿 IVOKA est prêt à être utilisé !"
echo "=================================="