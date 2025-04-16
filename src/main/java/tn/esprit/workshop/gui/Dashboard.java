package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Dashboard {

    @FXML
    private StackPane mainContent;

    @FXML
    private HBox navBar;

    @FXML
    private ImageView logoImage;


    @FXML
    public void initialize() {
        // Cacher la barre de navigation par défaut
        navBar.setVisible(false);
        navBar.setManaged(false);

        // Charger le logo
        try {
            URL imageUrl = getClass().getResource("/images/logo.jpeg");
            if (imageUrl != null) {
                Image logo = new Image(imageUrl.toExternalForm());
                logoImage.setImage(logo);
            } else {
                System.out.println("Erreur: Impossible de trouver l'image à l'emplacement /images/logo.jpeg");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du logo: " + e.getMessage());
        }

        // Charger le store par défaut (qui montrera la navBar)
        try {
            goToStore(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAjouter(ActionEvent event) throws IOException {
        loadView("/Ajouterproduit.fxml", false);
    }

    @FXML
    private void goToAfficher(ActionEvent event) throws IOException {
        loadView("/Afficherproduit.fxml", false);
    }

    @FXML
    private void goToStore(ActionEvent event) throws IOException {
        loadView("/store.fxml", true);
    }

    @FXML
    private void goToCommandes(ActionEvent event) throws IOException {
        loadView("/commandeclient.fxml", true);
    }

    @FXML
    private void goToCommandesArtisan(ActionEvent event) throws IOException {
        loadView("/commandeartisan.fxml", false);
    }

    @FXML
    private void quitApp(ActionEvent event) {
        System.exit(0);
    }

    private void loadView(String fxmlPath, boolean showNav) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = loader.load();
        mainContent.getChildren().setAll(view);
        navBar.setVisible(showNav);
        navBar.setManaged(showNav);
    }

    @FXML
    public void showClientView() {
        loadPage("/fxml/ClientView.fxml", false);
    }

    @FXML
    private void showAccueil() {
        loadPage("/fxml/Accueil.fxml", false);
    }

    @FXML
    private void showAbout() {
        loadPage("/fxml/About.fxml", false);
    }

    @FXML
    private void showPartenariats() {
        loadPage("/fxml/Partenariats.fxml", false);
    }

    @FXML
    private void showEvenements() {
        loadPage("/fxml/Evenements.fxml", false);
    }

    @FXML
    private void showContact() {
        loadPage("/fxml/Contact.fxml", false);
    }

    private void loadPage(String fxml, boolean showNav) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(root);
            navBar.setVisible(showNav);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement de la page " + fxml + ": " + e.getMessage());
        }
    }



}