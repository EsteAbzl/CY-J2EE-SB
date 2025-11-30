package com.controller;

import com.model.Employee;
import com.model.Department;
import com.model.User;
import com.repository.EmployeeRepository;
import com.repository.DepartmentRepository;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import com.model.Salaire;
import com.repository.SalaireRepository;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.Date;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SalaireRepository salaireRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public String listEmployees(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer department,
            Model model) {

        List<Employee> employees;
        
        if (query != null || grade != null || position != null || department != null) {
            String queryParam = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
            String gradeParam = (grade != null && !grade.trim().isEmpty()) ? grade.trim() : null;
            String positionParam = (position != null && !position.trim().isEmpty()) ? position.trim() : null;
            
            employees = employeeRepository.search(queryParam, gradeParam, positionParam, department);
        } else {
            employees = employeeRepository.findAll();
        }

        // Filtrer pour afficher SEULEMENT les employés actifs
        employees = employees.stream()
                .filter(Employee::isActive)
                .collect(Collectors.toList());

        List<String> grades = employeeRepository.findDistinctGrades();
        List<String> positions = employeeRepository.findDistinctPositions();
        List<Department> departments = departmentRepository.findAll();

        // Créer une map pour les salaires et départements
        Map<Integer, String> departmentNames = new HashMap<>();
        Map<Integer, java.sql.Date> firstSalaries = new HashMap<>();
        
        for (Department dept : departments) {
            departmentNames.put(dept.getId(), dept.getName());
        }
        
        for (Employee emp : employees) {
            List<Salaire> salaires = salaireRepository.findByEmployeeId(emp.getId());
            if (!salaires.isEmpty()) {
                Salaire firstSalaire = salaires.get(0);
                firstSalaries.put(emp.getId(), firstSalaire.getDate());
            }
        }

        model.addAttribute("employees", employees);
        model.addAttribute("grades", grades);
        model.addAttribute("positions", positions);
        model.addAttribute("departments", departments);
        model.addAttribute("departmentNames", departmentNames);
        model.addAttribute("firstSalaries", firstSalaries);
        if (query != null) {
            model.addAttribute("searchQuery", query);
        }

        return "employeesList";
    }

    @GetMapping("/{id}")
    public String viewEmployee(@PathVariable Integer id, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "employeeDetail";
        }
        return "redirect:/employee/list";
    }

    @GetMapping("/create/form")
    public String createForm(Model model) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("employee", new Employee());
        return "addEmployee";
    }

    @GetMapping("/getNextId")
    @ResponseBody
    public Map<String, Integer> getNextId() {
        // Récupérer le plus grand ID existant
        List<Employee> allEmployees = employeeRepository.findAll();
        Integer maxId = allEmployees.stream()
                .map(Employee::getId)
                .max(Integer::compare)
                .orElse(0);
        
        Map<String, Integer> response = new HashMap<>();
        response.put("nextId", maxId + 1);
        return response;
    }

    @PostMapping("/EmployeeCreateServlet")
    public String createEmployee(
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String email,
            @RequestParam String grade,
            @RequestParam String position_title,
            @RequestParam double base_salary,
            @RequestParam(required = false) Integer department_id,
            @RequestParam(required = false) String hire_day,
            @RequestParam(required = false) String hire_month,
            @RequestParam(required = false) String hire_year,
            RedirectAttributes redirectAttributes) {

        Employee emp = new Employee();
        emp.setFirstName(first_name);
        emp.setLastName(last_name);
        emp.setEmail(email);
        emp.setGrade(grade);
        emp.setPositionTitle(position_title);
        emp.setBaseSalary(base_salary);
        emp.setDepartmentId(department_id != null ? department_id : 0);
        emp.setActive(true);

        Employee savedEmp = employeeRepository.save(emp);

        // Créer une entrée Salaire avec la date d'embauche
        if (hire_day != null && !hire_day.isEmpty() && 
            hire_month != null && !hire_month.isEmpty() && 
            hire_year != null && !hire_year.isEmpty()) {
            try {
                int day = Integer.parseInt(hire_day);
                int month = Integer.parseInt(hire_month);
                int year = Integer.parseInt(hire_year);
                
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(year, month - 1, day);
                java.sql.Date hireDate = new java.sql.Date(cal.getTimeInMillis());
                
                Salaire salaire = new Salaire();
                salaire.setEmployeeId(savedEmp.getId());
                salaire.setDate(hireDate);
                salaire.setSalaire(base_salary);
                salaireRepository.save(salaire);
            } catch (Exception e) {
                // Si erreur, on continue sans créer l'entrée salaire
            }
        }

        // Créer un utilisateur associé à cet employé
        User user = new User();
        user.setUsername(email);
        user.setPasswordHash("test"); // Mot de passe par défaut
        user.setFullName(first_name + " " + last_name);
        user.setRoleId(4); // 4 = EMPLOYEE par défaut
        user.setActive(true);
        user.setEmployeeId(savedEmp.getId());
        user.setFirstConnexion(true); // L'utilisateur doit changer son mot de passe à la première connexion

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "Employé créé avec succès et compte utilisateur créé (mot de passe temporaire: test)");
        return "redirect:/employee/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            List<Department> departments = departmentRepository.findAll();
            model.addAttribute("employee", employee.get());
            model.addAttribute("departments", departments);
            return "editEmployee";
        }
        return "redirect:/employee/list";
    }

    @PostMapping("/{id}/update")
    public String updateEmployee(
            @PathVariable Integer id,
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String email,
            @RequestParam String grade,
            @RequestParam String position_title,
            @RequestParam double base_salary,
            @RequestParam(required = false) Integer department_id,
            RedirectAttributes redirectAttributes) {

        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            Employee emp = employeeOpt.get();
            emp.setFirstName(first_name);
            emp.setLastName(last_name);
            emp.setEmail(email);
            emp.setGrade(grade);
            emp.setPositionTitle(position_title);
            emp.setBaseSalary(base_salary);
            emp.setDepartmentId(department_id);

            employeeRepository.save(emp);
            redirectAttributes.addFlashAttribute("message", "Employé mis à jour avec succès");
        }

        return "redirect:/employee/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        employeeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Employé supprimé avec succès");
        return "redirect:/employee/list";
    }
}
