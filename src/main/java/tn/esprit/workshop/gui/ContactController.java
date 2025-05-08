package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ContactController {

    // FXML-injected components from the contact form
    @FXML
    private TextField nameField; // Matches TextField with fx:id="nameField"
    @FXML
    private TextField emailField; // Matches TextField with fx:id="emailField"
    @FXML
    private TextArea messageArea; // Matches TextArea with fx:id="messageArea"
    @FXML
    private WebView chatbotWebView; // Matches WebView with fx:id="chatbotWebView"

    // Method to handle the "Envoyer" button click
    @FXML
    private void handleSendButtonAction() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String message = messageArea.getText().trim();

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez entrer un email valide.");
            return;
        }

        // Process the form data and save to file
        try {
            saveToFile(name, email, message);
            System.out.println("Nom: " + name);
            System.out.println("Email: " + email);
            System.out.println("Message: " + message);

            // Show success message
            showAlert(AlertType.INFORMATION, "Succès", "Votre message a été envoyé et enregistré !");

            // Clear the form
            clearForm();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Échec de l'enregistrement des données: " + e.getMessage());
        }
    }

    // Utility method to save form data to a .txt file in src/main/resources
    private void saveToFile(String name, String email, String message) throws IOException {
        // Specify the exact file path
        String filePath = "C:\\Users\\Fatma\\IdeaProjects\\ArtizinaJava\\src\\main\\resources\\contact_data.txt";
        File file = new File(filePath);

        // Ensure the parent directory exists
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file, true)) { // Append mode
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String entry = String.format(
                    "Date: %s\nNom: %s\nEmail: %s\nMessage: %s\n---\n",
                    timestamp, name, email, message
            );
            writer.write(entry);
        }
    }

    // Utility method to validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    // Utility method to show alerts
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Utility method to clear the form
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        messageArea.clear();
    }

}