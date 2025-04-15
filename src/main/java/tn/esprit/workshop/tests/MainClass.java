package tn.esprit.workshop.tests;

import tn.esprit.workshop.models.Commande;
import tn.esprit.workshop.models.Produit;
import tn.esprit.workshop.models.CommandeProduit;
import tn.esprit.workshop.services.ServiceCommande;
import tn.esprit.workshop.services.Serviceproduit;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;


public class MainClass {
    public static void main(String[] args) {
        // Connexion à la base de données
        MyDbConnexion c1 = MyDbConnexion.getInstance();

        // Création d'une instance du service de commande
        ServiceCommande serviceCommande = new ServiceCommande();
        Serviceproduit serviceProduit = new Serviceproduit(); // Utilisation du service Produit

        // Création d'une commande
        Commande commande1 = new Commande();
        commande1.setStatut("En attente");
        commande1.setDateCmd(Date.valueOf(LocalDate.now()));

        try {
            // Création de produits
            Produit produit1 = serviceProduit.getById(7);  // Récupérer un produit par son ID (supposons qu'il existe)
            Produit produit2 = serviceProduit.getById(12);  // Récupérer un autre produit

            // Calcul du prix total de la commande
            double prixTotal = (produit1.getPrix() * 2) + (produit2.getPrix() * 3);  // 2 produits 1, 3 produits 2
            commande1.setPrix(prixTotal);  // On affecte le prix total à la commande

            // Insertion de la commande avec le prix calculé
            serviceCommande.insert(commande1);
            System.out.println("Commande insérée avec prix calculé : " + commande1);

            // Ajouter des produits à la commande avec leur quantité dans la base de données
            serviceCommande.ajouterProduitACommande(commande1.getId(), produit1.getId(), 2);  // 2 unités du produit 1
            serviceCommande.ajouterProduitACommande(commande1.getId(), produit2.getId(), 3);  // 3 unités du produit 2

            // Afficher toutes les commandes avec les produits associés
            System.out.println("Commandes et produits associés : ");
            for (Commande c : serviceCommande.showAll()) {
                System.out.println(c);  // Affiche la commande et ses produits avec quantités
            }

            // Supprimer la commande
            //serviceCommande.delete(commande1.getId());
            //System.out.println("Commande supprimée.");

        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}

