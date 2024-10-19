/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ChatsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class SendChatMailToAdminGroupMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        int sendCount = 0;
        File chatsAdminFolder = new File(ChatsFolderLocator.getFolder(), "Admin");
        for (File chatsAdminUserRoomFolder : chatsAdminFolder.listFiles()) {
            if (!chatsAdminUserRoomFolder.isDirectory()) {
                continue;
            }
            String room = chatsAdminUserRoomFolder.getName().split("__")[0];
            String language = chatsAdminUserRoomFolder.getName().split("__")[1];
            String userName = chatsAdminUserRoomFolder.getName().split("__")[2];
            if (userName.contains("@mailinator.com")) {
                continue;
            }
            File chatsAdminUserRoomControlFile = new File(chatsAdminUserRoomFolder, "control.json");
            try {
                JsonNode chatsAdminUserRoomControl = mapper.readTree(chatsAdminUserRoomControlFile);
                boolean sendMessage = false;
                if (chatsAdminUserRoomControl.has("notReadedMessages") && chatsAdminUserRoomControl.get("notReadedMessages").booleanValue()) {
                    if (!chatsAdminUserRoomControl.has("lastMailTimestamp")) {
                        sendMessage = true;
                    } else {
                        Date lastMailTimestampDate = DateUtil.parseDate(chatsAdminUserRoomControl.get("lastMailTimestamp").textValue());
                        Date currentDateLess20 = DateUtil.parseDate(DateUtil.getDateMinutesBefore(null, 20));
                        if (lastMailTimestampDate.before(currentDateLess20)) {
                            sendMessage = true;
                        }
                    }
                }
                if (sendMessage) {
                    sendMail(userName, room, language);
                    sendCount++;
                    ((ObjectNode) chatsAdminUserRoomControl).put("lastMailTimestamp", DateUtil.getCurrentDate());
                    FileUtil.editFile(chatsAdminUserRoomControl, chatsAdminUserRoomControlFile);
                    if(sendCount == 5){
                         break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(SendChatMailToAdminGroupMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void sendMail(String userName, String chatRoom, String language) {
        Set<String> recipients = new HashSet<>();
        String message;
        String subject;
        switch (language) {
            case "ES":
                recipients.add("molinabracho@gmail.com");
                recipients.add("genessis_7_11@hotmail.com");
                recipients.add("sinep77@gmail.com");
                message = "DollarBTC mensaje de usuario " + userName + " de la sala de chat " + chatRoom + " " + language;
                subject = "Nuevo mensaje en sala de chat DollarBTC";
                break;
            case "EN":
                recipients.add("molinabracho@gmail.com");
                recipients.add("genessis_7_11@hotmail.com");
                recipients.add("sinep77@gmail.com");
                message = "DollarBTC contact from user " + userName + " at chat room " + chatRoom + " " + language;
                subject = "New message in DollarBTC chat room";
                break;
            default:
                return;
        }
        try {
            new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").send("admin@dollarbtc.com__DOLLARBTC", subject, message, recipients, null);
        } catch (MailException ex) {
            Logger.getLogger(SendChatMailToAdminGroupMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
