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
    Write-Host ""
    Write-Host ("=" * 70) -ForegroundColor $Blue
    Write-Host "  $Title" -ForegroundColor $Blue
    Write-Host ("=" * 70) -ForegroundColor $Blue
    Write-Host ""
}

function Write-TestPassed {
    param([string]$Message)
    Write-Host "OK: $Message" -ForegroundColor $Green
    $global:TestsPassed++
}

function Write-TestFailed {
    param([string]$Message)
    Write-Host "ERREUR: $Message" -ForegroundColor $Red
    $global:TestsFailed++
}

function Write-TestInfo {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Blue
}

function Write-TestWarning {
    param([string]$Message)
    Write-Host "[AVERTISSEMENT] $Message" -ForegroundColor $Yellow
}

# Debut du script
Clear-Host
Write-Host "========================================================================"
Write-Host "      TEST LOCAL - IVOKA Backend API - Java 21 LTS"
Write-Host "========================================================================"
Write-Host ""

# Log header
Add-Content -Path $TestLog -Value "========================================================================"
Add-Content -Path $TestLog -Value "TEST LOG - $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
Add-Content -Path $TestLog -Value "========================================================================"
Add-Content -Path $TestLog -Value ""

# ====================================================================
# PHASE 1: VERIFICATION DE L'ENVIRONNEMENT
# ====================================================================
Write-TestHeader "PHASE 1: Verification de l'Environnement"

# Verifier Java
Write-TestInfo "Verification de Java 21..."
try {
    $JavaVersion = java -version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-TestPassed "Java trouve"
        Write-Host "$JavaVersion" -ForegroundColor $Green
        Add-Content -Path $TestLog -Value "Java version: $JavaVersion"
    }
    else {
        Write-TestFailed "Java non trouve ou erreur lors de l'execution"
        Add-Content -Path $TestLog -Value "ERREUR: Java non accessible"
        exit 1
    }
}
catch {
    Write-TestFailed "Erreur lors de la verification de Java: $_"
    exit 1
}

# Verifier Maven
Write-TestInfo "Verification de Maven..."
try {
    $MavenVersion = mvn --version 2>&1 | Select-Object -First 1
    if ($LASTEXITCODE -eq 0) {
        Write-TestPassed "Maven trouve: $MavenVersion"
        Add-Content -Path $TestLog -Value "Maven OK"
    }
    else {
        Write-TestFailed "Maven non trouve ou erreur lors de l'execution"
        exit 1
    }
}
catch {
    Write-TestFailed "Erreur lors de la verification de Maven: $_"
    exit 1
}

# Verifier le repertoire du projet
Write-TestInfo "Verification du repertoire du projet..."
if (Test-Path $ProjectPath) {
    Write-TestPassed "Repertoire du projet trouve: $ProjectPath"
    Add-Content -Path $TestLog -Value "Projet path: $ProjectPath"
}
else {
    Write-TestFailed "Repertoire du projet non trouve: $ProjectPath"
    exit 1
}

# Changer de repertoire
Set-Location -Path $ProjectPath
Write-TestInfo "Repertoire courant: $(Get-Location)"

# ====================================================================
# PHASE 2: CONSTRUCTION DU PROJET
# ====================================================================
Write-TestHeader "PHASE 2: Construction du Projet"

# Compilation
Write-TestInfo "Nettoyage et compilation..."
Add-Content -Path $TestLog -Value "--- Compilation ---"

$CompileOutput = mvn clean compile -q 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-TestPassed "Compilation reussie"
    Add-Content -Path $TestLog -Value "Compilation: OK"
}
else {
    Write-TestFailed "La compilation a echoue"
    Write-Host $CompileOutput -ForegroundColor $Red
    Add-Content -Path $TestLog -Value "Compilation ECHOUEE:`n$CompileOutput"
    exit 1
}

# Construction du WAR
Write-TestInfo "Construction du WAR..."
Add-Content -Path $TestLog -Value "--- Construction du WAR ---"

$WarOutput = mvn clean package -DskipTests -q 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-TestPassed "WAR construit avec succes"
    Add-Content -Path $TestLog -Value "Packaging: OK"
}
else {
    Write-TestFailed "La construction du WAR a echoue"
    Write-Host $WarOutput -ForegroundColor $Red
    Add-Content -Path $TestLog -Value "Packaging ECHOUE:`n$WarOutput"
    exit 1
}

# Verifier que le WAR existe
$WarFile = "target\ivoka-api.war"
if (Test-Path $WarFile) {
    $WarSize = (Get-Item $WarFile).Length
    Write-TestPassed "Fichier WAR trouve: $WarFile"
    Write-TestInfo "Taille du WAR: $([Math]::Round($WarSize / 1MB, 2)) MB"
    Add-Content -Path $TestLog -Value "WAR cree: $WarSize bytes"
}
else {
    Write-TestFailed "Fichier WAR non trouve: $WarFile"
    exit 1
}

# ====================================================================
# PHASE 3: VERIFICATION DES IMPORTS
# ====================================================================
Write-TestHeader "PHASE 3: Verification des Imports Jakarta/Javax"

Add-Content -Path $TestLog -Value "--- Verification des imports ---"

# Chercher les imports javax
Write-TestInfo "Recherche d'imports javax (ancien, deprecie)..."
$JavaxImports = Get-ChildItem -Path "src\main\java\com\ivoka\api\" -Include "*.java" -Recurse |
    Select-String -Pattern "import javax\." | Measure-Object

if ($JavaxImports.Count -eq 0) {
    Write-TestPassed "Aucun import javax trouve (bon!)"
    Add-Content -Path $TestLog -Value "Imports javax: AUCUN (OK)"
}
else {
    Write-TestWarning "Attention: $($JavaxImports.Count) imports javax trouves"
    Get-ChildItem -Path "src\main\java\com\ivoka\api\" -Include "*.java" -Recurse |
        Select-String -Pattern "import javax\." |
        ForEach-Object { Write-Host "  $_" -ForegroundColor $Yellow }
    Add-Content -Path $TestLog -Value "Imports javax trouves: $($JavaxImports.Count)"
}

# Verifier les imports jakarta
Write-TestInfo "Verification des imports jakarta..."
$JakartaImports = Get-ChildItem -Path "src\main\java\com\ivoka\api\servlets" -Include "*.java" -Recurse |
    Select-String -Pattern "import jakarta\." | Measure-Object

if ($JakartaImports.Count -gt 0) {
    Write-TestPassed "Imports jakarta trouves: $($JakartaImports.Count)"
    Add-Content -Path $TestLog -Value "Imports jakarta: $($JakartaImports.Count) (OK)"
}
else {
    Write-TestWarning "Aucun import jakarta trouve (verifier manuellement)"
    Add-Content -Path $TestLog -Value "Imports jakarta: 0"
}

# ====================================================================
# PHASE 4: VERIFICATION DU BYTECODE
# ====================================================================
Write-TestHeader "PHASE 4: Verification du Bytecode Java 21"

Add-Content -Path $TestLog -Value "--- Verification bytecode ---"

$ClassFile = "target\classes\com\ivoka\api\servlets\ProductsServlet.class"
if (Test-Path $ClassFile) {
    Write-TestPassed "Fichier .class trouve"
    
    try {
        $BytecodeInfo = javap -verbose $ClassFile 2>&1 | Select-String "major version"
        if ($BytecodeInfo) {
            Write-TestInfo "Version bytecode: $BytecodeInfo"
            # Java 21 = major version 65
            if ($BytecodeInfo -match "65") {
                Write-TestPassed "Bytecode Java 21 detecte (major version 65)"
                Add-Content -Path $TestLog -Value "Bytecode: Java 21 (OK)"
            }
            else {
                Write-TestWarning "Version bytecode attendue 65, trouvee: $BytecodeInfo"
                Add-Content -Path $TestLog -Value "Bytecode: $BytecodeInfo"
            }
        }
    }
    catch {
        Write-TestWarning "Impossible de verifier la version bytecode: $_"
    }
}
else {
    Write-TestWarning "Fichier .class non trouve: $ClassFile"
}

# ====================================================================
# PHASE 5: VERIFICATION DES DEPENDANCES
# ====================================================================
Write-TestHeader "PHASE 5: Verification des Dependances"

Add-Content -Path $TestLog -Value "--- Verification dependances ---"

Write-TestInfo "Analyse des dependances pom.xml..."

$Pom = Get-Content "pom.xml" -Raw

$Dependencies = @{
    "jakarta.servlet" = "6.0.0"
    "jakarta.servlet.jsp" = "3.1.1"
    "jakarta.json" = "2.1.1"
    "mysql-connector-java" = "8.2"
}

foreach ($Dep in $Dependencies.GetEnumerator()) {
    if ($Pom -match $Dep.Key) {
        Write-TestPassed "Dependance trouvee: $($Dep.Key)"
        Add-Content -Path $TestLog -Value "Dependance OK: $($Dep.Key)"
    }
    else {
        Write-TestWarning "Dependance manquante ou introuvable: $($Dep.Key)"
        Add-Content -Path $TestLog -Value "Dependance MANQUANTE: $($Dep.Key)"
    }
}

# Verifier la version Java dans pom.xml
if ($Pom -match "<maven.compiler.source>21</maven.compiler.source>") {
    Write-TestPassed "Version Java 21 trouvee dans pom.xml"
    Add-Content -Path $TestLog -Value "Maven source: Java 21 (OK)"
}
else {
    Write-TestWarning "Version Java 21 non trouvee dans pom.xml"
    Add-Content -Path $TestLog -Value "Maven source: VERIFIE MANUELLEMENT"
}

# ====================================================================
# PHASE 6: RAPPORT FINAL
# ====================================================================
Write-TestHeader "PHASE 6: Rapport Final"

Add-Content -Path $TestLog -Value ""
Add-Content -Path $TestLog -Value "========================================================================"
Add-Content -Path $TestLog -Value "RAPPORT FINAL"
Add-Content -Path $TestLog -Value "========================================================================"
Add-Content -Path $TestLog -Value "Tests reussis: $TestsPassed"
Add-Content -Path $TestLog -Value "Tests echoues: $TestsFailed"
Add-Content -Path $TestLog -Value "Date: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"

Write-Host "========================================================================"
Write-Host "                          RAPPORT DE TEST"
Write-Host "========================================================================"
Write-Host ""
Write-Host "Tests reussis: " -NoNewline
Write-Host $TestsPassed -ForegroundColor $Green
Write-Host "Tests echoues: " -NoNewline
Write-Host $TestsFailed -ForegroundColor $Red
Write-Host ""

Write-Host "========================================================================"
Write-Host "                       PROCHAINES ETAPES"
Write-Host "========================================================================"
Write-Host ""
Write-Host "1. Installer Tomcat 10.1+ (s'il n'est pas installe)" -ForegroundColor $Blue
Write-Host "   Telecharger: https://tomcat.apache.org/download-10.cgi"
Write-Host ""
Write-Host "2. Deployer le WAR:" -ForegroundColor $Blue
Write-Host "   - Copier: target\ivoka-api.war vers TOMCAT_HOME\webapps\"
Write-Host "   - Ou utiliser le gestionnaire Tomcat"
Write-Host ""
Write-Host "3. Demarrer Tomcat et tester:" -ForegroundColor $Blue
Write-Host "   - Acceder a: http://localhost:8080/ivoka-api/"
Write-Host "   - Consulter les logs: TOMCAT_HOME\logs\catalina.out"
Write-Host ""
Write-Host "4. Tester les endpoints:" -ForegroundColor $Blue
Write-Host "   - Produits: http://localhost:8080/ivoka-api/api/products"
Write-Host "   - Auth: POST http://localhost:8080/ivoka-api/api/auth"
Write-Host "   - Panier: POST http://localhost:8080/ivoka-api/api/cart"
Write-Host ""

Write-Host "========================================================================"
Write-Host "                   FICHIER WAR PRET POUR DEPLOIEMENT"
Write-Host "========================================================================"
Write-Host ""
Write-Host "Fichier: target\ivoka-api.war" -ForegroundColor $Green
Write-Host "Taille: $([Math]::Round($WarSize / 1MB, 2)) MB" -ForegroundColor $Green
Write-Host ""

Write-Host "Resultats du test: $TestLog" -ForegroundColor $Blue
Write-Host ""

Add-Content -Path $TestLog -Value ""
Add-Content -Path $TestLog -Value "========================================================================"
Add-Content -Path $TestLog -Value "FIN DU TEST"
Add-Content -Path $TestLog -Value "========================================================================"

if ($TestsFailed -eq 0) {
    Write-Host "TESTS REUSSIS!" -ForegroundColor $Green
    exit 0
}
else {
    Write-Host "TESTS ECHOUES!" -ForegroundColor $Red
    exit 1
}
