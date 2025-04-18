package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Platform;

import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuBackController implements Initializable {
    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger l'interface des formations par défaut
        showFormations();
    }

    @FXML
    public void showFormations() {
        loadPage("/fxml/Formation.fxml");
    }

    @FXML
    public void showCertificats() {
        loadPage("/fxml/Certificat.fxml");
    }

    @FXML
    public void ShowCandidatures() {
        loadPage("/ListeCandidatures.fxml");
    }

    @FXML
    public void ShowDashboard() {loadPage("/dashboardUser.fxml");}

    @FXML
    public void ShowEvents() {
        loadPage("/tn/esprit/workshop/resources/EvenementForm.fxml");
    }

    @FXML
    public void ShowProduits() {
        loadPage("/AfficherProduit.fxml");
    }

    @FXML
    public void ShowCommandes() {
        loadPage("/commandeartisan.fxml");
    }

    @FXML
    private void showClientView() {
        try {
            // Charger le MainMenu.fxml qui contient la barre de navigation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent root = loader.load();
            
            // Créer une nouvelle scène avec le MainMenu
            Scene scene = new Scene(root);
            
            // Obtenir la fenêtre actuelle et changer sa scène
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(scene);
            
            // Obtenir le contrôleur du MainMenu et charger la vue client
            MainMenuController controller = loader.getController();
            controller.showClientView();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la vue client: " + e.getMessage());
        }
    }
    private double x = 0;
    private double y = 0;
    @FXML
    private void showUsers() {
        try{
        Parent root  = FXMLLoader.load(getClass().getResource("/dashboardUser.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        root.setOnMousePressed((MouseEvent Mevent) ->{
            x = Mevent.getSceneX();
            y = Mevent.getSceneY();
        });

        root.setOnMouseDragged((MouseEvent Mevent) ->{
            stage.setX(Mevent.getScreenX() - x);
            stage.setY(Mevent.getScreenY() - y);
        });

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();


    } catch (IOException e) {
        throw new RuntimeException(e);
    }
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