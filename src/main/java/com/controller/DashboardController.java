package com.controller;

import com.model.User;
import com.service.AuthService;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping({"/dashboard.jsp", "/dashboard"})
    public String adminDashboard() {
        return "dashboard";
    }

    @GetMapping({"/managerDashboard.jsp", "/managerDashboard", "/projectDashboard.jsp", "/projectDashboard"})
    public String managerDashboard() {
        return "managerDashboard";
    }

    @GetMapping({"/EmployeeDashboardServlet", "/employeeDashboard"})
    public String employeeDashboard() {
        return "employeeDashboard";
    }

    @GetMapping({"/changePassword.jsp", "/changePassword"})
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

        user.setPasswordHash(newPassword);
        user.setFirstConnexion(false);
        userRepository.save(user);
        session.setAttribute("user", user);

        redirectAttributes.addFlashAttribute("message", "Mot de passe changé avec succès");
        return "redirect:" + authService.getRedirectUrl(user);
    }
}
