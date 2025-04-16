package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Modality;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServicePartenariat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class ListePartenariatsController {

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnRechercher;

    @FXML
    private Button btnAjouter;

    private ServicePartenariat servicePartenariat;

    @FXML
    public void initialize() {
        servicePartenariat = new ServicePartenariat();
        chargerPartenariats();
    }

    private void chargerPartenariats() {
        try {
            List<Partenariat> partenariats = servicePartenariat.showAll();
            
            // Trier les partenariats par ID décroissant (les plus récents en premier)
            partenariats.sort(Comparator.comparingInt(Partenariat::getId).reversed());
            
            cardsContainer.getChildren().clear();
            
            for (Partenariat partenariat : partenariats) {
                VBox card = creerCartePartenariat(partenariat);
                cardsContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des partenariats", e.getMessage());
        }
    }

    private VBox creerCartePartenariat(Partenariat partenariat) {
        // Création de la carte
        VBox card = new VBox();
        card.setPrefWidth(300.0);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-background-radius: 10;");
        card.setCursor(Cursor.HAND); // Changer le curseur pour indiquer que c'est cliquable

        // Conteneur pour l'image et le statut
        StackPane imageContainer = new StackPane();
        
        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300.0);
        imageView.setFitHeight(200.0);
        imageView.setPreserveRatio(false); // Standardiser la taille des images

        // Chargement de l'image
        String imagePath = partenariat.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
        }
        
        // Ajouter l'étiquette de statut sur l'image
        Label statutOverlay = new Label(partenariat.getStatut());
        
        // Définir le style de l'étiquette en fonction du statut
        String styleStatut;
        switch (partenariat.getStatut()) {
            case "Actif":
                styleStatut = "-fx-background-color: #2ecc71; -fx-text-fill: white;";
                break;
            case "EnCours":
                styleStatut = "-fx-background-color: #FFD700; -fx-text-fill: white;";
                break;
            case "Expiré":
                styleStatut = "-fx-background-color: #e74c3c; -fx-text-fill: white;";
                break;
            default:
                styleStatut = "-fx-background-color: #95a5a6; -fx-text-fill: white;";
        }
        
        statutOverlay.setStyle(styleStatut + " -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-weight: bold;");
        statutOverlay.setMaxHeight(30);
        
        // Positionner l'étiquette en haut à droite de l'image
        StackPane.setAlignment(statutOverlay, Pos.TOP_RIGHT);
        StackPane.setMargin(statutOverlay, new javafx.geometry.Insets(10, 10, 0, 0));
        
        imageContainer.getChildren().addAll(imageView, statutOverlay);

        // Contenu de la carte
        VBox contenu = new VBox(10);
        contenu.setStyle("-fx-padding: 20;");

        Label nomLabel = new Label(partenariat.getNom());
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label typeLabel = new Label("Type: " + partenariat.getType());
        typeLabel.setStyle("-fx-text-fill: #666666;");

        Label descriptionLabel = new Label(partenariat.getDescription());
        descriptionLabel.setStyle("-fx-text-fill: #666666;");
        descriptionLabel.setWrapText(true);

        Label dateDebutLabel = new Label("Début: " + partenariat.getDateDebut());
        dateDebutLabel.setStyle("-fx-text-fill: #666666;");

        Label dateFinLabel = new Label("Fin: " + partenariat.getDateFin());
        dateFinLabel.setStyle("-fx-text-fill: #666666;");

        Button btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-background-color: #D4A76A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 20;");
        btnModifier.setOnAction(e -> {
            e.consume(); // Empêcher l'événement de se propager à la carte
            modifierPartenariat(partenariat);
        });

        contenu.getChildren().addAll(nomLabel, typeLabel, descriptionLabel, dateDebutLabel, dateFinLabel, btnModifier);
        card.getChildren().addAll(imageContainer, contenu);
        
        // Ajouter un gestionnaire d'événements pour ouvrir les détails lors d'un clic sur la carte
        card.setOnMouseClicked(e -> ouvrirDetailsPartenariat(partenariat));

        return card;
    }
    
    private void ouvrirDetailsPartenariat(Partenariat partenariat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsPartenariat.fxml"));
            Parent root = loader.load();
            
            DetailsPartenariatController controller = loader.getController();
            controller.initData(partenariat);
            
            // Ajouter un callback pour rafraîchir la liste après suppression
            controller.setOnDeleteCallback(this::chargerPartenariats);
            
            Stage stage = new Stage();
            stage.setTitle("Détails du Partenariat");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Rendre la fenêtre modale
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre de détails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void ajouterPartenariat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPartenariat.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Partenariat");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Rafraîchir la liste après la fermeture de la fenêtre
            stage.setOnHidden(e -> chargerPartenariats());
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre d'ajout de partenariat");
        }
    }

    @FXML
    void rechercher() {
        String recherche = searchField.getText().toLowerCase();
        try {
            List<Partenariat> partenariats = servicePartenariat.showAll();
            // Trier les partenariats par ID décroissant (les plus récents en premier)
            partenariats.sort(Comparator.comparingInt(Partenariat::getId).reversed());
            
            cardsContainer.getChildren().clear();
            
            for (Partenariat partenariat : partenariats) {
                if (partenariat.getNom().toLowerCase().contains(recherche) ||
                    partenariat.getType().toLowerCase().contains(recherche) ||
                    partenariat.getDescription().toLowerCase().contains(recherche)) {
                    VBox card = creerCartePartenariat(partenariat);
                    cardsContainer.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la recherche", e.getMessage());
        }
    }

    private void modifierPartenariat(Partenariat partenariat) {
        try {
            if (partenariat == null) {
                afficherErreur("Erreur", "Le partenariat sélectionné est invalide");
                return;
            }
            
            System.out.println("Modification du partenariat: ID=" + partenariat.getId() + ", Nom=" + partenariat.getNom());
            
            // Utiliser l'interface en code Java pur sans utiliser de FXML
            ModifierPartenariatController controller = new ModifierPartenariatController(partenariat, aVoid -> {
                // Rafraîchir la liste après fermeture
                chargerPartenariats();
            });
            controller.afficher();
        } catch (Exception e) {
            System.err.println("Erreur détaillée lors de la création de l'interface: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de créer l'interface de modification: " + 
                          (e.getMessage() != null ? e.getMessage() : "Une erreur inattendue s'est produite"));
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 