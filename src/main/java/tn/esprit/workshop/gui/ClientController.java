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
import tn.esprit.workshop.entities.Formation;
import tn.esprit.workshop.services.FormationService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    @FXML private FlowPane formationsContainer;
    @FXML private StackPane contentArea;
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
            
            System.out.println("\n=== Formations chargées depuis la base de données ===");
            for (Formation formation : formations) {
                System.out.println("Formation: " + formation.getTitre());
                System.out.println("Image path: " + formation.getImage());
                System.out.println("------------------------");
                
                VBox card = createFormationCard(formation);
                formationsContainer.getChildren().add(card);
            }
            System.out.println("=== Fin du chargement des formations ===\n");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des formations: " + e.getMessage());
        }
    }

    private VBox createFormationCard(Formation formation) {
        VBox card = new VBox(10);
        card.getStyleClass().add("formation-card");
        card.setPrefWidth(250);
        card.setPrefHeight(350);

        // Conteneur pour l'image
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefWidth(250);
        imageContainer.setPrefHeight(150);
        imageContainer.setStyle("-fx-background-color: #e0e0e0;");

        // Image de la formation
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        try {
            System.out.println("=== Début du chargement de l'image pour la formation: " + formation.getTitre() + " ===");
            System.out.println("Chemin de l'image dans la base de données: " + formation.getImage());
            
            if (formation.getImage() != null && !formation.getImage().isEmpty()) {
                // Essayer depuis les ressources
                URL resourceUrl = getClass().getResource("/" + formation.getImage());
                System.out.println("Tentative de chargement depuis les ressources: " + resourceUrl);
                
                if (resourceUrl != null) {
                    System.out.println("Image trouvée dans les ressources!");
                    Image image = new Image(resourceUrl.toExternalForm());
                    imageView.setImage(image);
                    
                    // Ajouter un listener pour vérifier si l'image est chargée correctement
                    image.errorProperty().addListener((obs, oldValue, newValue) -> {
                        if (newValue) {
                            System.err.println("Erreur lors du chargement de l'image: " + image.getException());
                        }
                    });
                    
                    image.progressProperty().addListener((obs, oldValue, newValue) -> {
                        System.out.println("Progression du chargement: " + (newValue.doubleValue() * 100) + "%");
                    });
                    
                    imageContainer.getChildren().add(imageView);
                } else {
                    System.out.println("Image non trouvée dans les ressources, tentative depuis le système de fichiers...");
                    // Essayer depuis le système de fichiers
                    File resourceFile = new File("src/main/resources/" + formation.getImage());
                    System.out.println("Chemin complet du fichier: " + resourceFile.getAbsolutePath());
                    System.out.println("Le fichier existe? " + resourceFile.exists());
                    
                    if (resourceFile.exists()) {
                        System.out.println("Image trouvée dans le système de fichiers!");
                        Image image = new Image(resourceFile.toURI().toString());
                        imageView.setImage(image);
                        imageContainer.getChildren().add(imageView);
                    } else {
                        System.out.println("Image non trouvée dans le système de fichiers non plus.");
                        showNoImageLabel(imageContainer);
                    }
                }
            } else {
                System.out.println("Aucun chemin d'image spécifié pour cette formation.");
                showNoImageLabel(imageContainer);
            }
            System.out.println("=== Fin du chargement de l'image ===\n");
        } catch (Exception e) {
            System.err.println("Exception lors du chargement de l'image:");
            e.printStackTrace();
            showNoImageLabel(imageContainer);
        }

        // Titre de la formation
        Label titleLabel = new Label(formation.getTitre());
        titleLabel.getStyleClass().add("formation-title");
        titleLabel.setWrapText(true);

        // Description courte
        Label descriptionLabel = new Label(formation.getDescription());
        descriptionLabel.getStyleClass().add("formation-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxHeight(60);

        // Niveau
        Label levelLabel = new Label("Niveau: " + formation.getNiveau());
        levelLabel.getStyleClass().add("formation-level");

        // Prix
        Label priceLabel = new Label("Prix: " + formation.getPrix() + " DT");
        priceLabel.getStyleClass().add("formation-price");

        // Bouton "Voir plus"
        Button detailsButton = new Button("Voir plus");
        detailsButton.getStyleClass().add("details-button");
        detailsButton.setOnAction(e -> showFormationDetails(formation));

        card.getChildren().addAll(imageContainer, titleLabel, descriptionLabel, levelLabel, priceLabel, detailsButton);
        return card;
    }

    private void showNoImageLabel(StackPane container) {
        container.getChildren().clear();
        Label noImageLabel = new Label("Pas d'image");
        noImageLabel.setStyle("-fx-text-fill: #666666;");
        container.getChildren().add(noImageLabel);
        
        // Ajouter plus de détails dans les logs
        System.out.println("INFO: Affichage du label 'Pas d'image' pour le conteneur: " + container);
        System.out.println("INFO: État du conteneur - Largeur: " + container.getWidth() + 
                         ", Hauteur: " + container.getHeight() +
                         ", Nombre d'enfants: " + container.getChildren().size());
    }

    @FXML
    private void showFormationDetails(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FormationDetailsView.fxml"));
            Parent detailsView = loader.load();
            
            FormationDetailsController controller = loader.getController();
            controller.setFormation(formation);
            
            // Sauvegarder la vue actuelle
            Node currentContent = contentArea.getChildren().get(0);
            if (currentContent instanceof Parent) {
                controller.setPreviousView((Parent) currentContent);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(detailsView);
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