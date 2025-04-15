package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Commande;
import tn.esprit.workshop.services.ServiceCommande;

import java.sql.SQLException;

public class ModifierStatut {

    @FXML private ComboBox<String> statutCombo;

    private Commande commande;
    private Runnable onUpdate;
    private final ServiceCommande serviceCommande = new ServiceCommande();

    public void setCommande(Commande commande) {
        this.commande = commande;
        statutCombo.getItems().addAll("En attente", "En cours", "Livrée", "Annulée");
        statutCombo.setValue(commande.getStatut());
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    @FXML
    public void valider() {
        String nouveauStatut = statutCombo.getValue();
        if (nouveauStatut == null || nouveauStatut.isEmpty()) return;

        try {
            serviceCommande.updateStatutCommande(commande.getId(), nouveauStatut);
            if (onUpdate != null) onUpdate.run();
            ((Stage) statutCombo.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}