package tn.esprit.workshop.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.JSONObject;
import tn.esprit.workshop.models.Evenement;
import tn.esprit.workshop.services.EvenementService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class EvenementDetailController implements Initializable {

    // Clé API OpenWeatherMap 
    private static final String OPENWEATHER_API_KEY = "f5cb0b965ea1564c50c6f1b74534d823"; // Remplacez par votre propre clé

    @FXML
    private ImageView imgEvent;

    @FXML
    private Label lblTitre;

    @FXML
    private Label lblCategorie;

    @FXML
    private Label lblGoalAmount;

    @FXML
    private Label lblCollectedAmount;

    @FXML
    private Label lblProgression;

    @FXML
    private Label lblJoursRestants;

    @FXML
    private Label lblStartDate;

    @FXML
    private Label lblEndDate;

    @FXML
    private Label lblLocalisation;

    @FXML
    private Label lblDescription;

    @FXML
    private Button btnFaireDon;

    @FXML
    private Button btnRetour;
    
    @FXML
    private MenuButton menuItineraire;
    
    @FXML
    private MenuItem menuItemPositionActuelle;
    
    @FXML
    private MenuItem menuItemPointCarte;
    
    @FXML
    private WebView mapWebView;
    
    @FXML
    private VBox weatherBox;
    
    @FXML
    private ImageView weatherIcon;
    
    @FXML
    private Label weatherDescription;
    
    @FXML
    private Label weatherTemp;
    
    @FXML
    private Label weatherFeelsLike;
    
    @FXML
    private Label weatherHumidity;
    
    @FXML
    private Label weatherWind;
    
    @FXML
    private Label weatherSunrise;
    
    @FXML
    private Label weatherSunset;
    
    @FXML
    private Label weatherLocation;
    
    @FXML
    private Label weatherUpdateTime;

    @FXML
    private VBox weatherWidget;

    // Composants pour la prévision du jour de l'événement
    @FXML
    private VBox eventDayForecastBox;

    @FXML
    private ImageView eventDayWeatherIcon;

    @FXML
    private Label eventDayDate;

    @FXML
    private Label eventDayTemp;

    @FXML
    private Label eventDayDescription;

    private Evenement evenement;
    private EvenementService evenementService;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private boolean weatherRetried = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evenementService = new EvenementService();
        
        // Initialiser les valeurs par défaut pour éviter les NullPointerException
        initializeWeatherWidget();
    }

    /**
     * Initialise les composants du widget météo avec des valeurs par défaut
     */
    private void initializeWeatherWidget() {
        try {
            // Vérification et initialisation des composants météo
            if (weatherTemp != null) weatherTemp.setText("--°C");
            if (weatherFeelsLike != null) weatherFeelsLike.setText("Ressenti: --°C");
            if (weatherHumidity != null) weatherHumidity.setText("--%");
            if (weatherWind != null) weatherWind.setText("-- m/s");
            if (weatherSunrise != null) weatherSunrise.setText("--:--");
            if (weatherSunset != null) weatherSunset.setText("--:--");
            if (weatherLocation != null) weatherLocation.setText("---");
            if (weatherDescription != null) weatherDescription.setText("Chargement...");
            if (weatherUpdateTime != null) weatherUpdateTime.setText("Chargement...");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du widget météo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        
        // Vérifier et mettre à jour le statut de l'événement avant l'affichage
        updateEventStatus();
        
        // Afficher les détails de l'événement
        displayEvenementDetails();
        
        // Initialiser la carte
        initializeMap();
        
        // Réinitialiser le drapeau de nouvelle tentative météo
        weatherRetried = false;
        
        try {
            // S'assurer que les composants du widget météo existent
            if (weatherTemp != null && weatherFeelsLike != null && weatherLocation != null && 
                weatherDescription != null && weatherUpdateTime != null) {
                
                // Initialiser les valeurs par défaut du widget météo
                weatherTemp.setText("--°C");
                weatherFeelsLike.setText("Ressenti: --°C");
                weatherHumidity.setText("--%");
                weatherWind.setText("-- m/s");
                weatherSunrise.setText("--:--");
                weatherSunset.setText("--:--");
                weatherLocation.setText("---");
                weatherDescription.setText("Chargement...");
                weatherUpdateTime.setText("Chargement...");
                
                // Cacher la prévision pour le jour de l'événement par défaut
                if (eventDayForecastBox != null) {
                    eventDayForecastBox.setVisible(false);
                    eventDayForecastBox.setManaged(false);
                }
                
                // Vérifier que weatherBox existe avant d'essayer de l'utiliser
                if (weatherBox != null) {
                    // Charger la météo pour la localisation de l'événement
                    if (evenement != null && evenement.getLocalisation() != null && !evenement.getLocalisation().isEmpty()) {
                        System.out.println("Chargement des données météo pour: " + evenement.getLocalisation());
                        loadWeatherData(evenement.getLocalisation());
                        
                        // Vérifier si nous pouvons obtenir des prévisions pour le jour de l'événement
                        if (evenement.getStartdate() != null) {
                            loadEventDayForecast(evenement.getLocalisation(), evenement.getStartdate());
                        }
                        
                        weatherBox.setVisible(true);
                        weatherBox.setManaged(true);
                    } else {
                        // Cacher la boîte météo si pas de localisation
                        weatherBox.setVisible(false);
                        weatherBox.setManaged(false);
                    }
                } else {
                    System.err.println("ERREUR: Le composant weatherBox est null");
                }
            } else {
                System.err.println("ERREUR: Un ou plusieurs composants météo sont null");
            }
        } catch (Exception e) {
            System.err.println("Exception lors de l'initialisation des données météo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Vérifie et met à jour le statut de l'événement en fonction de la date actuelle
     */
    private void updateEventStatus() {
        if (evenement != null) {
            // Si le statut a changé après vérification
            if (evenement.updateStatus()) {
                // Mettre à jour le statut dans la base de données
                evenementService.modifier(evenement);
                System.out.println("Statut de l'événement ID " + evenement.getId() + 
                                  " mis à jour vers: " + evenement.getStatus());
            }
        }
    }

    private void displayEvenementDetails() {
        if (evenement != null) {
            // Chargement de l'image
            if (evenement.getImageurl() != null && !evenement.getImageurl().isEmpty()) {
                try {
                    Image image = new Image(getClass().getResourceAsStream(evenement.getImageurl()));
                    if (image != null && !image.isError()) {
                        imgEvent.setImage(image);
                    } else {
                        // Si l'image ne peut pas être chargée, utiliser une image par défaut
                        loadDefaultImage();
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image de l'événement: " + e.getMessage());
                    loadDefaultImage();
                }
            } else {
                loadDefaultImage();
            }

            // Remplir les informations de l'événement
            lblTitre.setText(evenement.getTitre());
            lblCategorie.setText(evenement.getCategorie());
            lblGoalAmount.setText(String.format("%.2f DT", evenement.getGoalamount()));
            lblCollectedAmount.setText(String.format("%.2f DT", evenement.getCollectedamount()));
            
            // Calcul de la progression
            double progressPercent = evenement.getGoalamount() > 0 
                ? (evenement.getCollectedamount() / evenement.getGoalamount()) * 100 
                : 0;
            lblProgression.setText(String.format("%.1f%%", progressPercent));
            
            // Calcul des jours restants
            if (evenement.getEnddate() != null) {
                LocalDateTime now = LocalDateTime.now();
                long daysUntilEnd = ChronoUnit.DAYS.between(now, evenement.getEnddate());
                lblJoursRestants.setText(String.valueOf(Math.max(0, daysUntilEnd)));
                
                lblEndDate.setText(evenement.getEnddate().format(formatter));
            } else {
                lblJoursRestants.setText("N/A");
                lblEndDate.setText("Non défini");
            }
            
            if (evenement.getStartdate() != null) {
                lblStartDate.setText(evenement.getStartdate().format(formatter));
            } else {
                lblStartDate.setText("Non défini");
            }
            
            lblLocalisation.setText(evenement.getLocalisation() != null ? evenement.getLocalisation() : "Non spécifié");
            lblDescription.setText(evenement.getDescription());
            
            // Vérifier le statut de l'événement et ajuster le bouton "Faire un don" en conséquence
            if ("Terminé".equals(evenement.getStatus())) {
                btnFaireDon.setDisable(true);
                btnFaireDon.setText("Dons fermés");
                btnFaireDon.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
            } else {
                btnFaireDon.setDisable(false);
                btnFaireDon.setText("Faire un don");
                btnFaireDon.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px;");
            }
        }
    }
    
    private void initializeMap() {
        if (evenement != null && mapWebView != null) {
            String location = evenement.getLocalisation();
            if (location == null || location.isEmpty()) {
                location = "Tunis, Tunisie"; // Localisation par défaut si non spécifiée
            }
            
            // Échapper les caractères spéciaux pour l'utilisation dans le script JavaScript
            location = location.replace("'", "\\'");
            
            // Créer le contenu HTML avec Leaflet pour la carte OpenStreetMap avec des améliorations pour le rendu
            String mapContent = 
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Carte de l'événement</title>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />\n" +
                "    <style>\n" +
                "        body, html {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            overflow: hidden;\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "        }\n" +
                "        #map {\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            flex: 1;\n" +
                "            background: #f8f8f8;\n" +
                "        }\n" +
                "        .leaflet-control-attribution {\n" +
                "            font-size: 9px;\n" +
                "            background-color: rgba(255, 255, 255, 0.7);\n" +
                "            padding: 2px 5px;\n" +
                "            bottom: 2px;\n" +
                "            right: 2px;\n" +
                "        }\n" +
                "        .leaflet-control-layers {\n" +
                "            font-size: 12px;\n" +
                "            background-color: white;\n" +
                "            border-radius: 4px;\n" +
                "            box-shadow: 0 1px 5px rgba(0,0,0,0.2);\n" +
                "        }\n" +
                "        .leaflet-control-layers-toggle {\n" +
                "            background-size: 16px 16px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
                "    <script>\n" +
                "        var map, destination, startMarker, routeLine;\n" +
                "        var routeMode = false;\n" +
                "        \n" +
                "        // Fonction pour initialiser la carte avec un délai pour assurer le bon rendu\n" +
                "        function initMap() {\n" +
                "            map = L.map('map', {\n" +
                "                center: [36.8065, 10.1815], // Coordonnées par défaut (Tunis)\n" +
                "                zoom: 13,\n" +
                "                zoomControl: true,\n" +
                "                attributionControl: false  // Désactiver le contrôle d'attribution par défaut\n" +
                "            });\n" +
                "\n" +
                "            // Ajouter le contrôle d'attribution dans un coin moins visible\n" +
                "            L.control.attribution({\n" +
                "                position: 'bottomright',\n" +
                "                prefix: '<a href=\"https://leafletjs.com\" target=\"_blank\">Leaflet</a>'\n" +
                "            }).addTo(map);\n" +
                "\n" +
                "            // Définir différents calques de fond de carte\n" +
                "            var baseMaps = {\n" +
                "                'Standard': L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "                    attribution: '© OpenStreetMap contributors',\n" +
                "                    maxZoom: 19\n" +
                "                }),\n" +
                "                'Satellite': L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {\n" +
                "                    attribution: '© Esri, Maxar, Earthstar Geographics',\n" +
                "                    maxZoom: 19\n" +
                "                }),\n" +
                "                'Transport': L.tileLayer('https://{s}.tile.thunderforest.com/transport/{z}/{x}/{y}.png?apikey=6170aad10dfd42a38d4d8c709a536f38', {\n" +
                "                    attribution: '© OpenStreetMap & Thunderforest',\n" +
                "                    maxZoom: 19\n" +
                "                }),\n" +
                "                'Clair': L.tileLayer('https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}{r}.png', {\n" +
                "                    attribution: '© OpenStreetMap & CARTO',\n" +
                "                    maxZoom: 19\n" +
                "                })\n" +
                "            };\n" +
                "\n" +
                "            // Ajouter le calque Standard par défaut à la carte\n" +
                "            baseMaps['Standard'].addTo(map);\n" +
                "\n" +
                "            // Ajouter le contrôle de calques\n" +
                "            L.control.layers(baseMaps, null, {\n" +
                "                position: 'topright',\n" +
                "                collapsed: false\n" +
                "            }).addTo(map);\n" +
                "\n" +
                "            // Forcer le recalcul de la taille\n" +
                "            setTimeout(function() {\n" +
                "                map.invalidateSize(true);\n" +
                "            }, 500);\n" +
                "\n" +
                "            var locationName = '" + location + "';\n" +
                "            var geocodeUrl = 'https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(locationName);\n" +
                "\n" +
                "            // Ajouter un User-Agent pour respecter les conditions d'utilisation de Nominatim\n" +
                "            fetch(geocodeUrl, {\n" +
                "                headers: {\n" +
                "                    'User-Agent': 'ArtizinaApp/1.0'\n" +
                "                }\n" +
                "            })\n" +
                "            .then(response => response.json())\n" +
                "            .then(data => {\n" +
                "                if (data && data.length > 0) {\n" +
                "                    var lat = parseFloat(data[0].lat);\n" +
                "                    var lon = parseFloat(data[0].lon);\n" +
                "                    \n" +
                "                    // Définir la vue sur les coordonnées et ajouter un marqueur\n" +
                "                    map.setView([lat, lon], 15);\n" +
                "                    \n" +
                "                    var marker = L.marker([lat, lon]).addTo(map);\n" +
                "                    marker.bindPopup('<b>" + location + "</b>').openPopup();\n" +
                "                    \n" +
                "                    // Stocker la position de destination pour le calcul d'itinéraire\n" +
                "                    destination = L.latLng(lat, lon);\n" +
                "                    \n" +
                "                    // Ajouter un cercle autour du marqueur\n" +
                "                    L.circle([lat, lon], {\n" +
                "                        color: '#3498db',\n" +
                "                        fillColor: '#3498db',\n" +
                "                        fillOpacity: 0.1,\n" +
                "                        radius: 200  // Réduire le rayon du cercle de 500m à 200m\n" +
                "                    }).addTo(map);\n" +
                "                    \n" +
                "                    // Forcer à nouveau le recalcul après avoir défini la vue\n" +
                "                    setTimeout(function() {\n" +
                "                        map.invalidateSize(true);\n" +
                "                    }, 800);\n" +
                "                } else {\n" +
                "                    console.error('Location not found: ' + locationName);\n" +
                "                    // Afficher un message d'erreur sur la carte\n" +
                "                    var popup = L.popup()\n" +
                "                        .setLatLng([36.8065, 10.1815])\n" +
                "                        .setContent('<p>Localisation non trouvée: ' + locationName + '</p>')\n" +
                "                        .openOn(map);\n" +
                "                }\n" +
                "            })\n" +
                "            .catch(error => {\n" +
                "                console.error('Error fetching location data:', error);\n" +
                "                // Afficher un message d'erreur sur la carte\n" +
                "                var popup = L.popup()\n" +
                "                    .setLatLng([36.8065, 10.1815])\n" +
                "                    .setContent('<p>Erreur de chargement de la carte</p>')\n" +
                "                    .openOn(map);\n" +
                "            });\n" +
                "            \n" +
                "            // Gérer les événements de redimensionnement\n" +
                "            window.addEventListener('resize', function() {\n" +
                "                map.invalidateSize(true);\n" +
                "            });\n" +
                "        }\n" +
                "        \n" +
                "        // Activer le mode de sélection d'un point de départ sur la carte\n" +
                "        function enableMapClickForRoute(dest) {\n" +
                "            routeMode = true;\n" +
                "            alert('Cliquez sur la carte pour définir le point de départ');\n" +
                "            \n" +
                "            // Ajouter un événement de clic sur la carte\n" +
                "            map.once('click', function(e) {\n" +
                "                var startPoint = e.latlng;\n" +
                "                calculateRouteFrom(startPoint, dest);\n" +
                "                routeMode = false;\n" +
                "            });\n" +
                "        }\n" +
                "        \n" +
                "        // Calculer et afficher l'itinéraire\n" +
                "        function calculateRouteFrom(start, end) {\n" +
                "            // Supprimer l'itinéraire précédent s'il existe\n" +
                "            if (routeLine) {\n" +
                "                map.removeLayer(routeLine);\n" +
                "            }\n" +
                "            \n" +
                "            // Supprimer le marqueur de départ précédent s'il existe\n" +
                "            if (startMarker) {\n" +
                "                map.removeLayer(startMarker);\n" +
                "            }\n" +
                "            \n" +
                "            // Ajouter un marqueur pour le point de départ\n" +
                "            startMarker = L.marker(start, {\n" +
                "                icon: L.divIcon({\n" +
                "                    html: '<div style=\"background-color:#4CAF50;width:12px;height:12px;border-radius:50%;border:2px solid white;\"></div>',\n" +
                "                    className: 'start-marker-icon',\n" +
                "                    iconSize: [16, 16],\n" +
                "                    iconAnchor: [8, 8]\n" +
                "                })\n" +
                "            }).addTo(map).bindPopup('Départ').openPopup();\n" +
                "            \n" +
                "            // Construire l'URL pour l'API OSRM\n" +
                "            var url = 'https://router.project-osrm.org/route/v1/driving/' + \n" +
                "                      start.lng + ',' + start.lat + ';' +\n" +
                "                      end.lng + ',' + end.lat +\n" +
                "                      '?overview=full&geometries=geojson';\n" +
                "            \n" +
                "            // Récupérer l'itinéraire\n" +
                "            fetch(url)\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    if (data.routes && data.routes.length > 0) {\n" +
                "                        // Extraire la géométrie de l'itinéraire\n" +
                "                        var routeGeometry = data.routes[0].geometry;\n" +
                "                        \n" +
                "                        // Créer une ligne pour l'itinéraire\n" +
                "                        routeLine = L.geoJSON(routeGeometry, {\n" +
                "                            style: {\n" +
                "                                color: '#3498db',\n" +
                "                                weight: 5,\n" +
                "                                opacity: 0.7,\n" +
                "                                dashArray: '10, 10',\n" +
                "                                lineJoin: 'round'\n" +
                "                            }\n" +
                "                        }).addTo(map);\n" +
                "                        \n" +
                "                        // Ajuster la vue pour voir l'itinéraire complet\n" +
                "                        map.fitBounds(routeLine.getBounds(), {\n" +
                "                            padding: [50, 50]\n" +
                "                        });\n" +
                "                        \n" +
                "                        // Afficher la distance et le temps estimé\n" +
                "                        var distance = (data.routes[0].distance / 1000).toFixed(1);\n" +
                "                        var duration = Math.round(data.routes[0].duration / 60);\n" +
                "                        var popupContent = '<b>Distance:</b> ' + distance + ' km<br>' +\n" +
                "                                         '<b>Temps estimé:</b> ' + duration + ' min';\n" +
                "                        \n" +
                "                        startMarker.bindPopup(popupContent).openPopup();\n" +
                "                    } else {\n" +
                "                        alert('Impossible de calculer l\\'itinéraire. Veuillez essayer un autre point de départ.');\n" +
                "                    }\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    console.error('Erreur lors du calcul de l\\'itinéraire:', error);\n" +
                "                    alert('Erreur lors du calcul de l\\'itinéraire. Veuillez réessayer.');\n" +
                "                });\n" +
                "        }\n" +
                "        \n" +
                "        // Initialiser la carte après le chargement complet de la page\n" +
                "        window.onload = function() {\n" +
                "            setTimeout(initMap, 300);\n" +
                "        };\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
            
            // Charger le contenu HTML dans le WebView
            mapWebView.getEngine().loadContent(mapContent);
            
            // Définir une hauteur fixe pour le WebView pour éviter les problèmes de taille
            mapWebView.setPrefHeight(400);
            mapWebView.setMinHeight(350);
            mapWebView.setMaxHeight(500);
            
            // Écouter les changements de taille pour s'assurer que la carte s'adapte correctement
            mapWebView.widthProperty().addListener((obs, oldVal, newVal) -> {
                mapWebView.getEngine().executeScript("if(window.map) window.map.invalidateSize();");
            });
            
            mapWebView.heightProperty().addListener((obs, oldVal, newVal) -> {
                mapWebView.getEngine().executeScript("if(window.map) window.map.invalidateSize();");
            });
        }
    }
    
    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/tn/esprit/workshop/resources/placeholder.png"));
            imgEvent.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EvenementsList.fxml"));
            Parent root = loader.load();
            
            // Accéder au conteneur parent (EvenementsController)
            StackPane container = (StackPane) btnRetour.getScene().lookup("#evenementsContainer");
            if (container != null) {
                container.getChildren().clear();
                container.getChildren().add(root);
            } else {
                // Fallback si le conteneur n'est pas trouvé
                Scene scene = new Scene(root);
                Stage stage = (Stage) btnRetour.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste: " + e.getMessage());
        }
    }

    @FXML
    private void handleFaireDon(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DonForm.fxml"));
            Parent root = loader.load();
            
            // Passer l'événement au contrôleur du formulaire
            DonFormController controller = loader.getController();
            controller.setEvenement(evenement);
            controller.setReturnEventId(evenement.getId());
            
            // Accéder au conteneur parent (EvenementsController)
            StackPane container = (StackPane) btnFaireDon.getScene().lookup("#evenementsContainer");
            if (container != null) {
                container.getChildren().clear();
                container.getChildren().add(root);
            } else {
                // Fallback si le conteneur n'est pas trouvé
                Scene scene = new Scene(root);
                Stage stage = (Stage) btnFaireDon.getScene().getWindow();
                stage.setScene(scene);
            }
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de don: " + e.getMessage());
        }
    }

    @FXML
    private void handleItineraireDepuisPosition(ActionEvent event) {
        // Utiliser une position fixe (Esprit Block M) au lieu de la géolocalisation
        // Coordonnées extraites de l'URL Google Maps : 36.9021262, 10.1893184
        String script = "if (typeof destination !== 'undefined') {\n" +
                        "    // Position fixe de Esprit Block M au lieu de la géolocalisation\n" +
                        "    var espritPosition = L.latLng(36.9021262, 10.1893184);\n" +
                        "    \n" +
                        "    // Afficher un marqueur pour Esprit (point de départ)\n" +
                        "    if (startMarker) map.removeLayer(startMarker);\n" +
                        "    startMarker = L.marker(espritPosition, {\n" +
                        "        icon: L.divIcon({\n" +
                        "            html: '<div style=\"background-color:#4CAF50;width:12px;height:12px;border-radius:50%;border:2px solid white;\"></div>',\n" +
                        "            className: 'start-marker-icon',\n" +
                        "            iconSize: [16, 16],\n" +
                        "            iconAnchor: [8, 8]\n" +
                        "        })\n" +
                        "    }).addTo(map);\n" +
                        "    startMarker.bindPopup('Esprit Block M (Votre position)').openPopup();\n" +
                        "    \n" +
                        "    // Calculer l'itinéraire à partir de cette position fixe\n" +
                        "    calculateRouteFrom(espritPosition, destination);\n" +
                        "} else {\n" +
                        "    alert('La destination n\\'est pas définie correctement.');\n" +
                        "}";
        
        mapWebView.getEngine().executeScript(script);
    }
    
    @FXML
    private void handleItinerairePointCarte(ActionEvent event) {
        // Choisir un point sur la carte
        String script = "if (typeof destination !== 'undefined') {\n" +
                        "    enableMapClickForRoute(destination);\n" +
                        "} else {\n" +
                        "    alert('La destination n\\'est pas définie correctement.');\n" +
                        "}";
        
        mapWebView.getEngine().executeScript(script);
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Charge les données météo pour une localisation donnée
     * @param location Le nom de la localisation
     */
    private void loadWeatherData(String location) {
        // Vérifier que la localisation est valide
        if (location == null || location.trim().isEmpty()) {
            System.err.println("Localisation vide, impossible de charger les données météo");
            return;
        }
        
        // S'assurer que weatherBox existe
        if (weatherBox == null) {
            System.err.println("ERREUR: weatherBox est null, impossible de charger les données météo");
            return;
        }

        System.out.println("Démarrage du chargement des données météo pour: " + location);
        
        // Utiliser CompletableFuture pour exécuter l'appel API de manière asynchrone
        CompletableFuture.supplyAsync(() -> {
            try {
                // D'abord obtenir les coordonnées géographiques de la localisation
                JSONObject geoData = getGeoLocation(location);
                if (geoData != null) {
                    double lat = geoData.getDouble("lat");
                    double lon = geoData.getDouble("lon");
                    
                    // Ensuite obtenir les données météo à partir des coordonnées
                    return getWeatherData(lat, lon);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des données météo: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }).thenAcceptAsync(weatherData -> {
            try {
                // Mettre à jour l'interface avec les données météo (sur le thread JavaFX)
                updateWeatherUI(weatherData);
            } catch (Exception e) {
                System.err.println("Exception lors de la mise à jour de l'interface météo: " + e.getMessage());
                e.printStackTrace();
            }
        }, javafx.application.Platform::runLater)
        .exceptionally(ex -> {
            System.err.println("Exception fatale lors du chargement des données météo: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
    }
    
    /**
     * Récupère les coordonnées géographiques d'une localisation
     * @param location Le nom de la localisation
     * @return Un objet JSON contenant lat et lon, ou null en cas d'erreur
     */
    private JSONObject getGeoLocation(String location) throws IOException {
        String encodedLocation = java.net.URLEncoder.encode(location, "UTF-8");
        String urlStr = "https://api.openweathermap.org/geo/1.0/direct?q=" + encodedLocation + "&limit=1&appid=" + OPENWEATHER_API_KEY;
        
        System.out.println("Requête géolocalisation: " + urlStr);
        
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            org.json.JSONArray jsonArray = new org.json.JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                return jsonArray.getJSONObject(0);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des coordonnées géographiques: " + e.getMessage());
        } finally {
            connection.disconnect();
        }
        
        return null;
    }
    
    /**
     * Récupère les données météo pour des coordonnées géographiques
     * @param lat Latitude
     * @param lon Longitude
     * @return Un objet JSON contenant les données météo, ou null en cas d'erreur
     */
    private JSONObject getWeatherData(double lat, double lon) throws IOException {
        String urlStr = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + 
                "&units=metric&lang=fr&appid=" + OPENWEATHER_API_KEY;
        
        System.out.println("Requête météo: " + urlStr);
        
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000); // 10 secondes timeout
        connection.setReadTimeout(10000);    // 10 secondes timeout
        
        int responseCode = connection.getResponseCode();
        System.out.println("Code de réponse météo: " + responseCode);
        
        try {
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String jsonResponse = response.toString();
                    System.out.println("Réponse météo reçue: " + jsonResponse.substring(0, Math.min(100, jsonResponse.length())) + "...");
                    
                    return new JSONObject(jsonResponse);
                }
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.err.println("Erreur API météo: " + response.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Exception lors de la récupération des données météo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        
        return null;
    }
    
    /**
     * Met à jour l'interface utilisateur avec les données météo
     * @param weatherData Les données météo sous forme d'objet JSON
     */
    private void updateWeatherUI(JSONObject weatherData) {
        // Vérifier que les composants UI existent
        if (weatherBox == null) {
            System.err.println("ERREUR: weatherBox est null. Widget météo non initialisé correctement.");
            return;
        }
        
        if (weatherData == null) {
            // Formatage correspondant à la capture d'écran pour l'erreur météo
            if (weatherDescription != null) weatherDescription.setText("Erreur météo");
            if (weatherTemp != null) weatherTemp.setText("--°C");
            if (weatherFeelsLike != null) weatherFeelsLike.setText("Ressenti: --°C");
            if (weatherHumidity != null) weatherHumidity.setText("--");
            if (weatherWind != null) weatherWind.setText("-- m/s");
            if (weatherSunrise != null) weatherSunrise.setText("--:--");
            if (weatherSunset != null) weatherSunset.setText("--:--");
            if (weatherLocation != null && evenement != null) {
                // Extraction de la ville et du pays si possible
                String location = evenement.getLocalisation();
                if (location != null && !location.isEmpty()) {
                    String[] parts = location.split(",");
                    if (parts.length > 0) {
                        String city = parts[0].trim();
                        String country = parts.length > 1 ? parts[1].trim() : "";
                        // Format: "Ville, PAYS" (comme dans la capture "Montreux, CH")
                        weatherLocation.setText(city + (country.isEmpty() ? "" : ", " + country));
                    } else {
                        weatherLocation.setText(location);
                    }
                } else {
                    weatherLocation.setText("--");
                }
            }
            if (weatherUpdateTime != null) {
                // Formater la date comme dans la capture
                java.text.SimpleDateFormat updateFormat = new java.text.SimpleDateFormat("dd/MM HH:mm");
                weatherUpdateTime.setText("Mis à jour: " + updateFormat.format(new java.util.Date()));
            }
            
            // Assurer que les styles correspondent à la capture d'écran:
            // - Boîte extérieure blanche avec bordure grise
            // - Widget intérieur bleu
            if (weatherWidget != null) {
                weatherWidget.setStyle("-fx-background-color: #00558b; -fx-background-radius: 8px;");
            }
            
            weatherBox.setVisible(true);
            weatherBox.setManaged(true);
            
            // Essayer une nouvelle tentative après un délai si c'est la première erreur
            if (!weatherRetried && evenement != null && evenement.getLocalisation() != null) {
                weatherRetried = true;
                new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                System.out.println("Nouvelle tentative de récupération météo...");
                                loadWeatherData(evenement.getLocalisation());
                            });
                        }
                    }, 3000 // délai de 3 secondes
                );
            }
            return;
        }
        
        try {
            // Extraire les données météo du JSON
            JSONObject main = weatherData.getJSONObject("main");
            double temperature = main.getDouble("temp");
            double feelsLike = main.getDouble("feels_like");
            int humidity = main.getInt("humidity");
            
            JSONObject weather = weatherData.getJSONArray("weather").getJSONObject(0);
            String description = weather.getString("description");
            String iconCode = weather.getString("icon");
            
            // Vitesse du vent
            JSONObject wind = weatherData.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");
            
            // Lever et coucher du soleil
            JSONObject sys = weatherData.getJSONObject("sys");
            long sunriseTimestamp = sys.getLong("sunrise");
            long sunsetTimestamp = sys.getLong("sunset");
            
            // Nom de la localisation
            String locationName = weatherData.getString("name");
            String countryCode = sys.getString("country");
            
            // Formatage des heures de lever et coucher du soleil
            java.util.Date sunriseDate = new java.util.Date(sunriseTimestamp * 1000);
            java.util.Date sunsetDate = new java.util.Date(sunsetTimestamp * 1000);
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
            String sunriseTime = timeFormat.format(sunriseDate);
            String sunsetTime = timeFormat.format(sunsetDate);
            
            // Mettre à jour les éléments de l'interface avec vérification de nullité
            if (weatherDescription != null) weatherDescription.setText(description.substring(0, 1).toUpperCase() + description.substring(1));
            
            // Formater correctement la température sans décimales comme dans la capture d'écran
            if (weatherTemp != null) weatherTemp.setText(String.format("%.0f°C", temperature));
            if (weatherFeelsLike != null) weatherFeelsLike.setText(String.format("Ressenti: %.1f°C", feelsLike));
            if (weatherHumidity != null) weatherHumidity.setText(humidity + "%");
            if (weatherWind != null) weatherWind.setText(String.format("%.1f m/s", windSpeed));
            if (weatherSunrise != null) weatherSunrise.setText(sunriseTime);
            if (weatherSunset != null) weatherSunset.setText(sunsetTime);
            if (weatherLocation != null) weatherLocation.setText(locationName + ", " + countryCode);
            
            // Horodatage de la mise à jour
            java.text.SimpleDateFormat updateFormat = new java.text.SimpleDateFormat("dd/MM HH:mm");
            if (weatherUpdateTime != null) weatherUpdateTime.setText("Mis à jour: " + updateFormat.format(new java.util.Date()));
            
            // Charger l'icône météo depuis l'API OpenWeatherMap
            if (weatherIcon != null) {
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                try {
                    Image weatherImg = new Image(iconUrl, true); // true = load in background
                    weatherImg.progressProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal.doubleValue() == 1.0 && weatherIcon != null) {
                            weatherIcon.setImage(weatherImg);
                        }
                    });
                    weatherImg.errorProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal) {
                            System.err.println("Erreur lors du chargement de l'icône météo");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'icône météo: " + e.getMessage());
                }
            }
            
            // Assurer que le style est correct
            weatherBox.setStyle("-fx-background-color: #00558b; -fx-background-radius: 8px;");
            
            // Rendre la boîte météo visible
            weatherBox.setVisible(true);
            weatherBox.setManaged(true);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'interface météo: " + e.getMessage());
            e.printStackTrace();
            if (weatherDescription != null) weatherDescription.setText("Données incompatibles");
            if (weatherTemp != null) weatherTemp.setText("--°C");
            if (weatherLocation != null && evenement != null) weatherLocation.setText(evenement.getLocalisation());
        }
    }

    /**
     * Charge les prévisions météo pour le jour de l'événement, si disponibles
     * @param location Le nom de la localisation
     * @param eventDate La date de début de l'événement
     */
    private void loadEventDayForecast(String location, LocalDateTime eventDate) {
        // Vérifier que les composants UI existent
        if (eventDayForecastBox == null || eventDayTemp == null || eventDayDescription == null) {
            System.err.println("ERREUR: Composants de prévision météo pour l'événement non initialisés");
            return;
        }
        
        // Vérifier si la date de l'événement est dans les 5 prochains jours (limite de l'API gratuite)
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), eventDate.toLocalDate());
        
        if (daysBetween < 0) {
            // L'événement est dans le passé - pas de prévisions possibles
            System.out.println("L'événement est dans le passé, impossible d'obtenir des prévisions météo");
            return;
        }
        
        if (daysBetween > 5) {
            // L'événement est à plus de 5 jours, hors de la période de prévision de l'API gratuite
            System.out.println("L'événement est à plus de 5 jours, hors de la portée de l'API de prévisions");
            
            // Afficher un message spécial - prévisions non disponibles
            eventDayDate.setText(eventDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            eventDayTemp.setText("--°C");
            eventDayDescription.setText("Prévisions non disponibles");
            eventDayForecastBox.setVisible(true);
            eventDayForecastBox.setManaged(true);
            return;
        }
        
        // L'événement est dans les 5 prochains jours, nous pouvons obtenir des prévisions
        System.out.println("Chargement des prévisions météo pour l'événement le " + eventDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        // Utiliser CompletableFuture pour exécuter l'appel API de manière asynchrone
        CompletableFuture.supplyAsync(() -> {
            try {
                // D'abord obtenir les coordonnées géographiques de la localisation
                JSONObject geoData = getGeoLocation(location);
                if (geoData != null) {
                    double lat = geoData.getDouble("lat");
                    double lon = geoData.getDouble("lon");
                    
                    // Obtenir les prévisions à 5 jours
                    return getForecastData(lat, lon);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des prévisions météo: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }).thenAcceptAsync(forecastData -> {
            try {
                // Mettre à jour l'interface avec les prévisions météo (sur le thread JavaFX)
                updateEventDayForecastUI(forecastData, eventDate);
            } catch (Exception e) {
                System.err.println("Exception lors de la mise à jour des prévisions météo: " + e.getMessage());
                e.printStackTrace();
            }
        }, javafx.application.Platform::runLater)
        .exceptionally(ex -> {
            System.err.println("Exception fatale lors du chargement des prévisions météo: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
    }

    /**
     * Récupère les données de prévisions météo pour des coordonnées géographiques
     * @param lat Latitude
     * @param lon Longitude
     * @return Un objet JSON contenant les prévisions météo, ou null en cas d'erreur
     */
    private JSONObject getForecastData(double lat, double lon) throws IOException {
        String urlStr = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + 
                "&units=metric&lang=fr&appid=" + OPENWEATHER_API_KEY;
        
        System.out.println("Requête prévisions météo: " + urlStr);
        
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000); // 10 secondes timeout
        connection.setReadTimeout(10000);    // 10 secondes timeout
        
        int responseCode = connection.getResponseCode();
        System.out.println("Code de réponse prévisions météo: " + responseCode);
        
        try {
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String jsonResponse = response.toString();
                    System.out.println("Réponse prévisions météo reçue: " + jsonResponse.substring(0, Math.min(100, jsonResponse.length())) + "...");
                    
                    return new JSONObject(jsonResponse);
                }
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.err.println("Erreur API prévisions météo: " + response.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Exception lors de la récupération des prévisions météo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        
        return null;
    }

    /**
     * Met à jour l'interface utilisateur avec les prévisions météo pour le jour de l'événement
     */
    private void updateEventDayForecastUI(JSONObject forecastData, LocalDateTime eventDate) {
        // Si les données de prévision sont nulles, ne pas afficher la section
        if (forecastData == null || eventDayForecastBox == null) {
            return;
        }
        
        try {
            // Format de la date de l'événement pour l'affichage
            String eventDateString = eventDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            eventDayDate.setText(eventDateString);
            
            // Formater la date de l'événement pour la comparaison avec les prévisions
            // Les prévisions API sont données toutes les 3 heures
            String eventDateForComparison = eventDate.toLocalDate().toString();
            
            // Extraire la liste des prévisions
            org.json.JSONArray forecastList = forecastData.getJSONArray("list");
            
            // Trouver la prévision la plus proche du jour de l'événement
            JSONObject closestForecast = null;
            int eventHour = eventDate.getHour();
            
            // Parcourir toutes les prévisions pour trouver celle qui correspond au jour de l'événement
            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecast = forecastList.getJSONObject(i);
                String forecastDateTimeString = forecast.getString("dt_txt");
                
                // Vérifier si cette prévision correspond au jour de l'événement
                if (forecastDateTimeString.startsWith(eventDateForComparison)) {
                    // Si c'est la première correspondance ou si c'est plus proche de l'heure de l'événement
                    if (closestForecast == null) {
                        closestForecast = forecast;
                    } else {
                        // Extraire l'heure de la prévision
                        int forecastHour = Integer.parseInt(forecastDateTimeString.split(" ")[1].split(":")[0]);
                        int currentClosestHour = Integer.parseInt(closestForecast.getString("dt_txt").split(" ")[1].split(":")[0]);
                        
                        // Calculer quelle heure est la plus proche de l'heure de l'événement
                        if (Math.abs(forecastHour - eventHour) < Math.abs(currentClosestHour - eventHour)) {
                            closestForecast = forecast;
                        }
                    }
                }
            }
            
            // Si nous avons trouvé une prévision pour le jour de l'événement
            if (closestForecast != null) {
                // Extraire les données météo
                JSONObject main = closestForecast.getJSONObject("main");
                double temperature = main.getDouble("temp");
                
                JSONObject weather = closestForecast.getJSONArray("weather").getJSONObject(0);
                String description = weather.getString("description");
                String iconCode = weather.getString("icon");
                
                // Mettre à jour l'interface
                eventDayTemp.setText(String.format("%.0f°C", temperature));
                eventDayDescription.setText(description.substring(0, 1).toUpperCase() + description.substring(1));
                
                // Charger l'icône météo
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                try {
                    Image weatherImg = new Image(iconUrl, true);
                    eventDayWeatherIcon.setImage(weatherImg);
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'icône météo pour l'événement: " + e.getMessage());
                }
                
                // Rendre visible la section de prévision pour le jour de l'événement
                eventDayForecastBox.setVisible(true);
                eventDayForecastBox.setManaged(true);
            } else {
                // Pas de prévision trouvée pour le jour exact, mais l'événement est dans les 5 jours
                // Cela peut arriver si les prévisions ne couvrent pas exactement 5 jours complets
                eventDayTemp.setText("--°C");
                eventDayDescription.setText("Prévisions non disponibles");
                eventDayForecastBox.setVisible(true);
                eventDayForecastBox.setManaged(true);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des prévisions météo: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 