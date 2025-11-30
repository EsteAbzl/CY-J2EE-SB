package com.service;

import com.model.User;
import com.model.Employee;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Détermine la page de redirection appropriée après login/changement de mot de passe
     */
    public String getRedirectUrl(User user) {
        if (user == null) {
            return "/login";
        }

        Employee emp = null;
        if (user.getEmployeeId() != null) {
            Optional<Employee> empOpt = employeeRepository.findById(user.getEmployeeId());
            if (empOpt.isPresent()) {
                emp = empOpt.get();
            }
        }

        // Vérifier le département en priorité
        if (emp != null && emp.getDepartmentId() != null) {
            if (emp.getDepartmentId() == 1) {
                return "/dashboard";
            }
        }

        // Fallback selon le rôle
        switch (user.getRoleId()) {
            case 1:
                return "/dashboard";
            case 2:
                return "/managerDashboard";
            case 3:
                return "/managerDashboard";
            case 4:
            default:
                return "/employeeDashboard";
        }
    }
}
