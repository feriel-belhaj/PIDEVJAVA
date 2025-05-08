package tn.esprit.workshop.gui;

import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Don;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.models.SessionManager;
import tn.esprit.workshop.services.DonService;
import tn.esprit.workshop.services.EvenementService;
import tn.esprit.workshop.services.UserGetData;
import tn.esprit.workshop.utils.StripeApi;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class StripeCheckoutController implements Initializable {

    @FXML
    private WebView webView;

    @FXML
    private Button btnCancel;

    private WebEngine webEngine;
    private Evenement evenement;
    private double amount;
    private String message;
    private DonService donService;
    private EvenementService evenementService;
    private int returnEventId;
    private String sessionId;
    private Stage parentStage;
    private boolean paymentProcessed = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        donService = new DonService();
        evenementService = new EvenementService();
        
        webEngine = webView.getEngine();
        
        System.out.println("Initialisation du contrôleur Stripe Checkout");
        
        // Écouter les changements d'état pour détecter quand le paiement est complété
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                String currentUrl = webEngine.getLocation();
                System.out.println("WebView URL changée : " + currentUrl);
                
                // Vérifier si l'URL correspond à notre URL de succès
                if (currentUrl.contains("success") && currentUrl.contains("session_id=")) {
                    System.out.println("Paiement réussi détecté : " + currentUrl);
                    // Extraire l'ID de session de l'URL
                    try {
                        String sessionId = currentUrl.substring(currentUrl.indexOf("session_id=") + 11);
                        if (sessionId.contains("&")) {
                            sessionId = sessionId.substring(0, sessionId.indexOf("&"));
                        }
                        this.sessionId = sessionId;
                        System.out.println("ID de session extrait : " + sessionId);
                        
                        // Éviter le traitement en double
                        if (!paymentProcessed) {
                            paymentProcessed = true;
                            // Enregistrer le don après paiement réussi
                            Platform.runLater(this::saveDonation);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'extraction de l'ID de session : " + e.getMessage());
                        e.printStackTrace();
                    }
                } 
                // Vérifier si l'URL correspond à notre URL d'annulation
                else if (currentUrl.contains("cancel")) {
                    System.out.println("Annulation de paiement détectée");
                    // Retourner à la page de détail sans enregistrer le don
                    Platform.runLater(this::returnToEventDetail);
                }
                // Vérifier si l'URL contient "checkout.stripe.com" et le paramètre "success_url"
                else if (currentUrl.contains("checkout.stripe.com") && currentUrl.contains("success_url=")) {
                    System.out.println("Page Stripe Checkout avec URL de succès détectée");
                    // On est sur la page Stripe et le paiement est en cours
                }
            } else if (newState == State.FAILED) {
                System.err.println("Échec du chargement de la page");
                Throwable exception = webEngine.getLoadWorker().getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });
        
        // Intercepter les changements d'emplacement avant qu'ils ne se produisent
        webEngine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            System.out.println("Redirection vers : " + newLoc);
            
            // Vérifier directement ici si c'est une URL de succès ou d'annulation
            if (newLoc != null) {
                if (newLoc.contains("success.artizina.com") && newLoc.contains("session_id=")) {
                    System.out.println("URL de succès détectée dans la redirection : " + newLoc);
                    try {
                        String sid = newLoc.substring(newLoc.indexOf("session_id=") + 11);
                        if (sid.contains("&")) {
                            sid = sid.substring(0, sid.indexOf("&"));
                        }
                        sessionId = sid;
                        System.out.println("ID de session extrait de la redirection : " + sessionId);
                        
                        // Éviter le traitement en double
                        if (!paymentProcessed) {
                            paymentProcessed = true;
                            // Traiter le paiement réussi immédiatement
                            Platform.runLater(this::saveDonation);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'extraction de l'ID de session (redirection) : " + e.getMessage());
                    }
                } else if (newLoc.contains("cancel.artizina.com")) {
                    System.out.println("URL d'annulation détectée dans la redirection");
                    Platform.runLater(this::returnToEventDetail);
                }
            }
        });
    }

    public void loadStripeCheckout(Evenement evenement, double amount, String message, int returnEventId) {
        this.evenement = evenement;
        this.amount = amount;
        this.message = message;
        this.returnEventId = returnEventId;
        
        try {
            // Créer la session Stripe Checkout
            String eventName = evenement.getTitre();
            
            System.out.println("Création de la session Stripe pour : " + eventName + ", montant : " + amount);
            
            // Les URLs sont définies dans StripeApi maintenant
            String stripeUrl = StripeApi.createCheckoutSession(amount, eventName, "", "");
            System.out.println("URL Stripe générée : " + stripeUrl);
            
            // Charger l'URL dans la WebView
            webEngine.load(stripeUrl);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du paiement Stripe: " + e.getMessage());
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation du paiement Stripe: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }
    
    private void saveDonation() {
        System.out.println("Début de l'enregistrement du don");
        try {
            // Créer le don
            Don don = new Don();
            don.setEvenement_id(evenement.getId());
            don.setDonationdate(LocalDateTime.now());
            don.setAmount(amount);
            don.setPaymentref("stripe_" + sessionId);
            don.setMessage(message);
            
            // Utiliser l'ID de l'utilisateur connecté
            int currentUserId = SessionManager.getUserId();
            don.setCreateur_id(currentUserId);
            don.setUser_id(currentUserId);
            
            System.out.println("Tentative d'enregistrement du don - Montant: " + amount + ", Référence: stripe_" + sessionId);
            
            // Ajouter le don
            donService.ajouter(don);
            
            System.out.println("Don enregistré avec succès");
            
            // Afficher un message de confirmation
            showAlert(AlertType.INFORMATION, "Succès", "Merci pour votre don ! Le paiement a été effectué avec succès.");
            
            // Retourner à la page de détail de l'événement
            returnToEventDetail();
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement du don: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'enregistrement du don: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        System.out.println("Annulation manuelle du paiement");
        returnToEventDetail();
    }
    
    private void returnToEventDetail() {
        System.out.println("Retour à la page de détail de l'événement");
        try {
            // Charger la vue détaillée de l'événement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EvenementDetail.fxml"));
            Parent root = loader.load();
            
            // Récupérer l'événement mis à jour
            System.out.println("Récupération de l'événement mis à jour, ID: " + returnEventId);
            Evenement updatedEvent = evenementService.getById(returnEventId);
            
            // Configurer le contrôleur
            EvenementDetailController controller = loader.getController();
            controller.setEvenement(updatedEvent);
            
            System.out.println("Recherche du conteneur principal pour l'affichage");
            
            // Trouver le conteneur principal pour remplacer son contenu
            StackPane container = (StackPane) btnCancel.getScene().lookup("#evenementsContainer");
            if (container != null) {
                System.out.println("Conteneur trouvé dans la scène actuelle");
                container.getChildren().clear();
                container.getChildren().add(root);
            } else if (parentStage != null) {
                // Si on ne trouve pas le conteneur dans cette scène mais qu'on a la référence du stage parent
                System.out.println("Conteneur non trouvé, recherche dans le stage parent");
                StackPane parentContainer = (StackPane) parentStage.getScene().lookup("#evenementsContainer");
                if (parentContainer != null) {
                    System.out.println("Conteneur trouvé dans le stage parent");
                    parentContainer.getChildren().clear();
                    parentContainer.getChildren().add(root);
                } else {
                    // Fallback - changer toute la scène
                    System.out.println("Aucun conteneur trouvé, remplacement de toute la scène");
                    Scene scene = new Scene(root);
                    parentStage.setScene(scene);
                }
            } else {
                System.out.println("Impossible de trouver un conteneur et pas de référence au stage parent");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à la page de détail: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du retour à la page de détail: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
} 