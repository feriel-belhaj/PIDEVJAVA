package tn.esprit.workshop.services;

import tn.esprit.workshop.models.FormationReservee;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;

public class FormationReserveeService {
    private Connection connection;

    public FormationReserveeService() {
        connection = MyDbConnexion.getInstance().getCnx();
    }

    public void reserverFormation(int formationId, int utilisateurId, String nom, String prenom) {
        String query = "INSERT INTO formation_reservee (status, formation_id, utilisateur_id, nom, prenom, date_reservation) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "En attente");
            statement.setInt(2, formationId);
            statement.setInt(3, utilisateurId);
            statement.setString(4, nom);
            statement.setString(5, prenom);
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean estDejaReservee(int formationId, int utilisateurId) {
        String query = "SELECT COUNT(*) FROM formation_reservee WHERE formation_id = ? AND utilisateur_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, formationId);
            statement.setInt(2, utilisateurId);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}