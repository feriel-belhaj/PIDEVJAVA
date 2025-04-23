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
        String req = "INSERT INTO `partenariat`(`date_debut`, `date_fin`, `statut`, `description`, `nom`, `type`, `image`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

        ps.setDate(1, new java.sql.Date(partenariat.getDateDebut().getTime()));
        ps.setDate(2, new java.sql.Date(partenariat.getDateFin().getTime()));
        ps.setString(3, partenariat.getStatut());
        ps.setString(4, partenariat.getDescription());
        ps.setString(5, partenariat.getNom());
        ps.setString(6, partenariat.getType());
        ps.setString(7, partenariat.getImage());

        ps.executeUpdate();

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
        String deleteCandidatures = "DELETE FROM candidature WHERE partenariat_id = ?";
        PreparedStatement psCandidatures = cnx.prepareStatement(deleteCandidatures);
        psCandidatures.setInt(1, partenariat.getId());
        psCandidatures.executeUpdate();

        String req = "DELETE FROM `partenariat` WHERE `id`=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, partenariat.getId());
        ps.executeUpdate();

        System.out.println("Partenariat et ses candidatures supprimés avec succès!");
    }

    @Override
    public List<Partenariat> showAll() throws SQLException {
        List<Partenariat> temp = new ArrayList<>();
        String req = "SELECT * FROM partenariat ORDER BY id DESC";
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

    public List<Partenariat> searchPartenariats(String searchText, java.util.Date dateDebut, java.util.Date dateFin) throws SQLException {
        List<Partenariat> temp = new ArrayList<>();
        StringBuilder req = new StringBuilder("SELECT * FROM partenariat WHERE 1=1");

        List<Object> params = new ArrayList<>();
        if (searchText != null && !searchText.isEmpty()) {
            req.append(" AND (LOWER(nom) LIKE ? OR LOWER(type) LIKE ? OR LOWER(description) LIKE ?)");
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Logique de contention stricte : date_debut >= dateDebutRecherche AND date_fin <= dateFinRecherche
        if (dateDebut != null && dateFin != null) {
            req.append(" AND date_debut >= ? AND date_fin <= ?");
            params.add(new java.sql.Date(dateDebut.getTime()));
            params.add(new java.sql.Date(dateFin.getTime()));
        } else if (dateDebut != null) {
            req.append(" AND date_debut >= ?");
            params.add(new java.sql.Date(dateDebut.getTime()));
        } else if (dateFin != null) {
            req.append(" AND date_fin <= ?");
            params.add(new java.sql.Date(dateFin.getTime()));
        }

        req.append(" ORDER BY id DESC");

        // Logs pour déboguer
        System.out.println("Requête SQL : " + req.toString());
        System.out.println("Paramètres : " + params);

        PreparedStatement ps = cnx.prepareStatement(req.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
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

        System.out.println("Nombre de partenariats trouvés : " + temp.size());
        return temp;
    }

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

            Partenariat p = new Partenariat();
            p.setId(partenariatId);
            c.setPartenariat(p);

            candidatures.add(c);
        }
        return candidatures;
    }
}