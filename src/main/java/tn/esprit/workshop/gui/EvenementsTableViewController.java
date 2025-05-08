package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.services.EvenementService;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class EvenementsTableViewController implements Initializable {

    @FXML
    private VBox tableContainer;
    
    @FXML
    private TextField tfSearch;
    
    @FXML
    private ComboBox<String> cbCategorie;
    
    @FXML
    private ComboBox<String> cbTri;
    
    private EvenementService evenementService;
    private ObservableList<Evenement> evenements;
    private List<Evenement> allEvenements;
    
    private String currentSearchText = "";
    private String currentCategorie = "Toutes";
    private String currentSortOption = "Par défaut";
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evenementService = new EvenementService();
        
        // Mettre à jour les statuts avant d'afficher
        evenementService.updateAllEventStatus();
        
        // Initialiser les combobox
        setupComboBoxes();
        
        // Charger les événements
        loadEvenements();
    }
    
    private void setupComboBoxes() {
        // Options de tri
        ObservableList<String> triOptions = FXCollections.observableArrayList(
            "Par défaut",
            "Titre (A-Z)",
            "Titre (Z-A)",
            "Date de début (croissant)",
            "Date de début (décroissant)",
            "Objectif (croissant)",
            "Objectif (décroissant)",
            "Statut"
        );
        cbTri.setItems(triOptions);
        cbTri.setValue("Par défaut");
        
        // Les catégories seront chargées après le chargement des événements
    }
    
    private void loadCategories() {
        // Extraire toutes les catégories uniques des événements
        Set<String> categories = new HashSet<>();
        allEvenements.forEach(e -> {
            if (e.getCategorie() != null && !e.getCategorie().isEmpty()) {
                categories.add(e.getCategorie());
            }
        });
        
        // Créer la liste des catégories pour le ComboBox
        List<String> categoriesList = new ArrayList<>(categories);
        java.util.Collections.sort(categoriesList);  // Trier par ordre alphabétique
        
        // Ajouter l'option "Toutes" en premier
        ObservableList<String> categoriesOptions = FXCollections.observableArrayList();
        categoriesOptions.add("Toutes");
        categoriesOptions.addAll(categoriesList);
        
        cbCategorie.setItems(categoriesOptions);
        cbCategorie.setValue("Toutes");
    }
    
    private void loadEvenements() {
        allEvenements = evenementService.getAll();
        evenements = FXCollections.observableArrayList(allEvenements);
        
        // Charger les catégories après avoir récupéré les événements
        loadCategories();
        
        // Appliquer les filtres et tris
        applyFiltersAndSort();
    }
    
    private void applyFiltersAndSort() {
        List<Evenement> filteredList = allEvenements;
        
        // Appliquer le filtre de recherche
        if (!currentSearchText.isEmpty()) {
            filteredList = filteredList.stream()
                .filter(e -> 
                    e.getTitre().toLowerCase().contains(currentSearchText) ||
                    e.getDescription().toLowerCase().contains(currentSearchText) ||
                    e.getCategorie().toLowerCase().contains(currentSearchText) ||
                    String.valueOf(e.getId()).contains(currentSearchText)
                )
                .collect(Collectors.toList());
        }
        
        // Appliquer le filtre de catégorie
        if (!"Toutes".equals(currentCategorie)) {
            filteredList = filteredList.stream()
                .filter(e -> currentCategorie.equals(e.getCategorie()))
                .collect(Collectors.toList());
        }
        
        // Appliquer le tri
        switch (currentSortOption) {
            case "Titre (A-Z)":
                filteredList.sort(Comparator.comparing(Evenement::getTitre));
                break;
            case "Titre (Z-A)":
                filteredList.sort(Comparator.comparing(Evenement::getTitre).reversed());
                break;
            case "Date de début (croissant)":
                filteredList.sort(Comparator.comparing(Evenement::getStartdate));
                break;
            case "Date de début (décroissant)":
                filteredList.sort(Comparator.comparing(Evenement::getStartdate).reversed());
                break;
            case "Objectif (croissant)":
                filteredList.sort(Comparator.comparing(Evenement::getGoalamount));
                break;
            case "Objectif (décroissant)":
                filteredList.sort(Comparator.comparing(Evenement::getGoalamount).reversed());
                break;
            case "Statut":
                filteredList.sort(Comparator.comparing(Evenement::getStatus));
                break;
            default:
                // Par défaut: tri par ID
                filteredList.sort(Comparator.comparing(Evenement::getId));
                break;
        }
        
        evenements = FXCollections.observableArrayList(filteredList);
        displayEvenementsTable();
    }
    
    private void displayEvenementsTable() {
        // Vider le conteneur (sauf l'en-tête qui est le premier enfant)
        if (tableContainer.getChildren().size() > 1) {
            tableContainer.getChildren().remove(1, tableContainer.getChildren().size());
        }
        
        // Mettre à jour l'en-tête avec le style CSS
        if (!tableContainer.getChildren().isEmpty()) {
            HBox header = (HBox) tableContainer.getChildren().get(0);
            header.getStyleClass().add("table-header");
        }
        
        // Afficher chaque événement comme une ligne du tableau
        int index = 0;
        for (Evenement evenement : evenements) {
            HBox row = createTableRow(evenement);
            
            // Alterner les couleurs des lignes
            if (index % 2 == 0) {
                row.getStyleClass().add("table-row-even");
            } else {
                row.getStyleClass().add("table-row-odd");
            }
            row.getStyleClass().add("table-row");
            
            tableContainer.getChildren().add(row);
            index++;
        }
        
        // Afficher un message si aucun résultat
        if (evenements.isEmpty()) {
            Label emptyLabel = new Label("Aucun événement trouvé");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20px; -fx-font-style: italic;");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            emptyLabel.setAlignment(Pos.CENTER);
            tableContainer.getChildren().add(emptyLabel);
        }
    }
    
    @FXML
    private void handleCategorieFilter() {
        currentCategorie = cbCategorie.getValue();
        applyFiltersAndSort();
    }
    
    @FXML
    private void handleSort() {
        currentSortOption = cbTri.getValue();
        applyFiltersAndSort();
    }
    
    private HBox createTableRow(Evenement evenement) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(5);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        
        // Colonne ID
        Label lblId = new Label(String.valueOf(evenement.getId()));
        lblId.setMinWidth(50);
        lblId.setMaxWidth(50);
        lblId.setPadding(new Insets(0, 10, 0, 5));
        
        // Colonne Titre
        Label lblTitre = new Label(evenement.getTitre());
        lblTitre.setWrapText(true);
        lblTitre.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblTitre, Priority.ALWAYS);
        
        // Colonne Catégorie
        Label lblCategorie = new Label(evenement.getCategorie());
        lblCategorie.setMinWidth(120);
        lblCategorie.setMaxWidth(120);
        
        // Colonne Date début
        String dateDebut = evenement.getStartdate() != null ? 
                           evenement.getStartdate().toLocalDate().format(formatter) : "N/A";
        Label lblDateDebut = new Label(dateDebut);
        lblDateDebut.setMinWidth(100);
        lblDateDebut.setMaxWidth(100);
        
        // Colonne Date fin
        String dateFin = evenement.getEnddate() != null ? 
                         evenement.getEnddate().toLocalDate().format(formatter) : "N/A";
        Label lblDateFin = new Label(dateFin);
        lblDateFin.setMinWidth(100);
        lblDateFin.setMaxWidth(100);
        
        // Colonne Objectif
        Label lblObjectif = new Label(String.format("%.2f", evenement.getGoalamount()));
        lblObjectif.setMinWidth(100);
        lblObjectif.setMaxWidth(100);
        
        // Colonne Statut avec style
        Label lblStatut = new Label(evenement.getStatus());
        lblStatut.setMinWidth(80);
        lblStatut.setMaxWidth(80);
        
        // Appliquer différents styles selon le statut
        String statusStyle = "";
        if ("Actif".equals(evenement.getStatus())) {
            statusStyle = "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-background-color: #e8f8f5; -fx-padding: 3 8; -fx-background-radius: 3;";
        } else if ("Terminé".equals(evenement.getStatus())) {
            statusStyle = "-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-background-color: #fdedec; -fx-padding: 3 8; -fx-background-radius: 3;";
        } else if ("Inactif".equals(evenement.getStatus())) {
            statusStyle = "-fx-text-fill: #7f8c8d; -fx-background-color: #f2f3f4; -fx-padding: 3 8; -fx-background-radius: 3;";
        }
        lblStatut.setStyle(statusStyle);
        
        // Colonne Actions
        HBox actionsBox = new HBox();
        actionsBox.setSpacing(5);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setMinWidth(180);
        actionsBox.setMaxWidth(180);
        
        Button btnView = new Button("Voir");
        btnView.getStyleClass().add("btn-info");
        btnView.setPrefWidth(55);
        btnView.setOnAction(e -> handleViewEvenement(evenement));
        
        Button btnEdit = new Button("Modifier");
        btnEdit.getStyleClass().add("btn-warning");
        btnEdit.setPrefWidth(70);
        btnEdit.setOnAction(e -> handleEditEvenement(evenement));
        
        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("btn-danger");
        btnDelete.setPrefWidth(75);
        btnDelete.setOnAction(e -> handleDeleteEvenement(evenement));
        
        actionsBox.getChildren().addAll(btnView, btnEdit, btnDelete);
        
        // Ajouter toutes les colonnes à la ligne
        row.getChildren().addAll(lblId, lblTitre, lblCategorie, lblDateDebut, 
                                  lblDateFin, lblObjectif, lblStatut, actionsBox);
        
        return row;
    }
    
    @FXML
    private void handleSearch(KeyEvent event) {
        currentSearchText = tfSearch.getText().toLowerCase().trim();
        applyFiltersAndSort();
    }
    
    @FXML
    private void refreshTable() {
        // Mettre à jour les statuts
        evenementService.updateAllEventStatus();
        
        // Réinitialiser les filtres
        currentSearchText = "";
        tfSearch.clear();
        cbCategorie.setValue("Toutes");
        currentCategorie = "Toutes";
        cbTri.setValue("Par défaut");
        currentSortOption = "Par défaut";
        
        // Recharger les événements
        loadEvenements();
    }
    
    private void handleViewEvenement(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EvenementDetail.fxml"));
            Parent root = loader.load();
            
            // Configurer le contrôleur
            EvenementDetailController controller = loader.getController();
            controller.setEvenement(evenement);
            
            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Détails de l'événement: " + evenement.getTitre());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setWidth(900);
            stage.setHeight(600);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture des détails: " + e.getMessage());
        }
    }
    
    private void handleEditEvenement(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/workshop/resources/EvenementForm.fxml"));
            Parent root = loader.load();
            
            // Configurer le contrôleur
            EvenementController controller = loader.getController();
            // Passer l'événement à modifier au contrôleur
            controller.setEvenementToEdit(evenement);
            controller.setOnEventAddedCallback(this::refreshTable);
            
            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier l'événement: " + evenement.getTitre());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de modification: " + e.getMessage());
        }
    }
    
    private void handleDeleteEvenement(Evenement evenement) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer l'événement");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer l'événement '" + evenement.getTitre() + "' ?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Exécuter la suppression
            evenementService.supprimer(evenement.getId());
            
            // Recharger les données
            refreshTable();
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'événement a été supprimé avec succès!");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 