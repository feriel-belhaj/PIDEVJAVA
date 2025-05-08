package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import tn.esprit.workshop.models.Don;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.services.EvenementService;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DonDetailController implements Initializable {

    @FXML private Label lblId;
    @FXML private Label lblEvenement;
    @FXML private Label lblDonateur;
    @FXML private Label lblMontant;
    @FXML private Label lblDate;
    @FXML private Label lblMessage;
    
    private Don don;
    private EvenementService evenementService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        evenementService = new EvenementService();
    }
    
    public void setDon(Don don) {
        this.don = don;
        displayDonDetails();
    }
    
    private void displayDonDetails() {
        if (don == null) return;
        
        lblId.setText(String.valueOf(don.getId()));
        
        // Récupérer le titre de l'événement
        Evenement evenement = evenementService.getById(don.getEvenement_id());
        lblEvenement.setText(evenement != null ? evenement.getTitre() : "Événement inconnu");
        
        // Afficher les autres détails
        lblDonateur.setText("Utilisateur #" + don.getUser_id());
        lblMontant.setText(String.format("%.2f DT", don.getAmount()));
        
        if (don.getDonationdate() != null) {
            lblDate.setText(don.getDonationdate().format(formatter));
        } else {
            lblDate.setText("Date inconnue");
        }
        
        lblMessage.setText(don.getMessage() != null ? don.getMessage() : "Aucun message");
    }
} 