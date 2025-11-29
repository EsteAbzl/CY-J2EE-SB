package com.controller;

import com.model.Employee;
import com.model.Department;
import com.model.Salaire;
import com.repository.EmployeeRepository;
import com.repository.DepartmentRepository;
import com.repository.SalaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SalaireRepository salaireRepository;

    @GetMapping("/list")
    public String listEmployees(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer department,
            Model model) {

        List<Employee> employees;
        
        // Vérifier si au moins un filtre est défini et non-vide
        boolean hasFilters = (query != null && !query.trim().isEmpty()) || 
                           (grade != null && !grade.trim().isEmpty()) || 
                           (position != null && !position.trim().isEmpty()) || 
                           department != null;
        
        if (hasFilters) {
            // Convertir les chaînes vides en null pour la requête
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
        model.addAttribute("departmentNames", departmentNames);
        model.addAttribute("firstSalaries", firstSalaries);
        model.addAttribute("grades", grades);
        model.addAttribute("positions", positions);
        model.addAttribute("departments", departments);
        
        // Garder les valeurs des filtres après la recherche
        if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("searchQuery", query);
        }
        if (grade != null && !grade.trim().isEmpty()) {
            model.addAttribute("selectedGrade", grade);
        }
        if (position != null && !position.trim().isEmpty()) {
            model.addAttribute("selectedPosition", position);
        }
        if (department != null) {
            model.addAttribute("selectedDepartment", department);
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

    @PostMapping("/EmployeeCreateServlet")
    public String createEmployee(
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String email,
            @RequestParam String grade,
            @RequestParam String position_title,
            @RequestParam double base_salary,
            @RequestParam(required = false) Integer department_id,
            @RequestParam(required = false) Integer hire_day,
            @RequestParam(required = false) Integer hire_month,
            @RequestParam(required = false) Integer hire_year,
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

        employeeRepository.save(emp);

        // Créer l'enregistrement du salaire avec la date d'embauche
        if (hire_day != null && hire_month != null && hire_year != null && 
            hire_day > 0 && hire_month > 0 && hire_month <= 12 && hire_year > 0) {
            Salaire salaire = new Salaire();
            salaire.setEmployeeId(emp.getId());
            salaire.setSalaire(base_salary);
            
            // Créer la date SQL (année-mois-jour)
            String dateString = String.format("%04d-%02d-%02d", hire_year, hire_month, hire_day);
            try {
                java.sql.Date hireDate = java.sql.Date.valueOf(dateString);
                salaire.setDate(hireDate);
                salaireRepository.save(salaire);
            } catch (IllegalArgumentException e) {
                // Date invalide, ignorer
            }
        }

        redirectAttributes.addFlashAttribute("message", "Employé créé avec succès");
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

    @GetMapping("/getNextId")
    @ResponseBody
    public Map<String, Integer> getNextId() {
        List<Employee> employees = employeeRepository.findAll();
        int nextId = employees.isEmpty() ? 1 : employees.stream()
                .mapToInt(Employee::getId)
                .max()
                .orElse(0) + 1;
        
        Map<String, Integer> response = new HashMap<>();
        response.put("nextId", nextId);
        return response;
    }

    @GetMapping("/deactivate/form")
    public String deactivateForm(Model model) {
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        return "deactivateEmployee";
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            Employee emp = employeeOpt.get();
            emp.setActive(false);
            employeeRepository.save(emp);
            redirectAttributes.addFlashAttribute("message", "Employé désactivé avec succès");
        } else {
            redirectAttributes.addFlashAttribute("error", "Employé non trouvé");
        }
        return "redirect:/employee/list";
    }
}
