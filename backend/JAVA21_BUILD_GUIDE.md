# Java 21 Build & Deployment Guide

## Quick Start

### Prerequisites
- Java 21 JDK installed
- Maven 3.8.0 or higher
- Tomcat 10.1+ (for running the WAR file)

### Build Command
```bash
cd backend
mvn clean package
```

This will:
1. Clean previous build artifacts
2. Compile all Java sources with Java 21
3. Run tests (if any)
4. Package into `target/ivoka-api.war`

### What Changed from Java 11 to 21

#### Configuration Changes
- **pom.xml**: Updated `maven.compiler.source` and `maven.compiler.target` to `21`
- **Dependencies**: Updated Jakarta EE (formerly javax) packages
- **MySQL Driver**: Updated to version 8.2.0

#### Code Changes
- **Import Updates**: All `javax.*` imports replaced with `jakarta.*` for:
  - Servlet APIs
  - JSON Processing
  - JSP/JSTL APIs

#### Files Modified
- 8 Java source files with import updates
- 1 pom.xml with version and dependency updates

### Deployment

#### Option 1: Standalone Tomcat
1. Extract Tomcat 10.1+
2. Copy `target/ivoka-api.war` to `$TOMCAT_HOME/webapps/`
3. Restart Tomcat
4. Access: `http://localhost:8080/ivoka-api/`

#### Option 2: Docker (Optional)
Create a Dockerfile:
```dockerfile
FROM tomcat:10-jdk21-temurin
COPY target/ivoka-api.war $CATALINA_HOME/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]
```

Build and run:
```bash
docker build -t ivoka-api:java21 .
docker run -p 8080:8080 ivoka-api:java21
```

### Troubleshooting

#### Maven Build Issues

**Error: "Maven is not in PATH"**
- Install Maven from https://maven.apache.org/download.cgi
- Add Maven bin directory to system PATH
- Verify: `mvn --version`

**Error: "Java version mismatch"**
- Ensure Java 21 is the default: `java -version`
- If needed, set `JAVA_HOME`: 
  - Windows: `set JAVA_HOME=C:\path\to\jdk21`
  - Linux/Mac: `export JAVA_HOME=/path/to/jdk21`

**Error: "Package jakarta.servlet not found"**
- This is a compile-time dependency issue
- Run: `mvn clean install`
- Check internet connection for Maven Central

#### Runtime Issues

**Error: "ClassNotFoundException: jakarta.servlet.http.HttpServlet"**
- Ensure Tomcat 10.1+ is being used
- Tomcat 9 and earlier use javax, not jakarta
- Update to Tomcat 10+

**Error: "Unsupported class version"**
- WAR file compiled with Java 21 but runtime is Java 8/11
- Update Java Runtime to 21+
- Verify: `java -version` on production server

### Java 21 Features Available

Your application can now use:
- **Virtual Threads** - Lightweight async processing
- **Pattern Matching** - Simplified complex logic
- **Records** - Immutable data classes
- **Sealed Classes** - Controlled inheritance
- **Text Blocks** - Multi-line strings
- **Foreign Function & Memory API** - Native interop

### Performance Tips

1. Use Virtual Threads for I/O-heavy operations:
```java
// Modern Java 21 way
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
```

2. Enable preview features if needed:
```bash
mvn clean package -X -o '--enable-preview'
```

### Validation Checklist

- [ ] Java 21 JDK installed and verified: `java -version`
- [ ] Maven installed and in PATH: `mvn --version`
- [ ] Project builds successfully: `mvn clean package`
- [ ] WAR file created in `target/ivoka-api.war`
- [ ] Tomcat 10.1+ running with Java 21
- [ ] Application accessible at deployment URL
- [ ] Database connections working
- [ ] API endpoints responding correctly

### Support & Resources

- [Oracle Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Jakarta EE Documentation](https://jakarta.ee/learn/)
- [Apache Tomcat 10 Guide](https://tomcat.apache.org/tomcat-10.1-doc/)
- [Maven Documentation](https://maven.apache.org/guides/)

### Rollback to Java 11 (if needed)

1. Revert pom.xml `maven.compiler.source` and `target` to `11`
2. Revert dependencies back to javax versions
3. Update import statements: jakarta → javax
4. Run: `mvn clean package`

---

**Last Updated**: November 26, 2025
