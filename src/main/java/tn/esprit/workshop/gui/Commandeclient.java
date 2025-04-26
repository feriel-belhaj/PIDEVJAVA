package tn.esprit.workshop.gui;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
    @FXML private TableColumn<Commande, String> colDate;
    @FXML private TableColumn<Commande, String> colStatut;
    @FXML private TableColumn<Commande, Double> colPrix;
    @FXML private TableColumn<Commande, List<CommandeProduit>> colProduits;
    @FXML private TableColumn<Commande, Void> colActions;
    @FXML private TableColumn<Commande, Void> colPaiement;

    private final ServiceCommande serviceCommande = new ServiceCommande();
    private static final String STRIPE_SECRET_KEY = "";
    private static final String STRIPE_SUCCESS_URL = "https://checkout.stripe.com/success";
    private static final String STRIPE_CANCEL_URL = "https://checkout.stripe.com/cancel";

    public void initialize() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
        configureColumns();
        refreshCommandes();
    }

    private void configureColumns() {
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateCmd().toString()));
        colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));
        colPrix.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrix()).asObject());
        colProduits.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getProduits()));
        colProduits.setCellFactory(param -> new tn.esprit.workshop.gui.Commandeclient.ProduitCell());

        colActions.setCellFactory(param -> createActionCell());
        colPaiement.setCellFactory(param -> createPaymentCell());
    }

    private TableCell<Commande, Void> createActionCell() {
        return new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox hbox = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                editBtn.setOnAction(e -> handleEditAction());
                deleteBtn.setOnAction(e -> handleDeleteAction());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                } else {
                    Commande commande = getTableView().getItems().get(getIndex());
                    boolean enAttente = "En attente".equalsIgnoreCase(commande.getStatut());
                    editBtn.setDisable(!enAttente);
                    deleteBtn.setDisable(!enAttente);
                    setGraphic(hbox);
                }
            }

            private void handleEditAction() {
                Commande commande = getTableView().getItems().get(getIndex());
                if ("En attente".equalsIgnoreCase(commande.getStatut())) {
                    openModifierQuantiteWindow(commande);
                } else {
                    showAlert("Seules les commandes en attente peuvent être modifiées");
                }
            }

            private void handleDeleteAction() {
                Commande commande = getTableView().getItems().get(getIndex());
                if ("En attente".equalsIgnoreCase(commande.getStatut())) {
                    confirmAndDelete(commande);
                } else {
                    showAlert("Seules les commandes en attente peuvent être supprimées");
                }
            }
        };
    }

    private TableCell<Commande, Void> createPaymentCell() {
        return new TableCell<>() {
            private final Button payBtn = new Button("Payer");

            {
                payBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                payBtn.setOnAction(e -> handlePaymentAction());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                } else {
                    Commande commande = getTableView().getItems().get(getIndex());
                    boolean enAttente = "En attente".equalsIgnoreCase(commande.getStatut());
                    payBtn.setDisable(!enAttente);
                    setGraphic(payBtn);
                }
            }

            private void handlePaymentAction() {
                Commande commande = getTableView().getItems().get(getIndex());
                if ("En attente".equalsIgnoreCase(commande.getStatut())) {
                    try {
                        processPayment(commande);
                    } catch (StripeException ex) {
                        showAlert("Erreur de paiement: " + ex.getMessage());
                    }
                } else {
                    showAlert("Seules les commandes en attente peuvent être payées");
                }
            }
        };
    }

    private void processPayment(Commande commande) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Commande #" + commande.getId())
                                                                .build())
                                                .setUnitAmount((long) (commande.getPrix() * 100))
                                                .build())
                                .setQuantity(1L)
                                .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(STRIPE_SUCCESS_URL)
                .setCancelUrl(STRIPE_CANCEL_URL)
                .putMetadata("commande_id", String.valueOf(commande.getId()))
                .build();

        Session session = Session.create(params);
        openPaymentWindow(session.getUrl(), commande.getId());
    }

    private void openPaymentWindow(String paymentUrl, int commandeId) {
        Stage paymentStage = new Stage();
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserAgent("Mozilla/5.0");

        webEngine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            if (newUrl != null) {
                if (newUrl.contains(STRIPE_SUCCESS_URL)) {
                    Platform.runLater(() -> {
                        paymentStage.close();
                        verifyAndHandlePayment(commandeId);
                    });
                } else if (newUrl.contains(STRIPE_CANCEL_URL)) {
                    Platform.runLater(() -> {
                        paymentStage.close();
                        showAlert("Paiement annulé", "Vous avez annulé le paiement", Alert.AlertType.WARNING);
                    });
                }
            }
        });

        webEngine.getLoadWorker().exceptionProperty().addListener((obs, oldEx, newEx) -> {
            if (newEx != null) {
                Platform.runLater(() -> {
                    paymentStage.close();
                    showAlert("Erreur de paiement", "Impossible de charger la page de paiement", Alert.AlertType.ERROR);
                });
            }
        });

        paymentStage.setScene(new Scene(webView, 800, 600));
        paymentStage.setTitle("Paiement Stripe - Commande #" + commandeId);
        paymentStage.setOnCloseRequest(e -> {
            showAlert("Paiement interrompu", "La fenêtre de paiement a été fermée", Alert.AlertType.WARNING);
        });
        paymentStage.show();

        webEngine.load(paymentUrl);
    }

    private void verifyAndHandlePayment(int commandeId) {
        try {
            serviceCommande.updateStatut(commandeId, "Payé");
            showPaymentSuccessAlert(commandeId);
            refreshCommandes();
        } catch (SQLException e) {
            showAlert("Erreur de mise à jour", "Le paiement a réussi mais la commande n'a pas été mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showPaymentSuccessAlert(int commandeId) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paiement confirmé");
        alert.setHeaderText("✅ Paiement réussi");
        alert.setContentText("Votre commande #" + commandeId + " a été payée avec succès.\n"
                + "Un email de confirmation vous a été envoyé.");

        // Style inline sans CSS
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #2e7d32; -fx-border-width: 2px;");
        Label contentLabel = (Label) dialogPane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2e7d32;");
        }

        alert.showAndWait();
    }

    private void confirmAndDelete(Commande commande) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la commande #" + commande.getId());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette commande?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceCommande.delete(commande.getId());
                    refreshCommandes();
                } catch (SQLException e) {
                    showAlert("Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    private void openModifierQuantiteWindow(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierQuantite.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Modifier Quantité - Commande #" + commande.getId());
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            ModifierQuantite controller = loader.getController();
            controller.setCommande(commande);
            controller.setOnUpdate(this::refreshCommandes);

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur d'ouverture: " + e.getMessage());
        }
    }

    private void refreshCommandes() {
        try {
            List<Commande> commandes = serviceCommande.showAll();
            commandeTable.setItems(FXCollections.observableArrayList(commandes));
        } catch (SQLException e) {
            showAlert("Erreur de chargement des commandes: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        showAlert("Erreur", message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
                    sb.append("• ")
                            .append(cp.getProduit().getNom())
                            .append(" (Qté: ").append(cp.getQuantite())
                            .append(", Prix: ").append(cp.getProduit().getPrix()).append(" DT)\n");
                }
                setText(sb.toString());
            }
        }
    }
}