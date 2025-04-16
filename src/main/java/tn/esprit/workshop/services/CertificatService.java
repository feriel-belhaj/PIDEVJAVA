package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Certificat;
import tn.esprit.workshop.models.Formation;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificatService {
    private Connection connection;

    public CertificatService() {
        connection = MyDbConnexion.getInstance().getCnx();
    }

    public void ajouter(Certificat certificat) throws SQLException {
        String query = "INSERT INTO certificat (formation_id, nom, prenom, dateobt, niveau, nomorganisme) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, certificat.getFormationId());
            ps.setString(2, certificat.getNom());
            ps.setString(3, certificat.getPrenom());
            ps.setDate(4, Date.valueOf(certificat.getDateobt()));
            ps.setString(5, certificat.getNiveau());
            ps.setString(6, certificat.getNomorganisme());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                certificat.setId(rs.getInt(1));
            }
        }
    }

    public void modifier(Certificat certificat) throws SQLException {
        String query = "UPDATE certificat SET formation_id=?, nom=?, prenom=?, dateobt=?, niveau=?, nomorganisme=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, certificat.getFormationId());
            ps.setString(2, certificat.getNom());
            ps.setString(3, certificat.getPrenom());
            ps.setDate(4, Date.valueOf(certificat.getDateobt()));
            ps.setString(5, certificat.getNiveau());
            ps.setString(6, certificat.getNomorganisme());
            ps.setInt(7, certificat.getId());
            ps.executeUpdate();
        }
    }

    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM certificat WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Certificat getById(int id) throws SQLException {
        String query = "SELECT * FROM certificat WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractCertificatFromResultSet(rs);
            }
        }
        return null;
    }

    public List<Certificat> getByFormationId(int formationId) throws SQLException {
        List<Certificat> certificats = new ArrayList<>();
        String query = "SELECT * FROM certificat WHERE formation_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, formationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                certificats.add(extractCertificatFromResultSet(rs));
            }
        }
        return certificats;
    }

    public List<Certificat> getAll() throws SQLException {
        List<Certificat> certificats = new ArrayList<>();
        String query = "SELECT c.*, f.titre as formation_titre FROM certificat c JOIN formation f ON c.formation_id = f.id";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Certificat certificat = extractCertificatFromResultSet(rs);
                certificat.setFormationTitre(rs.getString("formation_titre"));
                certificats.add(certificat);
            }
        }
        return certificats;
    }

    private Certificat extractCertificatFromResultSet(ResultSet rs) throws SQLException {
        Certificat certificat = new Certificat();
        certificat.setId(rs.getInt("id"));
        certificat.setFormationId(rs.getInt("formation_id"));
        certificat.setNom(rs.getString("nom"));
        certificat.setPrenom(rs.getString("prenom"));
        certificat.setDateobt(rs.getDate("dateobt").toLocalDate());
        certificat.setNiveau(rs.getString("niveau"));
        certificat.setNomorganisme(rs.getString("nomorganisme"));
        return certificat;
    }
}