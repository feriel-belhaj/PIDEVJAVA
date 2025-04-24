//package tn.esprit.workshop.gui;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.VBox;
//import tn.esprit.workshop.models.CameraCapture;
//import tn.esprit.workshop.models.FaceRecognitionResponse;
////import org.bytedeco.opencv.opencv_core.Mat;
////import org.bytedeco.javacv.JavaFXFrameConverter;
////import org.bytedeco.javacv.Frame;
////import org.bytedeco.javacv.OpenCVFrameConverter;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class CameraController {
//    @FXML
//    private ImageView cameraView;
//    @FXML
//    private Button captureButton;
//    @FXML
//    private VBox cameraContainer;
//    @FXML
//    private Label resultLabel;
//
//    private CameraCapture cameraCapture;
//    private ScheduledExecutorService timer;
//    private final JavaFXFrameConverter converter = new JavaFXFrameConverter();
//    private final OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
//
//    public void initialize() {
//        cameraCapture = new CameraCapture();
//        if (cameraCapture.startCamera()) {
//            startCameraStream();
//        }
//    }
//
//    private void startCameraStream() {
//        timer = Executors.newSingleThreadScheduledExecutor();
//        timer.scheduleAtFixedRate(() -> {
//            try {
//                Mat frame = cameraCapture.captureImage();
//                if (frame != null) {
//                    Frame convertedFrame = converterToMat.convert(frame);
//                    Image imageToShow = converter.convert(convertedFrame);
//                    javafx.application.Platform.runLater(() -> cameraView.setImage(imageToShow));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, 0, 33, TimeUnit.MILLISECONDS);
//    }
//
//    @FXML
//    private void handleCapture() {
//        try {
//            Mat capturedImage = cameraCapture.captureImage();
//            if (capturedImage != null) {
//                FaceRecognitionResponse response = cameraCapture.sendImageToFlask(capturedImage);
//                if (response != null && !response.getResults().isEmpty()) {
//                    StringBuilder resultText = new StringBuilder("Résultats de la reconnaissance :\n");
//                    for (FaceRecognitionResponse.FaceResult result : response.getResults()) {
//                        resultText.append(result.getName()).append("\n");
//                    }
//                    resultLabel.setText(resultText.toString());
//                } else {
//                    resultLabel.setText("Aucun visage détecté");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultLabel.setText("Erreur lors de la reconnaissance faciale");
//        }
//    }
//
//    public void stopCamera() {
//        if (timer != null && !timer.isShutdown()) {
//            timer.shutdown();
//        }
//        if (cameraCapture != null) {
//            cameraCapture.releaseCamera();
//        }
//    }
//}