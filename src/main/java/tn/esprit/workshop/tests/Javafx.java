package tn.esprit.workshop.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Javafx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader load = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));

        try {
            Parent root = load.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            // Fixer la taille de la fenêtre principale
            primaryStage.setWidth(800);   // Largeur fixe
            primaryStage.setHeight(600);  // Hauteur fixe

            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



}

