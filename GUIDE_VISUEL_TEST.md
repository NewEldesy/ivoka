# 🎬 Guide Visuel - Test Local IVOKA Java 21

## Scénario: Tester Localement en 3 Minutes

```
┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 1: Préparation (30 secondes)                              │
└─────────────────────────────────────────────────────────────────┘

Terminal ouvert:
C:\Users\USER\Documents\ivoka> 

✓ Vérifier qu'on est au bon endroit
C:\Users\USER\Documents\ivoka> cd .
C:\Users\USER\Documents\ivoka> 


┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 2: Vérifier Java 21 (30 secondes)                         │
└─────────────────────────────────────────────────────────────────┘

C:\Users\USER\Documents\ivoka> java -version

Résultat attendu:
  openjdk version "21.0.x" 2023-xx-xx
  OpenJDK Runtime Environment (build 21.0.x+x-LTS)
  OpenJDK 64-Bit Server VM (build 21.0.x+x-LTS, mixed mode)

✓ Parfait! Java 21 détecté
⏸ ▮▮▮▮▮▮░░░░░░░░░░░░░░░░░░░░


┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 3: Vérifier Maven (30 secondes)                           │
└─────────────────────────────────────────────────────────────────┘

C:\Users\USER\Documents\ivoka> mvn --version

Résultat attendu:
  Apache Maven 3.9.x (xxx)
  Maven home: C:\...\maven
  Java version: 21.0.x, vendor: xxx
  Java home: C:\...\jdk21

✓ Maven prêt
⏸ ▮▮▮▮▮▮▮▮░░░░░░░░░░░░░░░░░░


┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 4: Exécuter le Test (2-3 minutes)                         │
└─────────────────────────────────────────────────────────────────┘

C:\Users\USER\Documents\ivoka> .\TEST_LOCAL.ps1

╔════════════════════════════════════════════════════════════════════════╗
║      TEST LOCAL - IVOKA Backend API - Java 21 LTS                      ║
╚════════════════════════════════════════════════════════════════════════╝

====================================================================
PHASE 1: Vérification de l'Environnement
====================================================================

✓ [OK] Java trouvé
  openjdk version "21.0.x" ...

✓ [OK] Maven trouvé
  Apache Maven 3.9.x

✓ [OK] Répertoire du projet trouvé

⏸ ▮▮▮▮▮▮▮▮▮░░░░░░░░░░░░░░░░░░░


====================================================================
PHASE 2: Construction du Projet
====================================================================

[INFO] Nettoyage et compilation...
[INFO] Compilation réussie

[INFO] Construction du WAR...
[INFO] Building war: target/ivoka-api.war
[INFO] BUILD SUCCESS

✓ [OK] WAR construit avec succès
[INFO] Taille du WAR: 2.5 MB

⏸ ▮▮▮▮▮▮▮▮▮▮▮▮░░░░░░░░░░░░░░░


====================================================================
PHASE 3: Vérification des Imports Jakarta/Javax
====================================================================

✓ [OK] Aucun import javax trouvé
✓ [OK] Imports jakarta trouvés: 55+

⏸ ▮▮▮▮▮▮▮▮▮▮▮▮▮▮░░░░░░░░░░░░░░


====================================================================
PHASE 4: Vérification du Bytecode Java 21
====================================================================

✓ [OK] Fichier .class trouvé
✓ [OK] Bytecode Java 21 détecté (major version 65)

⏸ ▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮░░░░░░░░░░░░░


====================================================================
PHASE 5: Vérification des Dépendances
====================================================================

✓ [OK] Dépendance trouvée: jakarta.servlet
✓ [OK] Dépendance trouvée: jakarta.servlet.jsp
✓ [OK] Dépendance trouvée: jakarta.json
✓ [OK] Dépendance trouvée: mysql-connector-java
✓ [OK] Version Java 21 trouvée dans pom.xml

⏸ ▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮░░░░░░░░░░░░░


════════════════════════════════════════════════════════════════════════
PHASE 6: Rapport Final
════════════════════════════════════════════════════════════════════════

Tests réussis: 12
Tests échoués: 0

╔════════════════════════════════════════════════════════════════════════╗
║                       PROCHAINES ETAPES                                ║
╚════════════════════════════════════════════════════════════════════════╝

1. Installer Tomcat 10.1+ (s'il n'est pas installé)
   Télécharger: https://tomcat.apache.org/download-10.cgi

2. Déployer le WAR:
   - Copier: target\ivoka-api.war vers TOMCAT_HOME\webapps\

3. Démarrer Tomcat et tester:
   - Accéder à: http://localhost:8080/ivoka-api/

4. Tester les endpoints:
   - Produits: http://localhost:8080/ivoka-api/api/products

╔════════════════════════════════════════════════════════════════════════╗
║                   FICHIER WAR PRET POUR DEPLOIEMENT                     ║
║                                                                         ║
║                  target\ivoka-api.war                                  ║
║                                                                         ║
║                 Taille: 2.5 MB                                         ║
╚════════════════════════════════════════════════════════════════════════╝

Détails des tests: test_results.log

TESTS REUSSIS! ✓

⏸ ▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮░░░░░░░░░░ (PRESQUE)


┌─────────────────────────────────────────────────────────────────┐
│ RÉSULTAT: ✅ SUCCÈS!                                            │
└─────────────────────────────────────────────────────────────────┘

✓ Application compilée avec Java 21
✓ WAR créé (2.5 MB)
✓ Aucun import javax (ancien)
✓ Tous les imports jakarta
✓ Bytecode version 65 (Java 21)
✓ Toutes les dépendances présentes
✓ Prêt pour déploiement!

Total du test: 2 min 45 sec

Rapport détaillé: test_results.log
```

---

## Scénario Alternatif: Si une Erreur Survient

```
┌─────────────────────────────────────────────────────────────────┐
│ ERREUR POSSIBLE 1: Java non trouvé                              │
└─────────────────────────────────────────────────────────────────┘

✗ [ERREUR] Java non trouvé ou erreur lors de l'exécution

SOLUTION:
1. Installer Java 21: https://adoptium.net/
2. Ajouter au PATH
3. Redémarrer la console
4. Relancer le test


┌─────────────────────────────────────────────────────────────────┐
│ ERREUR POSSIBLE 2: Maven non trouvé                             │
└─────────────────────────────────────────────────────────────────┘

✗ [ERREUR] Maven non trouvé ou erreur lors de l'exécution

SOLUTION:
1. Installer Maven: https://maven.apache.org/
2. Ajouter au PATH
3. Redémarrer la console
4. Relancer le test


┌─────────────────────────────────────────────────────────────────┐
│ ERREUR POSSIBLE 3: La compilation a échoué                      │
└─────────────────────────────────────────────────────────────────┘

✗ [ERREUR] La compilation a échoué

Causes possibles:
- Imports javax au lieu de jakarta
- Dépendances manquantes
- Erreurs de syntaxe Java

SOLUTION:
1. Consulter test_results.log
2. Chercher "ERROR" dans le log
3. Vérifier que tous les imports sont jakarta.*
4. Exécuter: mvn clean compile
5. Relancer le test
```

---

## Après le Test: Déployer sur Tomcat

```
┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 5: Installer Tomcat 10.1+ (5-10 minutes)                 │
└─────────────────────────────────────────────────────────────────┘

Windows Command Prompt:

C:> cd C:\
C:> tar -xf apache-tomcat-10.1.x.zip
C:> set CATALINA_HOME=C:\apache-tomcat-10.1.x
C:> set JAVA_HOME=C:\Program Files\jdk-21

✓ Tomcat extrait


┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 6: Copier le WAR (30 secondes)                            │
└─────────────────────────────────────────────────────────────────┘

PowerShell:

PS> copy c:\Users\USER\Documents\ivoka\backend\target\ivoka-api.war `
    C:\apache-tomcat-10.1.x\webapps\

✓ WAR copié vers webapps


┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 7: Démarrer Tomcat (1-2 secondes)                         │
└─────────────────────────────────────────────────────────────────┘

PowerShell (Admin):

PS> C:\apache-tomcat-10.1.x\bin\startup.bat

Résultat attendu:
  Using CATALINA_BASE:   C:\apache-tomcat-10.1.x
  Using CATALINA_HOME:   C:\apache-tomcat-10.1.x
  Using CATALINA_TMPDIR: C:\apache-tomcat-10.1.x\temp
  Using JRE_HOME:        C:\Program Files\jdk-21

✓ Tomcat démarre


┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 8: Attendre le déploiement (5-10 secondes)                │
└─────────────────────────────────────────────────────────────────┘

Vérifier les logs:

PS> Get-Content C:\apache-tomcat-10.1.x\logs\catalina.out -Wait

Signes de succès:
  ✓ "Application startup completed..."
  ✓ "SEVERE" absent
  ✓ Aucune exception

⏸ ▮▮▮▮▮▮░░░░░░░░░░░░░░░░░░░░


┌─────────────────────────────────────────────────────────────────┐
│ ÉTAPE 9: Tester l'Application (1 minute)                        │
└─────────────────────────────────────────────────────────────────┘

Ouvrir navigateur:

URL: http://localhost:8080/ivoka-api/

Résultat attendu:
  - Page d'accueil OU 404 (normal)
  - Pas d'erreur 500
  - Pas d'exception


Tester une API:

URL: http://localhost:8080/ivoka-api/api/products

Résultat attendu:
  [
    {
      "id": 1,
      "name": "Produit 1",
      "price": 99.99,
      ...
    }
  ]

✓ Application opérationnelle!


┌─────────────────────────────────────────────────────────────────┐
│ RÉSULTAT: ✅ DÉPLOIEMENT RÉUSSI!                               │
└─────────────────────────────────────────────────────────────────┘

✓ Application sur Tomcat
✓ Port 8080 fonctionnel
✓ API répondant
✓ Database connectée (si configurée)
✓ Prêt pour tests de production

Accès: http://localhost:8080/ivoka-api/
Logs: C:\apache-tomcat-10.1.x\logs\catalina.out
```

---

## Progression du Processus Complet

```
START
  │
  ├─ ÉTAPE 1: Préparation ───────── ✓ (30 sec)
  │
  ├─ ÉTAPE 2: Vérifier Java 21 ──── ✓ (30 sec)
  │
  ├─ ÉTAPE 3: Vérifier Maven ────── ✓ (30 sec)
  │
  ├─ ÉTAPE 4: Test Automatisé ───── ✓ (2-3 min)
  │     ├─ Phase 1: Environnement
  │     ├─ Phase 2: Compilation
  │     ├─ Phase 3: Imports
  │     ├─ Phase 4: Bytecode
  │     ├─ Phase 5: Dépendances
  │     └─ Phase 6: Rapport
  │
  └─ RÉSULTAT: ✅ SUCCÈS! (Total: ~4 minutes)
       │
       └─ Prêt pour déploiement Tomcat
          ├─ Installer Tomcat 10.1+
          ├─ Copier WAR
          ├─ Démarrer Tomcat
          └─ Tester endpoints

DURÉE TOTALE (jusqu'au déploiement): ~15-20 minutes
```

---

## Checklist Visuelle

```
✓ Java 21 installé                           [████████████████████] 100%
✓ Maven installé                             [████████████████████] 100%
✓ Projet compilé                             [████████████████████] 100%
✓ WAR créé                                   [████████████████████] 100%
✓ Imports Jakarta                            [████████████████████] 100%
✓ Tests réussis                              [████████████████████] 100%
✓ Prêt pour déploiement                      [████████████████████] 100%
```

---

## Résumé des Commandes Principales

```bash
# Vérification
java -version
mvn --version

# Test
.\TEST_LOCAL.ps1

# Build
cd backend
mvn clean package -DskipTests

# Deploy
copy target\ivoka-api.war %CATALINA_HOME%\webapps\
%CATALINA_HOME%\bin\startup.bat

# Test
http://localhost:8080/ivoka-api/api/products
```

---

## Emojis de Statut

| Emoji | Signification |
|-------|--------------|
| ✓ | Succès / OK |
| ✗ | Erreur / Problème |
| ⏸ | Progression |
| ⏱ | En cours d'exécution |
| ⚠️ | Avertissement |
| 💡 | Conseil / Astuce |
| 📝 | Information |
| 🚀 | Action importante |

---

**Êtes-vous prêt?** 🚀

```powershell
cd c:\Users\USER\Documents\ivoka
.\TEST_LOCAL.ps1
```

Bonne chance! 🍀
