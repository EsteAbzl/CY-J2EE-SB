package com.util;

import com.model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PermissionUtil {
    public enum ValidationEnum {
        ALLOWED, NOT_LOGGED, DENIED
    }

    /**
     * Vérifie si l'utilisateur est connecté (sans restrictions)
     */
    public static ValidationEnum isConnexionAllowed(HttpServletRequest req) {
        return isConnexionAllowed(req, new Integer[]{});
    }

    /**
     * Vérifie si l'utilisateur est connecté et dans les départements autorisés
     */
    public static ValidationEnum isConnexionAllowed(HttpServletRequest req, Integer[] allowedDepartmentIds) {
        return isConnexionAllowed(req, allowedDepartmentIds, new Integer[]{});
    }

    /**
     * Vérifie si l'utilisateur est connecté et dans les départements/employés autorisés
     */
    public static ValidationEnum isConnexionAllowed(HttpServletRequest req, Integer[] allowedDepartmentIds, Integer[] allowedEmployeeIds) {
        ArrayList<Integer> DepartmentIds = new ArrayList<>(Arrays.asList(allowedDepartmentIds));
        ArrayList<Integer> EmployeeIds = new ArrayList<>(Arrays.asList(allowedEmployeeIds));

        ValidationEnum res = ValidationEnum.DENIED;

        HttpSession session = req.getSession();
        Employee employee = (Employee) session.getAttribute("SESSION_employee");

        if (employee != null) {
            // Vérifier si département est autorisé (vide = tous les départements)
            if (DepartmentIds.isEmpty() || DepartmentIds.contains(employee.getDepartmentId())) {
                res = ValidationEnum.ALLOWED;
            }

            // Vérifier si l'employé spécifique est autorisé
            if (EmployeeIds.contains(employee.getId())) {
                res = ValidationEnum.ALLOWED;
            }
        } else {
            res = ValidationEnum.NOT_LOGGED;
        }

        return res;
    }

    /**
     * Gère la redirection selon le résultat de validation
     */
    public static void manageConnexionPermission(HttpServletRequest req, HttpServletResponse resp, ValidationEnum v) throws IOException, ServletException {
        manageConnexionPermission(req, resp, v, "permissionDenied", "error");
    }

    /**
     * Gère la redirection avec pages personnalisées
     */
    public static void manageConnexionPermission(HttpServletRequest req, HttpServletResponse resp, ValidationEnum v, String pageDenied, String pageNotLogged) throws IOException, ServletException {
        switch (v) {
            case DENIED:
                req.getRequestDispatcher("/" + pageDenied).forward(req, resp);
                break;
            case NOT_LOGGED:
                req.getRequestDispatcher("/" + pageNotLogged).forward(req, resp);
                break;
            case ALLOWED:
                // Déjà traité avant l'appel
                break;
        }
    }
}
