package tn.esprit.workshop.gui;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.workshop.models.Commande;
import tn.esprit.workshop.models.CommandeProduit;
import tn.esprit.workshop.services.ServiceCommande;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Commandeartisan {

    // Tableau des commandes
    @FXML private TableView<Commande> commandeTable;
    @FXML private TableColumn<Commande, Integer> colId;
    @FXML private TableColumn<Commande, String> colDate;
    @FXML private TableColumn<Commande, String> colStatut;
    @FXML private TableColumn<Commande, Double> colPrix;
    @FXML private TableColumn<Commande, List<CommandeProduit>> colProduits;
    @FXML private TableColumn<Commande, Void> colActions;
    @FXML private Button exportPdfBtn;
    @FXML private ComboBox<String> triComboBox;
    @FXML private ComboBox<String> cbStatuts;

    // Éléments pour les statistiques
    @FXML private Label lblTotalCommandes;
    @FXML private Label lblCommandesEnAttente;
    @FXML private Label lblCommandesEnCours;
    @FXML private Label lblCommandesTerminees;
    @FXML private Label lblCommandesAnnulees;
    @FXML private Label lblMontantMoyen;
    @FXML private Label lblTauxAnnulation;
    @FXML private Label lblProduitPlusVendu;
    @FXML private Label lblChiffreAffaires;
    @FXML private PieChart statutPieChart;
    @FXML private BarChart<String, Number> categorieBarChart;
    @FXML private BarChart<String, Number> caParStatutBarChart;
    @FXML private VBox statsContainer;
    @FXML private LineChart<String, Number> commandesParJourChart;
    @FXML private Label lblProduitsMoyens;

    // Pagination
    @FXML private Pagination pagination;
    private ObservableList<Commande> allCommandes = FXCollections.observableArrayList();
    private FilteredList<Commande> filteredCommandes;
    private final int ITEMS_PER_PAGE = 5;

    private final ServiceCommande serviceCommande = new ServiceCommande();

    @FXML
    private void minimizeWindow(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void maximizeWindow(ActionEvent event) {
        Stage stage = ((Stage)((Button)event.getSource()).getScene().getWindow());
        stage.setMaximized(!stage.isMaximized());
    }

    public void initialize() {
        configurerTableau();
        CategoryAxis xAxis = (CategoryAxis) commandesParJourChart.getXAxis();
        xAxis.setTickLabelRotation(-45);

        // Configuration du tri
        triComboBox.getItems().addAll("ID", "Prix", "Date", "Statut");
        triComboBox.getSelectionModel().selectFirst();
        triComboBox.setOnAction(event -> appliquerTri());
        configurerFiltreStatut();
        configurePagination();
        refreshAll();
    }
    private void configurerFiltreStatut() {
        // Initialiser la liste filtrée
        filteredCommandes = new FilteredList<>(allCommandes, p -> true);

        // Remplir le ComboBox avec les statuts possibles
        cbStatuts.getItems().addAll("Tous", "En attente", "En cours", "livrée", "Annulée","payé");
        cbStatuts.getSelectionModel().selectFirst();

        // Appliquer le filtre quand le statut change
        cbStatuts.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredCommandes.setPredicate(commande -> {
                if (newVal == null || newVal.equals("Tous")) {
                    return true;
                }
                return commande.getStatut().equalsIgnoreCase(newVal);
            });
            updatePage(pagination.getCurrentPageIndex());
        });
    }

    private void appliquerTri() {
        String critere = triComboBox.getSelectionModel().getSelectedItem();
        Comparator<Commande> comparator;

        switch (critere) {
            case "Prix":
                comparator = Comparator.comparingDouble(Commande::getPrix);
                break;
            case "Date":
                comparator = Comparator.comparing(Commande::getDateCmd);
                break;
            case "Statut":
                comparator = Comparator.comparing(Commande::getStatut);
                break;
            default: // "ID"
                comparator = Comparator.comparingInt(Commande::getId);
                break;
        }

        // Créer une SortedList avec le comparateur
        SortedList<Commande> sortedData = new SortedList<>(filteredCommandes);
        sortedData.setComparator(comparator);

        // Mettre à jour la pagination avec les données triées et filtrées
        int totalPages = (int) Math.ceil((double) sortedData.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(totalPages > 0 ? totalPages : 1);

        // Mettre à jour la table avec les données triées et filtrées
        commandeTable.setItems(sortedData);
    }

    private void configurerTableau() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateCmd().toString()));
        colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));
        colPrix.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrix()).asObject());
        colProduits.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getProduits()));
        colProduits.setCellFactory(param -> new Commandeclient.ProduitCell());

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editStatutBtn = new Button("Modifier Statut");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox hbox = new HBox(5, editStatutBtn, deleteBtn);

            {
                editStatutBtn.setStyle(
                        "-fx-background-color: #327252;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-text-fill: #fff;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-family: 'Arial';" +
                                "-fx-cursor: hand;"
                );
                deleteBtn.setStyle(
                        "-fx-background-color: #86253f;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-text-fill: #fff;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-family: 'Arial';" +
                                "-fx-cursor: hand;"
                );

                editStatutBtn.setOnAction(e -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    openModifierStatutWindow(commande);
                });

                deleteBtn.setOnAction(e -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette commande ?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                serviceCommande.delete(commande.getId());
                                refreshAll();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                showAlert("Erreur lors de la suppression de la commande");
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        exportPdfBtn.setOnAction(e -> exportToPdf());
    }

    private void refreshAll() {
        try {
            allCommandes.setAll(serviceCommande.showAll());

            // Réappliquer le filtre et le tri
            if (cbStatuts.getValue() != null && !cbStatuts.getValue().equals("Tous")) {
                filteredCommandes.setPredicate(commande ->
                        commande.getStatut().equalsIgnoreCase(cbStatuts.getValue()));
            } else {
                filteredCommandes.setPredicate(commande -> true);
            }

            appliquerTri();

            // Mise à jour des statistiques
            chargerStatistiques();
            loadChiffreAffairesParStatut();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du rafraîchissement des données");
        }
    }

    private void chargerStatistiques() throws SQLException {
        int total = serviceCommande.countAllCommandes();
        int enAttente = serviceCommande.countByStatut("En attente");
        int enCours = serviceCommande.countByStatut("En cours");
        int terminees = serviceCommande.countByStatut("livrée");
        int annulees = serviceCommande.countByStatut("Annulée");
        double montantMoyen = serviceCommande.getMontantMoyen();
        double chiffreAffaires = serviceCommande.getChiffreAffairesTotal();
        double tauxAnnulation = total > 0 ? (annulees * 100.0) / total : 0;
        String nomProduit = serviceCommande.getNomProduitLePlusCommande();

        lblTotalCommandes.setText(String.valueOf(total));
        lblCommandesEnAttente.setText(String.valueOf(enAttente));
        lblCommandesEnCours.setText(String.valueOf(enCours));
        lblCommandesTerminees.setText(String.valueOf(terminees));
        lblCommandesAnnulees.setText(String.valueOf(annulees));
        lblMontantMoyen.setText(String.format("%.2f DT", montantMoyen));
        lblTauxAnnulation.setText(String.format("%.1f%%", tauxAnnulation));
        lblProduitPlusVendu.setText(nomProduit != null ? nomProduit : "Aucun");
        lblChiffreAffaires.setText(String.format("%.2f DT", chiffreAffaires));

        configurerPieChart(enAttente, enCours, terminees, annulees);
        configurerBarChart();
        updateProduitsMoyens();
        loadCommandesParJour();
    }

    private void loadCommandesParJour() throws SQLException {
        commandesParJourChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commandes par jour");

        Map<String, Integer> commandesParJour = serviceCommande.getCommandesParJour();
        for (Map.Entry<String, Integer> entry : commandesParJour.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        commandesParJourChart.getData().add(series);

        // Personnalisation de l'apparence
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            node.setStyle("-fx-background-color: #4e73df, white; -fx-background-radius: 4px;");
        }
    }

    private void updateProduitsMoyens() throws SQLException {
        double moyenne = serviceCommande.getProduitsMoyensParCommande();
        lblProduitsMoyens.setText(String.format("%.1f produits/commande", moyenne));
    }

    private void loadChiffreAffairesParStatut() throws SQLException {
        caParStatutBarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chiffre d'affaires");

        Map<String, Double> caParStatut = serviceCommande.getChiffreAffairesParStatut();
        for (Map.Entry<String, Double> entry : caParStatut.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        caParStatutBarChart.getData().add(series);

        // Personnalisation des couleurs des barres
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: #4e73df;");
        }
    }

    private void configurerPieChart(int enAttente, int enCours, int terminees, int annulees) {
        statutPieChart.getData().clear();

        PieChart.Data sliceEnAttente = new PieChart.Data("En attente", enAttente);
        PieChart.Data sliceEnCours = new PieChart.Data("En cours", enCours);
        PieChart.Data sliceTerminees = new PieChart.Data("Terminées", terminees);
        PieChart.Data sliceAnnulees = new PieChart.Data("Annulées", annulees);

        statutPieChart.getData().addAll(sliceEnAttente, sliceEnCours, sliceTerminees, sliceAnnulees);

        // Personnalisation des couleurs
        sliceEnAttente.getNode().setStyle("-fx-pie-color: #FFA500;");
        sliceEnCours.getNode().setStyle("-fx-pie-color: #1E90FF;");
        sliceTerminees.getNode().setStyle("-fx-pie-color: #32CD32;");
        sliceAnnulees.getNode().setStyle("-fx-pie-color: #FF4500;");
    }

    private void configurerBarChart() throws SQLException {
        categorieBarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top catégories");

        List<Object[]> topCategories = serviceCommande.getTopCategories(5);
        for (Object[] categorie : topCategories) {
            String nomCategorie = (String) categorie[0];
            int total = ((Number) categorie[1]).intValue();
            series.getData().add(new XYChart.Data<>(nomCategorie, total));
        }

        categorieBarChart.getData().add(series);

        // Personnalisation des couleurs des barres
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: #4682B4;");
        }
    }

    private void openModifierStatutWindow(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierStatut.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Modifier Statut");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            ModifierStatut controller = loader.getController();
            controller.setCommande(commande);
            controller.setOnUpdate(this::refreshAll);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture de la fenêtre de modification");
        }
    }

    private void exportToPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("commandes_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf");

        File file = fileChooser.showSaveDialog(commandeTable.getScene().getWindow());
        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Couleurs personnalisées
                BaseColor titleColor = new BaseColor(0, 102, 204);
                BaseColor headerColor = new BaseColor(51, 153, 255);
                BaseColor rowColor1 = new BaseColor(240, 240, 240);
                BaseColor rowColor2 = new BaseColor(255, 255, 255);
                BaseColor statusColorEnAttente = new BaseColor(255, 165, 0);
                BaseColor statusColorValidee = new BaseColor(0, 128, 0);
                BaseColor statusColorAnnulee = new BaseColor(255, 0, 0);

                // Titre du document
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, titleColor);
                Paragraph title = new Paragraph("Liste des Commandes", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                // Informations supplémentaires
                Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                Paragraph info = new Paragraph("Généré le: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), infoFont);
                info.setAlignment(Element.ALIGN_RIGHT);
                info.setSpacingAfter(15);
                document.add(info);

                // Tableau des commandes
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);
                table.setWidths(new float[]{1, 2, 2, 2});

                // En-têtes du tableau
                String[] headers = {"ID", "Date", "Statut", "Prix Total"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                    cell.setBackgroundColor(headerColor);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);
                }

                // Données des commandes
                boolean alternate = false;
                for (Commande commande : commandeTable.getItems()) {
                    BaseColor rowColor = alternate ? rowColor1 : rowColor2;
                    alternate = !alternate;

                    // ID
                    PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(commande.getId())));
                    idCell.setBackgroundColor(rowColor);
                    idCell.setPadding(5);
                    table.addCell(idCell);

                    // Date
                    PdfPCell dateCell = new PdfPCell(new Phrase(commande.getDateCmd().toString()));
                    dateCell.setBackgroundColor(rowColor);
                    dateCell.setPadding(5);
                    table.addCell(dateCell);

                    // Statut
                    BaseColor statusColor = BaseColor.BLACK;
                    if (commande.getStatut().equalsIgnoreCase("En attente")) {
                        statusColor = statusColorEnAttente;
                    } else if (commande.getStatut().equalsIgnoreCase("Validée")) {
                        statusColor = statusColorValidee;
                    } else if (commande.getStatut().equalsIgnoreCase("Annulée")) {
                        statusColor = statusColorAnnulee;
                    }

                    PdfPCell statusCell = new PdfPCell(new Phrase(commande.getStatut(),
                            FontFactory.getFont(FontFactory.HELVETICA, 12, statusColor)));
                    statusCell.setBackgroundColor(rowColor);
                    statusCell.setPadding(5);
                    table.addCell(statusCell);

                    // Prix
                    PdfPCell prixCell = new PdfPCell(new Phrase(String.format("%.2f DT", commande.getPrix())));
                    prixCell.setBackgroundColor(rowColor);
                    prixCell.setPadding(5);
                    prixCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(prixCell);
                }

                document.add(table);

                // Pied de page
                Paragraph footer = new Paragraph("Artizina - Système de gestion des commandes",
                        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
                footer.setAlignment(Element.ALIGN_CENTER);
                footer.setSpacingBefore(20);
                document.add(footer);

                document.close();

                showAlert("Export PDF réussi!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur lors de l'export PDF: " + e.getMessage());
            }
        }
    }

    private void showAlert(String message) {
        showAlert(message, Alert.AlertType.ERROR);
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        new Alert(alertType, message).showAndWait();
    }


    //pag
    private void configurePagination() {
        // Calcul initial du nombre de pages
        int totalPages = (int) Math.ceil((double) allCommandes.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(totalPages > 0 ? totalPages : 1);

        // Mise à jour lors du changement de page
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            updatePage(newVal.intValue());
        });


    }

    private Node createPage(int pageIndex) {
        return commandeTable;
    }

    private void updatePage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredCommandes.size());

        if (fromIndex < filteredCommandes.size()) {
            commandeTable.setItems(FXCollections.observableArrayList(
                    filteredCommandes.subList(fromIndex, toIndex)
            ));
        } else {
            commandeTable.setItems(FXCollections.observableArrayList());
        }
    }
}