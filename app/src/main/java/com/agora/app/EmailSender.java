package com.agora.app;

import android.os.AsyncTask;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/* Class EmailSender: A class dedicated to sending emails using the JavaMail API
 * Utilizes a gmail account setup with a password, and the gmail smtp server.
 * Currently in debugging process
 */
public class EmailSender extends AsyncTask<String, Void, Boolean> {

    private String RECIPIENT_EMAIL;
    private final String SENDER_EMAIL = "agora.auth.mail@gmail.com";
    private final String EMAIL_PASS = "Gns904wow123@@?";
    private final String smtpHost = "smtp.gmail.com";
    private final String smtpPort = "587";

    protected Boolean doInBackground(String... params) {
        if (params.length == 0)
            return false;
        
        RECIPIENT_EMAIL = params[0];
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties);

        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(RECIPIENT_EMAIL));
            message.setSubject("Welcome to Agora!");
            message.setText("This message is to test the JavaMail API.");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
}
