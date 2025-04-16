package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Formation;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FormationService {
    private Connection connection;
    private static final String RESOURCES_PATH = "src/main/resources/";
    private static final String IMAGES_PATH = "uploads/images/";

    public FormationService() {
        connection = MyDbConnexion.getInstance().getCnx();
        // Créer le dossier des images s'il n'existe pas
        try {
            Path uploadPath = Paths.get(RESOURCES_PATH + IMAGES_PATH);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Dossier d'images créé: " + uploadPath);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du dossier d'images: " + e.getMessage());
        }
    }

    public void ajouter(Formation formation) throws SQLException {
        String query = "INSERT INTO formation (titre, description, datedeb, datefin, niveau, prix, emplacement, nbplace, nbparticipant, organisateur, duree, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, formation.getTitre());
            ps.setString(2, formation.getDescription());
            ps.setDate(3, Date.valueOf(formation.getDateDeb()));
            ps.setDate(4, Date.valueOf(formation.getDateFin()));
            ps.setString(5, formation.getNiveau());
            ps.setFloat(6, formation.getPrix());
            ps.setString(7, formation.getEmplacement());
            ps.setInt(8, formation.getNbPlace());
            ps.setInt(9, formation.getNbParticipant());
            ps.setString(10, formation.getOrganisateur());
            ps.setString(11, formation.getDuree());
            ps.setString(12, formation.getImage());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                formation.setId(rs.getInt(1));
            }
        }
    }

    public void modifier(Formation formation) throws SQLException {
        String query = "UPDATE formation SET titre=?, description=?, datedeb=?, datefin=?, niveau=?, prix=?, emplacement=?, nbplace=?, nbparticipant=?, organisateur=?, duree=?, image=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, formation.getTitre());
            ps.setString(2, formation.getDescription());
            ps.setDate(3, Date.valueOf(formation.getDateDeb()));
            ps.setDate(4, Date.valueOf(formation.getDateFin()));
            ps.setString(5, formation.getNiveau());
            ps.setFloat(6, formation.getPrix());
            ps.setString(7, formation.getEmplacement());
            ps.setInt(8, formation.getNbPlace());
            ps.setInt(9, formation.getNbParticipant());
            ps.setString(10, formation.getOrganisateur());
            ps.setString(11, formation.getDuree());
            ps.setString(12, formation.getImage());
            ps.setInt(13, formation.getId());
            ps.executeUpdate();
        }
    }

    public void supprimer(int id) throws SQLException {
        // D'abord, récupérer l'image pour la supprimer si elle existe
        Formation formation = getById(id);
        if (formation != null && formation.getImage() != null) {
            try {
                Path imagePath = Paths.get(RESOURCES_PATH + formation.getImage());
                Files.deleteIfExists(imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors de la suppression de l'image: " + e.getMessage());
            }
        }

        String query = "DELETE FROM formation WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Formation getById(int id) throws SQLException {
        String query = "SELECT * FROM formation WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractFormationFromResultSet(rs);
            }
        }
        return null;
    }

    public List<Formation> getAll() throws SQLException {
        List<Formation> formations = new ArrayList<>();
        String query = "SELECT * FROM formation";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Formation formation = extractFormationFromResultSet(rs);
                formations.add(formation);
            }
        }
        return formations;
    }

    private Formation extractFormationFromResultSet(ResultSet rs) throws SQLException {
        Formation formation = new Formation();
        formation.setId(rs.getInt("id"));
        formation.setTitre(rs.getString("titre"));
        formation.setDescription(rs.getString("description"));
        formation.setDateDeb(rs.getDate("datedeb").toLocalDate());
        formation.setDateFin(rs.getDate("datefin").toLocalDate());
        formation.setNiveau(rs.getString("niveau"));
        formation.setPrix(rs.getFloat("prix"));
        formation.setEmplacement(rs.getString("emplacement"));
        formation.setNbPlace(rs.getInt("nbplace"));
        formation.setNbParticipant(rs.getInt("nbparticipant"));
        formation.setOrganisateur(rs.getString("organisateur"));
        formation.setDuree(rs.getString("duree"));
        formation.setImage(rs.getString("image"));
        return formation;
    }

    // Méthode utilitaire pour copier une image vers le dossier des ressources
    public String copyImageToResources(File sourceFile, String fileName) {
        try {
            String targetPath = IMAGES_PATH + fileName;
            Path destination = Paths.get(RESOURCES_PATH + targetPath);
            Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        } catch (Exception e) {
            System.err.println("Erreur lors de la copie de l'image: " + e.getMessage());
            return null;
        }
    }
}