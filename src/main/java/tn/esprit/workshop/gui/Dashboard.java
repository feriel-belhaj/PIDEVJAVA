package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class Dashboard {

    @FXML
    private StackPane mainContent;

    @FXML
    public void initialize() {
        // Afficher par défaut le store
        try {
            goToStore(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToAjouter(ActionEvent event) throws IOException {
        loadView("/Ajouterproduit.fxml");
    }


    @FXML
    private void goToAfficher(ActionEvent event) throws IOException {
        loadView("/Afficherproduit.fxml");
    }

    @FXML
    private void goToStore(ActionEvent event) throws IOException {
        loadView("/store.fxml");
    }

    @FXML
    private void goToCommandes(ActionEvent event) throws IOException {
        loadView("/commandeclient.fxml");
    }

    @FXML
    private void goToCommandesArtisan(ActionEvent event) throws IOException {
        loadView("/commandeartisan.fxml");
    }

    @FXML
    private void quitApp(ActionEvent event) {
        System.exit(0);
    }

    private void loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = loader.load();
        mainContent.getChildren().setAll(view);
    }
}
