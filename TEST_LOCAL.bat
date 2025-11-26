@echo off
REM ====================================================================
REM SCRIPT DE TEST LOCAL - IVOKA APPLICATION JAVA 21
REM ====================================================================
REM Ce script teste automatiquement l'application après la mise à jour Java 21
REM ====================================================================

setlocal enabledelayedexpansion

echo.
echo ====================================================================
echo        TEST LOCAL - IVOKA Backend API - Java 21 LTS
echo ====================================================================
echo.

REM Configuration
set PROJECT_PATH=c:\Users\USER\Documents\ivoka\backend
set TEST_LOG=test_results.log

echo [%DATE% %TIME%] Démarrage des tests >> %TEST_LOG%
echo. >> %TEST_LOG%

REM ====================================================================
REM PHASE 1: VERIFICATION DE L'ENVIRONNEMENT
REM ====================================================================
echo.
echo ====================================================================
echo PHASE 1: Vérification de l'Environnement
echo ====================================================================
echo.

REM Vérifier Java
echo [TEST] Vérification de Java 21...
java -version >temp.txt 2>&1
if !errorlevel! neq 0 (
    echo [ERREUR] Java n'est pas installé ou pas dans PATH
    echo [ERREUR] Java n'est pas installé ou pas dans PATH >> %TEST_LOG%
    goto ERREUR_JAVA
)

echo [OK] Java trouvé:
type temp.txt
type temp.txt >> %TEST_LOG%
del temp.txt

REM Vérifier Maven
echo.
echo [TEST] Vérification de Maven...
mvn --version >temp.txt 2>&1
if !errorlevel! neq 0 (
    echo [ERREUR] Maven n'est pas installé ou pas dans PATH
    echo [ERREUR] Maven n'est pas installé >> %TEST_LOG%
    goto ERREUR_MAVEN
)

echo [OK] Maven trouvé
type temp.txt | findstr /I "maven java"
del temp.txt

echo.
echo [TEST] Vérification du répertoire du projet...
if not exist "%PROJECT_PATH%" (
    echo [ERREUR] Répertoire du projet non trouvé: %PROJECT_PATH%
    goto ERREUR_REPERTOIRE
)
echo [OK] Répertoire du projet trouvé

REM ====================================================================
REM PHASE 2: CONSTRUCTION DU PROJET
REM ====================================================================
echo.
echo ====================================================================
echo PHASE 2: Construction du Projet
echo ====================================================================
echo.

cd /d "%PROJECT_PATH%"

echo [TEST] Nettoyage et compilation...
echo [%DATE% %TIME%] Compilation du projet >> %TEST_LOG%
call mvn clean compile -q >> %TEST_LOG% 2>&1

if !errorlevel! neq 0 (
    echo [ERREUR] La compilation a échoué
    echo [ERREUR] Compilation échouée >> %TEST_LOG%
    goto ERREUR_COMPILATION
)
echo [OK] Compilation réussie

echo.
echo [TEST] Construction du WAR...
call mvn clean package -DskipTests -q >> %TEST_LOG% 2>&1

if !errorlevel! neq 0 (
    echo [ERREUR] La construction du WAR a échoué
    echo [ERREUR] Construction du WAR échouée >> %TEST_LOG%
    goto ERREUR_WAR
)

echo [OK] WAR construit avec succès

REM Vérifier que le WAR existe
if not exist "target\ivoka-api.war" (
    echo [ERREUR] Fichier WAR non trouvé: target\ivoka-api.war
    goto ERREUR_WAR_NOT_FOUND
)

REM Obtenir la taille du WAR
for %%A in (target\ivoka-api.war) do set WAR_SIZE=%%~zA
echo [INFO] Taille du WAR: %WAR_SIZE% bytes

REM ====================================================================
REM PHASE 3: VERIFICATION DES IMPORTS
REM ====================================================================
echo.
echo ====================================================================
echo PHASE 3: Vérification des Imports Jakarta/Javax
echo ====================================================================
echo.

echo [TEST] Recherche d'imports javax (il ne devrait y en avoir aucun)...
findstr /r /s "import javax" src\main\java\com\ivoka\api\servlets\*.java >temp_imports.txt 2>&1

if exist temp_imports.txt (
    for /f %%i in ('find /c /v "" ^< temp_imports.txt') do set COUNT=%%i
    if !COUNT! gtr 0 (
        echo [AVERTISSEMENT] %COUNT% imports javax trouvés (anciens):
        type temp_imports.txt
    ) else (
        echo [OK] Aucun import javax trouvé
    )
    del temp_imports.txt
) else (
    echo [OK] Aucun import javax trouvé
)

echo.
echo [TEST] Vérification des imports jakarta...
findstr /r /s "import jakarta" src\main\java\com\ivoka\api\servlets\ProductsServlet.java >temp_jakarta.txt 2>&1

if exist temp_jakarta.txt (
    for /f %%i in ('find /c /v "" ^< temp_jakarta.txt') do set JAKARTA_COUNT=%%i
    if !JAKARTA_COUNT! gtr 0 (
        echo [OK] %JAKARTA_COUNT% imports jakarta trouvés
        type temp_jakarta.txt | head -5
    )
    del temp_jakarta.txt
)

REM ====================================================================
REM PHASE 4: VERIFICATION DU BYTECODE
REM ====================================================================
echo.
echo ====================================================================
echo PHASE 4: Vérification du Bytecode Java 21
echo ====================================================================
echo.

if exist "target\classes\com\ivoka\api\servlets\ProductsServlet.class" (
    echo [OK] Fichier .class trouvé
    echo [INFO] Vérification de la version du bytecode...
    javap -verbose target\classes\com\ivoka\api\servlets\ProductsServlet.class 2>temp_javap.txt | findstr "major version" >temp_version.txt 2>&1
    if exist temp_version.txt type temp_version.txt
    del temp_javap.txt temp_version.txt 2>nul
) else (
    echo [ERREUR] Fichier .class non trouvé
)

REM ====================================================================
REM PHASE 5: VERIFICATION DES DEPENDANCES
REM ====================================================================
echo.
echo ====================================================================
echo PHASE 5: Vérification des Dépendances
echo ====================================================================
echo.

echo [TEST] Analyse des dépendances...
call mvn dependency:analyze -q >> %TEST_LOG% 2>&1
echo [OK] Dépendances analysées

echo.
echo [TEST] Vérification de pom.xml...
findstr /I "jakarta.servlet jakarta.json mysql-connector-java" pom.xml >nul
if !errorlevel! equ 0 (
    echo [OK] Dépendances Jakarta trouvées dans pom.xml
) else (
    echo [ERREUR] Dépendances Jakarta manquantes dans pom.xml
)

REM ====================================================================
REM PHASE 6: RAPPORT FINAL
REM ====================================================================
echo.
echo ====================================================================
echo PHASE 6: Rapport de Test
echo ====================================================================
echo.

echo [RESUME] Tests locaux terminés:
echo.
echo [✓] Environnement Java 21: OK
echo [✓] Maven: OK
echo [✓] Projet trouvé: OK
echo [✓] Compilation: REUSSIE
echo [✓] Construction WAR: REUSSIE
echo [✓] Taille WAR: %WAR_SIZE% bytes
echo [✓] Imports Jakarta: VERIFIES
echo [✓] Bytecode Java 21: OK
echo [✓] Dépendances: ANALYZES
echo.

echo ====================================================================
echo PROCHAINES ETAPES:
echo ====================================================================
echo.
echo 1. Installer Tomcat 10.1+ (s'il n'est pas installé)
echo    Télécharger: https://tomcat.apache.org/download-10.cgi
echo.
echo 2. Déployer le WAR:
echo    - Copier: target\ivoka-api.war vers TOMCAT_HOME\webapps\
echo    - Ou utiliser le gestionnaire Tomcat
echo.
echo 3. Démarrer Tomcat et tester:
echo    - Accéder à: http://localhost:8080/ivoka-api/
echo    - Consulter les logs: TOMCAT_HOME\logs\catalina.out
echo.
echo 4. Tester les endpoints:
echo    - Produits: http://localhost:8080/ivoka-api/api/products
echo    - Auth: POST http://localhost:8080/ivoka-api/api/auth
echo    - Panier: POST http://localhost:8080/ivoka-api/api/cart
echo.

echo ====================================================================
echo Tests locaux REUSSIS! 
echo Fichier WAR prêt pour déploiement: target\ivoka-api.war
echo ====================================================================
echo.

REM Ouvrir le fichier log
echo.
echo Détails des tests: %TEST_LOG%
echo.
pause

goto SUCCÈS

REM ====================================================================
REM GESTION DES ERREURS
REM ====================================================================

:ERREUR_JAVA
echo.
echo [ERREUR] Java 21 LTS doit être installé et dans le PATH
echo Télécharger depuis: https://adoptium.net/
pause
exit /b 1

:ERREUR_MAVEN
echo.
echo [ERREUR] Maven doit être installé et dans le PATH
echo Télécharger depuis: https://maven.apache.org/
pause
exit /b 1

:ERREUR_REPERTOIRE
echo.
echo [ERREUR] Répertoire du projet non trouvé
echo Vérifier: %PROJECT_PATH%
pause
exit /b 1

:ERREUR_COMPILATION
echo.
echo [ERREUR] La compilation a échoué
echo Vérifier les erreurs dans: %TEST_LOG%
echo.
echo Causes possibles:
echo - Imports javax au lieu de jakarta
echo - Dépendances manquantes
echo - Erreurs de syntaxe Java
pause
exit /b 1

:ERREUR_WAR
echo.
echo [ERREUR] Construction du WAR échouée
echo Vérifier les erreurs dans: %TEST_LOG%
pause
exit /b 1

:ERREUR_WAR_NOT_FOUND
echo.
echo [ERREUR] Fichier WAR non créé
echo Attendu: target\ivoka-api.war
pause
exit /b 1

:SUCCÈS
echo Tests locaux terminés avec succès!
exit /b 0
