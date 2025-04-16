package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IEvenementService {

    private Connection connection;
    
    public EvenementService() {
        connection = MyDbConnexion.getInstance().getCnx();
    }
    
    @Override
    public void ajouter(Evenement evenement) {
        String query = "INSERT INTO evenement (titre, description, startdate, enddate, localisation, " +
                       "goalamount, collectedamount, status, imageurl, createdat, categorie, createur_id) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, evenement.getTitre());
            pst.setString(2, evenement.getDescription());
            pst.setTimestamp(3, evenement.getStartdate() != null ? Timestamp.valueOf(evenement.getStartdate()) : null);
            pst.setTimestamp(4, evenement.getEnddate() != null ? Timestamp.valueOf(evenement.getEnddate()) : null);
            pst.setString(5, evenement.getLocalisation());
            pst.setDouble(6, evenement.getGoalamount());
            pst.setDouble(7, evenement.getCollectedamount());
            pst.setString(8, evenement.getStatus());
            pst.setString(9, evenement.getImageurl());
            pst.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(11, evenement.getCategorie());
            pst.setInt(12, evenement.getCreateur_id());
            
            pst.executeUpdate();
            
            // Get the generated ID and set it to the evenement object
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    evenement.setId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Evenement ajouté avec succès!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Evenement evenement) {
        String query = "UPDATE evenement SET titre = ?, description = ?, startdate = ?, enddate = ?, " +
                       "localisation = ?, goalamount = ?, collectedamount = ?, status = ?, imageurl = ?, " +
                       "categorie = ?, createur_id = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, evenement.getTitre());
            pst.setString(2, evenement.getDescription());
            pst.setTimestamp(3, evenement.getStartdate() != null ? Timestamp.valueOf(evenement.getStartdate()) : null);
            pst.setTimestamp(4, evenement.getEnddate() != null ? Timestamp.valueOf(evenement.getEnddate()) : null);
            pst.setString(5, evenement.getLocalisation());
            pst.setDouble(6, evenement.getGoalamount());
            pst.setDouble(7, evenement.getCollectedamount());
            pst.setString(8, evenement.getStatus());
            pst.setString(9, evenement.getImageurl());
            pst.setString(10, evenement.getCategorie());
            pst.setInt(11, evenement.getCreateur_id());
            pst.setInt(12, evenement.getId());
            
            pst.executeUpdate();
            System.out.println("Evenement modifié avec succès!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String query = "DELETE FROM evenement WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Evenement supprimé avec succès!");
            } else {
                System.out.println("Aucun evenement trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Evenement getById(int id) {
        String query = "SELECT * FROM evenement WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return extractEvenementFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Evenement> getAll() {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                evenements.add(extractEvenementFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return evenements;
    }

    @Override
    public List<Evenement> getByStatus(String status) {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE status = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, status);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    evenements.add(extractEvenementFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return evenements;
    }

    @Override
    public List<Evenement> getByCategorie(String categorie) {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE categorie = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, categorie);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    evenements.add(extractEvenementFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return evenements;
    }

    @Override
    public List<Evenement> getByCreateurId(int createurId) {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE createur_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, createurId);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    evenements.add(extractEvenementFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return evenements;
    }

    @Override
    public void updateCollectedAmount(int evenementId, double newAmount) {
        String query = "UPDATE evenement SET collectedamount = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setDouble(1, newAmount);
            pst.setInt(2, evenementId);
            
            pst.executeUpdate();
            System.out.println("Montant collecté mis à jour pour l'événement ID: " + evenementId);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    // Helper method to extract evenement from ResultSet
    private Evenement extractEvenementFromResultSet(ResultSet rs) throws SQLException {
        Evenement evenement = new Evenement();
        evenement.setId(rs.getInt("id"));
        evenement.setTitre(rs.getString("titre"));
        evenement.setDescription(rs.getString("description"));
        
        Timestamp startdate = rs.getTimestamp("startdate");
        if (startdate != null) {
            evenement.setStartdate(startdate.toLocalDateTime());
        }
        
        Timestamp enddate = rs.getTimestamp("enddate");
        if (enddate != null) {
            evenement.setEnddate(enddate.toLocalDateTime());
        }
        
        evenement.setLocalisation(rs.getString("localisation"));
        evenement.setGoalamount(rs.getDouble("goalamount"));
        evenement.setCollectedamount(rs.getDouble("collectedamount"));
        evenement.setStatus(rs.getString("status"));
        evenement.setImageurl(rs.getString("imageurl"));
        
        Timestamp createdat = rs.getTimestamp("createdat");
        if (createdat != null) {
            evenement.setCreatedat(createdat.toLocalDateTime());
        }
        
        evenement.setCategorie(rs.getString("categorie"));
        evenement.setCreateur_id(rs.getInt("createur_id"));
        
        return evenement;
    }
} 