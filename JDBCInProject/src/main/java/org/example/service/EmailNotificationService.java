package org.example.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.model.Transaction;
import org.example.model.Location;
import org.example.observer.Observer;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailNotificationService implements Observer {

    Dotenv dotenv = Dotenv.load();
    private final String fromEmail = "satishkumarv752@gmail.com"; // sender email
    private final String password = dotenv.get("EMAIL_PASSWORD");
    private final String toEmail;

    public EmailNotificationService(String toEmail) {
        this.toEmail = toEmail;
    }

    @Override
    public void update(String state, Transaction t) {
        if (!"FLAGGED".equals(state)) return;

        String host = "smtp.gmail.com";
        int port = 587;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("⚠ Suspicious Transaction Alert");

            // Build a string with all locations
            StringBuilder locations = new StringBuilder();
            if (t.getLocations() != null) {
                for (Location loc : t.getLocations()) {
                    if (locations.length() > 0) locations.append(", ");
                    locations.append(loc.getLocationName());
                }
            }

            String emailContent =
                    "Dear Customer,\n\n" +
                            "A suspicious transaction has been detected:\n\n" +
                            "Transaction ID: " + t.getTransactionId() + "\n" +
                            "Card Number: " + t.getCard().getCardNumber() + "\n" +
                            "Amount: $" + t.getAmount() + "\n" +
                            "Location(s): " + locations + "\n\n" +
                            "If this was not you, please contact support immediately.\n\n" +
                            "Regards,\nFraud Detection System";

            message.setText(emailContent);
            Transport.send(message);

            System.out.println("[EMAIL] Notification sent to " + toEmail);

        } catch (MessagingException e) {
            System.out.println("[EMAIL ERROR] Failed to send email");
            e.printStackTrace();
        }
    }
}