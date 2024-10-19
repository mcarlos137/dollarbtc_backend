/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ChatsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class SendChatSmsToAdminGroupMain {

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
                    if (!chatsAdminUserRoomControl.has("lastSMSTimestamp")) {
                        sendMessage = true;
                    } else {
                        Date lastSMSTimestampDate = DateUtil.parseDate(chatsAdminUserRoomControl.get("lastSMSTimestamp").textValue());
                        Date currentDateLess20 = DateUtil.parseDate(DateUtil.getDateMinutesBefore(null, 20));
                        if (lastSMSTimestampDate.before(currentDateLess20)) {
                            sendMessage = true;
                        }
                    }
                }
                if (sendMessage) {
                    sendSMS(userName, room, language);
                    sendCount++;
                    ((ObjectNode) chatsAdminUserRoomControl).put("lastSMSTimestamp", DateUtil.getCurrentDate());
                    FileUtil.editFile(chatsAdminUserRoomControl, chatsAdminUserRoomControlFile);
                    if(sendCount == 5){
                         break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(SendChatSmsToAdminGroupMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void sendSMS(String userName, String chatRoom, String language) {
        String[] phoneNumbers;
        String message;
        switch (language) {
            case "ES":
                phoneNumbers = new String[]{"+584245522788", "+584242947017"};
                message = "MC mensaje de usuario " + userName + " de la sala de chat " + chatRoom + " " + language;
                break;
            case "EN":
                phoneNumbers = new String[]{"+12019896074", "+17862380560"};
                message = "MC contact from user " + userName + " at chat room " + chatRoom + " " + language;
                break;
            default:
                return;
        }
        new SMSSender().publish(message, phoneNumbers);
    }

}
