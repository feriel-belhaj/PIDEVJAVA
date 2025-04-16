package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.workshop.models.Don;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.services.DonService;
import tn.esprit.workshop.services.EvenementService;
import tn.esprit.workshop.services.UserGetData;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class DonController implements Initializable {
    
    @FXML
    private TableView<Don> tableDons;
    @FXML
    private TableColumn<Don, Integer> colId;
    @FXML
    private TableColumn<Don, LocalDateTime> colDonationDate;
    @FXML
    private TableColumn<Don, Double> colAmount;
    @FXML
    private TableColumn<Don, String> colPaymentRef;
    @FXML
    private TableColumn<Don, String> colMessage;
    @FXML
    private TableColumn<Don, Integer> colUserId;
    
    @FXML
    private TextField tfAmount;
    @FXML
    private TextField tfPaymentRef;
    @FXML
    private TextArea taMessage;
    @FXML
    private Label lblTotalDonations;
    @FXML
    private Label lblEventTitle;
    @FXML
    private Label lblGoalAmount;
    
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnClear;
    
    private DonService donService;
    private EvenementService evenementService;
    private ObservableList<Don> donList;
    private Don selectedDon;
    private int evenementId;
    
    // Assuming a simple user ID for the current user (should be replaced with real authentication)
    private int CURRENT_USER_ID = UserGetData.id;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        donService = new DonService();
        evenementService = new EvenementService();
        
        // Check if user exists and create if needed
        ensureUserExists();
        
        // Initialize table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDonationDate.setCellValueFactory(new PropertyValueFactory<>("donationdate"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colPaymentRef.setCellValueFactory(new PropertyValueFactory<>("paymentref"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        
        // Add a click listener to the table
        tableDons.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedDon = newSelection;
                displayDonDetails();
                btnSupprimer.setDisable(false);
            } else {
                clearFields();
                btnSupprimer.setDisable(true);
            }
        });
        
        // Set buttons initial state
        btnSupprimer.setDisable(true);
    }
    
    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
        loadDons();
        displayEventInfo();
    }
    
    private void loadDons() {
        List<Don> dons = donService.getByEvenementId(evenementId);
        donList = FXCollections.observableArrayList(dons);
        tableDons.setItems(donList);
        
        // Update total donations label
        double totalDonations = donService.getTotalDonationsByEvenementId(evenementId);
        lblTotalDonations.setText(String.format("%.2f", totalDonations));
    }
    
    private void displayEventInfo() {
        Evenement evenement = evenementService.getById(evenementId);
        if (evenement != null) {
            lblEventTitle.setText(evenement.getTitre());
            lblGoalAmount.setText(String.format("%.2f", evenement.getGoalamount()));
        }
    }
    
    private void displayDonDetails() {
        if (selectedDon != null) {
            tfAmount.setText(String.valueOf(selectedDon.getAmount()));
            tfPaymentRef.setText(selectedDon.getPaymentref());
            taMessage.setText(selectedDon.getMessage());
        }
    }
    
    private void clearFields() {
        tfAmount.clear();
        tfPaymentRef.clear();
        taMessage.clear();
        selectedDon = null;
        btnSupprimer.setDisable(true);
    }
    
    @FXML
    private void handleAjouter(ActionEvent event) {
        if (validateInput()) {
            CURRENT_USER_ID=UserGetData.id;
            Don don = new Don();
            don.setEvenement_id(evenementId);
            don.setDonationdate(LocalDateTime.now());
            don.setAmount(Double.parseDouble(tfAmount.getText().trim()));
            don.setPaymentref(tfPaymentRef.getText().trim());
            don.setMessage(taMessage.getText().trim());
            don.setCreateur_id(CURRENT_USER_ID); // This could be the admin or moderator
            don.setUser_id(CURRENT_USER_ID); // In a real app, this would be the authenticated user

            donService.ajouter(don);
            loadDons();
            clearFields();
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Don ajouté avec succès!");
        }
    }
    
    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (selectedDon != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer le don");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce don?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    donService.supprimer(selectedDon.getId());
                    loadDons();
                    clearFields();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Don supprimé avec succès!");
                }
            });
        }
    }
    
    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
    }
    
    private boolean validateInput() {
        StringBuilder errorMsg = new StringBuilder();
        
        try {
            double amount = Double.parseDouble(tfAmount.getText().trim());
            if (amount <= 0) {
                errorMsg.append("Le montant du don doit être supérieur à 0.\n");
            }
        } catch (NumberFormatException e) {
            errorMsg.append("Le montant du don doit être un nombre valide.\n");
        }
        
        if (tfPaymentRef.getText().isEmpty()) {
            errorMsg.append("La référence de paiement ne peut pas être vide.\n");
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
     * Trouve un utilisateur existant dans la base de données à utiliser
     * Cette approche évite de modifier la table utilisateur
     */
    private void ensureUserExists() {
        try {
            Connection conn = MyDbConnexion.getInstance().getCnx();
            
            // Trouver un utilisateur existant à utiliser
            String findUserQuery = "SELECT id FROM utilisateur LIMIT 1";
            PreparedStatement findStmt = conn.prepareStatement(findUserQuery);
            ResultSet rs = findStmt.executeQuery();
            
            if (rs.next()) {
                // Utiliser l'ID d'un utilisateur existant
                CURRENT_USER_ID = rs.getInt("id");
                System.out.println("Utilisation d'un utilisateur existant avec ID " + CURRENT_USER_ID);
            } else {
                System.err.println("Aucun utilisateur trouvé dans la base de données. Veuillez demander à votre collègue de créer un utilisateur.");
                showAlert(Alert.AlertType.WARNING, "Attention", 
                         "Aucun utilisateur n'existe dans la base de données. La création de dons ne fonctionnera pas. " +
                         "Veuillez contacter la personne responsable de la gestion des utilisateurs.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'un utilisateur: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", 
                     "Impossible de vérifier les utilisateurs existants: " + e.getMessage());
        }
    }
} 