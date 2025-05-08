package tn.esprit.workshop.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import tn.esprit.workshop.utils.MyDbConnexion;

public class EvenementsDonDashboardController implements Initializable {

    @FXML private Label lblTotalEvents;
    @FXML private Label lblTotalDons;
    @FXML private Label lblTotalAmount;
    @FXML private Label lblAverageDon;
    
    @FXML private Label lblEventsTrend;
    @FXML private Label lblDonsTrend;
    @FXML private Label lblAmountTrend;
    @FXML private Label lblAverageTrend;
    
    @FXML private ComboBox<String> periodFilter;
    @FXML private ComboBox<String> eventTrendFilter;
    @FXML private ComboBox<String> donationDisplayType;
    
    @FXML private PieChart donationPieChart;
    @FXML private AreaChart<String, Number> eventTrendChart;
    @FXML private BarChart<String, Number> topEventsChart;
    @FXML private AreaChart<String, Number> donEvolutionChart;
    
    @FXML private AnchorPane contentArea;
    
    private Connection connection;
    private DecimalFormat df = new DecimalFormat("0.00");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM yyyy");
    
    // Variables pour stocker les périodes de comparaison
    private String currentPeriod = "Année courante";
    private Map<String, Double> previousStats = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connection = MyDbConnexion.getInstance().getCnx();
        
        // Initialiser les filtres
        setupFilters();
        
        // Charger les données
        loadAllData();
    }
    
    private void setupFilters() {
        // Configurer les options de période
        ObservableList<String> periodOptions = FXCollections.observableArrayList(
                "Année courante", "Dernier trimestre", "6 derniers mois", "Tout l'historique"
        );
        periodFilter.setItems(periodOptions);
        periodFilter.setValue("Année courante");
        periodFilter.setOnAction(e -> {
            currentPeriod = periodFilter.getValue();
            loadAllData();
        });
        
        // Charger les types d'événements depuis la base de données
        loadEventTypes();
        
        // Configurer les options d'affichage des dons
        ObservableList<String> donDisplayOptions = FXCollections.observableArrayList(
                "Montant", "Nombre"
        );
        donationDisplayType.setItems(donDisplayOptions);
        donationDisplayType.setValue("Montant");
        donationDisplayType.setOnAction(e -> loadDonationPieChart());
    }
    
    private void loadEventTypes() {
        try {
            // Vérifier si la table existe
            String checkTableQuery = "SHOW TABLES LIKE 'evenement'";
            PreparedStatement checkStatement = connection.prepareStatement(checkTableQuery);
            ResultSet checkResult = checkStatement.executeQuery();
            
            if (!checkResult.next()) {
                System.err.println("La table 'evenement' n'existe pas dans la base de données!");
                setDefaultEventTypes();
                return;
            }
            
            // Vérifier si la colonne categorie existe
            String checkColumnQuery = "SHOW COLUMNS FROM evenement LIKE 'categorie'";
            PreparedStatement checkColumnStatement = connection.prepareStatement(checkColumnQuery);
            ResultSet checkColumnResult = checkColumnStatement.executeQuery();
            
            String columnName = checkColumnResult.next() ? "categorie" : "type";
            
            // Requête pour obtenir tous les types d'événements distincts
            String query = "SELECT DISTINCT " + columnName + " FROM evenement ORDER BY " + columnName;
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
            ObservableList<String> eventTypeOptions = FXCollections.observableArrayList();
            eventTypeOptions.add("Tous les types"); // Option par défaut
            
            while (resultSet.next()) {
                String type = resultSet.getString(1);
                if (type != null && !type.isEmpty()) {
                    eventTypeOptions.add(type);
                }
            }
            
            // Si aucun type n'est trouvé, utiliser les types par défaut du système
            if (eventTypeOptions.size() <= 1) {
                addDefaultEventTypes(eventTypeOptions);
            }
            
            eventTrendFilter.setItems(eventTypeOptions);
            eventTrendFilter.setValue("Tous les types");
            eventTrendFilter.setOnAction(e -> loadEventTrendChart());
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des types d'événements: " + e.getMessage());
            e.printStackTrace();
            
            setDefaultEventTypes();
        }
    }
    
    private void setDefaultEventTypes() {
        // En cas d'erreur, utiliser des valeurs par défaut
        ObservableList<String> eventTypeOptions = FXCollections.observableArrayList();
        eventTypeOptions.add("Tous les types");
        addDefaultEventTypes(eventTypeOptions);
        
        eventTrendFilter.setItems(eventTypeOptions);
        eventTrendFilter.setValue("Tous les types");
    }
    
    private void addDefaultEventTypes(ObservableList<String> eventTypeOptions) {
        eventTypeOptions.addAll(
            "Musique", "Théâtre", "Humour", "Danse", "Peinture & Arts Visuels", 
            "Cinéma & Audiovisuel", "Artisanat", "Littérature & Poésie", "Mode & Design"
        );
    }
    
    @FXML
    public void refreshDashboard() {
        loadAllData();
    }
    
    private void loadAllData() {
        // Sauvegarder les anciennes statistiques pour calculer les tendances
        savePreviousStats();
        
        // Charger toutes les données
        loadStats();
        loadEventTrendChart();
        loadDonationPieChart();
        loadTopEventsChart();
        loadDonEvolutionChart();
        
        // Calculer et afficher les tendances
        calculateTrends();
    }
    
    private void savePreviousStats() {
        try {
            previousStats.put("events", Double.parseDouble(lblTotalEvents.getText().replace(",", ".")));
            previousStats.put("dons", Double.parseDouble(lblTotalDons.getText().replace(",", ".")));
            previousStats.put("amount", Double.parseDouble(lblTotalAmount.getText().replace(",", ".")));
            previousStats.put("average", Double.parseDouble(lblAverageDon.getText().replace(",", ".")));
        } catch (NumberFormatException e) {
            // Initialiser avec des zéros si conversion impossible
            previousStats.put("events", 0.0);
            previousStats.put("dons", 0.0);
            previousStats.put("amount", 0.0);
            previousStats.put("average", 0.0);
        }
    }
    
    private void calculateTrends() {
        calculateTrend(lblEventsTrend, "events", Double.parseDouble(lblTotalEvents.getText().replace(",", ".")));
        calculateTrend(lblDonsTrend, "dons", Double.parseDouble(lblTotalDons.getText().replace(",", ".")));
        calculateTrend(lblAmountTrend, "amount", Double.parseDouble(lblTotalAmount.getText().replace(",", ".")));
        calculateTrend(lblAverageTrend, "average", Double.parseDouble(lblAverageDon.getText().replace(",", ".")));
    }
    
    private void calculateTrend(Label trendLabel, String key, double currentValue) {
        double previousValue = previousStats.get(key);
        double percentChange = 0;
        
        if (previousValue > 0) {
            percentChange = ((currentValue - previousValue) / previousValue) * 100;
        }
        
        String trendText = String.format("%+.1f%% vs période précédente", percentChange);
        trendLabel.setText(trendText);
        
        // Définir la couleur en fonction de la tendance
        if (percentChange > 0) {
            trendLabel.setTextFill(Color.GREEN);
        } else if (percentChange < 0) {
            trendLabel.setTextFill(Color.RED);
        } else {
            trendLabel.setTextFill(Color.GRAY);
        }
    }
    
    private void loadStats() {
        try {
            // Vérifier si les tables existent
            try {
                String checkTableQuery = "SHOW TABLES LIKE 'evenement'";
                PreparedStatement checkStatement = connection.prepareStatement(checkTableQuery);
                ResultSet checkResult = checkStatement.executeQuery();
                
                if (!checkResult.next()) {
                    System.err.println("La table 'evenement' n'existe pas dans la base de données!");
                    setDummyData();
                    return;
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la vérification des tables: " + e.getMessage());
                setDummyData();
                return;
            }
            
            String whereClause = getPeriodWhereClause("e.startdate");
            
            // Requête pour le total d'événements
            String eventQuery = "SELECT COUNT(*) FROM evenement e WHERE " + whereClause;
            PreparedStatement eventStatement = connection.prepareStatement(eventQuery);
            applyPeriodParameters(eventStatement, 1);
            ResultSet eventResult = eventStatement.executeQuery();
            
            if (eventResult.next()) {
                lblTotalEvents.setText(String.valueOf(eventResult.getInt(1)));
            }
            
            // Requête pour les dons
            whereClause = getPeriodWhereClause("d.donationdate");
            String donQuery = "SELECT COUNT(*), SUM(d.amount), AVG(d.amount) " +
                             "FROM don d WHERE " + whereClause;
            PreparedStatement donStatement = connection.prepareStatement(donQuery);
            applyPeriodParameters(donStatement, 1);
            ResultSet donResult = donStatement.executeQuery();
            
            if (donResult.next()) {
                lblTotalDons.setText(String.valueOf(donResult.getInt(1)));
                double totalAmount = donResult.getDouble(2);
                double averageAmount = donResult.getDouble(3);
                
                // Si NULL est retourné (pas de dons), mettre à 0
                if (donResult.wasNull() || totalAmount <= 0) {
                    lblTotalAmount.setText("0.00");
                    lblAverageDon.setText("0.00");
                } else {
                    lblTotalAmount.setText(df.format(totalAmount));
                    lblAverageDon.setText(df.format(averageAmount));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            e.printStackTrace();
            setDummyData();
        }
    }
    
    private void setDummyData() {
        // Définir des données fictives pour éviter les erreurs d'affichage
        lblTotalEvents.setText("0");
        lblTotalDons.setText("0");
        lblTotalAmount.setText("0.00");
        lblAverageDon.setText("0.00");
        
        // Ajouter également des données fictives pour les graphiques
        setDummyChartData();
    }
    
    private void setDummyChartData() {
        // Données fictives pour eventTrendChart
        eventTrendChart.getData().clear();
        XYChart.Series<String, Number> eventSeries = new XYChart.Series<>();
        eventSeries.setName("Événements");
        eventSeries.getData().add(new XYChart.Data<>("Jan 2023", 5));
        eventSeries.getData().add(new XYChart.Data<>("Fév 2023", 8));
        eventSeries.getData().add(new XYChart.Data<>("Mar 2023", 12));
        eventSeries.getData().add(new XYChart.Data<>("Avr 2023", 7));
        eventSeries.getData().add(new XYChart.Data<>("Mai 2023", 10));
        eventTrendChart.getData().add(eventSeries);
        
        // Données fictives pour donationPieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.add(new PieChart.Data("Musique (1500 TND)", 1500));
        pieChartData.add(new PieChart.Data("Théâtre (800 TND)", 800));
        pieChartData.add(new PieChart.Data("Danse (1200 TND)", 1200));
        donationPieChart.setData(pieChartData);
        
        // Données fictives pour topEventsChart
        topEventsChart.getData().clear();
        XYChart.Series<String, Number> topSeries = new XYChart.Series<>();
        topSeries.setName("Montant collecté (TND)");
        topSeries.getData().add(new XYChart.Data<>("Concert A", 2500));
        topSeries.getData().add(new XYChart.Data<>("Expo B", 1800));
        topSeries.getData().add(new XYChart.Data<>("Festival C", 1500));
        topSeries.getData().add(new XYChart.Data<>("Atelier D", 1200));
        topSeries.getData().add(new XYChart.Data<>("Spectacle E", 1000));
        topEventsChart.getData().add(topSeries);
        
        // Données fictives pour donEvolutionChart
        donEvolutionChart.getData().clear();
        XYChart.Series<String, Number> evolutionSeries1 = new XYChart.Series<>();
        evolutionSeries1.setName("Musique");
        evolutionSeries1.getData().add(new XYChart.Data<>("Jan 2023", 500));
        evolutionSeries1.getData().add(new XYChart.Data<>("Fév 2023", 700));
        evolutionSeries1.getData().add(new XYChart.Data<>("Mar 2023", 900));
        
        XYChart.Series<String, Number> evolutionSeries2 = new XYChart.Series<>();
        evolutionSeries2.setName("Théâtre");
        evolutionSeries2.getData().add(new XYChart.Data<>("Jan 2023", 300));
        evolutionSeries2.getData().add(new XYChart.Data<>("Fév 2023", 400));
        evolutionSeries2.getData().add(new XYChart.Data<>("Mar 2023", 600));
        
        donEvolutionChart.getData().addAll(evolutionSeries1, evolutionSeries2);
    }
    
    private void loadEventTrendChart() {
        try {
            eventTrendChart.getData().clear();
            
            // Vérifier si la table existe
            String checkTableQuery = "SHOW TABLES LIKE 'evenement'";
            PreparedStatement checkStatement = connection.prepareStatement(checkTableQuery);
            ResultSet checkResult = checkStatement.executeQuery();
            
            if (!checkResult.next()) {
                System.err.println("La table 'evenement' n'existe pas dans la base de données!");
                setDummyChartData();
                return;
            }
            
            // Vérifier si la colonne categorie existe
            String checkColumnQuery = "SHOW COLUMNS FROM evenement LIKE 'categorie'";
            PreparedStatement checkColumnStatement = connection.prepareStatement(checkColumnQuery);
            ResultSet checkColumnResult = checkColumnStatement.executeQuery();
            
            String columnName = checkColumnResult.next() ? "categorie" : "type";
            String dateColumn = "startdate"; // Par défaut, utiliser startdate
            
            // Vérifier si la colonne date_evenement existe à la place
            String checkDateColumnQuery = "SHOW COLUMNS FROM evenement LIKE 'date_evenement'";
            PreparedStatement checkDateColumnStatement = connection.prepareStatement(checkDateColumnQuery);
            ResultSet checkDateColumnResult = checkDateColumnStatement.executeQuery();
            
            if (checkDateColumnResult.next()) {
                dateColumn = "date_evenement";
            }
            
            String typeFilter = eventTrendFilter.getValue();
            String typeCondition = typeFilter.equals("Tous les types") ? "" : " AND e." + columnName + " = ?";
            
            String whereClause = getPeriodWhereClause("e." + dateColumn);
            
            // Requête pour obtenir le nombre d'événements par mois
            String query = "SELECT DATE_FORMAT(e." + dateColumn + ", '%Y-%m') as period, COUNT(*) as count " +
                           "FROM evenement e " +
                           "WHERE " + whereClause + typeCondition +
                           " GROUP BY period " +
                           "ORDER BY period";
                           
            PreparedStatement statement = connection.prepareStatement(query);
            int paramIndex = applyPeriodParameters(statement, 1);
            
            if (!typeFilter.equals("Tous les types")) {
                statement.setString(paramIndex, typeFilter);
            }
            
            ResultSet resultSet = statement.executeQuery();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Nombre d'événements");
            
            boolean hasData = false;
            while (resultSet.next()) {
                hasData = true;
                String period = resultSet.getString("period");
                int count = resultSet.getInt("count");
                
                // Formater la période pour affichage
                LocalDate date = LocalDate.parse(period + "-01");
                String formattedPeriod = date.format(dtf);
                
                series.getData().add(new XYChart.Data<>(formattedPeriod, count));
            }
            
            if (hasData) {
                eventTrendChart.getData().add(series);
            } else {
                // Aucune donnée trouvée, définir des données fictives
                XYChart.Series<String, Number> dummySeries = new XYChart.Series<>();
                dummySeries.setName("Nombre d'événements");
                
                LocalDate now = LocalDate.now();
                for (int i = 5; i >= 0; i--) {
                    LocalDate date = now.minusMonths(i);
                    String formattedPeriod = date.format(dtf);
                    dummySeries.getData().add(new XYChart.Data<>(formattedPeriod, 0));
                }
                
                eventTrendChart.getData().add(dummySeries);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des tendances d'événements: " + e.getMessage());
            e.printStackTrace();
            setDummyChartData();
        }
    }
    
    private void loadDonationPieChart() {
        try {
            // Vérifier si les tables existent
            String checkTableQuery = "SHOW TABLES LIKE 'don'";
            PreparedStatement checkStatement = connection.prepareStatement(checkTableQuery);
            ResultSet checkResult = checkStatement.executeQuery();
            
            if (!checkResult.next()) {
                System.err.println("La table 'don' n'existe pas dans la base de données!");
                setDummyChartData();
                return;
            }
            
            String displayType = donationDisplayType.getValue();
            String aggregation = displayType.equals("Montant") ? "SUM(d.amount)" : "COUNT(d.id)";
            
            // Vérifier si la colonne amount existe
            String amountColumn = "amount";
            String checkAmountColumnQuery = "SHOW COLUMNS FROM don LIKE 'montant'";
            PreparedStatement checkAmountColumnStatement = connection.prepareStatement(checkAmountColumnQuery);
            ResultSet checkAmountColumnResult = checkAmountColumnStatement.executeQuery();
            
            if (checkAmountColumnResult.next()) {
                amountColumn = "montant";
            }
            
            // Ajuster l'agrégation en fonction de la colonne réelle
            aggregation = displayType.equals("Montant") ? "SUM(d." + amountColumn + ")" : "COUNT(d.id)";
            
            // Vérifier si la colonne categorie existe dans evenement
            String checkCategorieColumnQuery = "SHOW COLUMNS FROM evenement LIKE 'categorie'";
            PreparedStatement checkCategorieColumnStatement = connection.prepareStatement(checkCategorieColumnQuery);
            ResultSet checkCategorieColumnResult = checkCategorieColumnStatement.executeQuery();
            
            String categoryColumn = checkCategorieColumnResult.next() ? "categorie" : "type";
            
            String whereClause = getPeriodWhereClause("d.donationdate");
            
            // Requête pour obtenir la distribution des dons par type d'événement
            String query = "SELECT e." + categoryColumn + " as type, " + aggregation + " as value " +
                           "FROM don d " +
                           "JOIN evenement e ON d.evenement_id = e.id " +
                           "WHERE " + whereClause +
                           " GROUP BY type " +
                           "ORDER BY value DESC";
                           
            PreparedStatement statement = connection.prepareStatement(query);
            applyPeriodParameters(statement, 1);
            ResultSet resultSet = statement.executeQuery();
            
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            boolean hasData = false;
            while (resultSet.next()) {
                hasData = true;
                String type = resultSet.getString("type");
                double value = resultSet.getDouble("value");
                
                if (type == null || type.isEmpty()) {
                    type = "Non catégorisé";
                }
                
                String label = type;
                if (displayType.equals("Montant")) {
                    label += " (" + df.format(value) + " TND)";
                } else {
                    label += " (" + (int)value + ")";
                }
                
                pieChartData.add(new PieChart.Data(label, value));
            }
            
            if (hasData) {
                donationPieChart.setData(pieChartData);
                donationPieChart.setTitle("Distribution par " + (displayType.equals("Montant") ? "montant" : "nombre"));
            } else {
                // Aucune donnée trouvée, définir des données fictives
                ObservableList<PieChart.Data> dummyData = FXCollections.observableArrayList();
                dummyData.add(new PieChart.Data("Aucune donnée disponible", 1));
                donationPieChart.setData(dummyData);
                donationPieChart.setTitle("Aucune donnée disponible");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du graphique des dons: " + e.getMessage());
            e.printStackTrace();
            setDummyChartData();
        }
    }
    
    private void loadTopEventsChart() {
        try {
            topEventsChart.getData().clear();
            
            // Vérifier si les tables existent
            String checkEventTableQuery = "SHOW TABLES LIKE 'evenement'";
            PreparedStatement checkEventStatement = connection.prepareStatement(checkEventTableQuery);
            ResultSet checkEventResult = checkEventStatement.executeQuery();
            
            String checkDonTableQuery = "SHOW TABLES LIKE 'don'";
            PreparedStatement checkDonStatement = connection.prepareStatement(checkDonTableQuery);
            ResultSet checkDonResult = checkDonStatement.executeQuery();
            
            if (!checkEventResult.next() || !checkDonResult.next()) {
                System.err.println("Les tables 'evenement' ou 'don' n'existent pas dans la base de données!");
                setDummyChartData();
                return;
            }
            
            // Vérifier la structure des colonnes dans la table don
            String amountColumn = "amount";
            String donDateColumn = "donationdate";
            
            String checkAmountColumnQuery = "SHOW COLUMNS FROM don LIKE 'montant'";
            PreparedStatement checkAmountColumnStatement = connection.prepareStatement(checkAmountColumnQuery);
            ResultSet checkAmountColumnResult = checkAmountColumnStatement.executeQuery();
            
            if (checkAmountColumnResult.next()) {
                amountColumn = "montant";
            }
            
            String checkDonDateColumnQuery = "SHOW COLUMNS FROM don LIKE 'date_don'";
            PreparedStatement checkDonDateColumnStatement = connection.prepareStatement(checkDonDateColumnQuery);
            ResultSet checkDonDateColumnResult = checkDonDateColumnStatement.executeQuery();
            
            if (checkDonDateColumnResult.next()) {
                donDateColumn = "date_don";
            }
            
            String whereClause = getPeriodWhereClause("d." + donDateColumn);
            
            // Requête pour obtenir les top 5 événements par montant collecté
            String query = "SELECT e.titre, SUM(d." + amountColumn + ") as montant_total " +
                           "FROM don d " +
                           "JOIN evenement e ON d.evenement_id = e.id " +
                           "WHERE " + whereClause +
                           " GROUP BY e.id, e.titre " +
                           "ORDER BY montant_total DESC " +
                           "LIMIT 5";
                           
            PreparedStatement statement = connection.prepareStatement(query);
            applyPeriodParameters(statement, 1);
            ResultSet resultSet = statement.executeQuery();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Montant collecté (TND)");
            
            boolean hasData = false;
            while (resultSet.next()) {
                hasData = true;
                String titre = resultSet.getString("titre");
                double montant = resultSet.getDouble("montant_total");
                
                // Tronquer les titres trop longs
                if (titre != null && titre.length() > 15) {
                    titre = titre.substring(0, 12) + "...";
                } else if (titre == null || titre.isEmpty()) {
                    titre = "Événement sans titre";
                }
                
                series.getData().add(new XYChart.Data<>(titre, montant));
            }
            
            if (hasData) {
                topEventsChart.getData().add(series);
            } else {
                // Aucune donnée trouvée, utiliser des données fictives
                XYChart.Series<String, Number> dummySeries = new XYChart.Series<>();
                dummySeries.setName("Montant collecté (TND)");
                dummySeries.getData().add(new XYChart.Data<>("Aucune donnée", 0));
                topEventsChart.getData().add(dummySeries);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du top des événements: " + e.getMessage());
            e.printStackTrace();
            setDummyChartData();
        }
    }
    
    private void loadDonEvolutionChart() {
        try {
            donEvolutionChart.getData().clear();
            
            // Vérifier si les tables existent
            String checkEventTableQuery = "SHOW TABLES LIKE 'evenement'";
            PreparedStatement checkEventStatement = connection.prepareStatement(checkEventTableQuery);
            ResultSet checkEventResult = checkEventStatement.executeQuery();
            
            String checkDonTableQuery = "SHOW TABLES LIKE 'don'";
            PreparedStatement checkDonStatement = connection.prepareStatement(checkDonTableQuery);
            ResultSet checkDonResult = checkDonStatement.executeQuery();
            
            if (!checkEventResult.next() || !checkDonResult.next()) {
                System.err.println("Les tables 'evenement' ou 'don' n'existent pas dans la base de données!");
                setDummyChartData();
                return;
            }
            
            // Vérifier la structure des colonnes dans les tables
            String amountColumn = "amount";
            String donDateColumn = "donationdate";
            
            String checkAmountColumnQuery = "SHOW COLUMNS FROM don LIKE 'montant'";
            PreparedStatement checkAmountColumnStatement = connection.prepareStatement(checkAmountColumnQuery);
            ResultSet checkAmountColumnResult = checkAmountColumnStatement.executeQuery();
            
            if (checkAmountColumnResult.next()) {
                amountColumn = "montant";
            }
            
            String checkDonDateColumnQuery = "SHOW COLUMNS FROM don LIKE 'date_don'";
            PreparedStatement checkDonDateColumnStatement = connection.prepareStatement(checkDonDateColumnQuery);
            ResultSet checkDonDateColumnResult = checkDonDateColumnStatement.executeQuery();
            
            if (checkDonDateColumnResult.next()) {
                donDateColumn = "date_don";
            }
            
            // Vérifier si la colonne categorie existe dans evenement
            String checkCategorieColumnQuery = "SHOW COLUMNS FROM evenement LIKE 'categorie'";
            PreparedStatement checkCategorieColumnStatement = connection.prepareStatement(checkCategorieColumnQuery);
            ResultSet checkCategorieColumnResult = checkCategorieColumnStatement.executeQuery();
            
            String categoryColumn = checkCategorieColumnResult.next() ? "categorie" : "type";
            
            String whereClause = getPeriodWhereClause("d." + donDateColumn);
            
            // Requête pour obtenir l'évolution des dons dans le temps
            String query = "SELECT DATE_FORMAT(d." + donDateColumn + ", '%Y-%m') as period, " +
                           "e." + categoryColumn + " as type, " +
                           "SUM(d." + amountColumn + ") as montant " +
                           "FROM don d " +
                           "JOIN evenement e ON d.evenement_id = e.id " +
                           "WHERE " + whereClause +
                           " GROUP BY period, type " +
                           "ORDER BY period";
                           
            PreparedStatement statement = connection.prepareStatement(query);
            applyPeriodParameters(statement, 1);
            ResultSet resultSet = statement.executeQuery();
            
            // Organiser les données par période et type d'événement
            Map<String, Map<String, Double>> periodData = new HashMap<>();
            List<String> periods = new ArrayList<>();
            Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
            
            boolean hasData = false;
            while (resultSet.next()) {
                hasData = true;
                String period = resultSet.getString("period");
                String type = resultSet.getString("type");
                double montant = resultSet.getDouble("montant");
                
                // Gérer les valeurs nulles ou vides
                if (type == null || type.isEmpty()) {
                    type = "Non catégorisé";
                }
                
                // Formater la période pour affichage
                LocalDate date = LocalDate.parse(period + "-01");
                String formattedPeriod = date.format(dtf);
                
                if (!periods.contains(formattedPeriod)) {
                    periods.add(formattedPeriod);
                }
                
                if (!seriesMap.containsKey(type)) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(type);
                    seriesMap.put(type, series);
                }
                
                if (!periodData.containsKey(formattedPeriod)) {
                    periodData.put(formattedPeriod, new HashMap<>());
                }
                
                periodData.get(formattedPeriod).put(type, montant);
            }
            
            if (hasData) {
                // Remplir les séries avec les données
                for (String period : periods) {
                    for (String type : seriesMap.keySet()) {
                        double value = 0;
                        if (periodData.get(period).containsKey(type)) {
                            value = periodData.get(period).get(type);
                        }
                        seriesMap.get(type).getData().add(new XYChart.Data<>(period, value));
                    }
                }
                
                // Ajouter les séries au graphique
                donEvolutionChart.getData().addAll(seriesMap.values());
            } else {
                // Aucune donnée trouvée, définir des données fictives
                XYChart.Series<String, Number> dummySeries = new XYChart.Series<>();
                dummySeries.setName("Aucune donnée");
                
                LocalDate now = LocalDate.now();
                for (int i = 5; i >= 0; i--) {
                    LocalDate date = now.minusMonths(i);
                    String formattedPeriod = date.format(dtf);
                    dummySeries.getData().add(new XYChart.Data<>(formattedPeriod, 0));
                }
                
                donEvolutionChart.getData().add(dummySeries);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement de l'évolution des dons: " + e.getMessage());
            e.printStackTrace();
            setDummyChartData();
        }
    }
    
    private String getPeriodWhereClause(String dateField) {
        switch (currentPeriod) {
            case "Dernier trimestre":
                return dateField + " >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)";
            case "6 derniers mois":
                return dateField + " >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)";
            case "Année courante":
                return "YEAR(" + dateField + ") = YEAR(CURDATE())";
            case "Tout l'historique":
            default:
                return "1=1"; // Pas de filtre de date
        }
    }
    
    private int applyPeriodParameters(PreparedStatement statement, int startIndex) throws SQLException {
        // Cette méthode n'a pas besoin d'appliquer de paramètres avec la structure actuelle des requêtes
        // Elle est prévue pour une extension future avec des filtres de date paramétrés
        return startIndex;
    }
    
    @FXML
    public void showEvenementForm() {
        showFormInContentArea("/tn/esprit/workshop/resources/EvenementForm.fxml");
    }
    
    @FXML
    public void showEventsList() {
        showFormInContentArea("/fxml/EvenementsTableView.fxml");
    }
    
    @FXML
    public void showDonForm() {
        showFormInContentArea("/fxml/DonForm.fxml");
    }
    
    @FXML
    public void showDonsList() {
        showFormInContentArea("/fxml/DonsTableView.fxml");
    }
    
    private void showFormInContentArea(String fxmlPath) {
        try {
            contentArea.setVisible(true);
            contentArea.setManaged(true);
            
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);
            
            // Ajuster la taille du contenu
            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
} 