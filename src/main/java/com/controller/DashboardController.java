package com.controller;

import com.model.User;
import com.model.Employee;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard.jsp")
    public String adminDashboard() {
        return "dashboard";
    }

    @GetMapping("/dashboard")
    public String adminDashboardNewUrl() {
        return "dashboard";
    }

    @GetMapping("/managerDashboard.jsp")
    public String managerDashboard() {
        return "managerDashboard";
    }

    @GetMapping("/managerDashboard")
    public String managerDashboardNewUrl() {
        return "managerDashboard";
    }

    @GetMapping("/projectDashboard.jsp")
    public String projectDashboard() {
        return "managerDashboard";
    }

    @GetMapping("/projectDashboard")
    public String projectDashboardNewUrl() {
        return "managerDashboard";
    }

    @GetMapping("/EmployeeDashboardServlet")
    public String employeeDashboard() {
        return "employeeDashboard";
    }

    @GetMapping("/employeeDashboard")
    public String employeeDashboardNewUrl() {
        return "employeeDashboard";
    }

    @GetMapping("/changePassword.jsp")
    public String changePasswordPageOld() {
        return "changePassword";
    }

    @GetMapping("/changePassword")
    public String changePasswordPage() {
        return "changePassword";
    }

    @PostMapping("/ChangePasswordServlet")
    public String changePassword(
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Les mots de passe ne correspondent pas");
            return "redirect:/changePassword";
        }

        if ("test".equals(newPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vous ne pouvez pas utiliser 'test' comme mot de passe");
            return "redirect:/changePassword";
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aucun utilisateur connecté");
            return "redirect:/login";
        }

        // Mettre à jour le mot de passe dans la base de données
        user.setPasswordHash(newPassword);
        user.setFirstConnexion(false);
        userRepository.save(user);

        // Mettre à jour la session
        session.setAttribute("user", user);

        // Redirection selon le département de l'employé
        Employee emp = (Employee) session.getAttribute("emp");
        if (emp != null && emp.getDepartmentId() != null) {
            // Département 1 = RH/ADMIN, redirection vers dashboard admin
            if (emp.getDepartmentId() == 1) {
                redirectAttributes.addFlashAttribute("message", "Mot de passe changé avec succès");
                return "redirect:/dashboard";
            } else {
                // Autres départements, redirection vers dashboard employé
                redirectAttributes.addFlashAttribute("message", "Mot de passe changé avec succès");
                return "redirect:/employeeDashboard";
            }
        }

        // Fallback : redirection selon le rôle utilisateur
        switch (user.getRoleId()) {
            case 1: // ADMIN
                redirectAttributes.addFlashAttribute("message", "Mot de passe changé avec succès");
                return "redirect:/dashboard";
            case 2: // DEPT_HEAD
                redirectAttributes.addFlashAttribute("message", "Mot de passe changé avec succès");
                return "redirect:/managerDashboard";
            case 3: // PROJECT_HEAD
                redirectAttributes.addFlashAttribute("message", "Mot de passe changé avec succès");
                return "redirect:/projectDashboard";
            case 4: // EMPLOYEE
            default:
                redirectAttributes.addFlashAttribute("message", "Mot de passe changé avec succès");
                return "redirect:/employeeDashboard";
        }
    }
}
