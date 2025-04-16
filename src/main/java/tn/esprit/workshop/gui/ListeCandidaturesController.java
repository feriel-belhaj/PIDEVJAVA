package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServiceCandidature;
import tn.esprit.workshop.services.ServicePartenariat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.awt.Desktop;
import java.net.URL;

public class ListeCandidaturesController {

    @FXML
    private FlowPane candidaturesContainer;

    @FXML
    private Button btnStatistiques;

    private ServiceCandidature serviceCandidature;
    private ServicePartenariat servicePartenariat;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur ListeCandidaturesController");
        try {
            // Tester l'accès aux ressources
            testResourcesAccess();

            serviceCandidature = new ServiceCandidature();
            servicePartenariat = new ServicePartenariat();
            System.out.println("Services initialisés avec succès");

            chargerCandidatures();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des services: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testResourcesAccess() {
        String[] resources = {
                "/DetailsCandidature.fxml",
                "/ModifierCandidature.fxml",
                "/images/default.PNG",
                "/images/calendar-icon.png",
                "/images/cv-icon.png"
        };

        for (String resource : resources) {
            if (getClass().getResource(resource) != null) {
                System.out.println("✓ Ressource trouvée: " + resource);
            } else {
                System.err.println("❌ Ressource INTROUVABLE: " + resource);
            }
        }
    }

    private void chargerCandidatures() {
        try {
            List<Candidature> candidatures = serviceCandidature.showAll();
            candidaturesContainer.getChildren().clear();

            // Afficher message si aucune candidature n'est trouvée
            if (candidatures.isEmpty()) {
                Label emptyLabel = new Label("Aucune candidature trouvée dans la base de données");
                emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");
                emptyLabel.setPadding(new javafx.geometry.Insets(50));
                candidaturesContainer.getChildren().add(emptyLabel);
                System.out.println("Aucune candidature trouvée dans la base de données");
                return;
            }

            System.out.println("Nombre de candidatures récupérées: " + candidatures.size());

            for (Candidature candidature : candidatures) {
                System.out.println("Traitement de la candidature ID: " + candidature.getId());
                StackPane card = creerCarteCandidature(candidature);
                candidaturesContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du chargement des candidatures: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur lors du chargement des candidatures", e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception inattendue: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur inattendue", e.getMessage());
        }
    }

    private StackPane creerCarteCandidature(Candidature candidature) {
        // Création de la carte principale
        StackPane card = new StackPane();
        card.setPrefWidth(350.0);
        card.setPrefHeight(400.0);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-background-radius: 10;");

        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Section image avec icônes superposées
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(250.0);

        // Image du partenariat
        ImageView imageView = new ImageView();
        imageView.setFitWidth(350.0);
        imageView.setFitHeight(250.0);
        imageView.setPreserveRatio(true);

        // Chargement de l'image du partenariat
        try {
            Partenariat partenariat = candidature.getPartenariat();
            if (partenariat != null && partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                File imageFile = new File(partenariat.getImage());
                if (imageFile.exists()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
                }
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
            } catch (Exception ex) {
                System.err.println("Impossible de charger l'image par défaut: " + ex.getMessage());
            }
        }

        // Icônes superposées
        VBox iconsBox = new VBox(10);
        iconsBox.setAlignment(Pos.TOP_RIGHT);
        StackPane.setAlignment(iconsBox, Pos.TOP_RIGHT);
        StackPane.setMargin(iconsBox, new javafx.geometry.Insets(10, 10, 0, 0));

        // Icône calendrier
        Button calendarBtn = createIconButton("/images/calendar-icon.png", "Date de postulation");
        Tooltip calendarTooltip = new Tooltip(
                candidature.getDatePostulation() != null ?
                new SimpleDateFormat("dd/MM/yyyy").format(candidature.getDatePostulation()) :
                "Date non disponible"
        );
        Tooltip.install(calendarBtn, calendarTooltip);

        // Icône CV
        Button cvBtn = createIconButton("/images/cv-icon.png", "Voir CV");
        cvBtn.setOnAction(e -> {
            if (candidature.getCv() != null && !candidature.getCv().isEmpty()) {
                ouvrirFichier(candidature.getCv());
            } else {
                afficherErreur("CV non disponible", "Aucun CV n'est disponible pour cette candidature");
            }
        });

        iconsBox.getChildren().addAll(calendarBtn, cvBtn);

        imageContainer.getChildren().addAll(imageView, iconsBox);

        // Section infos et boutons
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new javafx.geometry.Insets(15));
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // Type (Stage, etc.)
        Label typeLabel = new Label(candidature.getTypeCollab() != null ? candidature.getTypeCollab() : "Type non spécifié");
        typeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        // Boutons d'action
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        Button btnDetails = new Button("Voir Détails");
        styleButton(btnDetails, "#f39c12");
        btnDetails.setOnAction(e -> ouvrirDetailsCandidature(candidature));

        Button btnModifier = new Button("Modifier");
        styleButton(btnModifier, "#2ecc71");
        btnModifier.setOnAction(e -> {
            try {
                if (candidature == null) {
                    afficherErreur("Erreur", "Candidature invalide");
                    return;
                }
                modifierAvecInterfaceAlternative(candidature);
            } catch (Exception ex) {
                System.err.println("Erreur lors du clic sur le bouton Modifier: " + ex.getMessage());
                ex.printStackTrace();
                afficherErreur("Erreur", "Impossible d'ouvrir l'interface de modification: " + ex.getMessage());
            }
        });

        buttonsBox.getChildren().addAll(btnDetails, btnModifier);

        infoBox.getChildren().addAll(typeLabel, buttonsBox);

        // Assemblage final
        contentBox.getChildren().addAll(imageContainer, infoBox);
        card.getChildren().add(contentBox);

        return card;
    }

    private Button createIconButton(String iconPath, String tooltipText) {
        Button button = new Button();
        button.setStyle("-fx-background-color: rgba(52, 73, 94, 0.7); -fx-background-radius: 25;");
        button.setPrefSize(40, 40);

        try {
            if (getClass().getResource(iconPath) == null) {
                System.err.println("❌ Icône introuvable: " + iconPath);
                // Créer un bouton de remplacement sans icône
                Label replacementLabel = new Label("?");
                replacementLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                button.setGraphic(replacementLabel);
            } else {
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
                icon.setFitHeight(20);
                icon.setFitWidth(20);
                icon.setPreserveRatio(true);
                button.setGraphic(icon);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'icône " + iconPath + ": " + e.getMessage());
            e.printStackTrace();
            // Créer un bouton de remplacement sans icône en cas d'erreur
            Label replacementLabel = new Label("!");
            replacementLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            button.setGraphic(replacementLabel);
        }

        button.setTooltip(new Tooltip(tooltipText));
        return button;
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
    }

    private void ouvrirFichier(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                afficherErreur("Fichier introuvable", "Le fichier n'existe pas: " + filePath);
            }
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le fichier: " + e.getMessage());
        }
    }


    private void ouvrirDetailsCandidature(Candidature candidature) {
        try {
            System.out.println("Ouverture des détails pour la candidature ID: " + candidature.getId());

            // Vérifier que le fichier FXML existe
            String fxmlPath = "/DetailsCandidature.fxml";
            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("ERREUR: Impossible de trouver le fichier " + fxmlPath);
                afficherErreur("Fichier manquant", "Le fichier FXML n'a pas été trouvé: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            System.out.println("FXMLLoader créé");

            try {
                Parent root = loader.load();
                System.out.println("FXML chargé avec succès");

                DetailsCandidatureController controller = loader.getController();
                if (controller == null) {
                    System.err.println("ERREUR: Le contrôleur est null");
                    afficherErreur("Erreur", "Impossible d'initialiser le contrôleur");
                    return;
                }

                System.out.println("Initialisation des données dans le contrôleur");
                controller.initData(candidature);

                Stage stage = new Stage();
                stage.setTitle("Détails de la Candidature");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                System.out.println("Affichage de la fenêtre de détails");
                stage.show();
            } catch (Exception e) {
                System.err.println("Exception lors du chargement ou de l'initialisation: " + e.getMessage());
                e.printStackTrace();
                afficherErreur("Erreur", "Impossible de charger la vue des détails: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Exception générale: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Une erreur s'est produite: " + e.getMessage());
        }
    }

    private void modifierAvecInterfaceAlternative(Candidature candidature) {
        try {
            // Vérification que la candidature n'est pas null
            if (candidature == null) {
                afficherErreur("Erreur", "La candidature sélectionnée est invalide");
                return;
            }
            
            System.out.println("Tentative de modification de la candidature ID: " + candidature.getId());
            System.out.println("Type: " + candidature.getTypeCollab());
            System.out.println("Date: " + (candidature.getDatePostulation() != null ? candidature.getDatePostulation() : "null"));
            System.out.println("CV: " + (candidature.getCv() != null ? candidature.getCv() : "non disponible"));
            System.out.println("Portfolio: " + (candidature.getPortfolio() != null ? candidature.getPortfolio() : "non disponible"));
            
            // Vérifier que le fichier FXML existe
            String fxmlPath = "/ModifierCandidature.fxml";
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("ERREUR: Impossible de trouver le fichier " + fxmlPath);
                afficherErreur("Fichier manquant", "Le fichier FXML n'a pas été trouvé: " + fxmlPath);
                return;
            }
            
            System.out.println("Ressource FXML trouvée: " + resource);
            
            try {
                FXMLLoader loader = new FXMLLoader(resource);
                Parent root = loader.load();
                System.out.println("FXML chargé avec succès");
                
                // Obtenir le contrôleur et initialiser les données
                ModifierCandidatureController controller = loader.getController();
                if (controller == null) {
                    System.err.println("ERREUR: Le contrôleur est null");
                    afficherErreur("Erreur", "Impossible d'initialiser le contrôleur");
                    return;
                }
                
                System.out.println("Contrôleur obtenu, initialisation des données...");
                controller.initData(candidature);
                System.out.println("Données initialisées dans le contrôleur");
                
                Stage stage = new Stage();
                stage.setTitle("Modifier une Candidature");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                
                // Rafraîchir la liste après la fermeture de la fenêtre
                stage.setOnHidden(e -> {
                    System.out.println("Fenêtre de modification fermée, rafraîchissement de la liste...");
                    chargerCandidatures();
                });
                
                System.out.println("Affichage de la fenêtre de modification...");
                stage.show();
            } catch (IOException ex) {
                System.err.println("Exception lors du chargement du FXML: " + ex.getMessage());
                ex.printStackTrace();
                afficherErreur("Erreur", "Impossible de charger l'interface: " + ex.getMessage());
            }
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

    @FXML
    void modifierCandidaturePartenariat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCandidature.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Créer une Candidature");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Rafraîchir la liste après la fermeture de la fenêtre
            stage.setOnHidden(e -> chargerCandidatures());
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre de création de candidature: " + e.getMessage());
        }
    }


    @FXML
    private void voirStatistiques() {
        // TODO: Implémenter la vue des statistiques
        afficherInfo("Information", "La fonctionnalité des statistiques n'est pas encore implémentée.");
    }

    @FXML
    private void rafraichirListe() {
        System.out.println("Rafraîchissement de la liste des candidatures");
        chargerCandidatures();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}