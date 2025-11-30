package com.controller;

import com.model.Employee;
import com.model.Department;
import com.model.User;
import com.model.Salaire;
import com.model.Absence;
import com.model.Payslip;
import com.model.ProjectAssignment;
import com.service.EmployeeService;
import com.util.SecurityUtil;
import com.repository.EmployeeRepository;
import com.repository.DepartmentRepository;
import com.repository.UserRepository;
import com.repository.SalaireRepository;
import com.repository.AbsenceRepository;
import com.repository.PayslipRepository;
import com.repository.ProjectAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private ProjectAssignmentRepository projectAssignmentRepository;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    public String listEmployees(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer department,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Protéger l'accès - seul l'admin peut voir la liste
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }

        List<Employee> employees = employeeService.searchEmployees(query, grade, position, department);

        List<String> grades = employeeRepository.findDistinctGrades();
        List<String> positions = employeeRepository.findDistinctPositions();
        List<Department> departments = departmentRepository.findAll();

        Map<Integer, String> departmentNames = new HashMap<>();
        Map<Integer, java.sql.Date> firstSalaries = new HashMap<>();
        
        departments.forEach(dept -> departmentNames.put(dept.getId(), dept.getName()));
        
        employees.forEach(emp -> {
            List<Salaire> salaires = salaireRepository.findByEmployeeId(emp.getId());
            if (!salaires.isEmpty()) {
                firstSalaries.put(emp.getId(), salaires.get(0).getDate());
            }
        });

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
    public String viewEmployee(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acc\u00e8s refus\u00e9");
            return "redirect:/permissionDenied";
        }
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "employeeDetail";
        }
        return "redirect:/employee/list";
    }

    @GetMapping("/create/form")
    public String createForm(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acc\u00e8s refus\u00e9");
            return "redirect:/permissionDenied";
        }
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

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmployeeId() != null) {
            Optional<Employee> employee = employeeRepository.findById(user.getEmployeeId());
            if (employee.isPresent()) {
                List<Department> departments = departmentRepository.findAll();
                model.addAttribute("employee", employee.get());
                model.addAttribute("departments", departments);
                return "employeeProfile";
            }
        }
        return "redirect:/employeeDashboard";
    }

    @PostMapping("/EmployeeCreateServlet")
    public String createEmployee(
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String position_title,
            @RequestParam double base_salary,
            @RequestParam(required = false) Integer department_id,
            @RequestParam(required = false) String hire_day,
            @RequestParam(required = false) String hire_month,
            @RequestParam(required = false) String hire_year,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acc\u00e8s refus\u00e9");
            return "redirect:/permissionDenied";
        }

        Employee emp = new Employee();
        emp.setFirstName(first_name);
        emp.setLastName(last_name);
        emp.setGrade(grade != null ? grade : "");
        emp.setPositionTitle(position_title != null ? position_title : "");
        emp.setBaseSalary(base_salary);
        emp.setDepartmentId(department_id != null ? department_id : 0);
        emp.setActive(true);
        
        // Email temporaire pour la première sauvegarde (la DB n'accepte pas null)
        emp.setEmail("temp@entreprise.com");

        // Sauvegarder d'abord pour obtenir l'ID
        Employee savedEmp = employeeRepository.save(emp);
        
        // Générer l'email avec le vrai ID
        String email = first_name.trim().toLowerCase() + "." + last_name.trim().toLowerCase() + savedEmp.getId() + "@entreprise.com";
        savedEmp.setEmail(email);
        employeeRepository.save(savedEmp);

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
    public String editForm(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acc\u00e8s refus\u00e9");
            return "redirect:/permissionDenied";
        }
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
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acc\u00e8s refus\u00e9");
            return "redirect:/permissionDenied";
        }

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
    public String deleteEmployee(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }
        employeeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Employé supprimé avec succès");
        return "redirect:/employee/list";
    }

    @GetMapping("/absences")
    public String viewMyAbsences(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmployeeId() != null) {
            List<Absence> absences = absenceRepository.findByEmployeeId(user.getEmployeeId());
            absences.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            model.addAttribute("absences", absences);
            return "employeeAbsences";
        }
        return "redirect:/employeeDashboard";
    }

    @GetMapping("/payslips")
    public String viewMyPayslips(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmployeeId() != null) {
            List<Payslip> payslips = payslipRepository.findAll().stream()
                    .filter(p -> p.getEmployeeId() == user.getEmployeeId())
                    .collect(Collectors.toList());
            model.addAttribute("payslips", payslips);
            return "employeePayslips";
        }
        return "redirect:/employeeDashboard";
    }

    @GetMapping("/projects")
    public String viewMyProjects(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmployeeId() != null) {
            List<ProjectAssignment> assignments = projectAssignmentRepository.findByEmployeeId(user.getEmployeeId());
            model.addAttribute("assignments", assignments);
            return "employeeProjects";
        }
        return "redirect:/employeeDashboard";
    }
}
