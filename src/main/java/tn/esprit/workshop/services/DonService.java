package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Don;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DonService implements IDonService {

    private Connection connection;
    private EvenementService evenementService;
    
    public DonService() {
        connection = MyDbConnexion.getInstance().getCnx();
        evenementService = new EvenementService();
    }
    
    @Override
    public void ajouter(Don don) {
        String query = "INSERT INTO don (evenement_id, donationdate, amount, paymentref, message, createur_id, user_id) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, don.getEvenement_id());
            pst.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pst.setDouble(3, don.getAmount());
            pst.setString(4, don.getPaymentref());
            pst.setString(5, don.getMessage());
            pst.setInt(6, don.getCreateur_id());
            pst.setInt(7, don.getUser_id());
            
            pst.executeUpdate();
            
            // Get the generated ID and set it to the don object
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    don.setId(generatedKeys.getInt(1));
                }
            }
            
            // Update the collected amount for the associated event
            updateEventCollectedAmount(don.getEvenement_id());
            
            System.out.println("Don ajouté avec succès!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Don don) {
        String query = "UPDATE don SET evenement_id = ?, donationdate = ?, amount = ?, paymentref = ?, " +
                       "message = ?, createur_id = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, don.getEvenement_id());
            pst.setTimestamp(2, Timestamp.valueOf(don.getDonationdate()));
            pst.setDouble(3, don.getAmount());
            pst.setString(4, don.getPaymentref());
            pst.setString(5, don.getMessage());
            pst.setInt(6, don.getCreateur_id());
            pst.setInt(7, don.getUser_id());
            pst.setInt(8, don.getId());
            
            pst.executeUpdate();
            
            // Update the collected amount for the associated event
            updateEventCollectedAmount(don.getEvenement_id());
            
            System.out.println("Don modifié avec succès!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        // First, get the don to know which event to update after deletion
        Don don = getById(id);
        if (don == null) {
            System.out.println("Aucun don trouvé avec l'ID: " + id);
            return;
        }
        
        int evenementId = don.getEvenement_id();
        
        String query = "DELETE FROM don WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                // Update the collected amount for the associated event
                updateEventCollectedAmount(evenementId);
                
                System.out.println("Don supprimé avec succès!");
            } else {
                System.out.println("Aucun don trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Don getById(int id) {
        String query = "SELECT * FROM don WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return extractDonFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Don> getAll() {
        List<Don> dons = new ArrayList<>();
        String query = "SELECT * FROM don";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                dons.add(extractDonFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return dons;
    }

    @Override
    public List<Don> getByEvenementId(int evenementId) {
        List<Don> dons = new ArrayList<>();
        String query = "SELECT * FROM don WHERE evenement_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, evenementId);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    dons.add(extractDonFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return dons;
    }

    @Override
    public List<Don> getByUserId(int userId) {
        List<Don> dons = new ArrayList<>();
        String query = "SELECT * FROM don WHERE user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    dons.add(extractDonFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return dons;
    }

    @Override
    public double getTotalDonationsByEvenementId(int evenementId) {
        String query = "SELECT SUM(amount) AS total FROM don WHERE evenement_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, evenementId);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }
    
    // Helper method to update the collected amount in the event
    private void updateEventCollectedAmount(int evenementId) {
        double totalDonations = getTotalDonationsByEvenementId(evenementId);
        evenementService.updateCollectedAmount(evenementId, totalDonations);
    }
    
    // Helper method to extract don from ResultSet
    private Don extractDonFromResultSet(ResultSet rs) throws SQLException {
        Don don = new Don();
        don.setId(rs.getInt("id"));
        don.setEvenement_id(rs.getInt("evenement_id"));
        
        Timestamp donationdate = rs.getTimestamp("donationdate");
        if (donationdate != null) {
            don.setDonationdate(donationdate.toLocalDateTime());
        }
        
        don.setAmount(rs.getDouble("amount"));
        don.setPaymentref(rs.getString("paymentref"));
        don.setMessage(rs.getString("message"));
        don.setCreateur_id(rs.getInt("createur_id"));
        don.setUser_id(rs.getInt("user_id"));
        
        return don;
    }
} 