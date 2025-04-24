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
    private Button btnVoirPortfolio;

    @FXML
    private Button btnTelechargerCV;

    @FXML
    private Button btnSupprimer;

    private Candidature candidature;
    private ListeCandidaturesController listeController;
    private ServiceCandidature serviceCandidature;

    private final String FILE_DESTINATION_PATH = "C:\\xampp\\htdocs\\img\\"; // Chemin du dossier où les fichiers CV et portfolio sont stockés

    @FXML
    public void initialize() {
        serviceCandidature = new ServiceCandidature();
        // Ajouter les effets de survol pour les boutons
        addHoverEffect(btnVoirPortfolio);
        addHoverEffect(btnTelechargerCV);
    }

    private void addHoverEffect(Button button) {
        String defaultStyle = "-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 20;";
        String hoverStyle = "-fx-background-color: #357ABD; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 20;";

        button.setStyle(defaultStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
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

            // Ajouter des logs pour déboguer les chemins des fichiers
            System.out.println("Partenariat Image Filename: " + partenariat.getImage());
            System.out.println("CV Filename: " + candidature.getCv());

            // Chargement de l'image du partenariat
            if (partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                String imageName = partenariat.getImage().toLowerCase();
                if (imageName.endsWith(".jpg") || imageName.endsWith(".jpeg") || imageName.endsWith(".png") || imageName.endsWith(".gif")) {
                    File imageFile = new File(FILE_DESTINATION_PATH, imageName);
                    System.out.println("Attempting to load partnership image from: " + imageFile.getAbsolutePath());
                    if (imageFile.exists()) {
                        partenariatImage.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        System.err.println("Partnership image file not found: " + imageFile.getAbsolutePath());
                        partenariatImage.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
                    }
                } else {
                    System.err.println("Partnership image is not a valid image file: " + imageName);
                    partenariatImage.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
                }
            } else {
                System.err.println("Partnership image is null or empty");
                partenariatImage.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
            }
        } else {
            System.err.println("Partenariat is null for this candidature");
            partenariatImage.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
        }

        typeCollabLabel.setText(candidature.getTypeCollab());
        datePostulationLabel.setText(candidature.getDatePostulation() != null ? dateFormat.format(candidature.getDatePostulation()) : "Date non disponible");
        motivationTextArea.setText(candidature.getMotivation());
    }

    @FXML
    private void voirPortfolio() {
        if (candidature != null && candidature.getPortfolio() != null && !candidature.getPortfolio().isEmpty()) {
            ouvrirFichier(candidature.getPortfolio());
        } else {
            afficherErreur("Erreur", "Le portfolio n'est pas disponible");
        }
    }

    @FXML
    private void telechargerCV() {
        if (candidature != null && candidature.getCv() != null && !candidature.getCv().isEmpty()) {
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
            Stage currentStage = (Stage) btnVoirPortfolio.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la liste des candidatures: " + e.getMessage());
        }
    }

    private void ouvrirFichier(String fileName) {
        try {
            // Vérifier si le nom du fichier est null ou vide
            if (fileName == null || fileName.trim().isEmpty()) {
                System.err.println("Nom du fichier invalide: " + fileName);
                afficherErreur("Erreur", "Le nom du fichier est invalide.");
                return;
            }

            // Construire le chemin complet en combinant le dossier et le nom du fichier
            File file = new File(FILE_DESTINATION_PATH, fileName);
            System.out.println("Tentative d'ouverture du fichier: " + file.getAbsolutePath());

            // Vérifier si le fichier existe
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
                System.out.println("Fichier ouvert avec succès: " + file.getAbsolutePath());
            } else {
                System.err.println("Fichier introuvable: " + file.getAbsolutePath());
                afficherErreur("Fichier introuvable", "Le fichier n'existe pas: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir le fichier: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'ouverture du fichier: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
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