# Migration Jakarta EE → Spring Boot

## Vue d'ensemble
Ce document détaille la conversion du projet `CY-J2EE` (Jakarta EE) en `CY-J2EE-SB` (Spring Boot 3.2.0).

## Philosophie de conversion
✅ **Conserver la logique métier identique**
✅ **Préserver la structure de données**
✅ **Garder les mêmes noms de routes**
✅ **Respecter la même expérience utilisateur**
❌ **Ne pas améliorer le code**
❌ **Ne pas ajouter de nouvelles fonctionnalités**

## Mapping des composants

### 1. Servlets → Contrôleurs Spring

#### Jakarta EE (Servlet)
```java
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        // Traitement
    }
}
```

#### Spring Boot (Controller)
```java
@Controller
public class LoginController {
    @PostMapping("/LoginServlet")
    public String login(...) {
        // Même traitement
    }
}
```

### Correspondances de routes

| Jakarta EE | Spring Boot |
|------------|------------|
| `/LoginServlet` | `POST /LoginServlet` |
| `/LogoutServlet` | `GET /logout` |
| `/EmployeeListServlet` | `GET /employee/list` |
| `/EmployeeCreateServlet` | `POST /employee/EmployeeCreateServlet` |
| `/EmployeeEditServlet` | `GET/POST /employee/{id}/...` |
| `/EmployeeDeleteServlet` | `POST /employee/{id}/delete` |
| `/DepartmentsListServlet` | `GET /department/list` |
| `/DepartmentCreateServlet` | `POST /department/DepartmentCreateServlet` |
| `/ProjectsListServlet` | `GET /project/list` |
| `/AbsenceListServlet` | `GET /absence/list` |
| `/AbsenceServlet` | `POST /absence/AbsenceServlet` |
| `/PayslipListServlet` | `GET /payslip/list` |
| `/PayslipCreateServlet` | `POST /payslip/PayslipCreateServlet` |

### 2. DAOs → Spring Data JPA Repositories

#### Jakarta EE (DAO avec JDBC)
```java
public class EmployeeDAO {
    private Connection conn;
    
    public List<Employee> findAll() throws SQLException {
        // SQL direct
        String sql = "SELECT * FROM employees";
        // Exécution et mapping
    }
}
```

#### Spring Boot (Repository)
```java
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findAll(); // Automatique
    Optional<Employee> findByEmail(String email);
    List<Employee> findByDepartmentId(Integer departmentId);
    
    @Query("SELECT e FROM Employee e WHERE ...")
    List<Employee> search(...);
}
```

### 3. JSP → Thymeleaf

#### Jakarta EE (JSP)
```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Liste des employés</h2>
<table>
    <c:forEach var="emp" items="${employees}">
        <tr>
            <td>${emp.firstName} ${emp.lastName}</td>
        </tr>
    </c:forEach>
</table>
```

#### Spring Boot (Thymeleaf)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<h2>Liste des employés</h2>
<table>
    <tr th:each="emp : ${employees}">
        <td th:text="${emp.firstName + ' ' + emp.lastName}"></td>
    </tr>
</table>
```

### 4. Configuration de persistance

#### Jakarta EE (persistence.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence>
    <persistence-unit name="Cy-j2eePU">
        <class>com.model.Employee</class>
        ...
        <properties>
            <property name="jakarta.persistence.jdbc.url" 
                value="jdbc:mysql://localhost:3306/JeeDb?serverTimezone=UTC"/>
            <property name="jakarta.persistence.jdbc.user" value="root"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

#### Spring Boot (application.properties)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/JeeDb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 5. Authentification et sessions

**Jakarta EE**
```java
HttpSession session = req.getSession();
session.setAttribute("user", user);
session.getAttribute("user");
```

**Spring Boot**
```java
HttpSession session = request.getSession();
session.setAttribute("user", user);
session.getAttribute("user");
```
→ *Identique, géré automatiquement par Spring*

## Changements d'architecture

### Structure des dossiers

```
Jakarta EE (WAR)                Spring Boot (JAR)
src/main/java/com/              src/main/java/com/
├── dao/                        ├── repository/      (remplace dao/)
├── model/                       ├── model/           (identique)
├── servlet/                     ├── controller/      (remplace servlet/)
├── util/                        └── util/            (identique)
src/main/webapp/
├── *.jsp                        src/main/resources/templates/
└── WEB-INF/web.xml            ├── *.html           (remplace JSP)
                                └── application.properties
```

### Dépendances principales

| Jakarta EE | Spring Boot |
|-----------|------------|
| `jakarta.servlet:jakarta.servlet-api` | Spring Web (automatique) |
| `org.hibernate:hibernate-core` | Spring Data JPA |
| `jakarta.servlet.jsp:jakarta.servlet.jsp-api` | Thymeleaf |
| Configuration manuelle | Configuration auto |
| `EntityManager` manuel | `JpaRepository` automatique |

## Changements de comportement

### Session utilisateur
- **Avant**: Gestion manuelle avec `HttpSession`
- **Après**: Gestion identique, Spring gère les cookies

### Transactions
- **Avant**: Gérées manuellement avec `EntityManager.getTransaction()`
- **Après**: Gérées automatiquement avec `@Transactional`

### Injection de dépendances
- **Avant**: Manuelle (instanciation directe)
- **Après**: Automatique avec `@Autowired`

## Points clés de l'implémentation

### 1. Entités JPA (inchangées)
```java
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // Même structure qu'avant
}
```

### 2. Repositories
Les repositories remplacent les DAOs avec des requêtes JPA:
```java
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // Les méthodes CRUD sont automatiques
    // Ajouter des recherches personnalisées au besoin
}
```

### 3. Contrôleurs
Remplacent les servlets avec le pattern MVC:
```java
@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @GetMapping("/list")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employeesList";
    }
}
```

### 4. Templates Thymeleaf
Remplacent les JSP:
```html
<tr th:each="emp : ${employees}">
    <td th:text="${emp.firstName}"></td>
</tr>
```

## Avantages de Spring Boot

✅ Configuration simplifiée (application.properties vs persistence.xml + web.xml)
✅ Démarrage rapide (auto-configuration)
✅ Moins de code passe-partout
✅ Meilleur support de la communauté
✅ Excellent pour les microservices

## Conservation des fonctionnalités

### Authentification
- Même logique avec `User` model
- Même gestion de session
- Même redirection selon le rôle

### CRUD
- Même opérations sur les entités
- Même validations
- Même gestion d'erreurs

### Base de données
- Même schéma MySQL
- Même relations
- Même contraintes

### Présentation
- Même CSS et design
- Même fonctionnalités JavaScript
- Même expérience utilisateur

## Testing et validation

Pour valider la conversion:

1. **Tests de login**
   - Accès avec admin/admin
   - Accès avec jean.dupont1@company.com/test
   - Vérification du changement de mot de passe

2. **Tests CRUD**
   - Créer un employé
   - Éditer l'employé
   - Lister les employés
   - Supprimer l'employé

3. **Tests de navigation**
   - Vérifier tous les dashboards
   - Vérifier les redirections selon le rôle
   - Vérifier la déconnexion

## Migration future

Pour passer à une version encore plus moderne:
- Ajouter Spring Security
- Implémenter des services métier
- Ajouter des tests unitaires
- Convertir en architecture REST
- Ajouter une frontend React/Angular

Mais ce n'était pas l'objectif ici ! 😊

---

**Conclusion**: La conversion Jakarta EE → Spring Boot est complète tout en conservant fidèlement la logique métier originale.
