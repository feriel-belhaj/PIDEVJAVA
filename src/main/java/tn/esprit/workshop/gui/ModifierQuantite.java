package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Commande;
import tn.esprit.workshop.models.CommandeProduit;
import tn.esprit.workshop.models.Produit;
import tn.esprit.workshop.services.ServiceCommande;
import tn.esprit.workshop.services.Serviceproduit;

public class ModifierQuantite {

    @FXML private ComboBox<CommandeProduit> produitCombo;
    @FXML private TextField quantiteField;

    private Commande commande;
    private final ServiceCommande serviceCommande = new ServiceCommande();
    private final Serviceproduit serviceProduit = new Serviceproduit(); // ✅ pour récupérer le stock à jour
    private Runnable onUpdate;

    public void setCommande(Commande commande) {
        this.commande = commande;
        produitCombo.getItems().setAll(commande.getProduits());

        produitCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CommandeProduit item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getProduit().getNom());
            }
        });

        produitCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CommandeProduit item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getProduit().getNom());
            }
        });
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    @FXML
    public void valider() {
        CommandeProduit selected = produitCombo.getValue();
        if (selected == null || quantiteField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un produit et saisir une quantité.");
            alert.showAndWait();
            return;
        }

        try {
            int newQte = Integer.parseInt(quantiteField.getText());

            if (newQte <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "La quantité doit être supérieure à zéro.");
                alert.showAndWait();
                return;
            }

            if (!"En attente".equalsIgnoreCase(commande.getStatut())) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "La commande n'est plus modifiable car elle n'est plus en attente.");
                alert.showAndWait();
                return;
            }

            // ✅ Recharge le produit depuis la base pour avoir le vrai stock
            Produit produitActuel = serviceProduit.getById(selected.getProduit().getId());
            int stockDisponible = produitActuel.getQuantiteStock();

            if (newQte > stockDisponible) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Stock insuffisant. Stock disponible : " + stockDisponible);
                alert.showAndWait();
                return;
            }

            // Mettre à jour la commande
            serviceCommande.updateQuantiteProduit(commande.getId(), produitActuel.getId(), newQte);
            serviceCommande.updatePrix(commande.getId());

            // ✅ Mise à jour du stock
            int stockRestant = stockDisponible - newQte; // On réduit le stock disponible
            produitActuel.setQuantiteStock(stockRestant);
            serviceProduit.update(produitActuel); // Met à jour le produit avec la nouvelle quantité

            if (onUpdate != null) {
                onUpdate.run();
            }

            ((Stage) quantiteField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez saisir une quantité valide.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour.");
            alert.showAndWait();
        }
    }
}
