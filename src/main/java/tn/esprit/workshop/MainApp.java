package tn.esprit.workshop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Creation.fxml directly instead of MainMenu.fxml
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Creation.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Gestion des Créations");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}