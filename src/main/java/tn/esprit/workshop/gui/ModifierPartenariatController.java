package tn.esprit.workshop.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServicePartenariat;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Contrôleur pour la modification des partenariats
 * Utilise du code Java pur sans FXML
 */
public class ModifierPartenariatController {

    private final ServicePartenariat servicePartenariat;
    private final Partenariat partenariat;
    private final Consumer<Void> onCloseCallback;
    private String nouveauCheminImage;

    public ModifierPartenariatController(Partenariat partenariat, Consumer<Void> onCloseCallback) {
        if (partenariat == null) {
            throw new IllegalArgumentException("Le partenariat ne peut pas être null");
        }
        
        this.partenariat = partenariat;
        this.servicePartenariat = new ServicePartenariat();
        this.onCloseCallback = onCloseCallback;
        this.nouveauCheminImage = partenariat.getImage();
        
        System.out.println("ModifierPartenariatController initialisé pour le partenariat ID: " + partenariat.getId());
        System.out.println("Nom: " + partenariat.getNom());
        System.out.println("Type: " + partenariat.getType());
        System.out.println("Image: " + this.nouveauCheminImage);
    }

    public void afficher() {
        try {
            System.out.println("Démarrage de la création de l'interface de modification...");
            
            // Création de la scène manuellement
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #F5F5F5;");

            // Titre
            Label titleLabel = new Label("Mise à Jour du Partenariat");
            titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-style: italic;");
            titleLabel.setAlignment(Pos.CENTER);
            titleLabel.setMaxWidth(Double.MAX_VALUE);

            // Nom
            VBox nomBox = new VBox(5);
            Label nomLabel = new Label("Nom");
            nomLabel.setStyle("-fx-text-fill: #555555;");
            TextField nomField = new TextField(partenariat.getNom() != null ? partenariat.getNom() : "");
            nomField.setPrefWidth(400);
            nomBox.getChildren().addAll(nomLabel, nomField);

            // Type
            VBox typeBox = new VBox(5);
            Label typeLabel = new Label("Type");
            typeLabel.setStyle("-fx-text-fill: #555555;");
            TextField typeField = new TextField(partenariat.getType() != null ? partenariat.getType() : "");
            typeField.setPrefWidth(400);
            typeBox.getChildren().addAll(typeLabel, typeField);

            // Description
            VBox descriptionBox = new VBox(5);
            Label descriptionLabel = new Label("Description");
            descriptionLabel.setStyle("-fx-text-fill: #555555;");
            TextArea descriptionArea = new TextArea(partenariat.getDescription() != null ? partenariat.getDescription() : "");
            descriptionArea.setPrefHeight(100);
            descriptionArea.setWrapText(true);
            descriptionBox.getChildren().addAll(descriptionLabel, descriptionArea);

            // Date début
            VBox dateDebutBox = new VBox(5);
            Label dateDebutLabel = new Label("Date debut");
            dateDebutLabel.setStyle("-fx-text-fill: #555555;");
            DatePicker dateDebutPicker = new DatePicker();
            try {
                if (partenariat.getDateDebut() != null) {
                    LocalDate dateDebut = partenariat.getDateDebut().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    dateDebutPicker.setValue(dateDebut);
                    System.out.println("Date de début définie avec succès: " + dateDebut);
                } else {
                    // Si date null, définir la date du jour
                    dateDebutPicker.setValue(LocalDate.now());
                    System.out.println("La date de début est null, définie à aujourd'hui");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date de début: " + e.getMessage());
                dateDebutPicker.setValue(LocalDate.now());
                System.out.println("Date de début définie par défaut après erreur: " + LocalDate.now());
            }
            dateDebutBox.getChildren().addAll(dateDebutLabel, dateDebutPicker);

            // Date fin
            VBox dateFinBox = new VBox(5);
            Label dateFinLabel = new Label("Date fin");
            dateFinLabel.setStyle("-fx-text-fill: #555555;");
            DatePicker dateFinPicker = new DatePicker();
            try {
                if (partenariat.getDateFin() != null) {
                    LocalDate dateFin = partenariat.getDateFin().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    dateFinPicker.setValue(dateFin);
                    System.out.println("Date de fin définie avec succès: " + dateFin);
                } else {
                    System.out.println("La date de fin est null, aucune valeur définie");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date de fin: " + e.getMessage());
                // Si erreur, définir une date par défaut
                dateFinPicker.setValue(LocalDate.now());
                System.out.println("Date de fin définie par défaut après erreur: " + LocalDate.now());
            }
            dateFinBox.getChildren().addAll(dateFinLabel, dateFinPicker);

            // Statut
            VBox statutBox = new VBox(5);
            Label statutLabel = new Label("Statut");
            statutLabel.setStyle("-fx-text-fill: #555555;");
            ComboBox<String> statutCombo = new ComboBox<>();
            statutCombo.getItems().addAll("En cours", "Terminé", "En attente", "Annulé");
            statutCombo.setValue(partenariat.getStatut() != null ? partenariat.getStatut() : "En attente");
            statutBox.getChildren().addAll(statutLabel, statutCombo);

            // Image du Partenariat
            VBox imageBox = new VBox(10);
            Label imageTitleLabel = new Label("Image du Partenariat");
            imageTitleLabel.setStyle("-fx-text-fill: #555555;");

            // Conteneur pour la sélection de fichier
            HBox fileSelectBox = new HBox(10);
            Button choisirImageBtn = new Button("Choisir un fichier");
            choisirImageBtn.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #CCCCCC; -fx-border-radius: 3;");
            Label imagePathLabel = new Label("Aucun fichier n'a été sélectionné");
            
            if (partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                try {
                    File imageFile = new File(partenariat.getImage());
                    imagePathLabel.setText(imageFile.getName());
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès au fichier image: " + e.getMessage());
                }
            }
            
            fileSelectBox.getChildren().addAll(choisirImageBtn, imagePathLabel);

            // Aperçu de l'image
            ImageView imagePreview = new ImageView();
            imagePreview.setFitHeight(150);
            imagePreview.setFitWidth(150);
            imagePreview.setPreserveRatio(true);

            // Charger l'image existante si disponible
            if (partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                try {
                    File file = new File(partenariat.getImage());
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        imagePreview.setImage(image);
                    } else {
                        System.out.println("Le fichier image n'existe pas: " + partenariat.getImage());
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            imageBox.getChildren().addAll(imageTitleLabel, fileSelectBox, imagePreview);

            // Bouton Mettre à jour
            HBox updateBtnBox = new HBox();
            updateBtnBox.setAlignment(Pos.CENTER);
            Button updateBtn = new Button("Mettre à jour");
            updateBtn.setStyle("-fx-background-color: #D4A76A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 25;");
            updateBtnBox.getChildren().add(updateBtn);
            updateBtnBox.setPadding(new Insets(20, 0, 0, 0));

            // Ajouter tous les éléments au conteneur principal
            root.getChildren().addAll(
                    titleLabel,
                    nomBox,
                    typeBox,
                    descriptionBox,
                    dateDebutBox,
                    dateFinBox,
                    statutBox,
                    imageBox,
                    updateBtnBox
            );
            
            System.out.println("Interface créée, configuration de la scène...");

            // Créer la scène et la fenêtre
            Scene scene = new Scene(root, 600, 750);
            Stage stage = new Stage();
            stage.setTitle("Modifier un Partenariat");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(600);
            stage.setMinHeight(750);
            
            System.out.println("Configuration des événements...");

            // Action du bouton de mise à jour
            updateBtn.setOnAction(e -> {
                try {
                    // Mettre à jour les données du partenariat
                    partenariat.setNom(nomField.getText());
                    partenariat.setType(typeField.getText());
                    partenariat.setDescription(descriptionArea.getText());
                    partenariat.setStatut(statutCombo.getValue());

                    // Mettre à jour le chemin de l'image
                    if (nouveauCheminImage != null) {
                        partenariat.setImage(nouveauCheminImage);
                    }

                    // Convertir LocalDate en Date
                    if (dateDebutPicker.getValue() != null) {
                        Date dateDebut = Date.from(
                                dateDebutPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );
                        partenariat.setDateDebut(dateDebut);
                    }

                    if (dateFinPicker.getValue() != null) {
                        Date dateFin = Date.from(
                                dateFinPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );
                        partenariat.setDateFin(dateFin);
                    }

                    // Sauvegarder les modifications
                    servicePartenariat.update(partenariat);

                    // Afficher un message de succès
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Le partenariat a été mis à jour avec succès !");
                    successAlert.showAndWait();

                    // Fermer la fenêtre
                    stage.close();
                } catch (SQLException ex) {
                    System.err.println("Erreur SQL lors de la mise à jour: " + ex.getMessage());
                    ex.printStackTrace();
                    afficherErreur("Erreur", "Impossible de mettre à jour le partenariat: " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("Erreur générale lors de la mise à jour: " + ex.getMessage());
                    ex.printStackTrace();
                    afficherErreur("Erreur", "Une erreur s'est produite lors de la mise à jour: " + ex.getMessage());
                }
            });

            // Gestion de l'image
            choisirImageBtn.setOnAction(e -> {
                try {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Choisir une image");
                    fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
                    );

                    File selectedFile = fileChooser.showOpenDialog(stage);
                    if (selectedFile != null) {
                        nouveauCheminImage = selectedFile.getAbsolutePath();
                        imagePathLabel.setText(selectedFile.getName());

                        // Mettre à jour l'aperçu
                        try {
                            Image image = new Image(selectedFile.toURI().toString());
                            imagePreview.setImage(image);
                        } catch (Exception ex) {
                            System.err.println("Erreur lors du chargement de l'image: " + ex.getMessage());
                            afficherErreur("Erreur", "Impossible de charger l'image sélectionnée");
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Erreur lors de la sélection de l'image: " + ex.getMessage());
                    afficherErreur("Erreur", "Impossible de sélectionner l'image");
                }
            });

            // Exécuter le callback quand la fenêtre est fermée
            stage.setOnHidden(e -> {
                if (onCloseCallback != null) {
                    onCloseCallback.accept(null);
                }
            });
            
            System.out.println("Affichage de la fenêtre...");

            // Afficher la fenêtre
            stage.show();

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