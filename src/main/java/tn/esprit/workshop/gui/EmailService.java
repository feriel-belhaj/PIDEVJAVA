package tn.esprit.workshop.gui;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
public class EmailService {
    private final String username;
    private final String password;
    private final Properties props;

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;

        // Configuration pour Gmail
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    public void sendConfirmationEmail(String toEmail, int commandeId) throws MessagingException {
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Confirmation de commande #" + commandeId);

            String htmlContent = "<h1>Merci pour votre commande !</h1>" +
                    "<p>Votre commande #" + commandeId + " a été confirmée.</p>" +
                    "<p>Statut : Paiement accepté</p>" +
                    "<p>Nous vous contacterons lorsque votre commande sera prête.</p>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("Email envoyé avec succès à " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            throw e;
        }
    }
}

