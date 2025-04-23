package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Partenariat;
import tn.esprit.workshop.services.ServicePartenariat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class DetailsPartenariatController {

    @FXML
    private Label nomLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private TextArea descriptionLabel;

    @FXML
    private Label dateDebutLabel;

    @FXML
    private Label dateFinLabel;

    @FXML
    private Label statutLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private Button btnPostuler;

    @FXML
    private Button btnSupprimer;

    private Partenariat partenariat;
    private ServicePartenariat servicePartenariat;
    private Runnable onDeleteCallback; // Callback pour après suppression

    @FXML
    public void initialize() {
        servicePartenariat = new ServicePartenariat();
    }

    // Méthode pour définir le callback
    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }

    public void initData(Partenariat partenariat) {
        this.partenariat = partenariat;

        // Mettre à jour l'interface avec les données du partenariat
        nomLabel.setText(partenariat.getNom());
        typeLabel.setText(partenariat.getType());
        descriptionLabel.setText(partenariat.getDescription());
        statutLabel.setText(partenariat.getStatut());

        // Mettre à jour les dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateDebutStr = partenariat.getDateDebut() != null ? dateFormat.format(partenariat.getDateDebut()) : "N/A";
        String dateFinStr = partenariat.getDateFin() != null ? dateFormat.format(partenariat.getDateFin()) : "N/A";
        dateDebutLabel.setText(dateDebutStr);
        dateFinLabel.setText(dateFinStr);

        // Colorer le statut en fonction de sa valeur
        String styleStatut;
        switch (partenariat.getStatut()) {
            case "EnCours":
                styleStatut = "-fx-background-color: #FFD700; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-weight: bold;";
                break;
            case "Expiré":
                styleStatut = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-weight: bold;";
                btnPostuler.setDisable(true); // Désactiver le bouton postuler si expiré
                break;
            case "À venir":
                styleStatut = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-weight: bold;";
                break;
            default:
                styleStatut = "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-weight: bold;";
        }
        statutLabel.setStyle(styleStatut);

        // Charger l'image du partenariat depuis C:\xampp\htdocs\img
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

        // Configurer le bouton de suppression
        btnSupprimer.setOnAction(event -> {
            // Demander confirmation avant suppression
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le partenariat");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce partenariat ? Cette action est irréversible.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Supprimer le partenariat
                    servicePartenariat.delete(partenariat);

                    // Afficher un message de succès
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Le partenariat a été supprimé avec succès!");
                    successAlert.showAndWait();

                    // Fermer la fenêtre de détails
                    Stage currentStage = (Stage) btnSupprimer.getScene().getWindow();
                    currentStage.close();

                    // Exécuter le callback après suppression
                    if (onDeleteCallback != null) {
                        onDeleteCallback.run();
                    }

                } catch (SQLException e) {
                    afficherErreur("Erreur", "Impossible de supprimer le partenariat: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void postuler() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PostulerPartenariat.fxml"));
            Parent root = loader.load();

            PostulerPartenariatController controller = loader.getController();
            controller.initData(partenariat);

            // Fermer la fenêtre de détails
            Stage currentStage = (Stage) btnPostuler.getScene().getWindow();
            currentStage.close();

            // Ouvrir la fenêtre de postulation
            Stage stage = new Stage();
            stage.setTitle("Postuler pour un partenariat");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la fenêtre de postulation: " + e.getMessage());
            e.printStackTrace();
        }
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
            afficherErreur("Erreur", "Impossible d'ouvrir la liste des partenariats: " + e.getMessage());
            e.printStackTrace();
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