# Configuration Azure pour IVOKA

## 🚀 Déploiement sur Azure App Service

### 1. Configuration de l'Environnement Azure

#### Ressources Azure nécessaires :
- **Azure App Service** (pour le backend Java)
- **Azure Database for MySQL** (base de données)
- **Azure Storage** (pour les images et fichiers statiques)
- **Azure CDN** (optionnel, pour la performance)

### 2. Configuration Backend (Java Servlets/JSP)

#### Fichier `application.properties` pour Azure :
```properties
# Configuration Azure MySQL
spring.datasource.url=jdbc:mysql://ivoka-mysql-server.mysql.database.azure.com:3306/ivoka_db?useSSL=true&serverTimezone=UTC
spring.datasource.username=ivoka_admin@ivoka-mysql-server
spring.datasource.password=${AZURE_MYSQL_PASSWORD}

# Configuration Tomcat
server.port=8080
server.servlet.context-path=/api

# Configuration JSP
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

#### Configuration web.xml pour Azure :
```xml
<context-param>
    <param-name>db.url</param-name>
    <param-value>jdbc:mysql://ivoka-mysql-server.mysql.database.azure.com:3306/ivoka_db?useSSL=true&amp;serverTimezone=UTC</param-value>
</context-param>

<context-param>
    <param-name>db.user</param-name>
    <param-value>ivoka_admin@ivoka-mysql-server</param-value>
</context-param>

<context-param>
    <param-name>db.password</param-name>
    <param-value>${AZURE_MYSQL_PASSWORD}</param-value>
</context-param>
```

### 3. Configuration Frontend (React.js)

#### Fichier `.env.production` :
```bash
REACT_APP_API_URL=https://ivoka-api.azurewebsites.net/api
REACT_APP_ENV=production
REACT_APP_AZURE_STORAGE_URL=https://ivokastorage.blob.core.windows.net
```

#### Build production :
```bash
cd frontend
npm run build
```

### 4. Pipeline CI/CD Azure DevOps

#### Fichier `azure-pipelines.yml` :
```yaml
trigger:
- main

pool:
  vmImage: 'ubuntu-latest'

variables:
  azureSubscription: 'Azure-Connection'
  appName: 'ivoka-api'
  resourceGroupName: 'ivoka-rg'

stages:
- stage: Build
  jobs:
  - job: BuildBackend
    steps:
    - task: Maven@3
      inputs:
        mavenPomFile: 'backend/pom.xml'
        goals: 'clean package'
        publishJUnitResults: false
        javaHomeOption: 'JDKVersion'
        jdkVersionOption: '1.11'
        mavenVersionOption: 'Default'
    
    - task: PublishBuildArtifacts@1
      inputs:
        PathtoPublish: 'backend/target'
        ArtifactName: 'backend-artifacts'
        publishLocation: 'Container'

  - job: BuildFrontend
    steps:
    - task: NodeTool@0
      inputs:
        versionSpec: '16.x'
      displayName: 'Install Node.js'

    - script: |
        cd frontend
        npm install
        npm run build
      displayName: 'Build React App'
    
    - task: PublishBuildArtifacts@1
      inputs:
        PathtoPublish: 'frontend/build'
        ArtifactName: 'frontend-artifacts'
        publishLocation: 'Container'

- stage: Deploy
  jobs:
  - job: DeployBackend
    steps:
    - task: AzureWebApp@1
      inputs:
        azureSubscription: $(azureSubscription)
        appType: 'webAppLinux'
        appName: $(appName)
        package: '$(Pipeline.Workspace)/backend-artifacts/*.war'
        runtimeStack: 'JAVA|11-java11'

  - job: DeployFrontend
    steps:
    - task: AzureFileCopy@4
      inputs:
        SourcePath: '$(Pipeline.Workspace)/frontend-artifacts/*'
        azureSubscription: $(azureSubscription)
        Destination: 'AzureBlob'
        storage: 'ivokastorage'
        ContainerName: '$web'
```

### 5. Configuration Infrastructure Azure (Terraform)

#### Fichier `main.tf` :
```hcl
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
}

provider "azurerm" {
  features {}
}

# Resource Group
resource "azurerm_resource_group" "ivoka" {
  name     = "ivoka-rg"
  location = "West Europe"
}

# MySQL Database
resource "azurerm_mysql_server" "ivoka" {
  name                = "ivoka-mysql-server"
  location            = azurerm_resource_group.ivoka.location
  resource_group_name = azurerm_resource_group.ivoka.name

  administrator_login          = "ivoka_admin"
  administrator_login_password = var.mysql_password

  sku_name   = "GP_Gen5_2"
  storage_mb = 5120
  version    = "8.0"

  auto_grow_enabled                 = true
  backup_retention_days             = 7
  geo_redundant_backup_enabled      = false
  infrastructure_encryption_enabled = false
  public_network_access_enabled     = true
  ssl_enforcement_enabled           = true
  ssl_minimal_tls_version_enforced  = "TLS1_2"
}

resource "azurerm_mysql_database" "ivoka" {
  name                = "ivoka_db"
  resource_group_name = azurerm_resource_group.ivoka.name
  server_name         = azurerm_mysql_server.ivoka.name
  charset             = "utf8"
  collation           = "utf8_unicode_ci"
}

# App Service Plan
resource "azurerm_app_service_plan" "ivoka" {
  name                = "ivoka-asp"
  location            = azurerm_resource_group.ivoka.location
  resource_group_name = azurerm_resource_group.ivoka.name
  kind                = "Linux"
  reserved            = true

  sku {
    tier = "Standard"
    size = "S1"
  }
}

# App Service
resource "azurerm_app_service" "ivoka_api" {
  name                = "ivoka-api"
  location            = azurerm_resource_group.ivoka.location
  resource_group_name = azurerm_resource_group.ivoka.name
  app_service_plan_id = azurerm_app_service_plan.ivoka.id

  site_config {
    java_version             = "11"
    java_container           = "TOMCAT"
    java_container_version   = "9.0"
    always_on                = true
    ftps_state               = "Disabled"
    http2_enabled            = true
    scm_type                 = "LocalGit"
    use_32_bit_worker_process = false

    cors {
      allowed_origins = ["*"]
      support_credentials = false
    }
  }

  app_settings = {
    "db.url"      = "jdbc:mysql://${azurerm_mysql_server.ivoka.fqdn}:3306/ivoka_db?useSSL=true&serverTimezone=UTC"
    "db.user"     = "ivoka_admin@${azurerm_mysql_server.ivoka.name}"
    "db.password" = var.mysql_password
    "db.driver"   = "com.mysql.cj.jdbc.Driver"
  }

  connection_string {
    name  = "MYSQLCONNSTR_str"
    type  = "MySql"
    value = "Database=ivoka_db;Data Source=${azurerm_mysql_server.ivoka.fqdn};User Id=ivoka_admin@${azurerm_mysql_server.ivoka.name};Password=${var.mysql_password}"
  }
}

# Storage Account
resource "azurerm_storage_account" "ivoka" {
  name                     = "ivokastorage"
  resource_group_name      = azurerm_resource_group.ivoka.name
  location                 = azurerm_resource_group.ivoka.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
  account_kind             = "StorageV2"
  static_website {
    index_document     = "index.html"
    error_404_document = "index.html"
  }
}

# CDN
resource "azurerm_cdn_profile" "ivoka" {
  name                = "ivoka-cdn"
  location            = "Global"
  resource_group_name = azurerm_resource_group.ivoka.name
  sku                 = "Standard_Microsoft"
}

resource "azurerm_cdn_endpoint" "ivoka" {
  name                = "ivoka-cdn-endpoint"
  profile_name        = azurerm_cdn_profile.ivoka.name
  location            = azurerm_resource_group.ivoka.location
  resource_group_name = azurerm_resource_group.ivoka.name

  origin {
    name      = "ivoka-origin"
    host_name = azurerm_storage_account.ivoka.primary_web_host
  }
}

variable "mysql_password" {
  description = "Password for MySQL administrator"
  type        = string
  sensitive   = true
}

output "app_url" {
  value = "https://${azurerm_app_service.ivoka_api.default_site_hostname}"
}

output "storage_url" {
  value = azurerm_storage_account.ivoka.primary_web_endpoint
}

output "cdn_url" {
  value = "https://${azurerm_cdn_endpoint.ivoka.host_name}"
}
```

### 6. Configuration pour le Déploiement

#### Script de déploiement `deploy.sh` :
```bash
#!/bin/bash

# Configuration
RESOURCE_GROUP="ivoka-rg"
LOCATION="westeurope"
APP_NAME="ivoka-api"
MYSQL_SERVER="ivoka-mysql-server"
MYSQL_DB="ivoka_db"
MYSQL_USER="ivoka_admin"
STORAGE_ACCOUNT="ivokastorage"

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Déploiement IVOKA sur Azure${NC}"
echo "================================="

# 1. Créer le groupe de ressources
echo -e "${GREEN}📦 Création du groupe de ressources...${NC}"
az group create --name $RESOURCE_GROUP --location $LOCATION

# 2. Créer la base de données MySQL
echo -e "${GREEN}🗄️ Configuration de la base de données MySQL...${NC}"
az mysql server create \
    --name $MYSQL_SERVER \
    --resource-group $RESOURCE_GROUP \
    --location $LOCATION \
    --admin-user $MYSQL_USER \
    --admin-password $AZURE_MYSQL_PASSWORD \
    --sku-name GP_Gen5_2 \
    --version 8.0 \
    --storage-size 5120 \
    --ssl-enforcement Enabled

# 3. Créer la base de données
echo -e "${GREEN}📋 Création de la base de données...${NC}"
az mysql db create \
    --name $MYSQL_DB \
    --resource-group $RESOURCE_GROUP \
    --server-name $MYSQL_SERVER

# 4. Créer le compte de stockage
echo -e "${GREEN}💾 Configuration du stockage...${NC}"
az storage account create \
    --name $STORAGE_ACCOUNT \
    --resource-group $RESOURCE_GROUP \
    --location $LOCATION \
    --sku Standard_LRS \
    --kind StorageV2

# 5. Activer le site web statique
echo -e "${GREEN}🌐 Configuration du site web statique...${NC}"
az storage blob service-properties update \
    --account-name $STORAGE_ACCOUNT \
    --static-website \
    --index-document index.html \
    --404-document index.html

# 6. Créer le plan App Service
echo -e "${GREEN}🖥️ Configuration du plan App Service...${NC}"
az appservice plan create \
    --name ivoka-asp \
    --resource-group $RESOURCE_GROUP \
    --sku S1 \
    --is-linux \
    --location $LOCATION

# 7. Créer l'App Service
echo -e "${GREEN}🚀 Création de l'App Service...${NC}"
az webapp create \
    --name $APP_NAME \
    --resource-group $RESOURCE_GROUP \
    --plan ivoka-asp \
    --runtime "JAVA:11-java11"

# 8. Configurer les variables d'environnement
echo -e "${GREEN}⚙️ Configuration des variables d'environnement...${NC}"
az webapp config appsettings set \
    --name $APP_NAME \
    --resource-group $RESOURCE_GROUP \
    --settings \
        db.url="jdbc:mysql://$MYSQL_SERVER.mysql.database.azure.com:3306/$MYSQL_DB?useSSL=true&serverTimezone=UTC" \
        db.user="$MYSQL_USER@$MYSQL_SERVER" \
        db.password="$AZURE_MYSQL_PASSWORD" \
        db.driver="com.mysql.cj.jdbc.Driver"

# 9. Déployer le backend
echo -e "${GREEN}📦 Déploiement du backend...${NC}"
cd backend
mvn clean package -DskipTests
az webapp deploy \
    --name $APP_NAME \
    --resource-group $RESOURCE_GROUP \
    --src-path target/ivoka-api.war

# 10. Déployer le frontend
echo -e "${GREEN}🎨 Déploiement du frontend...${NC}"
cd ../frontend
npm install
npm run build

# Upload des fichiers statiques
az storage blob upload-batch \
    --account-name $STORAGE_ACCOUNT \
    --source build \
    --destination '$web' \
    --overwrite

# 11. Afficher les URLs
echo -e "${GREEN}✅ Déploiement terminé !${NC}"
echo "================================="
echo -e "${GREEN}Backend API:${NC} https://$APP_NAME.azurewebsites.net/api"
echo -e "${GREEN}Frontend:${NC} https://$STORAGE_ACCOUNT.z6.web.core.windows.net"

# 12. Tests de connexion
echo -e "${GREEN}🧪 Tests de connexion...${NC}"
sleep 30  # Attendre que l'application démarre
curl -f "https://$APP_NAME.azurewebsites.net/api/products" || echo "❌ API non accessible"
```

### 7. Monitoring et Logs

#### Configuration Application Insights :
```bash
# Activer Application Insights
az monitor app-insights component create \
    --app ivoka-insights \
    --location $LOCATION \
    --resource-group $RESOURCE_GROUP

# Connecter à l'App Service
az monitor app-insights component connect-webapp \
    --app ivoka-insights \
    --resource-group $RESOURCE_GROUP \
    --web-app $APP_NAME
```

### 8. Sécurité

#### Configuration SSL/TLS :
```bash
# Activer SSL personnalisé
az webapp config ssl bind \
    --name $APP_NAME \
    --resource-group $RESOURCE_GROUP \
    --certificate-thumbprint <thumbprint> \
    --ssl-type SNI
```

#### Configuration CORS pour production :
```bash
az webapp cors add \
    --name $APP_NAME \
    --resource-group $RESOURCE_GROUP \
    --allowed-origins "https://$STORAGE_ACCOUNT.z6.web.core.windows.net"
```

## 📋 Checklist de Déploiement

### Pré-déploiement
- [ ] Créer un compte Azure
- [ ] Installer Azure CLI
- [ ] Configurer les variables d'environnement
- [ ] Préparer le certificat SSL (optionnel)

### Déploiement
- [ ] Exécuter le script Terraform
- [ ] Déployer le backend (WAR)
- [ ] Déployer le frontend (build React)
- [ ] Configurer la base de données
- [ ] Tester les connexions

### Post-déploiement
- [ ] Configurer le monitoring
- [ ] Activer les logs
- [ ] Configurer les alertes
- [ ] Tester la performance
- [ ] Sécuriser l'application

## 🔧 Commandes Utiles

### Vérifier le statut de l'application :
```bash
az webapp show --name ivoka-api --resource-group ivoka-rg
az webapp log tail --name ivoka-api --resource-group ivoka-rg
```

### Redémarrer l'application :
```bash
az webapp restart --name ivoka-api --resource-group ivoka-rg
```

### Mettre à jour l'application :
```bash
az webapp deploy --name ivoka-api --resource-group ivoka-rg --src-path backend/target/ivoka-api.war
```

## 📊 Architecture Déployée

```
┌─────────────────────────────────────────────────────────────┐
│                        INTERNET                              │
└─────────────────┬───────────────────────────────────────────┘
                  │
        ┌─────────▼─────────┐
        │    Azure CDN      │
        └─────────┬─────────┘
                  │
        ┌─────────▼─────────┐
        │  Azure Storage    │  (Frontend React)
        │   (Static Web)    │
        └─────────┬─────────┘
                  │
        ┌─────────▼─────────┐
        │  Azure App Service│  (Backend Java API)
        │    (Tomcat)       │
        └─────────┬─────────┘
                  │
        ┌─────────▼─────────┐
        │ Azure Database    │  (MySQL)
        │     for MySQL     │
        └───────────────────┘
```

Cette configuration permet un déploiement complet et scalable d'IVOKA sur Azure avec React.js frontend et Java Servlets/JSP backend.