/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MailFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class SendGiftCardMailMain {

    public static void main(String[] args) {
        new SendGiftCardMailMain().send();
    }

    private void send() {
        ObjectMapper mapper = new ObjectMapper();
        File mailFolder = MailFolderLocator.getFolder();
        File mailSendedFolder = MailFolderLocator.getSendedFolder();
        File mailFailedFolder = MailFolderLocator.getFailedFolder();
        for (File mailfile : mailFolder.listFiles()) {
            if (!mailfile.isFile()) {
                continue;
            }
            try {
                JsonNode mail = mapper.readTree(mailfile);
                String userName = mail.get("userName").textValue();
                String currency = mail.get("currency").textValue();
                String source = mail.get("source").textValue();
                String language = mail.get("language").textValue();
                String email = mail.get("email").textValue();
                String qrId = mail.get("qrId").textValue();
                String qrUrl = mail.get("qrUrl").textValue();
                double amount = mail.get("amount").doubleValue();
                Set<String> recipients = new HashSet<>();
                recipients.add(email);
                String subject = "MoneyClick Gift Card Send by " + amount + " " + currency;
                if (language.equals("ES")) {
                    subject = "Envío de Tarjeta de Regalo MoneyClick por " + amount + " " + currency;
                }
                new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").sendHTML("support@moneyclick.com__MONEYCLICK", subject, getMessage(userName, currency, amount, language, qrUrl, qrId), recipients, null);
                FileUtil.moveFileToFolder(mailfile, mailSendedFolder);
            } catch (IOException | MailException ex) {
                Logger.getLogger(SendGiftCardMailMain.class.getName()).log(Level.SEVERE, null, ex);
                FileUtil.moveFileToFolder(mailfile, mailFailedFolder);
            }
        }

    }

    private String getMessage(String userName, String currency, double amount, String language, String qrUrl, String id) {
        StringBuilder messageBuilder = new StringBuilder();
        String message1;
        String message2;
        String message3;
        String message4;
        String message5;
        String message6;
        switch (language) {
            case "ES":
                message1 = "Felicidades, has recibido una tarjeta de regalo de <b>" + amount + " " + currency + "</b> de " + userName + ". Para reclamarlos, descarga el APP MoneyClick en los siguientes enlaces:";
                message2 = "Si no posees Smartphone (teléfono inteligente) visita nuestra web:";
                message3 = "Luego de registrarte, dirígete a por el menu principal a la opcion de Recibir/Depositar y pulsa Por Tarjeta de Recarga.";
                message4 = "Entonces escanea el siguiente código QR para que tu dinero te sea acreditado:";
                message5 = "Para más información te proveemos nuestros números de contacto de atención al cliente:";
                message6 = "Bienvenido a nuestra plataforma.";
                break;
            case "EN":
                message1 = "Congratulations, you have received a gift card of <b>" + amount + " " + currency + "</b> from " + userName + ". To claim it, download the MoneyClick APP at the following links:";
                message2 = "If you don't have a Smartphone you can visit our website:";
                message3 = "After registering, go to the main menu to the Receive/Deposit option and press Gift Card.";
                message4 = "Then you have to scan the following QR code:";
                message5 = "For more details we provide you our customer service info:";
                message6 = "Welcome to our platform.";
                break;
            default:
                return "LANGUAGE NOT SUPPORTED";
        }
        messageBuilder.append("<div>");
        messageBuilder.append("<div dir=\"ltr\" role=\"textbox\" aria-multiline=\"true\" aria-label=\"Cuerpo del mensaje\" spellcheck=\"false\" contenteditable=\"true\">");
        messageBuilder.append("<div style=\"font-family: Calibri, Arial, Helvetica, sans-serif; font-size: 12pt; color: rgb(0, 0, 0);\">");
        messageBuilder.append("<img originalsrc=\"cid:0c501623-f1b0-4dc1-a871-c2026250fe71\" size=\"18410\" contenttype=\"image/jpeg\" style=\"user-select: none; width: 145.25px;\" crossorigin=\"use-credentials\" src=\"https://moneyclick.com/static/media/logo.b8dc8796.png\" unselectable=\"on\" tabindex=\"-1\" data-imgsize=\"small\">");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message1);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append("Android <a href=\"https://play.google.com/store/apps/details?id=com.dollarbtc.moneyclick&amp;hl=es\">https://play.google.com/store/apps/details?id=com.dollarbtc.moneyclick&amp;hl=es</a>");
        messageBuilder.append("<br>");
        messageBuilder.append("Apple <a href=\"https://apps.apple.com/us/app/moneyclick/id1501864260\">https://apps.apple.com/us/app/moneyclick/id1501864260</a>");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message2);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append("<a href=\"https://moneyclick.com\">https://moneyclick.com</a>");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message3);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message4);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append("<img originalsrc=\"cid:8d16cb6c-9745-492e-941a-777067404701\" size=\"4648\" contenttype=\"image/png\" style=\"user-select: none; width: 155px;\" crossorigin=\"use-credentials\" src=\"").append(qrUrl).append("\" unselectable=\"on\" tabindex=\"-1\" data-imgsize=\"small\">");
        messageBuilder.append("<br>");
        messageBuilder.append("<b>").append(id).append("</b>");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message5);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append("<a href=\"https://moneyclick.com/contact\">https://moneyclick.com/contact</a>");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message6);
        messageBuilder.append("</div>");
        messageBuilder.append("</div>");
        return messageBuilder.toString();
    }

}
