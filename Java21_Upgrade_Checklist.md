# Java 21 Upgrade Checklist

## ✅ Completed Tasks

### Configuration Updates
- [x] Updated `maven.compiler.source` from 11 to 21
- [x] Updated `maven.compiler.target` from 11 to 21
- [x] Added Maven Compiler Plugin 3.11.0 with `<release>21</release>`
- [x] Updated Maven War Plugin from 3.3.1 to 3.3.2

### Dependency Migration (javax → jakarta)
- [x] Servlet API: 4.0.1 → 6.0.0
- [x] JSP API: 2.3.3 → 3.1.1
- [x] JSTL: 1.2 → 3.0.0 + 3.0.1
- [x] JSON Processing: 1.1.4 → 2.1.1
- [x] MySQL Connector: 8.0.33 → 8.2.0

### Java Source Code Updates
- [x] ProductsServlet.java - 11 imports migrated
- [x] UsersServlet.java - 9 imports migrated
- [x] CartServlet.java - 11 imports migrated
- [x] AuthServlet.java - 9 imports migrated
- [x] MessagesServlet.java - 9 imports migrated
- [x] AdminServlet.java - 11 imports migrated
- [x] DatabaseInitializer.java - 4 imports migrated
- [x] DatabaseConnection.java - 1 import migrated

### Documentation Created
- [x] JAVA_21_UPGRADE_SUMMARY.md
- [x] JAVA21_BUILD_GUIDE.md
- [x] JAVA_21_CHANGELOG.md
- [x] Java21_Upgrade_Checklist.md (this file)

---

## 🔄 Remaining Tasks

### Pre-Build Verification
- [ ] Verify Java 21 JDK is installed
  ```
  java -version
  ```
- [ ] Verify Maven is installed
  ```
  mvn --version
  ```
- [ ] Ensure Maven can find Java 21
  ```
  mvn help:system | grep java.version
  ```

### Build & Test
- [ ] Run Maven clean package
  ```
  mvn clean package
  ```
- [ ] Verify WAR file created
  ```
  ls -la target/ivoka-api.war
  ```
- [ ] Check for compilation errors
- [ ] Review build logs for warnings

### Deployment Preparation
- [ ] Backup current application
- [ ] Verify Tomcat 10.1+ is available
- [ ] Backup Tomcat configuration
- [ ] Review Tomcat Java options
- [ ] Ensure sufficient disk space

### Deployment
- [ ] Stop Tomcat service
- [ ] Copy new WAR to webapps/
- [ ] Start Tomcat service
- [ ] Monitor Tomcat logs
- [ ] Test application endpoints
- [ ] Verify database connectivity

### Post-Deployment Validation
- [ ] Check application logs
- [ ] Run smoke tests
- [ ] Verify all servlets respond
- [ ] Test authentication flow
- [ ] Test product listing
- [ ] Test shopping cart
- [ ] Test checkout process
- [ ] Test admin panel

### Performance & Monitoring
- [ ] Monitor memory usage
- [ ] Check response times
- [ ] Verify error rates are normal
- [ ] Monitor database connections
- [ ] Check for any deprecated warnings

---

## 📋 System Requirements

### Minimum Requirements
- Java 21 JDK (LTS version)
- Apache Tomcat 10.1+
- Maven 3.8.0+
- MySQL 5.7+

### Recommended
- Java 21.0.1 LTS or later (latest patch)
- Apache Tomcat 10.1.x
- Maven 3.9.x
- MySQL 8.0+

### Incompatible
- ❌ Java 11 or earlier
- ❌ Apache Tomcat 9 or earlier
- ❌ Maven 3.5.x or earlier

---

## 🚀 Build Command Reference

### Full Clean Build
```bash
cd backend
mvn clean package
```

### Skip Tests
```bash
mvn clean package -DskipTests
```

### Verbose Output
```bash
mvn clean package -X
```

### Update Dependencies
```bash
mvn dependency:resolve-plugins
```

### Check for Vulnerabilities (CVE)
```bash
mvn org.owasp:dependency-check-maven:check
```

---

## 🔍 Verification Commands

### Verify Java 21 is Used
```bash
mvn --version
mvn help:system | grep java.version
```

### Check Dependencies
```bash
mvn dependency:tree
```

### Find Unused Dependencies
```bash
mvn dependency:analyze
```

### Check for Compilation Issues
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

---

## 📊 Expected Build Output

### Successful Build Signs
```
[INFO] --- maven-compiler-plugin:3.11.0:compile ...
[INFO] --- maven-war-plugin:3.3.2:war ...
[INFO] Building war: target/ivoka-api.war
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXXs
```

### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Cannot find symbol | Missing import or incorrect package |
| ClassNotFoundException | Missing JAR in classpath |
| Method not found | API incompatibility - check version |
| Compilation failed | Syntax errors or missing dependencies |
| Cannot connect to DB | Database connection config issue |

---

## 📝 Testing Checklist

### Unit Tests (if applicable)
- [ ] Run: `mvn test`
- [ ] Verify all tests pass
- [ ] Check code coverage
- [ ] Review test failures

### Integration Tests
- [ ] Authentication works
- [ ] Database operations work
- [ ] API endpoints respond
- [ ] Session management works
- [ ] Cart operations work
- [ ] Admin functions work

### Functional Tests
- [ ] Browse products
- [ ] Add to cart
- [ ] Complete checkout
- [ ] View orders
- [ ] Update profile
- [ ] Access admin panel

### Performance Tests
- [ ] Response times acceptable
- [ ] Database queries optimized
- [ ] Memory usage stable
- [ ] No memory leaks observed
- [ ] CPU usage normal

---

## 🆘 Troubleshooting

### Build Fails with "package javax not found"
**Cause**: Incorrect imports in source files  
**Solution**: Verify all imports were changed to jakarta.*

### Compilation Error "cannot find symbol"
**Cause**: Missing import or wrong dependency version  
**Solution**: Check pom.xml dependencies and imports

### Runtime Error "ClassNotFoundException"
**Cause**: Missing dependency in classpath  
**Solution**: Run `mvn clean package` and verify JAR files in target/

### Tomcat Cannot Start Application
**Cause**: Tomcat version doesn't support Jakarta EE  
**Solution**: Upgrade to Tomcat 10.1+

### Database Connection Failed
**Cause**: Connection properties or driver issue  
**Solution**: Check web.xml database parameters

---

## 📞 Support Resources

- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Jakarta EE Documentation](https://jakarta.ee/)
- [Apache Tomcat 10 Guide](https://tomcat.apache.org/tomcat-10.1-doc/)
- [Maven Documentation](https://maven.apache.org/)
- [MySQL Java Connector](https://dev.mysql.com/downloads/connector/j/)

---

## 📅 Timeline Template

```
Date: _______________
Start Time: _______________

PHASE 1: Pre-Deployment (15 min)
- [ ] Backup application
- [ ] Backup database
- [ ] Verify backup integrity
- [ ] Stop current application

PHASE 2: Deploy New Build (10 min)
- [ ] Copy new WAR file
- [ ] Start Tomcat
- [ ] Monitor startup logs
- [ ] Verify application running

PHASE 3: Testing (30 min)
- [ ] Test basic functionality
- [ ] Test all endpoints
- [ ] Verify database connectivity
- [ ] Check error logs

PHASE 4: Validation (15 min)
- [ ] Performance acceptable
- [ ] No errors in logs
- [ ] All users can access
- [ ] Sign-off from team

End Time: _______________
Total Duration: _______________
Status: [ ] Success [ ] Rollback [ ] Partial Success
```

---

## ✨ Success Criteria

Your upgrade is successful when:

1. ✅ Application builds without errors
2. ✅ WAR file is created successfully
3. ✅ Application starts in Tomcat
4. ✅ All endpoints respond correctly
5. ✅ Database connections work
6. ✅ No error logs on startup
7. ✅ Authentication works
8. ✅ Product browsing works
9. ✅ Shopping cart functions properly
10. ✅ Admin panel is accessible

---

**Last Updated**: November 26, 2025  
**Upgrade Status**: Configuration Complete ✅
