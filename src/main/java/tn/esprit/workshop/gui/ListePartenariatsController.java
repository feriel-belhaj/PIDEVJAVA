package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServicePartenariat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ListePartenariatsController {

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private TextField searchField;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private Button btnRechercher;

    @FXML
    private Button btnAjouter;

    private ServicePartenariat servicePartenariat;
    private int currentPage = 1;
    private final int itemsPerPage = 4;
    private int totalPages = 1;

    // Pagination controls
    private Button btnPrevious;
    private Button btnNext;
    private Label pageLabel;

    @FXML
    public void initialize() {
        servicePartenariat = new ServicePartenariat();
        // Initialize pagination controls
        setupPaginationControls();
        chargerPartenariats();
    }

    private void setupPaginationControls() {
        HBox paginationBox = new HBox(10);
        paginationBox.setAlignment(Pos.CENTER);
        paginationBox.setStyle("-fx-padding: 10;");

        btnPrevious = new Button("←");
        btnPrevious.setStyle("-fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 8 20;");
        btnPrevious.setOnAction(e -> goToPreviousPage());
        btnPrevious.setDisable(true);

        pageLabel = new Label("Page 1");
        pageLabel.setStyle("-fx-font-size: 14px;");

        btnNext = new Button("→");
        btnNext.setStyle("-fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 8 20;");
        btnNext.setOnAction(e -> goToNextPage());

        paginationBox.getChildren().addAll(btnPrevious, pageLabel, btnNext);

        // Add pagination controls to the bottom of cardsContainer's parent
        VBox parentContainer = (VBox) cardsContainer.getParent();
        parentContainer.getChildren().add(paginationBox);
    }

    private void chargerPartenariats() {
        try {
            List<Partenariat> partenariats = servicePartenariat.showAll();
            updatePagination(partenariats);
        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des partenariats", e.getMessage());
        }
    }

    private void updatePagination(List<Partenariat> partenariats) {
        cardsContainer.getChildren().clear();
        totalPages = (int) Math.ceil((double) partenariats.size() / itemsPerPage);
        totalPages = Math.max(1, totalPages); // Ensure at least 1 page

        // Ensure currentPage is within bounds
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        // Calculate start and end indices
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, partenariats.size());

        // Display partnerships for the current page
        for (int i = startIndex; i < endIndex; i++) {
            VBox card = creerCartePartenariat(partenariats.get(i));
            cardsContainer.getChildren().add(card);
        }

        // Update pagination controls
        pageLabel.setText("Page " + currentPage + " / " + totalPages);
        btnPrevious.setDisable(currentPage <= 1);
        btnNext.setDisable(currentPage >= totalPages);

        if (partenariats.isEmpty()) {
            afficherMessage("Information", "Aucun partenariat disponible.");
        }
    }

    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            chargerPartenariats();
        }
    }

    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            chargerPartenariats();
        }
    }

    private VBox creerCartePartenariat(Partenariat partenariat) {
        VBox card = new VBox();
        card.setPrefWidth(300.0);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-background-radius: 10;");
        card.setCursor(Cursor.HAND);

        StackPane imageContainer = new StackPane();
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300.0);
        imageView.setFitHeight(200.0);
        imageView.setPreserveRatio(false);

        String imagePath = partenariat.getImage();
        String basePath = "C:\\xampp\\htdocs\\img";
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(basePath, imagePath);
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
        }

        Label statutOverlay = new Label(partenariat.getStatut());
        String styleStatut;
        switch (partenariat.getStatut()) {
            case "EnCours":
                styleStatut = "-fx-background-color: #FFD700; -fx-text-fill: white;";
                break;
            case "Expiré":
                styleStatut = "-fx-background-color: #e74c3c; -fx-text-fill: white;";
                break;
            case "À venir":
                styleStatut = "-fx-background-color: #3498db; -fx-text-fill: white;";
                break;
            default:
                styleStatut = "-fx-background-color: #95a5a6; -fx-text-fill: white;";
        }
        statutOverlay.setStyle(styleStatut + " -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-weight: bold;");
        statutOverlay.setMaxHeight(30);
        StackPane.setAlignment(statutOverlay, Pos.TOP_RIGHT);
        StackPane.setMargin(statutOverlay, new javafx.geometry.Insets(10, 10, 0, 0));
        imageContainer.getChildren().addAll(imageView, statutOverlay);

        VBox contenu = new VBox(10);
        contenu.setStyle("-fx-padding: 20;");

        Label nomLabel = new Label(partenariat.getNom());
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label typeLabel = new Label("Type: " + partenariat.getType());
        typeLabel.setStyle("-fx-text-fill: #666666;");

        Label descriptionLabel = new Label(partenariat.getDescription());
        descriptionLabel.setStyle("-fx-text-fill: #666666;");
        descriptionLabel.setWrapText(true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateDebutStr = partenariat.getDateDebut() != null ? dateFormat.format(partenariat.getDateDebut()) : "N/A";
        String dateFinStr = partenariat.getDateFin() != null ? dateFormat.format(partenariat.getDateFin()) : "N/A";

        Label dateDebutLabel = new Label("Début: " + dateDebutStr);
        dateDebutLabel.setStyle("-fx-text-fill: #666666;");

        Label dateFinLabel = new Label("Fin: " + dateFinStr);
        dateFinLabel.setStyle("-fx-text-fill: #666666;");

        Button btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-background-color: #D4A76A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 20;");
        btnModifier.setOnAction(e -> {
            e.consume();
            modifierPartenariat(partenariat);
        });

        contenu.getChildren().addAll(nomLabel, typeLabel, descriptionLabel, dateDebutLabel, dateFinLabel, btnModifier);
        card.getChildren().addAll(imageContainer, contenu);
        card.setOnMouseClicked(e -> ouvrirDetailsPartenariat(partenariat));

        return card;
    }

    private void ouvrirDetailsPartenariat(Partenariat partenariat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsPartenariat.fxml"));
            Parent root = loader.load();
            DetailsPartenariatController controller = loader.getController();
            controller.initData(partenariat);
            controller.setOnDeleteCallback(this::chargerPartenariats);
            Stage stage = new Stage();
            stage.setTitle("Détails du Partenariat");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre de détails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void ajouterPartenariat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPartenariat.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Partenariat");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(e -> {
                currentPage = 1; // Reset to first page after adding
                chargerPartenariats();
            });
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre d'ajout de partenariat");
        }
    }

    @FXML
    void rechercher() {
        // Récupérer les dates saisies
        LocalDate dateDebutLocal = dateDebutPicker.getValue();
        LocalDate dateFinLocal = dateFinPicker.getValue();

        // Validation des dates
        if (dateDebutLocal != null && dateFinLocal != null && dateDebutLocal.isAfter(dateFinLocal)) {
            afficherErreur("Erreur", "La date de début doit être antérieure ou égale à la date de fin.");
            cardsContainer.getChildren().clear();
            afficherMessage("Information", "Aucun partenariat trouvé dans cet intervalle de dates.");
            // Vider les champs DatePicker
            dateDebutPicker.setValue(null);
            dateFinPicker.setValue(null);
            return;
        }

        // Convertir les LocalDate en java.util.Date
        Date dateDebut = dateDebutLocal != null ? java.sql.Date.valueOf(dateDebutLocal) : null;
        Date dateFin = dateFinLocal != null ? java.sql.Date.valueOf(dateFinLocal) : null;

        // Logs pour déboguer les dates saisies
        System.out.println("Date de début recherchée : " + dateDebut);
        System.out.println("Date de fin recherchée : " + dateFin);

        try {
            // Appeler searchPartenariats (searchText is null since the field was removed from FXML)
            List<Partenariat> partenariats = servicePartenariat.searchPartenariats(null, dateDebut, dateFin);
            currentPage = 1; // Reset to first page after search
            updatePagination(partenariats);
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la recherche", e.getMessage());
        } finally {
            // Vider les champs DatePicker après la recherche
            dateDebutPicker.setValue(null);
            dateFinPicker.setValue(null);
        }
    }

    private void modifierPartenariat(Partenariat partenariat) {
        try {
            if (partenariat == null) {
                afficherErreur("Erreur", "Le partenariat sélectionné est invalide");
                return;
            }
            System.out.println("Modification du partenariat: ID=" + partenariat.getId() + ", Nom=" + partenariat.getNom());
            ModifierPartenariatController controller = new ModifierPartenariatController(partenariat, aVoid -> {
                chargerPartenariats();
            });
            controller.afficher();
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

    private void afficherMessage(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}