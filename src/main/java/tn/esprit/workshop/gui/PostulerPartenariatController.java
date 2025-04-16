package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServiceCandidature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PostulerPartenariatController {

    @FXML
    private Label nomLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private DatePicker datePostulation;

    @FXML
    private Label cvLabel;

    @FXML
    private Label portfolioLabel;

    @FXML
    private ComboBox<String> typeCollaboration;

    @FXML
    private TextArea lettreMotivation;

    @FXML
    private Button btnEnregistrer;

    @FXML
    private Label errorDatePostulation;

    @FXML
    private Label errorCv;

    @FXML
    private Label errorPortfolio;

    @FXML
    private Label errorTypeCollaboration;

    @FXML
    private Label errorLettreMotivation;

    private Partenariat partenariat;
    private String cvPath;
    private String portfolioPath;
    private ServiceCandidature serviceCandidature;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB en octets
    private static final List<String> ALLOWED_CV_EXTENSIONS = Arrays.asList(".pdf", ".doc", ".docx");
    private static final List<String> ALLOWED_PORTFOLIO_EXTENSIONS = Arrays.asList(".png", ".jpeg", ".jpg");
    private static final String UPLOAD_DIR = "C:\\xampp\\htdocs\\img";

    @FXML
    public void initialize() {
        datePostulation.setValue(LocalDate.now());
        serviceCandidature = new ServiceCandidature();

        // Initialiser les types de collaboration
        typeCollaboration.setItems(FXCollections.observableArrayList(
                "Stage", "Sponsoring", "Atelier Collaboratif"
        ));
    }

    public void initData(Partenariat partenariat) {
        this.partenariat = partenariat;

        // Remplir les informations du partenariat
        nomLabel.setText(partenariat.getNom());
        typeLabel.setText(partenariat.getType());
        descriptionLabel.setText(partenariat.getDescription());

        // Chargement de l'image
        String imagePath = partenariat.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_img.png")));
            }
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_img.png")));
        }
    }

    @FXML
    private void choisirCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre CV");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx")
        );

        Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
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

            cvPath = selectedFile.getAbsolutePath();
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

        Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
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

            portfolioPath = selectedFile.getAbsolutePath();
            portfolioLabel.setText(selectedFile.getName());
            portfolioLabel.setTextFill(javafx.scene.paint.Color.BLACK);
            errorPortfolio.setVisible(false);
        }
    }

    @FXML
    private void enregistrer() {
        // Réinitialiser les styles et les erreurs
        resetStyle(datePostulation, typeCollaboration, lettreMotivation);
        resetErrors();
        cvLabel.setTextFill(javafx.scene.paint.Color.BLACK);
        portfolioLabel.setTextFill(javafx.scene.paint.Color.BLACK);

        boolean hasError = false;

        // Validation de la date de postulation
        if (datePostulation.getValue() == null) {
            datePostulation.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDatePostulation.setText("La date de postulation est requise.");
            errorDatePostulation.setVisible(true);
            hasError = true;
        }

        // Validation du CV
        if (cvPath == null || cvPath.isEmpty()) {
            cvLabel.setTextFill(javafx.scene.paint.Color.RED);
            errorCv.setText("Le CV est requis.");
            errorCv.setVisible(true);
            hasError = true;
        }

        // Validation du portfolio
        if (portfolioPath == null || portfolioPath.isEmpty()) {
            portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
            errorPortfolio.setText("Le portfolio est requis.");
            errorPortfolio.setVisible(true);
            hasError = true;
        }

        // Validation du type de collaboration
        if (typeCollaboration.getValue() == null) {
            typeCollaboration.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorTypeCollaboration.setText("Le type de collaboration est requis.");
            errorTypeCollaboration.setVisible(true);
            hasError = true;
        }

        // Validation de la lettre de motivation
        if (lettreMotivation.getText().isEmpty()) {
            lettreMotivation.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorLettreMotivation.setText("La lettre de motivation est requise.");
            errorLettreMotivation.setVisible(true);
            hasError = true;
        } else if (lettreMotivation.getText().length() < 10) {
            lettreMotivation.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorLettreMotivation.setText("La lettre de motivation doit contenir au moins 10 caractères.");
            errorLettreMotivation.setVisible(true);
            hasError = true;
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
            // Copier les fichiers et obtenir les noms de fichiers
            String cvFileName = copyFileToDestination(cvPath, "cv");
            String portfolioFileName = copyFileToDestination(portfolioPath, "portfolio");

            // Création d'un nouvel objet Candidature
            Candidature candidature = new Candidature();

            // Conversion de LocalDate en Date
            Date datePost = Date.from(datePostulation.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Paramétrage de l'objet Candidature
            candidature.setDatePostulation(datePost);
            candidature.setCv(cvFileName);
            candidature.setPortfolio(portfolioFileName);
            candidature.setMotivation(lettreMotivation.getText());
            candidature.setTypeCollab(typeCollaboration.getValue());
            candidature.setPartenariat(partenariat);

            // Enregistrer la candidature dans la base de données
            serviceCandidature.insert(candidature);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Votre candidature a été enregistrée avec succès !");
            alert.showAndWait();

            // Fermer la fenêtre
            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la copie des fichiers: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'enregistrement de la candidature: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private String copyFileToDestination(String sourcePath, String fileType) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IOException("Chemin du fichier source non spécifié pour " + fileType);
        }

        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new IOException("Le fichier source n'existe pas: " + sourcePath);
        }

        // Créer le répertoire de destination s'il n'existe pas
        Path destDir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(destDir)) {
            Files.createDirectories(destDir);
        }

        // Obtenir le nom du fichier
        String originalFileName = sourceFile.getName();
        String fileName = originalFileName;
        Path destPath = Paths.get(UPLOAD_DIR, fileName);

        // Gérer les conflits de noms (ajouter un timestamp si le fichier existe)
        if (Files.exists(destPath)) {
            String nameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            fileName = nameWithoutExtension + "_" + Instant.now().toEpochMilli() + extension;
            destPath = Paths.get(UPLOAD_DIR, fileName);
        }

        // Copier le fichier
        Files.copy(sourceFile.toPath(), destPath);

        // Retourner uniquement le nom du fichier
        return fileName;
    }

    private void resetErrors() {
        errorDatePostulation.setVisible(false);
        errorCv.setVisible(false);
        errorPortfolio.setVisible(false);
        errorTypeCollaboration.setVisible(false);
        errorLettreMotivation.setVisible(false);
    }

    private void resetStyle(Control... controls) {
        for (Control control : controls) {
            control.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        }
    }
}