package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;


import javafx.scene.input.MouseEvent;

import javafx.stage.StageStyle;
import tn.esprit.workshop.services.UserGetData;

import java.io.IOException;


public class Utilisateur {
    private double x = 0;
    private double y = 0;
    @FXML
    private Button User_login_btn;

    @FXML
    private TextField User_login_mail;

    @FXML
    private PasswordField User_login_pwd;

    @FXML
    private AnchorPane Utilisateur_main_form;

    private ServiceUser serviceUser = new ServiceUser();
    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void login(ActionEvent event) {
        String email = User_login_mail.getText();
        String password = User_login_pwd.getText();
        Alert alert;
        try {
            User loggedUser = serviceUser.loginUser(email, password);
            if (User_login_mail.getText().isEmpty() || User_login_pwd.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            }
            else {
                if (loggedUser != null) {
                    UserGetData.nom=loggedUser.getNom();
                    UserGetData.prenom=loggedUser.getPrenom();
                    UserGetData.id=loggedUser.getId();

                    System.out.println(loggedUser);
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Login");
                    alert.showAndWait();
                    User_login_btn.getScene().getWindow().hide();

                    try {
                        Parent root  = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
                        Stage stage = new Stage();
                        Scene scene = new Scene(root);
                        root.setOnMousePressed((MouseEvent Mevent) ->{
                            x = Mevent.getSceneX();
                            y = Mevent.getSceneY();
                        });

                        root.setOnMouseDragged((MouseEvent Mevent) ->{
                            stage.setX(Mevent.getScreenX() - x);
                            stage.setY(Mevent.getScreenY() - y);
                        });

                        stage.initStyle(StageStyle.TRANSPARENT);
                        stage.setScene(scene);
                        stage.show();


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong Username/Password");
                    alert.showAndWait();


                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

}
