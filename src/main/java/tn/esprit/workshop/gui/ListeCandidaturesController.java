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
    private Button btnPrevious;

    @FXML
    private Button btnNext;

    @FXML
    private Label pageIndicator;

    private ServiceCandidature serviceCandidature;
    private ServicePartenariat servicePartenariat;
    private List<Candidature> allCandidatures;
    private int currentPage = 1;
    private final int itemsPerPage = 4;

    private final String CV_DESTINATION_PATH = "C:\\xampp\\htdocs\\img\\"; // Chemin du dossier où les CV sont stockés

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
                "/images/cv-icon.png",
                "/images/left-arrow.png",
                "/images/right-arrow.png"
        };

        for (String resource : resources) {
            if (getClass().getResource(resource) != null) {
                System.out.println("✓ Ressource trouvée: " + resource);
            } else {
                System.err.println("❌ Ressource INTROUVABLE: " + resource);
            }
        }
    }

    public void chargerCandidatures() {
        try {
            allCandidatures = serviceCandidature.showAll();
            displayPage(currentPage);
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

    private void displayPage(int page) {
        candidaturesContainer.getChildren().clear();

        // Afficher message si aucune candidature n'est trouvée
        if (allCandidatures == null || allCandidatures.isEmpty()) {
            Label emptyLabel = new Label("Aucune candidature trouvée dans la base de données");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");
            emptyLabel.setPadding(new javafx.geometry.Insets(50));
            candidaturesContainer.getChildren().add(emptyLabel);
            System.out.println("Aucune candidature trouvée dans la base de données");
            pageIndicator.setText("Page 0 / 0");
            btnPrevious.setDisable(true);
            btnNext.setDisable(true);
            return;
        }

        // Calculer les index de début et de fin pour la page actuelle
        int totalItems = allCandidatures.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        currentPage = Math.max(1, Math.min(page, totalPages)); // Assurer que currentPage est dans les limites

        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        System.out.println("Affichage de la page " + currentPage + " (éléments " + (startIndex + 1) + " à " + endIndex + " sur " + totalItems + ")");

        // Parcourir les candidatures pour la page actuelle en ordre inverse
        for (int i = endIndex - 1; i >= startIndex; i--) {
            Candidature candidature = allCandidatures.get(i);
            System.out.println("Traitement de la candidature ID: " + candidature.getId() + ", CV: " + candidature.getCv());
            StackPane card = creerCarteCandidature(candidature);
            candidaturesContainer.getChildren().add(card);
        }

        // Mettre à jour les contrôles de pagination
        pageIndicator.setText("Page " + currentPage + " / " + totalPages);
        btnPrevious.setDisable(currentPage == 1);
        btnNext.setDisable(currentPage == totalPages);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            displayPage(currentPage);
        }
    }

    @FXML
    private void nextPage() {
        int totalPages = (int) Math.ceil((double) allCandidatures.size() / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            displayPage(currentPage);
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
            String basePath = "C:\\xampp\\htdocs\\img\\";
            if (partenariat != null && partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                File imageFile = new File(basePath, partenariat.getImage());
                System.out.println("Tentative de chargement de l'image du partenariat: " + imageFile.getAbsolutePath());
                if (imageFile.exists() && imageFile.isFile() && imageFile.canRead()) {
                    Image image = new Image(imageFile.toURI().toString());
                    if (!image.isError()) {
                        imageView.setImage(image);
                        System.out.println("Image du partenariat chargée avec succès: " + imageFile.getAbsolutePath());
                    } else {
                        System.err.println("Erreur: Image corrompue ou invalide: " + imageFile.getAbsolutePath());
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
                    }
                } else {
                    System.err.println("Fichier image du partenariat introuvable ou non accessible: " + imageFile.getAbsolutePath());
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
                }
            } else {
                System.err.println("Image du partenariat null ou vide pour candidature ID: " + candidature.getId());
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.PNG")));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image du partenariat: " + e.getMessage());
            e.printStackTrace();
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
                System.out.println("Tentative d'ouverture du CV: " + candidature.getCv());
                ouvrirFichier(candidature.getCv());
            } else {
                System.err.println("CV non disponible pour candidature ID: " + candidature.getId());
                afficherErreur("CV non disponible", "Aucun CV n'est disponible pour cette candidature");
            }
        });

        iconsBox.getChildren().addAll(calendarBtn, cvBtn);

        imageContainer.getChildren().addAll(imageView, iconsBox);

        // Section infos et boutons
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new javafx.geometry.Insets(15));
        infoBox.setAlignment(Pos.CENTER); // Centrer verticalement et horizontalement

        // Type (Stage, etc.) - Encapsuler dans un HBox pour centrage
        Label typeLabel = new Label(candidature.getTypeCollab() != null ? candidature.getTypeCollab() : "Type non spécifié");
        typeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        // Conteneur pour centrer le typeLabel
        HBox typeContainer = new HBox();
        typeContainer.setAlignment(Pos.CENTER); // Centrer horizontalement
        typeContainer.getChildren().add(typeLabel);

        // Boutons d'action
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        Button btnDetails = new Button("Voir Détails");
        styleButton(btnDetails, "#f39c12");
        btnDetails.setOnAction(e -> ouvrirDetailsCandidature(candidature));

        buttonsBox.getChildren().add(btnDetails);

        infoBox.getChildren().addAll(typeContainer, buttonsBox);

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

    private void ouvrirFichier(String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                System.err.println("Nom du fichier CV invalide: " + fileName);
                afficherErreur("Erreur", "Le nom du fichier CV est invalide.");
                return;
            }

            File file = new File(CV_DESTINATION_PATH, fileName);
            System.out.println("Tentative d'ouverture du fichier: " + file.getAbsolutePath());

            if (file.exists() && file.isFile() && file.canRead()) {
                Desktop.getDesktop().open(file);
                System.out.println("Fichier CV ouvert avec succès: " + file.getAbsolutePath());
            } else {
                System.err.println("Fichier CV introuvable ou non accessible: " + file.getAbsolutePath());
                afficherErreur("Fichier introuvable", "Le fichier CV n'existe pas: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier CV: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir le fichier CV: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'ouverture du fichier CV: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }

    private void ouvrirDetailsCandidature(Candidature candidature) {
        try {
            System.out.println("Ouverture des détails pour la candidature ID: " + candidature.getId() + ", CV: " + candidature.getCv());

            String fxmlPath = "/DetailsCandidature.fxml";
            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("ERREUR: Impossible de trouver le fichier " + fxmlPath);
                afficherErreur("Fichier manquant", "Le fichier FXML n'a pas été trouvé: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            DetailsCandidatureController controller = loader.getController();
            if (controller == null) {
                System.err.println("ERREUR: Le contrôleur est null");
                afficherErreur("Erreur", "Impossible d'initialiser le contrôleur");
                return;
            }

            controller.initData(candidature, this);

            Stage stage = new Stage();
            stage.setTitle("Détails de la Candidature");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            System.err.println("Exception lors du chargement ou de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger la vue des détails: " + e.getMessage());
        }
    }

    private void modifierAvecInterfaceAlternative(Candidature candidature) {
        try {
            if (candidature == null) {
                afficherErreur("Erreur", "La candidature sélectionnée est invalide");
                return;
            }

            System.out.println("Tentative de modification de la candidature ID: " + candidature.getId());
            String fxmlPath = "/ModifierCandidature.fxml";
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("ERREUR: Impossible de trouver le fichier " + fxmlPath);
                afficherErreur("Fichier manquant", "Le fichier FXML n'a pas été trouvé: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            ModifierCandidatureController controller = loader.getController();
            if (controller == null) {
                System.err.println("ERREUR: Le contrôleur est null");
                afficherErreur("Erreur", "Impossible d'initialiser le contrôleur");
                return;
            }

            controller.initData(candidature);
            Stage stage = new Stage();
            stage.setTitle("Modifier une Candidature");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(e -> chargerCandidatures());
            stage.show();
        } catch (IOException ex) {
            System.err.println("Exception lors du chargement du FXML: " + ex.getMessage());
            ex.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger l'interface: " + ex.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'interface: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de créer l'interface de modification: " + e.getMessage());
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
            stage.setOnHidden(e -> chargerCandidatures());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre de création de candidature: " + e.getMessage());
        }
    }

    @FXML
    private void voirStatistiques() {
        afficherErreur("Information", "La fonctionnalité des statistiques n'est pas encore implémentée.");
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