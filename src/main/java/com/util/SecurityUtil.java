package com.util;

import com.model.User;
import jakarta.servlet.http.HttpSession;

/**
 * Utilitaire pour vérifier les permissions de sécurité
 */
public class SecurityUtil {

    /**
     * Vérifie si l'utilisateur est Admin (département RH = 1)
     */
    public static boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getRoleId() == 1;
    }

    /**
     * Vérifie si l'utilisateur est Manager (rôles 2 ou 3)
     */
    public static boolean isManager(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && (user.getRoleId() == 2 || user.getRoleId() == 3);
    }

    /**
     * Vérifie si l'utilisateur est Employé (rôle 4)
     */
    public static boolean isEmployee(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getRoleId() == 4;
    }

    /**
     * Vérifie si l'utilisateur est connecté
     */
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    /**
     * Obtient l'utilisateur actuel
     */
    public static User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
