package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @FXML
    private Label errorDatePostulation;

    @FXML
    private Label errorCv;

    @FXML
    private Label errorPortfolio;

    @FXML
    private Label errorTypeCollaboration;

    @FXML
    private Label errorMotivation;

    private Candidature candidature;
    private ServiceCandidature serviceCandidature;
    private String nouveauCV;
    private String nouveauPortfolio;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB en octets
    private static final List<String> ALLOWED_CV_EXTENSIONS = Arrays.asList(".pdf", ".doc", ".docx");
    private static final List<String> ALLOWED_PORTFOLIO_EXTENSIONS = Arrays.asList(".png", ".jpeg", ".jpg");

    @FXML
    public void initialize() {
        serviceCandidature = new ServiceCandidature();

        // Initialiser les options du ComboBox
        typeCollabCombo.setItems(FXCollections.observableArrayList(
                "Stage", "Sponsoring", "Atelier Collaboratif"
        ));
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
            }

            if (candidature.getMotivation() != null) {
                motivationTextArea.setText(candidature.getMotivation());
                System.out.println("Motivation définie, longueur: " + candidature.getMotivation().length());
            }

            // Convertir Date en LocalDate pour DatePicker
            try {
                if (candidature.getDatePostulation() != null) {
                    LocalDate dateLocale = candidature.getDatePostulation().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    datePostulation.setValue(dateLocale);
                    System.out.println("Date de postulation définie: " + dateLocale);
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
            // Vérifier la taille du fichier
            if (selectedFile.length() > MAX_FILE_SIZE) {
                cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorCv.setText("Le fichier CV ne doit pas dépasser 5 Mo.");
                errorCv.setVisible(true);
                return;
            }

            // Vérifier l'extension du fichier
            String fileName = selectedFile.getName().toLowerCase();
            boolean isValidExtension = ALLOWED_CV_EXTENSIONS.stream().anyMatch(fileName::endsWith);
            if (!isValidExtension) {
                cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorCv.setText("Le CV doit être un fichier PDF, DOC ou DOCX.");
                errorCv.setVisible(true);
                return;
            }

            nouveauCV = selectedFile.getAbsolutePath();
            cvLabel.setText(selectedFile.getName());
            cvLabel.setTextFill(javafx.scene.paint.Color.BLACK);
            errorCv.setVisible(false);
        }
    }

    @FXML
    private void choisirPortfolio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre Portfolio");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpeg", "*.jpg")
        );

        Stage stage = (Stage) btnMettreAJour.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Vérifier la taille du fichier
            if (selectedFile.length() > MAX_FILE_SIZE) {
                portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorPortfolio.setText("Le fichier portfolio ne doit pas dépasser 5 Mo.");
                errorPortfolio.setVisible(true);
                return;
            }

            // Vérifier l'extension du fichier
            String fileName = selectedFile.getName().toLowerCase();
            boolean isValidExtension = ALLOWED_PORTFOLIO_EXTENSIONS.stream().anyMatch(fileName::endsWith);
            if (!isValidExtension) {
                portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorPortfolio.setText("Le portfolio doit être une image PNG, JPEG ou JPG.");
                errorPortfolio.setVisible(true);
                return;
            }

            nouveauPortfolio = selectedFile.getAbsolutePath();
            portfolioLabel.setText(selectedFile.getName());
            portfolioLabel.setTextFill(javafx.scene.paint.Color.BLACK);
            errorPortfolio.setVisible(false);
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
        // Réinitialiser les styles et les erreurs
        resetStyle(datePostulation, typeCollabCombo, motivationTextArea);
        resetErrors();
        cvLabel.setTextFill(javafx.scene.paint.Color.BLACK);
        portfolioLabel.setTextFill(javafx.scene.paint.Color.BLACK);

        boolean hasError = false;

        // Validation de la lettre de motivation (si non vide)
        if (!motivationTextArea.getText().isEmpty() && motivationTextArea.getText().length() < 10) {
            motivationTextArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            errorMotivation.setText("La lettre de motivation doit contenir au moins 10 caractères.");
            errorMotivation.setVisible(true);
            hasError = true;
        }

        // Validation du CV si modifié
        if (nouveauCV != null && !nouveauCV.isEmpty()) {
            File cvFile = new File(nouveauCV);
            if (cvFile.length() > MAX_FILE_SIZE) {
                cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorCv.setText("Le fichier CV ne doit pas dépasser 5 Mo.");
                errorCv.setVisible(true);
                hasError = true;
            } else {
                String fileName = cvFile.getName().toLowerCase();
                boolean isValidExtension = ALLOWED_CV_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                if (!isValidExtension) {
                    cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                    errorCv.setText("Le CV doit être un fichier PDF, DOC ou DOCX.");
                    errorCv.setVisible(true);
                    hasError = true;
                }
            }
        }

        // Validation du portfolio si modifié
        if (nouveauPortfolio != null && !nouveauPortfolio.isEmpty()) {
            File portfolioFile = new File(nouveauPortfolio);
            if (portfolioFile.length() > MAX_FILE_SIZE) {
                portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                errorPortfolio.setText("Le fichier portfolio ne doit pas dépasser 5 Mo.");
                errorPortfolio.setVisible(true);
                hasError = true;
            } else {
                String fileName = portfolioFile.getName().toLowerCase();
                boolean isValidExtension = ALLOWED_PORTFOLIO_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                if (!isValidExtension) {
                    portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                    errorPortfolio.setText("Le portfolio doit être une image PNG, JPEG ou JPG.");
                    errorPortfolio.setVisible(true);
                    hasError = true;
                }
            }
        }

        // Afficher une alerte si des erreurs sont détectées
        if (hasError) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez corriger les erreurs en rouge !");
            alert.showAndWait();
            return;
        }

        try {
            // Mettre à jour les données de la candidature si elles sont fournies
            if (typeCollabCombo.getValue() != null) {
                candidature.setTypeCollab(typeCollabCombo.getValue());
            }
            if (!motivationTextArea.getText().isEmpty()) {
                candidature.setMotivation(motivationTextArea.getText());
            }
            if (datePostulation.getValue() != null) {
                Date datePost = Date.from(datePostulation.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                candidature.setDatePostulation(datePost);
            }
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

            // Fermer la fenêtre
            Stage stage = (Stage) btnMettreAJour.getScene().getWindow();
            stage.close();

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

    private void resetErrors() {
        errorDatePostulation.setVisible(false);
        errorCv.setVisible(false);
        errorPortfolio.setVisible(false);
        errorTypeCollaboration.setVisible(false);
        errorMotivation.setVisible(false);
    }

    private void resetStyle(Control... controls) {
        for (Control control : controls) {
            control.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        }
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

            // ScrollPane pour le contenu défilant
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            // Contenu du ScrollPane
            VBox content = new VBox(15);
            content.setPadding(new Insets(10, 20, 20, 20));

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
            typeCollabCombo.getItems().addAll("Stage", "Sponsoring", "Atelier Collaboratif");
            typeCollabCombo.setValue(candidature.getTypeCollab());
            typeCollabCombo.setPrefWidth(250);
            Label errorTypeCollaboration = new Label("");
            errorTypeCollaboration.setStyle("-fx-text-fill: red;");
            errorTypeCollaboration.setVisible(false);
            typeBox.getChildren().addAll(typeCollabCombo, errorTypeCollaboration);

            // Motivation
            VBox motivationBox = new VBox(5);
            motivationBox.getChildren().add(new Label("Motivation"));
            TextArea motivationTextArea = new TextArea(candidature.getMotivation());
            motivationTextArea.setPrefHeight(100);
            motivationTextArea.setWrapText(true);
            Label errorMotivation = new Label("");
            errorMotivation.setStyle("-fx-text-fill: red;");
            errorMotivation.setVisible(false);
            motivationBox.getChildren().addAll(motivationTextArea, errorMotivation);

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
            Label errorDatePostulation = new Label("");
            errorDatePostulation.setStyle("-fx-text-fill: red;");
            errorDatePostulation.setVisible(false);
            dateBox.getChildren().addAll(datePostulation, errorDatePostulation);

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

            Label errorCv = new Label("");
            errorCv.setStyle("-fx-text-fill: red;");
            errorCv.setVisible(false);

            Button voirCVBtn = new Button("Voir le CV actuel");
            voirCVBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: blue;");
            cvBox.getChildren().addAll(cvFileBox, errorCv, voirCVBtn);

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

            Label errorPortfolio = new Label("");
            errorPortfolio.setStyle("-fx-text-fill: red;");
            errorPortfolio.setVisible(false);

            Button voirPortfolioBtn = new Button("Voir le Portfolio actuel");
            voirPortfolioBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: blue;");
            portfolioBox.getChildren().addAll(portfolioFileBox, errorPortfolio, voirPortfolioBtn);

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

            // Ajout au contenu du ScrollPane
            content.getChildren().addAll(titleLabel, formBox);
            scrollPane.setContent(content);

            // Assemblage de l'interface complète
            root.getChildren().addAll(header, scrollPane);

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
                    // Vérifier la taille du fichier
                    if (selectedFile.length() > MAX_FILE_SIZE) {
                        cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorCv.setText("Le fichier CV ne doit pas dépasser 5 Mo.");
                        errorCv.setVisible(true);
                        return;
                    }

                    // Vérifier l'extension du fichier
                    String fileName = selectedFile.getName().toLowerCase();
                    boolean isValidExtension = ALLOWED_CV_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                    if (!isValidExtension) {
                        cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorCv.setText("Le CV doit être un fichier PDF, DOC ou DOCX.");
                        errorCv.setVisible(true);
                        return;
                    }

                    nouveauCV[0] = selectedFile.getAbsolutePath();
                    cvLabel.setText(selectedFile.getName());
                    cvLabel.setTextFill(javafx.scene.paint.Color.BLACK);
                    errorCv.setVisible(false);
                }
            });

            // Sélection du portfolio
            choisirPortfolioBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir votre Portfolio");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpeg", "*.jpg")
                );

                File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null) {
                    // Vérifier la taille du fichier
                    if (selectedFile.length() > MAX_FILE_SIZE) {
                        portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorPortfolio.setText("Le fichier portfolio ne doit pas dépasser 5 Mo.");
                        errorPortfolio.setVisible(true);
                        return;
                    }

                    // Vérifier l'extension du fichier
                    String fileName = selectedFile.getName().toLowerCase();
                    boolean isValidExtension = ALLOWED_PORTFOLIO_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                    if (!isValidExtension) {
                        portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorPortfolio.setText("Le portfolio doit être une image PNG, JPEG ou JPG.");
                        errorPortfolio.setVisible(true);
                        return;
                    }

                    nouveauPortfolio[0] = selectedFile.getAbsolutePath();
                    portfolioLabel.setText(selectedFile.getName());
                    portfolioLabel.setTextFill(javafx.scene.paint.Color.BLACK);
                    errorPortfolio.setVisible(false);
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
                // Réinitialiser les styles et les erreurs
                typeCollabCombo.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
                motivationTextArea.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
                datePostulation.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
                errorDatePostulation.setVisible(false);
                errorCv.setVisible(false);
                errorPortfolio.setVisible(false);
                errorTypeCollaboration.setVisible(false);
                errorMotivation.setVisible(false);
                cvLabel.setTextFill(javafx.scene.paint.Color.BLACK);
                portfolioLabel.setTextFill(javafx.scene.paint.Color.BLACK);

                boolean hasError = false;

                // Validation de la lettre de motivation (si non vide)
                if (!motivationTextArea.getText().isEmpty() && motivationTextArea.getText().length() < 10) {
                    motivationTextArea.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
                    errorMotivation.setText("La lettre de motivation doit contenir au moins 10 caractères.");
                    errorMotivation.setVisible(true);
                    hasError = true;
                }

                // Validation du CV si modifié
                if (nouveauCV[0] != null && !nouveauCV[0].isEmpty()) {
                    File cvFile = new File(nouveauCV[0]);
                    if (cvFile.length() > MAX_FILE_SIZE) {
                        cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorCv.setText("Le fichier CV ne doit pas dépasser 5 Mo.");
                        errorCv.setVisible(true);
                        hasError = true;
                    } else {
                        String fileName = cvFile.getName().toLowerCase();
                        boolean isValidExtension = ALLOWED_CV_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                        if (!isValidExtension) {
                            cvLabel.setTextFill(javafx.scene.paint.Color.RED);
                            errorCv.setText("Le CV doit être un fichier PDF, DOC ou DOCX.");
                            errorCv.setVisible(true);
                            hasError = true;
                        }
                    }
                }

                // Validation du portfolio si modifié
                if (nouveauPortfolio[0] != null && !nouveauPortfolio[0].isEmpty()) {
                    File portfolioFile = new File(nouveauPortfolio[0]);
                    if (portfolioFile.length() > MAX_FILE_SIZE) {
                        portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                        errorPortfolio.setText("Le fichier portfolio ne doit pas dépasser 5 Mo.");
                        errorPortfolio.setVisible(true);
                        hasError = true;
                    } else {
                        String fileName = portfolioFile.getName().toLowerCase();
                        boolean isValidExtension = ALLOWED_PORTFOLIO_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                        if (!isValidExtension) {
                            portfolioLabel.setTextFill(javafx.scene.paint.Color.RED);
                            errorPortfolio.setText("Le portfolio doit être une image PNG, JPEG ou JPG.");
                            errorPortfolio.setVisible(true);
                            hasError = true;
                        }
                    }
                }

                // Afficher une alerte si des erreurs sont détectées
                if (hasError) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Erreur de validation");
                    alert.setHeaderText(null);
                    alert.setContentText("Veuillez corriger les erreurs en rouge !");
                    alert.showAndWait();
                    return;
                }

                try {
                    // Services pour la mise à jour
                    ServiceCandidature serviceCandidature = new ServiceCandidature();

                    // Mettre à jour les données de la candidature si elles sont fournies
                    if (typeCollabCombo.getValue() != null) {
                        candidature.setTypeCollab(typeCollabCombo.getValue());
                    }
                    if (!motivationTextArea.getText().isEmpty()) {
                        candidature.setMotivation(motivationTextArea.getText());
                    }
                    if (datePostulation.getValue() != null) {
                        Date datePost = Date.from(datePostulation.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        candidature.setDatePostulation(datePost);
                    }
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