package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Creation;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CreationService {
    private Connection connection;
    private static final String PROFANITY_API_URL = "https://vector.profanity.dev";

    public CreationService() {
        connection = MyDbConnexion.getInstance().getCnx();
    }
    
    /**
     * Check if the provided text contains profanity using the profanity API
     * 
     * @param text The text to check for profanity
     * @return true if profanity is detected, false otherwise
     * @throws IOException if there's an issue with the API call
     * @throws InterruptedException if the API call is interrupted
     */
    private boolean containsProfanity(String text) throws IOException, InterruptedException {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        // Create the JSON request body
        String requestBody = "{\"message\":\"" + text.replace("\"", "\\\"") + "\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PROFANITY_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Check if the API detected profanity
        return response.statusCode() == 200 && response.body().contains("true");
    }

    // Create a new creation
    public boolean add(Creation creation) {
        // Check for profanity in title and description
        try {
            if (containsProfanity(creation.getTitre()) || containsProfanity(creation.getDescription())) {
                System.err.println("Creation contains profanity and was rejected");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Warning: Could not check for profanity: " + e.getMessage());
            // Continue with the operation even if profanity check fails
        }
        
        // Validate creation before adding to database
        List<String> validationErrors = creation.validate();
        if (!validationErrors.isEmpty()) {
            System.err.println("Creation validation failed:");
            for (String error : validationErrors) {
                System.err.println("- " + error);
            }
            return false;
        }
        
        String query = "INSERT INTO creation (utilisateur_id, titre, description, image, categorie, date_public, statut, nb_like) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, creation.getUtilisateurId());
            statement.setString(2, creation.getTitre());
            statement.setString(3, creation.getDescription());
            statement.setString(4, creation.getImage());
            statement.setString(5, creation.getCategorie());
            statement.setTimestamp(6, Timestamp.valueOf(creation.getDatePublic()));
            statement.setString(7, creation.getStatut());
            statement.setInt(8, creation.getNbLike());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    creation.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding creation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing creation
    public boolean update(Creation creation) {
        // Check for profanity in title and description
        try {
            if (containsProfanity(creation.getTitre()) || containsProfanity(creation.getDescription())) {
                System.err.println("Creation contains profanity and was rejected");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Warning: Could not check for profanity: " + e.getMessage());
            // Continue with the operation even if profanity check fails
        }
        
        // Validate creation before updating in database
        List<String> validationErrors = creation.validate();
        if (!validationErrors.isEmpty()) {
            System.err.println("Creation validation failed:");
            for (String error : validationErrors) {
                System.err.println("- " + error);
            }
            return false;
        }
        
        String query = "UPDATE creation SET utilisateur_id = ?, titre = ?, description = ?, " +
                      "image = ?, categorie = ?, date_public = ?, statut = ?, nb_like = ? " +
                      "WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, creation.getUtilisateurId());
            statement.setString(2, creation.getTitre());
            statement.setString(3, creation.getDescription());
            statement.setString(4, creation.getImage());
            statement.setString(5, creation.getCategorie());
            statement.setTimestamp(6, Timestamp.valueOf(creation.getDatePublic()));
            statement.setString(7, creation.getStatut());
            statement.setInt(8, creation.getNbLike());
            statement.setInt(9, creation.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating creation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Delete a creation
    public boolean delete(int id) {
        String query = "DELETE FROM creation WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting creation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Get a creation by ID
    public Creation getById(int id) {
        String query = "SELECT * FROM creation WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToCreation(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error getting creation by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get all creations
    public List<Creation> getAll() {
        List<Creation> creations = new ArrayList<>();
        String query = "SELECT * FROM creation";
        
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            
            while (resultSet.next()) {
                Creation creation = mapResultSetToCreation(resultSet);
                creations.add(creation);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all creations: " + e.getMessage());
            e.printStackTrace();
        }
        return creations;
    }

    // Get creations by user ID
    public List<Creation> getByUserId(int userId) {
        List<Creation> creations = new ArrayList<>();
        String query = "SELECT * FROM creation WHERE utilisateur_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Creation creation = mapResultSetToCreation(resultSet);
                creations.add(creation);
            }
        } catch (SQLException e) {
            System.err.println("Error getting creations by user id: " + e.getMessage());
            e.printStackTrace();
        }
        return creations;
    }

    // Get creations by category
    public List<Creation> getByCategorie(String categorie) {
        List<Creation> creations = new ArrayList<>();
        String query = "SELECT * FROM creation WHERE categorie = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categorie);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Creation creation = mapResultSetToCreation(resultSet);
                creations.add(creation);
            }
        } catch (SQLException e) {
            System.err.println("Error getting creations by category: " + e.getMessage());
            e.printStackTrace();
        }
        return creations;
    }

    // Get active creations
    public List<Creation> getActiveCreations() {
        List<Creation> creations = new ArrayList<>();
        String query = "SELECT * FROM creation WHERE statut = 'actif'";
        
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            
            while (resultSet.next()) {
                Creation creation = mapResultSetToCreation(resultSet);
                creations.add(creation);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active creations: " + e.getMessage());
            e.printStackTrace();
        }
        return creations;
    }

    // Increment like count
    public boolean incrementLike(int id) {
        String query = "UPDATE creation SET nb_like = nb_like + 1 WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error incrementing like: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to map ResultSet to Creation object
    private Creation mapResultSetToCreation(ResultSet resultSet) throws SQLException {
        Creation creation = new Creation();
        creation.setId(resultSet.getInt("id"));
        creation.setUtilisateurId(resultSet.getInt("utilisateur_id"));
        creation.setTitre(resultSet.getString("titre"));
        creation.setDescription(resultSet.getString("description"));
        creation.setImage(resultSet.getString("image"));
        creation.setCategorie(resultSet.getString("categorie"));
        Timestamp timestamp = resultSet.getTimestamp("date_public");
        if (timestamp != null) {
            creation.setDatePublic(timestamp.toLocalDateTime());
        }
        creation.setStatut(resultSet.getString("statut"));
        creation.setNbLike(resultSet.getInt("nb_like"));
        return creation;
    }
}