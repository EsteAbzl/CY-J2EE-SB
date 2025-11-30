package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard.jsp")
    public String adminDashboard() {
        return "dashboard";
    }

    @GetMapping("/dashboard")
    public String adminDashboardNew() {
        return "dashboard";
    }

    @GetMapping("/managerDashboard.jsp")
    public String managerDashboard() {
        return "managerDashboard";
    }

    @GetMapping("/managerDashboard")
    public String managerDashboardNew() {
        return "managerDashboard";
    }

    @GetMapping("/projectDashboard.jsp")
    public String projectDashboard() {
        return "managerDashboard";
    }

    @GetMapping("/projectDashboard")
    public String projectDashboardNew() {
        return "managerDashboard";
    }

    @GetMapping("/EmployeeDashboardServlet")
    public String employeeDashboard() {
        return "employeeDashboard";
    }

    @GetMapping("/employeeDashboard")
    public String employeeDashboardNew() {
        return "employeeDashboard";
    }

    @GetMapping("/changePassword.jsp")
    public String changePasswordPage() {
        return "changePassword";
    }

    @GetMapping("/changePassword")
    public String changePasswordPageNew() {
        return "changePassword";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }

    @PostMapping("/ChangePasswordServlet")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Les mots de passe ne correspondent pas");
            return "redirect:/changePassword.jsp";
        }

        // Implémenter la logique de changement de mot de passe ici
        // Pour l'instant, redirection vers le dashboard
        redirectAttributes.addFlashAttribute("message", "Mot de passe changé avec succès");
        return "redirect:/dashboard.jsp";
    }
}
