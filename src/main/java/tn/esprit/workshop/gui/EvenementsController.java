package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EvenementsController implements Initializable {
    
    @FXML
    private StackPane evenementsContainer;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ce contrôleur est principalement un conteneur
        // L'initialisation de la liste d'événements est gérée par EvenementsListController
    }
    
    /**
     * Charge une vue dans le conteneur principal
     * @param fxmlPath Chemin vers le fichier FXML à charger
     */
    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            evenementsContainer.getChildren().clear();
            evenementsContainer.getChildren().add(view);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la vue: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 