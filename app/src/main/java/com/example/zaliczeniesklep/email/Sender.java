package com.example.zaliczeniesklep.email;

import android.os.AsyncTask;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// https://github.com/Musfick/JavaMailAPIDemo/blob/master/app/src/main/java/com/teamcreative/javamailapidemo/JavaMailAPI.java

public class Sender extends AsyncTask<Void,Void,Void>{
    private final String email = "***";
    private final String password = "***";

    private Session session;

    private String to;
    private String subject;
    private String msg;

    public Sender(String to, String subject, String msg) {
        this.to = to;
        this.subject = subject;
        this.msg = msg;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, password);
                    }
                });

        try {
            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress(email));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mm.setSubject(subject);
            mm.setText(msg);

            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }
}