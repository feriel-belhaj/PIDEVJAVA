package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private ImageView logoImage;

    @FXML
    private Button logout;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger le logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.out.println("Erreur de chargement du logo: " + e.getMessage());
        }
        
        // Charger l'interface des formations par défaut
        showClientView();
    }

    @FXML
    private void showFormations() {
        switchToBackOffice("/fxml/Formation.fxml");
    }

    @FXML
    private void showCertificats() {
        switchToBackOffice("/fxml/Certificat.fxml");
    }

    @FXML
    private void ShowDashboard() {switchToBackOffice("/dashboardUser.fxml");}


    private void switchToBackOffice(String initialView) {
        try {
            // Charger le MainMenuBack.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenuBack.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            // Créer une nouvelle scène


            root.setOnMousePressed((MouseEvent event) -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });

            root.setOnMouseDragged((MouseEvent event) -> {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
                stage.setOpacity(0.8);
            });

            root.setOnMouseReleased((MouseEvent event) -> {
                stage.setOpacity(1);
            });


            // Obtenir la fenêtre actuelle
            stage.setScene(scene);
            stage.show();

            // Fermer la fenêtre actuelle (celle où il y avait le contentArea)
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.close();

            // Changer la scène
            //stage.setScene(scene);
            
            // Obtenir le contrôleur et charger la vue initiale
            MainMenuBackController controller = loader.getController();
            if (initialView.equals("/fxml/Formation.fxml")) {
                controller.showFormations();
            } else if (initialView.equals("/fxml/Certificat.fxml")) {
                controller.showCertificats();
            } else if (initialView.equals("/dashboardUser.fxml")) {
                controller.ShowDashboard();
            }
            else if (initialView.equals("/ListeCandidatures.fxml")) {
                controller.ShowCandidatures();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement du back office: " + e.getMessage());
        }
    }

    @FXML
    public void showClientView() {
        loadPage("/fxml/ClientView.fxml");
    }

    @FXML
    private void showAccueil() {
        loadPage("/fxml/Accueil.fxml");
    }

    @FXML
    private void showAbout() {
        loadPage("/fxml/About.fxml");
    }

    @FXML
    private void showPartenariats() {
        loadPage("/ListePartenariats.fxml");
    }

    @FXML
    private void showEvenements() {
        loadPage("/tn/esprit/workshop/resources/EvenementClient.fxml");
    }

    @FXML
    private void showProduits() {
        loadPage("/store.fxml");
    }

    @FXML
    private void showCommande() {
        loadPage("/commandeclient.fxml");
    }



    @FXML
    private void quit() {
        Platform.exit();
    }

    private void loadPage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement de la page " + fxml + ": " + e.getMessage());
        }
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
} 