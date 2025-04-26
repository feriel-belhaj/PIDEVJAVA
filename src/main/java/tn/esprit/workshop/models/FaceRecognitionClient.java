package tn.esprit.workshop.models;

import tn.esprit.workshop.services.ServiceUser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

public class FaceRecognitionClient {

    public static User sendImageToFlask(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        URL url = new URL("http://127.0.0.1:8889/compare_face");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---boundary");

        try (OutputStream os = connection.getOutputStream()) {
            String boundary = "---boundary";
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            // Start form data part
            os.write((twoHyphens + boundary + lineEnd).getBytes());

            // Add the image part
            os.write(("Content-Disposition: form-data; name=\"image\"; filename=\"" + imageFile.getName() + "\"" + lineEnd).getBytes());
            os.write(("Content-Type: image/jpeg" + lineEnd).getBytes());
            os.write(lineEnd.getBytes());

            FileInputStream fis = new FileInputStream(imageFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            fis.close();

            os.write(lineEnd.getBytes());
            os.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
            os.flush();

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            System.out.println("Response: " + response.toString());
            if (response.toString().contains("No match found")) {
                System.out.println("Aucun visage correspondant trouvé.");
                return null;
            } else if (response.toString().contains("Face matched with image")) {

                String matchedImagePath = response.toString().split(": ")[2].trim().replace("\"}", "");
                System.out.println("Matched image path: " + matchedImagePath);

                String imageName = new File(matchedImagePath).getName();
                System.out.println("Image Name: " + imageName);

                ServiceUser serviceUser = new ServiceUser();
                User user = null;
                try {
                    user = serviceUser.getUserByImage(imageName);
                    if (user != null) {
                        System.out.println("Connexion réussie pour l'utilisateur : " + user.getNom());
                        return user;
                    } else {
                        System.out.println("Aucun utilisateur trouvé avec cette image.");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return null;
    }


}

