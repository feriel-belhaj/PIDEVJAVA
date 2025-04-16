package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Formation;
import tn.esprit.workshop.services.FormationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FormationController implements Initializable {
    @FXML private TextField txtTitre;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<String> comboNiveau;
    @FXML private TextField txtPrix;
    @FXML private TextField txtEmplacement;
    @FXML private TextField txtNbPlace;
    @FXML private TextField txtNbParticipant;
    @FXML private TextField txtOrganisateur;
    @FXML private TextField txtDuree;
    @FXML private TextField txtImage;
    
    @FXML private TableView<Formation> tableFormations;
    @FXML private TableColumn<Formation, String> titreCol;
    @FXML private TableColumn<Formation, String> descriptionCol;
    @FXML private TableColumn<Formation, String> niveauCol;
    @FXML private TableColumn<Formation, String> prixCol;
    
    private FormationService formationService;
    private Formation selectedFormation;
    private static final String UPLOAD_DIR = "src/main/resources/uploads/images";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formationService = new FormationService();
        
        // Initialisation du ComboBox des niveaux
        comboNiveau.getItems().addAll("Débutant", "Intermédiaire", "Avancé");
        
        // Configuration des colonnes
        titreCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitre()));
        descriptionCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        niveauCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNiveau()));
        prixCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getPrix())));
        
        // Gestion de la sélection
        tableFormations.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedFormation = newSelection;
                fillFormWithFormation(newSelection);
            }
        });
        
        // Création du dossier d'upload s'il n'existe pas
        createUploadDirectory();
        
        // Chargement initial des données
        refreshTable();
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(txtImage.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path targetPath = Paths.get(UPLOAD_DIR, fileName);
                
                // Copie du fichier vers le dossier d'upload
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Mise à jour du champ image avec le chemin relatif
                txtImage.setText("uploads/images/" + fileName);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image: " + e.getMessage());
            }
        }
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du dossier d'upload: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            Formation formation = getFormationFromForm();
            formationService.ajouter(formation);
            clearForm();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation ajoutée avec succès!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void handleModifier() {
        if (selectedFormation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une formation à modifier");
            return;
        }
        
        try {
            Formation formation = getFormationFromForm();
            formation.setId(selectedFormation.getId());
            formationService.modifier(formation);
            clearForm();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation modifiée avec succès!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        if (selectedFormation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une formation à supprimer");
            return;
        }
        
        try {
            // Suppression de l'image si elle existe
            if (selectedFormation.getImage() != null && !selectedFormation.getImage().isEmpty()) {
                try {
                    Files.deleteIfExists(Paths.get("src/main/resources/", selectedFormation.getImage()));
                } catch (IOException e) {
                    System.err.println("Erreur lors de la suppression de l'image: " + e.getMessage());
                }
            }
            
            formationService.supprimer(selectedFormation.getId());
            clearForm();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation supprimée avec succès!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
        }
    }

    @FXML
    private void handleGererCertificats() {
        if (selectedFormation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une formation");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Certificat.fxml"));
            Scene scene = new Scene(loader.load());
            
            CertificatController controller = loader.getController();
            controller.setFormation(selectedFormation);
            
            Stage stage = new Stage();
            stage.setTitle("Gestion des Certificats - Formation: " + selectedFormation.getTitre());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la gestion des certificats: " + e.getMessage());
        }
    }

    private Formation getFormationFromForm() {
        Formation formation = new Formation();
        formation.setTitre(txtTitre.getText());
        formation.setDescription(txtDescription.getText());
        formation.setDateDeb(dateDebut.getValue());
        formation.setDateFin(dateFin.getValue());
        formation.setNiveau(comboNiveau.getValue());
        formation.setPrix(Float.parseFloat(txtPrix.getText()));
        formation.setEmplacement(txtEmplacement.getText());
        formation.setNbPlace(Integer.parseInt(txtNbPlace.getText()));
        formation.setNbParticipant(Integer.parseInt(txtNbParticipant.getText()));
        formation.setOrganisateur(txtOrganisateur.getText());
        formation.setDuree(txtDuree.getText());
        formation.setImage(txtImage.getText());
        return formation;
    }

    private void fillFormWithFormation(Formation formation) {
        txtTitre.setText(formation.getTitre());
        txtDescription.setText(formation.getDescription());
        dateDebut.setValue(formation.getDateDeb());
        dateFin.setValue(formation.getDateFin());
        comboNiveau.setValue(formation.getNiveau());
        txtPrix.setText(String.valueOf(formation.getPrix()));
        txtEmplacement.setText(formation.getEmplacement());
        txtNbPlace.setText(String.valueOf(formation.getNbPlace()));
        txtNbParticipant.setText(String.valueOf(formation.getNbParticipant()));
        txtOrganisateur.setText(formation.getOrganisateur());
        txtDuree.setText(formation.getDuree());
        txtImage.setText(formation.getImage());
    }

    private void clearForm() {
        txtTitre.clear();
        txtDescription.clear();
        dateDebut.setValue(null);
        dateFin.setValue(null);
        comboNiveau.setValue(null);
        txtPrix.clear();
        txtEmplacement.clear();
        txtNbPlace.clear();
        txtNbParticipant.clear();
        txtOrganisateur.clear();
        txtDuree.clear();
        txtImage.clear();
        selectedFormation = null;
    }

    private void refreshTable() {
        try {
            tableFormations.getItems().clear();
            tableFormations.getItems().addAll(formationService.getAll());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des formations: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}