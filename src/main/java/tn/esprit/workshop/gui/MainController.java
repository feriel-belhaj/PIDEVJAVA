package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private BorderPane mainPane;
    
    @FXML
    private Button btnAdminView;
    
    @FXML
    private Button btnClientView;
    
    private Parent adminView;
    private Parent clientView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Précharger les vues
            FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/tn/esprit/workshop/resources/EvenementForm.fxml"));
            adminView = adminLoader.load();
            
            FXMLLoader clientLoader = new FXMLLoader(getClass().getResource("/tn/esprit/workshop/resources/EvenementClient.fxml"));
            clientView = clientLoader.load();
            
            // Afficher la vue client par défaut
            mainPane.setCenter(clientView);
            btnClientView.setDisable(true);
            btnAdminView.setDisable(false);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void switchToAdminView(ActionEvent event) {
        mainPane.setCenter(adminView);
        btnAdminView.setDisable(true);
        btnClientView.setDisable(false);
    }
    
    @FXML
    private void switchToClientView(ActionEvent event) {
        mainPane.setCenter(clientView);
        btnClientView.setDisable(true);
        btnAdminView.setDisable(false);
    }
} 