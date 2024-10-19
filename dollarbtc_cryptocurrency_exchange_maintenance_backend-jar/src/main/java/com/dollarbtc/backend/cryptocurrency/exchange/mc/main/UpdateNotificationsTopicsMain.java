/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UpdateNotificationsTopicsMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(new FileInputStream(NotificationsFolderLocator.getFirebaseFile())))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException ex) {
            Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        String timestamp = DateUtil.getCurrentDate();
        ObjectMapper mapper = new ObjectMapper();
        for (File notificationTopicFile : NotificationsFolderLocator.getTopicsFolder().listFiles()) {
            if (!notificationTopicFile.isFile()) {
                continue;
            }
            String topicId = notificationTopicFile.getName().replace(".json", "");
            Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "STARTING TOPIC {0}", topicId);
            try {
                JsonNode notificationTopic = mapper.readTree(notificationTopicFile);
                if (!notificationTopic.has("groups")) {
                    continue;
                }
                String serviceName = notificationTopic.get("serviceName").textValue();
                Iterator<String> notificationTopicGroupsIterator = notificationTopic.get("groups").fieldNames();
                while (notificationTopicGroupsIterator.hasNext()) {
                    String notificationTopicGroupsIt = notificationTopicGroupsIterator.next();
                    File notificationGroupFile = NotificationsFolderLocator.getGroupFile(notificationTopicGroupsIt);
                    if (!notificationGroupFile.isFile()) {
                        continue;
                    }
                    Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "STARTING GROUP {0}", notificationGroupFile.getName());
                    Set<String> newUserNames = new HashSet<>();
                    try {
                        JsonNode notificationGroup = mapper.readTree(notificationGroupFile);
                        Iterator<String> notificationGroupUserNamesIterator = notificationGroup.get("userNames").fieldNames();
                        while (notificationGroupUserNamesIterator.hasNext()) {
                            String notificationGroupUserNamesIt = notificationGroupUserNamesIterator.next();
                            Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "USER {0}", notificationGroupUserNamesIt);
                            if (notificationGroup.get("userNames").get(notificationGroupUserNamesIt).has(topicId)) {
                                continue;
                            }
                            Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "USER ADDED {0}", notificationGroupUserNamesIt);
                            newUserNames.add(notificationGroupUserNamesIt);
                            if (newUserNames.size() >= 30000) {
                                break;
                            }
                        }
                        Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "NEW USERS SIZE {0}", newUserNames.size());
                        List<String> tokens = new ArrayList<>();
                        for (String newUserName : newUserNames) {
                            try {
                                JsonNode userConfig = mapper.readTree(UsersFolderLocator.getConfigFile(newUserName));
                                if (!userConfig.has("notificationToken")) {
                                    continue;
                                }
                                String token = userConfig.get("notificationToken").textValue();
                                ((ObjectNode) notificationGroup.get("userNames").get(newUserName)).put("token", token);
                                ((ObjectNode) notificationGroup.get("userNames").get(newUserName)).put(topicId, timestamp);
                                Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "ADD TOKEN {0}", token);
                                tokens.add(token);
                                if (tokens.size() >= 100) {
                                    break;
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "TOKENS SIZE {0}", tokens.size());
                        int usersWithToken = 0;
                        notificationGroupUserNamesIterator = notificationGroup.get("userNames").fieldNames();
                        while (notificationGroupUserNamesIterator.hasNext()) {
                            String notificationGroupUserNamesIt = notificationGroupUserNamesIterator.next();
                            if (!notificationGroup.get("userNames").get(notificationGroupUserNamesIt).has("token")) {
                                continue;
                            }
                            usersWithToken++;
                        }
                        ((ObjectNode) notificationGroup).put("usersWithToken", usersWithToken);
                        FileUtil.editFile(notificationGroup, notificationGroupFile);
                        if (tokens.isEmpty()) {
                            continue;
                        }
                        TopicManagementResponse resp = FirebaseMessaging.getInstance().subscribeToTopic(tokens, serviceName);
                        Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "{0} tokens were subscribed successfully", resp.getSuccessCount());
                    } catch (IOException | FirebaseMessagingException ex) {
                        Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "FINISHING GROUP {0}", notificationGroupFile.getName());
                }
            } catch (IOException ex) {
                Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(UpdateNotificationsTopicsMain.class.getName()).log(Level.INFO, "FINISHING TOPIC {0}", topicId);
        }
    }

}
