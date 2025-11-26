# ====================================================================
# SCRIPT DE TEST LOCAL - IVOKA APPLICATION JAVA 21
# ====================================================================
# Fichier: TEST_LOCAL.ps1
# Description: Tests automatiques pour l'application IVOKA Java 21
# Usage: .\TEST_LOCAL.ps1
# ====================================================================

param(
    [switch]$SkipTests = $false,
    [switch]$Verbose = $false
)

# Configuration
$ProjectPath = "c:\Users\USER\Documents\ivoka\backend"
$TestLog = "test_results.log"
$TestsPassed = 0
$TestsFailed = 0

# Couleurs
$Green = "Green"
$Red = "Red"
$Yellow = "Yellow"
$Blue = "Cyan"

# Fonctions
function Write-TestHeader {
    param([string]$Title)
    Write-Host "`n" -NoNewline
    Write-Host ("=" * 70) -ForegroundColor $Blue
    Write-Host "  $Title" -ForegroundColor $Blue
    Write-Host ("=" * 70) -ForegroundColor $Blue
    Write-Host "`n"
}

function Write-TestPassed {
    param([string]$Message)
    Write-Host "✓ [OK] " -ForegroundColor $Green -NoNewline
    Write-Host "$Message" -ForegroundColor $Green
    $global:TestsPassed++
}

function Write-TestFailed {
    param([string]$Message)
    Write-Host "✗ [ERREUR] " -ForegroundColor $Red -NoNewline
    Write-Host "$Message" -ForegroundColor $Red
    $global:TestsFailed++
}

function Write-TestInfo {
    param([string]$Message)
    Write-Host "[INFO] " -ForegroundColor $Blue -NoNewline
    Write-Host "$Message"
}

function Write-TestWarning {
    param([string]$Message)
    Write-Host "[AVERTISSEMENT] " -ForegroundColor $Yellow -NoNewline
    Write-Host "$Message"
}

# Début du script
Clear-Host
Write-Host "╔════════════════════════════════════════════════════════════════════════╗"
Write-Host "║      TEST LOCAL - IVOKA Backend API - Java 21 LTS                      ║"
Write-Host "╚════════════════════════════════════════════════════════════════════════╝"
Write-Host ""

# Log header
Add-Content -Path $TestLog -Value "======================================================================"
Add-Content -Path $TestLog -Value "TEST LOG - $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
Add-Content -Path $TestLog -Value "======================================================================"
Add-Content -Path $TestLog -Value ""

# ====================================================================
# PHASE 1: VERIFICATION DE L'ENVIRONNEMENT
# ====================================================================
Write-TestHeader "PHASE 1: Vérification de l'Environnement"

# Vérifier Java
Write-TestInfo "Vérification de Java 21..."
try {
    $JavaVersion = java -version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-TestPassed "Java trouvé"
        Write-Host "$JavaVersion" -ForegroundColor $Green
        Add-Content -Path $TestLog -Value "Java version: $JavaVersion"
    }
    else {
        Write-TestFailed "Java non trouvé ou erreur lors de l'exécution"
        Add-Content -Path $TestLog -Value "ERREUR: Java non accessible"
        exit 1
    }
}
catch {
    Write-TestFailed "Erreur lors de la vérification de Java: $_"
    exit 1
}

# Vérifier Maven
Write-TestInfo "Vérification de Maven..."
try {
    $MavenVersion = mvn --version 2>&1 | Select-Object -First 1
    if ($LASTEXITCODE -eq 0) {
        Write-TestPassed "Maven trouvé: $MavenVersion"
        Add-Content -Path $TestLog -Value "Maven OK"
    }
    else {
        Write-TestFailed "Maven non trouvé ou erreur lors de l'exécution"
        exit 1
    }
}
catch {
    Write-TestFailed "Erreur lors de la vérification de Maven: $_"
    exit 1
}

# Vérifier le répertoire du projet
Write-TestInfo "Vérification du répertoire du projet..."
if (Test-Path $ProjectPath) {
    Write-TestPassed "Répertoire du projet trouvé: $ProjectPath"
    Add-Content -Path $TestLog -Value "Projet path: $ProjectPath"
}
else {
    Write-TestFailed "Répertoire du projet non trouvé: $ProjectPath"
    exit 1
}

# Changer de répertoire
Set-Location -Path $ProjectPath
Write-TestInfo "Répertoire courant: $(Get-Location)"

# ====================================================================
# PHASE 2: CONSTRUCTION DU PROJET
# ====================================================================
Write-TestHeader "PHASE 2: Construction du Projet"

# Compilation
Write-TestInfo "Nettoyage et compilation..."
Add-Content -Path $TestLog -Value "--- Compilation ---"

$CompileOutput = mvn clean compile -q 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-TestPassed "Compilation réussie"
    Add-Content -Path $TestLog -Value "Compilation: OK"
}
else {
    Write-TestFailed "La compilation a échoué"
    Write-Host $CompileOutput -ForegroundColor $Red
    Add-Content -Path $TestLog -Value "Compilation ECHOUEE:`n$CompileOutput"
    exit 1
}

# Construction du WAR
Write-TestInfo "Construction du WAR..."
Add-Content -Path $TestLog -Value "--- Construction du WAR ---"

$WarOutput = mvn clean package -DskipTests -q 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-TestPassed "WAR construit avec succès"
    Add-Content -Path $TestLog -Value "Packaging: OK"
}
else {
    Write-TestFailed "La construction du WAR a échoué"
    Write-Host $WarOutput -ForegroundColor $Red
    Add-Content -Path $TestLog -Value "Packaging ECHOUE:`n$WarOutput"
    exit 1
}

# Vérifier que le WAR existe
$WarFile = "target\ivoka-api.war"
if (Test-Path $WarFile) {
    $WarSize = (Get-Item $WarFile).Length
    Write-TestPassed "Fichier WAR trouvé: $WarFile"
    Write-TestInfo "Taille du WAR: $($WarSize / 1MB) MB"
    Add-Content -Path $TestLog -Value "WAR créé: $WarSize bytes"
}
else {
    Write-TestFailed "Fichier WAR non trouvé: $WarFile"
    exit 1
}

# ====================================================================
# PHASE 3: VERIFICATION DES IMPORTS
# ====================================================================
Write-TestHeader "PHASE 3: Vérification des Imports Jakarta/Javax"

Add-Content -Path $TestLog -Value "--- Vérification des imports ---"

# Chercher les imports javax
Write-TestInfo "Recherche d'imports javax (ancien, déprécié)..."
$JavaxImports = Get-ChildItem -Path "src\main\java\com\ivoka\api\" -Include "*.java" -Recurse |
    Select-String -Pattern "import javax\." | Measure-Object

if ($JavaxImports.Count -eq 0) {
    Write-TestPassed "Aucun import javax trouvé (bon!)"
    Add-Content -Path $TestLog -Value "Imports javax: AUCUN (OK)"
}
else {
    Write-TestWarning "Attention: $($JavaxImports.Count) imports javax trouvés"
    Get-ChildItem -Path "src\main\java\com\ivoka\api\" -Include "*.java" -Recurse |
        Select-String -Pattern "import javax\." |
        ForEach-Object { Write-Host "  $_" -ForegroundColor $Yellow }
    Add-Content -Path $TestLog -Value "Imports javax trouvés: $($JavaxImports.Count)"
}

# Vérifier les imports jakarta
Write-TestInfo "Vérification des imports jakarta..."
$JakartaImports = Get-ChildItem -Path "src\main\java\com\ivoka\api\servlets" -Include "*.java" -Recurse |
    Select-String -Pattern "import jakarta\." | Measure-Object

if ($JakartaImports.Count -gt 0) {
    Write-TestPassed "Imports jakarta trouvés: $($JakartaImports.Count)"
    Add-Content -Path $TestLog -Value "Imports jakarta: $($JakartaImports.Count) (OK)"
}
else {
    Write-TestWarning "Aucun import jakarta trouvé (vérifier manuellement)"
    Add-Content -Path $TestLog -Value "Imports jakarta: 0"
}

# ====================================================================
# PHASE 4: VERIFICATION DU BYTECODE
# ====================================================================
Write-TestHeader "PHASE 4: Vérification du Bytecode Java 21"

Add-Content -Path $TestLog -Value "--- Vérification bytecode ---"

$ClassFile = "target\classes\com\ivoka\api\servlets\ProductsServlet.class"
if (Test-Path $ClassFile) {
    Write-TestPassed "Fichier .class trouvé"
    
    try {
        $BytecodeInfo = javap -verbose $ClassFile 2>&1 | Select-String "major version"
        if ($BytecodeInfo) {
            Write-TestInfo "Version bytecode: $BytecodeInfo"
            # Java 21 = major version 65
            if ($BytecodeInfo -match "65") {
                Write-TestPassed "Bytecode Java 21 détecté (major version 65)"
                Add-Content -Path $TestLog -Value "Bytecode: Java 21 (OK)"
            }
            else {
                Write-TestWarning "Version bytecode attendue 65, trouvée: $BytecodeInfo"
                Add-Content -Path $TestLog -Value "Bytecode: $BytecodeInfo"
            }
        }
    }
    catch {
        Write-TestWarning "Impossible de vérifier la version bytecode: $_"
    }
}
else {
    Write-TestWarning "Fichier .class non trouvé: $ClassFile"
}

# ====================================================================
# PHASE 5: VERIFICATION DES DEPENDANCES
# ====================================================================
Write-TestHeader "PHASE 5: Vérification des Dépendances"

Add-Content -Path $TestLog -Value "--- Vérification dépendances ---"

Write-TestInfo "Analyse des dépendances pom.xml..."

$Pom = Get-Content "pom.xml" -Raw

$Dependencies = @{
    "jakarta.servlet" = "6.0.0"
    "jakarta.servlet.jsp" = "3.1.1"
    "jakarta.json" = "2.1.1"
    "mysql-connector-java" = "8.2"
}

foreach ($Dep in $Dependencies.GetEnumerator()) {
    if ($Pom -match $Dep.Key) {
        Write-TestPassed "Dépendance trouvée: $($Dep.Key)"
        Add-Content -Path $TestLog -Value "Dépendance OK: $($Dep.Key)"
    }
    else {
        Write-TestWarning "Dépendance manquante ou introuvable: $($Dep.Key)"
        Add-Content -Path $TestLog -Value "Dépendance MANQUANTE: $($Dep.Key)"
    }
}

# Vérifier la version Java dans pom.xml
if ($Pom -match "<maven.compiler.source>21</maven.compiler.source>") {
    Write-TestPassed "Version Java 21 trouvée dans pom.xml"
    Add-Content -Path $TestLog -Value "Maven source: Java 21 (OK)"
}
else {
    Write-TestWarning "Version Java 21 non trouvée dans pom.xml"
    Add-Content -Path $TestLog -Value "Maven source: VERIFIE MANUELLEMENT"
}

# ====================================================================
# PHASE 6: RAPPORT FINAL
# ====================================================================
Write-TestHeader "PHASE 6: Rapport Final"

Add-Content -Path $TestLog -Value ""
Add-Content -Path $TestLog -Value "======================================================================"
Add-Content -Path $TestLog -Value "RAPPORT FINAL"
Add-Content -Path $TestLog -Value "======================================================================"
Add-Content -Path $TestLog -Value "Tests réussis: $TestsPassed"
Add-Content -Path $TestLog -Value "Tests échoués: $TestsFailed"
Add-Content -Path $TestLog -Value "Date: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"

Write-Host "╔════════════════════════════════════════════════════════════════════════╗"
Write-Host "║                          RAPPORT DE TEST                               ║"
Write-Host "╚════════════════════════════════════════════════════════════════════════╝"
Write-Host ""
Write-Host "Tests réussis: " -NoNewline
Write-Host $TestsPassed -ForegroundColor $Green
Write-Host "Tests échoués: " -NoNewline
Write-Host $TestsFailed -ForegroundColor $Red
Write-Host ""

Write-Host "╔════════════════════════════════════════════════════════════════════════╗"
Write-Host "║                       PROCHAINES ETAPES                                ║"
Write-Host "╚════════════════════════════════════════════════════════════════════════╝"
Write-Host ""
Write-Host "1. Installer Tomcat 10.1+ (s'il n'est pas installé)" -ForegroundColor $Blue
Write-Host "   Télécharger: https://tomcat.apache.org/download-10.cgi"
Write-Host ""
Write-Host "2. Déployer le WAR:" -ForegroundColor $Blue
Write-Host "   - Copier: target\ivoka-api.war vers TOMCAT_HOME\webapps\"
Write-Host "   - Ou utiliser le gestionnaire Tomcat"
Write-Host ""
Write-Host "3. Démarrer Tomcat et tester:" -ForegroundColor $Blue
Write-Host "   - Accéder à: http://localhost:8080/ivoka-api/"
Write-Host "   - Consulter les logs: TOMCAT_HOME\logs\catalina.out"
Write-Host ""
Write-Host "4. Tester les endpoints:" -ForegroundColor $Blue
Write-Host "   - Produits: http://localhost:8080/ivoka-api/api/products"
Write-Host "   - Auth: POST http://localhost:8080/ivoka-api/api/auth"
Write-Host "   - Panier: POST http://localhost:8080/ivoka-api/api/cart"
Write-Host ""

Write-Host "╔════════════════════════════════════════════════════════════════════════╗"
Write-Host "║                   FICHIER WAR PRET POUR DEPLOIEMENT                     ║"
Write-Host "║                                                                         ║"
Write-Host "║                  $WarFile" -ForegroundColor $Green
Write-Host "║                                                                         ║"
Write-Host "║                 Taille: $($WarSize / 1MB) MB                            ║"
Write-Host "╚════════════════════════════════════════════════════════════════════════╝"
Write-Host ""

Write-Host "Détails des tests: $TestLog" -ForegroundColor $Blue
Write-Host ""

Add-Content -Path $TestLog -Value ""
Add-Content -Path $TestLog -Value "======================================================================"
Add-Content -Path $TestLog -Value "FIN DU TEST"
Add-Content -Path $TestLog -Value "======================================================================"

if ($TestsFailed -eq 0) {
    Write-Host "TESTS REUSSIS! ✓" -ForegroundColor $Green
    exit 0
}
else {
    Write-Host "TESTS ECHOUES! ✗" -ForegroundColor $Red
    exit 1
}
