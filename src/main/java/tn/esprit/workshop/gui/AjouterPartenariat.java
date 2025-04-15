package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServicePartenariat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AjouterPartenariat {

    @FXML
    private TextField nom;

    @FXML
    private TextArea description;

    @FXML
    private TextField type;

    @FXML
    private ComboBox<String> statut;

    @FXML
    private DatePicker dateDebut;

    @FXML
    private DatePicker dateFin;

    @FXML
    private Label imageLabel;

    @FXML
    private Label errorNom;

    @FXML
    private Label errorDescription;

    @FXML
    private Label errorType;

    @FXML
    private Label errorStatut;

    @FXML
    private Label errorDateDebut;

    @FXML
    private Label errorDateFin;

    @FXML
    private Button btnAjouter;

    private String selectedImagePath;
    private ServicePartenariat servicePartenariat;

    @FXML
    public void initialize() {
        servicePartenariat = new ServicePartenariat();
        dateDebut.setValue(LocalDate.now());
        dateFin.setValue(LocalDate.now());
        
        // Initialiser les valeurs du ComboBox
        ObservableList<String> statutOptions = FXCollections.observableArrayList(
            "Actif", "EnCours", "Terminé"
        );
        statut.setItems(statutOptions);
        statut.setValue("Actif"); // Valeur par défaut
    }

    private void resetErrors() {
        errorNom.setVisible(false);
        errorDescription.setVisible(false);
        errorType.setVisible(false);
        errorStatut.setVisible(false);
        errorDateDebut.setVisible(false);
        errorDateFin.setVisible(false);
    }

    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            imageLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    void ajouterPartenariat(ActionEvent event) {
        String nomPartenariat = nom.getText();
        String descriptionPartenariat = description.getText();
        String typePartenariat = type.getText();
        String statutPartenariat = statut.getValue();
        LocalDate dateDebutValue = dateDebut.getValue();
        LocalDate dateFinValue = dateFin.getValue();

        resetStyle(nom, description, type, dateDebut, dateFin);
        resetErrors();

        boolean hasError = false;

        if (nomPartenariat.isEmpty()) {
            nom.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorNom.setText("Le nom est requis.");
            errorNom.setVisible(true);
            hasError = true;
        }

        if (descriptionPartenariat.isEmpty() || descriptionPartenariat.length() < 10) {
            description.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDescription.setText("La description doit contenir au moins 10 caractères.");
            errorDescription.setVisible(true);
            hasError = true;
        }

        if (typePartenariat.isEmpty()) {
            type.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorType.setText("Le type est requis.");
            errorType.setVisible(true);
            hasError = true;
        }

        if (statutPartenariat == null) {
            statut.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorStatut.setText("Le statut est requis.");
            errorStatut.setVisible(true);
            hasError = true;
        }

        if (dateDebutValue == null) {
            dateDebut.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDateDebut.setText("La date de début est requise.");
            errorDateDebut.setVisible(true);
            hasError = true;
        }

        if (dateFinValue == null) {
            dateFin.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDateFin.setText("La date de fin est requise.");
            errorDateFin.setVisible(true);
            hasError = true;
        } else if (dateDebutValue != null && dateFinValue.isBefore(dateDebutValue)) {
            dateFin.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDateFin.setText("La date de fin doit être postérieure à la date de début.");
            errorDateFin.setVisible(true);
            hasError = true;
        }

        if (hasError) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez corriger les erreurs en rouge !");
            alert.showAndWait();
            return;
        }

        try {
            Partenariat partenariat = new Partenariat(
                Date.from(dateDebutValue.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(dateFinValue.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                statutPartenariat,
                descriptionPartenariat,
                nomPartenariat,
                typePartenariat,
                selectedImagePath
            );

            servicePartenariat.insert(partenariat);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Le partenariat a été ajouté avec succès !");
            alert.showAndWait();

            // Fermer la fenêtre actuelle
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();
            
            // Ouvrir la fenêtre ListePartenariats
            ouvrirListePartenariats();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de l'ajout du partenariat : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void resetStyle(Control... controls) {
        for (Control control : controls) {
            control.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        }
        statut.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
    }

    private void ouvrirListePartenariats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListePartenariats.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Liste des Partenariats");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir la liste des partenariats : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void preRemplirFormulaire(Partenariat partenariat) {
        if (partenariat != null) {
            nom.setText(partenariat.getNom());
            description.setText(partenariat.getDescription());
            type.setText(partenariat.getType());
            statut.setValue(partenariat.getStatut());
            
            // Convertir Date en LocalDate
            if (partenariat.getDateDebut() != null) {
                dateDebut.setValue(partenariat.getDateDebut().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            }
            
            if (partenariat.getDateFin() != null) {
                dateFin.setValue(partenariat.getDateFin().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            }
            
            if (partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                selectedImagePath = partenariat.getImage();
                imageLabel.setText(new File(selectedImagePath).getName());
            }
        }
    }
}