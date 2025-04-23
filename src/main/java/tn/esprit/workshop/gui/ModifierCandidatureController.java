package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.services.ServiceCandidature;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @FXML
    private Label errorDatePostulation;

    @FXML
    private Label errorCv;

    @FXML
    private Label errorPortfolio;

    @FXML
    private Label errorTypeCollaboration;

    @FXML
    private Label errorMotivation;

    private Candidature candidature;
    private ServiceCandidature serviceCandidature;
    private String nouveauCV;
    private String nouveauPortfolio;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB en octets
    private static final List<String> ALLOWED_CV_EXTENSIONS = Arrays.asList(".pdf", ".doc", ".docx");
    private static final List<String> ALLOWED_PORTFOLIO_EXTENSIONS = Arrays.asList(".png", ".jpeg", ".jpg");

    @FXML
    public void initialize() {
        serviceCandidature = new ServiceCandidature();

        // Initialiser les options du ComboBox
        typeCollabCombo.setItems(FXCollections.observableArrayList(
                "Stage", "Sponsoring", "Atelier Collaboratif"
        ));
    }

    public void initData(Candidature candidature) {//champs m3bain
        try {
            if (candidature == null) {
                System.err.println("ERREUR: La candidature passée à initData est null");
                throw new IllegalArgumentException("La candidature ne peut pas être null");
            }

            System.out.println("Initialisation des données pour la candidature ID: " + candidature.getId());
            this.candidature = candidature;

            // Remplir les champs avec les données existantes
            if (candidature.getTypeCollab() != null) {
                typeCollabCombo.setValue(candidature.getTypeCollab());
                System.out.println("Type de collaboration défini: " + candidature.getTypeCollab());
            }

            if (candidature.getMotivation() != null) {
                motivationTextArea.setText(candidature.getMotivation());
                System.out.println("Motivation définie, longueur: " + candidature.getMotivation().length());
            }

            // Convertir Date en LocalDate pour DatePicker
            try {
                if (candidature.getDatePostulation() != null) {
                    LocalDate dateLocale = candidature.getDatePostulation().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    datePostulation.setValue(dateLocale);
                    System.out.println("Date de postulation définie: " + dateLocale);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date: " + e.getMessage());
                datePostulation.setValue(LocalDate.now());
                System.out.println("Date définie à aujourd'hui suite à une erreur");
            }

            // Afficher les noms de fichiers existants
            if (candidature.getCv() != null && !candidature.getCv().isEmpty()) {
                try {
                    File cvFile = new File(candidature.getCv());
                    cvLabel.setText(cvFile.getName());
                    nouveauCV = candidature.getCv();
                    System.out.println("CV défini: " + cvFile.getName());
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès au fichier CV: " + e.getMessage());
                    cvLabel.setText("Fichier non accessible");
                }
            }

            if (candidature.getPortfolio() != null && !candidature.getPortfolio().isEmpty()) {
                try {
                    File portfolioFile = new File(candidature.getPortfolio());
                    portfolioLabel.setText(portfolioFile.getName());
                    nouveauPortfolio = candidature.getPortfolio();
                    System.out.println("Portfolio défini: " + portfolioFile.getName());
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès au fichier portfolio: " + e.getMessage());
                    portfolioLabel.setText("Fichier non accessible");
                }
            }

            System.out.println("Initialisation des données terminée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des données: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation: " + e.getMessage(), e);
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
            // Vérifier la taille du fichier
            if (selectedFile.length() > MAX_FILE_SIZE) {
                cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorCv.setText("Le fichier CV ne doit pas dépasser 5 Mo.");
                errorCv.setVisible(true);
                return;
            }

            // Vérifier l'extension du fichier
            String fileName = selectedFile.getName().toLowerCase();
            boolean isValidExtension = ALLOWED_CV_EXTENSIONS.stream().anyMatch(fileName::endsWith);
            if (!isValidExtension) {
                cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorCv.setText("Le CV doit être un fichier PDF, DOC ou DOCX.");
                errorCv.setVisible(true);
                return;
            }

            nouveauCV = selectedFile.getAbsolutePath();
            cvLabel.setText(selectedFile.getName());
            cvLabel.setTextFill(javafx.scene.paint.Color.BLACK);
            errorCv.setVisible(false);
        }
    }

    @FXML
    private void choisirPortfolio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre Portfolio");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpeg", "*.jpg")
        );

        Stage stage = (Stage) btnMettreAJour.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Vérifier la taille du fichier
            if (selectedFile.length() > MAX_FILE_SIZE) {
                portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorPortfolio.setText("Le fichier portfolio ne doit pas dépasser 5 Mo.");
                errorPortfolio.setVisible(true);
                return;
            }

            // Vérifier l'extension du fichier
            String fileName = selectedFile.getName().toLowerCase();
            boolean isValidExtension = ALLOWED_PORTFOLIO_EXTENSIONS.stream().anyMatch(fileName::endsWith);
            if (!isValidExtension) {
                portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorPortfolio.setText("Le portfolio doit être une image PNG, JPEG ou JPG.");
                errorPortfolio.setVisible(true);
                return;
            }

            nouveauPortfolio = selectedFile.getAbsolutePath();
            portfolioLabel.setText(selectedFile.getName());
            portfolioLabel.setTextFill(javafx.scene.paint.Color.BLACK);
            errorPortfolio.setVisible(false);
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
        // Réinitialiser les styles et les erreurs
        resetStyle(datePostulation, typeCollabCombo, motivationTextArea);
        resetErrors();
        cvLabel.setTextFill(javafx.scene.paint.Color.BLACK);
        portfolioLabel.setTextFill(javafx.scene.paint.Color.BLACK);

        boolean hasError = false;

        // Validation de la lettre de motivation (si non vide)
        if (!motivationTextArea.getText().isEmpty() && motivationTextArea.getText().length() < 10) {
            motivationTextArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorMotivation.setText("La lettre de motivation doit contenir au moins 10 caractères.");
            errorMotivation.setVisible(true);
            hasError = true;
        }

        // Validation du CV si modifié
        if (nouveauCV != null && !nouveauCV.isEmpty()) {
            File cvFile = new File(nouveauCV);
            if (cvFile.length() > MAX_FILE_SIZE) {
                cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorCv.setText("Le fichier CV ne doit pas dépasser 5 Mo.");
                errorCv.setVisible(true);
                hasError = true;
            } else {
                String fileName = cvFile.getName().toLowerCase();
                boolean isValidExtension = ALLOWED_CV_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                if (!isValidExtension) {
                    cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                    errorCv.setText("Le CV doit être un fichier PDF, DOC ou DOCX.");
                    errorCv.setVisible(true);
                    hasError = true;
                }
            }
        }

        // Validation du portfolio si modifié
        if (nouveauPortfolio != null && !nouveauPortfolio.isEmpty()) {
            File portfolioFile = new File(nouveauPortfolio);
            if (portfolioFile.length() > MAX_FILE_SIZE) {
                portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorPortfolio.setText("Le fichier portfolio ne doit pas dépasser 5 Mo.");
                errorPortfolio.setVisible(true);
                hasError = true;
            } else {
                String fileName = portfolioFile.getName().toLowerCase();
                boolean isValidExtension = ALLOWED_PORTFOLIO_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                if (!isValidExtension) {
                    portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                    errorPortfolio.setText("Le portfolio doit être une image PNG, JPEG ou JPG.");
                    errorPortfolio.setVisible(true);
                    hasError = true;
                }
            }
        }

        // Afficher une alerte si des erreurs sont détectées
        if (hasError) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez corriger les erreurs en rouge !");
            alert.showAndWait();
            return;
        }

        try {
            // Mettre à jour les données de la candidature si elles sont fournies
            if (typeCollabCombo.getValue() != null) {
                candidature.setTypeCollab(typeCollabCombo.getValue());
            }
            if (!motivationTextArea.getText().isEmpty()) {
                candidature.setMotivation(motivationTextArea.getText());
            }
            if (datePostulation.getValue() != null) {
                Date datePost = Date.from(datePostulation.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                candidature.setDatePostulation(datePost);
            }
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

            // Fermer la fenêtre
            Stage stage = (Stage) btnMettreAJour.getScene().getWindow();
            stage.close();

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

    private void resetErrors() {
        errorDatePostulation.setVisible(false);
        errorCv.setVisible(false);
        errorPortfolio.setVisible(false);
        errorTypeCollaboration.setVisible(false);
        errorMotivation.setVisible(false);
    }

    private void resetStyle(Control... controls) {
        for (Control control : controls) {
            control.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        }
    }


}