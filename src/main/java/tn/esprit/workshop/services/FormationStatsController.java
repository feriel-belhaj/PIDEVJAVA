package tn.esprit.workshop.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.workshop.models.Formation;
import tn.esprit.workshop.models.FormationReservee;
import tn.esprit.workshop.services.FormationService;
import tn.esprit.workshop.services.FormationReserveeService;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class FormationStatsController implements Initializable {
    @FXML private Label totalFormationsLabel;
    @FXML private Label totalReservationsLabel;
    @FXML private Label tauxOccupationLabel;
    @FXML private BarChart<String, Number> reservationsChart;
    @FXML private PieChart occupationChart;
    @FXML private TableView<FormationStats> statsTable;
    @FXML private TableColumn<FormationStats, String> formationCol;
    @FXML private TableColumn<FormationStats, Integer> reservationsCol;
    @FXML private TableColumn<FormationStats, Integer> placesCol;
    @FXML private TableColumn<FormationStats, Double> occupationCol;

    private FormationService formationService;
    private FormationReserveeService reservationService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formationService = new FormationService();
        reservationService = new FormationReserveeService();

        // Configuration des colonnes du tableau
        formationCol.setCellValueFactory(new PropertyValueFactory<>("formationTitre"));
        reservationsCol.setCellValueFactory(new PropertyValueFactory<>("nombreReservations"));
        placesCol.setCellValueFactory(new PropertyValueFactory<>("nombrePlaces"));
        occupationCol.setCellValueFactory(new PropertyValueFactory<>("tauxOccupation"));

        // Chargement des données
        loadStats();
    }

    private void loadStats() {
        try {
            List<Formation> formations = formationService.getAll();
            List<FormationReservee> reservations = reservationService.getAll();

            // Calcul des statistiques
            int totalFormations = formations.size();
            int totalReservations = reservations.size();
            double tauxOccupationMoyen = 0;

            // Préparation des données pour les graphiques
            XYChart.Series<String, Number> reservationsSeries = new XYChart.Series<>();
            Map<String, Integer> reservationsParFormation = new HashMap<>();
            Map<String, Integer> placesParFormation = new HashMap<>();

            // Calcul des réservations par formation
            for (FormationReservee reservation : reservations) {
                String formationTitre = getFormationTitre(formations, reservation.getFormationId());
                reservationsParFormation.merge(formationTitre, 1, Integer::sum);
            }

            // Remplissage des données
            for (Formation formation : formations) {
                String titre = formation.getTitre();
                int nbReservations = reservationsParFormation.getOrDefault(titre, 0);
                int nbPlaces = formation.getNbPlace();
                double tauxOccupation = nbPlaces > 0 ? (double) nbReservations / nbPlaces * 100 : 0;

                // Ajout au graphique des réservations
                reservationsSeries.getData().add(new XYChart.Data<>(titre, nbReservations));

                // Ajout au tableau
                statsTable.getItems().add(new FormationStats(titre, nbReservations, nbPlaces, tauxOccupation));

                // Mise à jour du taux d'occupation moyen
                tauxOccupationMoyen += tauxOccupation;
            }

            // Mise à jour des labels
            totalFormationsLabel.setText("Total des formations: " + totalFormations);
            totalReservationsLabel.setText("Total des réservations: " + totalReservations);
            tauxOccupationLabel.setText(String.format("Taux d'occupation moyen: %.1f%%", 
                tauxOccupationMoyen / totalFormations));

            // Mise à jour du graphique des réservations
            reservationsChart.getData().add(reservationsSeries);

            // Mise à jour du graphique en camembert
            updatePieChart(formations, reservationsParFormation);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getFormationTitre(List<Formation> formations, int formationId) {
        return formations.stream()
                .filter(f -> f.getId() == formationId)
                .findFirst()
                .map(Formation::getTitre)
                .orElse("Formation inconnue");
    }

    private void updatePieChart(List<Formation> formations, Map<String, Integer> reservationsParFormation) {
        occupationChart.getData().clear();
        for (Formation formation : formations) {
            String titre = formation.getTitre();
            int nbReservations = reservationsParFormation.getOrDefault(titre, 0);
            int nbPlaces = formation.getNbPlace();
            double tauxOccupation = nbPlaces > 0 ? (double) nbReservations / nbPlaces * 100 : 0;
            
            occupationChart.getData().add(new PieChart.Data(titre, tauxOccupation));
        }
    }

    // Classe interne pour représenter les statistiques d'une formation
    public static class FormationStats {
        private String formationTitre;
        private int nombreReservations;
        private int nombrePlaces;
        private double tauxOccupation;

        public FormationStats(String formationTitre, int nombreReservations, int nombrePlaces, double tauxOccupation) {
            this.formationTitre = formationTitre;
            this.nombreReservations = nombreReservations;
            this.nombrePlaces = nombrePlaces;
            this.tauxOccupation = tauxOccupation;
        }

        public String getFormationTitre() { return formationTitre; }
        public int getNombreReservations() { return nombreReservations; }
        public int getNombrePlaces() { return nombrePlaces; }
        public double getTauxOccupation() { return tauxOccupation; }
    }
} 