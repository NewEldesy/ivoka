# IVOKA — Application e-commerce

Application e-commerce complète avec espace admin. Backend Spring Boot 3.2.5 + Frontend React 18, servis depuis le même serveur Java.

---

## Prérequis

- Java 21
- Maven 3.8+
- Node.js 18+ (géré automatiquement par `frontend-maven-plugin`)
- MySQL (XAMPP recommandé en local)

---

## Installation

### 1. Cloner le dépôt

```bash
git clone <url-du-repo>
cd ivoka
```

### 2. Initialiser la base de données

Démarrer MySQL (XAMPP), puis exécuter le schéma complet :

```bash
# Windows (XAMPP)
"C:\xampp\mysql\bin\mysql.exe" -u root -e "CREATE DATABASE IF NOT EXISTS ivoka_db;"
"C:\xampp\mysql\bin\mysql.exe" -u root ivoka_db < backend/src/main/resources/complete-schema.sql
```

### 3. Configurer les variables d'environnement

Copier le fichier exemple et le remplir :

```bash
cp backend/.env.example backend/.env
```

Ouvrir `backend/.env` et renseigner les valeurs :

```env
# Base de données
DB_HOST=localhost
DB_PORT=3306
DB_NAME=ivoka_db
DB_USER=root            # utilisateur MySQL
DB_PASSWORD=            # mot de passe MySQL (vide par défaut sur XAMPP)

# CORS — origines autorisées (séparées par des virgules)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080

# Comptes créés automatiquement au premier démarrage (si absents en base)
# Décommenter et personnaliser si nécessaire.
# Les mots de passe doivent contenir 8+ caractères avec majuscule, minuscule et chiffre ou caractère spécial.
# INIT_ADMIN_EMAIL=admin@ivoka.fr
# INIT_ADMIN_PASSWORD=Admin@ivoka2024
# INIT_ADMIN_FIRST_NAME=Admin
# INIT_ADMIN_LAST_NAME=IVOKA

# INIT_USER_EMAIL=user@ivoka.fr
# INIT_USER_PASSWORD=User@ivoka2024
# INIT_USER_FIRST_NAME=Utilisateur
# INIT_USER_LAST_NAME=Test
```

> **Si les variables `INIT_*` sont commentées**, les comptes par défaut seront tout de même créés avec les valeurs hardcodées dans `application.properties` (voir ci-dessous). Décommenter les lignes uniquement si vous voulez des credentials différents.

Valeurs par défaut appliquées si les variables ne sont pas définies :

| Variable | Valeur par défaut |
|---|---|
| `INIT_ADMIN_EMAIL` | `admin@ivoka.fr` |
| `INIT_ADMIN_PASSWORD` | `Admin@ivoka2024` |
| `INIT_USER_EMAIL` | `user@ivoka.fr` |
| `INIT_USER_PASSWORD` | `User@ivoka2024` |

### 4. Lancer l'application

```bash
cd backend
mvn spring-boot:run
```

Le plugin Maven compile automatiquement le frontend React. L'application est accessible à :

```
http://localhost:8080/ivoka-api/
```

L'espace admin est accessible à :

```
http://localhost:8080/ivoka-api/admin
```

---

## Développement frontend (hot reload)

Pour travailler sur le frontend avec rechargement automatique :

```bash
# Créer le fichier de config frontend
echo "REACT_APP_API_URL=http://localhost:8080/ivoka-api/api" > frontend/.env
echo "PUBLIC_URL=/ivoka-api" >> frontend/.env

cd frontend
npm install
npm start
```

Le serveur de dev React tourne sur `http://localhost:3000/ivoka-api/`.

---

## Structure du projet

```
ivoka/
├── backend/                  # Spring Boot 3.2.5
│   ├── src/main/java/com/ivoka/api/
│   │   ├── config/           # WebConfig, DataSourceConfig, DataInitializer
│   │   ├── controllers/      # AuthController, AdminController, ...
│   │   ├── dao/              # UserAuthDAO, ProductDAO, OrderDAO, ...
│   │   ├── models/           # User, Product, Order, ...
│   │   └── utils/            # PasswordUtils, SessionUtils
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── complete-schema.sql   # Schéma MySQL complet
│   │   └── static/               # Build React (généré par Maven)
│   ├── .env                  # Variables locales — NE PAS COMMITTER
│   └── .env.example          # Modèle à copier
└── frontend/                 # React 18
    ├── src/
    │   ├── components/       # Navigation, Footer, Cart, ConfirmModal, ...
    │   ├── context/          # AuthContext, CartContext
    │   ├── pages/            # Home, Products, Admin, Login, ...
    │   └── services/api.js   # Tous les appels API (axios)
    └── .env                  # Variables locales — NE PAS COMMITTER
```

---

## Sécurité

- Ne jamais committer les fichiers `.env`.
- Les mots de passe sont hachés en PBKDF2WithHmacSHA256 (10 000 itérations).
- Les sessions sont stockées en base avec expiration 24h.
