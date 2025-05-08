package tn.esprit.workshop.models;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Message;

public class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";  // Utilise un service SMTP
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "chakrounfatma23@gmail.com";
    private static final String PASSWORD = "zeoydafupqelieqx";

    public static void sendOTP(String recipientEmail, String otp) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);


        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        // Créer le message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Tentative de connexion à Artizina");
        message.setText("Votre code est: " + otp);

        // Envoi du message
        Transport.send(message);
        System.out.println("OTP sent to " + recipientEmail);
    }

    public static void sendWelcomeEmail(String recipientEmail, String userName) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD); // Expéditeur
            }
        });

        // Créer le message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Bienvenue dans notre application Artizina !");
        message.setText("Bonjour " + userName + ",\n\nBienvenue sur notre plateforme !\nNous sommes ravis de vous compter parmi nous.\n\nCordialement,\nL’équipe Artizina.");

        // Envoyer le message
        Transport.send(message);
        System.out.println("Email de bienvenue envoyé à " + recipientEmail);
    }



}
