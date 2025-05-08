package tn.esprit.workshop.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.AmbientLight;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.asset.plugins.FileLocator;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.embed.swing.SwingNode;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javafx.application.Platform;
import java.io.File;
import com.jme3.math.FastMath;

public class Model3DViewerController {
    @FXML
    private AnchorPane viewerContainer;
    
    private SimpleApplication jmeApp;
    private Stage stage;
    
    public void initialize() {
        // Initialize JME application
        jmeApp = new SimpleApplication() {
            @Override
            public void simpleInitApp() {
                // Désactiver les contrôles de vol par défaut
                flyCam.setEnabled(false);
                
                // Configuration de la caméra
                cam.setLocation(new Vector3f(0, 0, 2));
                cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
                
                // Fond blanc pour mieux voir
                viewPort.setBackgroundColor(ColorRGBA.White);
                
                // Éclairage plus intense
                DirectionalLight sun = new DirectionalLight();
                sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
                sun.setColor(ColorRGBA.White.mult(1.5f));
                rootNode.addLight(sun);
                
                // Lumière ambiante plus forte
                AmbientLight ambient = new AmbientLight();
                ambient.setColor(ColorRGBA.White.mult(1.0f));
                rootNode.addLight(ambient);

                // Add asset roots
                assetManager.registerLocator("src/main/resources", FileLocator.class);
                assetManager.registerLocator("src/main/resources/uploads", FileLocator.class);
                assetManager.registerLocator("src/main/resources/uploads/models", FileLocator.class);
            }
        };
        
        // Configure JME settings
        AppSettings settings = new AppSettings(true);
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setSamples(4);
        settings.setFrameRate(60);
        settings.setVSync(true);
        settings.setGammaCorrection(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL2);
        jmeApp.setSettings(settings);
        jmeApp.createCanvas();
        
        // Create a JPanel to hold the JME canvas
        JPanel jmePanel = new JPanel(new BorderLayout());
        jmePanel.setPreferredSize(new Dimension(800, 600));
        jmePanel.setBackground(java.awt.Color.WHITE);
        
        // Get the JME canvas context and add it to the panel
        JmeCanvasContext context = (JmeCanvasContext) jmeApp.getContext();
        context.getCanvas().setBackground(java.awt.Color.WHITE);
        jmePanel.add(context.getCanvas(), BorderLayout.CENTER);
        
        // Create SwingNode and add the panel
        Platform.runLater(() -> {
            SwingNode swingNode = new SwingNode();
            swingNode.setContent(jmePanel);
            
            viewerContainer.getChildren().add(swingNode);
            AnchorPane.setTopAnchor(swingNode, 0.0);
            AnchorPane.setBottomAnchor(swingNode, 0.0);
            AnchorPane.setLeftAnchor(swingNode, 0.0);
            AnchorPane.setRightAnchor(swingNode, 0.0);
        });
        
        // Start JME in a separate thread
        new Thread(() -> jmeApp.start()).start();
    }
    
    public void loadModel(String modelPath) {
        if (jmeApp != null) {
            jmeApp.enqueue(() -> {
                try {
                    // Utiliser le même format de chemin que l'ancien Model3DViewer
                    String resourcePath = "uploads/models/" + modelPath.substring(modelPath.lastIndexOf("/") + 1);
                    System.out.println("Tentative de chargement du modèle: " + resourcePath);
                    
                    // Clear any existing models
                    jmeApp.getRootNode().detachAllChildren();
                    
                    // Load the 3D model
                    Spatial model = jmeApp.getAssetManager().loadModel(resourcePath);
                    if (model == null) {
                        throw new RuntimeException("Échec du chargement du modèle");
                    }
                    
                    // Add the new model
                    jmeApp.getRootNode().attachChild(model);
                    
                    // Centrer et ajuster le modèle
                    model.setLocalTranslation(0, 0, 0);
                    model.scale(0.5f);
                    
                    // Rotation initiale pour mieux voir le modèle
                    model.rotate(0, FastMath.PI * 0.5f, 0);
                    
                    System.out.println("Modèle chargé avec succès");
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR,
                            "Erreur lors du chargement du modèle 3D: " + e.getMessage()
                        );
                        alert.showAndWait();
                    });
                    return null;
                }
            });
        }
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void handleClose() {
        if (jmeApp != null) {
            jmeApp.stop();
        }
        if (stage != null) {
            stage.close();
        }
    }
} 