# Guide de Test Local - IVOKA Application

## 🧪 Plan de Test Local

### Phase 1: Vérification de l'Environnement

#### 1. Vérifier Java 21 LTS
```bash
# Vérifier la version de Java
java -version

# Résultat attendu:
# openjdk version "21.x.x" ou "Java 21.x.x"
# Runtime version "21.x.x"
```

#### 2. Vérifier Maven
```bash
# Vérifier Maven
mvn --version

# Résultat attendu:
# Apache Maven 3.8.0+ ou plus recent
# Java version: 21.x.x
```

#### 3. Vérifier la Connexion à la Base de Données
```bash
# Tester la connexion MySQL
mysql -h localhost -u root -p -e "SELECT VERSION();"

# Ou vérifier que MySQL est en cours d'exécution
# Windows: sc query MySQL80
# Linux/Mac: ps aux | grep mysql
```

---

### Phase 2: Construction du Projet

#### Étape 1: Nettoyer et Compiler
```bash
cd c:\Users\USER\Documents\ivoka\backend
mvn clean compile
```

**Points de vérification:**
- ✓ Pas d'erreurs de compilation
- ✓ Pas d'erreurs d'imports (jakarta.* correctement importé)
- ✓ Tous les fichiers .java compilés avec succès
- ✓ Temps de compilation acceptable (< 1 minute)

#### Étape 2: Exécuter les Tests (si applicable)
```bash
mvn test
```

**Points de vérification:**
- ✓ Tous les tests passent (si des tests existent)
- ✓ Pas de ClassNotFoundException
- ✓ Pas d'erreurs de base de données

#### Étape 3: Construire le WAR
```bash
mvn clean package
```

**Points de vérification:**
- ✓ BUILD SUCCESS
- ✓ Fichier WAR créé: `target/ivoka-api.war`
- ✓ Taille WAR raisonnable (> 100KB)

---

### Phase 3: Test de Déploiement Local

#### Option A: Tomcat Local (Recommandé)

**Prérequis:**
- Tomcat 10.1+ installé
- CATALINA_HOME défini

**Étapes:**

1. **Sauvegarder la version actuelle** (si applicable)
```bash
# Copier le WAR existant en sauvegarde
copy %TOMCAT_HOME%\webapps\ivoka-api.war %TOMCAT_HOME%\webapps\ivoka-api.war.bak
```

2. **Déployer le nouveau WAR**
```bash
copy target\ivoka-api.war %TOMCAT_HOME%\webapps\
```

3. **Redémarrer Tomcat**
```bash
# Arrêter Tomcat
%TOMCAT_HOME%\bin\shutdown.bat

# Attendre quelques secondes
timeout /t 5

# Démarrer Tomcat
%TOMCAT_HOME%\bin\startup.bat
```

4. **Vérifier les logs**
```bash
# Afficher les logs en temps réel
type %TOMCAT_HOME%\logs\catalina.out

# Rechercher les erreurs
find /I "ERROR" %TOMCAT_HOME%\logs\catalina.out
```

**Signes de succès:**
- ✓ Application déployée (message dans les logs)
- ✓ Pas de StackTrace d'erreur
- ✓ Application accessible sur http://localhost:8080/ivoka-api/

#### Option B: Serveur Maven Intégré (Pour Tests Rapides)

```bash
cd backend
mvn jetty:run
```

**Signes de succès:**
- ✓ Jetty démarre sur port 8080
- ✓ Application accessible sur http://localhost:8080/
- ✓ Pas d'erreurs de démarrage

---

### Phase 4: Test des Endpoints API

#### 1. Test de Santé (Health Check)
```bash
# Tester la connexion basique
curl -X GET http://localhost:8080/ivoka-api/

# Résultat attendu: Page d'accueil ou message d'erreur 404 (pas grave)
```

#### 2. Tester Produits
```bash
# Récupérer la liste des produits
curl -X GET http://localhost:8080/ivoka-api/api/products

# Résultat attendu: JSON avec liste de produits
# Exemple:
# [
#   {
#     "id": 1,
#     "name": "Produit 1",
#     "price": 99.99,
#     ...
#   }
# ]
```

#### 3. Tester Authentification
```bash
# Tentative de connexion
curl -X POST http://localhost:8080/ivoka-api/api/auth \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"password\"}"

# Résultat attendu: 
# Réponse JSON avec token ou message d'erreur
```

#### 4. Tester Panier
```bash
# Ajouter un article au panier
curl -X POST http://localhost:8080/ivoka-api/api/cart \
  -H "Content-Type: application/json" \
  -d "{\"productId\":1,\"quantity\":1}"

# Résultat attendu: Confirmation de l'ajout
```

#### 5. Tester Utilisateurs
```bash
# Récupérer la liste des utilisateurs (protégé)
curl -X GET http://localhost:8080/ivoka-api/api/users \
  -H "Authorization: Bearer YOUR_TOKEN"

# Résultat attendu: Liste des utilisateurs ou erreur 401
```

#### 6. Tester Messages
```bash
# Envoyer un message
curl -X POST http://localhost:8080/ivoka-api/api/messages \
  -H "Content-Type: application/json" \
  -d "{\"subject\":\"Test\",\"message\":\"Ceci est un test\"}"

# Résultat attendu: Confirmation d'envoi
```

---

### Phase 5: Vérifications Avancées

#### 1. Vérifier les Imports Jakarta
```bash
# Rechercher les imports de javax (il ne devrait y en avoir aucun)
grep -r "import javax" backend/src/main/java/com/ivoka/api/servlets/
grep -r "import javax" backend/src/main/java/com/ivoka/api/listener/
grep -r "import javax" backend/src/main/java/com/ivoka/api/dao/

# Résultat attendu: Aucun résultat (OK)
```

#### 2. Vérifier la Version Java Compilée
```bash
# Vérifier que le bytecode est Java 21
javap -verbose target/classes/com/ivoka/api/servlets/ProductsServlet.class | grep "major version"

# Résultat attendu: major version: 65 (équivalent Java 21)
```

#### 3. Tester la Connexion Base de Données
```bash
# Vérifier que la base de données est accessible
# Accéder à l'admin panel et vérifier les données
curl -X GET http://localhost:8080/ivoka-api/api/admin \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

#### 4. Vérifier les Performances
```bash
# Tester avec load (utiliser Apache Bench)
ab -n 100 -c 10 http://localhost:8080/ivoka-api/api/products

# Résultat attendu:
# - Tous les requêtes réussissent (200 OK)
# - Temps de réponse < 500ms par requête
# - Pas d'erreurs 500
```

---

### Phase 6: Checklist de Validation

#### Compilation et Build
- [ ] `mvn clean compile` réussit sans erreurs
- [ ] Aucune erreur d'import (javax → jakarta)
- [ ] `mvn clean package` produit un WAR sans erreurs
- [ ] Fichier WAR existe: `target/ivoka-api.war`

#### Déploiement
- [ ] Tomcat démarre avec la nouvelle application
- [ ] Aucune erreur ClassNotFoundException
- [ ] Aucune erreur dans les logs au démarrage
- [ ] Application accessible sur http://localhost:8080/ivoka-api/

#### Tests Fonctionnels
- [ ] Endpoint /api/products répond avec JSON
- [ ] Endpoint /api/auth accepte les requêtes POST
- [ ] Endpoint /api/users répond (authentification ou 401)
- [ ] Endpoint /api/cart accepte les requêtes POST
- [ ] Endpoint /api/messages accepte les requêtes POST
- [ ] Base de données est accessible
- [ ] Sessions utilisateur fonctionnent

#### Logs et Monitoring
- [ ] Aucune erreur "ClassNotFoundException"
- [ ] Aucune erreur "PackageNotFoundException"
- [ ] Aucune exception StackTrace grave
- [ ] Mémoire stable (pas de fuites)
- [ ] Pas de warnings Jakarta EE

#### Performance
- [ ] Temps de réponse acceptable (< 1000ms)
- [ ] Pas de timeouts
- [ ] Pas de déconnexions de base de données
- [ ] CPU usage normal (< 50% inactif)

---

## 🔧 Configuration Requise pour Tests

### Variables d'Environnement
```bash
# Java 21
set JAVA_HOME=C:\path\to\jdk21

# Tomcat
set CATALINA_HOME=C:\path\to\tomcat10

# Maven (optionnel)
set M2_HOME=C:\path\to\maven
```

### Fichiers de Configuration Requis
```
backend/src/main/resources/database.properties
backend/src/main/resources/complete-schema.sql
backend/src/main/webapp/WEB-INF/web.xml
```

### Ports Requis (Libres)
- 8080: Tomcat (HTTP)
- 8009: Tomcat (AJP)
- 3306: MySQL (Base de données)

---

## 📊 Résultats Attendus

### Build Réussi
```
[INFO] --- maven-compiler-plugin:3.11.0:compile
[INFO] --- maven-jar-plugin:3.3.0:jar
[INFO] --- maven-war-plugin:3.3.2:war
[INFO] Building war: ...target/ivoka-api.war
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXXs
```

### Déploiement Réussi
```
[...] INFO  Server startup in X ms with Y classes
[...] INFO  [IvokaAPI] Initialized successfully
[...] INFO  Application ready at http://localhost:8080/ivoka-api/
```

### Tests API Réussis
```json
GET /api/products
→ 200 OK
→ [{"id": 1, "name": "...", ...}]

POST /api/auth
→ 200 OK ou 401 Unauthorized
→ {"token": "..."} ou {"error": "..."}
```

---

## 🆘 Dépannage Rapide

| Erreur | Cause Probable | Solution |
|--------|----------------|----------|
| `ClassNotFoundException` | Import incorrect | Vérifier imports jakarta.* |
| `Port 8080 already in use` | Tomcat déjà en cours | `netstat -ano \| findstr :8080` |
| `Database connection failed` | MySQL arrêté | Vérifier MySQL est lancé |
| `BUILD FAILURE` | Dépendances manquantes | `mvn dependency:resolve` |
| `Unsupported class version` | Java trop vieux | Vérifier Java 21 est utilisé |
| `JAR not found` | WAR incomplet | Vérifier target/ivoka-api.war |

---

## 📝 Notes

- ✅ Le projet est prêt pour les tests
- ✅ Tous les imports ont été migrés vers Jakarta
- ✅ Les dépendances sont compatibles Java 21
- ⏳ Temps estimé de test: 30-45 minutes

## 🎯 Objectif Final

À la fin des tests, vous devriez avoir:
1. ✅ Application compilée et packagée
2. ✅ Application déployée sur Tomcat
3. ✅ Tous les endpoints testés et fonctionnels
4. ✅ Aucune erreur dans les logs
5. ✅ Base de données accessible
6. ✅ Performance acceptable

Bon test! 🚀
