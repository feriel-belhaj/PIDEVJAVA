package tn.esprit.workshop.gui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Commande;
import tn.esprit.workshop.models.CommandeProduit;
import tn.esprit.workshop.services.ServiceCommande;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Commandeclient {

    @FXML private TableView<Commande> commandeTable;
    //@FXML private TableColumn<Commande, Integer> colId;
    @FXML private TableColumn<Commande, String> colDate;
    @FXML private TableColumn<Commande, String> colStatut;
    @FXML private TableColumn<Commande, Double> colPrix;
    @FXML private TableColumn<Commande, List<CommandeProduit>> colProduits;
    @FXML private TableColumn<Commande, Void> colActions;

    private final ServiceCommande serviceCommande = new ServiceCommande();

    public void initialize() {
        //colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateCmd().toString()));
        colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));
        colPrix.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrix()).asObject());
        colProduits.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getProduits()));
        colProduits.setCellFactory(param -> new ProduitCell());

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier Quantité");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox hbox = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setPrefWidth(150);
                deleteBtn.setPrefWidth(100);

                editBtn.setOnAction(e -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    openModifierQuantiteWindow(commande);
                });

                deleteBtn.setOnAction(e -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette commande ?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                serviceCommande.delete(commande.getId());
                                loadCommandes();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                } else {
                    Commande commande = getTableView().getItems().get(getIndex());
                    boolean enAttente = "En attente".equalsIgnoreCase(commande.getStatut());

                    // Gestion bouton Modifier
                    editBtn.setDisable(!enAttente);
                    editBtn.setStyle(enAttente
                            ? "-fx-background-color: #27ae60; -fx-text-fill: white;"
                            : "-fx-background-color: #bdc3c7; -fx-text-fill: #666;");
                    editBtn.setTooltip(enAttente
                            ? null
                            : new Tooltip("Impossible de modifier une commande non en attente"));

                    // Gestion bouton Supprimer
                    deleteBtn.setDisable(!enAttente);
                    deleteBtn.setStyle(enAttente
                            ? "-fx-background-color: #c0392b; -fx-text-fill: white;"
                            : "-fx-background-color: #bdc3c7; -fx-text-fill: #666;");
                    deleteBtn.setTooltip(enAttente
                            ? null
                            : new Tooltip("Suppression désactivée : commande non en attente"));

                    setGraphic(hbox);
                }
            }
        });

        try {
            loadCommandes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadCommandes() throws SQLException {
        List<Commande> commandes = serviceCommande.showAll();
        commandeTable.setItems(FXCollections.observableArrayList(commandes));
    }

    public static class ProduitCell extends TableCell<Commande, List<CommandeProduit>> {
        @Override
        protected void updateItem(List<CommandeProduit> produits, boolean empty) {
            super.updateItem(produits, empty);
            if (empty || produits == null) {
                setText(null);
            } else {
                StringBuilder sb = new StringBuilder();
                for (CommandeProduit cp : produits) {
                    sb.append(cp.getProduit().getNom()).append(" - ")
                            .append("Prix: ").append(cp.getProduit().getPrix()).append(" - ")
                            .append("Qté: ").append(cp.getQuantite()).append("\n");
                }
                setText(sb.toString());
            }
        }
    }

    private void openModifierQuantiteWindow(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierQuantite.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Modifier Quantité");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            ModifierQuantite controller = loader.getController();
            controller.setCommande(commande);
            controller.setOnUpdate(() -> {
                try {
                    loadCommandes();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
