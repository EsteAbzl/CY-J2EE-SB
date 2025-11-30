package com.controller;

import com.model.User;
import com.model.Employee;
import com.service.AuthService;
import com.repository.UserRepository;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    @PostMapping("/LoginServlet")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (!userOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nom d'utilisateur ou mot de passe incorrect");
            return "redirect:/login";
        }

        User user = userOpt.get();
        
        if (!user.getPasswordHash().equals(password)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nom d'utilisateur ou mot de passe incorrect");
            return "redirect:/login";
        }

        // Store user in session
        session.setAttribute("user", user);

        // Charger l'employé associé et le stocker UNE SEULE FOIS
        if (user.getEmployeeId() != null) {
            Optional<Employee> empOpt = employeeRepository.findById(user.getEmployeeId());
            if (empOpt.isPresent()) {
                Employee emp = empOpt.get();
                session.setAttribute("SESSION_employee", emp);
            }
        }

        // Forcer le changement de mot de passe si first connexion ou password = "test"
        if (user.isFirstConnexion() || "test".equals(password)) {
            session.setAttribute("firstConnexionRequired", true);
            return "redirect:/changePassword";
        }

        redirectAttributes.addFlashAttribute("message", "Connecté avec succès");
        return "redirect:" + authService.getRedirectUrl(user);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
