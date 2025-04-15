package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Produit;
import tn.esprit.workshop.services.Serviceproduit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherProduit {

    @FXML
    private TableView<Produit> tableProduit;
    @FXML
    private TableColumn<Produit, String> colNom;
    @FXML
    private TableColumn<Produit, String> colDescription;
    @FXML
    private TableColumn<Produit, Double> colPrix;
    @FXML
    private TableColumn<Produit, Integer> colQuantite;
    @FXML
    private TableColumn<Produit, String> colCategorie;
    @FXML
    private TableColumn<Produit, java.sql.Date> colDateCreation;

    @FXML
    private TableColumn<Produit, Button> colModifier;
    @FXML
    private TableColumn<Produit, Button> colSupprimer;

    private final Serviceproduit sp = new Serviceproduit();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        // Colonne Modifier
        colModifier.setCellFactory(param -> new TableCell<Produit, Button>() {
            private final Button btnModifier = new Button("Modifier");

            {
                btnModifier.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnModifier);
                    btnModifier.setOnAction(event -> {
                        Produit produit = getTableView().getItems().get(getIndex());
                        modifierProduit(produit);
                    });
                }
            }
        });

        // Colonne Supprimer
        colSupprimer.setCellFactory(param -> new TableCell<Produit, Button>() {
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnSupprimer);
                    btnSupprimer.setOnAction(event -> {
                        Produit produit = getTableView().getItems().get(getIndex());
                        supprimerProduit(produit);
                    });
                }
            }
        });

        // Charger la table
        rafraichirTable();
    }

    public void rafraichirTable() {
        try {
            List<Produit> produits = sp.showAll();
            ObservableList<Produit> produitList = FXCollections.observableArrayList(produits);
            tableProduit.setItems(produitList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashbord.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableProduit.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void modifierProduit(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierproduit.fxml"));
            Parent root = loader.load();

            ModifierProduit controller = loader.getController();
            controller.preRemplirFormulaire(produit);
            controller.setAfficherProduitController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void supprimerProduit(Produit produit) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Voulez-vous vraiment supprimer ce produit ?");
        confirmation.setContentText("Produit : " + produit.getNom());

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    sp.delete(produit);
                    rafraichirTable();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Produit supprimé avec succès !");
                    alert.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Erreur lors de la suppression !");
                    alert.show();
                }
            }
        });
    }
    public void goToAjouter(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Ajouterproduit.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
