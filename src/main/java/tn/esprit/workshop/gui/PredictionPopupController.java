package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.workshop.services.PredictionService.PredictionResult;

public class PredictionPopupController {
    @FXML private ProgressBar scoreProgressBar;
    @FXML private Label scoreLabel;
    @FXML private VBox recommendationsBox;
    @FXML private VBox risquesBox;

    public void setPredictionResult(PredictionResult result) {
        // Afficher le score
        double score = result.getScore();
        scoreProgressBar.setProgress(score);
        scoreLabel.setText(String.format("%.0f%%", score * 100));

        // Appliquer la couleur en fonction du score
        String color;
        if (score < 0.5) {
            color = "#ff4444"; // Rouge pour score faible
        } else if (score < 0.8) {
            color = "#ffbb33"; // Orange pour score moyen
        } else {
            color = "#00C851"; // Vert pour bon score
        }
        scoreProgressBar.setStyle("-fx-accent: " + color + ";");
        scoreLabel.setStyle("-fx-text-fill: " + color + ";");

        // Afficher les recommandations
        recommendationsBox.getChildren().clear();
        for (String recommendation : result.getRecommendations()) {
            Label label = new Label("• " + recommendation);
            label.setWrapText(true);
            recommendationsBox.getChildren().add(label);
        }

        // Afficher les risques
        risquesBox.getChildren().clear();
        for (String risque : result.getRisques()) {
            Label label = new Label("• " + risque);
            label.setWrapText(true);
            risquesBox.getChildren().add(label);
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) scoreProgressBar.getScene().getWindow()).close();
    }
} 