package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tn.esprit.workshop.entities.Formation;
import tn.esprit.workshop.services.FormationReserveeService;
import tn.esprit.workshop.services.UserGetData;
import tn.esprit.workshop.services.UtilisateurService;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class FormationDetailsController implements Initializable {
    @FXML private VBox root;
    @FXML private ImageView formationImage;
    @FXML private Label lblTitre;
    @FXML private Label lblDescription;
    @FXML private Label lblDuree;
    @FXML private Label lblPrix;
    @FXML private Label lblNiveau;
    @FXML private Label lblDateDebut;
    @FXML private Label lblDateFin;
    @FXML private Label lblLieu;
    @FXML private Label lblFormateur;

    private Formation formation;
    private FormationReserveeService reservationService;
    private UtilisateurService utilisateurService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reservationService = new FormationReserveeService();
        utilisateurService = new UtilisateurService();
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
        updateUI();
    }

    private void updateUI() {
        if (formation != null) {
            lblTitre.setText(formation.getTitre());
            lblDescription.setText(formation.getDescription());
            lblDuree.setText(formation.getDuree());
            lblPrix.setText(formation.getPrix() + " DT");
            lblNiveau.setText(formation.getNiveau());
            lblDateDebut.setText(formation.getDateDeb().format(dateFormatter));
            lblDateFin.setText(formation.getDateFin().format(dateFormatter));
            lblLieu.setText(formation.getEmplacement());
            lblFormateur.setText(formation.getOrganisateur());

            // Chargement de l'image
            if (formation.getImage() != null && !formation.getImage().isEmpty()) {
                try {
                    URL resourceUrl = getClass().getResource("/" + formation.getImage());
                    if (resourceUrl != null) {
                        Image image = new Image(resourceUrl.toExternalForm());
                        formationImage.setImage(image);
                    } else {
                        showNoImageLabel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showNoImageLabel();
                }
            } else {
                showNoImageLabel();
            }
        }
    }

    private void showNoImageLabel() {
        // Créer un label pour indiquer l'absence d'image
        Label noImageLabel = new Label("Pas d'image");
        noImageLabel.getStyleClass().add("no-image-label");
        
        // Remplacer l'ImageView par le label
        StackPane imageContainer = (StackPane) formationImage.getParent();
        if (imageContainer != null) {
            imageContainer.getChildren().clear();
            imageContainer.getChildren().add(noImageLabel);
        }
    }

    @FXML
    private void handleReserver() {
        if (utilisateurService.getCurrentUser() == null) {
            showAlert(Alert.AlertType.WARNING, "Connexion requise", "Veuillez vous connecter pour réserver une formation.");
            return;
        }

        try {
            reservationService.reserver(formation.getId(), UserGetData.id);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation réservée avec succès!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la réservation: " + e.getMessage());
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientView.fxml"));
            Parent clientView = loader.load();
            
            // Trouver le contentArea du MainMenu
            Node currentNode = root.getScene().getRoot();
            while (currentNode != null && !(currentNode instanceof VBox)) {
                currentNode = currentNode.getParent();
            }
            
            if (currentNode != null) {
                VBox mainMenu = (VBox) currentNode;
                StackPane contentArea = (StackPane) mainMenu.getChildren().get(mainMenu.getChildren().size() - 1);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(clientView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à la liste des formations.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 