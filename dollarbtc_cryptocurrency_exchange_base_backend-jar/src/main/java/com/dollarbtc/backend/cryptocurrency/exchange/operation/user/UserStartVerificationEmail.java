/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserStartVerificationEmailRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class UserStartVerificationEmail extends AbstractOperation<String> {

    private final UserStartVerificationEmailRequest userStartVerificationEmailRequest;

    public UserStartVerificationEmail(UserStartVerificationEmailRequest userStartVerificationEmailRequest) {
        super(String.class);
        this.userStartVerificationEmailRequest = userStartVerificationEmailRequest;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userStartVerificationEmailRequest.getUserName());
        if (!userFile.isFile()) {
            super.response = "USERNAME DOES NOT EXIST";
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            if (!user.has("email")) {
                super.response = "USER DOES NOT HAVE FIELDNAME " + "email";
                return;
            }
            String email = user.get("email").textValue();
            if (!user.has("verification")) {
                ((ObjectNode) user).set("verification", mapper.createObjectNode());
            }
            JsonNode userVerification = user.get("verification");
            if (!userVerification.has("A")) {
                ((ObjectNode) userVerification).set("A", mapper.createObjectNode());
            }
            JsonNode userVerificationType = userVerification.get("A");
            if (!userVerificationType.has("userVerificationStatus")) {
                String verificationOperationId = BaseOperation.getId();
                ((ObjectNode) userVerificationType).put("userVerificationStatus", UserVerificationStatus.PROCESSING.name());
                ((ObjectNode) userVerificationType).put("timestamp", DateUtil.getCurrentDate());
                ((ObjectNode) userVerificationType).putArray("fieldNames");
                ((ArrayNode) userVerificationType.get("fieldNames")).add("email");
                ((ObjectNode) userVerificationType).put("info", userStartVerificationEmailRequest.getInfo());
                ((ObjectNode) userVerificationType).put("verificationOperationId", verificationOperationId);
                FileUtil.editFile(user, userFile);
                createEmailThread(userStartVerificationEmailRequest.getUserName(), email, verificationOperationId);
                super.response = "OK";
                return;
            } else {
                UserVerificationStatus userVerificationStatus = UserVerificationStatus.valueOf(userVerificationType.get("userVerificationStatus").textValue());
                switch (userVerificationStatus) {
                    case PROCESSING:
                        createEmailThread(userStartVerificationEmailRequest.getUserName(), email, userVerificationType.get("verificationOperationId").textValue());
                        super.response = "VERIFICATION ALREADY STARTED";
                        return;
                    case OK:
                        super.response = "VERIFICATION OK";
                        return;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserStartVerificationEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void createEmailThread(String userName, String email, String id) {
        Thread createEmailThread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(UserStartVerificationEmail.class.getName()).log(Level.SEVERE, null, ex);
            }
            Set<String> recipients = new HashSet<>();
            recipients.add(email);
            String language = "EN";
            String verificationUrl = "https://service8081.moneyclick.com/user/verifyEmail?userName=" + userName + "&id=" + id;
            String subject = "email verification";
            if(language.equals("ES")){
                subject = "verificación de email";
            }
            try {
                new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").sendHTML("support@moneyclick.com__MONEYCLICK", subject, getMessage(language, verificationUrl), recipients, null);
            } catch (MailException ex) {
                Logger.getLogger(UserStartVerificationEmail.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        createEmailThread.start();
    }
    
    private String getMessage(String language, String verificationUrl) {
        StringBuilder messageBuilder = new StringBuilder();
        String message1;
        String message2;
        switch (language) {
            case "ES":
                message1 = "<b>Bienvenido a MoneyClick.</b>";
                message2 = "Para verificar tu correo por favor cliquea el link de abajo:";
                break;
            case "EN":
                message1 = "<b>Welcome to MoneyClick.</b>";
                message2 = "To verify your email please click link below:";
                break;
            default:
                return "LANGUAGE NOT SUPPORTED";
        }
        //messageBuilder.append("<div>");
        messageBuilder.append("<div dir=\"ltr\" role=\"textbox\" aria-multiline=\"true\" aria-label=\"Cuerpo del mensaje\" spellcheck=\"false\" contenteditable=\"true\">");
        messageBuilder.append("<div style=\"font-family: Calibri, Arial, Helvetica, sans-serif; font-size: 12pt; color: rgb(0, 0, 0);\">");
        messageBuilder.append("<img originalsrc=\"cid:0c501623-f1b0-4dc1-a871-c2026250fe71\" size=\"18410\" contenttype=\"image/jpeg\" style=\"user-select: none; width: 145.25px;\" crossorigin=\"use-credentials\" src=\"https://moneyclick.com/static/media/logo.b8dc8796.png\" unselectable=\"on\" tabindex=\"-1\" data-imgsize=\"small\">");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message1);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append(message2);
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append("<a href=\"" + verificationUrl + "\">" + verificationUrl + "</a>");
        messageBuilder.append("<br>");
        messageBuilder.append("<br>");
        messageBuilder.append("</div>");
        messageBuilder.append("</div>");
        return messageBuilder.toString();
    }

}
