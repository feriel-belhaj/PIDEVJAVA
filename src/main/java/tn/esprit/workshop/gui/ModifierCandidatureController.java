package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.services.ServiceCandidature;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ModifierCandidatureController {

    @FXML
    private ComboBox<String> typeCollabCombo;

    @FXML
    private TextArea motivationTextArea;

    @FXML
    private DatePicker datePostulation;

    @FXML
    private Label cvLabel;

    @FXML
    private Label portfolioLabel;

    @FXML
    private Button btnMettreAJour;

    @FXML
    private Button btnFermer;

    private Candidature candidature;
    private ServiceCandidature serviceCandidature;
    private String nouveauCV;
    private String nouveauPortfolio;

    @FXML
    public void initialize() {
        serviceCandidature = new ServiceCandidature();

        // Initialiser les options du ComboBox
        typeCollabCombo.getItems().addAll(
                "Stage",
                "Permanent",
                "Temporaire",
                "Projet spécifique",
                "Sponsoring",
                "Mécénat"
        );
    }

    public void initData(Candidature candidature) {
        this.candidature = candidature;

        // Remplir les champs avec les données existantes
        typeCollabCombo.setValue(candidature.getTypeCollab());
        motivationTextArea.setText(candidature.getMotivation());

        // Convertir Date en LocalDate pour DatePicker
        if (candidature.getDatePostulation() != null) {
            datePostulation.setValue(candidature.getDatePostulation().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            datePostulation.setValue(LocalDate.now());
        }

        // Afficher les noms de fichiers existants
        if (candidature.getCv() != null && !candidature.getCv().isEmpty()) {
            File cvFile = new File(candidature.getCv());
            cvLabel.setText(cvFile.getName());
            nouveauCV = candidature.getCv();
        }

        if (candidature.getPortfolio() != null && !candidature.getPortfolio().isEmpty()) {
            File portfolioFile = new File(candidature.getPortfolio());
            portfolioLabel.setText(portfolioFile.getName());
            nouveauPortfolio = candidature.getPortfolio();
        }
    }

    @FXML
    private void choisirCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre CV");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx")
        );

        Stage stage = (Stage) btnMettreAJour.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            nouveauCV = selectedFile.getAbsolutePath();
            cvLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void choisirPortfolio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre Portfolio");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx"),
                new FileChooser.ExtensionFilter("Fichiers Compressés", "*.zip", "*.rar")
        );

        Stage stage = (Stage) btnMettreAJour.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            nouveauPortfolio = selectedFile.getAbsolutePath();
            portfolioLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void voirCV() {
        if (candidature != null && candidature.getCv() != null && !candidature.getCv().isEmpty()) {
            ouvrirFichier(candidature.getCv());
        } else {
            afficherErreur("Erreur", "Aucun CV n'est disponible");
        }
    }

    @FXML
    private void voirPortfolio() {
        if (candidature != null && candidature.getPortfolio() != null && !candidature.getPortfolio().isEmpty()) {
            ouvrirFichier(candidature.getPortfolio());
        } else {
            afficherErreur("Erreur", "Aucun portfolio n'est disponible");
        }
    }

    @FXML
    private void mettreAJour() {
        // Vérification des champs obligatoires
        if (typeCollabCombo.getValue() == null ||
                motivationTextArea.getText().isEmpty() ||
                datePostulation.getValue() == null) {

            afficherErreur("Erreur de validation", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            // Mettre à jour les données de la candidature
            candidature.setTypeCollab(typeCollabCombo.getValue());
            candidature.setMotivation(motivationTextArea.getText());

            // Convertir LocalDate en Date
            Date datePost = Date.from(datePostulation.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            candidature.setDatePostulation(datePost);

            // Mettre à jour les fichiers si nécessaire
            if (nouveauCV != null) {
                candidature.setCv(nouveauCV);
            }

            if (nouveauPortfolio != null) {
                candidature.setPortfolio(nouveauPortfolio);
            }

            // Enregistrer les modifications
            serviceCandidature.update(candidature);

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("La candidature a été mise à jour avec succès !");
            successAlert.showAndWait();

            // Rediriger vers la liste des candidatures
            ouvrirListeCandidatures();

        } catch (SQLException e) {
            afficherErreur("Erreur", "Impossible de mettre à jour la candidature: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void fermer() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }

    private void ouvrirListeCandidatures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCandidatures.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Candidatures");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) btnMettreAJour.getScene().getWindow();
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