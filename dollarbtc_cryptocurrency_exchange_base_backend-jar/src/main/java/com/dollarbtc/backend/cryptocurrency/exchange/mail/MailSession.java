/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.naming.NamingException;

/**
 *
 * @author CarlosDaniel
 */
public class MailSession {

    protected String provider, user, password;

    public MailSession() {
    }

    public MailSession(String provider, String user, String password) {
        this.provider = provider;
        this.user = user;
        this.password = password;
    }

    public Session getSession(final String protocol) throws NamingException, MailException {
        Session session = null;
        if (provider != null) {
            if (!provider.equals("")) {
                session = Session.getInstance(getBasicProperties(provider, protocol), new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
            }
        }
        return session;
    }

    private static Properties getBasicProperties(String provider, String protocol) throws MailException {
        Properties props = new Properties();
        switch (provider + "__" + protocol) {
            case "GMAIL__SMTP":
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                break;
            case "AWS__SMTP":
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
                props.put("mail.smtp.port", "587");
//                props.put("mail.smtp.socketFactory.fallback", "false");
//                props.put("mail.smtp.socketFactory.port", "465");
//                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                break;
            default:
                throw new MailException("provider - protocol not implemented yet");
        }
        return props;
    }

}
