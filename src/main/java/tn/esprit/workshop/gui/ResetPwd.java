package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tn.esprit.workshop.models.EmailSender;
import tn.esprit.workshop.models.OTPGenerator;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.ServiceUser;
import tn.esprit.workshop.services.UserGetData;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;

public class ResetPwd {

    @FXML
    private Label User_CreateAccount;

    @FXML
    private Button User_ResetPwd_btn;

    @FXML
    private TextField User_ResetPwd_mail;

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
    EmailSender emailSender = new EmailSender();
    @FXML
    void SendResetLink(ActionEvent event) {
        String mail = User_ResetPwd_mail.getText();
        ServiceUser userService = new ServiceUser();
        Alert alert;
        String otp = OTPGenerator.generateOTP();
        try {
            User u= userService.findByEmail(mail);
            if (u != null) {
                try {
                    emailSender.sendOTP(mail,otp);
                    User_ResetPwd_mail.getScene().getWindow().hide();
                    UserGetData.email=u.getEmail();
                    TextInputDialog otpDialog = new TextInputDialog();
                    otpDialog.setTitle("OTP Verification");
                    otpDialog.setHeaderText("Please enter the OTP sent to your email");
                    otpDialog.showAndWait().ifPresent(otpInput -> {
                        if (otpInput.equals(otp)) {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Information Message");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("Code Correct");
                            successAlert.showAndWait();
                            try {
                                Parent root  = FXMLLoader.load(getClass().getResource("/ResetPwdForm.fxml"));
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
                        else {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error Message");
                            errorAlert.setHeaderText(null);
                            errorAlert.setContentText("Invalid OTP. Please try again.");
                            errorAlert.showAndWait();
                        }

                    });

                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Email introuvable!");
                alert.showAndWait();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



}
