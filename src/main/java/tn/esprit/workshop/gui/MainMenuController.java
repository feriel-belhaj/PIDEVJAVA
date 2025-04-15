package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private ImageView logoImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger le logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.out.println("Erreur de chargement du logo: " + e.getMessage());
        }
        
        // Charger l'interface des formations par défaut
        showClientView();
    }

    @FXML
    private void showFormations() {
        switchToBackOffice("/fxml/Formation.fxml");
    }

    @FXML
    private void showCertificats() {
        switchToBackOffice("/fxml/Certificat.fxml");
    }

    private void switchToBackOffice(String initialView) {
        try {
            // Charger le MainMenuBack.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenuBack.fxml"));
            Parent root = loader.load();
            
            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            
            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) contentArea.getScene().getWindow();
            
            // Changer la scène
            stage.setScene(scene);
            
            // Obtenir le contrôleur et charger la vue initiale
            MainMenuBackController controller = loader.getController();
            if (initialView.equals("/fxml/Formation.fxml")) {
                controller.showFormations();
            } else if (initialView.equals("/fxml/Certificat.fxml")) {
                controller.showCertificats();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement du back office: " + e.getMessage());
        }
    }

    @FXML
    public void showClientView() {
        loadPage("/fxml/ClientView.fxml");
    }

    @FXML
    private void showAccueil() {
        loadPage("/fxml/Accueil.fxml");
    }

    @FXML
    private void showAbout() {
        loadPage("/fxml/About.fxml");
    }

    @FXML
    private void showPartenariats() {
        loadPage("/fxml/Partenariats.fxml");
    }

    @FXML
    private void showEvenements() {
        loadPage("/fxml/Evenements.fxml");
    }

    @FXML
    private void showContact() {
        loadPage("/fxml/Contact.fxml");
    }

    @FXML
    private void quit() {
        Platform.exit();
    }

    private void loadPage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement de la page " + fxml + ": " + e.getMessage());
        }
    }
} 