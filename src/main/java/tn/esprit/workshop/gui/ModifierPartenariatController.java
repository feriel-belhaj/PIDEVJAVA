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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Consumer;

public class ModifierPartenariatController {

    private final ServicePartenariat servicePartenariat;
    private final Partenariat partenariat;
    private final Consumer<Void> onCloseCallback;
    private String nouveauCheminImage;

    // Error labels for validation
    private Label errorNom;
    private Label errorType;
    private Label errorDescription;
    private Label errorStatut;
    private Label errorDateDebut;
    private Label errorDateFin;
    private Label errorImage;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String UPLOAD_DIR = "C:\\xampp\\htdocs\\img";
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg"};

    public ModifierPartenariatController(Partenariat partenariat, Consumer<Void> onCloseCallback) {
        if (partenariat == null) {
            throw new IllegalArgumentException("Le partenariat ne peut pas être null");
        }

        this.partenariat = partenariat;
        this.servicePartenariat = new ServicePartenariat();
        this.onCloseCallback = onCloseCallback;
        this.nouveauCheminImage = partenariat.getImage() != null ? partenariat.getImage() : "";

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
            Label dateDebutLabel = new Label("Date debut *");
            dateDebutLabel.setStyle("-fx-text-fill: #555555;");
            DatePicker dateDebutPicker = new DatePicker();
            try {
                if (partenariat.getDateDebut() != null) {
                    LocalDate dateDebut = partenariat.getDateDebut().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    dateDebutPicker.setValue(dateDebut);
                    System.out.println("Date de début définie: " + dateDebut);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date de début: " + e.getMessage());
            }
            errorDateDebut = new Label();
            errorDateDebut.setStyle("-fx-text-fill: red;");
            errorDateDebut.setVisible(false);
            dateDebutBox.getChildren().addAll(dateDebutLabel, dateDebutPicker, errorDateDebut);

            // Date fin
            VBox dateFinBox = new VBox(5);
            Label dateFinLabel = new Label("Date fin *");
            dateFinLabel.setStyle("-fx-text-fill: #555555;");
            DatePicker dateFinPicker = new DatePicker();
            try {
                if (partenariat.getDateFin() != null) {
                    LocalDate dateFin = partenariat.getDateFin().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    dateFinPicker.setValue(dateFin);
                    System.out.println("Date de fin définie: " + dateFin);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date de fin: " + e.getMessage());
            }
            errorDateFin = new Label();
            errorDateFin.setStyle("-fx-text-fill: red;");
            errorDateFin.setVisible(false);
            dateFinBox.getChildren().addAll(dateFinLabel, dateFinPicker, errorDateFin);

            // Statut
            VBox statutBox = new VBox(5);
            Label statutLabel = new Label("Statut *");
            statutLabel.setStyle("-fx-text-fill: #555555;");
            ComboBox<String> statutCombo = new ComboBox<>();
            statutCombo.getItems().addAll("Actif", "EnCours");
            statutCombo.setValue(partenariat.getStatut() != null ? partenariat.getStatut() : null);
            statutCombo.setCellFactory(listView -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if ("EnCours".equals(item)) {
                            setStyle("-fx-text-fill: #D4A76A;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            });
            statutCombo.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if ("EnCours".equals(item)) {
                            setStyle("-fx-text-fill: #D4A76A;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            });
            errorStatut = new Label();
            errorStatut.setStyle("-fx-text-fill: red;");
            errorStatut.setVisible(false);
            statutBox.getChildren().addAll(statutLabel, statutCombo, errorStatut);

            // Image du Partenariat
            VBox imageBox = new VBox(10);
            Label imageTitleLabel = new Label("Image du Partenariat *");
            imageTitleLabel.setStyle("-fx-text-fill: #555555;");
            HBox fileSelectBox = new HBox(10);
            Button choisirImageBtn = new Button("Choisir un fichier");
            choisirImageBtn.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #CCCCCC; -fx-border-radius: 3;");
            Label imagePathLabel = new Label("Aucun fichier sélectionné");
            if (partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                try {
                    imagePathLabel.setText(new File(partenariat.getImage()).getName());
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès au fichier image: " + e.getMessage());
                }
            }
            errorImage = new Label();
            errorImage.setStyle("-fx-text-fill: red;");
            errorImage.setVisible(false);
            fileSelectBox.getChildren().addAll(choisirImageBtn, imagePathLabel);
            ImageView imagePreview = new ImageView();
            imagePreview.setFitHeight(150);
            imagePreview.setFitWidth(150);
            imagePreview.setPreserveRatio(true);
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
                }
            }
            imageBox.getChildren().addAll(imageTitleLabel, fileSelectBox, errorImage, imagePreview);

            // Bouton Mettre à jour
            HBox updateBtnBox = new HBox();
            updateBtnBox.setAlignment(Pos.CENTER);
            Button updateBtn = new Button("Mettre à jour");
            updateBtn.setStyle("-fx-background-color: #D4A76A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 25;");
            updateBtnBox.getChildren().add(updateBtn);
            updateBtnBox.setPadding(new Insets(20, 0, 0, 0));

            // Ajouter les éléments
            root.getChildren().addAll(
                    titleLabel, nomBox, typeBox, descriptionBox, dateDebutBox, dateFinBox, statutBox, imageBox, updateBtnBox
            );

            // ScrollPane
            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #F5F5F5;");

            // Scène et fenêtre
            Scene scene = new Scene(scrollPane, 600, 750);
            Stage stage = new Stage();
            stage.setTitle("Modifier un Partenariat");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(600);
            stage.setMinHeight(750);

            // Action de mise à jour
            updateBtn.setOnAction(e -> {
                try {
                    resetStyle(nomField, typeField, descriptionArea, dateDebutPicker, dateFinPicker, statutCombo);
                    resetErrors();
                    imagePathLabel.setTextFill(javafx.scene.paint.Color.BLACK);

                    boolean hasError = false;

                    // Validation du nom
                    String nomText = nomField.getText();
                    if (nomText.length() > 50) {
                        nomField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorNom.setText("Le nom ne peut pas dépasser 50 caractères.");
                        errorNom.setVisible(true);
                        hasError = true;
                    } else if (!nomText.isEmpty() && !nomText.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï]+( [a-zA-ZÀ-ÿéèêëàâäçôùûîï]+)*$")) {
                        nomField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorNom.setText("Le nom doit commencer par une majuscule et peut contenir des lettres accentuées et des espaces.");
                        errorNom.setVisible(true);
                        hasError = true;
                    }

                    // Validation du type
                    String typeText = typeField.getText();
                    if (typeText.length() > 50) {
                        typeField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorType.setText("Le type ne peut pas dépasser 50 caractères.");
                        errorType.setVisible(true);
                        hasError = true;
                    } else if (!typeText.isEmpty() && !typeText.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï]+( [a-zA-ZÀ-ÿéèêëàâäçôùûîï]+)*$")) {
                        typeField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorType.setText("Le type doit commencer par une majuscule et peut contenir des lettres accentuées et des espaces.");
                        errorType.setVisible(true);
                        hasError = true;
                    }

                    // Validation de la description
                    String descriptionText = descriptionArea.getText();
                    if (descriptionText.length() < 10 && !descriptionText.isEmpty()) {
                        descriptionArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorDescription.setText("La description doit contenir au moins 10 caractères.");
                        errorDescription.setVisible(true);
                        hasError = true;
                    } else if (descriptionText.length() > 255) {
                        descriptionArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorDescription.setText("La description ne peut pas dépasser 255 caractères.");
                        errorDescription.setVisible(true);
                        hasError = true;
                    } else if (!descriptionText.isEmpty() && !descriptionText.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï ,;.:'\"!?-]*$")) {
                        descriptionArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorDescription.setText("La description doit commencer par une majuscule et contenir uniquement des lettres accentuées, des espaces et des signes de ponctuation.");
                        errorDescription.setVisible(true);
                        hasError = true;
                    }

                    // Validation du statut
                    String statutValue = statutCombo.getValue();
                    if (statutValue == null) {
                        statutCombo.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorStatut.setText("Le statut est requis.");
                        errorStatut.setVisible(true);
                        hasError = true;
                    }

                    // Validation des dates
                    LocalDate dateDebutValue = dateDebutPicker.getValue();
                    if (dateDebutValue == null) {
                        dateDebutPicker.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorDateDebut.setText("La date de début est requise.");
                        errorDateDebut.setVisible(true);
                        hasError = true;
                    }

                    LocalDate dateFinValue = dateFinPicker.getValue();
                    if (dateFinValue == null) {
                        dateFinPicker.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorDateFin.setText("La date de fin est requise.");
                        errorDateFin.setVisible(true);
                        hasError = true;
                    } else if (dateDebutValue != null && dateFinValue.isBefore(dateDebutValue)) {
                        dateFinPicker.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                        errorDateFin.setText("La date de fin doit être postérieure à la date de début.");
                        errorDateFin.setVisible(true);
                        hasError = true;
                    }

                    // Validation de l'image
                    if (nouveauCheminImage == null || nouveauCheminImage.isEmpty()) {
                        imagePathLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorImage.setText("L'image est requise.");
                        errorImage.setVisible(true);
                        hasError = true;
                    } else {
                        File imageFile = new File(nouveauCheminImage);
                        if (!imageFile.exists()) {
                            imagePathLabel.setTextFill(javafx.scene.paint.Color.RED);
                            errorImage.setText("Le fichier image n'existe pas.");
                            errorImage.setVisible(true);
                            hasError = true;
                        } else if (imageFile.length() > MAX_FILE_SIZE) {
                            imagePathLabel.setTextFill(javafx.scene.paint.Color.RED);
                            errorImage.setText("L'image ne doit pas dépasser 5 Mo.");
                            errorImage.setVisible(true);
                            hasError = true;
                        } else {
                            String lowerCasePath = nouveauCheminImage.toLowerCase();
                            boolean validExtension = false;
                            for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
                                if (lowerCasePath.endsWith(ext)) {
                                    validExtension = true;
                                    break;
                                }
                            }
                            if (!validExtension) {
                                imagePathLabel.setTextFill(javafx.scene.paint.Color.RED);
                                errorImage.setText("Veuillez sélectionner une image au format PNG, JPEG ou JPG.");
                                errorImage.setVisible(true);
                                hasError = true;
                            }
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

                    // Copier l'image
                    String imageFileName = copyFileToDestination(nouveauCheminImage);

                    // Mettre à jour les données
                    partenariat.setNom(nomText);
                    partenariat.setType(typeText);
                    partenariat.setDescription(descriptionText);
                    partenariat.setStatut(statutValue);
                    partenariat.setImage(imageFileName);

                    if (dateDebutValue != null) {
                        partenariat.setDateDebut(Date.from(dateDebutValue.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }
                    if (dateFinValue != null) {
                        partenariat.setDateFin(Date.from(dateFinValue.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }

                    // Sauvegarder
                    servicePartenariat.update(partenariat);

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Le partenariat a été mis à jour avec succès !");
                    successAlert.showAndWait();

                    stage.close();
                } catch (IOException ex) {
                    afficherErreur("Erreur", "Erreur lors de la copie de l'image: " + ex.getMessage());
                } catch (SQLException ex) {
                    afficherErreur("Erreur", "Impossible de mettre à jour le partenariat: " + ex.getMessage());
                } catch (Exception ex) {
                    afficherErreur("Erreur", "Une erreur s'est produite: " + ex.getMessage());
                }
            });

            // Gestion de l'image
            choisirImageBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir une image");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
                );

                File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null) {
                    if (selectedFile.length() > MAX_FILE_SIZE) {
                        imagePathLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorImage.setText("L'image ne doit pas dépasser 5 Mo.");
                        errorImage.setVisible(true);
                        return;
                    }

                    String fileName = selectedFile.getName().toLowerCase();
                    boolean isValidExtension = false;
                    for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
                        if (fileName.endsWith(ext)) {
                            isValidExtension = true;
                            break;
                        }
                    }
                    if (!isValidExtension) {
                        imagePathLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorImage.setText("Veuillez sélectionner une image au format PNG, JPEG ou JPG.");
                        errorImage.setVisible(true);
                        return;
                    }

                    nouveauCheminImage = selectedFile.getAbsolutePath();
                    imagePathLabel.setText(selectedFile.getName());
                    imagePathLabel.setTextFill(javafx.scene.paint.Color.BLACK);
                    errorImage.setVisible(false);

                    try {
                        Image image = new Image(selectedFile.toURI().toString());
                        imagePreview.setImage(image);
                    } catch (Exception ex) {
                        afficherErreur("Erreur", "Impossible de charger l'image sélectionnée");
                    }
                }
            });

            // Callback on close
            stage.setOnHidden(e -> {
                if (onCloseCallback != null) {
                    onCloseCallback.accept(null);
                }
            });

            stage.show();
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible de créer l'interface: " + e.getMessage());
        }
    }

    private String copyFileToDestination(String sourcePath) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IOException("Chemin de l'image source non spécifié");
        }

        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new IOException("L'image source n'existe pas: " + sourcePath);
        }

        Path destDir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(destDir)) {
            Files.createDirectories(destDir);
        }

        String originalFileName = sourceFile.getName();
        String fileName = originalFileName;
        Path destPath = Paths.get(UPLOAD_DIR, fileName);

        if (Files.exists(destPath)) {
            String nameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            fileName = nameWithoutExtension + "_" + Instant.now().toEpochMilli() + extension;
            destPath = Paths.get(UPLOAD_DIR, fileName);
        }

        Files.copy(sourceFile.toPath(), destPath);
        return fileName;
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
        errorStatut.setVisible(false);
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