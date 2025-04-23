package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import tn.esprit.workshop.models.Commande;
import tn.esprit.workshop.models.CommandeProduit;
import tn.esprit.workshop.models.Produit;
import tn.esprit.workshop.services.ServiceCommande;
import tn.esprit.workshop.services.Serviceproduit;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class Store implements Initializable {

    @FXML
    private FlowPane produitContainer;

    @FXML
    private Button btnCommander;

    private final Map<Produit, TextField> quantitesMap = new HashMap<>();
    private final Serviceproduit serviceProduit = new Serviceproduit();
    private final ServiceCommande serviceCommande = new ServiceCommande();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chargerProduits();
    }

    private void chargerProduits() {
        produitContainer.getChildren().clear();
        quantitesMap.clear();

        List<Produit> produits = serviceProduit.getAll();

        for (Produit produit : produits) {
            VBox card = new VBox(10);
            card.setPrefWidth(220);
            card.setPadding(new Insets(10));
            card.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: #cccccc;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-effect: dropshadow(gaussian, lightgray, 5, 0, 2, 2);"
            );

            ImageView imageView = new ImageView();
            imageView.setFitWidth(200);
            imageView.setFitHeight(120);
            String imagePath = "file:///C:/xampp/htdocs/images/" + produit.getImage();
            try {
                Image image = new Image(imagePath, true);
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setImage(new Image("https://via.placeholder.com/200x120?text=Image+manquante"));
            }

            Label lblNom = new Label(produit.getNom());
            lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            Label lblDescription = new Label(produit.getDescription().length() > 50 ?
                    produit.getDescription().substring(0, 47) + "..." : produit.getDescription());
            lblDescription.setWrapText(true);

            Label lblCategorie = new Label("Catégorie : " + produit.getCategorie());

            Label lblPrix = new Label("Prix : " + produit.getPrix() + " DT");
            lblPrix.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

            Label lblStock = new Label("Stock : " + produit.getQuantiteStock());

            TextField tfQuantite = new TextField();
            tfQuantite.setPromptText("Quantité");
            tfQuantite.setDisable(produit.getQuantiteStock() == 0);
            quantitesMap.put(produit, tfQuantite);

            card.getChildren().addAll(imageView, lblNom, lblDescription, lblCategorie, lblPrix, lblStock, tfQuantite);
            produitContainer.getChildren().add(card);
        }
    }

    @FXML
    public void commander() {
        List<CommandeProduit> produitsCommandes = new ArrayList<>();
        boolean erreur = false;

        for (Map.Entry<Produit, TextField> entry : quantitesMap.entrySet()) {
            Produit produit = entry.getKey();
            TextField tfQuantite = entry.getValue();
            tfQuantite.setStyle("");

            String input = tfQuantite.getText().trim();
            if (!input.isEmpty()) {
                try {
                    int quantite = Integer.parseInt(input);
                    if (quantite <= 0 || quantite > produit.getQuantiteStock()) {
                        tfQuantite.setStyle("-fx-border-color: red;");
                        erreur = true;

                        if (quantite > produit.getQuantiteStock()) {
                            new Alert(Alert.AlertType.ERROR, "La quantité saisie pour \"" + produit.getNom() + "\" dépasse le stock (" + produit.getQuantiteStock() + ").").showAndWait();
                        }

                    } else {
                        tfQuantite.setStyle("-fx-border-color: green;");
                        produitsCommandes.add(new CommandeProduit(produit, quantite));
                    }

                } catch (NumberFormatException e) {
                    tfQuantite.setStyle("-fx-border-color: red;");
                    new Alert(Alert.AlertType.ERROR, "Quantité invalide pour : " + produit.getNom()).showAndWait();
                    erreur = true;
                }
            }
        }

        if (erreur) return;

        if (produitsCommandes.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez saisir une quantité valide pour au moins un produit.").showAndWait();
            return;
        }

        Commande commande = new Commande();
        commande.setStatut("En attente");
        commande.setDateCmd(Date.valueOf(LocalDate.now()));
        commande.setProduits(produitsCommandes);

        try {
            serviceCommande.insert(commande);

            for (CommandeProduit cp : produitsCommandes) {
                serviceCommande.ajouterProduitACommande(commande.getId(), cp.getProduit().getId(), cp.getQuantite());

                // Décrément stock
                Produit p = cp.getProduit();
                int nouveauStock = p.getQuantiteStock() - cp.getQuantite();
                p.setQuantiteStock(nouveauStock);
                serviceProduit.updateQuantiteStock(p.getId(), nouveauStock);
            }

            new Alert(Alert.AlertType.INFORMATION, "Commande enregistrée avec succès !").showAndWait();
            chargerProduits();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la commande.").showAndWait();
        }
    }
}
