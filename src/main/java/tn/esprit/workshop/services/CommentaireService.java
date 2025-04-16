package tn.esprit.workshop.services;

import tn.esprit.workshop.entities.Commentaire;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService {
    private Connection connection;

    public CommentaireService() {
        connection = MyDbConnexion.getInstance().getCnx();
    }

    // Create a new comment
    public boolean add(Commentaire commentaire) {
        // Validate commentaire before adding to database
        List<String> validationErrors = commentaire.validate();
        if (!validationErrors.isEmpty()) {
            System.err.println("Commentaire validation failed:");
            for (String error : validationErrors) {
                System.err.println("- " + error);
            }
            return false;
        }
        
        String query = "INSERT INTO commentaire (creation_id, utilisateur_id, contenu, date_comment, etat) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, commentaire.getCreationId());
            
            if (commentaire.getUtilisateurId() != null) {
                statement.setInt(2, commentaire.getUtilisateurId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            
            statement.setString(3, commentaire.getContenu());
            statement.setTimestamp(4, Timestamp.valueOf(commentaire.getDateComment()));
            statement.setString(5, commentaire.getEtat());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    commentaire.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding comment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing comment
    public boolean update(Commentaire commentaire) {
        // Validate commentaire before updating in database
        List<String> validationErrors = commentaire.validate();
        if (!validationErrors.isEmpty()) {
            System.err.println("Commentaire validation failed:");
            for (String error : validationErrors) {
                System.err.println("- " + error);
            }
            return false;
        }
        
        String query = "UPDATE commentaire SET creation_id = ?, utilisateur_id = ?, " +
                      "contenu = ?, date_comment = ?, etat = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, commentaire.getCreationId());
            
            if (commentaire.getUtilisateurId() != null) {
                statement.setInt(2, commentaire.getUtilisateurId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            
            statement.setString(3, commentaire.getContenu());
            statement.setTimestamp(4, Timestamp.valueOf(commentaire.getDateComment()));
            statement.setString(5, commentaire.getEtat());
            statement.setInt(6, commentaire.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating comment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Delete a comment
    public boolean delete(int id) {
        String query = "DELETE FROM commentaire WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Get a comment by ID
    public Commentaire getById(int id) {
        String query = "SELECT * FROM commentaire WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToCommentaire(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error getting comment by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get all comments
    public List<Commentaire> getAll() {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaire";
        
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            
            while (resultSet.next()) {
                Commentaire commentaire = mapResultSetToCommentaire(resultSet);
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all comments: " + e.getMessage());
            e.printStackTrace();
        }
        return commentaires;
    }

    // Get comments by creation ID
    public List<Commentaire> getByCreationId(int creationId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaire WHERE creation_id = ? ORDER BY date_comment DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, creationId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Commentaire commentaire = mapResultSetToCommentaire(resultSet);
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.err.println("Error getting comments by creation id: " + e.getMessage());
            e.printStackTrace();
        }
        return commentaires;
    }

    // Get comments by user ID
    public List<Commentaire> getByUserId(int userId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaire WHERE utilisateur_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Commentaire commentaire = mapResultSetToCommentaire(resultSet);
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.err.println("Error getting comments by user id: " + e.getMessage());
            e.printStackTrace();
        }
        return commentaires;
    }
    
    // Get active comments
    public List<Commentaire> getActiveComments() {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaire WHERE etat = 'actif'";
        
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            
            while (resultSet.next()) {
                Commentaire commentaire = mapResultSetToCommentaire(resultSet);
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active comments: " + e.getMessage());
            e.printStackTrace();
        }
        return commentaires;
    }

    // Update comment status
    public boolean updateStatus(int id, String etat) {
        String query = "UPDATE commentaire SET etat = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, etat);
            statement.setInt(2, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating comment status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to map ResultSet to Commentaire object
    private Commentaire mapResultSetToCommentaire(ResultSet resultSet) throws SQLException {
        Commentaire commentaire = new Commentaire();
        commentaire.setId(resultSet.getInt("id"));
        commentaire.setCreationId(resultSet.getInt("creation_id"));
        
        // Handle nullable utilisateur_id
        int utilisateurId = resultSet.getInt("utilisateur_id");
        if (!resultSet.wasNull()) {
            commentaire.setUtilisateurId(utilisateurId);
        } else {
            commentaire.setUtilisateurId(null);
        }
        
        commentaire.setContenu(resultSet.getString("contenu"));
        Timestamp timestamp = resultSet.getTimestamp("date_comment");
        if (timestamp != null) {
            commentaire.setDateComment(timestamp.toLocalDateTime());
        }
        commentaire.setEtat(resultSet.getString("etat"));
        return commentaire;
    }
}