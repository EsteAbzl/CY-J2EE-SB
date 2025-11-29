# CY-J2EE-SB - Guide de Démarrage Rapide

## 🚀 Démarrage rapide

### 1. Installation des prérequis

#### Windows
- **Java 21**: https://www.oracle.com/java/technologies/javase-jdk21-downloads.html
- **Maven 3.8+**: https://maven.apache.org/download.cgi
- **MySQL 8.0+**: https://www.mysql.com/downloads/

#### macOS (avec Homebrew)
```bash
brew install java@21
brew install maven
brew install mysql
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get install openjdk-21-jdk
sudo apt-get install maven
sudo apt-get install mysql-server
```

### 2. Configuration de la base de données

#### Windows
```bash
cd H:\Documents\GitHub\CY-J2EE-SB
mysql -u root -p < conception/JeeDb.sql
```

#### macOS/Linux
```bash
cd ~/Documents/GitHub/CY-J2EE-SB
mysql -u root -p < conception/JeeDb.sql
```

### 3. Configuration de la connexion à la base de données

Éditer `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/JeeDb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
```

### 4. Lancer l'application

#### Option 1: Avec Maven (tous les systèmes)
```bash
mvn clean spring-boot:run
```

#### Option 2: Avec les scripts fournis

**Windows:**
```bash
run.bat
```

**macOS/Linux:**
```bash
chmod +x run.sh
./run.sh
```

#### Option 3: En tant que JAR
```bash
mvn clean package
java -jar target/demo-1.0-SNAPSHOT.jar
```

### 5. Accès à l'application

L'application sera accessible sur: **http://localhost:8080**

### Identifiants par défaut

| Utilisateur | Mot de passe | Rôle |
|-------------|----------|------|
| admin | admin | ADMIN |
| jean.dupont1@company.com | test | ADMIN |

**Note**: Le mot de passe "test" demandera un changement à la première connexion.

## 📁 Structure du projet

```
CY-J2EE-SB/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       ├── model/          # Entités JPA
│   │   │       ├── repository/     # Spring Data JPA Repositories
│   │   │       ├── controller/     # Contrôleurs MVC
│   │   │       └── util/           # Utilitaires
│   │   └── resources/
│   │       ├── templates/          # Templates Thymeleaf
│   │       ├── static/             # CSS, JS, images
│   │       └── application.properties
│   └── test/
├── conception/
│   └── JeeDb.sql                  # Script de base de données
├── pom.xml                        # Configuration Maven
├── run.bat / run.sh               # Scripts de lancement
└── README.md / QUICKSTART.md
```

## 🔧 Dépannage

### Erreur: "Connection refused" pour MySQL
- Vérifiez que MySQL est installé et en cours d'exécution
- Vérifiez les identifiants dans `application.properties`

### Erreur: "Port 8080 already in use"
- Changez le port dans `application.properties`:
  ```properties
  server.port=8081
  ```

### Erreur: "Java version not supported"
- Vérifiez que Java 21 est installé et défini comme JDK par défaut:
  ```bash
  java -version
  ```

## 📚 Ressources utiles

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Thymeleaf**: https://www.thymeleaf.org/
- **MySQL Documentation**: https://dev.mysql.com/doc/

## 🔐 Sécurité

Pour la production:
1. Changez tous les mots de passe par défaut
2. Configurez HTTPS
3. Utilisez des variables d'environnement pour les identifiants
4. Activez CSRF protection dans Spring Security

## 📝 Notes supplémentaires

- Cette application est une conversion fidèle du projet Jakarta EE `CY-J2EE`
- Tous les chemins et noms de fichiers sont préservés pour la cohérence
- La base de données reste identique (MySQL)
- Les templates Thymeleaf reproduisent exactement les JSP

## ✅ Checklist de démarrage

- [ ] Java 21 installé
- [ ] Maven 3.8+ installé
- [ ] MySQL 8.0+ installé et en cours d'exécution
- [ ] Base de données créée (JeeDb.sql exécuté)
- [ ] application.properties configuré
- [ ] Application lancée sur http://localhost:8080
- [ ] Connexion réussie avec admin/admin

Bonne chance ! 🎉
