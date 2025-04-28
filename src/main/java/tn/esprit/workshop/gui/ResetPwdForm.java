package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tn.esprit.workshop.models.PasswordUtils;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.ServiceUser;
import tn.esprit.workshop.services.UserGetData;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

public class ResetPwdForm {

    @FXML
    private Button BackToLogin_btn;

    @FXML
    private Label User_CreateAccount;

    @FXML
    private Button User_ResetPwd_btn;

    @FXML
    private PasswordField User_SignUp_pwd1;

    @FXML
    private PasswordField User_SignUp_pwd2;
    private double x = 0;
    private double y = 0;


    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }


    @FXML
    void BackToLogin(ActionEvent event) {
        User_CreateAccount.getScene().getWindow().hide();

        try {
            Parent root  = FXMLLoader.load(getClass().getResource("/Utilisateur.fxml"));
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

    }

    @FXML
    void ConfirmPwd(ActionEvent event) {
        ServiceUser US = new ServiceUser();
        Alert alert;
        try {
            User u =US.findByEmail(UserGetData.email);
            if (u!=null)
            {
                if (!User_SignUp_pwd1.getText().equals(User_SignUp_pwd2.getText())) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Les mots de passe ne correspondent pas !");
                    alert.showAndWait();
                    return;
                } else if (User_SignUp_pwd1.getText().isEmpty() || User_SignUp_pwd2.getText().isEmpty() ) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Champs vide(s)!");
                    alert.showAndWait();
                    return;
                }
                else
                {
                    String hashedPassword = PasswordUtils.hashPassword(User_SignUp_pwd1.getText());
                    u.setPassword(hashedPassword);
                    System.out.println(u.getPassword());
                    US.updatePassword(u);
                    User_CreateAccount.getScene().getWindow().hide();

                    try {
                        Parent root  = FXMLLoader.load(getClass().getResource("/Utilisateur.fxml"));
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


                }
            }
            else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("user not found!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
