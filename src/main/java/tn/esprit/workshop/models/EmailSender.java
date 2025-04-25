package tn.esprit.workshop.models;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";  // Utilise un service SMTP
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

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
        message.setSubject("Your One-Time Password (OTP)");
        message.setText("Your OTP is: " + otp);

        // Envoi du message
        Transport.send(message);
        System.out.println("OTP sent to " + recipientEmail);
    }
}
