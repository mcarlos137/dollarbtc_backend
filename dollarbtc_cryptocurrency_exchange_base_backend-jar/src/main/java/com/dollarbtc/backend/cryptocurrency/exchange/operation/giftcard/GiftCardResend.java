/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardResendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.GiftCardFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class GiftCardResend extends AbstractOperation<String> {

    private final GiftCardResendRequest giftCardResendRequest;

    public GiftCardResend(GiftCardResendRequest giftCardResendRequest) {
        super(String.class);
        this.giftCardResendRequest = giftCardResendRequest;
    }

    @Override
    protected void execute() {
        File giftCardFile = new File(GiftCardFolderLocator.getRedeemedFolder() + giftCardResendRequest.getId() + ".json");
        if (giftCardFile.isFile()) {
            super.response = "GIFT CARD WAS ALREADY REDEEMED";
            return;
        }
        giftCardFile = new File(GiftCardFolderLocator.getSubmittedFolder() + giftCardResendRequest.getId() + ".json");
        if (!giftCardFile.isFile()) {
            super.response = "GIFT CARD WAS NOT SUBMITTED YET";
            return;
        }
        try {
            JsonNode giftCard = mapper.readTree(giftCardFile);
            String userName = giftCard.get("baseUserName").textValue();
            String email = giftCard.get("email").textValue();
            Double amount = giftCard.get("amount").doubleValue();
            String currency = giftCard.get("currency").textValue();
            Set<String> recipients = new HashSet<>();
            recipients.add(email);
            String language = "EN";
            if(giftCard.has("language")){
                language = giftCard.get("language").textValue();
            }
            String qrUrl = "https://giftcardqrattachments.moneyclick.com/" + giftCardResendRequest.getId() + ".png";
            String subject = "MoneyClick Gift Card Resend by " + amount + " " + currency;
            if(language.equals("ES")){
                subject = "Reenvío de Tarjeta de Regalo MoneyClick por " + amount + " " + currency;
            }
            try {
                new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").sendHTML("support@moneyclick.com__MONEYCLICK", subject, getMessage(language, qrUrl, giftCardResendRequest.getId(), userName, currency, amount), recipients, null);
            } catch (MailException ex) {
                Logger.getLogger(GiftCardSend.class.getName()).log(Level.SEVERE, null, ex);
                super.response = "FAIL";
                return;
            }
            super.response = "OK";
        } catch (IOException ex) {
            Logger.getLogger(GiftCardResend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getMessage(String language, String qrUrl, String id, String userName, String currency, Double amount) {
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
                message2 = "Si no posees Smartphone (teléfono inteligente) puedes utilizar nuestra web:";
                message3 = "Luego de registrarte, dirígete a por el menu principal a la opcion de GIFT CARDS y pulsa APLICAR.";
                message4 = "Desde tu Smartphone escanea el siguiente código QR:";
                message5 = "Desde la web, introduce el siguiente código manualmente:";
                message6 = "Bienvenido a nuestra plataforma.";
                break;
            case "EN":
                message1 = "Congratulations, you have received a gift card of <b>" + amount + " " + currency + "</b> from " + userName + ". To claim it, download the MoneyClick APP at the following links:";
                message2 = "If you don't have a Smartphone you can use our website:";
                message3 = "After registering, go to the main menu to the GIFT CARDS option and press APPLY.";
                message4 = "From your Smartphone scan the following QR code:";
                message5 = "From the website, enter the following code manually:";
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
        messageBuilder.append("<a href=\"https://moneyclick.com/main\">https://moneyclick.com/main</a>");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message3);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message4);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append("<img originalsrc=\"cid:8d16cb6c-9745-492e-941a-777067404701\" size=\"4648\" contenttype=\"image/png\" style=\"user-select: none; width: 155px;\" crossorigin=\"use-credentials\" src=\"" + qrUrl + "\" unselectable=\"on\" tabindex=\"-1\" data-imgsize=\"small\">");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message5);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append("<b>" + id + "</b>");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message6);
        messageBuilder.append("</div>");
        messageBuilder.append("</div>");
        return messageBuilder.toString();
    }

}
