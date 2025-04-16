package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger l'interface des formations par défaut
        showFormations();
    }

    @FXML
    private void showFormations() {
        loadPage("/fxml/Formation.fxml");
    }

    @FXML
    private void showCertificats() {
        loadPage("/fxml/Certificat.fxml");
    }

    @FXML
    public void showClientView() {
        loadPage("/fxml/ClientView.fxml");
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
        }
    }
}