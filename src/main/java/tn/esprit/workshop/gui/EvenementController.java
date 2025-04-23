package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.services.EvenementService;
import tn.esprit.workshop.services.UserGetData;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class EvenementController implements Initializable {
    
    @FXML
    private TableView<Evenement> tableEvenements;
    @FXML
    private TableColumn<Evenement, Integer> colId;
    @FXML
    private TableColumn<Evenement, String> colTitre;
    @FXML
    private TableColumn<Evenement, String> colDescription;
    @FXML
    private TableColumn<Evenement, LocalDateTime> colStartDate;
    @FXML
    private TableColumn<Evenement, LocalDateTime> colEndDate;
    @FXML
    private TableColumn<Evenement, String> colLocalisation;
    @FXML
    private TableColumn<Evenement, Double> colGoalAmount;
    @FXML
    private TableColumn<Evenement, Double> colCollectedAmount;
    @FXML
    private TableColumn<Evenement, String> colStatus;
    @FXML
    private TableColumn<Evenement, String> colCategorie;
    
    @FXML
    private TextField tfTitre;
    @FXML
    private TextArea taDescription;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private TextField tfLocalisation;
    @FXML
    private TextField tfGoalAmount;
    @FXML
    private TextField tfImageUrl;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private ComboBox<String> cbCategorie;
    
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnClear;
    
    private EvenementService evenementService;
    private ObservableList<Evenement> evenementList;
    private Evenement selectedEvenement;
    
    // Assuming a simple user ID for the current creator (should be replaced with real authentication)
    private int CREATOR_USER_ID = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evenementService = new EvenementService();
        CREATOR_USER_ID=UserGetData.id;
        // Check if creator user exists and create if needed
        //ensureCreatorUserExists();
        
        // Initialize status ComboBox
        cbStatus.setItems(FXCollections.observableArrayList("Actif", "Inactif", "Terminé"));
        
        // Initialize categories ComboBox
        cbCategorie.setItems(FXCollections.observableArrayList("Humanitaire", "Santé", "Éducation", "Environnement", "Autre"));
        
        // Initialize table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startdate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("enddate"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colGoalAmount.setCellValueFactory(new PropertyValueFactory<>("goalamount"));
        colCollectedAmount.setCellValueFactory(new PropertyValueFactory<>("collectedamount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        
        // Add a click listener to the table
        tableEvenements.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEvenement = newSelection;
                displayEvenementDetails();
                btnModifier.setDisable(false);
                btnSupprimer.setDisable(false);
            } else {
                clearFields();
                btnModifier.setDisable(true);
                btnSupprimer.setDisable(true);
            }
        });
        
        // Load evenements
        loadEvenements();
        
        // Set buttons initial state
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }
    
    private void loadEvenements() {
        List<Evenement> events = evenementService.getAll();
        evenementList = FXCollections.observableArrayList(events);
        tableEvenements.setItems(evenementList);
    }
    
    private void displayEvenementDetails() {
        if (selectedEvenement != null) {
            tfTitre.setText(selectedEvenement.getTitre());
            taDescription.setText(selectedEvenement.getDescription());
            
            if (selectedEvenement.getStartdate() != null) {
                dpStartDate.setValue(selectedEvenement.getStartdate().toLocalDate());
            }
            
            if (selectedEvenement.getEnddate() != null) {
                dpEndDate.setValue(selectedEvenement.getEnddate().toLocalDate());
            }
            
            tfLocalisation.setText(selectedEvenement.getLocalisation());
            tfGoalAmount.setText(String.valueOf(selectedEvenement.getGoalamount()));
            tfImageUrl.setText(selectedEvenement.getImageurl());
            cbStatus.setValue(selectedEvenement.getStatus());
            cbCategorie.setValue(selectedEvenement.getCategorie());
        }
    }
    
    private void clearFields() {
        tfTitre.clear();
        taDescription.clear();
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        tfLocalisation.clear();
        tfGoalAmount.clear();
        tfImageUrl.clear();
        cbStatus.setValue(null);
        cbCategorie.setValue(null);
        selectedEvenement = null;
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }
    
    @FXML
    private void handleAjouter(ActionEvent event) {
        if (validateInput()) {
            Evenement evenement = new Evenement();
            evenement.setTitre(tfTitre.getText().trim());
            evenement.setDescription(taDescription.getText().trim());
            
            if (dpStartDate.getValue() != null) {
                evenement.setStartdate(LocalDateTime.of(dpStartDate.getValue(), LocalTime.of(0, 0)));
            }
            
            if (dpEndDate.getValue() != null) {
                evenement.setEnddate(LocalDateTime.of(dpEndDate.getValue(), LocalTime.of(23, 59)));
            }
            CREATOR_USER_ID=UserGetData.id;
            evenement.setLocalisation(tfLocalisation.getText().trim());
            evenement.setGoalamount(Double.parseDouble(tfGoalAmount.getText().trim()));
            evenement.setCollectedamount(0.0); // Default 0 for new events
            evenement.setStatus(cbStatus.getValue());
            evenement.setImageurl(tfImageUrl.getText().trim());
            evenement.setCreatedat(LocalDateTime.now());
            evenement.setCategorie(cbCategorie.getValue());
            evenement.setCreateur_id(CREATOR_USER_ID);
            
            evenementService.ajouter(evenement);
            loadEvenements();
            clearFields();
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté avec succès!");
        }
    }
    
    @FXML
    private void handleModifier(ActionEvent event) {
        if (selectedEvenement != null && validateInput()) {
            selectedEvenement.setTitre(tfTitre.getText().trim());
            selectedEvenement.setDescription(taDescription.getText().trim());
            
            if (dpStartDate.getValue() != null) {
                selectedEvenement.setStartdate(LocalDateTime.of(dpStartDate.getValue(), LocalTime.of(0, 0)));
            }
            
            if (dpEndDate.getValue() != null) {
                selectedEvenement.setEnddate(LocalDateTime.of(dpEndDate.getValue(), LocalTime.of(23, 59)));
            }
            
            selectedEvenement.setLocalisation(tfLocalisation.getText().trim());
            selectedEvenement.setGoalamount(Double.parseDouble(tfGoalAmount.getText().trim()));
            selectedEvenement.setStatus(cbStatus.getValue());
            selectedEvenement.setImageurl(tfImageUrl.getText().trim());
            selectedEvenement.setCategorie(cbCategorie.getValue());
            
            evenementService.modifier(selectedEvenement);
            loadEvenements();
            clearFields();
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement modifié avec succès!");
        }
    }
    
    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (selectedEvenement != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer l'événement");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cet événement?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    evenementService.supprimer(selectedEvenement.getId());
                    loadEvenements();
                    clearFields();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement supprimé avec succès!");
                }
            });
        }
    }
    
    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
    }
    
    @FXML
    private void handleViewDons(ActionEvent event) {
        if (selectedEvenement != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/workshop/resources/DonList.fxml"));
                Parent root = loader.load();
                
                DonController controller = loader.getController();
                controller.setEvenementId(selectedEvenement.getId());
                
                Stage stage = new Stage();
                stage.setTitle("Dons pour " + selectedEvenement.getTitre());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre des dons: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un événement d'abord.");
        }
    }
    
    private boolean validateInput() {
        StringBuilder errorMsg = new StringBuilder();
        
        if (tfTitre.getText().isEmpty()) {
            errorMsg.append("Le titre ne peut pas être vide.\n");
        }
        
        if (taDescription.getText().isEmpty()) {
            errorMsg.append("La description ne peut pas être vide.\n");
        }
        
        if (dpStartDate.getValue() == null) {
            errorMsg.append("La date de début est requise.\n");
        }
        
        if (dpEndDate.getValue() == null) {
            errorMsg.append("La date de fin est requise.\n");
        } else if (dpStartDate.getValue() != null && dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            errorMsg.append("La date de fin doit être après la date de début.\n");
        }
        
        if (tfLocalisation.getText().isEmpty()) {
            errorMsg.append("La localisation ne peut pas être vide.\n");
        }
        
        try {
            double goalAmount = Double.parseDouble(tfGoalAmount.getText().trim());
            if (goalAmount <= 0) {
                errorMsg.append("Le montant objectif doit être supérieur à 0.\n");
            }
        } catch (NumberFormatException e) {
            errorMsg.append("Le montant objectif doit être un nombre valide.\n");
        }
        
        if (cbStatus.getValue() == null) {
            errorMsg.append("Le statut est requis.\n");
        }
        
        if (cbCategorie.getValue() == null) {
            errorMsg.append("La catégorie est requise.\n");
        }
        
        if (errorMsg.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMsg.toString());
            return false;
        }
        
        return true;
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Trouve un utilisateur existant dans la base de données à utiliser comme créateur
     * Cette approche évite de modifier la table utilisateur
     */
    private void ensureCreatorUserExists() {
        try {
            Connection conn = MyDbConnexion.getInstance().getCnx();
            CREATOR_USER_ID=UserGetData.id;
            // Trouver un utilisateur existant à utiliser
            String findUserQuery = "SELECT id FROM utilisateur LIMIT 1";
            PreparedStatement findStmt = conn.prepareStatement(findUserQuery);
            ResultSet rs = findStmt.executeQuery();
            
            if (rs.next()) {
                // Utiliser l'ID d'un utilisateur existant
                CREATOR_USER_ID = rs.getInt("id");
                System.out.println("Utilisation d'un utilisateur existant avec ID " + CREATOR_USER_ID);
            } else {
                System.err.println("Aucun utilisateur trouvé dans la base de données. Veuillez demander à votre collègue de créer un utilisateur.");
                showAlert(Alert.AlertType.WARNING, "Attention", 
                         "Aucun utilisateur n'existe dans la base de données. La création d'événements ne fonctionnera pas. " +
                         "Veuillez contacter la personne responsable de la gestion des utilisateurs.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'un utilisateur: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", 
                     "Impossible de vérifier les utilisateurs existants: " + e.getMessage());
        }
    }
} 