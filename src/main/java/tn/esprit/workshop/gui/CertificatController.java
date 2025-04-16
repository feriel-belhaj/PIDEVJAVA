package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import tn.esprit.workshop.entities.Certificat;
import tn.esprit.workshop.entities.Formation;
import tn.esprit.workshop.services.CertificatService;
import tn.esprit.workshop.services.FormationService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CertificatController implements Initializable {
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private DatePicker dateObt;
    @FXML private ComboBox<String> comboNiveau;
    @FXML private TextField txtNomOrganisme;
    
    @FXML private TableView<Certificat> tableCertificats;
    @FXML private TableColumn<Certificat, String> nomCol;
    @FXML private TableColumn<Certificat, String> prenomCol;
    @FXML private TableColumn<Certificat, String> niveauCol;
    @FXML private TableColumn<Certificat, String> nomOrganismeCol;
    @FXML private TableColumn<Certificat, String> formationTitreCol;
    
    private CertificatService certificatService;
    private Formation formation;
    private Certificat selectedCertificat;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        certificatService = new CertificatService();
        
        // Initialisation du ComboBox des niveaux
        comboNiveau.getItems().addAll("Débutant", "Intermédiaire", "Avancé");
        
        // Configuration des colonnes
        nomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        prenomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
        niveauCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNiveau()));
        nomOrganismeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomorganisme()));
        formationTitreCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormationTitre()));
        
        // Gestion de la sélection
        tableCertificats.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCertificat = newSelection;
                fillFormWithCertificat(newSelection);
                // Récupérer la formation associée au certificat
                try {
                    FormationService formationService = new FormationService();
                    formation = formationService.getById(newSelection.getFormationId());
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération de la formation: " + e.getMessage());
                }
            }
        });

        // Chargement initial de tous les certificats
        refreshTable();
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
        if (formation != null) {
            // Si une formation est spécifiée, afficher uniquement ses certificats
            refreshTableForFormation();
        } else {
            // Sinon, afficher tous les certificats
            refreshTable();
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            Certificat certificat = getCertificatFromForm();
            certificat.setFormationId(formation.getId());
            certificatService.ajouter(certificat);
            clearForm();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Certificat ajouté avec succès!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void handleModifier() {
        if (selectedCertificat == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un certificat à modifier");
            return;
        }
        
        try {
            Certificat certificat = getCertificatFromForm();
            certificat.setId(selectedCertificat.getId());
            certificat.setFormationId(formation.getId());
            certificatService.modifier(certificat);
            clearForm();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Certificat modifié avec succès!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        if (selectedCertificat == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un certificat à supprimer");
            return;
        }
        
        try {
            certificatService.supprimer(selectedCertificat.getId());
            clearForm();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Certificat supprimé avec succès!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
        }
    }

    @FXML
    private void handleVisualiserCertificat() {
        System.out.println("Début de la visualisation du certificat");
        
        if (selectedCertificat == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un certificat à visualiser");
            return;
        }
        
        System.out.println("Certificat sélectionné : " + selectedCertificat.getNom());
        
        try {
            System.out.println("Chargement du fichier FXML...");
            URL fxmlUrl = getClass().getResource("/fxml/CertificatView.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Le fichier CertificatView.fxml n'a pas été trouvé");
            }
            System.out.println("URL du FXML : " + fxmlUrl.toString());
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            System.out.println("FXML chargé, récupération du contrôleur...");
            CertificatViewController controller = loader.getController();
            if (controller == null) {
                throw new IOException("Le contrôleur n'a pas pu être chargé");
            }
            
            System.out.println("Configuration du certificat...");
            controller.setCertificat(selectedCertificat, formation);
            
            System.out.println("Création de la nouvelle fenêtre...");
            Stage stage = new Stage();
            stage.setTitle("Certificat de Formation");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Ajout d'un style CSS pour la fenêtre
            scene.getStylesheets().add(getClass().getResource("/styles/certificat.css").toExternalForm());
            
            // Configuration de la taille minimale
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            System.out.println("Affichage de la fenêtre");
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage du certificat: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Erreur lors de l'affichage du certificat: " + e.getMessage() + 
                     "\nVérifiez que tous les fichiers nécessaires sont présents.");
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }

    private Certificat getCertificatFromForm() {
        Certificat certificat = new Certificat();
        certificat.setNom(txtNom.getText());
        certificat.setPrenom(txtPrenom.getText());
        certificat.setDateobt(dateObt.getValue());
        certificat.setNiveau(comboNiveau.getValue());
        certificat.setNomorganisme(txtNomOrganisme.getText());
        return certificat;
    }

    private void fillFormWithCertificat(Certificat certificat) {
        txtNom.setText(certificat.getNom());
        txtPrenom.setText(certificat.getPrenom());
        dateObt.setValue(certificat.getDateobt());
        comboNiveau.setValue(certificat.getNiveau());
        txtNomOrganisme.setText(certificat.getNomorganisme());
    }

    private void clearForm() {
        txtNom.clear();
        txtPrenom.clear();
        dateObt.setValue(null);
        comboNiveau.setValue(null);
        txtNomOrganisme.clear();
        selectedCertificat = null;
    }

    private void refreshTable() {
        try {
            tableCertificats.getItems().clear();
            tableCertificats.getItems().addAll(certificatService.getAll());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des certificats: " + e.getMessage());
        }
    }

    private void refreshTableForFormation() {
        try {
            tableCertificats.getItems().clear();
            tableCertificats.getItems().addAll(certificatService.getByFormationId(formation.getId()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des certificats: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 