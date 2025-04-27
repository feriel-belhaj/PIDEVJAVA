package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.workshop.models.Certificat;
import tn.esprit.workshop.models.Formation;
import tn.esprit.workshop.services.CertificatService;
import tn.esprit.workshop.services.FormationService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    @FXML private TableColumn<Certificat, LocalDate> dateCol;
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
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateobt"));
        niveauCol.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        nomOrganismeCol.setCellValueFactory(new PropertyValueFactory<>("nomorganisme"));
        formationTitreCol.setCellValueFactory(new PropertyValueFactory<>("formationTitre"));
        
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

        // Ajout des listeners de validation
        setupValidation();

        // Chargement initial de tous les certificats
        refreshTable();
    }

    private void setupValidation() {
        // Validation du nom (au moins 2 caractères, lettres uniquement)
        txtNom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.matches("[a-zA-Z\\s]{2,}")) {
                    txtNom.setStyle("-fx-border-color: red;");
                } else {
                    txtNom.setStyle("");
                }
            }
        });

        // Validation du prénom (au moins 2 caractères, lettres uniquement)
        txtPrenom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.matches("[a-zA-Z\\s]{2,}")) {
                    txtPrenom.setStyle("-fx-border-color: red;");
                } else {
                    txtPrenom.setStyle("");
                }
            }
        });

        // Validation de l'organisme (au moins 2 caractères)
        txtNomOrganisme.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.length() < 2) {
                    txtNomOrganisme.setStyle("-fx-border-color: red;");
                } else {
                    txtNomOrganisme.setStyle("");
                }
            }
        });
    }

    private List<String> validateForm() {
        List<String> errors = new ArrayList<>();

        // Validation du nom
        if (txtNom.getText() == null || !txtNom.getText().matches("[a-zA-Z\\s]{2,}")) {
            errors.add("Le nom doit contenir au moins 2 caractères et ne contenir que des lettres");
        }

        // Validation du prénom
        if (txtPrenom.getText() == null || !txtPrenom.getText().matches("[a-zA-Z\\s]{2,}")) {
            errors.add("Le prénom doit contenir au moins 2 caractères et ne contenir que des lettres");
        }

        // Validation de la date d'obtention
        if (dateObt.getValue() == null) {
            errors.add("La date d'obtention est obligatoire");
        } else {
            if (dateObt.getValue().isAfter(LocalDate.now())) {
                errors.add("La date d'obtention ne peut pas être dans le futur");
            }
            if (formation != null && dateObt.getValue().isBefore(formation.getDateDeb())) {
                errors.add("La date d'obtention ne peut pas être antérieure à la date de début de la formation");
            }
        }

        // Validation du niveau
        if (comboNiveau.getValue() == null) {
            errors.add("Le niveau est obligatoire");
        }

        // Validation de l'organisme
        if (txtNomOrganisme.getText() == null || txtNomOrganisme.getText().trim().length() < 2) {
            errors.add("Le nom de l'organisme doit contenir au moins 2 caractères");
        }

        return errors;
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
        List<String> errors = validateForm();
        if (!errors.isEmpty()) {
            String errorMessage = String.join("\n", errors);
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage);
            return;
        }

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

        List<String> errors = validateForm();
        if (!errors.isEmpty()) {
            String errorMessage = String.join("\n", errors);
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage);
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