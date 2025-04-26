package tn.esprit.workshop.gui;

import com.google.api.services.gmail.Gmail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.workshop.models.EmailSender;
import tn.esprit.workshop.models.FaceRecognitionClient;
import tn.esprit.workshop.models.OTPGenerator;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.GoogleAuthService;
import tn.esprit.workshop.services.ServiceUser;
import tn.esprit.workshop.models.FaceRecognitionClient;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import tn.esprit.workshop.models.User;

import javax.swing.*;
import java.io.File;

import javafx.scene.input.MouseEvent;

import javafx.stage.StageStyle;
import tn.esprit.workshop.services.UserGetData;

import java.io.IOException;


public class Utilisateur {
    private double x = 0;
    private double y = 0;

    @FXML
    private Label User_CreateAccount;

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
                    String otp = OTPGenerator.generateOTP(); // Génération de l'OTP
                    try {
                        EmailSender.sendOTP(email, otp); // Envoi de l'OTP par email
                    } catch (Exception e) {
                        e.printStackTrace();
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Failed to send OTP email.");
                        alert.showAndWait();
                        return;
                    }
                    TextInputDialog otpDialog = new TextInputDialog();
                    otpDialog.setTitle("OTP Verification");
                    otpDialog.setHeaderText("Please enter the OTP sent to your email");
                    otpDialog.showAndWait().ifPresent(otpInput -> {
                        if (otpInput.equals(otp)) {
                            UserGetData.nom = loggedUser.getNom();
                            UserGetData.prenom = loggedUser.getPrenom();
                            UserGetData.id = loggedUser.getId();

                            System.out.println(loggedUser);
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Information Message");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("Successfully Login");
                            successAlert.showAndWait();
                            User_login_btn.getScene().getWindow().hide();

                            try {
                                Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
                                Stage stage = new Stage();
                                Scene scene = new Scene(root);
                                root.setOnMousePressed((MouseEvent Mevent) -> {
                                    x = Mevent.getSceneX();
                                    y = Mevent.getSceneY();
                                });

                                root.setOnMouseDragged((MouseEvent Mevent) -> {
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
    @FXML
    void SwitchSignUp(MouseEvent event) {
        User_CreateAccount.getScene().getWindow().hide();

        try {
            Parent root  = FXMLLoader.load(getClass().getResource("/SignUpUtilisateur.fxml"));
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
    private void onGoogleLogin() {
        try {
            Gmail service = GoogleAuthService.getGmailService();


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Authentification Google");
            alert.setHeaderText("Succès de l'authentification");
            alert.setContentText("Vous êtes maintenant connecté avec Google !");
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

            String userEmail = service.users().getProfile("me").execute().getEmailAddress();
            ServiceUser serviceUser = new ServiceUser();

            try {
                User user =serviceUser.findByEmail(userEmail);
                UserGetData.nom=user.getNom();
                UserGetData.prenom=user.getPrenom();
                UserGetData.id=user.getId();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Email de l'utilisateur : " + userEmail);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'authentification");
            alert.setHeaderText("Erreur lors de l'authentification");
            alert.setContentText("Erreur lors de la communication avec les services Google : " + e.getMessage());
            alert.showAndWait();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de sécurité");
            alert.setHeaderText("Erreur de sécurité lors de l'authentification");
            alert.setContentText("Une erreur de sécurité est survenue : " + e.getMessage());
            alert.showAndWait();
        }
    }




    public void captureAndLogin() {
        System.load("C:\\Users\\Fatma\\IdeaProjects\\ArtizinaJava\\venv\\Lib\\site-packages\\opencv_java4100.dll");

        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Erreur : Impossible d'accéder à la caméra.");
            return;
        }

        Mat frame = new Mat();

        camera.read(frame);


        if (!frame.empty()) {
            String outputPath = "C:\\xampp\\htdocs\\img\\image_capture.jpg";
            boolean isSaved = Imgcodecs.imwrite(outputPath, frame);

            if (isSaved) {
                System.out.println("Image capturée et sauvegardée avec succès.");
            } else {
                System.out.println("Erreur lors de la sauvegarde de l'image.");
            }

            try {
                User user = FaceRecognitionClient.sendImageToFlask(outputPath);
                Alert alert;
                if (user != null) {
                    System.out.println("Connexion réussie pour l'utilisateur : " + user.getNom());
                    User_login_mail.setText(user.getEmail());
                    User_login_pwd.setText(user.getPassword());
                    User loggedUser = user;
                    UserGetData.nom = loggedUser.getNom();
                    UserGetData.prenom = loggedUser.getPrenom();
                    UserGetData.id = loggedUser.getId();

                    System.out.println(loggedUser);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Information Message");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Successfully Login");
                    successAlert.showAndWait();
                    User_login_btn.getScene().getWindow().hide();

                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
                        Stage stage = new Stage();
                        Scene scene = new Scene(root);
                        root.setOnMousePressed((MouseEvent Mevent) -> {
                            x = Mevent.getSceneX();
                            y = Mevent.getSceneY();
                        });

                        root.setOnMouseDragged((MouseEvent Mevent) -> {
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
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Aucun utilisateur trouvé avec cette image.");
                    alert.showAndWait();


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Aucune image capturée.");
        }

        camera.release();

    }


}
