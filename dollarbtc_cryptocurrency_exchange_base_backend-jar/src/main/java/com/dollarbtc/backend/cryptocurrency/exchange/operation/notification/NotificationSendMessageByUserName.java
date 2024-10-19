/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractFirebaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class NotificationSendMessageByUserName extends AbstractFirebaseOperation<String> {

    private final String userName, title, content;
    private Map<String, String> data = null;

    public NotificationSendMessageByUserName(String userName, String title, String content) {
        super(String.class, false);
        this.userName = userName;
        this.title = title;
        this.content = content;
    }

    public NotificationSendMessageByUserName(String userName, String title, String content, boolean kaikai) {
        super(String.class, kaikai);
        this.userName = userName;
        this.title = title;
        this.content = content;
    }

    public NotificationSendMessageByUserName(String userName, String title, String content, boolean kaikai, Map<String, String> data) {
        super(String.class, kaikai);
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.data = data;
    }

    @Override
    protected void execute() {
        String timestamp = DateUtil.getCurrentDate();
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (userConfig.has("notificationToken")) {
                List<String> tokens = new ArrayList<>();
                tokens.add(userConfig.get("notificationToken").textValue());
                /*if(userConfig.has("otherNotificationTokens")){
                    Iterator<String> otherNotificationTokenIterator = userConfig.get("otherNotificationTokens").fieldNames();
                    while (otherNotificationTokenIterator.hasNext()) {
                        String otherNotificationTokenIt = otherNotificationTokenIterator.next();
                        tokens.add(otherNotificationTokenIt);
                    }
                }*/
                MulticastMessage message;
                if (data == null) {
                    message = MulticastMessage.builder()
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(content)
                                    .build())
                            .addAllTokens(tokens)
                            .build();
                } else {
                    message = MulticastMessage.builder()
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(content)
                                    .build())
                            .addAllTokens(tokens)
                            .putAllData(data)
                            .build();
                }
                try {
                    BatchResponse resp = FirebaseMessaging.getInstance().sendMulticast(message);
                    for (SendResponse sendResponse : resp.getResponses()) {
                        if (sendResponse.getException() != null) {
                            Logger.getLogger(NotificationSendMessageByUserName.class.getName()).log(Level.INFO, "sendResponse: {0}", sendResponse.getException().getMessage());
                        }
                    }
                    Logger.getLogger(NotificationSendMessageByUserName.class.getName()).log(Level.INFO, "Successfully failure messages: {0}", resp.getFailureCount());
                    Logger.getLogger(NotificationSendMessageByUserName.class.getName()).log(Level.INFO, "Successfully sent messages: {0}", resp.getSuccessCount());
                } catch (FirebaseMessagingException ex) {
                    Logger.getLogger(NotificationSendMessageByUserName.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (userConfig.has("phone")) {
                new SMSSender().publish(content, new String[]{userConfig.get("phone").textValue()});
            } else if (userConfig.has("createdFromMCSend") && userConfig.get("createdFromMCSend").booleanValue()) {
                new SMSSender().publish(content, new String[]{userConfig.get("name").textValue()});
            }
            ObjectNode userNotification = mapper.createObjectNode();
            userNotification.put("timestamp", timestamp);
            userNotification.put("title", title);
            userNotification.put("content", content);
            userNotification.put("readed", false);
            FileUtil.createFile(userNotification, new File(UsersFolderLocator.getNotificationsFolder(userName), DateUtil.getFileDate(timestamp) + ".json"));
            super.response = "OK";
        } catch (IOException ex) {
            Logger.getLogger(NotificationSendMessageByUserName.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
