package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServiceCandidature;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class DetailsCandidatureController {

    @FXML
    private Label nomPartenariatLabel;

    @FXML
    private Label typeCollabLabel;

    @FXML
    private Label datePostulationLabel;

    @FXML
    private TextArea motivationTextArea;

    @FXML
    private ImageView partenariatImage;

    @FXML
    private Label statutLabel;

    @FXML
    private Button btnVoirPortfolio;

    @FXML
    private Button btnTelechargerCV;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnRetour;

    private Candidature candidature;
    private ServiceCandidature serviceCandidature;
    private ListeCandidaturesController listeController;

    @FXML
    public void initialize() {
        serviceCandidature = new ServiceCandidature();
    }

    public void initData(Candidature candidature, ListeCandidaturesController listeController) {
        this.candidature = candidature;
        this.listeController = listeController;

        // Formatage des dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Mise à jour de l'interface avec les données de la candidature
        Partenariat partenariat = candidature.getPartenariat();
        if (partenariat != null) {
            nomPartenariatLabel.setText(partenariat.getNom());
            String imagePath = partenariat.getImage();
            String basePath = "C:\\xampp\\htdocs\\img";
            // Chargement de l'image du partenariat
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(basePath, imagePath);
                if (imageFile.exists()) {
                    partenariatImage.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    partenariatImage.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
                }
            } else {
                partenariatImage.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
            }
        }

        typeCollabLabel.setText(candidature.getTypeCollab());
        datePostulationLabel.setText(dateFormat.format(candidature.getDatePostulation()));
        motivationTextArea.setText(candidature.getMotivation());
    }

    @FXML
    private void voirPortfolio() {
        if (candidature != null && candidature.getPortfolio() != null) {
            ouvrirFichier(candidature.getPortfolio());
        } else {
            afficherErreur("Erreur", "Le portfolio n'est pas disponible");
        }
    }

    @FXML
    private void telechargerCV() {
        if (candidature != null && candidature.getCv() != null) {
            ouvrirFichier(candidature.getCv());
        } else {
            afficherErreur("Erreur", "Le CV n'est pas disponible");
        }
    }

    @FXML
    private void supprimerCandidature() {
        // Demander confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la candidature");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette candidature ? Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer la candidature
                serviceCandidature.delete(candidature);

                // Afficher un message de succès
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("La candidature a été supprimée avec succès !");
                successAlert.showAndWait();

                // Rafraîchir la liste des candidatures dans ListeCandidaturesController
                if (listeController != null) {
                    listeController.chargerCandidatures();
                }

                // Fermer la fenêtre de détails
                Stage currentStage = (Stage) btnSupprimer.getScene().getWindow();
                currentStage.close();

            } catch (SQLException e) {
                afficherErreur("Erreur", "Impossible de supprimer la candidature: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void retourListeCandidatures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCandidatures.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Candidatures");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) btnRetour.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la liste des candidatures: " + e.getMessage());
        }
    }

    private void ouvrirFichier(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                afficherErreur("Fichier introuvable", "Le fichier n'existe pas: " + filePath);
            }
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le fichier: " + e.getMessage());
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}