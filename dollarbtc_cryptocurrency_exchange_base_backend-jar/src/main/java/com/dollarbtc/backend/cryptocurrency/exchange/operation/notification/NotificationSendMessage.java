/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification.NotificationSendMessageRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractFirebaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.MulticastMessage.Builder;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class NotificationSendMessage extends AbstractFirebaseOperation<String> {

    private final NotificationSendMessageRequest notificationSendMessageRequest;

    public NotificationSendMessage(NotificationSendMessageRequest notificationSendMessageRequest) {
        super(String.class, notificationSendMessageRequest.isKaikai());
        this.notificationSendMessageRequest = notificationSendMessageRequest;
    }

    @Override
    protected void execute() {
        String timestamp = DateUtil.getCurrentDate();
        if (notificationSendMessageRequest.getTopicId() != null) {
            super.response = sendToTopic(notificationSendMessageRequest.getTopicId(), notificationSendMessageRequest.getTitle(), notificationSendMessageRequest.getContent(), timestamp);
            return;
        }
        if (notificationSendMessageRequest.getUserNames() != null && notificationSendMessageRequest.getTitle() != null) {
            super.response = sendToUserNames(notificationSendMessageRequest.getUserNames(), notificationSendMessageRequest.getTitle(), notificationSendMessageRequest.getContent(), timestamp, notificationSendMessageRequest.getData(), notificationSendMessageRequest.getSound());
        }
    }

    private String sendToTopic(String topicId, String title, String content, String timestamp) {
        try {
            File notificationTopicFile = NotificationsFolderLocator.getTopicFile(topicId);
            if (!notificationTopicFile.isFile()) {
                return "FAIL";
            }
            JsonNode notificationTopic = mapper.readTree(notificationTopicFile);
            if (title == null) {
                title = notificationTopic.get("name").textValue();
            }
            String serviceName = notificationTopic.get("serviceName").textValue();
            Set<String> userNames = new HashSet<>();
            if (notificationTopic.has("groups")) {
                Iterator<String> notificationTopicGroupsIterator = notificationTopic.get("groups").fieldNames();
                while (notificationTopicGroupsIterator.hasNext()) {
                    String notificationTopicGroupsIt = notificationTopicGroupsIterator.next();
                    File notificationGroupFile = NotificationsFolderLocator.getGroupFile(notificationTopicGroupsIt);
                    if (!notificationGroupFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode notificationGroup = mapper.readTree(notificationGroupFile);
                        Iterator<String> notificationGroupUserNamesIterator = notificationGroup.get("userNames").fieldNames();
                        while (notificationGroupUserNamesIterator.hasNext()) {
                            String notificationGroupUserNamesIt = notificationGroupUserNamesIterator.next();
                            userNames.add(notificationGroupUserNamesIt);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(NotificationSendMessage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                userNames.add("584245522788");
            }
            createNotificacionForUserNames(userNames, timestamp, title, content);
            createNotificacion(timestamp, title, content, userNames.size());
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .setTopic(serviceName)
                    .build();
            // Send a message to the devices subscribed to the provided topic.
            String resp = FirebaseMessaging.getInstance().send(message);
            // Response is a message ID string.
            Logger.getLogger(NotificationSendMessage.class.getName()).log(Level.INFO, "Successfully sent message: {0}", resp);
            return "OK";
        } catch (Exception ex) {
            Logger.getLogger(NotificationSendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "FAIL";
    }

    private String sendToUserNames(Set<String> userNames, String title, String content, String timestamp, Map<String, String> data, String sound) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>6 userNames " + userNames);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>6 title " + title);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>6 content " + content);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>6 data " + data);
        List<String> tokens = new ArrayList<>();
        for (String userName : userNames) {
            File userConfigFile = UsersFolderLocator.getConfigFile(userName);
            if (!userConfigFile.isFile()) {
                continue;
            }
            try {
                JsonNode userConfig = mapper.readTree(userConfigFile);
                if (userConfig.has("notificationToken")) {
                    tokens.add(userConfig.get("notificationToken").textValue());
                    /*if (userConfig.has("otherNotificationTokens")) {
                        Iterator<String> otherNotificationTokenIterator = userConfig.get("otherNotificationTokens").fieldNames();
                        while (otherNotificationTokenIterator.hasNext()) {
                            String otherNotificationTokenIt = otherNotificationTokenIterator.next();
                            tokens.add(otherNotificationTokenIt);
                        }
                    }*/
                }
            } catch (IOException ex) {
                Logger.getLogger(NotificationSendMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        createNotificacionForUserNames(userNames, timestamp, title, content);
        createNotificacion(timestamp, title, content, userNames.size());
        if (tokens.isEmpty()) {
            return "THERE IS NO TOKENS FOR USERS";
        }
        Builder builder = MulticastMessage.builder();
        builder.setNotification(
                Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .addAllTokens(tokens);
        if (data != null) {
            builder.putAllData(data);
        }
        if (sound != null) {
            builder.setAndroidConfig(
                    AndroidConfig.builder()
                            .setTtl(30 * 1000)
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound(sound)
                                    .build())
                            .build());
            builder.setApnsConfig(
                    ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound(sound)
                                    .build())
                            .build());
        }
        MulticastMessage message = builder.build();
        try {
            BatchResponse resp = FirebaseMessaging.getInstance().sendMulticast(message);
            Logger.getLogger(NotificationSendMessage.class.getName()).log(Level.INFO, "Successfully sent message: {0}", resp.getSuccessCount());
            for(SendResponse sendResponse : resp.getResponses()){
                if(sendResponse.getException() != null){
                    Logger.getLogger(NotificationSendMessage.class.getName()).log(Level.INFO, sendResponse.getException().toString());
                }
            }
        } catch (FirebaseMessagingException ex) {
            Logger.getLogger(NotificationSendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "OK";
    }

    private void createNotificacionForUserNames(Set<String> userNames, String timestamp, String title, String content) {
        ObjectNode userNotification = mapper.createObjectNode();
        userNotification.put("timestamp", timestamp);
        userNotification.put("title", title);
        userNotification.put("content", content);
        userNotification.put("readed", false);
        Thread createNotificacionForUserNamesThread = new Thread(() -> {
            for (String userName : userNames) {
                FileUtil.createFile(userNotification, new File(UsersFolderLocator.getNotificationsFolder(userName), DateUtil.getFileDate(timestamp) + ".json"));
            }
        });
        createNotificacionForUserNamesThread.start();
    }

    private void createNotificacion(String timestamp, String title, String content, int userNamesCount) {
        ObjectNode notification = mapper.createObjectNode();
        notification.put("timestamp", timestamp);
        notification.put("title", title);
        notification.put("content", content);
        notification.put("userNamesCount", userNamesCount);
        FileUtil.createFile(notification, new File(NotificationsFolderLocator.getMessagesFolder(), DateUtil.getFileDate(timestamp) + ".json"));
    }

}
