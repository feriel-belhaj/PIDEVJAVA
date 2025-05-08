package tn.esprit.workshop.services;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import tn.esprit.workshop.models.Vente;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PredicteurVentes {

    public static void main(String[] args) {
        // Récupérer les données de vente depuis la base de données
        List<Vente> ventes = recupererVentesProduit(1);  // ID du produit à analyser (par exemple, 1)

        // Appliquer une régression linéaire pour la prédiction
        double prediction = predireVentes(ventes);

        // Afficher la prédiction pour la prochaine vente
        System.out.println("Prédiction des ventes pour le prochain jour : " + prediction);
    }

    // Méthode pour récupérer les données de vente
    public static List<Vente> recupererVentesProduit(int produitId) {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT p.id AS produit_id, c.datecmd AS date_commande, cp.quantite AS quantite_vendue " +
                "FROM commande c " +
                "JOIN commande_produit cp ON c.id = cp.commande_id " +
                "JOIN produit p ON cp.produit_id = p.id " +
                "WHERE p.id = ? " +
                "ORDER BY c.datecmd ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Connexion à la base de données (la connexion restera ouverte)
            conn = MyDbConnexion.getInstance().getCnx();

            // Définir l'ID du produit dans la requête
            ps = conn.prepareStatement(query);
            ps.setInt(1, produitId);

            rs = ps.executeQuery();
            while (rs.next()) {
                Date dateCommande = rs.getDate("date_commande");
                int quantiteVendue = rs.getInt("quantite_vendue");
                ventes.add(new Vente(dateCommande, quantiteVendue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture explicite des ressources, mais pas de la connexion
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ventes;
    }

    // Méthode de régression linéaire pour prédire les ventes
    public static double predireVentes(List<Vente> ventes) {
        SimpleRegression regression = new SimpleRegression();

        // Transformer les données de vente en points (x, y)
        for (int i = 0; i < ventes.size(); i++) {
            // Ici, on utilise l'index comme variable indépendante (x) et la quantité vendue comme variable dépendante (y)
            regression.addData(i, ventes.get(i).getQuantiteVendue());
        }

        // Prédiction pour le prochain jour (par exemple, jour suivant dans la séquence)
        return regression.predict(ventes.size());  // Prédiction pour le jour suivant
    }
}

