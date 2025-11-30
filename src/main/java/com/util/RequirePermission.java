package com.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour vérifier les permissions d'accès à une page/endpoint
 * 
 * Usage:
 * @RequirePermission(allowedDepartments = {1, 2})
 * public String myPage() { ... }
 * 
 * @RequirePermission(allowedDepartments = {1})
 * public String adminOnly() { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * Liste des ID de départements autorisés
     * Vide = tous les départements
     */
    int[] allowedDepartments() default {};

    /**
     * Liste des ID d'employés autorisés
     * Vide = tous les employés
     */
    int[] allowedEmployees() default {};

    /**
     * Page à afficher si accès refusé
     */
    String deniedPage() default "permissionDenied";

    /**
     * Page à afficher si non connecté
     */
    String notLoggedPage() default "error";
}
