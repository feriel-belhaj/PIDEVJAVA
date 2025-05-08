package tn.esprit.workshop.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.MouseInput;
import javafx.scene.layout.Pane;
import java.awt.Canvas;
import javafx.embed.swing.SwingNode;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;

public class Model3DViewer extends SimpleApplication {
    private String modelPath;
    private Spatial model;
    private float rotationSpeed = 1f;
    private SwingNode swingNode;
    private CountDownLatch initLatch = new CountDownLatch(1);
    private float zoomSpeed = 2.0f;
    private float moveSpeed = 0.5f;
    private float currentZoom = 5.0f;
    private float minZoom = 1.0f;
    private float maxZoom = 20.0f;
    private float autoRotationSpeed = 0.5f;
    private boolean isAutoRotating = true;

    // Actions pour les contrôles
    private static final String ROTATE_LEFT = "RotateLeft";
    private static final String ROTATE_RIGHT = "RotateRight";
    private static final String ZOOM_IN = "ZoomIn";
    private static final String ZOOM_OUT = "ZoomOut";
    private static final String MOVE_LEFT = "MoveLeft";
    private static final String MOVE_RIGHT = "MoveRight";
    private static final String MOVE_UP = "MoveUp";
    private static final String MOVE_DOWN = "MoveDown";
    private static final String TOGGLE_ROTATION = "ToggleRotation";
    private static final String MOUSE_DRAG = "MouseDrag";
    private static final String MOUSE_ROTATE = "MouseRotate";

    public Model3DViewer(String modelPath) {
        this.modelPath = modelPath;
        this.swingNode = new SwingNode();

        AppSettings settings = new AppSettings(true);
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setSamples(4);
        settings.setFrameRate(60);
        settings.setVSync(true);
        settings.setGammaCorrection(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL2);
        this.setSettings(settings);
        this.createCanvas();
        
        new Thread(() -> {
            try {
                start();
                initLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        try {
            initLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void simpleInitApp() {
        System.out.println("Initialisation du viewer 3D");
        
        // Rendre le curseur visible
        inputManager.setCursorVisible(true);
        
        // Configuration de la caméra
        cam.setLocation(new Vector3f(0, 0, currentZoom));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        
        // Fond blanc
        viewPort.setBackgroundColor(ColorRGBA.White);
        
        // Éclairage
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.5f));
        rootNode.addLight(sun);
        
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(1.0f));
        rootNode.addLight(ambient);
        
        try {
            System.out.println("Tentative de chargement du modèle: " + modelPath);
            String resourcePath = "uploads/models/" + modelPath.substring(modelPath.lastIndexOf("/") + 1);
            System.out.println("Chemin de ressource: " + resourcePath);
            model = assetManager.loadModel(resourcePath);
            
            model.setLocalTranslation(0, 0, 0);
            model.scale(0.5f);
            model.rotate(0, FastMath.PI * 0.5f, 0);
            
            rootNode.attachChild(model);
            
            System.out.println("Modèle chargé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du modèle: " + e.getMessage());
            e.printStackTrace();
        }

        // Configuration des contrôles
        setupControls();
    }

    private void setupControls() {
        // Mapping des touches
        inputManager.addMapping(ROTATE_LEFT, new KeyTrigger(com.jme3.input.KeyInput.KEY_LEFT));
        inputManager.addMapping(ROTATE_RIGHT, new KeyTrigger(com.jme3.input.KeyInput.KEY_RIGHT));
        inputManager.addMapping(ZOOM_IN, new KeyTrigger(com.jme3.input.KeyInput.KEY_UP));
        inputManager.addMapping(ZOOM_OUT, new KeyTrigger(com.jme3.input.KeyInput.KEY_DOWN));
        inputManager.addMapping(MOVE_LEFT, new KeyTrigger(com.jme3.input.KeyInput.KEY_A));
        inputManager.addMapping(MOVE_RIGHT, new KeyTrigger(com.jme3.input.KeyInput.KEY_D));
        inputManager.addMapping(MOVE_UP, new KeyTrigger(com.jme3.input.KeyInput.KEY_W));
        inputManager.addMapping(MOVE_DOWN, new KeyTrigger(com.jme3.input.KeyInput.KEY_S));
        inputManager.addMapping(TOGGLE_ROTATION, new KeyTrigger(com.jme3.input.KeyInput.KEY_SPACE));

        // Contrôles de la souris
        inputManager.addMapping(MOUSE_DRAG, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(MOUSE_ROTATE, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(ZOOM_IN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping(ZOOM_OUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));

        // Ajout des listeners
        inputManager.addListener(analogListener, 
            ROTATE_LEFT, ROTATE_RIGHT, ZOOM_IN, ZOOM_OUT,
            MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN);
        inputManager.addListener(actionListener, TOGGLE_ROTATION, MOUSE_DRAG, MOUSE_ROTATE);
    }

    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (model == null) return;

            switch (name) {
                case ROTATE_LEFT:
                    model.rotate(0, tpf * rotationSpeed, 0);
                    break;
                case ROTATE_RIGHT:
                    model.rotate(0, -tpf * rotationSpeed, 0);
                    break;
                case ZOOM_IN:
                    currentZoom = Math.max(minZoom, currentZoom - tpf * zoomSpeed);
                    updateCameraZoom();
                    break;
                case ZOOM_OUT:
                    currentZoom = Math.min(maxZoom, currentZoom + tpf * zoomSpeed);
                    updateCameraZoom();
                    break;
                case MOVE_LEFT:
                    model.move(-tpf * moveSpeed, 0, 0);
                    break;
                case MOVE_RIGHT:
                    model.move(tpf * moveSpeed, 0, 0);
                    break;
                case MOVE_UP:
                    model.move(0, tpf * moveSpeed, 0);
                    break;
                case MOVE_DOWN:
                    model.move(0, -tpf * moveSpeed, 0);
                    break;
            }
        }
    };

    private void updateCameraZoom() {
        Vector3f direction = cam.getDirection().normalize();
        cam.setLocation(direction.mult(currentZoom));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(TOGGLE_ROTATION)) {
                isAutoRotating = !isAutoRotating;
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        if (model != null && isAutoRotating) {
            model.rotate(0, tpf * autoRotationSpeed, 0);
        }
    }

    public void embedIn(Pane container) {
        System.out.println("Intégration du viewer dans le conteneur");
        Canvas canvas = ((JmeCanvasContext) getContext()).getCanvas();
        canvas.setBackground(java.awt.Color.WHITE);
        
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setBackground(java.awt.Color.WHITE);
        jPanel.add(canvas, BorderLayout.CENTER);
        
        swingNode.setContent(jPanel);
        container.getChildren().add(swingNode);
        
        container.setPrefWidth(800);
        container.setPrefHeight(600);
        container.setMinWidth(800);
        container.setMinHeight(600);
        
        System.out.println("Viewer intégré avec succès");
    }

    public void cleanup() {
        try {
            if (context != null) {
                stop();
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}