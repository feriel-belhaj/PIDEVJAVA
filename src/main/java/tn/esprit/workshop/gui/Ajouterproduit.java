package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Produit;
import tn.esprit.workshop.services.Serviceproduit;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.sql.SQLException;

public class Ajouterproduit {

    @FXML
    private TextField categorie;

    @FXML
    private TextField description;

    @FXML
    private TextField nom;

    @FXML
    private TextField prix;

    @FXML
    private TextField quantitestock;

    @FXML
    private Label errorNom;

    @FXML
    private Label errorDescription;

    @FXML
    private Label errorPrix;

    @FXML
    private Label errorQuantite;

    @FXML
    private Label errorCategorie;

    @FXML
    private Label imageLabel;  // Correspond au Label affichant l'image choisie

    private File imageFile = null;

    private void resetErrors() {
        errorNom.setVisible(false);
        errorDescription.setVisible(false);
        errorPrix.setVisible(false);
        errorQuantite.setVisible(false);
        errorCategorie.setVisible(false);
    }

    private void resetStyle(TextField... fields) {
        for (TextField field : fields) {
            field.setStyle("");
        }
    }

    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imageFile = selectedFile;
            imageLabel.setText(selectedFile.getName());  // Affichage du nom de l'image
        }
    }

    @FXML
    void ajouterproduit(ActionEvent event) {
        String nomProduit = nom.getText();
        String descriptionProduit = description.getText();
        String categorieProduit = categorie.getText();
        String prixText = prix.getText();
        String quantiteText = quantitestock.getText();

        resetStyle(nom, description, prix, quantitestock, categorie);
        resetErrors();

        boolean hasError = false;

        if (nomProduit.isEmpty()) {
            nom.setStyle("-fx-border-color: red;");
            errorNom.setText("Le nom est requis.");
            errorNom.setVisible(true);
            hasError = true;
        }

        if (descriptionProduit.isEmpty() || descriptionProduit.length() < 10) {
            description.setStyle("-fx-border-color: red;");
            errorDescription.setText("La description doit contenir au moins 10 caractères.");
            errorDescription.setVisible(true);
            hasError = true;
        }

        if (categorieProduit.isEmpty()) {
            categorie.setStyle("-fx-border-color: red;");
            errorCategorie.setText("La catégorie est requise.");
            errorCategorie.setVisible(true);
            hasError = true;
        } else if (!categorieProduit.matches("[a-zA-Z\\s]+")) {
            categorie.setStyle("-fx-border-color: red;");
            errorCategorie.setText("La catégorie ne doit contenir que des lettres.");
            errorCategorie.setVisible(true);
            hasError = true;
        } else {
            categorie.setStyle("-fx-border-color: green;");
        }

        double prixProduit = 0;
        try {
            prixProduit = Double.parseDouble(prixText);
            if (prixProduit <= 0) {
                throw new NumberFormatException("Le prix doit être positif.");
            }
            prix.setStyle("-fx-border-color: green;");
        } catch (NumberFormatException e) {
            prix.setStyle("-fx-border-color: red;");
            errorPrix.setText("Prix invalide. Entrez un nombre positif.");
            errorPrix.setVisible(true);
            hasError = true;
        }

        int quantiteProduit = 0;
        try {
            quantiteProduit = Integer.parseInt(quantiteText);
            if (quantiteProduit <= 0) {
                throw new NumberFormatException("La quantité doit être positive.");
            }
            quantitestock.setStyle("-fx-border-color: green;");
        } catch (NumberFormatException e) {
            quantitestock.setStyle("-fx-border-color: red;");
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

        String image = "";
        if (imageFile != null) {
            try {
                File destDir = new File("C:/xampp/htdocs/images");
                if (!destDir.exists()) destDir.mkdirs();
                File destFile = new File(destDir, imageFile.getName());
                Files.copy(imageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                image = imageFile.getName(); // Nom du fichier uniquement
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Erreur lors de la copie de l'image : " + e.getMessage());
                alert.show();
                return;
            }
        }

        java.sql.Date dateCreation = new java.sql.Date(System.currentTimeMillis());
        Produit produit = new Produit(nomProduit, descriptionProduit, prixProduit, quantiteProduit, image, categorieProduit, dateCreation);

        Serviceproduit service = new Serviceproduit();
        try {
            service.insert1(produit);

            // Afficher message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Produit ajouté avec succès !");
            alert.showAndWait();

            // Redirection vers afficherproduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherproduit.fxml"));
            Parent root = loader.load();

            // Rafraîchir les données
            AfficherProduit controller = loader.getController();
            controller.rafraichirTable();

            // Changer de scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ajout du produit : " + e.getMessage());
            alert.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement de l'interface : " + e.getMessage());
            alert.show();
        }
    }


    public void preRemplirFormulaire(Produit produit) {
        nom.setText(produit.getNom());
        description.setText(produit.getDescription());
        prix.setText(String.valueOf(produit.getPrix()));
        quantitestock.setText(String.valueOf(produit.getQuantiteStock()));
        categorie.setText(produit.getCategorie());
        imageLabel.setText(produit.getImage());
    }
}
