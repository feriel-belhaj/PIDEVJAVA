package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.workshop.entities.Creation;
import tn.esprit.workshop.services.CreationService;
import tn.esprit.workshop.services.UtilisateurService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreationController implements Initializable {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categorieFilter;
    @FXML private TableView<Creation> creationsTable;
    @FXML private TableColumn<Creation, Integer> idColumn;
    @FXML private TableColumn<Creation, String> titreColumn;
    @FXML private TableColumn<Creation, String> categorieColumn;
    @FXML private TableColumn<Creation, LocalDateTime> dateColumn;
    @FXML private TableColumn<Creation, String> statutColumn;
    @FXML private TableColumn<Creation, Integer> likesColumn;
    
    @FXML private ImageView creationImage;
    @FXML private Label creationTitle;
    @FXML private Label creationDate;
    @FXML private Label categorieLabel;
    @FXML private Label createurLabel;
    @FXML private Label statutLabel;
    @FXML private Label likesLabel;
    @FXML private TextArea descriptionArea;
    @FXML private VBox creationDetailsPane;
    
    private CreationService creationService;
    private UtilisateurService utilisateurService;
    private Creation selectedCreation;
    private static final String UPLOAD_DIR = "src/main/resources/uploads/images";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        creationService = new CreationService();
        utilisateurService = new UtilisateurService();
        
        // Initialize categories ComboBox
        categorieFilter.getItems().addAll("Tous", "Painting", "Digital Art", "Sculpture", "Photography", "Other");
        categorieFilter.setValue("Tous");
        
        // Configure TableView columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("datePublic"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        likesColumn.setCellValueFactory(new PropertyValueFactory<>("nbLike"));
        
        // Format the date column
        dateColumn.setCellFactory(column -> new TableCell<Creation, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(item));
                }
            }
        });
        
        // Handle selection changes
        creationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCreation = newSelection;
                displayCreationDetails(newSelection);
            }
        });
        
        // Create upload directory if it doesn't exist
        createUploadDirectory();
        
        // Load data initially
        refreshTable();
    }
    
    private void createUploadDirectory() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du dossier d'upload: " + e.getMessage());
        }
    }
    
    private void displayCreationDetails(Creation creation) {
        if (creation == null) return;
        
        // Display the image
        try {
            if (creation.getImage() != null && !creation.getImage().isEmpty()) {
                URL imageUrl = getClass().getResource("/" + creation.getImage());
                if (imageUrl != null) {
                    creationImage.setImage(new Image(imageUrl.toExternalForm()));
                } else {
                    File file = new File("src/main/resources/" + creation.getImage());
                    if (file.exists()) {
                        creationImage.setImage(new Image(file.toURI().toString()));
                    } else {
                        creationImage.setImage(new Image(getClass().getResource("/images/dot-pattern.png").toExternalForm()));
                    }
                }
            } else {
                creationImage.setImage(new Image(getClass().getResource("/images/dot-pattern.png").toExternalForm()));
            }
        } catch (Exception e) {
            creationImage.setImage(new Image(getClass().getResource("/images/dot-pattern.png").toExternalForm()));
        }
        
        // Display other creation details
        creationTitle.setText(creation.getTitre());
        creationDate.setText("Publié le " + dateFormatter.format(creation.getDatePublic()));
        categorieLabel.setText(creation.getCategorie());
        
        // Get creator information
        try {
            String creatorName = utilisateurService.getUsernameById(creation.getUtilisateurId());
            createurLabel.setText(creatorName);
        } catch (Exception e) {
            createurLabel.setText("Utilisateur #" + creation.getUtilisateurId());
        }
        
        statutLabel.setText(creation.getStatut());
        likesLabel.setText(String.valueOf(creation.getNbLike()));
        descriptionArea.setText(creation.getDescription());
    }

    @FXML
    private void handleSearchAction() {
        String searchText = searchField.getText().trim().toLowerCase();
        String category = categorieFilter.getValue();
        
        try {
            List<Creation> allCreations = creationService.getAll();
            creationsTable.getItems().clear();
            
            for (Creation creation : allCreations) {
                boolean matchesSearch = searchText.isEmpty() || 
                    creation.getTitre().toLowerCase().contains(searchText) || 
                    creation.getDescription().toLowerCase().contains(searchText);
                
                boolean matchesCategory = "Tous".equals(category) || 
                    (creation.getCategorie() != null && creation.getCategorie().equals(category));
                
                if (matchesSearch && matchesCategory) {
                    creationsTable.getItems().add(creation);
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
        }
    }

    @FXML
    private void showAddCreationForm() {
        try {
            // Create form fields
            TextField txtTitre = new TextField();
            TextArea txtDescription = new TextArea();
            ComboBox<String> comboCategorie = new ComboBox<>();
            comboCategorie.getItems().addAll("Painting", "Digital Art", "Sculpture", "Photography", "Other");
            TextField txtImage = new TextField();
            Button btnImage = new Button("Choisir une image");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            grid.add(new Label("Titre:"), 0, 0);
            grid.add(txtTitre, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(txtDescription, 1, 1);
            grid.add(new Label("Catégorie:"), 0, 2);
            grid.add(comboCategorie, 1, 2);
            
            HBox imageBox = new HBox(10);
            imageBox.getChildren().addAll(txtImage, btnImage);
            grid.add(new Label("Image:"), 0, 3);
            grid.add(imageBox, 1, 3);
            
            btnImage.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir une image");
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );
                
                File selectedFile = fileChooser.showOpenDialog(txtImage.getScene().getWindow());
                if (selectedFile != null) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                        Path targetPath = Paths.get(UPLOAD_DIR, fileName);
                        
                        Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                        txtImage.setText("uploads/images/" + fileName);
                    } catch (IOException ex) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image: " + ex.getMessage());
                    }
                }
            });
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Nouvelle Création");
            dialog.setHeaderText("Ajouter une nouvelle création");
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (txtTitre.getText().isEmpty() || comboCategorie.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Attention", "Le titre et la catégorie sont obligatoires.");
                    return;
                }
                
                Creation newCreation = new Creation();
                newCreation.setUtilisateurId(utilisateurService.getCurrentUser().getId());
                newCreation.setTitre(txtTitre.getText());
                newCreation.setDescription(txtDescription.getText());
                newCreation.setCategorie(comboCategorie.getValue());
                newCreation.setImage(txtImage.getText());
                newCreation.setDatePublic(LocalDateTime.now());
                newCreation.setStatut("actif");
                newCreation.setNbLike(0);
                
                try {
                    // Validate the creation first and show any errors
                    List<String> validationErrors = newCreation.validate();
                    if (!validationErrors.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Erreur de validation", 
                                 "La création n'a pas pu être ajoutée pour les raisons suivantes:\n\n" + 
                                 String.join("\n", validationErrors));
                        return;
                    }
                    
                    // Try to add the creation to the database
                    boolean added = creationService.add(newCreation);
                    if (added) {
                        refreshTable();
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "La création a été ajoutée avec succès.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", 
                                 "L'ajout de la création a échoué. Vérifiez les journaux pour plus de détails.");
                    }
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditAction() {
        if (selectedCreation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une création à modifier.");
            return;
        }
        
        try {
            // Create form fields
            TextField txtTitre = new TextField(selectedCreation.getTitre());
            TextArea txtDescription = new TextArea(selectedCreation.getDescription());
            ComboBox<String> comboCategorie = new ComboBox<>();
            comboCategorie.getItems().addAll("Painting", "Digital Art", "Sculpture", "Photography", "Other");
            comboCategorie.setValue(selectedCreation.getCategorie());
            
            TextField txtImage = new TextField(selectedCreation.getImage());
            Button btnImage = new Button("Choisir une image");
            
            ComboBox<String> comboStatut = new ComboBox<>();
            comboStatut.getItems().addAll("actif", "inactif", "signalé");
            comboStatut.setValue(selectedCreation.getStatut());
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            grid.add(new Label("Titre:"), 0, 0);
            grid.add(txtTitre, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(txtDescription, 1, 1);
            grid.add(new Label("Catégorie:"), 0, 2);
            grid.add(comboCategorie, 1, 2);
            
            HBox imageBox = new HBox(10);
            imageBox.getChildren().addAll(txtImage, btnImage);
            grid.add(new Label("Image:"), 0, 3);
            grid.add(imageBox, 1, 3);
            
            grid.add(new Label("Statut:"), 0, 4);
            grid.add(comboStatut, 1, 4);
            
            btnImage.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir une image");
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );
                
                File selectedFile = fileChooser.showOpenDialog(txtImage.getScene().getWindow());
                if (selectedFile != null) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                        Path targetPath = Paths.get(UPLOAD_DIR, fileName);
                        
                        Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                        txtImage.setText("uploads/images/" + fileName);
                    } catch (IOException ex) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image: " + ex.getMessage());
                    }
                }
            });
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Modifier la Création");
            dialog.setHeaderText("Modifier la création: " + selectedCreation.getTitre());
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (txtTitre.getText().isEmpty() || comboCategorie.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Attention", "Le titre et la catégorie sont obligatoires.");
                    return;
                }
                
                selectedCreation.setTitre(txtTitre.getText());
                selectedCreation.setDescription(txtDescription.getText());
                selectedCreation.setCategorie(comboCategorie.getValue());
                selectedCreation.setImage(txtImage.getText());
                selectedCreation.setStatut(comboStatut.getValue());
                
                try {
                    creationService.update(selectedCreation);
                    refreshTable();
                    displayCreationDetails(selectedCreation);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "La création a été modifiée avec succès.");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteAction() {
        if (selectedCreation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une création à supprimer.");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la création");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la création '" + selectedCreation.getTitre() + "' ?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete the image file if it exists
                if (selectedCreation.getImage() != null && !selectedCreation.getImage().isEmpty()) {
                    try {
                        Files.deleteIfExists(Paths.get("src/main/resources/", selectedCreation.getImage()));
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la suppression de l'image: " + e.getMessage());
                    }
                }
                
                creationService.delete(selectedCreation.getId());
                refreshTable();
                
                // Clear the details pane
                selectedCreation = null;
                if (creationsTable.getItems().isEmpty()) {
                    creationTitle.setText("");
                    creationDate.setText("");
                    categorieLabel.setText("");
                    createurLabel.setText("");
                    statutLabel.setText("");
                    likesLabel.setText("");
                    descriptionArea.setText("");
                    creationImage.setImage(new Image(getClass().getResource("/images/dot-pattern.png").toExternalForm()));
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "La création a été supprimée avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddLike() {
        if (selectedCreation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une création.");
            return;
        }
        
        try {
            boolean success = creationService.incrementLike(selectedCreation.getId());
            if (success) {
                selectedCreation.setNbLike(selectedCreation.getNbLike() + 1);
                likesLabel.setText(String.valueOf(selectedCreation.getNbLike()));
                
                // Also update the table view
                refreshTable();
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Like ajouté avec succès!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter un like.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    @FXML
    private void showCommentaires() {
        if (selectedCreation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une création pour voir les commentaires.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Commentaire.fxml"));
            Parent root = loader.load();
            
            CommentaireController controller = loader.getController();
            controller.setCreation(selectedCreation);
            controller.setPreviousView(creationsTable.getScene().getRoot());
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) creationsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Commentaires - " + selectedCreation.getTitre());
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture des commentaires: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshTable() {
        try {
            List<Creation> creations = creationService.getAll();
            creationsTable.getItems().clear();
            creationsTable.getItems().addAll(creations);
            
            if (!creations.isEmpty() && selectedCreation == null) {
                creationsTable.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données: " + e.getMessage());
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