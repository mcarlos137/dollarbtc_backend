/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.naming.NamingException;

/**
 *
 * @author CarlosDaniel
 */
public class MailSMTP extends MailSession {

    private static final String PROTOCOL = "SMTP";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern;
    private Matcher matcher;

    public MailSMTP() {
    }

    public MailSMTP(String provider, String user, String password) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        super.provider = provider;
        super.user = user;
        super.password = password;
    }

    public Set<String> send(String from, String subject, String message, Set<String> recipients, File attachmentFile) throws MailException {
        if (subject == null) {
            throw new MailException("subject is null");
        }
        if (subject.equals("")) {
            throw new MailException("subject is empty");
        }
        if (recipients == null) {
            throw new MailException("recipients is null");
        }
        if (recipients.isEmpty()) {
            throw new MailException("recipients is empty");
        }
        try {
            Set<String> validatedRecipients = new HashSet<>();
            Session mailSession = getSession(PROTOCOL);
            MimeMessage msg = new MimeMessage(mailSession);
            String encodingOptions = "text/html; charset=UTF-8";
            msg.setHeader("viewPort", "width=device-width, initial-scale=1.0,  maximum-scale=1");
            msg.addHeader("Disposition-Notification-To", from);
            msg.setHeader("Content-Type", encodingOptions);
            if (from != null) {
                if (!from.equals("")) {
                    Logger.getLogger(MailSMTP.class.getName()).log(Level.INFO, "from: " + from);
                    if (from.split("__")[1].equals("")) {
                        msg.setFrom(new InternetAddress(from.split("__")[0].trim()));
                    } else if (from.split("__")[0].equals("")) {
                        msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from"), from.split("__")[1].trim(), "utf-8"));
                    } else {
                        msg.setFrom(new InternetAddress(from.split("__")[0].trim(), from.split("__")[1].trim(), "utf-8"));
                    }
                } else {
                    msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
                }
            } else {
                msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
            }
            List<InternetAddress> addresses = new ArrayList<>();
            for (String recipient : recipients) {
                if (this.validate(recipient)) {
                    addresses.add(new InternetAddress(recipient));
                    validatedRecipients.add(recipient);
                }
            }
            msg.setRecipients(Message.RecipientType.BCC, addresses.toArray(new InternetAddress[addresses.size()]));
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());
            BodyPart text = new MimeBodyPart();
            text.setText(message);
            MimeMultipart multiPart = new MimeMultipart();
            multiPart.addBodyPart(text);
            if (attachmentFile != null) {
                MimeBodyPart attachment = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(attachmentFile);
                attachment.setDataHandler(new DataHandler(fileDataSource));
                attachment.setDisposition(MimeBodyPart.ATTACHMENT);
                attachment.setFileName(MimeUtility.encodeText(attachmentFile.getName(), "UTF-8", null));
                if (attachment.getFileName() != null && !attachment.getFileName().isEmpty()) {
                    multiPart.addBodyPart(attachment);
                }
            }
            msg.setContent(multiPart);
            Transport.send(msg);
            Logger.getLogger(MailSMTP.class.getName()).log(Level.INFO, "SENDING MAIL " + msg);
            return validatedRecipients;
        } catch (MessagingException | UnsupportedEncodingException | NamingException ex) {
            Logger.getLogger(MailSMTP.class.getName()).log(Level.SEVERE, null, ex);
            throw new MailException(ex.getMessage(), ex);
        }
    }

    public Set<String> sendHTML(String from, String subject, String htmlMessage, Set<String> recipients, File attachmentFile) throws MailException {
        if (subject == null) {
            throw new MailException("subject is null");
        }
        if (subject.equals("")) {
            throw new MailException("subject is empty");
        }
        if (recipients == null) {
            throw new MailException("recipients is null");
        }
        if (recipients.isEmpty()) {
            throw new MailException("recipients is empty");
        }
        try {
            Set<String> validatedRecipients = new HashSet<>();
            Session mailSession = getSession(PROTOCOL);
            MimeMessage msg = new MimeMessage(mailSession);
            String encodingOptions = "text/html; charset=UTF-8";
            msg.setHeader("viewPort", "width=device-width, initial-scale=1.0,  maximum-scale=1");
            msg.addHeader("Disposition-Notification-To", from);
            msg.setHeader("Content-Type", encodingOptions);
            if (from != null) {
                if (!from.equals("")) {
                    Logger.getLogger(MailSMTP.class.getName()).log(Level.INFO, "from: " + from);
                    if (from.split("__")[1].equals("")) {
                        msg.setFrom(new InternetAddress(from.split("__")[0].trim()));
                    } else if (from.split("__")[0].equals("")) {
                        msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from"), from.split("__")[1].trim(), "utf-8"));
                    } else {
                        msg.setFrom(new InternetAddress(from.split("__")[0].trim(), from.split("__")[1].trim(), "utf-8"));
                    }
                } else {
                    msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
                }
            } else {
                msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
            }
            List<InternetAddress> addresses = new ArrayList<>();
            for (String recipient : recipients) {
                if (this.validate(recipient)) {
                    addresses.add(new InternetAddress(recipient));
                    validatedRecipients.add(recipient);
                }
            }
            msg.setRecipients(Message.RecipientType.BCC, addresses.toArray(new InternetAddress[addresses.size()]));
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());
            msg.setContent(
                    htmlMessage,
                    "text/html");
            Transport.send(msg);
            Logger.getLogger(MailSMTP.class.getName()).log(Level.INFO, "SENDING MAIL " + msg);
            return validatedRecipients;
        } catch (MessagingException | UnsupportedEncodingException | NamingException ex) {
            Logger.getLogger(MailSMTP.class.getName()).log(Level.SEVERE, null, ex);
            throw new MailException(ex.getMessage(), ex);
        }
    }

    private boolean validate(String hex) {
        if (hex == null || hex.equals("")) {
            return false;
        }
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

}
