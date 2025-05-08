package tn.esprit.workshop.utils;

import tn.esprit.workshop.models.Utilisateur;

public class SessionManager {
    private static Utilisateur connectedUser;
    
    public static Utilisateur getConnectedUser() {
        return connectedUser;
    }
    
    public static void setConnectedUser(Utilisateur user) {
        connectedUser = user;
    }
    
    public static void cleanUserSession() {
        connectedUser = null;
    }
    
    public static boolean isLoggedIn() {
        return connectedUser != null;
    }
} 