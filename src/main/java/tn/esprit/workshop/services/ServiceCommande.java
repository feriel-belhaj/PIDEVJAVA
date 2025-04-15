package tn.esprit.workshop.services;

import tn.esprit.workshop.models.Commande;
import tn.esprit.workshop.models.CommandeProduit;
import tn.esprit.workshop.models.Produit;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommande {
    private Connection cnx;

    public ServiceCommande() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    // Insertion d'une commande
    public void insert(Commande commande) throws SQLException {
        // Calcul du prix total avant l'insertion
        double prixTotal = 0.0;
        for (CommandeProduit cp : commande.getProduits()) {
            prixTotal += cp.getProduit().getPrix() * cp.getQuantite(); // Prix unitaire * quantité
        }
        commande.setPrix(prixTotal); // On assigne le prix calculé à la commande

        // Insérer la commande avec le prix calculé
        String req = "INSERT INTO commande (statut, datecmd, prix) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, commande.getStatut());
        ps.setDate(2, commande.getDateCmd());
        ps.setDouble(3, commande.getPrix());  // Insertion du prix calculé

        ps.executeUpdate();

        // Récupérer l'ID de la commande généré
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            commande.setId(rs.getInt(1));
        }

        System.out.println("Commande insérée avec succès, ID: " + commande.getId());

        // Mise à jour du prix de la commande après insertion
        updatePrix(commande.getId());
    }

    // Ajouter un produit à une commande avec la quantité dans la table pivot
    public void ajouterProduitACommande(int commandeId, int produitId, int quantite) throws SQLException {
        String req = "INSERT INTO commande_produit (commande_id, produit_id, quantite) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, commandeId);
        ps.setInt(2, produitId);
        ps.setInt(3, quantite);

        ps.executeUpdate();
        System.out.println("Produit ajouté à la commande avec quantité : " + quantite);

        // Mise à jour du prix de la commande après ajout du produit
        updatePrix(commandeId);
    }

    // Mise à jour du prix de la commande (calculé à partir des produits et de leur quantité)
    public void updatePrix(int commandeId) throws SQLException {
        String req = "SELECT SUM(p.prix * cp.quantite) AS total FROM produit p " +
                "JOIN commande_produit cp ON p.id = cp.produit_id WHERE cp.commande_id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, commandeId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            double prixTotal = rs.getDouble("total");

            String updateReq = "UPDATE commande SET prix = ? WHERE id = ?";
            PreparedStatement psUpdate = cnx.prepareStatement(updateReq);
            psUpdate.setDouble(1, prixTotal);
            psUpdate.setInt(2, commandeId);

            psUpdate.executeUpdate();
            System.out.println("Prix total de la commande mis à jour à : " + prixTotal);
        }
    }

    // Afficher toutes les commandes avec les produits associés et leur quantité
    public List<Commande> showAll() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String req = "SELECT * FROM commande";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Commande c = new Commande();
            c.setId(rs.getInt("id"));
            c.setStatut(rs.getString("statut"));
            c.setDateCmd(rs.getDate("datecmd"));
            c.setPrix(rs.getFloat("prix"));

            // Récupérer les produits associés à cette commande avec leur quantité depuis la table pivot
            String produitsReq = "SELECT p.id, p.nom, p.prix, p.categorie, p.DateCreation, p.description, cp.quantite " +
                    "FROM produit p " +
                    "JOIN commande_produit cp ON p.id = cp.produit_id WHERE cp.commande_id = ?";
            PreparedStatement ps = cnx.prepareStatement(produitsReq);
            ps.setInt(1, c.getId());
            ResultSet rsProduits = ps.executeQuery();

            // Ajouter chaque produit avec sa quantité dans la commande
            List<CommandeProduit> commandeProduits = new ArrayList<>();
            while (rsProduits.next()) {
                Produit p = new Produit();
                p.setId(rsProduits.getInt("id"));
                p.setNom(rsProduits.getString("nom"));
                p.setPrix(rsProduits.getDouble("prix"));
                p.setCategorie(rsProduits.getString("categorie"));
                p.setDateCreation(rsProduits.getDate("DateCreation"));
                p.setDescription(rsProduits.getString("description"));
                int quantite = rsProduits.getInt("quantite");

                CommandeProduit cp = new CommandeProduit(p, quantite);
                commandeProduits.add(cp);
            }

            c.setProduits(commandeProduits); // Set produits associés avec quantité
            commandes.add(c);
        }

        return commandes;
    }

    // Supprimer une commande
    public void delete(int commandeId) throws SQLException {
        // Supprimer d'abord les produits associés dans la table pivot
        String req = "DELETE FROM commande_produit WHERE commande_id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, commandeId);
        ps.executeUpdate();

        // Supprimer ensuite la commande
        String reqCmd = "DELETE FROM commande WHERE id = ?";
        ps = cnx.prepareStatement(reqCmd);
        ps.setInt(1, commandeId);
        ps.executeUpdate();

        System.out.println("Commande supprimée avec succès.");
    }
    public void updateQuantiteProduit(int commandeId, int produitId, int newQuantite) throws SQLException {
        String req = "UPDATE commande_produit SET quantite = ? WHERE commande_id = ? AND produit_id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, newQuantite);
        ps.setInt(2, commandeId);
        ps.setInt(3, produitId);
        ps.executeUpdate();
    }
    public void updateStatutCommande(int idCommande, String nouveauStatut) throws SQLException {
        String req = "UPDATE commande SET statut = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, nouveauStatut);
        ps.setInt(2, idCommande);
        ps.executeUpdate();
    }
}
