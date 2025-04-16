package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePartenariat implements CRUDPartenariat<Partenariat> {
    private Connection cnx;

    public ServicePartenariat() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(Partenariat partenariat) throws SQLException {
        String req = "INSERT INTO `partenariat`(`date_debut`, `date_fin`, `statut`, `description`, `nom`, `type`, `image`,createur_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

        int createurId = UserGetData.id;

        ps.setDate(1, new java.sql.Date(partenariat.getDateDebut().getTime()));
        ps.setDate(2, new java.sql.Date(partenariat.getDateFin().getTime()));
        ps.setString(3, partenariat.getStatut());
        ps.setString(4, partenariat.getDescription());
        ps.setString(5, partenariat.getNom());
        ps.setString(6, partenariat.getType());
        ps.setString(7, partenariat.getImage());
        ps.setInt(8, createurId);

        ps.executeUpdate();

        // Récupérer l'ID généré
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            partenariat.setId(rs.getInt(1));
        }

        System.out.println("Partenariat ajouté avec succès!");
    }

    @Override
    public void update(Partenariat partenariat) throws SQLException {
        String req = "UPDATE `partenariat` SET `date_debut`=?, `date_fin`=?, `statut`=?, `description`=?, `nom`=?, `type`=?, `image`=? WHERE `id`=?";
        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setDate(1, new java.sql.Date(partenariat.getDateDebut().getTime()));
        ps.setDate(2, new java.sql.Date(partenariat.getDateFin().getTime()));
        ps.setString(3, partenariat.getStatut());
        ps.setString(4, partenariat.getDescription());
        ps.setString(5, partenariat.getNom());
        ps.setString(6, partenariat.getType());
        ps.setString(7, partenariat.getImage());
        ps.setInt(8, partenariat.getId());

        ps.executeUpdate();
        System.out.println("Partenariat mis à jour avec succès!");
    }

    @Override
    public void delete(Partenariat partenariat) throws SQLException {
        // D'abord supprimer les candidatures associées
        String deleteCandidatures = "DELETE FROM candidature WHERE partenariat_id = ?";
        PreparedStatement psCandidatures = cnx.prepareStatement(deleteCandidatures);
        psCandidatures.setInt(1, partenariat.getId());
        psCandidatures.executeUpdate();

        // Ensuite supprimer le partenariat
        String req = "DELETE FROM `partenariat` WHERE `id`=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, partenariat.getId());
        ps.executeUpdate();

        System.out.println("Partenariat et ses candidatures supprimés avec succès!");
    }

    @Override
    public List<Partenariat> showAll() throws SQLException {
        List<Partenariat> temp = new ArrayList<>();
        String req = "SELECT * FROM partenariat";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Partenariat p = new Partenariat();
            p.setId(rs.getInt("id"));
            p.setDateDebut(rs.getDate("date_debut"));
            p.setDateFin(rs.getDate("date_fin"));
            p.setStatut(rs.getString("statut"));
            p.setDescription(rs.getString("description"));
            p.setNom(rs.getString("nom"));
            p.setType(rs.getString("type"));
            p.setImage(rs.getString("image"));
            temp.add(p);
        }
        return temp;
    }

    // Méthode pour récupérer un partenariat par son ID
    public Partenariat findById(int id) throws SQLException {
        String req = "SELECT * FROM partenariat WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Partenariat p = new Partenariat();
            p.setId(rs.getInt("id"));
            p.setDateDebut(rs.getDate("date_debut"));
            p.setDateFin(rs.getDate("date_fin"));
            p.setStatut(rs.getString("statut"));
            p.setDescription(rs.getString("description"));
            p.setNom(rs.getString("nom"));
            p.setType(rs.getString("type"));
            p.setImage(rs.getString("image"));
            return p;
        }
        return null;
    }

    // Méthode pour récupérer les candidatures d'un partenariat
    private List<Candidature> getCandidaturesByPartenariatId(int partenariatId) throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
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

            candidatures.add(c);
        }
        return candidatures;
    }
}
