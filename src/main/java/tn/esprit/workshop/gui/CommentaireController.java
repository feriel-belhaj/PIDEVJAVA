package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Commentaire;
import tn.esprit.workshop.models.Creation;
import tn.esprit.workshop.services.CommentaireService;
import tn.esprit.workshop.services.UtilisateurService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CommentaireController implements Initializable {
    @FXML private ImageView creationThumbnail;
    @FXML private Label creationTitreLabel;
    @FXML private Label creationCategorieLabel;
    @FXML private Label creationDateLabel;
    @FXML private VBox commentairesContainer;
    @FXML private TextArea nouveauCommentaireArea;
    @FXML private CheckBox isAnonymousCheckbox;
    @FXML private TableView<Commentaire> commentairesTable;
    @FXML private TableColumn<Commentaire, Integer> commentIdColumn;
    @FXML private TableColumn<Commentaire, String> commentUtilisateurColumn;
    @FXML private TableColumn<Commentaire, String> commentContenuColumn;
    @FXML private TableColumn<Commentaire, LocalDateTime> commentDateColumn;
    @FXML private TableColumn<Commentaire, String> commentEtatColumn;
    @FXML private ComboBox<String> commentFilterComboBox;
    
    private Creation creation;
    private Parent previousView;
    private CommentaireService commentaireService;
    private UtilisateurService utilisateurService;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        commentaireService = new CommentaireService();
        utilisateurService = new UtilisateurService();
        
        // Initialize filter ComboBox
        commentFilterComboBox.getItems().addAll("Tous", "actif", "signalé", "inactif");
        commentFilterComboBox.setValue("Tous");
        
        // Configure table columns
        commentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        commentUtilisateurColumn.setCellFactory(column -> new TableCell<Commentaire, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Commentaire commentaire = getTableRow().getItem();
                    if (commentaire != null) {
                        if (commentaire.getUtilisateurId() == null) {
                            setText("Anonyme");
                        } else {
                            try {
                                setText(utilisateurService.getUsernameById(commentaire.getUtilisateurId()));
                            } catch (Exception e) {
                                setText("Utilisateur #" + commentaire.getUtilisateurId());
                            }
                        }
                    } else {
                        setText(null);
                    }
                }
            }
        });
        commentContenuColumn.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        commentDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateComment"));
        commentDateColumn.setCellFactory(column -> new TableCell<Commentaire, LocalDateTime>() {
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
        commentEtatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
    }

    public void setCreation(Creation creation) {
        this.creation = creation;
        updateUI();
        loadComments();
    }
    
    public void setPreviousView(Parent previousView) {
        this.previousView = previousView;
    }
    
    private void updateUI() {
        if (creation == null) return;
        
        // Set creation details
        creationTitreLabel.setText(creation.getTitre());
        creationCategorieLabel.setText(creation.getCategorie());
        creationDateLabel.setText(dateFormatter.format(creation.getDatePublic()));
        
        // Load image
        try {
            if (creation.getImage() != null && !creation.getImage().isEmpty()) {
                URL imageUrl = getClass().getResource("/" + creation.getImage());
                if (imageUrl != null) {
                    creationThumbnail.setImage(new Image(imageUrl.toExternalForm()));
                } else {
                    File file = new File("src/main/resources/" + creation.getImage());
                    if (file.exists()) {
                        creationThumbnail.setImage(new Image(file.toURI().toString()));
                    } else {
                        creationThumbnail.setImage(new Image(getClass().getResource("/images/dot-pattern.png").toExternalForm()));
                    }
                }
            } else {
                creationThumbnail.setImage(new Image(getClass().getResource("/images/dot-pattern.png").toExternalForm()));
            }
        } catch (Exception e) {
            creationThumbnail.setImage(new Image(getClass().getResource("/images/dot-pattern.png").toExternalForm()));
        }
    }
    
    private void loadComments() {
        if (creation == null) return;
        
        try {
            // Get comments for this creation
            List<Commentaire> commentaires = commentaireService.getByCreationId(creation.getId());
            
            // Clear existing comments
            commentairesContainer.getChildren().clear();
            
            // Populate comments container
            for (Commentaire commentaire : commentaires) {
                // Only show active comments in the visual list
                if ("actif".equals(commentaire.getEtat())) {
                    VBox commentBox = createCommentBox(commentaire);
                    commentairesContainer.getChildren().add(commentBox);
                }
            }
            
            // Update table view (for admin)
            commentairesTable.getItems().clear();
            commentairesTable.getItems().addAll(commentaires);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des commentaires: " + e.getMessage());
        }
    }
    
    private VBox createCommentBox(Commentaire commentaire) {
        VBox commentBox = new VBox(5);
        commentBox.getStyleClass().add("comment-box");
        commentBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        // User info and date
        HBox headerBox = new HBox(10);
        Label userLabel = new Label();
        if (commentaire.getUtilisateurId() == null) {
            userLabel.setText("Anonyme");
        } else {
            try {
                userLabel.setText(utilisateurService.getUsernameById(commentaire.getUtilisateurId()));
            } catch (Exception e) {
                userLabel.setText("Utilisateur #" + commentaire.getUtilisateurId());
            }
        }
        userLabel.setStyle("-fx-font-weight: bold;");
        
        Label dateLabel = new Label(dateFormatter.format(commentaire.getDateComment()));
        dateLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666666;");
        
        headerBox.getChildren().addAll(userLabel, dateLabel);
        
        // Comment content
        Label contentLabel = new Label(commentaire.getContenu());
        contentLabel.setWrapText(true);
        
        // Add action buttons
        HBox actionBox = new HBox(10);
        Button modifierBtn = new Button("Modifier");
        modifierBtn.getStyleClass().add("update-btn");
        modifierBtn.setOnAction(e -> handleModifierCommentaire(commentaire));
        
        actionBox.getChildren().add(modifierBtn);
        
        commentBox.getChildren().addAll(headerBox, contentLabel, actionBox);
        return commentBox;
    }
    
    /**
     * Handles the modification of a comment
     * @param commentaire The comment to modify
     */
    private void handleModifierCommentaire(Commentaire commentaire) {
        // Create a dialog for editing the comment
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Modifier le commentaire");
        dialog.setHeaderText("Modifier votre commentaire");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the text area and set its content
        TextArea textArea = new TextArea(commentaire.getContenu());
        textArea.setWrapText(true);
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(200);
        
        dialog.getDialogPane().setContent(textArea);
        
        // Convert the result to the comment text when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return textArea.getText();
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(content -> {
            if (!content.trim().isEmpty()) {
                try {
                    // Update the comment with new content
                    commentaire.setContenu(content.trim());
                    boolean success = commentaireService.update(commentaire);
                    if (success) {
                        loadComments();
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Le commentaire a été mis à jour avec succès!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le commentaire.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Attention", "Le commentaire ne peut pas être vide.");
            }
        });
    }
    
    @FXML
    private void handlePublierCommentaire() {
        if (creation == null) return;
        
        String contenu = nouveauCommentaireArea.getText().trim();
        if (contenu.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez écrire un commentaire avant de publier.");
            return;
        }
        
        try {
            Commentaire commentaire = new Commentaire();
            commentaire.setCreationId(creation.getId());
            
            // Set user ID based on anonymous checkbox
            if (isAnonymousCheckbox.isSelected()) {
                commentaire.setUtilisateurId(null);
            } else {
                try {
                    commentaire.setUtilisateurId(utilisateurService.getCurrentUser().getId());
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez être connecté pour commenter. Votre commentaire sera publié en anonyme.");
                    commentaire.setUtilisateurId(null);
                }
            }
            
            commentaire.setContenu(contenu);
            commentaire.setDateComment(LocalDateTime.now());
            commentaire.setEtat("actif");
            
            boolean success = commentaireService.add(commentaire);
            if (success) {
                nouveauCommentaireArea.clear();
                isAnonymousCheckbox.setSelected(false);
                loadComments();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre commentaire a été publié avec succès!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de publier votre commentaire.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAfficherTout() {
        commentFilterComboBox.setValue("Tous");
        filterComments();
    }
    
    @FXML
    private void handleApprouverCommentaire() {
        Commentaire selectedCommentaire = commentairesTable.getSelectionModel().getSelectedItem();
        if (selectedCommentaire == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un commentaire à approuver.");
            return;
        }
        
        try {
            boolean success = commentaireService.updateStatus(selectedCommentaire.getId(), "actif");
            if (success) {
                loadComments();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le commentaire a été approuvé.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'approuver le commentaire.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSignalerCommentaire() {
        Commentaire selectedCommentaire = commentairesTable.getSelectionModel().getSelectedItem();
        if (selectedCommentaire == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un commentaire à signaler.");
            return;
        }
        
        try {
            boolean success = commentaireService.updateStatus(selectedCommentaire.getId(), "signalé");
            if (success) {
                loadComments();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le commentaire a été signalé.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de signaler le commentaire.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimerCommentaire() {
        Commentaire selectedCommentaire = commentairesTable.getSelectionModel().getSelectedItem();
        if (selectedCommentaire == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un commentaire à supprimer.");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le commentaire");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = commentaireService.delete(selectedCommentaire.getId());
                    if (success) {
                        loadComments();
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Le commentaire a été supprimé.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le commentaire.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
                }
            }
        });
    }
    
    @FXML
    private void filterComments() {
        String filter = commentFilterComboBox.getValue();
        
        try {
            List<Commentaire> allComments = commentaireService.getByCreationId(creation.getId());
            commentairesTable.getItems().clear();
            
            for (Commentaire commentaire : allComments) {
                if ("Tous".equals(filter) || commentaire.getEtat().equals(filter)) {
                    commentairesTable.getItems().add(commentaire);
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du filtrage des commentaires: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRetourAction() {
        try {
            if (previousView != null) {
                // Instead of reusing the previousView (which might be attached to another scene)
                // Get the current stage and load a fresh instance of the view
                Stage stage = (Stage) creationThumbnail.getScene().getWindow();
                stage.setScene(new Scene(clonePreviousView()));
                stage.setTitle("Gestion des Créations");
                stage.show();
            } else {
                // Fallback if previousView is somehow null
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Creation.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) creationThumbnail.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Gestion des Créations");
                stage.show();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la page des créations: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a fresh copy of the previous view to avoid reusing nodes
     * that are already attached to another scene
     */
    private Parent clonePreviousView() throws IOException {
        if (previousView == null) {
            return null;
        }
        
        // Instead of directly using previousView, get its source FXML and reload it
        String fxmlPath = "/fxml/Creation.fxml"; // Default fallback
        
        // Try to determine the actual FXML path from the class name if possible
        if (previousView.getId() != null) {
            fxmlPath = "/fxml/" + previousView.getId() + ".fxml";
        } else {
            // Could also check the class name if ID isn't set
            String className = previousView.getClass().getSimpleName();
            if (className.contains("BorderPane") || className.contains("AnchorPane")) {
                // This is a generic container, look for a controller
                Object controller = null;
                try {
                    // Reflection might be used here, but for simplicity:
                    if (className.toLowerCase().contains("creation")) {
                        fxmlPath = "/fxml/Creation.fxml";
                    }
                } catch (Exception e) {
                    // Keep the default
                }
            }
        }
        
        // Load a fresh instance
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        return loader.load();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}