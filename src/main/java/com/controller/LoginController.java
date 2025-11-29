package com.controller;

import com.model.User;
import com.model.Employee;
import com.repository.UserRepository;
import com.repository.EmployeeRepository;
import com.util.PasswordUtil;
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

    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    @PostMapping("/LoginServlet")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Vérifier le mot de passe (storé en clair dans ce projet)
            if (user.getPasswordHash() != null && user.getPasswordHash().equals(password)) {
                session.setAttribute("user", user);

                // Charger l'employé lié
                if (user.getEmployeeId() != null) {
                    Optional<Employee> empOpt = employeeRepository.findById(user.getEmployeeId());
                    if (empOpt.isPresent()) {
                        Employee emp = empOpt.get();
                        session.setAttribute("emp", emp);
                        session.setAttribute("employe", emp);
                        session.setAttribute("employeeId", user.getEmployeeId());
                    }
                }

                // Si firstConnexion est vrai ou mot de passe est "test", forcer le changement de mot de passe
                boolean mustChange = false;
                try {
                    mustChange = user.isFirstConnexion();
                } catch (Throwable ignore) {
                }
                if (mustChange || "test".equals(password)) {
                    return "redirect:/changePassword";
                }

                // Redirection selon rôle
                switch (user.getRoleId()) {
                    case 1: // ADMIN
                        return "redirect:/dashboard";
                    case 2: // DEPT_HEAD
                        return "redirect:/managerDashboard";
                    case 3: // PROJECT_HEAD
                        return "redirect:/projectDashboard";
                    case 4: // EMPLOYEE
                        return "redirect:/employeeDashboard";
                    default:
                        return "redirect:/error";
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Nom d'utilisateur ou mot de passe incorrect");
                return "redirect:/login";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nom d'utilisateur ou mot de passe incorrect");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
