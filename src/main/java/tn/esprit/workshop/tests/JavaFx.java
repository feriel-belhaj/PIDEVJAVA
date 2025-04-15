package tn.esprit.workshop.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class JavaFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Utilisateur.fxml"));
            Scene scene = new Scene(root);

            root.setOnMousePressed((MouseEvent event) ->{
                x = event.getSceneX();
                y = event.getSceneY();
            });

            root.setOnMouseDragged((MouseEvent event) ->{
                primaryStage.setX(event.getScreenX() - x);
                primaryStage.setY(event.getScreenY() - y);

                primaryStage.setOpacity(.8);
            });

            root.setOnMouseReleased((MouseEvent event) ->{
                primaryStage.setOpacity(1);
            });

            primaryStage.initStyle(StageStyle.TRANSPARENT);

            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        primaryStage.show();
    }
}
