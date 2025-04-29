package tn.esprit.workshop.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class EmailService {
    
    // Configuration du serveur SMTP
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "anasallam02@gmail.com"; // Remplacez par votre email
    private static final String PASSWORD = "tylz hkfz igfk kajl"; // Remplacez par votre mot de passe d'application
    
    /**
     * Envoie un email de confirmation de paiement
     * @param destinataire L'adresse email du destinataire
     * @param nomFormation Le nom de la formation réservée
     * @param montant Le montant payé
     * @return true si l'email a été envoyé avec succès, false sinon
     */
    public static boolean envoyerEmailConfirmationPaiement(String destinataire, String nomFormation, double montant) {
        if (destinataire == null || destinataire.isEmpty()) {
            System.err.println("L'adresse email du destinataire est invalide");
            return false;
        }

        try {
            // Désactiver la vérification SSL
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };
            
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Configuration des propriétés
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.enable", "false");
            
            // Création de la session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });
            
            // Désactiver la vérification SSL pour la session
            session.setDebug(true);
            
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject("Confirmation de paiement - Formation " + nomFormation);
            
            // Contenu de l'email
            String contenu = "Bonjour,\n\n" +
                    "Nous vous confirmons que votre paiement pour la formation \"" + nomFormation + "\" a été effectué avec succès.\n\n" +
                    "Montant payé : " + montant + " DT\n\n" +
                    "Merci pour votre réservation !\n\n" +
                    "Cordialement,\n" +
                    "L'équipe Artizina";
            
            message.setText(contenu);
            
            // Envoi de l'email
            Transport.send(message);
            
            System.out.println("Email de confirmation envoyé à " + destinataire);
            return true;
            
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            return false;
        }
    }
} 