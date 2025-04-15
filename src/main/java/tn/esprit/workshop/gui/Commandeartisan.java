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

public class Commandeartisan {

    @FXML private TableView<Commande> commandeTable;
    @FXML private TableColumn<Commande, Integer> colId;
    @FXML private TableColumn<Commande, String> colDate;
    @FXML private TableColumn<Commande, String> colStatut;
    @FXML private TableColumn<Commande, Double> colPrix;
    @FXML private TableColumn<Commande, List<CommandeProduit>> colProduits;
    @FXML private TableColumn<Commande, Void> colActions;

    private final ServiceCommande serviceCommande = new ServiceCommande();

    public void initialize() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateCmd().toString()));
        colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));
        colPrix.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrix()).asObject());
        colProduits.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getProduits()));
        colProduits.setCellFactory(param -> new Commandeclient.ProduitCell());

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editStatutBtn = new Button("Modifier Statut");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox hbox = new HBox(5, editStatutBtn, deleteBtn);

            {
                editStatutBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                editStatutBtn.setOnAction(e -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    openModifierStatutWindow(commande);
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
                setGraphic(empty ? null : hbox);
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

    private void openModifierStatutWindow(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierStatut.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Modifier Statut");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            ModifierStatut controller = loader.getController();
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
