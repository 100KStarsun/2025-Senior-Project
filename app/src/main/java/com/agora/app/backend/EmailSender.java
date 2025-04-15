package com.agora.app.backend;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.MessagingException;
import android.util.Log;

/**
 * Test implementation of an email sender, needs to be run asynchronously to the UI thread.
 */
public class EmailSender {

    private final String smtpHost = "smtp.gmail.com";
    private final String smtpPort = "587";
    private final String smtpUsername = "agora.auth.mail@gmail.com";
    private final String smtpPassword = "sjwdutoduutynzpi";

    /**
     * Establishes an SMTP session utilizing the jakarta mail API, and gmails smtp server.
     *
     * @return  SMTP session used to send automatic emails.
     */
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
    }

    /**
     * Method utilizing jakarta mail API to send authentication emails to the user.
     * Utilizes the agora gmail account to send emails, and details the body of the email sent.
     *
     * @param to  Case-ID of the user that the email will be sent to
     */
    public void sendEmail(String to, String otp) {
        String from = "agora.auth.mail@gmail.com";
        String subject = "Verify your Agora account!";
        String body = "The following is your OTP: " + otp + "\n\rDo not share this value with anybody.";

        Session session = createSession();
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to.trim()));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            Log.e("EmailSender", "Email Sending Failed" + e.getMessage(), e);
        }
    }
}
