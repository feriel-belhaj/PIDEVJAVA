package tn.esprit.workshop.services;


import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidature implements CRUDCandidature<Candidature> {
    private Connection cnx;

    public ServiceCandidature() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(Candidature candidature) throws SQLException {
        String req = "INSERT INTO `candidature`(`date_postulation`, `cv`, `portfolio`, `motivation`, `type_collab`, `partenariat_id`,createur_id) " +
                "VALUES (?, ?, ?, ?, ?, ?,?)";

        PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        int createurId =UserGetData.id;

        ps.setDate(1, new java.sql.Date(candidature.getDatePostulation().getTime()));
        ps.setString(2, candidature.getCv());
        ps.setString(3, candidature.getPortfolio());
        ps.setString(4, candidature.getMotivation());
        ps.setString(5, candidature.getTypeCollab());
        ps.setInt(6, candidature.getPartenariat().getId());
        ps.setInt(7, createurId);

        ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            candidature.setId(rs.getInt(1));
        }

        System.out.println("Candidature ajoutée avec succès!");
    }

    @Override
    public void update(Candidature candidature) throws SQLException {
        String req = "UPDATE `candidature` SET `date_postulation`=?, `cv`=?, `portfolio`=?, `motivation`=?, `type_collab`=?, `partenariat_id`=?, `score_nlp`=?, `score_artistique`=?, `analysis_result`=? WHERE `id`=?";

        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setDate(1, new java.sql.Date(candidature.getDatePostulation().getTime()));
        ps.setString(2, candidature.getCv());
        ps.setString(3, candidature.getPortfolio());
        ps.setString(4, candidature.getMotivation());
        ps.setString(5, candidature.getTypeCollab());
        ps.setInt(6, candidature.getPartenariat().getId());
        ps.setDouble(7, candidature.getScoreNlp());
        ps.setDouble(8, candidature.getScoreArtistique());
        ps.setString(9, candidature.getAnalysisResult());
        ps.setInt(10, candidature.getId());

        ps.executeUpdate();
        System.out.println("Candidature mise à jour avec succès!");
    }

    @Override
    public void delete(Candidature candidature) throws SQLException {
        String req = "DELETE FROM `candidature` WHERE `id`=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, candidature.getId());
        ps.executeUpdate();
        System.out.println("Candidature supprimée avec succès!");
    }

    @Override
    public List<Candidature> showAll() throws SQLException {
        List<Candidature> temp = new ArrayList<>();
        String req = "SELECT c.*, p.nom, p.description, p.type, p.date_debut, p.date_fin, p.statut, p.image " +
                "FROM candidature c " +
                "LEFT JOIN partenariat p ON c.partenariat_id = p.id";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Candidature c = new Candidature();
            c.setId(rs.getInt("id"));
            c.setDatePostulation(rs.getDate("date_postulation"));
            c.setCv(rs.getString("cv"));
            c.setPortfolio(rs.getString("portfolio"));
            c.setMotivation(rs.getString("motivation"));
            c.setTypeCollab(rs.getString("type_collab"));
            c.setScoreNlp(rs.getDouble("score_nlp"));
            c.setScoreArtistique(rs.getDouble("score_artistique"));
            c.setAnalysisResult(rs.getString("analysis_result"));

            // Créer un partenariat complet
            Partenariat p = new Partenariat();
            p.setId(rs.getInt("partenariat_id"));
            p.setNom(rs.getString("nom"));
            p.setDescription(rs.getString("description"));
            p.setType(rs.getString("type"));
            p.setDateDebut(rs.getDate("date_debut"));
            p.setDateFin(rs.getDate("date_fin"));
            p.setStatut(rs.getString("statut"));
            p.setImage(rs.getString("image"));
            
            c.setPartenariat(p);

            temp.add(c);
        }
        return temp;
    }

    public Candidature findById(int id) throws SQLException {
        String req = "SELECT * FROM candidature WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Candidature c = new Candidature();
            c.setId(rs.getInt("id"));
            c.setDatePostulation(rs.getDate("date_postulation"));
            c.setCv(rs.getString("cv"));
            c.setPortfolio(rs.getString("portfolio"));
            c.setMotivation(rs.getString("motivation"));
            c.setTypeCollab(rs.getString("type_collab"));
            c.setScoreNlp(rs.getDouble("score_nlp"));
            c.setScoreArtistique(rs.getDouble("score_artistique"));
            c.setAnalysisResult(rs.getString("analysis_result"));

            // Créer un partenariat minimal pour éviter la récursion
            Partenariat p = new Partenariat();
            p.setId(rs.getInt("partenariat_id"));
            c.setPartenariat(p);

            return c;
        }
        return null;
    }

    public List<Candidature> findByPartenariatId(int partenariatId) throws SQLException {
        List<Candidature> temp = new ArrayList<>();
        String req = "SELECT * FROM candidature WHERE partenariat_id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, partenariatId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Candidature c = new Candidature();
            c.setId(rs.getInt("id"));
            c.setDatePostulation(rs.getDate("date_postulation"));
            c.setCv(rs.getString("cv"));
            c.setPortfolio(rs.getString("portfolio"));
            c.setMotivation(rs.getString("motivation"));
            c.setTypeCollab(rs.getString("type_collab"));
            c.setScoreNlp(rs.getDouble("score_nlp"));
            c.setScoreArtistique(rs.getDouble("score_artistique"));
            c.setAnalysisResult(rs.getString("analysis_result"));

            // Créer un partenariat minimal pour éviter la récursion
            Partenariat p = new Partenariat();
            p.setId(partenariatId);
            c.setPartenariat(p);

            temp.add(c);
        }
        return temp;
    }
}
