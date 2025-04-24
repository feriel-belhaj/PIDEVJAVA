package tn.esprit.workshop.gui;

import com.google.api.client.auth.oauth2.Credential;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.CRUD;
import tn.esprit.workshop.services.GoogleAuthService;
import tn.esprit.workshop.services.ServiceUser;
import tn.esprit.workshop.services.UserGetData;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class UtilisateurDashboard implements Initializable {


    @FXML
    private Button AddUser_AddBtn;

    @FXML
    private TableColumn<User, String> AddUser_ColAdd;

    @FXML
    private TableColumn<User, LocalDateTime> AddUser_ColDate;

    @FXML
    private TableColumn<User,  String> AddUser_ColMail;

    @FXML
    private TableColumn<User,  String> AddUser_ColNom;

    @FXML
    private TableColumn<User,  String> AddUser_ColPrenom;

    @FXML
    private TableColumn<User,  String> AddUser_ColRole;

    @FXML
    private TableColumn<User,  String> AddUser_ColSexe;

    @FXML
    private TableColumn<User,  String> AddUser_ColTel;

    @FXML
    private TableColumn<User,  String> AddUser_Colimg;

    @FXML
    private Button AddUser_DeleteBtn;

    @FXML
    private Button AddUser_ImportBtn;

    @FXML
    private Button AddUser_MajBtn;

    @FXML
    private Button AddUser_ResetBtn;

    @FXML
    private TableView<User> AddUser_TableView;

    @FXML
    private Button AddUser_btn;

    @FXML
    private AnchorPane AddUser_form;

    @FXML
    private AnchorPane User_MainForm;

    @FXML
    private TextField AddUser_search;

    @FXML
    private AnchorPane RoleUpdate_Form;

    @FXML
    private Button RoleUpdate_btn;

    @FXML
    private BarChart<String, Number> UserHome_Chart;

    @FXML
    private Label UserHome_NbrFemme;

    @FXML
    private Label UserHome_NbrHomme;

    @FXML
    private Label UserHome_NbrTotal;

    @FXML
    private Button UserHome_btn;

    @FXML
    private AnchorPane UserHome_form;

    @FXML
    private Label Utilisateur_UserName;

    @FXML
    private ImageView Utilisateur_Image;

    @FXML
    private TextField Utilisateur_add;

    @FXML
    private TextField Utilisateur_mail;

    @FXML
    private TextField Utilisateur_nom;

    @FXML
    private TextField Utilisateur_prenom;

    @FXML
    private PasswordField Utilisateur_pwd;

    @FXML
    private PasswordField Utilisateur_pwd2;

    @FXML
    private ComboBox<String> Utilisateur_role;

    @FXML
    private ComboBox<String> Utilisateur_sexe;

    @FXML
    private TextField Utilisateur_tel;

    @FXML
    private Button close;

    @FXML
    private Button logout;

    @FXML
    private Button minimize;

    @FXML
    private TableColumn<?, ?> utilisateur_majRole_ColAdd;

    @FXML
    private TableColumn<?, ?> utilisateur_majRole_ColDate;

    @FXML
    private TableColumn<?, ?> utilisateur_majRole_ColNom;

    @FXML
    private TableColumn<?, ?> utilisateur_majRole_ColPrenom;

    @FXML
    private TableColumn<?, ?> utilisateur_majRole_ColRole;

    @FXML
    private TableColumn<?, ?> utilisateur_majRole_ColSexe;

    @FXML
    private TableColumn<?, ?> utilisateur_majRole_ColTel;

    @FXML
    private TableColumn<?, ?> utilisateur_majRole_Colmail;

    @FXML
    private Label utilisateur_majRole_adresse;

    @FXML
    private Label utilisateur_majRole_date;

    @FXML
    private TextField utilisateur_majRole_id;

    @FXML
    private Label utilisateur_majRole_mail;

    @FXML
    private Button utilisateur_majRole_majbtn;

    @FXML
    private Label utilisateur_majRole_nom;

    @FXML
    private Label utilisateur_majRole_prenom;

    @FXML
    private Button utilisateur_majRole_resetBtn;

    @FXML
    private ComboBox<?> utilisateur_majRole_role;

    @FXML
    private Label utilisateur_majRole_sexe;

    @FXML
    private TableView<?> utilisateur_majRole_tableView;

    @FXML
    private Label utilisateur_majRole_tel;

    public void close() {
        System.exit(0);
    }

    public void minimize() {
        Stage stage = (Stage) User_MainForm.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> list = FXCollections.observableArrayList("Client","Artisant","Admin");
        Utilisateur_role.setItems(list);
        ObservableList<String> listSexe = FXCollections.observableArrayList("male","femelle");
        Utilisateur_sexe.setItems(listSexe);
        displayUsername();
        addUserShowListData();
        addUserSearch();
        NbrTotalUsers();
        NbrTotalFemme();
        NbrTotalHomme();
        StatUser();

    }
    @FXML
    void User_BackToFront(ActionEvent event) {
        System.out.println("User_BackToFront");

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));

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

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayUsername() {
        Utilisateur_UserName.setText(UserGetData.prenom + "  " + UserGetData.nom);
    }

    private double x = 0;
    private double y = 0;



    @FXML
    void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> option = alert.showAndWait();
        try {
            if (option.get().equals(ButtonType.OK)) {
                GoogleAuthService.logout();
                logout.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("/Utilisateur.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });

                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);

                    stage.setOpacity(.8);
                });

                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });

                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void switchForm(ActionEvent event) {

        if (event.getSource() == UserHome_btn) {
            UserHome_form.setVisible(true);
            AddUser_form.setVisible(false);
            RoleUpdate_Form.setVisible(false);

            UserHome_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #7b5442, #c8b09a);");
            AddUser_btn.setStyle("-fx-background-color:transparent");
            RoleUpdate_btn.setStyle("-fx-background-color:transparent");

//            homeTotalEmployees();
//            homeEmployeeTotalPresent();
//            homeTotalInactive();
//            homeChart();

        } else if (event.getSource() == AddUser_btn) {
            UserHome_form.setVisible(false);
            AddUser_form.setVisible(true);
            RoleUpdate_Form.setVisible(false);

            AddUser_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #7b5442, #c8b09a);");
            UserHome_btn.setStyle("-fx-background-color:transparent");
            RoleUpdate_btn.setStyle("-fx-background-color:transparent");



        } else if (event.getSource() == RoleUpdate_btn) {
            UserHome_form.setVisible(false);
            AddUser_form.setVisible(false);
            RoleUpdate_Form.setVisible(true);

            RoleUpdate_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #7b5442, #c8b09a);");
            AddUser_btn.setStyle("-fx-background-color:transparent");
            UserHome_btn.setStyle("-fx-background-color:transparent");

//            salaryShowListData();

        }

    }

    ServiceUser UserService =new ServiceUser();
    User user = new User();

    public void addUserSearch() {

        FilteredList<User> filter = new FilteredList<>(addEmployeeList, e -> true);

        AddUser_search.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(predicateUserData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateUserData.getId() != 0 && String.valueOf(predicateUserData.getId()).contains(searchKey)) {
                    return true;
                } else if (predicateUserData.getNom().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateUserData.getPrenom().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateUserData.getEmail().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateUserData.getAdresse().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateUserData.getRole().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateUserData.getSexe().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateUserData.getTelephone().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateUserData.getDate_inscription().toString().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<User> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(AddUser_TableView.comparatorProperty());
        AddUser_TableView.setItems(sortList);
    }

    private ObservableList<User> addEmployeeList;

    public void addUserShowListData() {
        addEmployeeList = UserService.showAll();
        AddUser_ColSexe.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        AddUser_ColNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        AddUser_ColPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        AddUser_ColRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        AddUser_ColMail.setCellValueFactory(new PropertyValueFactory<>("email"));
        AddUser_ColDate.setCellValueFactory(new PropertyValueFactory<>("date_inscription"));
        AddUser_ColAdd.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        AddUser_ColTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        AddUser_Colimg.setCellValueFactory(new PropertyValueFactory<>("image"));

        AddUser_TableView.setItems(addEmployeeList);


    }
    private Image img;

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

        File selectedFile = open.showOpenDialog(User_MainForm.getScene().getWindow());

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
                Utilisateur_Image.setImage(img);

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


    public void addUserSelect()
    {
        User SelectedUser = AddUser_TableView.getSelectionModel().getSelectedItem();
        int num = AddUser_TableView.getSelectionModel().getSelectedIndex();

        if((num-1) < -1)
            return;
        Utilisateur_prenom.setText(SelectedUser.getNom());
        Utilisateur_nom.setText(SelectedUser.getPrenom());
        Utilisateur_mail.setText(SelectedUser.getEmail());
        Utilisateur_pwd.setText(SelectedUser.getPassword());
        Utilisateur_pwd2.setText(SelectedUser.getPassword());
        Utilisateur_add.setText(SelectedUser.getAdresse());
        Utilisateur_sexe.setValue(SelectedUser.getSexe());
        Utilisateur_tel.setText(SelectedUser.getTelephone());
        Utilisateur_role.setValue(SelectedUser.getRole());

//        UserGetData.path =SelectedUser.getImage();
//        String uri = "file:" + SelectedUser.getImage();
//        img = new Image(uri,101, 127, false, true);
//        Utilisateur_Image.setImage(img);
        String imageName = SelectedUser.getImage();
        UserGetData.path = imageName;

        String basePath = "C:\\xampp\\htdocs\\img\\";
        File imageFile = new File(basePath + imageName);

        if (imageFile.exists()) {
            String uri = imageFile.toURI().toString();
            img = new Image(uri, 101, 127, false, true);
            Utilisateur_Image.setImage(img);
        } else {
            File defaultImage = new File(basePath + "profile.jpg");
            if (defaultImage.exists()) {
                String defaultUri = defaultImage.toURI().toString();
                img = new Image(defaultUri, 101, 127, false, true);
                Utilisateur_Image.setImage(img);
            } else {
                System.out.println("Image introuvable : ni " + imageFile.getName() + " ni profile.jpg");
            }
        }

    }

    public void addUserReset() {
        Utilisateur_prenom.setText("");
        Utilisateur_nom.setText("");
        Utilisateur_mail.setText("");
        Utilisateur_add.setText("");
        Utilisateur_sexe.getSelectionModel().clearSelection();
        Utilisateur_role.getSelectionModel().clearSelection();
        Utilisateur_pwd.setText("");
        Utilisateur_pwd2.setText("");
        Utilisateur_tel.setText("");
        Utilisateur_Image.setImage(null);
        UserGetData.path = "";
    }

    @FXML
    void ajouetrUtilisateur(ActionEvent event) {

        LocalDateTime sqlDate = LocalDateTime.now();

        try {
            Alert alert;
            if (Utilisateur_nom.getText().isEmpty()
                    || Utilisateur_prenom.getText().isEmpty()
                    || Utilisateur_mail.getText().isEmpty()
                    || Utilisateur_sexe.getSelectionModel().getSelectedItem() == null
                    || Utilisateur_pwd.getText().isEmpty()
                    || Utilisateur_pwd2.getText().isEmpty()
                    || Utilisateur_add.getText().isEmpty()
                    || Utilisateur_role.getSelectionModel().getSelectedItem() == null
                    || UserGetData.path == null || UserGetData.path.equals("")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            }
            if (!Utilisateur_mail.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Adresse e-mail invalide !");
                alert.showAndWait();
                return;
            }
            if (!Utilisateur_pwd.getText().equals(Utilisateur_pwd2.getText())) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Les mots de passe ne correspondent pas !");
                alert.showAndWait();
                return;
            }
            if (!Utilisateur_tel.getText().matches("\\d+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le numéro de téléphone doit contenir uniquement des chiffres !");
                alert.showAndWait();
                return;
            }

            if (!Utilisateur_prenom.getText().matches("[A-Za-z]+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le prénom ne doit contenir que des lettres !");
                alert.showAndWait();
                return;
            }
            if (!Utilisateur_nom.getText().matches("[A-Za-z]+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le nom ne doit contenir que des lettres !");
                alert.showAndWait();
                return;
            }
            if (!Utilisateur_pwd.getText().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,;:])[A-Za-z\\d@$!%*?&.,;:]{8,}$")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le mot de passe doit contenir au moins 8 caractères, avec une majuscule, un chiffre et un caractère spécial.");
                alert.showAndWait();
                return;
            }
            else {
                if (UserService.findByEmail(Utilisateur_mail.getText())!=null) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("L'Adresse mail " + Utilisateur_mail.getText() + " est  déjà existante!");
                    alert.showAndWait();
                } else {

                    String uri = Paths.get(UserGetData.path).getFileName().toString();
                    User u = new User(Utilisateur_nom.getText()
                             ,Utilisateur_prenom.getText()
                            ,Utilisateur_mail.getText()
                            ,Utilisateur_add.getText()
                             ,Utilisateur_pwd2.getText()
                             ,uri
                            ,Utilisateur_role.getSelectionModel().getSelectedItem()
                            ,Utilisateur_sexe.getSelectionModel().getSelectedItem()
                            ,Utilisateur_tel.getText(),
                            sqlDate

                    );
                    String Role;
                    if(Utilisateur_role.getSelectionModel().getSelectedItem().equals("Admin")){
                        Role = "ROLE_ADMIN";
                    }
                    else if (Utilisateur_role.getSelectionModel().getSelectedItem().equals("Client")){
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

                    addUserShowListData();
                    addUserReset();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void UpdateUtilisateur(ActionEvent event) {
        LocalDateTime sqlDate = LocalDateTime.now();
        try {
            Alert alert;
            if (Utilisateur_nom.getText().isEmpty()
                    || Utilisateur_prenom.getText().isEmpty()
                    || Utilisateur_mail.getText().isEmpty()
                    || Utilisateur_sexe.getSelectionModel().getSelectedItem() == null
                    || Utilisateur_pwd.getText().isEmpty()
                    || Utilisateur_pwd2.getText().isEmpty()
                    || Utilisateur_add.getText().isEmpty()
                    || Utilisateur_role.getSelectionModel().getSelectedItem() == null
                    || UserGetData.path == null || UserGetData.path.equals("")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            }
            if (!Utilisateur_mail.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Adresse e-mail invalide !");
                alert.showAndWait();
                return;
            }
            if (!Utilisateur_pwd.getText().equals(Utilisateur_pwd2.getText())) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Les mots de passe ne correspondent pas !");
                alert.showAndWait();
                return;
            }
            if (!Utilisateur_tel.getText().matches("\\d+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le numéro de téléphone doit contenir uniquement des chiffres !");
                alert.showAndWait();
                return;
            }

            if (!Utilisateur_prenom.getText().matches("[A-Za-z]+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le prénom ne doit contenir que des lettres !");
                alert.showAndWait();
                return;
            }
            if (!Utilisateur_nom.getText().matches("[A-Za-z]+")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le nom ne doit contenir que des lettres !");
                alert.showAndWait();
                return;
            }
            if (!Utilisateur_pwd.getText().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,;:])[A-Za-z\\d@$!%*?&.,;:]{8,}$")) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Le mot de passe doit contenir au moins 8 caractères, avec une majuscule, un chiffre et un caractère spécial.");
                alert.showAndWait();
                return;
            }

            else {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cofirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Vous etes sur de mettre à jour l'utilisateur: " + Utilisateur_mail.getText() + "?");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get().equals(ButtonType.OK)) {
                    String uri = Paths.get(UserGetData.path).getFileName().toString();
                    //String uri = Paths.get("C:", "xampp", "htdocs", "img", imgName).toString();

                    User u = new User(Utilisateur_nom.getText()
                            ,Utilisateur_prenom.getText()
                            ,Utilisateur_mail.getText()
                            ,Utilisateur_add.getText()
                            ,Utilisateur_pwd2.getText()
                            ,uri
                            ,Utilisateur_role.getSelectionModel().getSelectedItem()
                            ,Utilisateur_sexe.getSelectionModel().getSelectedItem()
                            ,Utilisateur_tel.getText(),
                            sqlDate

                    );
                    User SelectedUser = AddUser_TableView.getSelectionModel().getSelectedItem();
                    u.setId(SelectedUser.getId());
                    UserService.update(u);
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Mise à jour effectuée avec succès !");
                    alert.showAndWait();

                    addUserShowListData();
                    addUserReset();
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    void DeleteUtilisateur(ActionEvent event) {
        Alert alert;
        try {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cofirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir supprimer l'employé avec l'email: " + Utilisateur_mail.getText() + "?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)) {
                User SelectedUser = AddUser_TableView.getSelectionModel().getSelectedItem();
                UserService.delete(SelectedUser);
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Suppression effectuée avec succès !");
                alert.showAndWait();
                addUserShowListData();
                addUserReset();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public void NbrTotalUsers()
    {
        int nbr=0;
        nbr= UserService.homeTotalUsers();
        UserHome_NbrTotal.setText(String.valueOf(nbr));
    }
    public void NbrTotalFemme()
    {
        int nbr=0;
        nbr= UserService.homeTotalWomen();
        UserHome_NbrFemme.setText(String.valueOf(nbr));
    }
    public void NbrTotalHomme()
    {
        int nbr=0;
        nbr= UserService.homeTotalMen();
        UserHome_NbrHomme.setText(String.valueOf(nbr));
    }

    public void StatUser()
    {
        UserHome_Chart.getData().add(UserService.homeChart());
    }


}
