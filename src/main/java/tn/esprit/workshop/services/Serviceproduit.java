package tn.esprit.workshop.services;

import javafx.collections.ObservableList;
import tn.esprit.workshop.models.Produit;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Serviceproduit implements PrCRUD<Produit> {
    private Connection cnx;

    public Serviceproduit() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(Produit produit) throws SQLException {
        String req = "INSERT INTO produit(nom, description, prix, quantitestock, image, categorie, datecreation) " +
                "VALUES ('" + produit.getNom() + "', '" + produit.getDescription() + "', " + produit.getPrix() + ", " + produit.getQuantiteStock() + ", '" + produit.getImage() + "', '" + produit.getCategorie() + "', '" + produit.getDateCreation() + "')";

        Statement st = cnx.createStatement();
        st.executeUpdate(req);
        System.out.println("Insertion OK!");
    }

    public void insert1(Produit produit) throws SQLException {
        String req = "INSERT INTO produit(nom, description, prix, quantitestock, image, categorie, datecreation,createur_id) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        int createur_id= UserGetData.id;
        ps.setString(1, produit.getNom());
        ps.setString(2, produit.getDescription());
        ps.setDouble(3, produit.getPrix());
        ps.setInt(4, produit.getQuantiteStock());
        ps.setString(5, produit.getImage());
        ps.setString(6, produit.getCategorie());
        ps.setDate(7, produit.getDateCreation());
        ps.setInt(8, createur_id);

        ps.executeUpdate();
        System.out.println("Insertion OK!");
    }

    @Override
    public void update(Produit produit) throws SQLException {
        String req = "UPDATE produit SET nom = ?, description = ?, prix = ?, quantitestock = ?, image = ?, categorie = ?, datecreation = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setString(1, produit.getNom());
        ps.setString(2, produit.getDescription());
        ps.setDouble(3, produit.getPrix());
        ps.setInt(4, produit.getQuantiteStock());
        ps.setString(5, produit.getImage());
        ps.setString(6, produit.getCategorie());
        ps.setDate(7, produit.getDateCreation());
        ps.setInt(8, produit.getId());

        ps.executeUpdate();
        System.out.println("Mise à jour OK!");
    }

    @Override
    public void delete(Produit produit) throws SQLException {
        String req = "DELETE FROM produit WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, produit.getId());
        ps.executeUpdate();
        System.out.println("Suppression OK!");
    }

    @Override
    public List<Produit> showAll() throws SQLException {
        List<Produit> temp = new ArrayList<>();
        String req = "SELECT * FROM produit";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Produit p = new Produit();
            p.setId(rs.getInt("id"));
            p.setNom(rs.getString("nom"));
            p.setDescription(rs.getString("description"));
            p.setPrix(rs.getDouble("prix"));
            p.setQuantiteStock(rs.getInt("quantitestock"));
            p.setImage(rs.getString("image"));
            p.setCategorie(rs.getString("categorie"));
            p.setDateCreation(rs.getDate("datecreation"));

            temp.add(p);
        }

        return temp;
    }
    public Produit getById(int id) throws SQLException {
        Produit produit = null;
        String query = "SELECT * FROM produit WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setQuantiteStock(rs.getInt("quantitestock"));
                produit.setImage(rs.getString("image"));
                produit.setCategorie(rs.getString("categorie"));
                produit.setDateCreation(rs.getDate("datecreation"));
            }
        }
        return produit;
    }
    public void updateQuantiteStock(int produitId, int nouveauStock) throws SQLException {
        String req = "UPDATE produit SET quantiteStock = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, nouveauStock);
        ps.setInt(2, produitId);
        ps.executeUpdate();
    }
    public List<Produit> getAll() {
        try {
            return showAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
