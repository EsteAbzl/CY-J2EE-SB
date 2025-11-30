-- Schema: hrms
DROP DATABASE IF EXISTS JeeDb;
CREATE DATABASE IF NOT EXISTS JeeDb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE JeeDb;


DROP TABLE IF EXISTS salaire_extra;
DROP TABLE IF EXISTS salaire;
DROP TABLE IF EXISTS absences;
DROP TABLE IF EXISTS payslips;
DROP TABLE IF EXISTS project_assignments;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS roles;


-- Roles
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       `name` VARCHAR(50) NOT NULL UNIQUE -- ADMIN, RH, DEPT_HEAD, PROJECT_HEAD, EMPLOYEE
) ENGINE=InnoDB;

-- Departments
CREATE TABLE departments (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             `name` VARCHAR(150) NOT NULL UNIQUE,
                             `description` TEXT
) ENGINE=InnoDB;

-- Employees
CREATE TABLE employees (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(150) NOT NULL UNIQUE,
                           grade VARCHAR(50),
                           position_title VARCHAR(100),
                           base_salary DECIMAL(10,2) NOT NULL,
                           department_id INT,
                           `active` TINYINT(1) DEFAULT 1,
                           FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(150) NOT NULL,
                       first_connexion TINYINT(1) NOT NULL DEFAULT 0,
                       role_id INT NOT NULL,
                       `active` TINYINT(1) NOT NULL DEFAULT 1,
                       employee_id INT,
                       CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
                       CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
                           ON UPDATE CASCADE
                           ON DELETE SET NULL
) ENGINE=InnoDB;

-- Projects
CREATE TABLE projects (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          `name` VARCHAR(150) NOT NULL UNIQUE,
                          `description` TEXT,
                          `status` ENUM('EN_COURS','TERMINE','ANNULE') DEFAULT 'EN_COURS',
                          start_date DATE,
                          end_date DATE,
                          department_id INT,
                          FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB;

-- Employee assignments to projects (many-to-many)
CREATE TABLE project_assignments (
                                     project_id INT NOT NULL,
                                     employee_id INT NOT NULL,
                                     role_in_project VARCHAR(100),
                                     PRIMARY KEY (project_id, employee_id),
                                     FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                                     FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Payslips
CREATE TABLE payslips (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          employee_id INT NOT NULL,
                          period_year INT NOT NULL,
                          period_month INT NOT NULL, -- 1..12
                          base_salary DECIMAL(10,2) NOT NULL,
                          bonuses DECIMAL(10,2) DEFAULT 0.00,
                          deductions DECIMAL(10,2) DEFAULT 0.00,
                          net_pay DECIMAL(10,2) NOT NULL,
                          generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE KEY uniq_emp_period (employee_id, period_year, period_month),
                          FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB;

-- Absences (optional for deductions logic)
CREATE TABLE absences (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          employee_id INT NOT NULL,
                          `date` DATE NOT NULL,
                          `type` ENUM('CONGE','MALADIE','NON_PAYE') DEFAULT 'NON_PAYE',
                          hours INT DEFAULT 8,
                          FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB;

-- Salaire table
CREATE TABLE salaire (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         salaire DECIMAL(10,2),
                         `date` DATE NOT NULL,
                         employee_id INT NOT NULL,
                         FOREIGN KEY (employee_id) REFERENCES employees(id)
)ENGINE=InnoDB;

-- Salaire Extra table
CREATE TABLE salaire_extra (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               montant DECIMAL(10,2),
                               motif VARCHAR(250),
                               `date` DATE NOT NULL,
                               employee_id INT NOT NULL,
                               FOREIGN KEY (employee_id) REFERENCES employees(id)
)ENGINE=InnoDB;

-- Seed roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('DEPT_HEAD'), ('PROJECT_HEAD'), ('EMPLOYEE');

-- Example department
INSERT INTO departments (id, name, description)
VALUES (1, 'Ressources Humaines', 'Département responsable de la gestion des ressources humaines');

-- Example employee

INSERT INTO employees (id, first_name, last_name, email, grade, position_title, base_salary, department_id, active)
VALUES
    (1, 'admin', '', '', 'admin', 'Responsable RH', 3500.00, 1, 0),
    (2, 'Jean', 'Dupont', 'jean.dupont2@entreprise.com', 'Senior', 'Responsable RH', 3500.00, 1, 1);

-- Example admin user (password: admin123 hashed with BCrypt placeholder; replace in real)
-- admin user (first_connexion = 0)
INSERT INTO users (id, username, password_hash, full_name, first_connexion, role_id, employee_id)
VALUES (1, 'admin', 'admin', 'Administrateur', 0, 1, 1),
       (2, 'jean.dupont2@entreprise.com', 'test', 'jean dupont', 1, 1, 2);

-- Example salaire (salary history for employee 1)
INSERT INTO salaire (salaire, date, employee_id) VALUES
                                                     (3500.00, '2025-01-15', 2),
                                                     (3500.00, '2025-06-15', 2),
                                                     (3500.00, '2025-06-15', 2),
                                                     (3750.00, '2025-11-01', 2);

-- Example salaire_extra (bonuses and deductions for employee 1 in November 2025)
INSERT INTO salaire_extra (montant, motif, date, employee_id) VALUES
                                                                  (500.00, 'Prime de performance', '2025-11-10', 2),
                                                                  (250.00, 'Bonus d''équipe', '2025-11-15', 2),
                                                                  (-100.00, 'Retenue pour absence', '2025-11-05', 2),
                                                                  (-50.00, 'Avance sur salaire', '2025-11-12', 2);


