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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Consumer;

public class ModifierPartenariatController {

    private final ServicePartenariat servicePartenariat;
    private final Partenariat partenariat;
    private final Consumer<Void> onCloseCallback;
    private String nouveauCheminImage;
    private final String IMAGE_DESTINATION_PATH = "C:\\xampp\\htdocs\\img\\";

    // Error labels for validation
    private Label errorNom;
    private Label errorType;
    private Label errorDescription;
    private Label errorDateDebut;
    private Label errorDateFin;
    private Label errorImage;

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
            errorNom = new Label();
            errorNom.setStyle("-fx-text-fill: red;");
            errorNom.setVisible(false);
            nomBox.getChildren().addAll(nomLabel, nomField, errorNom);

            // Type
            VBox typeBox = new VBox(5);
            Label typeLabel = new Label("Type");
            typeLabel.setStyle("-fx-text-fill: #555555;");
            TextField typeField = new TextField(partenariat.getType() != null ? partenariat.getType() : "");
            typeField.setPrefWidth(400);
            errorType = new Label();
            errorType.setStyle("-fx-text-fill: red;");
            errorType.setVisible(false);
            typeBox.getChildren().addAll(typeLabel, typeField, errorType);

            // Description
            VBox descriptionBox = new VBox(5);
            Label descriptionLabel = new Label("Description");
            descriptionLabel.setStyle("-fx-text-fill: #555555;");
            TextArea descriptionArea = new TextArea(partenariat.getDescription() != null ? partenariat.getDescription() : "");
            descriptionArea.setPrefHeight(100);
            descriptionArea.setWrapText(true);
            errorDescription = new Label();
            errorDescription.setStyle("-fx-text-fill: red;");
            errorDescription.setVisible(false);
            descriptionBox.getChildren().addAll(descriptionLabel, descriptionArea, errorDescription);

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
                    // Pré-remplir avec la date actuelle
                    LocalDate defaultDateDebut = LocalDate.now();
                    dateDebutPicker.setValue(defaultDateDebut);
                    System.out.println("Date de début pré-remplie: " + defaultDateDebut);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date de début: " + e.getMessage());
                // Pré-remplir avec la date actuelle en cas d'erreur
                LocalDate defaultDateDebut = LocalDate.now();
                dateDebutPicker.setValue(defaultDateDebut);
                System.out.println("Date de début pré-remplie après erreur: " + defaultDateDebut);
            }
            errorDateDebut = new Label();
            errorDateDebut.setStyle("-fx-text-fill: red;");
            errorDateDebut.setVisible(false);
            dateDebutBox.getChildren().addAll(dateDebutLabel, dateDebutPicker, errorDateDebut);

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
                    // Pré-remplir avec une date future (1 mois après la date actuelle)
                    LocalDate defaultDateFin = LocalDate.now().plusMonths(1);
                    dateFinPicker.setValue(defaultDateFin);
                    System.out.println("Date de fin pré-remplie: " + defaultDateFin);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date de fin: " + e.getMessage());
                // Pré-remplir avec une date future en cas d'erreur
                LocalDate defaultDateFin = LocalDate.now().plusMonths(1);
                dateFinPicker.setValue(defaultDateFin);
                System.out.println("Date de fin pré-remplie après erreur: " + defaultDateFin);
            }
            errorDateFin = new Label();
            errorDateFin.setStyle("-fx-text-fill: red;");
            errorDateFin.setVisible(false);
            dateFinBox.getChildren().addAll(dateFinLabel, dateFinPicker, errorDateFin);

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
                    File imageFile = new File(IMAGE_DESTINATION_PATH, partenariat.getImage());
                    imagePathLabel.setText(imageFile.getName());
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès au fichier image: " + e.getMessage());
                }
            }
            errorImage = new Label();
            errorImage.setStyle("-fx-text-fill: red;");
            errorImage.setVisible(false);
            fileSelectBox.getChildren().addAll(choisirImageBtn, imagePathLabel);

            // Aperçu de l'image
            ImageView imagePreview = new ImageView();
            imagePreview.setFitHeight(150);
            imagePreview.setFitWidth(150);
            imagePreview.setPreserveRatio(true);

            // Charger l'image existante si disponible
            if (partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                try {
                    File file = new File(IMAGE_DESTINATION_PATH, partenariat.getImage());
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

            imageBox.getChildren().addAll(imageTitleLabel, fileSelectBox, errorImage, imagePreview);

            // Bouton Mettre à jour
            HBox updateBtnBox = new HBox();
            updateBtnBox.setAlignment(Pos.CENTER);
            Button updateBtn = new Button("Mettre à jour");
            updateBtn.setStyle("-fx-background-color: #D4A76A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 25;");
            updateBtnBox.getChildren().add(updateBtn); // Correction ici : get.Children() -> getChildren()
            updateBtnBox.setPadding(new Insets(20, 0, 0, 0));

            // Ajouter tous les éléments au conteneur principal
            root.getChildren().addAll(
                    titleLabel,
                    nomBox,
                    typeBox,
                    descriptionBox,
                    dateDebutBox,
                    dateFinBox,
                    imageBox,
                    updateBtnBox
            );

            System.out.println("Interface créée, configuration de la scène...");

            // Ajouter un ScrollPane pour rendre la fenêtre scrollable
            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #F5F5F5;");

            // Créer la scène et la fenêtre
            Scene scene = new Scene(scrollPane, 600, 750);
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
                    // Reset styles and errors
                    resetStyle(nomField, typeField, descriptionArea, dateDebutPicker, dateFinPicker);
                    resetErrors();

                    boolean hasError = false;

                    // Validation du nom (si non vide)
                    String nomText = nomField.getText();
                    if (!nomText.isEmpty()) {
                        if (nomText.length() > 50) {
                            nomField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                            errorNom.setText("Le nom ne peut pas dépasser 50 caractères.");
                            errorNom.setVisible(true);
                            hasError = true;
                        } else if (!nomText.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï]+( [a-zA-ZÀ-ÿéèêëàâäçôùûîï]+)*$")) {
                            nomField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                            errorNom.setText("Le nom doit commencer par une majuscule et peut contenir des lettres accentuées et des espaces.");
                            errorNom.setVisible(true);
                            hasError = true;
                        }
                    }

                    // Validation du type (si non vide)
                    String typeText = typeField.getText();
                    if (!typeText.isEmpty()) {
                        if (typeText.length() > 50) {
                            typeField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                            errorType.setText("Le type ne peut pas dépasser 50 caractères.");
                            errorType.setVisible(true);
                            hasError = true;
                        } else if (!typeText.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï]+( [a-zA-ZÀ-ÿéèêëàâäçôùûîï]+)*$")) {
                            typeField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                            errorType.setText("Le type doit commencer par une majuscule et peut contenir des lettres accentuées et des espaces.");
                            errorType.setVisible(true);
                            hasError = true;
                        }
                    }

                    // Validation de la description (si non vide)
                    String descriptionText = descriptionArea.getText();
                    if (!descriptionText.isEmpty()) {
                        if (descriptionText.length() < 10) {
                            descriptionArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                            errorDescription.setText("La description doit contenir au moins 10 caractères.");
                            errorDescription.setVisible(true);
                            hasError = true;
                        } else if (descriptionText.length() > 255) {
                            descriptionArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                            errorDescription.setText("La description ne peut pas dépasser 255 caractères.");
                            errorDescription.setVisible(true);
                            hasError = true;
                        } else if (!descriptionText.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï ,;.:'\"!?-]*$")) {
                            descriptionArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                            errorDescription.setText("La description doit commencer par une majuscule et contenir uniquement des lettres accentuées, des espaces et des signes de ponctuation.");
                            errorDescription.setVisible(true);
                            hasError = true;
                        }
                    }

                    // Validation des dates (non obligatoire, mais vérifier si les deux sont remplies)
                    LocalDate dateDebutValue = dateDebutPicker.getValue();
                    LocalDate dateFinValue = dateFinPicker.getValue();
                    if (dateDebutValue != null && dateFinValue != null && dateFinValue.isBefore(dateDebutValue)) {
                        dateFinPicker.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorDateFin.setText("La date de fin doit être postérieure à la date de début.");
                        errorDateFin.setVisible(true);
                        hasError = true;
                    }

                    // Validation de l'image (non obligatoire, mais vérifier le format si une image est sélectionnée)
                    if (nouveauCheminImage != null && !nouveauCheminImage.isEmpty()) {
                        String lowerCasePath = nouveauCheminImage.toLowerCase();
                        if (!lowerCasePath.endsWith(".png") && !lowerCasePath.endsWith(".jpg") && !lowerCasePath.endsWith(".jpeg")) {
                            imagePathLabel.setTextFill(javafx.scene.paint.Color.RED);
                            errorImage.setText("Veuillez sélectionner une image au format PNG, JPEG ou JPG.");
                            errorImage.setVisible(true);
                            hasError = true;
                        }
                    }

                    if (hasError) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Erreur de validation");
                        alert.setHeaderText(null);
                        alert.setContentText("Veuillez corriger les erreurs en rouge !");
                        alert.showAndWait();
                        return;
                    }

                    // Mettre à jour les données du partenariat
                    partenariat.setNom(nomField.getText());
                    partenariat.setType(typeField.getText());
                    partenariat.setDescription(descriptionArea.getText());

                    // Mettre à jour le chemin de l'image
                    if (nouveauCheminImage != null) {
                        partenariat.setImage(nouveauCheminImage);
                    } else {
                        partenariat.setImage(null); // Permettre que l'image soit null
                    }

                    // Convertir LocalDate en Date (gérer les cas où les dates sont null)
                    if (dateDebutPicker.getValue() != null) {
                        Date dateDebut = Date.from(
                                dateDebutPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );
                        partenariat.setDateDebut(dateDebut);
                        System.out.println("Mise à jour - dateDebut: " + dateDebut);
                    } else {
                        partenariat.setDateDebut(null);
                        System.out.println("Mise à jour - dateDebut: null");
                    }

                    if (dateFinPicker.getValue() != null) {
                        Date dateFin = Date.from(
                                dateFinPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );
                        partenariat.setDateFin(dateFin);
                        System.out.println("Mise à jour - dateFin: " + dateFin);
                    } else {
                        partenariat.setDateFin(null);
                        System.out.println("Mise à jour - dateFin: null");
                    }

                    // Log de l'image
                    System.out.println("Mise à jour - image: " + nouveauCheminImage);

                    // Mettre à jour le statut dynamiquement
                    partenariat.updateStatut();
                    System.out.println("Statut mis à jour dynamiquement: " + partenariat.getStatut());

                    // Sauvegarder les modifications
                    servicePartenariat.update(partenariat);
                    System.out.println("Partenariat mis à jour avec succès: ID=" + partenariat.getId());

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
                            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
                    );

                    File selectedFile = fileChooser.showOpenDialog(stage);
                    if (selectedFile != null) {
                        // Vérifier le format de l'image
                        String lowerCasePath = selectedFile.getName().toLowerCase();
                        if (!lowerCasePath.endsWith(".png") && !lowerCasePath.endsWith(".jpg") && !lowerCasePath.endsWith(".jpeg")) {
                            imagePathLabel.setTextFill(javafx.scene.paint.Color.RED);
                            errorImage.setText("Veuillez sélectionner une image au format PNG, JPEG ou JPG.");
                            errorImage.setVisible(true);
                            return;
                        }

                        // Copier l'image dans C:\xampp\htdocs\img
                        File destDir = new File(IMAGE_DESTINATION_PATH);
                        if (!destDir.exists()) {
                            destDir.mkdirs(); // Créer le dossier s'il n'existe pas
                        }
                        File destFile = new File(destDir, selectedFile.getName());
                        try {
                            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            nouveauCheminImage = selectedFile.getName(); // Stocker uniquement le nom du fichier
                            imagePathLabel.setText(selectedFile.getName());
                            imagePathLabel.setTextFill(javafx.scene.paint.Color.BLACK);
                            System.out.println("Image copiée avec succès: " + destFile.getAbsolutePath());

                            // Mettre à jour l'aperçu
                            Image image = new Image(destFile.toURI().toString());
                            imagePreview.setImage(image);
                        } catch (IOException ex) {
                            System.err.println("Erreur lors de la copie de l'image: " + ex.getMessage());
                            afficherErreur("Erreur", "Impossible de copier l'image dans le dossier cible: " + ex.getMessage());
                        }
                    } else {
                        // Si l'utilisateur annule la sélection, réinitialiser l'image
                        nouveauCheminImage = null;
                        imagePathLabel.setText("Aucun fichier n'a été sélectionné");
                        imagePathLabel.setTextFill(javafx.scene.paint.Color.BLACK);
                        imagePreview.setImage(null);
                        errorImage.setVisible(false);
                    }
                } catch (Exception ex) {
                    System.err.println("Erreur lors de la sélection de l'image: " + ex.getMessage());
                    afficherErreur("Erreur", "Impossible de sélectionner l'image: " + ex.getMessage());
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

    private void resetStyle(Control... controls) {
        for (Control control : controls) {
            control.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        }
    }

    private void resetErrors() {
        errorNom.setVisible(false);
        errorType.setVisible(false);
        errorDescription.setVisible(false);
        errorDateDebut.setVisible(false);
        errorDateFin.setVisible(false);
        errorImage.setVisible(false);
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
