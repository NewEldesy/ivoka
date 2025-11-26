# Test Local - Guide Rapide

## Commandes Essentielles

### 1️⃣ Vérifier Java 21
```powershell
java -version
```
**Résultat attendu:** `openjdk version "21.x.x"`

### 2️⃣ Construire l'Application
```bash
cd backend
mvn clean package -DskipTests
```
**Signes de succès:**
- ✅ `BUILD SUCCESS`
- ✅ Fichier `target/ivoka-api.war` créé

### 3️⃣ Lancer les Tests Automatisés

**Option 1 - PowerShell (Recommandé)**
```powershell
.\TEST_LOCAL.ps1
```

**Option 2 - Batch (Windows)**
```batch
TEST_LOCAL.bat
```

### 4️⃣ Installer Tomcat 10.1+

```bash
# Télécharger depuis:
https://tomcat.apache.org/download-10.cgi

# Extraire dans:
C:\apache-tomcat-10.1.x
```

### 5️⃣ Déployer le WAR

```bash
# Copier le WAR
copy target\ivoka-api.war %TOMCAT_HOME%\webapps\

# Redémarrer Tomcat
%TOMCAT_HOME%\bin\shutdown.bat
timeout /t 3
%TOMCAT_HOME%\bin\startup.bat
```

### 6️⃣ Tester les Endpoints

```bash
# Test 1: Produits
curl http://localhost:8080/ivoka-api/api/products

# Test 2: Authentification
curl -X POST http://localhost:8080/ivoka-api/api/auth ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"password\"}"

# Test 3: Panier
curl -X POST http://localhost:8080/ivoka-api/api/cart ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":1,\"quantity\":1}"
```

## ✅ Checklist de Vérification

- [ ] Java 21 installé
- [ ] Maven accessible
- [ ] Application compilée sans erreurs
- [ ] WAR créé (target/ivoka-api.war)
- [ ] Aucun import javax (ancien)
- [ ] Tomcat 10.1+ prêt
- [ ] Endpoints testés
- [ ] Logs vérifiés (pas d'erreurs)

## 🆘 Dépannage Rapide

| Problème | Solution |
|----------|----------|
| `java` not found | Installer Java 21 et ajouter au PATH |
| `mvn` not found | Installer Maven et ajouter au PATH |
| Build fails | Vérifier les imports jakarta.* |
| Port 8080 in use | `netstat -ano \| findstr :8080` |
| Tomcat won't start | Vérifier JAVA_HOME |
| ClassNotFoundException | Mettre à jour Tomcat à 10.1+ |

## 📊 Fichiers de Test Créés

- `TEST_LOCAL.ps1` - Script PowerShell complet
- `TEST_LOCAL.bat` - Script Batch Windows
- `TEST_LOCAL_GUIDE.md` - Guide détaillé

## 🎯 Résultat Attendu

```
✓ Java 21 trouvé
✓ Maven trouvé
✓ Compilation réussie
✓ WAR créé avec succès
✓ Imports jakarta vérifiés
✓ Bytecode Java 21 détecté
✓ Dépendances correctes

=> Application prête pour déploiement!
```

---

**Prêt? Exécutez le test:** `.\TEST_LOCAL.ps1`
