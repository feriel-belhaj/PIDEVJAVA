package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
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

public class AjouterPartenariat {

    @FXML
    private TextField nom;

    @FXML
    private TextArea description;

    @FXML
    private TextField type;

    @FXML
    private ComboBox<String> statut;

    @FXML
    private DatePicker dateDebut;

    @FXML
    private DatePicker dateFin;

    @FXML
    private Label imageLabel;

    @FXML
    private Label errorNom;

    @FXML
    private Label errorDescription;

    @FXML
    private Label errorType;

    @FXML
    private Label errorStatut;

    @FXML
    private Label errorDateDebut;

    @FXML
    private Label errorDateFin;

    @FXML
    private Label errorImage;

    @FXML
    private Button btnAjouter;

    private String selectedImagePath;
    private ServicePartenariat servicePartenariat;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB en octets
    private static final String UPLOAD_DIR = "C:\\xampp\\htdocs\\img";
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg"};

    @FXML
    public void initialize() {
        servicePartenariat = new ServicePartenariat();
        // Laisser les dates vides
        dateDebut.setValue(null);
        dateFin.setValue(null);

        // Initialiser les valeurs du ComboBox
        ObservableList<String> statutOptions = FXCollections.observableArrayList(
                "Actif", "EnCours"
        );
        statut.setItems(statutOptions);
        statut.setValue(null); // Aucune valeur par défaut sélectionnée

        // Configurer la cellule factory pour le ComboBox pour mettre "EnCours" en jaune
        statut.setCellFactory(listView -> new javafx.scene.control.ListCell<String>() {
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

        // Configurer l'affichage de l'élément sélectionné
        statut.setButtonCell(new javafx.scene.control.ListCell<String>() {
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
    }

    private void resetErrors() {
        errorNom.setVisible(false);
        errorDescription.setVisible(false);
        errorType.setVisible(false);
        errorStatut.setVisible(false);
        errorDateDebut.setVisible(false);
        errorDateFin.setVisible(false);
        errorImage.setVisible(false);
    }

    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Vérifier la taille du fichier
            if (selectedFile.length() > MAX_FILE_SIZE) {
                imageLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorImage.setText("L'image ne doit pas dépasser 5 Mo.");
                errorImage.setVisible(true);
                return;
            }

            // Vérifier l'extension du fichier
            String fileName = selectedFile.getName().toLowerCase();
            boolean isValidExtension = false;
            for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
                if (fileName.endsWith(ext)) {
                    isValidExtension = true;
                    break;
                }
            }
            if (!isValidExtension) {
                imageLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorImage.setText("Veuillez sélectionner une image au format PNG, JPEG ou JPG.");
                errorImage.setVisible(true);
                return;
            }

            selectedImagePath = selectedFile.getAbsolutePath();
            imageLabel.setText(selectedFile.getName());
            imageLabel.setTextFill(javafx.scene.paint.Color.BLACK);
            errorImage.setVisible(false);
        }
    }

    @FXML
    void ajouterPartenariat(ActionEvent event) {
        String nomPartenariat = nom.getText();
        String descriptionPartenariat = description.getText();
        String typePartenariat = type.getText();
        String statutPartenariat = statut.getValue();
        LocalDate dateDebutValue = dateDebut.getValue();
        LocalDate dateFinValue = dateFin.getValue();

        resetStyle(nom, description, type, dateDebut, dateFin);
        resetErrors();
        imageLabel.setTextFill(javafx.scene.paint.Color.BLACK);

        boolean hasError = false;

        // Validation du nom
        if (nomPartenariat.isEmpty()) {
            nom.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorNom.setText("Le nom est requis.");
            errorNom.setVisible(true);
            hasError = true;
        } else if (nomPartenariat.length() > 50) {
            nom.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorNom.setText("Le nom ne peut pas dépasser 50 caractères.");
            errorNom.setVisible(true);
            hasError = true;
        } else if (!nomPartenariat.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï]+( [a-zA-ZÀ-ÿéèêëàâäçôùûîï]+)*$")) {
            nom.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorNom.setText("Le nom doit commencer par une majuscule et peut contenir des lettres accentuées et des espaces.");
            errorNom.setVisible(true);
            hasError = true;
        }

        // Validation du type
        if (typePartenariat.isEmpty()) {
            type.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorType.setText("Le type est requis.");
            errorType.setVisible(true);
            hasError = true;
        } else if (typePartenariat.length() > 50) {
            type.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorType.setText("Le type ne peut pas dépasser 50 caractères.");
            errorType.setVisible(true);
            hasError = true;
        } else if (!typePartenariat.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï]+( [a-zA-ZÀ-ÿéèêëàâäçôùûîï]+)*$")) {
            type.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorType.setText("Le type doit commencer par une majuscule et peut contenir des lettres accentuées et des espaces.");
            errorType.setVisible(true);
            hasError = true;
        }

        // Validation de la description
        if (descriptionPartenariat.isEmpty()) {
            description.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDescription.setText("La description est requise.");
            errorDescription.setVisible(true);
            hasError = true;
        } else if (descriptionPartenariat.length() < 10) {
            description.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDescription.setText("La description doit contenir au moins 10 caractères.");
            errorDescription.setVisible(true);
            hasError = true;
        } else if (descriptionPartenariat.length() > 255) {
            description.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDescription.setText("La description ne peut pas dépasser 255 caractères.");
            errorDescription.setVisible(true);
            hasError = true;
        } else if (!descriptionPartenariat.matches("^[A-ZÀ-ÿ][a-zA-ZÀ-ÿéèêëàâäçôùûîï ,;.:'\"!?-]*$")) {
            description.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDescription.setText("La description doit commencer par une majuscule et contenir uniquement des lettres accentuées, des espaces et des signes de ponctuation.");
            errorDescription.setVisible(true);
            hasError = true;
        }

        // Validation du statut
        if (statutPartenariat == null) {
            statut.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorStatut.setText("Le statut est requis.");
            errorStatut.setVisible(true);
            hasError = true;
        }

        // Validation des dates
        if (dateDebutValue == null) {
            dateDebut.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDateDebut.setText("La date de début est requise.");
            errorDateDebut.setVisible(true);
            hasError = true;
        }

        if (dateFinValue == null) {
            dateFin.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDateFin.setText("La date de fin est requise.");
            errorDateFin.setVisible(true);
            hasError = true;
        } else if (dateDebutValue != null && dateFinValue.isBefore(dateDebutValue)) {
            dateFin.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorDateFin.setText("La date de fin doit être postérieure à la date de début.");
            errorDateFin.setVisible(true);
            hasError = true;
        }

        // Validation de l'image
        if (selectedImagePath == null || selectedImagePath.isEmpty()) {
            imageLabel.setTextFill(javafx.scene.paint.Color.RED);
            errorImage.setText("L'image est requise.");
            errorImage.setVisible(true);
            hasError = true;
        }

        if (hasError) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez corriger les erreurs en rouge !");
            alert.showAndWait();
            return;
        }

        try {
            // Copier l'image et obtenir le nom du fichier
            String imageFileName = copyFileToDestination(selectedImagePath);

            // Créer l'objet Partenariat avec le nom du fichier
            Partenariat partenariat = new Partenariat(
                    Date.from(dateDebutValue.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(dateFinValue.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    statutPartenariat,
                    descriptionPartenariat,
                    nomPartenariat,
                    typePartenariat,
                    imageFileName
            );

            // Insérer dans la base de données
            servicePartenariat.insert(partenariat);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Le partenariat a été ajouté avec succès !");
            alert.showAndWait();

            // Fermer la fenêtre
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();

            // Ouvre la liste des partenariats (décommentez si nécessaire)
            // ouvrirListePartenariats();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la copie de l'image: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de l'ajout du partenariat : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
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

        // Créer le répertoire de destination s'il n'existe pas
        Path destDir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(destDir)) {
            Files.createDirectories(destDir);
        }

        // Obtenir le nom du fichier
        String originalFileName = sourceFile.getName();
        String fileName = originalFileName;
        Path destPath = Paths.get(UPLOAD_DIR, fileName);

        // Gérer les conflits de noms
        if (Files.exists(destPath)) {
            String nameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            fileName = nameWithoutExtension + "_" + Instant.now().toEpochMilli() + extension;
            destPath = Paths.get(UPLOAD_DIR, fileName);
        }

        // Copier le fichier
        Files.copy(sourceFile.toPath(), destPath);

        // Retourner uniquement le nom du fichier
        return fileName;
    }

    private void resetStyle(Control... controls) {
        for (Control control : controls) {
            control.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        }
        statut.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
    }

    private void ouvrirListePartenariats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListePartenariats.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Partenariats");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir la liste des partenariats : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void preRemplirFormulaire(Partenariat partenariat) {
        if (partenariat != null) {
            nom.setText(partenariat.getNom());
            description.setText(partenariat.getDescription());
            type.setText(partenariat.getType());
            statut.setValue(partenariat.getStatut());

            // Convertir Date en LocalDate
            if (partenariat.getDateDebut() != null) {
                dateDebut.setValue(partenariat.getDateDebut().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
            }

            if (partenariat.getDateFin() != null) {
                dateFin.setValue(partenariat.getDateFin().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
            }

            if (partenariat.getImage() != null && !partenariat.getImage().isEmpty()) {
                selectedImagePath = partenariat.getImage();
                imageLabel.setText(new File(selectedImagePath).getName());
            }
        }
    }
}