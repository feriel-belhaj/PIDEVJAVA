package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Candidature;
import tn.esprit.workshop.services.ServiceCandidature;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ModifierCandidatureController {

    @FXML
    private ComboBox<String> typeCollabCombo;

    @FXML
    private TextArea motivationTextArea;

    @FXML
    private DatePicker datePostulation;

    @FXML
    private Label cvLabel;

    @FXML
    private Label portfolioLabel;

    @FXML
    private Button btnMettreAJour;

    @FXML
    private Button btnFermer;

    private Candidature candidature;
    private ServiceCandidature serviceCandidature;
    private String nouveauCV;
    private String nouveauPortfolio;

    @FXML
    public void initialize() {
        serviceCandidature = new ServiceCandidature();

        // Initialiser les options du ComboBox
        typeCollabCombo.getItems().addAll(
                "Stage",
                "Permanent",
                "Temporaire",
                "Projet spécifique",
                "Sponsoring",
                "Mécénat"
        );
    }

    public void initData(Candidature candidature) {
        try {
            if (candidature == null) {
                System.err.println("ERREUR: La candidature passée à initData est null");
                throw new IllegalArgumentException("La candidature ne peut pas être null");
            }
            
            System.out.println("Initialisation des données pour la candidature ID: " + candidature.getId());
            this.candidature = candidature;

            // Remplir les champs avec les données existantes
            if (candidature.getTypeCollab() != null) {
                typeCollabCombo.setValue(candidature.getTypeCollab());
                System.out.println("Type de collaboration défini: " + candidature.getTypeCollab());
            } else {
                System.out.println("Type de collaboration est null, aucune valeur définie");
            }
            
            if (candidature.getMotivation() != null) {
                motivationTextArea.setText(candidature.getMotivation());
                System.out.println("Motivation définie, longueur: " + candidature.getMotivation().length());
            } else {
                motivationTextArea.setText("");
                System.out.println("Motivation est null, champ vide défini");
            }

            // Convertir Date en LocalDate pour DatePicker
            try {
                if (candidature.getDatePostulation() != null) {
                    LocalDate dateLocale = candidature.getDatePostulation().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    datePostulation.setValue(dateLocale);
                    System.out.println("Date de postulation définie: " + dateLocale);
                } else {
                    datePostulation.setValue(LocalDate.now());
                    System.out.println("Date de postulation est null, aujourd'hui défini par défaut");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date: " + e.getMessage());
                datePostulation.setValue(LocalDate.now());
                System.out.println("Date définie à aujourd'hui suite à une erreur");
            }

            // Afficher les noms de fichiers existants
            if (candidature.getCv() != null && !candidature.getCv().isEmpty()) {
                try {
                    File cvFile = new File(candidature.getCv());
                    cvLabel.setText(cvFile.getName());
                    nouveauCV = candidature.getCv();
                    System.out.println("CV défini: " + cvFile.getName());
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès au fichier CV: " + e.getMessage());
                    cvLabel.setText("Fichier non accessible");
                }
            } else {
                System.out.println("Aucun CV n'est défini");
            }

            if (candidature.getPortfolio() != null && !candidature.getPortfolio().isEmpty()) {
                try {
                    File portfolioFile = new File(candidature.getPortfolio());
                    portfolioLabel.setText(portfolioFile.getName());
                    nouveauPortfolio = candidature.getPortfolio();
                    System.out.println("Portfolio défini: " + portfolioFile.getName());
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès au fichier portfolio: " + e.getMessage());
                    portfolioLabel.setText("Fichier non accessible");
                }
            } else {
                System.out.println("Aucun portfolio n'est défini");
            }
            
            System.out.println("Initialisation des données terminée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des données: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation: " + e.getMessage(), e);
        }
    }

    @FXML
    private void choisirCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre CV");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx")
        );

        Stage stage = (Stage) btnMettreAJour.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            nouveauCV = selectedFile.getAbsolutePath();
            cvLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void choisirPortfolio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre Portfolio");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx"),
                new FileChooser.ExtensionFilter("Fichiers Compressés", "*.zip", "*.rar")
        );

        Stage stage = (Stage) btnMettreAJour.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            nouveauPortfolio = selectedFile.getAbsolutePath();
            portfolioLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void voirCV() {
        if (candidature != null && candidature.getCv() != null && !candidature.getCv().isEmpty()) {
            ouvrirFichier(candidature.getCv());
        } else {
            afficherErreur("Erreur", "Aucun CV n'est disponible");
        }
    }

    @FXML
    private void voirPortfolio() {
        if (candidature != null && candidature.getPortfolio() != null && !candidature.getPortfolio().isEmpty()) {
            ouvrirFichier(candidature.getPortfolio());
        } else {
            afficherErreur("Erreur", "Aucun portfolio n'est disponible");
        }
    }

    @FXML
    private void mettreAJour() {
        // Vérification des champs obligatoires
        if (typeCollabCombo.getValue() == null ||
                motivationTextArea.getText().isEmpty() ||
                datePostulation.getValue() == null) {

            afficherErreur("Erreur de validation", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            // Mettre à jour les données de la candidature
            candidature.setTypeCollab(typeCollabCombo.getValue());
            candidature.setMotivation(motivationTextArea.getText());

            // Convertir LocalDate en Date
            Date datePost = Date.from(datePostulation.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            candidature.setDatePostulation(datePost);

            // Mettre à jour les fichiers si nécessaire
            if (nouveauCV != null) {
                candidature.setCv(nouveauCV);
            }

            if (nouveauPortfolio != null) {
                candidature.setPortfolio(nouveauPortfolio);
            }

            // Enregistrer les modifications
            serviceCandidature.update(candidature);

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("La candidature a été mise à jour avec succès !");
            successAlert.showAndWait();

            // Rediriger vers la liste des candidatures
            ouvrirListeCandidatures();

        } catch (SQLException e) {
            afficherErreur("Erreur", "Impossible de mettre à jour la candidature: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void fermer() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }

    private void ouvrirListeCandidatures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCandidatures.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Candidatures");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) btnMettreAJour.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la liste des candidatures: " + e.getMessage());
        }
    }

    private void ouvrirFichier(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                afficherErreur("Fichier introuvable", "Le fichier n'existe pas: " + filePath);
            }
        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le fichier: " + e.getMessage());
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Méthode statique pour créer une interface de modification sans utiliser FXML
     * @param candidature La candidature à modifier
     * @param onCloseCallback Callback appelé à la fermeture (pour rafraîchir la liste)
     */
    public static void creerInterface(Candidature candidature, Runnable onCloseCallback) {
        try {
            // Création d'une interface complète en code pur JavaFX
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #F3F4F6;");
            
            // En-tête avec bouton de fermeture
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_RIGHT);
            header.setPadding(new Insets(5, 10, 5, 10));
            Button btnFermer = new Button("✕");
            btnFermer.setStyle("-fx-background-color: transparent;");
            header.getChildren().add(btnFermer);
            
            // Titre
            Label titleLabel = new Label("Mise à Jour de la Candidature");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            titleLabel.setAlignment(Pos.CENTER);
            titleLabel.setMaxWidth(Double.MAX_VALUE);
            
            // Contenu du formulaire
            VBox formBox = new VBox(15);
            formBox.setPadding(new Insets(20, 40, 0, 40));
            
            // Type Collaboration
            VBox typeBox = new VBox(5);
            typeBox.getChildren().add(new Label("Type De Collaboration"));
            ComboBox<String> typeCollabCombo = new ComboBox<>();
            typeCollabCombo.getItems().addAll(
                    "Stage", "Permanent", "Temporaire", 
                    "Projet spécifique", "Sponsoring", "Mécénat"
            );
            typeCollabCombo.setValue(candidature.getTypeCollab());
            typeCollabCombo.setPrefWidth(250);
            typeBox.getChildren().add(typeCollabCombo);
            
            // Motivation
            VBox motivationBox = new VBox(5);
            motivationBox.getChildren().add(new Label("Motivation"));
            TextArea motivationTextArea = new TextArea(candidature.getMotivation());
            motivationTextArea.setPrefHeight(100);
            motivationTextArea.setWrapText(true);
            motivationBox.getChildren().add(motivationTextArea);
            
            // Date
            VBox dateBox = new VBox(5);
            dateBox.getChildren().add(new Label("Date postulation"));
            DatePicker datePostulation = new DatePicker();
            if (candidature.getDatePostulation() != null) {
                datePostulation.setValue(candidature.getDatePostulation().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
            } else {
                datePostulation.setValue(LocalDate.now());
            }
            datePostulation.setPrefWidth(250);
            dateBox.getChildren().add(datePostulation);
            
            // CV
            VBox cvBox = new VBox(5);
            cvBox.getChildren().add(new Label("Mettre Votre CV"));
            
            HBox cvFileBox = new HBox(10);
            Button choisirCVBtn = new Button("Choisir un fichier");
            Label cvLabel = new Label("Aucun fichier n'a été sélectionné");
            if (candidature.getCv() != null && !candidature.getCv().isEmpty()) {
                File cvFile = new File(candidature.getCv());
                cvLabel.setText(cvFile.getName());
            }
            cvFileBox.getChildren().addAll(choisirCVBtn, cvLabel);
            
            Button voirCVBtn = new Button("Voir le CV actuel");
            voirCVBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: blue;");
            cvBox.getChildren().addAll(cvFileBox, voirCVBtn);
            
            // Portfolio
            VBox portfolioBox = new VBox(5);
            portfolioBox.getChildren().add(new Label("Portfolio"));
            
            HBox portfolioFileBox = new HBox(10);
            Button choisirPortfolioBtn = new Button("Choisir un fichier");
            Label portfolioLabel = new Label("Aucun fichier n'a été sélectionné");
            if (candidature.getPortfolio() != null && !candidature.getPortfolio().isEmpty()) {
                File portfolioFile = new File(candidature.getPortfolio());
                portfolioLabel.setText(portfolioFile.getName());
            }
            portfolioFileBox.getChildren().addAll(choisirPortfolioBtn, portfolioLabel);
            
            Button voirPortfolioBtn = new Button("Voir le Portfolio actuel");
            voirPortfolioBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: blue;");
            portfolioBox.getChildren().addAll(portfolioFileBox, voirPortfolioBtn);
            
            // Bouton de mise à jour
            HBox updateBtnBox = new HBox();
            updateBtnBox.setAlignment(Pos.CENTER);
            updateBtnBox.setPadding(new Insets(20, 0, 0, 0));
            Button btnMettreAJour = new Button("Mettre à jour");
            btnMettreAJour.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
            btnMettreAJour.setPadding(new Insets(10, 30, 10, 30));
            updateBtnBox.getChildren().add(btnMettreAJour);
            
            // Ajout de tous les éléments au formulaire
            formBox.getChildren().addAll(
                    typeBox, motivationBox, dateBox, cvBox, portfolioBox, updateBtnBox
            );
            
            // Assemblage de l'interface complète
            root.getChildren().addAll(header, titleLabel, formBox);
            
            // Création de la scène et de la fenêtre
            Scene scene = new Scene(root, 700, 600);
            Stage stage = new Stage();
            stage.setTitle("Modifier une Candidature");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Variables pour stocker les chemins des fichiers
            String[] nouveauCV = {candidature.getCv()};
            String[] nouveauPortfolio = {candidature.getPortfolio()};
            
            // Gestion des actions
            
            // Fermeture
            btnFermer.setOnAction(e -> stage.close());
            
            // Sélection du CV
            choisirCVBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir votre CV");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx")
                );
                
                File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null) {
                    nouveauCV[0] = selectedFile.getAbsolutePath();
                    cvLabel.setText(selectedFile.getName());
                }
            });
            
            // Sélection du portfolio
            choisirPortfolioBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir votre Portfolio");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx"),
                        new FileChooser.ExtensionFilter("Fichiers Compressés", "*.zip", "*.rar")
                );
                
                File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null) {
                    nouveauPortfolio[0] = selectedFile.getAbsolutePath();
                    portfolioLabel.setText(selectedFile.getName());
                }
            });
            
            // Voir CV
            voirCVBtn.setOnAction(e -> {
                if (candidature.getCv() != null && !candidature.getCv().isEmpty()) {
                    try {
                        File file = new File(candidature.getCv());
                        if (file.exists()) {
                            Desktop.getDesktop().open(file);
                        } else {
                            showError(stage, "Fichier introuvable", "Le fichier n'existe pas: " + candidature.getCv());
                        }
                    } catch (IOException ex) {
                        showError(stage, "Erreur", "Impossible d'ouvrir le fichier: " + ex.getMessage());
                    }
                } else {
                    showError(stage, "Erreur", "Aucun CV n'est disponible");
                }
            });
            
            // Voir Portfolio
            voirPortfolioBtn.setOnAction(e -> {
                if (candidature.getPortfolio() != null && !candidature.getPortfolio().isEmpty()) {
                    try {
                        File file = new File(candidature.getPortfolio());
                        if (file.exists()) {
                            Desktop.getDesktop().open(file);
                        } else {
                            showError(stage, "Fichier introuvable", "Le fichier n'existe pas: " + candidature.getPortfolio());
                        }
                    } catch (IOException ex) {
                        showError(stage, "Erreur", "Impossible d'ouvrir le fichier: " + ex.getMessage());
                    }
                } else {
                    showError(stage, "Erreur", "Aucun portfolio n'est disponible");
                }
            });
            
            // Mise à jour
            btnMettreAJour.setOnAction(e -> {
                // Vérification des champs obligatoires
                if (typeCollabCombo.getValue() == null ||
                        motivationTextArea.getText().isEmpty() ||
                        datePostulation.getValue() == null) {
                    
                    showError(stage, "Erreur de validation", "Veuillez remplir tous les champs obligatoires");
                    return;
                }
                
                try {
                    // Services pour la mise à jour
                    ServiceCandidature serviceCandidature = new ServiceCandidature();
                    
                    // Mettre à jour les données de la candidature
                    candidature.setTypeCollab(typeCollabCombo.getValue());
                    candidature.setMotivation(motivationTextArea.getText());
                    
                    // Convertir LocalDate en Date
                    Date datePost = Date.from(datePostulation.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    candidature.setDatePostulation(datePost);
                    
                    // Mettre à jour les fichiers si nécessaire
                    if (nouveauCV[0] != null) {
                        candidature.setCv(nouveauCV[0]);
                    }
                    
                    if (nouveauPortfolio[0] != null) {
                        candidature.setPortfolio(nouveauPortfolio[0]);
                    }
                    
                    // Sauvegarder les modifications
                    serviceCandidature.update(candidature);
                    
                    // Afficher un message de succès
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("La candidature a été mise à jour avec succès !");
                    successAlert.showAndWait();
                    
                    // Fermer la fenêtre
                    stage.close();
                    
                } catch (SQLException ex) {
                    showError(stage, "Erreur", "Impossible de mettre à jour la candidature: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            
            // Rafraîchir la liste à la fermeture
            stage.setOnHidden(e -> {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            });
            
            // Afficher la fenêtre
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de créer l'interface de modification: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private static void showError(Stage parent, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(parent);
        alert.showAndWait();
    }
}