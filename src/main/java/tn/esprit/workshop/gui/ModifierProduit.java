package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Produit;
import tn.esprit.workshop.services.Serviceproduit;

import java.sql.SQLException;

public class ModifierProduit {

    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfDescription;
    @FXML
    private TextField tfPrix;
    @FXML
    private TextField tfQuantiteStock;
    @FXML
    private TextField tfCategorie;
    @FXML
    private Button btnModifier;

    @FXML private Label errorNom;
    @FXML private Label errorDescription;
    @FXML private Label errorPrix;
    @FXML private Label errorQuantite;
    @FXML private Label errorCategorie;

    private final Serviceproduit serviceproduit = new Serviceproduit();
    private Produit produitToModify;
    private AfficherProduit afficherProduitController;
    public void setAfficherProduitController(AfficherProduit controller) {
        this.afficherProduitController = controller;
    }

    // Méthode appelée pour pré-remplir le formulaire avec les données du produit
    public void preRemplirFormulaire(Produit produit) {
        this.produitToModify = produit;

        tfNom.setText(produit.getNom());
        tfDescription.setText(produit.getDescription());
        tfPrix.setText(String.valueOf(produit.getPrix()));
        tfQuantiteStock.setText(String.valueOf(produit.getQuantiteStock()));
        tfCategorie.setText(produit.getCategorie());
    }

    @FXML
    public void modifierProduit(MouseEvent event) {
        String nomProduit = tfNom.getText();
        String descriptionProduit = tfDescription.getText();
        String categorieProduit = tfCategorie.getText();
        String prixText = tfPrix.getText();
        String quantiteText = tfQuantiteStock.getText();

        resetStyle(tfNom, tfDescription, tfPrix, tfQuantiteStock, tfCategorie);
        resetErrors();

        boolean hasError = false;

        if (nomProduit.isEmpty()) {
            tfNom.setStyle("-fx-border-color: red;");
            errorNom.setText("Le nom est requis.");
            errorNom.setVisible(true);
            hasError = true;
        }

        if (descriptionProduit.isEmpty() || descriptionProduit.length() < 10) {
            tfDescription.setStyle("-fx-border-color: red;");
            errorDescription.setText("La description doit contenir au moins 10 caractères.");
            errorDescription.setVisible(true);
            hasError = true;
        }

        if (categorieProduit.isEmpty()) {
            tfCategorie.setStyle("-fx-border-color: red;");
            errorCategorie.setText("La catégorie est requise.");
            errorCategorie.setVisible(true);
            hasError = true;
        } else if (!categorieProduit.matches("[a-zA-Z\\s]+")) {
            tfCategorie.setStyle("-fx-border-color: red;");
            errorCategorie.setText("La catégorie ne doit contenir que des lettres.");
            errorCategorie.setVisible(true);
            hasError = true;
        } else {
            tfCategorie.setStyle("-fx-border-color: green;");
        }

        double prixProduit = 0;
        try {
            prixProduit = Double.parseDouble(prixText);
            if (prixProduit <= 0) throw new NumberFormatException();
            tfPrix.setStyle("-fx-border-color: green;");
        } catch (NumberFormatException e) {
            tfPrix.setStyle("-fx-border-color: red;");
            errorPrix.setText("Prix invalide. Entrez un nombre positif.");
            errorPrix.setVisible(true);
            hasError = true;
        }

        int quantiteProduit = 0;
        try {
            quantiteProduit = Integer.parseInt(quantiteText);
            if (quantiteProduit <= 0) throw new NumberFormatException();
            tfQuantiteStock.setStyle("-fx-border-color: green;");
        } catch (NumberFormatException e) {
            tfQuantiteStock.setStyle("-fx-border-color: red;");
            errorQuantite.setText("Quantité invalide. Entrez un entier positif.");
            errorQuantite.setVisible(true);
            hasError = true;
        }

        if (hasError) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Veuillez corriger les erreurs en rouge !");
            alert.show();
            return;
        }

        // Mise à jour
        produitToModify.setNom(nomProduit);
        produitToModify.setDescription(descriptionProduit);
        produitToModify.setPrix(prixProduit);
        produitToModify.setQuantiteStock(quantiteProduit);
        produitToModify.setCategorie(categorieProduit);

        try {
            serviceproduit.update(produitToModify);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Le produit a été modifié avec succès !");
            alert.show();
            // ✅ Fermer la fenêtre actuelle
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

            // ✅ Rafraîchir l'affichage dans AfficherProduit
            if (afficherProduitController != null) {
                afficherProduitController.rafraichirTable();
            }

            // Tu peux ici aussi appeler afficherProduitController.rafraichirTable() si tu veux.
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erreur lors de la modification : " + e.getMessage());
            alert.show();
        }
    }

    private void resetStyle(TextField... fields) {
        for (TextField field : fields) {
            field.setStyle("");
        }
    }

    private void resetErrors() {
        errorNom.setVisible(false);
        errorDescription.setVisible(false);
        errorPrix.setVisible(false);
        errorQuantite.setVisible(false);
        errorCategorie.setVisible(false);
    }


}
