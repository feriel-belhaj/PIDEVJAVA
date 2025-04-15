package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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

    private Partenariat partenariat;
    private String cvPath;
    private String portfolioPath;
    private ServiceCandidature serviceCandidature;

    @FXML
    public void initialize() {
        datePostulation.setValue(LocalDate.now());
        serviceCandidature = new ServiceCandidature();

        // Initialiser les types de collaboration
        typeCollaboration.setItems(FXCollections.observableArrayList(
                "Permanent", "Temporaire", "Projet spécifique", "Sponsoring", "Mécénat"
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
            cvPath = selectedFile.getAbsolutePath();
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

        Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            portfolioPath = selectedFile.getAbsolutePath();
            portfolioLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void enregistrer() {
        // Validation des champs
        if (datePostulation.getValue() == null ||
                cvPath == null || cvPath.isEmpty() ||
                portfolioPath == null || portfolioPath.isEmpty() ||
                typeCollaboration.getValue() == null ||
                lettreMotivation.getText().isEmpty()) {

            afficherAlerte(Alert.AlertType.WARNING, "Validation", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            // Création d'un nouvel objet Candidature
            Candidature candidature = new Candidature();
            
            // Conversion de LocalDate en Date
            Date datePost = Date.from(datePostulation.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            // Paramétrage de l'objet Candidature
            candidature.setDatePostulation(datePost);
            candidature.setCv(cvPath);
            candidature.setPortfolio(portfolioPath);
            candidature.setMotivation(lettreMotivation.getText());
            candidature.setTypeCollab(typeCollaboration.getValue());
            candidature.setPartenariat(partenariat);
            
            // Enregistrer la candidature dans la base de données
            serviceCandidature.insert(candidature);
            
            // Afficher un message de succès
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Votre candidature a été enregistrée avec succès !");
            
            // Rediriger vers la liste des partenariats
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListePartenariats.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
            stage.setTitle("Liste des Partenariats");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement de la candidature: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la liste des partenariats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}