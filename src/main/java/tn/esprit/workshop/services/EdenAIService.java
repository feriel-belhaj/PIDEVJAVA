package tn.esprit.workshop.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class EdenAIService {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjc3ZWQ4OTgtNzYyZS00OWUxLWEzYjEtYzQwNzUwNWY1MzczIiwidHlwZSI6ImFwaV90b2tlbiJ9.GfQjIrdAIh0X6jOaC26iR4giOwz1Rn7quV6PKsSQB8s"; // Replace with your actual API key
    private static final String API_URL = "https://api.edenai.run/v2/image/generation";
    
    public String generateImage(String prompt, String outputPath) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        
        // Prepare the request body with Replicate provider
        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = String.format("""
            {
                "providers": "replicate",
                "text": "%s",
                "resolution": "512x512",
                "num_images": 1,
                "model": "stability-ai/sdxl"
            }
            """, prompt);
            
        Request request = new Request.Builder()
            .url(API_URL)
            .post(RequestBody.create(requestBody, mediaType))
            .addHeader("Authorization", "Bearer " + API_KEY)
            .build();
            
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                throw new IOException("Unexpected response code: " + response.code() + " - " + errorBody);
            }
            
            ObjectMapper mapper = new ObjectMapper();
            String responseBody = response.body().string();
            JsonNode rootNode = mapper.readTree(responseBody);
            
            // Debug: Print the full response
            System.out.println("API Response: " + responseBody);
            
            // Get the image data from Replicate response
            JsonNode replicateNode = rootNode.path("replicate");
            if (replicateNode.isMissingNode()) {
                throw new IOException("Replicate provider response not found in API response");
            }
            
            JsonNode itemsNode = replicateNode.path("items");
            if (itemsNode.isMissingNode() || !itemsNode.isArray() || itemsNode.size() == 0) {
                throw new IOException("No items found in Replicate response");
            }
            
            // Get the base64 image data
            String base64Image = itemsNode.get(0).path("image").asText();
            if (base64Image == null || base64Image.isEmpty()) {
                throw new IOException("Image data is empty in Replicate response");
            }
            
            // Remove the data URL prefix if present
            if (base64Image.startsWith("data:image/png;base64,")) {
                base64Image = base64Image.substring("data:image/png;base64,".length());
            }
            
            // Decode and save the image
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Files.write(Paths.get(outputPath), imageBytes);
            
            return outputPath;
        }
    }
}