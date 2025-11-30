package com.util;

import com.model.Employee;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;

/**
 * Interceptor pour vérifier les permissions d'accès
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Vérifier si le handler a la méthode avec @RequirePermission
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequirePermission permission = handlerMethod.getMethodAnnotation(RequirePermission.class);

            if (permission != null) {
                // Vérifier les permissions
                PermissionUtil.ValidationEnum validation = checkPermission(request, permission);

                if (validation != PermissionUtil.ValidationEnum.ALLOWED) {
                    // Rediriger vers page d'erreur
                    String redirectUrl;
                    if (validation == PermissionUtil.ValidationEnum.NOT_LOGGED) {
                        redirectUrl = "/" + permission.notLoggedPage();
                    } else {
                        redirectUrl = "/" + permission.deniedPage();
                    }
                    response.sendRedirect(redirectUrl);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Vérifie les permissions basées sur l'annotation
     */
    private PermissionUtil.ValidationEnum checkPermission(HttpServletRequest request, RequirePermission permission) {
        HttpSession session = request.getSession();
        Employee employee = (Employee) session.getAttribute("SESSION_employee");

        if (employee == null) {
            return PermissionUtil.ValidationEnum.NOT_LOGGED;
        }

        // Si pas de restriction, accès autorisé
        if (permission.allowedDepartments().length == 0 && permission.allowedEmployees().length == 0) {
            return PermissionUtil.ValidationEnum.ALLOWED;
        }

        // Vérifier département
        if (permission.allowedDepartments().length > 0) {
            for (int deptId : permission.allowedDepartments()) {
                if (employee.getDepartmentId() != null && employee.getDepartmentId() == deptId) {
                    return PermissionUtil.ValidationEnum.ALLOWED;
                }
            }
        }

        // Vérifier employé spécifique
        if (permission.allowedEmployees().length > 0) {
            for (int empId : permission.allowedEmployees()) {
                if (employee.getId() == empId) {
                    return PermissionUtil.ValidationEnum.ALLOWED;
                }
            }
        }

        return PermissionUtil.ValidationEnum.DENIED;
    }
}
