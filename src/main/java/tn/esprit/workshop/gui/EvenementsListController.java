package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.models.SessionManager;
import tn.esprit.workshop.models.User;
import tn.esprit.workshop.services.EvenementService;
import tn.esprit.workshop.services.ServiceUser;
import tn.esprit.workshop.services.UserGetData;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EvenementsListController implements Initializable {
    
    @FXML
    private FlowPane eventFlowPane;
    
    @FXML
    private ComboBox<String> cbCategorie;
    
    @FXML
    private ComboBox<String> cbTri;
    
    @FXML
    private TextField tfSearch;
    
    @FXML
    private Button btnAjouterEvenement;
    
    private EvenementService evenementService;
    private ServiceUser serviceUser;
    private ObservableList<Evenement> evenementList;
    private List<Evenement> allEvenements;
    
    // Format pour afficher les dates
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evenementService = new EvenementService();
        serviceUser = new ServiceUser();
        
        // Mettre à jour automatiquement les statuts des événements
        updateEventStatuses();
        
        // Initialiser les ComboBox
        cbCategorie.setItems(FXCollections.observableArrayList(
            "Tous", "Musique", "Théâtre", "Humour", "Danse", "Peinture & Arts Visuels", 
            "Cinéma & Audiovisuel", "Artisanat", "Littérature & Poésie", "Mode & Design"
        ));
        cbCategorie.setValue("Tous");
        
        cbTri.setItems(FXCollections.observableArrayList(
            "Plus récents", "Plus anciens", "Objectif (croissant)", "Objectif (décroissant)", "Progression"
        ));
        cbTri.setValue("Plus récents");
        
        // Vérifier si l'utilisateur connecté est un administrateur
        checkUserRole();
        
        // Charger les événements
        loadEvenements();
    }
    
    /**
     * Met à jour le statut des événements en fonction de la date actuelle
     */
    private void updateEventStatuses() {
        int updatedCount = evenementService.updateAllEventStatus();
        if (updatedCount > 0) {
            System.out.println(updatedCount + " événement(s) ont eu leur statut mis à jour automatiquement.");
        }
    }
    
    /**
     * Vérifie si l'utilisateur connecté est un administrateur et affiche/masque le bouton en conséquence
     */
    private void checkUserRole() {
        try {
            // Récupérer l'ID de l'utilisateur connecté
            int userId = SessionManager.getUserId();
            
            if (userId > 0) {
                // Récupérer l'utilisateur actuel depuis la base de données
                User currentUser = null;
                for (User user : serviceUser.showAll()) {
                    if (user.getId() == userId) {
                        currentUser = user;
                        break;
                    }
                }
                
                if (currentUser != null) {
                    String role = currentUser.getRole();
                    System.out.println("Utilisateur connecté: " + currentUser.getNom() + " " + currentUser.getPrenom() + " avec le rôle: " + role);
                    
                    // Afficher le bouton si l'utilisateur a un rôle qui contient "admin" (non sensible à la casse)
                    if (role != null && isAdminRole(role)) {
                        System.out.println("Rôle administrateur détecté. Affichage du bouton 'Ajouter un événement'");
                        btnAjouterEvenement.setVisible(true);
                        btnAjouterEvenement.setManaged(true);
                    } else {
                        // Cacher le bouton pour les utilisateurs non-admin
                        System.out.println("Rôle non-administrateur. Masquage du bouton 'Ajouter un événement'");
                        btnAjouterEvenement.setVisible(false);
                        btnAjouterEvenement.setManaged(false);
                    }
                } else {
                    // Utilisateur non trouvé, cacher le bouton
                    System.out.println("Utilisateur avec ID " + userId + " non trouvé dans la base de données");
                    btnAjouterEvenement.setVisible(false);
                    btnAjouterEvenement.setManaged(false);
                }
            } else {
                // Aucun utilisateur connecté, cacher le bouton
                System.out.println("Aucun utilisateur connecté (ID <= 0)");
                btnAjouterEvenement.setVisible(false);
                btnAjouterEvenement.setManaged(false);
            }
        } catch (Exception e) {
            // En cas d'erreur, cacher le bouton par sécurité
            System.err.println("Erreur lors de la vérification du rôle de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            btnAjouterEvenement.setVisible(false);
            btnAjouterEvenement.setManaged(false);
        }
    }
    
    /**
     * Vérifie si un rôle correspond à un rôle administrateur
     * Accepte différents formats comme "admin", "ADMIN", "ROLE_ADMIN", etc.
     * 
     * @param role Le rôle à vérifier
     * @return true si c'est un rôle administrateur, false sinon
     */
    private boolean isAdminRole(String role) {
        if (role == null) return false;
        
        // Convertir en minuscules pour une comparaison non sensible à la casse
        String roleLowerCase = role.toLowerCase();
        
        // Vérifier les différentes possibilités
        return roleLowerCase.equals("admin") ||
               roleLowerCase.equals("role_admin") ||
               roleLowerCase.contains("admin");
    }
    
    @FXML
    private void handleFilterByCategorie(ActionEvent event) {
        filterAndSortEvents();
    }
    
    @FXML
    private void handleSortBy(ActionEvent event) {
        filterAndSortEvents();
    }
    
    @FXML
    private void handleSearch(KeyEvent event) {
        filterAndSortEvents();
    }
    
    @FXML
    private void handleAjouterEvenement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/workshop/resources/EvenementForm.fxml"));
            Parent root = loader.load();
            
            // Accéder au conteneur parent (StackPane)
            StackPane container = (StackPane) eventFlowPane.getScene().lookup("#evenementsContainer");
            if (container != null) {
                container.getChildren().clear();
                container.getChildren().add(root);
                
                // Get the controller to set a callback when an event is added
                EvenementController controller = loader.getController();
                controller.setOnEventAddedCallback(() -> {
                    try {
                        // Reload the events list view
                        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/fxml/EvenementsList.fxml"));
                        Parent listRoot = listLoader.load();
                        container.getChildren().clear();
                        container.getChildren().add(listRoot);
                        
                        // Initialize the new controller
                        EvenementsListController newController = listLoader.getController();
                        // Reload events if necessary
                        newController.loadEvenements();
                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du rechargement de la liste: " + e.getMessage());
                    }
                });
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Conteneur parent non trouvé");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }
    
    public void loadEvenements() {
        // Mettre à jour les statuts à chaque chargement de la liste
        updateEventStatuses();
        
        allEvenements = evenementService.getAll();
        filterAndSortEvents();
    }
    
    private void filterAndSortEvents() {
        String categorie = cbCategorie.getValue();
        String tri = cbTri.getValue();
        String searchText = tfSearch.getText().toLowerCase().trim();
        
        // Filtrer par catégorie
        List<Evenement> filteredEvents = allEvenements;
        
        if (!"Tous".equals(categorie)) {
            filteredEvents = filteredEvents.stream()
                    .filter(e -> categorie.equals(e.getCategorie()))
                    .collect(Collectors.toList());
        }
        
        // Filtrer par texte recherché
        if (!searchText.isEmpty()) {
            filteredEvents = filteredEvents.stream()
                    .filter(e -> e.getTitre().toLowerCase().contains(searchText) || 
                                e.getDescription().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
        }
        
        // Trier les événements
        switch (tri) {
            case "Plus récents":
                filteredEvents.sort(Comparator.comparing(Evenement::getCreatedat).reversed());
                break;
            case "Plus anciens":
                filteredEvents.sort(Comparator.comparing(Evenement::getCreatedat));
                break;
            case "Objectif (croissant)":
                filteredEvents.sort(Comparator.comparing(Evenement::getGoalamount));
                break;
            case "Objectif (décroissant)":
                filteredEvents.sort(Comparator.comparing(Evenement::getGoalamount).reversed());
                break;
            case "Progression":
                filteredEvents.sort((e1, e2) -> {
                    double prog1 = e1.getGoalamount() > 0 ? e1.getCollectedamount() / e1.getGoalamount() : 0;
                    double prog2 = e2.getGoalamount() > 0 ? e2.getCollectedamount() / e2.getGoalamount() : 0;
                    return Double.compare(prog2, prog1); // Du plus élevé au plus bas
                });
                break;
        }
        
        evenementList = FXCollections.observableArrayList(filteredEvents);
        displayEventsAsCards();
    }
    
    private void displayEventsAsCards() {
        // Effacer les éléments existants
        eventFlowPane.getChildren().clear();
        
        // Pour chaque événement, créer une carte
        for (Evenement event : evenementList) {
            VBox eventCard = createEventCard(event);
            eventFlowPane.getChildren().add(eventCard);
        }
    }
    
    private VBox createEventCard(Evenement event) {
        VBox card = new VBox();
        card.getStyleClass().add("event-card");
        card.setPrefWidth(280);
        card.setPrefHeight(380);
        card.setSpacing(0);
        
        // Make the entire card clickable
        card.setOnMouseClicked(e -> openEventDetail(event));
        card.setCursor(Cursor.HAND);
        
        // Image container
        VBox imageContainer = new VBox();
        imageContainer.setPrefHeight(180);
        imageContainer.getStyleClass().add("event-image-container");
        
        // Image de l'événement
        ImageView imageView = new ImageView();
        imageView.setFitWidth(280);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        
        // Vérifier si l'événement a une image et essayer de la charger
        if (event.getImageurl() != null && !event.getImageurl().isEmpty()) {
            try {
                // Essayer de charger l'image depuis le chemin stocké
                Image eventImage = new Image(getClass().getResourceAsStream(event.getImageurl()));
                if (eventImage != null && !eventImage.isError()) {
                    imageView.setImage(eventImage);
                    imageContainer.getChildren().add(imageView);
                } else {
                    // Si l'image ne peut pas être chargée, afficher un message
                    addPlaceholderLabel(imageContainer);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image pour l'événement " + event.getId() + ": " + e.getMessage());
                // En cas d'erreur, afficher un message
                addPlaceholderLabel(imageContainer);
            }
        } else {
            // Si aucune image n'est spécifiée, utiliser un placeholder
            addPlaceholderLabel(imageContainer);
        }
        
        card.getChildren().add(imageContainer);
        
        // Content container
        VBox contentBox = new VBox();
        contentBox.setSpacing(5);
        contentBox.setPadding(new Insets(15, 15, 15, 15));
        
        // Titre avec style élégant
        Label titleLabel = new Label(event.getTitre());
        titleLabel.getStyleClass().add("event-title");
        titleLabel.setWrapText(true);
        contentBox.getChildren().add(titleLabel);
        
        // Description (courte)
        String shortDesc = event.getDescription();
        if (shortDesc.length() > 80) {
            shortDesc = shortDesc.substring(0, 77) + "...";
        }
        Label descLabel = new Label(shortDesc);
        descLabel.getStyleClass().add("event-description");
        descLabel.setWrapText(true);
        contentBox.getChildren().add(descLabel);
        
        card.getChildren().add(contentBox);
        
        // Status badge at top-left corner
        Label statusLabel = new Label(event.getStatus());
        statusLabel.getStyleClass().addAll("event-status", 
            "Actif".equals(event.getStatus()) ? "status-active" : "status-finished");
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(10);
        
        // Bottom section
        VBox bottomBox = new VBox();
        bottomBox.setSpacing(8);
        bottomBox.setPadding(new Insets(0, 15, 15, 15));
        
        // Barre de progression très fine
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(250);
        progressBar.setPrefHeight(4); // Make it very thin like in the image
        progressBar.getStyleClass().add("funding-progress");
        
        // Calculer le pourcentage de financement
        double percentage = 0;
        if (event.getGoalamount() > 0) {
            percentage = (event.getCollectedamount() / event.getGoalamount());
        }
        progressBar.setProgress(percentage);
        
        // Funding info row
        Label fundingAmountLabel = new Label(String.format("%.0f €", event.getCollectedamount()));
        fundingAmountLabel.getStyleClass().add("funding-amount");
        
        Label fundingGoalLabel = new Label(String.format("collectés sur %.0f €", event.getGoalamount()));
        fundingGoalLabel.getStyleClass().add("funding-goal");
        
        // Container for the collected amount and goal amount
        VBox fundingInfoLeft = new VBox();
        fundingInfoLeft.getChildren().addAll(fundingAmountLabel);
        
        // Days counter
        VBox daysInfoRight = new VBox();
        daysInfoRight.setAlignment(Pos.CENTER_RIGHT);
        
        Label daysLabel;
        if ("Actif".equals(event.getStatus()) && event.getEnddate() != null) {
            long daysRemaining = java.time.LocalDate.now().until(event.getEnddate().toLocalDate()).getDays();
            daysLabel = new Label(daysRemaining + " jours restants");
            daysLabel.getStyleClass().add("days-remaining");
        } else {
            daysLabel = new Label("0 jours restants");
            daysLabel.getStyleClass().add("days-finished");
        }
        
        daysInfoRight.getChildren().add(daysLabel);
        
        // Creator info
        String creatorName = getCreatorName(event.getCreateur_id());
        
        Label creatorLabel = new Label("Par " + creatorName);
        creatorLabel.getStyleClass().add("creator-info");
        
        Label locationLabel = new Label(event.getLocalisation());
        locationLabel.getStyleClass().add("location-info");
        
        // Bottom funding section layout
        HBox fundingRow = new HBox();
        fundingRow.setPrefWidth(250);
        fundingRow.setSpacing(5);
        
        HBox.setHgrow(fundingInfoLeft, Priority.ALWAYS);
        HBox.setHgrow(daysInfoRight, Priority.ALWAYS);
        
        fundingRow.getChildren().addAll(fundingInfoLeft, daysInfoRight);
        
        // Action buttons row
        HBox buttonRow = new HBox();
        buttonRow.setSpacing(5);
        buttonRow.setPadding(new Insets(10, 0, 0, 0));
        
        if ("Actif".equals(event.getStatus())) {
            Button donButton = new Button("Faire un don");
            donButton.getStyleClass().add("make-donation-btn");
            donButton.setGraphic(createHeartIcon());
            donButton.setContentDisplay(ContentDisplay.LEFT);
            donButton.setPrefWidth(160);
            // Set up the button to consume click events so they don't propagate to the card
            donButton.setOnAction(e -> {
                e.consume(); // Consume the event to prevent it from propagating
                openDonForm(event);
            });
            buttonRow.getChildren().add(donButton);
        } else {
            Label donsLabel = new Label("Dons fermés");
            donsLabel.getStyleClass().addAll("event-status", "status-finished");
            donsLabel.setPrefWidth(160);
            donsLabel.setAlignment(Pos.CENTER);
            buttonRow.getChildren().add(donsLabel);
        }
        
        // Add all elements to the bottom section
        bottomBox.getChildren().addAll(progressBar, fundingRow, creatorLabel, locationLabel, buttonRow);
        card.getChildren().add(bottomBox);
        
        // Set up the hover effect (card is already clickable from above)
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-translate-y: -3px;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-translate-y: -3px;", "")));
        
        return card;
    }
    
    private void addPlaceholderLabel(VBox container) {
        Label placeholder = new Label("Aucune image");
        placeholder.setFont(Font.font("System", 14));
        placeholder.setTextFill(Color.GRAY);
        placeholder.setPrefWidth(280);
        placeholder.setPrefHeight(180);
        placeholder.setStyle("-fx-background-color: #f5f5f5; -fx-alignment: center;");
        container.getChildren().add(placeholder);
    }
    
    // Helper method to create a heart icon for the donation button
    private javafx.scene.Node createHeartIcon() {
        // Create a heart shape using a Unicode character
        Label heartIcon = new Label("❤");
        heartIcon.setTextFill(Color.WHITE);
        heartIcon.setStyle("-fx-font-size: 14px;");
        return heartIcon;
    }
    
    private void openEventDetail(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EvenementDetail.fxml"));
            Parent root = loader.load();
            
            // Configurer le contrôleur
            EvenementDetailController controller = loader.getController();
            controller.setEvenement(event);
            
            // Accéder au conteneur parent (StackPane)
            StackPane container = (StackPane) eventFlowPane.getScene().lookup("#evenementsContainer");
            if (container != null) {
                container.getChildren().clear();
                container.getChildren().add(root);
            } else {
                // Fallback si on ne trouve pas le conteneur principal
                Scene scene = new Scene(root);
                Stage stage = (Stage) eventFlowPane.getScene().getWindow();
                stage.setScene(scene);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue détaillée: " + e.getMessage());
        }
    }
    
    /**
     * Ouvre le formulaire de don pour un événement donné
     */
    private void openDonForm(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DonForm.fxml"));
            Parent root = loader.load();
            
            // Passer l'événement au contrôleur du formulaire
            DonFormController controller = loader.getController();
            controller.setEvenement(event);
            controller.setReturnEventId(event.getId());
            
            // Accéder au conteneur parent (StackPane)
            StackPane container = (StackPane) eventFlowPane.getScene().lookup("#evenementsContainer");
            if (container != null) {
                container.getChildren().clear();
                container.getChildren().add(root);
            } else {
                // Fallback si on ne trouve pas le conteneur principal
                Scene scene = new Scene(root);
                Stage stage = (Stage) eventFlowPane.getScene().getWindow();
                stage.setScene(scene);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de don: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Récupère le nom complet du créateur à partir de son ID
     * @param creatorId L'ID du créateur
     * @return Le nom complet du créateur, ou "Utilisateur inconnu" si non trouvé
     */
    private String getCreatorName(int creatorId) {
        try {
            // Rechercher l'utilisateur correspondant dans la liste des utilisateurs
            for (User user : serviceUser.showAll()) {
                if (user.getId() == creatorId) {
                    return user.getNom() + " " + user.getPrenom();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du créateur: " + e.getMessage());
        }
        return "Utilisateur inconnu";
    }
} 