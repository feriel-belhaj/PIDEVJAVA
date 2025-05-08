package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Message;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MessageDetailsController {

    @FXML
    private Label nameLabel;

    @FXML
    private Hyperlink emailHyperlink;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea messageArea;

    private Message message;
    private long lastEmailClickTime = 0; // For debouncing clicks

    public void setMessage(Message message) {
        this.message = message;
        nameLabel.setText(message.getName());
        emailHyperlink.setText(message.getEmail());
        dateLabel.setText(message.getDate());
        messageArea.setText(message.getMessage());
    }

    @FXML
    private void initialize() {
        emailHyperlink.setOnAction(event -> {
            // Debounce to prevent multiple rapid clicks
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEmailClickTime < 500) {
                return; // Ignore clicks within 500ms
            }
            lastEmailClickTime = currentTime;

            try {
                // Encode the subject to handle special characters (e.g., é)
                String subject = URLEncoder.encode("Réponse à votre message", StandardCharsets.UTF_8.toString());
                String mailto = "mailto:" + message.getEmail() + "?subject=" + subject;
                Desktop.getDesktop().mail(new URI(mailto));
            } catch (IOException | URISyntaxException e) {
                System.err.println("Erreur lors de l'ouverture du client de messagerie : " + e.getMessage());
            }
        });
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}