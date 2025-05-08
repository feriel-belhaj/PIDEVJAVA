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
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Don;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.services.DonService;
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

public class DonsTableViewController implements Initializable {

    @FXML
    private VBox tableContainer;
    
    @FXML
    private TextField tfSearch;
    
    @FXML
    private ComboBox<String> cbEvenement;
    
    @FXML
    private ComboBox<String> cbTri;
    
    private DonService donService;
    private EvenementService evenementService;
    private ObservableList<Don> dons;
    private List<Don> allDons;
    
    private String currentSearchText = "";
    private String currentEvenement = "Tous";
    private String currentSortOption = "Par défaut";
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        donService = new DonService();
        evenementService = new EvenementService();
        
        // Initialiser les combobox
        setupComboBoxes();
        
        // Charger les dons
        loadDons();
    }
    
    private void setupComboBoxes() {
        // Options de tri
        ObservableList<String> triOptions = FXCollections.observableArrayList(
            "Par défaut",
            "Montant (croissant)",
            "Montant (décroissant)",
            "Date (plus récent)",
            "Date (plus ancien)",
            "ID Utilisateur (croissant)"
        );
        cbTri.setItems(triOptions);
        cbTri.setValue("Par défaut");
        
        // Les événements seront chargés après le chargement des dons
    }
    
    private void loadEvenements() {
        // Récupérer tous les événements
        List<Evenement> evenements = evenementService.getAll();
        
        // Extraire les titres des événements
        Set<String> evenementTitles = new HashSet<>();
        allDons.forEach(don -> {
            String titre = getEvenementTitre(don.getEvenement_id(), evenements);
            if (titre != null && !titre.isEmpty()) {
                evenementTitles.add(titre);
            }
        });
        
        // Créer la liste des événements pour le ComboBox
        List<String> evenementsList = new ArrayList<>(evenementTitles);
        java.util.Collections.sort(evenementsList);  // Trier par ordre alphabétique
        
        // Ajouter l'option "Tous" en premier
        ObservableList<String> evenementsOptions = FXCollections.observableArrayList();
        evenementsOptions.add("Tous");
        evenementsOptions.addAll(evenementsList);
        
        cbEvenement.setItems(evenementsOptions);
        cbEvenement.setValue("Tous");
    }
    
    private String getEvenementTitre(int evenementId, List<Evenement> evenements) {
        for (Evenement evenement : evenements) {
            if (evenement.getId() == evenementId) {
                return evenement.getTitre();
            }
        }
        return "Événement inconnu";
    }
    
    private void loadDons() {
        allDons = donService.getAll();
        dons = FXCollections.observableArrayList(allDons);
        
        // Charger les événements après avoir récupéré les dons
        loadEvenements();
        
        // Appliquer les filtres et tris
        applyFiltersAndSort();
    }
    
    private void applyFiltersAndSort() {
        List<Don> filteredList = allDons;
        List<Evenement> evenements = evenementService.getAll();
        
        // Appliquer le filtre de recherche
        if (!currentSearchText.isEmpty()) {
            filteredList = filteredList.stream()
                .filter(d -> {
                    String evenementTitre = getEvenementTitre(d.getEvenement_id(), evenements);
                    String userId = String.valueOf(d.getUser_id());
                    String message = d.getMessage() != null ? d.getMessage() : "";
                    
                    return evenementTitre.toLowerCase().contains(currentSearchText) ||
                           userId.contains(currentSearchText) ||
                           message.toLowerCase().contains(currentSearchText) ||
                           String.valueOf(d.getAmount()).contains(currentSearchText);
                })
                .collect(Collectors.toList());
        }
        
        // Appliquer le filtre d'événement
        if (!"Tous".equals(currentEvenement)) {
            filteredList = filteredList.stream()
                .filter(d -> {
                    String evenementTitre = getEvenementTitre(d.getEvenement_id(), evenements);
                    return currentEvenement.equals(evenementTitre);
                })
                .collect(Collectors.toList());
        }
        
        // Appliquer le tri
        switch (currentSortOption) {
            case "Montant (croissant)":
                filteredList.sort(Comparator.comparing(Don::getAmount));
                break;
            case "Montant (décroissant)":
                filteredList.sort(Comparator.comparing(Don::getAmount).reversed());
                break;
            case "Date (plus récent)":
                filteredList.sort(Comparator.comparing(Don::getDonationdate).reversed());
                break;
            case "Date (plus ancien)":
                filteredList.sort(Comparator.comparing(Don::getDonationdate));
                break;
            case "ID Utilisateur (croissant)":
                filteredList.sort(Comparator.comparing(Don::getUser_id));
                break;
            default:
                // Par défaut: tri par ID
                filteredList.sort(Comparator.comparing(Don::getId));
                break;
        }
        
        dons = FXCollections.observableArrayList(filteredList);
        displayDonsTable();
    }
    
    private void displayDonsTable() {
        // Vider le conteneur (sauf l'en-tête qui est le premier enfant)
        if (tableContainer.getChildren().size() > 1) {
            tableContainer.getChildren().remove(1, tableContainer.getChildren().size());
        }
        
        // Mettre à jour l'en-tête avec le style CSS
        if (!tableContainer.getChildren().isEmpty()) {
            HBox header = (HBox) tableContainer.getChildren().get(0);
            header.getStyleClass().add("table-header");
        }
        
        // Récupérer la liste des événements une fois
        List<Evenement> evenements = evenementService.getAll();
        
        // Afficher chaque don comme une ligne du tableau
        int index = 0;
        for (Don don : dons) {
            HBox row = createTableRow(don, evenements);
            
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
        if (dons.isEmpty()) {
            Label emptyLabel = new Label("Aucun don trouvé");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20px; -fx-font-style: italic;");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            emptyLabel.setAlignment(Pos.CENTER);
            tableContainer.getChildren().add(emptyLabel);
        }
    }
    
    @FXML
    private void handleEvenementFilter() {
        currentEvenement = cbEvenement.getValue();
        applyFiltersAndSort();
    }
    
    @FXML
    private void handleSort() {
        currentSortOption = cbTri.getValue();
        applyFiltersAndSort();
    }
    
    private HBox createTableRow(Don don, List<Evenement> evenements) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(5);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        
        // Colonne ID
        Label lblId = new Label(String.valueOf(don.getId()));
        lblId.setMinWidth(50);
        lblId.setMaxWidth(50);
        lblId.setPadding(new Insets(0, 10, 0, 5));
        
        // Colonne Événement
        String evenementTitre = getEvenementTitre(don.getEvenement_id(), evenements);
        Label lblEvenement = new Label(evenementTitre);
        lblEvenement.setMinWidth(180);
        lblEvenement.setMaxWidth(180);
        lblEvenement.setWrapText(true);
        
        // Colonne Donateur (utilisateur)
        Label lblDonateur = new Label("Utilisateur #" + don.getUser_id());
        lblDonateur.setMinWidth(120);
        lblDonateur.setMaxWidth(120);
        
        // Colonne Montant
        Label lblMontant = new Label(String.format("%.2f", don.getAmount()));
        lblMontant.setMinWidth(100);
        lblMontant.setMaxWidth(100);
        
        // Colonne Date
        String date = don.getDonationdate() != null ? 
                      don.getDonationdate().toLocalDate().format(formatter) : "N/A";
        Label lblDate = new Label(date);
        lblDate.setMinWidth(100);
        lblDate.setMaxWidth(100);
        
        // Colonne Message
        Label lblMessage = new Label(don.getMessage() != null ? don.getMessage() : "");
        lblMessage.setWrapText(true);
        lblMessage.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblMessage, Priority.ALWAYS);
        
        // Colonne Actions
        HBox actionsBox = new HBox();
        actionsBox.setSpacing(5);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setMinWidth(80);
        actionsBox.setMaxWidth(80);
        
        Button btnView = new Button("Voir");
        btnView.getStyleClass().add("btn-info");
        btnView.setPrefWidth(55);
        btnView.setOnAction(e -> handleViewDon(don));
        
        actionsBox.getChildren().add(btnView);
        
        // Ajouter toutes les colonnes à la ligne
        row.getChildren().addAll(lblId, lblEvenement, lblDonateur, lblMontant, 
                                  lblDate, lblMessage, actionsBox);
        
        return row;
    }
    
    @FXML
    private void handleSearch(KeyEvent event) {
        currentSearchText = tfSearch.getText().toLowerCase().trim();
        applyFiltersAndSort();
    }
    
    @FXML
    private void refreshTable() {
        // Réinitialiser les filtres
        currentSearchText = "";
        tfSearch.clear();
        cbEvenement.setValue("Tous");
        currentEvenement = "Tous";
        cbTri.setValue("Par défaut");
        currentSortOption = "Par défaut";
        
        // Recharger les dons
        loadDons();
    }
    
    private void handleViewDon(Don don) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DonDetail.fxml"));
            Parent root = loader.load();
            
            // Configurer le contrôleur
            DonDetailController controller = loader.getController();
            controller.setDon(don);
            
            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Détails du don #" + don.getId());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture des détails: " + e.getMessage());
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