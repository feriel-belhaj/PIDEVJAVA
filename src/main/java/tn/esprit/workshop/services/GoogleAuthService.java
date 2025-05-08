package tn.esprit.workshop.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class GoogleAuthService {
    private static final String APPLICATION_NAME = "Votre application";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "C:/Users/Fatma/IdeaProjects/ArtizinaJava/src/main/resources/Utilisateur/credentials.json";

    public static Credential credential;

    public static Gmail getGmailService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH)));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8880).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    public static boolean logout() {
        try {
            // Si nous avons une référence active au credential
            if (credential != null && credential.getAccessToken() != null) {
                // Révoquer le token auprès de Google
                HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
                HttpResponse response = transport.createRequestFactory()
                        .buildGetRequest(new GenericUrl(
                                "https://accounts.google.com/o/oauth2/revoke?token=" +
                                        credential.getAccessToken()))
                        .execute();

                if (response.getStatusCode() != 200) {
                    System.err.println("Erreur lors de la révocation du token: " + response.getStatusCode());
                }
            }

            // Supprimer les fichiers de token stockés localement
            java.io.File tokenFolder = new java.io.File(TOKENS_DIRECTORY_PATH);
            if (tokenFolder.exists() && tokenFolder.isDirectory()) {
                java.io.File[] files = tokenFolder.listFiles();
                if (files != null) {
                    for (java.io.File file : files) {
                        if (!file.delete()) {
                            System.err.println("Impossible de supprimer le fichier: " + file.getName());
                        }
                    }
                }

                // Réinitialiser la référence credential
                credential = null;
                System.out.println("Déconnexion Google réussie");
                return true;
            } else {
                System.out.println("Aucun token d'authentification trouvé");
                return false;
            }
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }




}
