package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tn.esprit.workshop.entities.Formation;
import tn.esprit.workshop.services.FormationReserveeService;
import tn.esprit.workshop.services.UtilisateurService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FormationDetailsController implements Initializable {
    @FXML
    private VBox root;
    
    @FXML
    private ImageView formationImage;
    
    @FXML
    private Label lblTitre;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblDuree;
    @FXML
    private Label lblPrix;
    @FXML
    private Label lblNiveau;
    @FXML
    private Label lblDateDebut;
    @FXML
    private Label lblDateFin;
    @FXML
    private Label lblLieu;
    @FXML
    private Label lblFormateur;

    private Formation formation;
    private FormationReserveeService reservationService;
    private UtilisateurService utilisateurService;
    private Parent previousView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reservationService = new FormationReserveeService();
        utilisateurService = new UtilisateurService();
    }

    public void setPreviousView(Parent view) {
        this.previousView = view;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
        updateUI();
    }

    private void updateUI() {
        if (formation != null) {
            // Image
            try {
                if (formation.getImage() != null && !formation.getImage().isEmpty()) {
                    URL imageUrl = getClass().getResource("/" + formation.getImage());
                    if (imageUrl != null) {
                        formationImage.setImage(new Image(imageUrl.toExternalForm()));
                    } else {
                        formationImage.setStyle("-fx-background-color: #e0e0e0;");
                    }
                } else {
                    formationImage.setStyle("-fx-background-color: #e0e0e0;");
                }
            } catch (Exception e) {
                formationImage.setStyle("-fx-background-color: #e0e0e0;");
            }

            // Texte
            lblTitre.setText(formation.getTitre());
            lblDescription.setText(formation.getDescription());
            lblDuree.setText("Durée: " + formation.getDuree() + " heures");
            lblPrix.setText("Prix: " + formation.getPrix() + " DT");
            lblNiveau.setText("Niveau: " + formation.getNiveau());
            lblDateDebut.setText("Date de début: " + formation.getDateDeb());
            lblDateFin.setText("Date de fin: " + formation.getDateFin());
            lblLieu.setText("Lieu: " + formation.getEmplacement());
            lblFormateur.setText("Formateur: " + formation.getOrganisateur());
        }
    }

    @FXML
    private void handleReturn() {
        try {
            if (root != null && root.getParent() != null) {
                StackPane parent = (StackPane) root.getParent();
                parent.getChildren().clear();
                if (previousView != null) {
                    parent.getChildren().add(previousView);
                } else {
                    // Fallback au cas où previousView est null
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientView.fxml"));
                    Parent clientView = loader.load();
                    parent.getChildren().add(clientView);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la vue précédente.");
        }
    }

    @FXML
    private void handleReserve() {
        if (formation != null) {
            // Vérifier si l'utilisateur est déjà connecté
            if (utilisateurService.getCurrentUser() == null) {
                showAlert("Erreur", "Vous devez être connecté pour réserver une formation.");
                return;
            }

            // Vérifier si la formation est déjà réservée
            if (reservationService.estDejaReservee(formation.getId(), utilisateurService.getCurrentUser().getId())) {
                showAlert("Information", "Vous avez déjà réservé cette formation.");
                return;
            }

            // Demander confirmation
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de réservation");
            confirmation.setHeaderText("Voulez-vous réserver cette formation ?");
            confirmation.setContentText("Formation: " + formation.getTitre());

            if (confirmation.showAndWait().get() == ButtonType.OK) {
                // Effectuer la réservation
                reservationService.reserverFormation(
                    formation.getId(),
                    utilisateurService.getCurrentUser().getId(),
                    utilisateurService.getCurrentUser().getNom(),
                    utilisateurService.getCurrentUser().getPrenom()
                );

                showAlert("Succès", "Votre réservation a été enregistrée avec succès.");
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 