package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.services.DonService;
import tn.esprit.workshop.services.EvenementService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DonFormController implements Initializable {

    @FXML
    private Label lblTitreEvent;

    @FXML
    private Label lblObjectif;

    @FXML
    private Label lblCollecte;

    @FXML
    private Label lblProgression;

    @FXML
    private TextField tfMontant;

    @FXML
    private TextField tfReference;

    @FXML
    private TextArea taMessage;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnConfirmer;

    private Evenement evenement;
    private DonService donService;
    private EvenementService evenementService;
    private int returnEventId; // Pour stocker l'ID de l'événement pour le retour

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        donService = new DonService();
        evenementService = new EvenementService();
        
        // Masquer le champ de référence car on utilisera Stripe
        tfReference.setVisible(false);
        tfReference.setManaged(false);
        
        // Mettre à jour le texte du bouton
        btnConfirmer.setText("Payer avec Stripe");
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        updateDisplay();
    }
    
    public void setReturnEventId(int eventId) {
        this.returnEventId = eventId;
    }

    private void updateDisplay() {
        if (evenement != null) {
            lblTitreEvent.setText("Faire un don pour : " + evenement.getTitre());
            lblObjectif.setText(String.format("%.2f DT", evenement.getGoalamount()));
            lblCollecte.setText(String.format("%.2f DT", evenement.getCollectedamount()));
            
            double progressPercent = evenement.getGoalamount() > 0 
                ? (evenement.getCollectedamount() / evenement.getGoalamount()) * 100 
                : 0;
            lblProgression.setText(String.format("%.1f%%", progressPercent));
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        // Retourner à la page de détail de l'événement
        returnToEventDetail();
    }

    @FXML
    private void handleConfirmer(ActionEvent event) {
        if (validateInput()) {
            try {
                double amount = Double.parseDouble(tfMontant.getText().trim());
                String message = taMessage.getText().trim();
                
                // Ouvrir la page de paiement Stripe
                openStripeCheckout(amount, message);
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
            }
        }
    }
    
    private void openStripeCheckout(double amount, String message) {
        try {
            // Récupérer la scène principale et son conteneur
            Stage mainStage = (Stage) btnConfirmer.getScene().getWindow();
            
            // Trouver le conteneur principal de l'application
            StackPane container = (StackPane) btnConfirmer.getScene().lookup("#evenementsContainer");
            
            if (container != null) {
                // Charger la vue Stripe Checkout
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StripeCheckout.fxml"));
                Parent root = loader.load();
                
                // Configurer le contrôleur
                StripeCheckoutController controller = loader.getController();
                
                // Passer la référence de la scène principale au contrôleur
                controller.setParentStage(mainStage);
                
                // Charger le checkout Stripe et configurer le contrôleur
                controller.loadStripeCheckout(evenement, amount, message, returnEventId);
                
                // Afficher la vue dans le conteneur principal
                container.getChildren().clear();
                container.getChildren().add(root);
            } else {
                // Si on ne trouve pas de conteneur, changer toute la scène (cas de repli)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StripeCheckout.fxml"));
                Parent root = loader.load();
                
                StripeCheckoutController controller = loader.getController();
                controller.setParentStage(mainStage);
                controller.loadStripeCheckout(evenement, amount, message, returnEventId);
                
                Scene scene = new Scene(root);
                mainStage.setScene(scene);
            }
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du paiement Stripe: " + e.getMessage());
        }
    }
    
    private void returnToEventDetail() {
        try {
            // Charger la vue détaillée de l'événement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EvenementDetail.fxml"));
            Parent root = loader.load();
            
            // Récupérer l'événement mis à jour
            Evenement updatedEvent = evenementService.getById(returnEventId);
            
            // Configurer le contrôleur
            EvenementDetailController controller = loader.getController();
            controller.setEvenement(updatedEvent);
            
            // Afficher dans le conteneur principal
            StackPane container = (StackPane) btnConfirmer.getScene().lookup("#evenementsContainer");
            if (container != null) {
                container.getChildren().clear();
                container.getChildren().add(root);
            } else {
                // Fallback
                Scene scene = new Scene(root);
                Stage stage = (Stage) btnConfirmer.getScene().getWindow();
                stage.setScene(scene);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la page de détail: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        StringBuilder errorMsg = new StringBuilder();
        
        // Validation du montant
        try {
            double amount = Double.parseDouble(tfMontant.getText().trim());
            if (amount <= 0) {
                errorMsg.append("Le montant du don doit être supérieur à 0.\n");
            }
        } catch (NumberFormatException e) {
            errorMsg.append("Le montant du don doit être un nombre valide.\n");
        }
        
        // Afficher les erreurs s'il y en a
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
} 