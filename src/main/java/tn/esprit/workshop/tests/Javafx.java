package tn.esprit.workshop.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Javafx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Obtenir l'URL du fichier FXML
            URL fxmlUrl = getClass().getResource("/Listepartenariats.fxml");
            if (fxmlUrl == null) {
                System.err.println("Erreur: Impossible de trouver le fichier FXML");
                return;
            }
            System.out.println("FXML URL: " + fxmlUrl);

            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // Créer et configurer la scène
            Scene scene = new Scene(root);
            primaryStage.setTitle("Liste des Partenariats");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            
            // Afficher la fenêtre
            primaryStage.show();
            
            System.out.println("Fenêtre affichée avec succès");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Méthode pour charger la vue des cartes
    public static void loadPartenariatCards(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Javafx.class.getResource("/ListePartenariats.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Partenariats");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue des cartes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
