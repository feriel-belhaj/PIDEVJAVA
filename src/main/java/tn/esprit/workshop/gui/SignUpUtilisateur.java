package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.ServiceUser;
import tn.esprit.workshop.services.UserGetData;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class SignUpUtilisateur implements Initializable {
    private double x = 0;
    private double y = 0;

    @FXML
    private StackPane UserSignUp_MainForm;

    @FXML
    private TextField User_SignUp_Mail;

    @FXML
    private TextField User_SignUp_adresse;

    @FXML
    private TextField User_SignUp_nom;

    @FXML
    private TextField User_SignUp_prenom;

    @FXML
    private PasswordField User_SignUp_pwd1;

    @FXML
    private PasswordField User_SignUp_pwd2;

    @FXML
    private ComboBox<String> User_SignUp_role;

    @FXML
    private ComboBox<String> User_SignUp_sexe;

    @FXML
    private TextField User_SignUp_tel;

    @FXML
    private Button User_login_btn;

    @FXML
    private AnchorPane Utilisateur_main_form;

    @FXML
    private ImageView User_SignUp_Img;

    @FXML
    void close(ActionEvent event) {System.exit(0);}



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> list = FXCollections.observableArrayList("Client","Artisant");
        User_SignUp_role.setItems(list);
        ObservableList<String> listSexe = FXCollections.observableArrayList("male","femelle");
        User_SignUp_sexe.setItems(listSexe);
    }
    public void addUserInsertImage() {

//        FileChooser open = new FileChooser();
//        File file = open.showOpenDialog(User_MainForm.getScene().getWindow());
//
//        if (file != null) {
//            UserGetData.path = file.getAbsolutePath();
//
//            img = new Image(file.toURI().toString(), 101, 127, false, true);
//            Utilisateur_Image.setImage(img);
//        }
        FileChooser open = new FileChooser();
        open.setTitle("Choisir une image");

        open.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = open.showOpenDialog(UserSignUp_MainForm.getScene().getWindow());

        if (selectedFile != null) {
            try {
                File directory = new File("C:\\\\xampp\\\\htdocs\\\\img\\\\");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();

                File destinationFile = new File(directory, fileName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                UserGetData.path = destinationFile.getAbsolutePath();
                System.out.println(UserGetData.path);
                Image img = new Image(destinationFile.toURI().toString(), 101, 127, false, true);
                User_SignUp_Img.setImage(img);

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors du téléchargement de l'image !");
                alert.showAndWait();
            }
        }
    }
    ServiceUser UserService =new ServiceUser();
    @FXML
    void SignUp(ActionEvent event) {
        LocalDateTime sqlDate = LocalDateTime.now();

        try {
            Alert alert;
            if (User_SignUp_nom.getText().isEmpty()
                    || User_SignUp_prenom.getText().isEmpty()
                    || User_SignUp_Mail.getText().isEmpty()
                    || User_SignUp_sexe.getSelectionModel().getSelectedItem() == null
                    || User_SignUp_pwd1.getText().isEmpty()
                    || User_SignUp_pwd2.getText().isEmpty()
                    || User_SignUp_adresse.getText().isEmpty()
                    || User_SignUp_role.getSelectionModel().getSelectedItem() == null
                    || UserGetData.path == null || UserGetData.path.equals("")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            }
            if (!User_SignUp_Mail.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Adresse e-mail invalide !");
                alert.showAndWait();
                return;
            }
            if (!User_SignUp_pwd1.getText().equals(User_SignUp_pwd2.getText())) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Les mots de passe ne correspondent pas !");
                alert.showAndWait();
                return;
            }
            if (!User_SignUp_tel.getText().matches("\\d+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le numéro de téléphone doit contenir uniquement des chiffres !");
                alert.showAndWait();
                return;
            }

            if (!User_SignUp_prenom.getText().matches("[A-Za-z]+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le prénom ne doit contenir que des lettres !");
                alert.showAndWait();
                return;
            }
            if (!User_SignUp_nom.getText().matches("[A-Za-z]+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le nom ne doit contenir que des lettres !");
                alert.showAndWait();
                return;
            }
            if (!User_SignUp_pwd1.getText().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,;:])[A-Za-z\\d@$!%*?&.,;:]{8,}$")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le mot de passe doit contenir au moins 8 caractères, avec une majuscule, un chiffre et un caractère spécial.");
                alert.showAndWait();
                return;
            }
            else {
                if (UserService.findByEmail(User_SignUp_Mail.getText())!=null) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("L'Adresse mail " + User_SignUp_Mail.getText() + " est  déjà existante!");
                    alert.showAndWait();
                } else {

                    String uri = Paths.get(UserGetData.path).getFileName().toString();
                    User u = new User(User_SignUp_nom.getText()
                            ,User_SignUp_prenom.getText()
                            ,User_SignUp_Mail.getText()
                            ,User_SignUp_adresse.getText()
                            ,User_SignUp_pwd2.getText()
                            ,uri
                            ,User_SignUp_role.getSelectionModel().getSelectedItem()
                            ,User_SignUp_sexe.getSelectionModel().getSelectedItem()
                            ,User_SignUp_tel.getText(),
                            sqlDate

                    );
                    String Role;
                    if(User_SignUp_role.getSelectionModel().getSelectedItem().equals("Admin")){
                        Role = "ROLE_ADMIN";
                    }
                    else if (User_SignUp_role.getSelectionModel().getSelectedItem().equals("Client")){
                        Role = "ROLE_CLIENT";
                    }
                    else
                        Role = "ROLE_ARTISAN";
                    u.setRole(Role);
                    UserService.insert(u);
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Added!");
                    alert.showAndWait();
                    User_login_btn.getScene().getWindow().hide();

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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
