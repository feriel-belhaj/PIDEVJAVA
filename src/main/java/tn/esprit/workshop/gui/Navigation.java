package tn.esprit.workshop.gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigation {

    @FXML
    private StackPane contentArea;

    @FXML
    private void initialize() {
        // Par défaut, on peut charger une vue initiale (optionnel)
        loadView("/AfficherProduit.fxml");
    }

    @FXML
    private void goToAfficher(ActionEvent event) {
        loadView("/AfficherProduit.fxml");
    }

    @FXML
    private void goToStore(ActionEvent event) {
        loadView("/store.fxml");
    }

    @FXML
    private void goToCommandes(ActionEvent event) {
        loadView("/Commandeclient.fxml");
    }

    @FXML
    private void goToCommandesArtisan(ActionEvent event) {
        loadView("/commandeartisan.fxml");
    }

    @FXML
    private void quit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la vue : " + fxmlPath);
        }
    }
}
