package tn.esprit.workshop.models;

import io.jsonwebtoken.Claims;

public class SessionManager {
    private static String token;

    public static void setToken(String jwt) {
        token = jwt;
    }

    public static String getToken() {
        return token;
    }

    public static Claims getUserClaims() {
        return JwtUtils.decodeToken(token);
    }

    public static int getUserId() {
        return Integer.parseInt(getUserClaims().getSubject());
    }

    public static String getUserEmail() {
        return getUserClaims().get("email", String.class);
    }

    public static String getUserNom() {
        return getUserClaims().get("nom", String.class);
    }
    public static String getUserPreom() {
        return getUserClaims().get("prenom", String.class);
    }
    public static String getPath() {
        return getUserClaims().get("image", String.class);
    }


    public static void clearSession() {
        token = null;
    }
}
