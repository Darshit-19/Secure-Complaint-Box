package com.securecomplaintbox.util;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailUtil {

    public static void sendOtp(String to, int otp) throws Exception {
        String user = ConfigUtil.getEmailUser();
        String pass = ConfigUtil.getEmailPassword();
        String host = ConfigUtil.getSmtpHost();
        String port = ConfigUtil.getSmtpPort();
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Your OTP for Secure Complaint Box");
        message.setText("Your OTP is: " + otp);
        Transport.send(message);
    }
}
