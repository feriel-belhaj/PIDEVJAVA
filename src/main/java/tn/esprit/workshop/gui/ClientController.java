package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tn.esprit.workshop.models.Formation;
import tn.esprit.workshop.services.FormationService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    @FXML private FlowPane formationsContainer;
    private FormationService formationService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formationService = new FormationService();
        loadFormations();
    }

    private void loadFormations() {
        try {
            formationsContainer.getChildren().clear();
            List<Formation> formations = formationService.getAll();
            
            for (Formation formation : formations) {
                VBox card = createFormationCard(formation);
                formationsContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des formations: " + e.getMessage());
        }
    }

    private VBox createFormationCard(Formation formation) {
        VBox card = new VBox(0);
        card.getStyleClass().add("formation-card");

        // Conteneur pour l'image
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefWidth(300);
        imageContainer.setPrefHeight(200);

        // Image de la formation
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        
        try {
            if (formation.getImage() != null && !formation.getImage().isEmpty()) {
                // Utiliser un chemin de fichier direct
                final String imagePath = formation.getImage();
                String fullPath = "src/main/resources/" + imagePath;
                File imageFile = new File(fullPath);
                
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true, true);
                    
                    // Ajouter des listeners pour gérer le chargement
                    image.errorProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            System.out.println("Erreur de chargement de l'image: " + fullPath);
                            showNoImageLabel(imageContainer);
                        }
                    });
                    
                    image.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0) {
                            imageView.setImage(image);
                            if (!imageContainer.getChildren().contains(imageView)) {
                                imageContainer.getChildren().clear();
                                imageContainer.getChildren().add(imageView);
                            }
                        }
                    });
                } else {
                    System.out.println("Image non trouvée: " + fullPath);
                    showNoImageLabel(imageContainer);
                }
            } else {
                showNoImageLabel(imageContainer);
            }
        } catch (Exception e) {
            System.out.println("Exception lors du chargement de l'image: " + e.getMessage());
            e.printStackTrace();
            showNoImageLabel(imageContainer);
        }

        // Titre de la formation
        Label titleLabel = new Label(formation.getTitre());
        titleLabel.getStyleClass().add("formation-title");
        titleLabel.setWrapText(true);

        // Description
        Label descriptionLabel = new Label(formation.getDescription());
        descriptionLabel.getStyleClass().add("formation-description");
        descriptionLabel.setWrapText(true);

        // Conteneur pour les informations
        VBox infoContainer = new VBox(5);
        infoContainer.getStyleClass().add("formation-info");

        // Informations de la formation
        Label dateLabel = new Label("Date de début: " + formation.getDateDeb());
        Label niveauLabel = new Label("Niveau: " + formation.getNiveau());
        Label prixLabel = new Label("Prix: " + formation.getPrix() + " DT");
        Label lieuLabel = new Label("Lieu: " + formation.getEmplacement());
        Label placesLabel = new Label("Places disponibles: " + (formation.getNbPlace() - formation.getNbParticipant()));
        Label dureeLabel = new Label("Durée: " + formation.getDuree());

        infoContainer.getChildren().addAll(
            dateLabel, niveauLabel, prixLabel, 
            lieuLabel, placesLabel, dureeLabel
        );

        // Bouton Voir plus
        Button detailsButton = new Button("Voir plus ➜");
        detailsButton.getStyleClass().add("reserver-btn");
        detailsButton.setOnAction(e -> showFormationDetails(formation));

        card.getChildren().addAll(
            imageContainer,
            titleLabel,
            descriptionLabel,
            infoContainer,
            detailsButton
        );

        return card;
    }

    private void showNoImageLabel(StackPane container) {
        Label noImageLabel = new Label("Pas d'image");
        noImageLabel.getStyleClass().add("no-image-label");
        container.getChildren().add(noImageLabel);
    }

    @FXML
    private void showFormationDetails(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FormationDetailsView.fxml"));
            Parent detailsView = loader.load();
            
            FormationDetailsController controller = loader.getController();
            controller.setFormation(formation);
            
            // Trouver le contentArea du MainMenu
            Node currentNode = formationsContainer.getScene().getRoot();
            while (currentNode != null && !(currentNode instanceof VBox)) {
                currentNode = currentNode.getParent();
            }
            
            if (currentNode != null) {
                VBox mainMenu = (VBox) currentNode;
                StackPane contentArea = (StackPane) mainMenu.getChildren().get(mainMenu.getChildren().size() - 1);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(detailsView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher les détails de la formation.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 