
# Guide de Déploiement - IVOKA API Java 21

## ✅ Status: Prêt pour le déploiement

Fichier WAR: **`backend/target/ivoka-api.war`** (7.65 MB)
Date: 2025-11-26
Java Version: 21 LTS
Framework: Jakarta EE 10

---

## 1️⃣ Étape 1: Télécharger Tomcat 10.1+

Tomcat doit être version **10.1 minimum** (Jakarta EE 10 compatible)

📥 **Télécharger:**
```
https://tomcat.apache.org/download-10.cgi
```

📦 **Fichier recommandé:**
- `apache-tomcat-10.1.x-windows-x64.zip` (pour Windows)

---

## 2️⃣ Étape 2: Installer Tomcat

### Windows:

1. Télécharger le ZIP
2. Extraire vers: `C:\tomcat\` (ou votre chemin préféré)
3. Créer la variable d'environnement:
   ```
   CATALINA_HOME = C:\tomcat
   ```

### Vérifier l'installation:
```bash
cd C:\tomcat\bin
catalina.bat version
```

---

## 3️⃣ Étape 3: Déployer le WAR

### Option A: Déploiement simple (copie)

```bash
# 1. Copier le WAR
Copy: c:\Users\USER\Documents\ivoka\backend\target\ivoka-api.war
To:   C:\tomcat\webapps\

# 2. Démarrer Tomcat
cd C:\tomcat\bin
catalina.bat run

# 3. Vérifier dans les logs
# Vous devriez voir:
# [INFO] ... Deploying web application archive [ivoka-api.war]
```

### Option B: Déploiement avec Manager (recommandé)

```bash
# 1. Démarrer Tomcat
cd C:\tomcat\bin
catalina.bat run

# 2. Ouvrir le Manager
URL: http://localhost:8080/manager/html

# 3. Authentifier (user/password par défaut)
# À configurer dans: C:\tomcat\conf\tomcat-users.xml

# 4. Deploy section > Select WAR file
# Sélectionner: ivoka-api.war
# Context path: /ivoka-api
# Click: Deploy
```

---

## 4️⃣ Étape 4: Vérifier le Déploiement

### ✅ Checks après déploiement:

1. **Accéder à l'application:**
   ```
   http://localhost:8080/ivoka-api/
   ```

2. **Vérifier les logs:**
   ```
   File: C:\tomcat\logs\catalina.out
   OR:   C:\tomcat\logs\localhost.YYYY-MM-DD.log
   ```

3. **Rechercher les erreurs:**
   ```
   ❌ Si vous voyez: 
   - "Caused by java.lang.UnsupportedClassVersionError"
     → Tomcat version incompatible (besoin 10.1+)
   
   - "Caused by java.lang.ClassNotFoundException: jakarta.*"
     → Dépendances Jakarta manquantes
   
   - "java.sql.SQLException: Cannot connect to MySQL"
     → Vérifier la base de données MySQL
   ```

---

## 5️⃣ Étape 5: Tester les Endpoints

### Teste basiques avec cURL ou Postman:

```bash
# 1. GET Products
curl http://localhost:8080/ivoka-api/api/products

# 2. GET Users
curl http://localhost:8080/ivoka-api/api/users

# 3. POST Auth (login)
curl -X POST http://localhost:8080/ivoka-api/api/auth \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'

# 4. POST Cart
curl -X POST http://localhost:8080/ivoka-api/api/cart \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

---

## ⚙️ Configuration Requise

### Base de Données MySQL

Créer la base de données:
```sql
CREATE DATABASE ivoka;
```

Configurer `database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/ivoka
db.user=root
db.password=password
```

Importer le schéma:
```bash
mysql -u root ivoka < backend/src/main/resources/schema.sql
```

---

## 🔧 Troubleshooting

### ❌ Erreur: "UnsupportedClassVersionError"
```
Solution: Vérifier que Tomcat 10.1+ est installé
Vérifier: java -version (doit être Java 21+)
```

### ❌ Erreur: "ClassNotFoundException: jakarta.servlet"
```
Solution: 
1. Vérifier que le WAR contient jakarta-*.jar
2. Vérifier que Tomcat 10.1+ est utilisé
3. Tomcat 9.x n'est PAS compatible avec Jakarta
```

### ❌ Erreur: "Cannot connect to MySQL"
```
Solution:
1. Vérifier que MySQL est en cours d'exécution
2. Vérifier les credentials dans database.properties
3. Vérifier que la base 'ivoka' existe
```

### ❌ Port 8080 déjà utilisé
```
Solution: Modifier dans C:\tomcat\conf\server.xml
Chercher: <Connector port="8080"
Changer: port="8081" (ou autre port disponible)
```

---

## 📋 Checklist Déploiement

- [ ] Tomcat 10.1+ téléchargé et extrait
- [ ] CATALINA_HOME configuré
- [ ] Fichier WAR copié vers webapps/
- [ ] MySQL configuré et base créée
- [ ] database.properties mis à jour
- [ ] Tomcat démarré
- [ ] Application accessible (http://localhost:8080/ivoka-api/)
- [ ] Logs vérifiés (pas d'erreurs)
- [ ] Endpoints testés

---

## 🚀 Démarrage/Arrêt Tomcat

### Windows Batch:
```bash
# Démarrer
cd C:\tomcat\bin
catalina.bat run

# Arrêter
catalina.bat stop
```

### Windows PowerShell:
```powershell
# Démarrer
cd C:\tomcat\bin
.\catalina.bat run

# Arrêter
.\catalina.bat stop
```

---

## 📞 Support Utile

- **Tomcat Documentation:** https://tomcat.apache.org/
- **Jakarta EE:** https://jakarta.ee/
- **Java 21:** https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
- **MySQL:** https://dev.mysql.com/

---

**Date:** 2025-11-26  
**Status:** ✅ Ready to Deploy  
**WAR File:** ivoka-api.war (7.65 MB)  
**Java:** 21 LTS  
**Framework:** Jakarta EE 10
