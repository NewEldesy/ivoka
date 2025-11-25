# IVOKA - Site Web Thé & Huile d'Avocat Naturels

## 🌿 Présentation

IVOKA est un site vitrine moderne et épuré pour une marque de produits naturels biologiques, développé avec **React.js** pour le frontend et **Java Servlets/JSP** pour l'API REST backend. Le site met en valeur les thés et huiles d'avocat pressées à froid, avec un design respectant les principes de durabilité et de bien-être.

## 🚀 Architecture Technique

### Frontend (React.js)
- **React 18** - Bibliothèque JavaScript pour les interfaces utilisateur
- **React Router DOM 6** - Navigation entre les pages
- **Tailwind CSS** - Framework CSS utilitaire
- **Anime.js** - Bibliothèque d'animation
- **Splitting.js** - Animation de texte
- **Axios** - Client HTTP pour les requêtes API

### Backend (Java Servlets/JSP)
- **Java 11** - Langage de programmation
- **Servlet API 4.0** - Gestion des requêtes HTTP
- **JSP 2.3** - Pages Java Server
- **MySQL** - Base de données relationnelle
- **Apache Commons DBCP2** - Pool de connexions
- **JSON-P 1.1** - Traitement JSON

### Base de Données
- **MySQL 8.0** - Système de gestion de base de données
- **Tables**: products, messages, users
- **Support UTF-8** pour l'internationalisation

## 📁 Structure du Projet

```
/mnt/okcomputer/output/
├── frontend/                 # Application React
│   ├── src/
│   │   ├── components/      # Composants réutilisables
│   │   ├── pages/           # Pages principales
│   │   ├── hooks/           # Hooks personnalisés
│   │   ├── services/        # Services API
│   │   └── App.js           # Composant principal
│   ├── public/              # Fichiers statiques
│   └── package.json         # Dépendances frontend
├── backend/                 # API Java
│   ├── src/main/java/
│   │   ├── servlets/        # Servlets API REST
│   │   ├── models/          # Modèles de données
│   │   └── dao/             # Accès aux données
│   ├── src/main/resources/  # Configuration
│   └── pom.xml              # Configuration Maven
├── resources/               # Images et assets
│   ├── hero-tea-plantation.png
│   ├── tea-leaves-product.png
│   ├── avocado-oil-bottle.png
│   └── organic-background.png
├── azure-config.md         # Configuration Azure
├── deploy.sh               # Script de déploiement
└── README.md               # Ce fichier
```

## 🎨 Design System

### Palette de Couleurs
- **Fond principal**: #FEFEFE (blanc cassé)
- **Couleur primaire**: #2D3436 (gris foncé)
- **Couleur secondaire**: #6C7B7F (gris moyen)
- **Couleur d'accent**: #7D8471 (vert olive)
- **Couleur de survol**: #6B7280 (gris)

### Typographie
- **Police principale**: 'Inter', sans-serif
- **Police d'affichage**: 'Playfair Display', serif
- **Taille des titres**: 3rem (48px) pour h1, 2rem (32px) pour h2
- **Taille du corps**: 1rem (16px) avec interlignage 1.6

## 🔧 Installation et Configuration

### Prérequis
- Node.js 16+ et npm/yarn
- Java 11+ et Maven
- MySQL 8.0+
- Git

### Frontend (React)
```bash
# Navigation vers le dossier frontend
cd frontend

# Installation des dépendances
npm install

# Démarrage du serveur de développement
npm start

# Build pour la production
npm run build
```

### Backend (Java)
```bash
# Navigation vers le dossier backend
cd backend

# Compilation du projet
mvn clean compile

# Package de l'application
mvn package

# Démarrage du serveur Tomcat
mvn tomcat7:run
```

### Base de Données
```sql
-- Création de la base de données
CREATE DATABASE ivoka_db;
USE ivoka_db;

-- Exécution du script SQL
SOURCE src/main/resources/schema.sql;
```

## 📱 Pages du Site

### 1. Accueil (`/`)
- Hero section avec animation
- Présentation des valeurs
- Aperçu des produits
- Call-to-action

### 2. À Propos (`/about`)
- Histoire de l'entreprise
- Mission et valeurs
- Présentation de l'équipe

### 3. Produits (`/products`)
- Catalogue des thés
- Huiles d'avocat
- Filtres par catégorie
- Fiches produits détaillées

### 4. Bienfaits (`/benefits`)
- Bienfaits du thé
- Bienfaits de l'huile d'avocat
- Informations scientifiques

### 5. Contact (`/contact`)
- Formulaire de contact
- Coordonnées de l'entreprise
- Réseaux sociaux

## 🔌 API REST Endpoints

### Produits (`/api/products`)
- `GET /api/products` - Récupérer tous les produits
- `GET /api/products?category=tea` - Récupérer par catégorie
- `POST /api/products` - Créer un nouveau produit

### Messages (`/api/messages`)
- `POST /api/messages` - Envoyer un message de contact

### Utilisateurs (`/api/users`)
- `POST /api/users` - Créer un nouvel utilisateur

## 🎯 Fonctionnalités Clés

### Frontend
- ✅ Design responsive et moderne
- ✅ Animations fluides avec Anime.js
- ✅ Navigation SPA avec React Router
- ✅ Formulaires de contact interactifs
- ✅ Gestion d'état avec React Hooks
- ✅ Optimisation des performances

### Backend
- ✅ API RESTful avec Java Servlets
- ✅ Gestion des CORS
- ✅ Validation des données
- ✅ Pool de connexions MySQL
- ✅ Sécurité des requêtes SQL
- ✅ Support JSON

### Base de Données
- ✅ Schéma normalisé
- ✅ Index optimisés
- ✅ Contraintes d'intégrité
- ✅ Données de test incluses

## 🚀 Déploiement sur Azure

### Méthode 1 : Script de Déploiement Automatisé
```bash
# Rendre le script exécutable
chmod +x deploy.sh

# Exécuter le déploiement
./deploy.sh
```

### Méthode 2 : Azure CLI Manuel
```bash
# Créer le groupe de ressources
az group create --name ivoka-rg --location westeurope

# Déployer avec Terraform
cd azure-terraform
terraform init
terraform plan -var="mysql_password=VotreMotDePasse"
terraform apply

# Déployer le backend WAR
az webapp deploy --name ivoka-api --resource-group ivoka-rg --src-path backend/target/ivoka-api.war

# Déployer le frontend React
az storage blob upload-batch --account-name ivokastorage --source frontend/build --destination '$web' --overwrite
```

### Méthode 3 : Azure DevOps Pipeline
Utiliser le fichier `azure-pipelines.yml` fourni pour un déploiement automatique.

## 📊 Performance

### Optimisations Frontend
- Code splitting avec React.lazy
- Images optimisées et compressées
- CSS minimal avec Tailwind
- Animations performantes

### Optimisations Backend
- Pool de connexions configuré
- Requêtes SQL optimisées
- Caching approprié
- Gestion des erreurs

## 🔒 Sécurité

### Mesures de Sécurité
- Validation des entrées utilisateur
- Protection contre les injections SQL
- Headers CORS configurés
- Gestion sécurisée des erreurs
- HTTPS recommandé en production

## 📈 SEO et Accessibilité

### SEO
- Balises meta optimisées
- Structure sémantique HTML
- URLs propres et descriptives
- Contenu de qualité

### Accessibilité
- Contraste des couleurs WCAG AA
- Navigation au clavier
- Labels appropriés
- Structure sémantique

## 🌟 Points Forts

1. **Architecture Moderne** - React.js + Java Servlets/JSP
2. **Performance** - Chargement rapide et animations fluides
3. **Responsive** - Adaptation parfaite sur tous les appareils
4. **Sécurité** - Protection contre les vulnérabilités courantes
5. **Maintenabilité** - Code propre et bien documenté
6. **Évolutivité** - Architecture modulaire et extensible
7. **Déploiement Azure** - Prêt pour Azure App Service

## 📞 Support

Pour toute question ou problème technique :
- Email: contact@ivoka.fr
- Téléphone: +33 4 67 89 12 34
- Heures: Lundi-Vendredi, 9h-18h

## 📄 Licence

Ce projet est développé pour IVOKA. Tous droits réservés.

## 🔗 Liens Importants

- **Site en ligne** : https://pswybfqcerlbi.ok.kimi.link
- **Configuration Azure** : [azure-config.md](./azure-config.md)
- **Script de déploiement** : [deploy.sh](./deploy.sh)

---

*Site web développé avec passion pour la nature et l'innovation.* 🌱

**Technologies** : React.js, Java Servlets/JSP, MySQL, Tailwind CSS, Azure App Service