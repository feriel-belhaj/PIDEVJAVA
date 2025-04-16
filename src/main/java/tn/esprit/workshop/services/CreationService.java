package tn.esprit.workshop.services;

import tn.esprit.workshop.entities.Creation;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreationService {
    private Connection connection;

    public CreationService() {
        connection = MyDbConnexion.getInstance().getCnx();
    }

    // Create a new creation
    public boolean add(Creation creation) {
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