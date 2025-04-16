package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.workshop.models.Certificat;
import tn.esprit.workshop.models.Formation;

import java.time.format.DateTimeFormatter;

public class CertificatViewController {
    @FXML private Label lblNomComplet;
    @FXML private Label lblFormationTitre;
    @FXML private Label lblNiveau;
    @FXML private Label lblOrganisme;
    @FXML private Label lblDate;
    @FXML private ImageView imgSignature1;
    @FXML private ImageView imgSignature2;
    @FXML private ImageView imgCachet;

    public void setCertificat(Certificat certificat, Formation formation) {
        // Affichage du nom complet
        lblNomComplet.setText(certificat.getNom().toUpperCase() + " " + certificat.getPrenom());
        
        // Affichage du titre de la formation
        lblFormationTitre.setText("\"" + formation.getTitre() + "\"");
        
        // Affichage du niveau
        lblNiveau.setText(certificat.getNiveau());
        
        // Affichage de l'organisme
        lblOrganisme.setText(certificat.getNomorganisme());
        
        // Formatage et affichage de la date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblDate.setText("Délivré le " + certificat.getDateobt().format(formatter));
        
        // Chargement des images (à adapter selon vos besoins)
        try {
            imgSignature1.setImage(new Image(getClass().getResourceAsStream("/images/signature1.png")));
            imgSignature2.setImage(new Image(getClass().getResourceAsStream("/images/signature2.png")));
            imgCachet.setImage(new Image(getClass().getResourceAsStream("/images/cachet.png")));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
        }
    }
}