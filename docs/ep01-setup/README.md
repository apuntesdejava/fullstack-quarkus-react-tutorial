# Episodio 01 — Setup del Entorno Completo

> **Rama:** `ep/01-setup` | **Tag:** `ep-01`

---

## 🎯 Objetivo

Al terminar este episodio tendrás:

- Todas las herramientas instaladas y verificadas
- El backend estructurado como un proyecto Maven multi-módulo con arquitectura hexagonal real
- La infraestructura local corriendo en Docker
- El proyecto React creado con Vite + Tailwind CSS
- Todo funcionando con `quarkus:dev`

---

## 💻 Nota sobre sistemas operativos

| Ícono | Sistema                          |
|-------|----------------------------------|
| 🐧    | Linux, macOS o WSL2 (Bash / Zsh) |
| 🪟    | Windows nativo (PowerShell)      |

> **Recomendación para Windows:** con WSL2 + Ubuntu puedes seguir los comandos 🐧 en toda la serie.
 
---

## 📁 Archivos de este episodio

```
fullstack-quarkus-react-tutorial/
│
├── docs/
│   └── ep01-setup/
│       └── README.md                              ← este archivo
│
├── backend/
│   ├── pom.xml                                    ← Parent POM
│   ├── mvnw / mvnw.cmd                            ← Maven Wrapper
│   ├── quarkstack-domain/
│   │   ├── pom.xml
│   │   └── src/main/java/dev/quarkstack/domain/
│   │       └── package-info.java
│   ├── quarkstack-application/
│   │   ├── pom.xml
│   │   └── src/main/java/dev/quarkstack/application/
│   │       └── package-info.java
│   ├── quarkstack-adapter-rest/
│   │   ├── pom.xml
│   │   └── src/main/java/dev/quarkstack/adapter/rest/
│   │       └── package-info.java
│   ├── quarkstack-adapter-persistence/
│   │   ├── pom.xml
│   │   └── src/main/java/dev/quarkstack/adapter/persistence/
│   │       └── package-info.java
│   ├── quarkstack-adapter-messaging/
│   │   ├── pom.xml
│   │   └── src/main/java/dev/quarkstack/adapter/messaging/
│   │       └── package-info.java
│   └── quarkstack-runner/
│       ├── pom.xml
│       └── src/main/resources/
│           ├── application.properties
│           ├── application-docker.properties
│           └── application-prod.properties
│
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── index.css
│       └── App.jsx
│
└── infra/
    └── docker/
        └── docker-compose.yml
```

---

## Paso 1 — Instalar las herramientas

### 1.1 JDK 21

🐧 **Linux / WSL2 — con SDKMAN:**
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.5-tem
sdk use java 21.0.5-tem
```

🐧 **macOS:**
```bash
brew install --cask temurin@21
```

🪟 **Windows — con winget:**
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```

🪟 **Windows — instalador manual:**
Descarga el `.msi` desde https://adoptium.net → elige **Temurin 21 LTS** → marca *"Set JAVA_HOME variable"*.

**Verificar** (🐧 y 🪟):
```bash
java -version
# openjdk version "21.0.x" ...
```

---

### 1.2 Quarkus CLI

🐧 **Linux / WSL2 — con SDKMAN:**
```bash
sdk install quarkus
```

🐧 **macOS:**
```bash
brew install quarkusio/tap/quarkus
```

🪟 **Windows — con Scoop:**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
Invoke-RestMethod -Uri https://get.scoop.sh | Invoke-Expression
scoop bucket add java
scoop install quarkus-cli
```

🪟 **Windows — con Chocolatey:**
```powershell
choco install quarkus
```

**Verificar:**
```bash
quarkus --version
# 3.x.x
```

---

### 1.3 Node 24 LTS

🐧 **Linux / WSL2:**
```bash
curl -fsSL https://deb.nodesource.com/setup_24.x | sudo -E bash -
sudo apt install -y nodejs
```

🐧 **macOS:**
```bash
brew install node@24
brew link node@24
```

🪟 **Windows — con winget:**
```powershell
winget install OpenJS.NodeJS.LTS
```

🪟 **Windows — instalador manual:**
Descarga el `.msi` desde https://nodejs.org → elige **24 LTS**.

> Si ya tienes otra versión de Node instalada: [nvm](https://github.com/nvm-sh/nvm) (🐧) o [nvm-windows](https://github.com/coreybutler/nvm-windows) (🪟).

**Verificar:**
```bash
node --version   # v24.x.x
npm --version    # 10.x.x
```

---

### 1.4 Docker Desktop

Descarga e instala desde https://www.docker.com/products/docker-desktop

> 🪟 Habilita la integración WSL2 en **Settings → Resources → WSL Integration**.

**Verificar:**
```bash
docker --version
docker compose version
```

---

### 1.5 Azure CLI (para episodios de despliegue)

🐧 **Linux / WSL2:**
```bash
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
```

🐧 **macOS:**
```bash
brew install azure-cli
```

🪟 **Windows:**
```powershell
winget install Microsoft.AzureCLI
```

---

## Paso 2 — Crear la estructura multi-módulo

### 2.1 Directorios

🐧 Bash:
```bash
cd fullstack-quarkus-react-tutorial

BASE="backend/src/main/java/dev/quarkstack"

mkdir -p backend/quarkstack-domain/src/main/java/dev/quarkstack/domain/model
mkdir -p backend/quarkstack-domain/src/main/java/dev/quarkstack/domain/port/in
mkdir -p backend/quarkstack-domain/src/main/java/dev/quarkstack/domain/port/out
mkdir -p backend/quarkstack-domain/src/test/java/dev/quarkstack/domain
mkdir -p backend/quarkstack-application/src/main/java/dev/quarkstack/application/usecase
mkdir -p backend/quarkstack-application/src/main/java/dev/quarkstack/application/service
mkdir -p backend/quarkstack-application/src/test/java/dev/quarkstack/application
mkdir -p backend/quarkstack-adapter-rest/src/main/java/dev/quarkstack/adapter/rest
mkdir -p backend/quarkstack-adapter-persistence/src/main/java/dev/quarkstack/adapter/persistence/pg
mkdir -p backend/quarkstack-adapter-persistence/src/main/java/dev/quarkstack/adapter/persistence/mongo
mkdir -p backend/quarkstack-adapter-persistence/src/main/resources/db/migration
mkdir -p backend/quarkstack-adapter-messaging/src/main/java/dev/quarkstack/adapter/messaging/producer
mkdir -p backend/quarkstack-adapter-messaging/src/main/java/dev/quarkstack/adapter/messaging/consumer
mkdir -p backend/quarkstack-runner/src/main/java/dev/quarkstack/infrastructure/config
mkdir -p backend/quarkstack-runner/src/main/java/dev/quarkstack/infrastructure/exception
mkdir -p backend/quarkstack-runner/src/main/java/dev/quarkstack/infrastructure/mapper
mkdir -p backend/quarkstack-runner/src/main/resources
mkdir -p backend/quarkstack-runner/src/test/java/dev/quarkstack
```

🪟 PowerShell:
```powershell
cd fullstack-quarkus-react-tutorial

@(
  "backend/quarkstack-domain/src/main/java/dev/quarkstack/domain/model",
  "backend/quarkstack-domain/src/main/java/dev/quarkstack/domain/port/in",
  "backend/quarkstack-domain/src/main/java/dev/quarkstack/domain/port/out",
  "backend/quarkstack-domain/src/test/java/dev/quarkstack/domain",
  "backend/quarkstack-application/src/main/java/dev/quarkstack/application/usecase",
  "backend/quarkstack-application/src/main/java/dev/quarkstack/application/service",
  "backend/quarkstack-application/src/test/java/dev/quarkstack/application",
  "backend/quarkstack-adapter-rest/src/main/java/dev/quarkstack/adapter/rest",
  "backend/quarkstack-adapter-persistence/src/main/java/dev/quarkstack/adapter/persistence/pg",
  "backend/quarkstack-adapter-persistence/src/main/java/dev/quarkstack/adapter/persistence/mongo",
  "backend/quarkstack-adapter-persistence/src/main/resources/db/migration",
  "backend/quarkstack-adapter-messaging/src/main/java/dev/quarkstack/adapter/messaging/producer",
  "backend/quarkstack-adapter-messaging/src/main/java/dev/quarkstack/adapter/messaging/consumer",
  "backend/quarkstack-runner/src/main/java/dev/quarkstack/infrastructure/config",
  "backend/quarkstack-runner/src/main/java/dev/quarkstack/infrastructure/exception",
  "backend/quarkstack-runner/src/main/java/dev/quarkstack/infrastructure/mapper",
  "backend/quarkstack-runner/src/main/resources",
  "backend/quarkstack-runner/src/test/java/dev/quarkstack"
) | ForEach-Object { New-Item -ItemType Directory -Force -Path $_ }
```

---

### 2.2 package-info.java en cada módulo

> ⚠️ **Importante:** Maven solo crea el directorio `target/classes` si hay al menos un archivo `.java` que compilar. Sin él, Quarkus dev mode falla al arrancar porque busca ese directorio en cada módulo. La solución es agregar un `package-info.java` — un archivo Java estándar para documentar el paquete.

Crea los siguientes 5 archivos:

**`backend/quarkstack-domain/src/main/java/dev/quarkstack/domain/package-info.java`**
```java
/** Núcleo del negocio. Entidades, Value Objects y puertos. */
package dev.quarkstack.domain;
```

**`backend/quarkstack-application/src/main/java/dev/quarkstack/application/package-info.java`**
```java
/** Casos de uso y servicios de aplicación. */
package dev.quarkstack.application;
```

**`backend/quarkstack-adapter-rest/src/main/java/dev/quarkstack/adapter/rest/package-info.java`**
```java
/** Adaptador de entrada HTTP. JAX-RS resources y DTOs. */
package dev.quarkstack.adapter.rest;
```

**`backend/quarkstack-adapter-persistence/src/main/java/dev/quarkstack/adapter/persistence/package-info.java`**
```java
/** Adaptadores de salida para persistencia. PostgreSQL y MongoDB. */
package dev.quarkstack.adapter.persistence;
```

**`backend/quarkstack-adapter-messaging/src/main/java/dev/quarkstack/adapter/messaging/package-info.java`**
```java
/** Adaptadores de mensajería. Producers y consumers de Kafka. */
package dev.quarkstack.adapter.messaging;
```

---

### 2.3 Parent POM — `backend/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
           https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>dev.quarkstack</groupId>
  <artifactId>quarkstack-parent</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>pom</packaging>

  <name>QuarkStack :: Parent</name>
  <description>Curso Full-Stack con Quarkus y React</description>

    <!-- =========================================================
         Módulos que componen el proyecto
         El orden importa: Maven los compila en este orden
         ========================================================= -->
    <modules>
    <module>quarkstack-domain</module>
    <module>quarkstack-application</module>
    <module>quarkstack-adapter-rest</module>
    <module>quarkstack-adapter-persistence</module>
    <module>quarkstack-adapter-messaging</module>
    <module>quarkstack-runner</module>
  </modules>

  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <quarkus.platform.version>3.34.5</quarkus.platform.version>
    <quarkus.platform.group-id>io.quarkus.platform</quarkus.platform.group-id>
    <quarkus.platform.artifact-id>quarkus-bom</quarkus.platform.artifact-id>
    <surefire.version>3.5.5</surefire.version>
    <failsafe.version>3.5.5</failsafe.version>
  </properties>

    <!-- =========================================================
         BOM centralizado: todas las versiones de Quarkus
         y Jakarta EE se resuelven aquí
         ========================================================= -->
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>${quarkus.platform.group-id}</groupId>
        <artifactId>${quarkus.platform.artifact-id}</artifactId>
        <version>${quarkus.platform.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
        <!-- Dependencias internas del proyecto -->
      <dependency>
        <groupId>dev.quarkstack</groupId>
        <artifactId>quarkstack-domain</artifactId>
        <version>${project.version}</version>
      </dependency>
      <dependency>
        <groupId>dev.quarkstack</groupId>
        <artifactId>quarkstack-application</artifactId>
        <version>${project.version}</version>
      </dependency>
      <dependency>
        <groupId>dev.quarkstack</groupId>
        <artifactId>quarkstack-adapter-rest</artifactId>
        <version>${project.version}</version>
      </dependency>
      <dependency>
        <groupId>dev.quarkstack</groupId>
        <artifactId>quarkstack-adapter-persistence</artifactId>
        <version>${project.version}</version>
      </dependency>
      <dependency>
        <groupId>dev.quarkstack</groupId>
        <artifactId>quarkstack-adapter-messaging</artifactId>
        <version>${project.version}</version>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <build>
    <pluginManagement>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.15.0</version>
          <configuration>
            <release>21</release>
            <parameters>true</parameters>
          </configuration>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>${surefire.version}</version>
        </plugin>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-failsafe-plugin</artifactId>
          <version>${failsafe.version}</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>

</project>
```

---

### 2.4 Módulo Domain — `backend/quarkstack-domain/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
           https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>dev.quarkstack</groupId>
    <artifactId>quarkstack-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>quarkstack-domain</artifactId>
  <name>QuarkStack :: Domain</name>
  <description>
    Núcleo del negocio. Entidades, Value Objects y puertos (interfaces).
    CERO dependencias de frameworks. Java puro.
  </description>

  <dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

</project>
```

---

### 2.5 Módulo Application — `backend/quarkstack-application/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
           https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>dev.quarkstack</groupId>
    <artifactId>quarkstack-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>quarkstack-application</artifactId>
  <name>QuarkStack :: Application</name>
  <description>Casos de uso y servicios de aplicación. Solo conoce al dominio.</description>

  <dependencies>
    <dependency>
      <groupId>dev.quarkstack</groupId>
      <artifactId>quarkstack-domain</artifactId>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-junit-jupiter</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

</project>
```

---

### 2.6 Módulo Adapter REST — `backend/quarkstack-adapter-rest/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
           https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>dev.quarkstack</groupId>
    <artifactId>quarkstack-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>quarkstack-adapter-rest</artifactId>
  <name>QuarkStack :: Adapter :: REST</name>

  <dependencies>
    <dependency>
      <groupId>dev.quarkstack</groupId>
      <artifactId>quarkstack-application</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-reactive</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-reactive-jackson</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-smallrye-openapi</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-smallrye-health</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-arc</artifactId>
    </dependency>
  </dependencies>

</project>
```

---

### 2.7 Módulo Adapter Persistence — `backend/quarkstack-adapter-persistence/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
           https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>dev.quarkstack</groupId>
    <artifactId>quarkstack-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>quarkstack-adapter-persistence</artifactId>
  <name>QuarkStack :: Adapter :: Persistence</name>

  <dependencies>
    <dependency>
      <groupId>dev.quarkstack</groupId>
      <artifactId>quarkstack-application</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-hibernate-orm-panache</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-postgresql</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-hibernate-reactive-panache</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-reactive-pg-client</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-mongodb-panache</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-flyway</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-arc</artifactId>
    </dependency>
  </dependencies>

</project>
```

---

### 2.8 Módulo Adapter Messaging — `backend/quarkstack-adapter-messaging/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
           https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>dev.quarkstack</groupId>
    <artifactId>quarkstack-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>quarkstack-adapter-messaging</artifactId>
  <name>QuarkStack :: Adapter :: Messaging</name>

  <dependencies>
    <dependency>
      <groupId>dev.quarkstack</groupId>
      <artifactId>quarkstack-application</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-smallrye-reactive-messaging-kafka</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-arc</artifactId>
    </dependency>
  </dependencies>

</project>
```

---

### 2.9 Módulo Runner — `backend/quarkstack-runner/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
           https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>dev.quarkstack</groupId>
    <artifactId>quarkstack-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>quarkstack-runner</artifactId>
  <name>QuarkStack :: Runner</name>
  <description>
    Punto de ensamblaje. Une todos los adaptadores y ejecuta Quarkus.
    No contiene lógica de negocio.
  </description>

  <dependencies>
    <dependency>
      <groupId>dev.quarkstack</groupId>
      <artifactId>quarkstack-adapter-rest</artifactId>
    </dependency>
    <dependency>
      <groupId>dev.quarkstack</groupId>
      <artifactId>quarkstack-adapter-persistence</artifactId>
    </dependency>
    <dependency>
      <groupId>dev.quarkstack</groupId>
      <artifactId>quarkstack-adapter-messaging</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-oidc</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-smallrye-jwt</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-opentelemetry</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-container-image-docker</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-junit5</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>io.rest-assured</groupId>
      <artifactId>rest-assured</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
        <!--
          El quarkus-maven-plugin SOLO vive aquí.
          quarkus:dev, quarkus:build, quarkus:test se ejecutan
          desde este módulo (o desde el parent con -pl).
        -->
        <plugin>
        <groupId>${quarkus.platform.group-id}</groupId>
        <artifactId>quarkus-maven-plugin</artifactId>
        <version>${quarkus.platform.version}</version>
        <extensions>true</extensions>
        <executions>
          <execution>
            <goals>
              <goal>build</goal>
              <goal>generate-code</goal>
              <goal>generate-code-tests</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <configuration>
          <systemPropertyVariables>
            <java.util.logging.manager>
              org.jboss.logmanager.LogManager
            </java.util.logging.manager>
            <maven.home>${maven.home}</maven.home>
          </systemPropertyVariables>
        </configuration>
      </plugin>
    </plugins>
  </build>

</project>
```

---

### 2.10 Maven Wrapper

🐧 Bash:
```bash
cd backend
mvn wrapper:wrapper -Dmaven=3.9.15
```

🪟 PowerShell:
```powershell
cd backend
mvn wrapper:wrapper "-Dmaven=3.9.15"
```

🐧 Dar permisos de ejecución (solo Linux/macOS):
```bash
chmod +x mvnw
```

---

## Paso 3 — Archivos de configuración

Quarkus soporta archivos de propiedades por perfil. Usamos tres archivos separados para mantener cada entorno limpio y claro:

| Archivo                         | Perfil activo | Cuándo se usa                           |
|---------------------------------|---------------|-----------------------------------------|
| `application.properties`        | siempre       | Config común a todos los perfiles       |
| `application-docker.properties` | `docker`      | Desarrollo con docker-compose levantado |
| `application-prod.properties`   | `prod`        | Producción en Azure                     |

### Modos de arranque disponibles

```bash
# Modo Dev Services: Quarkus levanta sus propios contenedores automáticamente
# No necesitas docker-compose, pero los datos se pierden al reiniciar
quarkus:dev

# Modo Docker: usa el docker-compose que tienes corriendo
# Los datos persisten entre reinicios, tienes Kafka UI disponible
quarkus:dev -Dquarkus.profile=docker
```

> Para el curso usaremos siempre el perfil `docker` porque queremos ver los datos persistir y usar la Kafka UI. Pero si quieres arrancar rápido sin levantar nada, `quarkus:dev` sin perfil funciona igual gracias a Dev Services.

---

### `backend/quarkstack-runner/src/main/resources/application.properties`

Config común a todos los perfiles:

```properties
# =============================================================================
# QuarkStack — application.properties
# Config común. Los perfiles específicos van en:
#   application-docker.properties  → desarrollo con docker-compose
#   application-prod.properties    → producción (Azure)
# =============================================================================

# --- Aplicación ---
quarkus.application.name=quarkstack-backend
quarkus.application.version=1.0.0-SNAPSHOT

# --- HTTP ---
quarkus.http.port=8080
quarkus.http.cors.enabled=true
quarkus.http.cors.origins=http://localhost:5173
quarkus.http.cors.methods=GET,POST,PUT,PATCH,DELETE,OPTIONS
quarkus.http.cors.headers=Content-Type,Authorization

# --- Hibernate ORM ---
quarkus.hibernate-orm.database.generation=none

# --- Flyway ---
quarkus.flyway.migrate-at-start=true
quarkus.flyway.locations=classpath:db/migration

# --- OpenAPI ---
quarkus.smallrye-openapi.info-title=QuarkStack API
quarkus.smallrye-openapi.info-version=1.0.0
quarkus.smallrye-openapi.info-description=API del curso QuarkStack
quarkus.swagger-ui.always-include=true
quarkus.swagger-ui.path=/swagger-ui

# --- Health ---
quarkus.smallrye-health.ui.always-include=true

# --- OIDC: desactivado hasta el Ep 24 ---
quarkus.oidc.enabled=false

# --- OpenTelemetry: desactivado hasta el Ep 28b ---
quarkus.otel.sdk.disabled=true
quarkus.otel.exporter.otlp.endpoint=http://localhost:4317
quarkus.otel.resource.attributes=service.name=quarkstack-backend,service.version=1.0.0

# --- Logs ---
quarkus.log.console.format=%d{HH:mm:ss} %-5p [%c{2.}] (%t) %s%e%n
quarkus.log.level=INFO
quarkus.log.category."dev.quarkstack".level=DEBUG
```

---

### `backend/quarkstack-runner/src/main/resources/application-docker.properties`

Perfil para desarrollo con docker-compose. Se activa con `-Dquarkus.profile=docker`:

```properties
# =============================================================================
# QuarkStack — application-docker.properties
# Desarrollo local con docker-compose corriendo.
# Uso: quarkus:dev -Dquarkus.profile=docker
# =============================================================================

# --- PostgreSQL: usar contenedor del docker-compose ---
quarkus.datasource.db-kind=postgresql
quarkus.datasource.devservices.enabled=false
quarkus.datasource.username=quarkstack
quarkus.datasource.password=quarkstack123
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/quarkstack

# --- MongoDB: usar contenedor del docker-compose ---
quarkus.mongodb.devservices.enabled=false
quarkus.mongodb.connection-string=mongodb://quarkstack:quarkstack123@localhost:27017/quarkstack?authSource=admin
quarkus.mongodb.database=quarkstack

# --- Kafka: usar contenedor del docker-compose ---
quarkus.kafka.devservices.enabled=false
kafka.bootstrap.servers=localhost:9092
```

---

### `backend/quarkstack-runner/src/main/resources/application-prod.properties`

Perfil de producción. Las variables de entorno las inyecta Azure:

```properties
# =============================================================================
# QuarkStack — application-prod.properties
# Producción en Azure Container Apps.
# Las variables de entorno las provee el entorno de Azure.
# =============================================================================

# --- PostgreSQL (Azure Database for PostgreSQL) ---
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=${DB_USER}
quarkus.datasource.password=${DB_PASSWORD}
quarkus.datasource.jdbc.url=${DB_URL}

# --- MongoDB (Azure Cosmos DB API MongoDB) ---
quarkus.mongodb.connection-string=${MONGO_URL}
quarkus.mongodb.database=quarkstack

# --- Kafka (Azure Event Hubs) ---
kafka.bootstrap.servers=${KAFKA_BROKERS}

# --- OIDC (Microsoft Entra) ---
quarkus.oidc.enabled=true
quarkus.oidc.auth-server-url=${OIDC_AUTH_SERVER_URL}
quarkus.oidc.client-id=${OIDC_CLIENT_ID}

# --- OpenTelemetry (Azure Monitor) ---
quarkus.otel.sdk.disabled=false
quarkus.otel.exporter.otlp.endpoint=${OTEL_ENDPOINT}
```

---

## Paso 4 — Crear el proyecto React

🐧 Bash:
```bash
cd fullstack-quarkus-react-tutorial

npx create-vite@latest frontend --template react
```

🪟 PowerShell:
```powershell
cd fullstack-quarkus-react-tutorial

npx create-vite@latest frontend --template react
```

> ⚠️ Usa `npx create-vite@latest` en lugar de `npm create vite@latest -- --template react`. En Node 24 la segunda forma puede no pasar correctamente el argumento `--template` y mostrar un menú interactivo.

Si aparece la pregunta **"Install with npm and start now?"**, responde **No** — instalaremos las dependencias manualmente en el siguiente paso.

```bash
cd frontend
npm install
npm install react-router-dom @tanstack/react-query axios oidc-client-ts react-oidc-context
npm install -D tailwindcss @tailwindcss/vite
```

### `frontend/vite.config.js`

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
```

### `frontend/src/index.css`

Reemplaza **todo** el contenido con esta única línea. Tailwind se encarga del resto:

```css
@import "tailwindcss";
```

> Puedes borrar `App.css` — ya no se necesita.

### `frontend/src/App.jsx`

Reemplaza **todo** el contenido:

```jsx
function App() {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900 mb-2">
          QuarkStack 🔥
        </h1>
        <p className="text-gray-500">Full-Stack con Quarkus + React</p>
      </div>
    </div>
  )
}

export default App
```

---

## Paso 5 — Infraestructura Docker

Crea `infra/docker/docker-compose.yml`:

```yaml
# =============================================================================
# QuarkStack — Docker Compose local
# Uso (🐧 y 🪟 igual):
#   docker compose -f infra/docker/docker-compose.yml up -d
#   docker compose -f infra/docker/docker-compose.yml down
# =============================================================================

name: quarkstack

services:

  postgres:
    image: postgres:17.9-alpine
    container_name: quarkstack-postgres
    environment:
      POSTGRES_DB: quarkstack
      POSTGRES_USER: quarkstack
      POSTGRES_PASSWORD: quarkstack123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U quarkstack"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - quarkstack-net

  mongodb:
    image: mongo:7
    container_name: quarkstack-mongodb
    environment:
      MONGO_INITDB_ROOT_USERNAME: quarkstack
      MONGO_INITDB_ROOT_PASSWORD: quarkstack123
      MONGO_INITDB_DATABASE: quarkstack
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db
    healthcheck:
      test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - quarkstack-net

  kafka:
    image: apache/kafka:3.8.0
    container_name: quarkstack-kafka
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"
    ports:
      - "9092:9092"
    volumes:
      - kafka_data:/var/lib/kafka/data
    healthcheck:
      test: ["CMD", "/opt/kafka/bin/kafka-broker-api-versions.sh",
             "--bootstrap-server", "localhost:9092"]
      interval: 15s
      timeout: 10s
      retries: 5
    networks:
      - quarkstack-net

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: quarkstack-kafka-ui
    depends_on:
      kafka:
        condition: service_healthy
    environment:
      KAFKA_CLUSTERS_0_NAME: quarkstack-local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
    ports:
      - "8090:8080"
    networks:
      - quarkstack-net

  # Keycloak — se configura en el Ep 24. Comenta este bloque hasta entonces.
  keycloak:
    image: quay.io/keycloak/keycloak:26.0
    container_name: quarkstack-keycloak
    command: start-dev --import-realm
    environment:
      KC_BOOTSTRAP_ADMIN_USERNAME: admin
      KC_BOOTSTRAP_ADMIN_PASSWORD: admin
      KC_DB: dev-mem
      KC_HTTP_PORT: 8180
    ports:
      - "8180:8180"
    volumes:
      - ./keycloak/realm-export.json:/opt/keycloak/data/import/realm-export.json
    networks:
      - quarkstack-net

networks:
  quarkstack-net:
    driver: bridge

volumes:
  postgres_data:
  mongo_data:
  kafka_data:
```

> ⚠️ **PostgreSQL 18+** cambió la estructura interna de directorios. Por eso usamos `17.9-alpine` y montamos el volumen en `/var/lib/postgresql` (sin `/data`). Si en el futuro quieres usar PostgreSQL 18, consulta la [documentación oficial](https://github.com/docker-library/postgres/pull/1259).

---

## Paso 6 — Verificar que todo funciona

### 6.1 Levantar Docker

🐧 y 🪟:
```bash
docker compose -f infra/docker/docker-compose.yml up -d
docker compose -f infra/docker/docker-compose.yml ps
```

Resultado esperado:
```
NAME                     STATUS
quarkstack-postgres      running (healthy)
quarkstack-mongodb       running (healthy)
quarkstack-kafka         running (healthy)
quarkstack-kafka-ui      running
```

### 6.2 Compilar e instalar los módulos

> Este paso es necesario la primera vez y cada vez que modifiques un `pom.xml`. Instala todos los módulos en el repositorio local de Maven para que el runner pueda resolverlos.

🐧 Bash, desde `backend/`:
```bash
./mvnw install -DskipTests
```

🪟 PowerShell, desde `backend/`:
```powershell
.\mvnw.cmd install -DskipTests
```

Deberías ver el Reactor Build Order y al final:
```
[INFO] BUILD SUCCESS
```

### 6.3 Arrancar el backend en dev mode

🐧 Bash, desde `backend/quarkstack-runner/`:
```bash
../mvnw quarkus:dev -Dquarkus.profile=docker
```

🪟 PowerShell, desde `backend\quarkstack-runner\`:
```powershell
..\mvnw.cmd quarkus:dev "-Dquarkus.profile=docker"
```

> **Importante:** el goal correcto es `quarkus:dev`, no `quarkus:run`. El goal `run` empaqueta y ejecuta el JAR en modo producción — activa el perfil `prod` y falla si las variables de entorno de Azure no están definidas.

Verás el banner de Quarkus y las siguientes líneas:
```
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
...
Listening on: http://localhost:8080
```

Verifica:
- http://localhost:8080/q/health → `{"status":"UP"}`
- http://localhost:8080/swagger-ui → Swagger UI
- http://localhost:8080/q/dev → Dev UI de Quarkus

### 6.4 Arrancar el frontend

🐧 y 🪟:
```bash
cd frontend
npm run dev
```

Verifica: http://localhost:5173 → pantalla de QuarkStack 🔥

### 6.5 Kafka UI

Verifica: http://localhost:8090 → cluster `quarkstack-local` activo

---

## 🧩 Resumen de puertos

| Servicio        | Puerto | URL                                         |
|-----------------|--------|---------------------------------------------|
| Backend Quarkus | 8080   | http://localhost:8080                       |
| Swagger UI      | 8080   | http://localhost:8080/swagger-ui            |
| Quarkus Dev UI  | 8080   | http://localhost:8080/q/dev                 |
| Frontend React  | 5173   | http://localhost:5173                       |
| PostgreSQL      | 5432   | jdbc:postgresql://localhost:5432/quarkstack |
| MongoDB         | 27017  | mongodb://localhost:27017                   |
| Kafka           | 9092   | localhost:9092                              |
| Kafka UI        | 8090   | http://localhost:8090                       |
| Keycloak        | 8180   | http://localhost:8180                       |

---

## ⚠️ Troubleshooting frecuente

**Puerto ocupado:**

🐧: `sudo lsof -i :5432`
🪟: `netstat -ano | findstr :5432`

---

**Kafka tarda en iniciar:** puede tardar hasta 60 segundos con KRaft.
```bash
docker logs quarkstack-kafka --tail 20
```

---

**`mvnw` sin permisos** (solo 🐧):
```bash
chmod +x backend/mvnw
```

---

**Quarkus no encuentra `target/classes` de un módulo:**
Asegúrate de haber creado los archivos `package-info.java` en cada módulo (ver paso 2.2) y de haber ejecutado `install` desde `backend/` antes de `quarkus:dev`.

---

**Quarkus arranca Dev Services (descarga contenedores) aunque tienes docker-compose corriendo:**
Estás arrancando sin el perfil `docker`. Usa:
```bash
quarkus:dev -Dquarkus.profile=docker
```

---

**Error `quarkus:run` vs `quarkus:dev`:**
Son goals distintos. `quarkus:run` empaqueta y ejecuta el JAR en modo producción. Para desarrollo siempre usa `quarkus:dev`.

---

## ✅ Checklist del episodio

- [ ] `java -version` → Java 21
- [ ] `quarkus --version` → 3.x.x
- [ ] `node --version` → v24.x.x
- [ ] `docker compose ps` → servicios healthy
- [ ] `./mvnw install -DskipTests` → BUILD SUCCESS con 6 módulos
- [ ] `quarkus:dev -Dquarkus.profile=docker` arranca desde `quarkstack-runner/`
- [ ] http://localhost:8080/q/health → `{"status":"UP"}`
- [ ] http://localhost:5173 → QuarkStack 🔥
- [ ] http://localhost:8090 → Kafka UI

---

## ▶️ Siguiente episodio

**[Ep 02 → Arquitectura Hexagonal: puertos, adaptadores y el dominio](../ep02-hexagonal-teoria/README.md)**

Con la estructura multi-módulo en su lugar, en el Ep 02 crearemos las primeras clases reales del dominio: entidades, Value Objects y los puertos que definen los contratos entre capas.

---

*QuarkStack — Construido con ❤️ y mucho ☕*
