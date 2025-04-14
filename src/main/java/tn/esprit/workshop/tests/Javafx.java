package tn.esprit.workshop.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Javafx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger la vue principale qui contient la navigation
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/workshop/resources/MainView.fxml"));
        Parent root = loader.load();
        
        // Configurer la scène
        Scene scene = new Scene(root);
        primaryStage.setTitle("Gestion des Événements et Dons");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Pour la rendre responsive sur toute la fenêtre
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
