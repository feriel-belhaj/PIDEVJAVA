package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.poi.ss.formula.functions.T;
import tn.esprit.workshop.models.*;
import tn.esprit.workshop.services.DonService;
import tn.esprit.workshop.services.EvenementService;
import tn.esprit.workshop.services.ServiceCommande;
import tn.esprit.workshop.services.ServicePartenariat;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class EspacePerso implements Initializable {

    @FXML
    private FlowPane commandesContainer;

    @FXML
    private FlowPane donsContainer;

    @FXML
    private FlowPane evenementsContainer;

    @FXML
    private FlowPane formationsContainer;

    @FXML
    private FlowPane partenariatsContainer;

    @FXML
    private Label TotalDons;

    ServicePartenariat servicePart = new ServicePartenariat();
    ServiceCommande serviceCommande = new ServiceCommande();
    DonService donService = new DonService();
    private EvenementService serviceEvenement = new EvenementService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int userId = SessionManager.getUserId();
        loadPartenariats(userId);
        loadCommandes(userId);
        TotalDons.setText(String.valueOf(donService.calculerSommeDonsParUser(userId)));
        loadEvenements(userId);
    }


    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }



    private void loadPartenariats(int userId) {
        try {
            partenariatsContainer.getChildren().clear();
            List<Partenariat> partenariats = servicePart.findPartenariatsByUser(userId);
            for (Partenariat p : partenariats) {
                VBox card = createPartenariatCard(p);
                partenariatsContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert( "Erreur", "Impossible de charger vos partenariats.");
        }
    }


    private VBox createPartenariatCard(Partenariat p) {
        VBox card = new VBox(5);
        card.getStyleClass().add("formation-card"); // même style que les formations

        Label title = new Label(p.getNom());
        title.getStyleClass().add("h3");

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefWidth(300);
        imageContainer.setPrefHeight(200);

// Image de la formation
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        String imagePath = p.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {

            String fullPath = "C:\\xampp\\htdocs\\images\\" + imagePath;

            try {
                File imageFile = new File(fullPath);

                if (imageFile.exists()) {
                    // Charger l'image
                    Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true, true);

                    imageView.setImage(image);
                    imageContainer.getChildren().clear();
                    imageContainer.getChildren().add(imageView);

                    // Ajouter des listeners pour gérer le chargement
                    image.errorProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            showNoImageLabel(imageContainer);
                            System.out.println("Erreur de chargement de l'image: " + fullPath);
                        }
                    });

                    image.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0 && !imageContainer.getChildren().contains(imageView)) {
                            imageContainer.getChildren().clear();
                            imageContainer.getChildren().add(imageView);
                        }
                    });
                } else {
                    System.out.println("Image non trouvée: " + fullPath);
                    showNoImageLabel(imageContainer);
                }
            } catch (Exception e) {
                System.out.println("Exception lors du chargement de l'image: " + e.getMessage());
                e.printStackTrace();
                showNoImageLabel(imageContainer);
            }
        } else {
            showNoImageLabel(imageContainer);
        }


        Label desc = new Label(p.getDescription());
        desc.getStyleClass().add("formation-description");

        VBox infoBox = new VBox(3);
        infoBox.getChildren().addAll(
                new Label("Type : " + p.getType()),
                new Label("Statut : " + p.getStatut()),
                new Label("Du " + p.getDateDebut() + " au " + p.getDateFin())
        );

        card.getChildren().addAll(title,imageContainer, desc, infoBox);
        return card;
    }

    private void showNoImageLabel(StackPane container) {
        Label noImageLabel = new Label("Pas d'image");
        noImageLabel.getStyleClass().add("no-image-label");
        container.getChildren().clear();
        container.getChildren().add(noImageLabel);
    }

    private void loadCommandes(int userId) {
        try {
            commandesContainer.getChildren().clear();
            List<Commande> commandes = serviceCommande.getCommandesByCreateur(userId);

            for (Commande c : commandes) {
                VBox card = createCommandeCard(c);
                commandesContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger vos commandes.");
        }
    }
    private VBox createCommandeCard(Commande c) {
        VBox card = new VBox(5);
        card.getStyleClass().add("formation-card"); // même style que les formations

        Label title = new Label("Commande n°" + c.getId());
        title.getStyleClass().add("h3");

        Label dateLabel = new Label("Date : " + c.getDateCmd());
        Label statutLabel = new Label("Statut : " + c.getStatut());
        Label prixLabel = new Label("Prix total : " + c.getPrix() + " €");

        VBox produitsBox = new VBox(2);
        produitsBox.getChildren().add(new Label("Produits :"));
        for (CommandeProduit cp : c.getProduits()) {
            Produit p = cp.getProduit();
            String line = "- " + p.getNom() + " (x" + cp.getQuantite() + ")";
            produitsBox.getChildren().add(new Label(line));
        }

        VBox infoBox = new VBox(3);
        infoBox.getChildren().addAll(dateLabel, statutLabel, prixLabel, produitsBox);

        card.getChildren().addAll(title, infoBox);
        return card;
    }
    private void loadEvenements(int userId) {
        try {
            evenementsContainer.getChildren().clear();
            List<Evenement> evenements = serviceEvenement.findEvenementsByCreateurId(userId);
            for (Evenement e : evenements) {
                VBox card = createEvenementCard(e);
                evenementsContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger vos événements.");
        }
    }
    private VBox createEvenementCard(Evenement e) {
        VBox card = new VBox(5);
        card.getStyleClass().add("formation-card");

        Label title = new Label(e.getTitre());
        title.getStyleClass().add("h3");

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefWidth(300);
        imageContainer.setPrefHeight(200);


        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        String imagePath = e.getImageurl();
        if (imagePath != null && !imagePath.isEmpty()) {

            String fullPath = "C:\\Users\\Fatma\\IdeaProjects\\ArtizinaJava\\src\\main\\resources" + imagePath;

            try {
                File imageFile = new File(fullPath);

                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true, true);

                    imageView.setImage(image);
                    imageContainer.getChildren().clear();
                    imageContainer.getChildren().add(imageView);


                    image.errorProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            showNoImageLabel(imageContainer);
                            System.out.println("Erreur de chargement de l'image: " + fullPath);
                        }
                    });

                    image.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0 && !imageContainer.getChildren().contains(imageView)) {
                            imageContainer.getChildren().clear();
                            imageContainer.getChildren().add(imageView);
                        }
                    });
                } else {
                    System.out.println("Image non trouvée: " + fullPath);
                    showNoImageLabel(imageContainer);
                }
            } catch (Exception e1) {
                System.out.println("Exception lors du chargement de l'image: " + e1.getMessage());
                e1.printStackTrace();
                showNoImageLabel(imageContainer);
            }
        } else {
            showNoImageLabel(imageContainer);
        }

        Label desc = new Label(e.getDescription());
        desc.getStyleClass().add("evenement-description");

        VBox infoBox = new VBox(3);
        infoBox.getChildren().addAll(
                new Label("Catégorie : " + e.getCategorie()),
                new Label("Localisation : " + e.getLocalisation()),
                new Label("Du " + e.getStartdate() + " au " + e.getEnddate())
        );

        card.getChildren().addAll(title, imageContainer, desc, infoBox);
        return card;
    }







}