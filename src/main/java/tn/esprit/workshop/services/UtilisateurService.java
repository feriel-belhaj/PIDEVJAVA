package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Utilisateur;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;

public class UtilisateurService {
    private Connection connection;
    private static Utilisateur currentUser;

    public UtilisateurService() {
        connection = MyDbConnexion.getInstance().getCnx();
        // Simuler un utilisateur connecté
        if (currentUser == null) {
            currentUser = new Utilisateur();
            currentUser.setId(1);
            currentUser.setNom("Test");
            currentUser.setPrenom("User");
            currentUser.setEmail("test@example.com");
            currentUser.setRole("CLIENT");
        }
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    public Utilisateur login(String email, String password) {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND password = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(resultSet.getInt("id"));
                user.setNom(resultSet.getString("nom"));
                user.setPrenom(resultSet.getString("prenom"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setAdresse(resultSet.getString("adresse"));
                user.setTelephone(resultSet.getString("telephone"));
                user.setDateInscription(resultSet.getTimestamp("date_inscription").toLocalDateTime());
                user.setImage(resultSet.getString("image"));
                user.setRole(resultSet.getString("role"));
                user.setSexe(resultSet.getString("sexe"));
                
                setCurrentUser(user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }
    
    /**
     * Get a username by user ID
     * @param userId The ID of the user to find
     * @return The username (firstName + lastName) of the user, or "Unknown User" if not found
     */
    public String getUsernameById(int userId) {
        String query = "SELECT nom, prenom FROM utilisateur WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                return prenom + " " + nom;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Utilisateur #" + userId;
    }
    
    /**
     * Get a username by user ID (Integer wrapper for nullable IDs)
     * @param userId The ID of the user to find, may be null
     * @return The username (firstName + lastName) of the user, or "Anonymous" if ID is null or user not found
     */
    public String getUsernameById(Integer userId) {
        if (userId == null) {
            return "Anonyme";
        }
        return getUsernameById(userId.intValue());
    }
}