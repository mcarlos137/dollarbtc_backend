/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class NotificationGetMessages extends AbstractOperation<ArrayNode> {

    private String userName;

    public NotificationGetMessages() {
        super(ArrayNode.class);
    }

    public NotificationGetMessages(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        ArrayNode messages = mapper.createArrayNode();
        Map<String, JsonNode> messagesMap = new TreeMap<>(Collections.reverseOrder());
        if (userName == null) {
            for (File notificationMessageFile : NotificationsFolderLocator.getMessagesFolder().listFiles()) {
                if (!notificationMessageFile.isFile()) {
                    continue;
                }
                try {
                    messagesMap.put(notificationMessageFile.getName(), mapper.readTree(notificationMessageFile));
                } catch (IOException ex) {
                    Logger.getLogger(NotificationGetMessages.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (File userNotificationFile : UsersFolderLocator.getNotificationsFolder(userName).listFiles()) {
                if (!userNotificationFile.isFile()) {
                    continue;
                }
                try {
                    messagesMap.put(userNotificationFile.getName(), mapper.readTree(userNotificationFile));
                } catch (IOException ex) {
                    Logger.getLogger(NotificationGetMessages.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        messages.addAll(messagesMap.values());
        super.response = messages;
    }

}
