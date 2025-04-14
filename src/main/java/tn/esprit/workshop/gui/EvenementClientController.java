package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Don;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.services.DonService;
import tn.esprit.workshop.services.EvenementService;
import tn.esprit.workshop.utils.MyDbConnexion;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class EvenementClientController implements Initializable {
    
    @FXML
    private FlowPane eventFlowPane;
    
    @FXML
    private Label lblTitre;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblStartDate;
    @FXML
    private Label lblEndDate;
    @FXML
    private Label lblLocalisation;
    @FXML
    private Label lblGoalAmount;
    @FXML
    private Label lblCollectedAmount;
    @FXML
    private Label lblCategorie;
    @FXML
    private Label lblProgression;
    
    @FXML
    private TextField tfAmount;
    @FXML
    private TextField tfPaymentRef;
    @FXML
    private TextArea taMessage;
    @FXML
    private Button btnDonner;
    @FXML
    private ComboBox<String> cbCategorie;
    
    private EvenementService evenementService;
    private DonService donService;
    private ObservableList<Evenement> evenementList;
    private Evenement selectedEvenement;
    
    // Assuming a simple user ID for the current user (should be replaced with real authentication)
    private int CURRENT_USER_ID = 1;
    
    // Format pour afficher les dates
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evenementService = new EvenementService();
        donService = new DonService();
        
        // Check if user exists and create if needed
        ensureUserExists();
        
        // Initialize categories ComboBox for filtering
        cbCategorie.setItems(FXCollections.observableArrayList(
            "Tous", "Humanitaire", "Santé", "Éducation", "Environnement", "Autre"
        ));
        cbCategorie.setValue("Tous");
        
        // Load evenements
        loadEvenements();
        
        // Set buttons initial state
        btnDonner.setDisable(true);
    }
    
    private void loadEvenements() {
        String categorie = cbCategorie.getValue();
        List<Evenement> events;
        
        if (categorie.equals("Tous")) {
            // Load all active events
            events = evenementService.getByStatus("Actif");
        } else {
            // Filter by category and active status
            List<Evenement> filteredEvents = evenementService.getByCategorie(categorie);
            events = filteredEvents.stream()
                    .filter(e -> "Actif".equals(e.getStatus()))
                    .toList();
        }
        
        evenementList = FXCollections.observableArrayList(events);
        displayEventsAsCards();
    }
    
    private void displayEventsAsCards() {
        // Clear existing items
        eventFlowPane.getChildren().clear();
        
        // For each event, create a card
        for (Evenement event : evenementList) {
            VBox eventCard = createEventCard(event);
            eventFlowPane.getChildren().add(eventCard);
        }
    }
    
    private VBox createEventCard(Evenement event) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
        card.setPrefWidth(250);
        card.setPrefHeight(300);
        card.setSpacing(10);
        
        // Image placeholder
        ImageView imageView = new ImageView();
        Image placeholderImage = null;
        try {
            placeholderImage = new Image(getClass().getResourceAsStream("/tn/esprit/workshop/resources/placeholder.png"));
        } catch (Exception e) {
            System.err.println("Impossible de charger l'image placeholder: " + e.getMessage());
        }
        
        if (placeholderImage == null) {
            // If placeholder not found, create a simple rectangle with text
            Label imagePlaceholder = new Label("Pas d'image");
            imagePlaceholder.setPrefWidth(230);
            imagePlaceholder.setPrefHeight(150);
            imagePlaceholder.setStyle("-fx-background-color: #f5f5f5; -fx-alignment: center;");
            card.getChildren().add(imagePlaceholder);
        } else {
            imageView.setImage(placeholderImage);
            imageView.setFitWidth(230);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            card.getChildren().add(imageView);
        }
        
        // Titre
        Label titleLabel = new Label(event.getTitre());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setWrapText(true);
        card.getChildren().add(titleLabel);
        
        // Description (courte)
        String shortDesc = event.getDescription();
        if (shortDesc.length() > 50) {
            shortDesc = shortDesc.substring(0, 47) + "...";
        }
        Label descLabel = new Label(shortDesc);
        descLabel.setWrapText(true);
        card.getChildren().add(descLabel);
        
        // Niveau
        String niveau = "intermédiaire"; // Default
        if (event.getCategorie().equalsIgnoreCase("débutant")) {
            niveau = "débutant";
        }
        Label niveauLabel = new Label("Niveau: " + niveau);
        niveauLabel.setFont(Font.font("System", 12));
        card.getChildren().add(niveauLabel);
        
        // Prix
        Label prixLabel = new Label("Prix: " + event.getGoalamount() + " DT");
        prixLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        prixLabel.setTextFill(Color.web("#3498db"));
        card.getChildren().add(prixLabel);
        
        // Bouton Voir plus
        Button viewButton = new Button("Voir plus");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewButton.setPrefWidth(230);
        viewButton.setOnAction(e -> {
            selectedEvenement = event;
            displayEvenementDetails();
            btnDonner.setDisable(false);
        });
        card.getChildren().add(viewButton);
        
        return card;
    }
    
    private void displayEvenementDetails() {
        if (selectedEvenement != null) {
            lblTitre.setText(selectedEvenement.getTitre());
            lblDescription.setText(selectedEvenement.getDescription());
            
            if (selectedEvenement.getStartdate() != null) {
                lblStartDate.setText(selectedEvenement.getStartdate().format(formatter));
            }
            
            if (selectedEvenement.getEnddate() != null) {
                lblEndDate.setText(selectedEvenement.getEnddate().format(formatter));
            }
            
            lblLocalisation.setText(selectedEvenement.getLocalisation());
            lblGoalAmount.setText(String.format("%.2f DT", selectedEvenement.getGoalamount()));
            lblCollectedAmount.setText(String.format("%.2f DT", selectedEvenement.getCollectedamount()));
            lblCategorie.setText(selectedEvenement.getCategorie());
            
            // Calculate progress percentage
            double goalAmount = selectedEvenement.getGoalamount();
            double collectedAmount = selectedEvenement.getCollectedamount();
            double progressPercent = goalAmount > 0 ? (collectedAmount / goalAmount) * 100 : 0;
            lblProgression.setText(String.format("%.1f%%", progressPercent));
        }
    }
    
    private void clearEvenementDetails() {
        lblTitre.setText("");
        lblDescription.setText("");
        lblStartDate.setText("");
        lblEndDate.setText("");
        lblLocalisation.setText("");
        lblGoalAmount.setText("");
        lblCollectedAmount.setText("");
        lblCategorie.setText("");
        lblProgression.setText("");
    }
    
    @FXML
    private void handleFilterByCategorie(ActionEvent event) {
        loadEvenements();
    }
    
    @FXML
    private void handleDonner(ActionEvent event) {
        if (selectedEvenement != null && validateDonInput()) {
            try {
                Don don = new Don();
                don.setEvenement_id(selectedEvenement.getId());
                don.setDonationdate(LocalDateTime.now());
                don.setAmount(Double.parseDouble(tfAmount.getText().trim()));
                don.setPaymentref(tfPaymentRef.getText().trim());
                don.setMessage(taMessage.getText().trim());
                don.setCreateur_id(CURRENT_USER_ID); // In a real app this might be different
                don.setUser_id(CURRENT_USER_ID); // The authenticated user
                
                donService.ajouter(don);
                
                // Refresh the event details to show updated collected amount
                selectedEvenement = evenementService.getById(selectedEvenement.getId());
                displayEvenementDetails();
                
                // Clear donation fields
                tfAmount.clear();
                tfPaymentRef.clear();
                taMessage.clear();
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Merci pour votre don!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
            }
        }
    }
    
    private boolean validateDonInput() {
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