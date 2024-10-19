/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification.NotificationAddGroupsToTopicRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class NotificationAddGroupsToTopic extends AbstractOperation<String> {

    private final NotificationAddGroupsToTopicRequest notificationAddGroupsToTopicRequest;

    public NotificationAddGroupsToTopic(NotificationAddGroupsToTopicRequest notificationAddGroupsToTopicRequest) {
        super(String.class);
        this.notificationAddGroupsToTopicRequest = notificationAddGroupsToTopicRequest;
    }

    @Override
    protected void execute() {
        File notificationsTopicFile = NotificationsFolderLocator.getTopicFile(notificationAddGroupsToTopicRequest.getTopicId());
        if (!notificationsTopicFile.isFile()) {
            super.response = "TOPIC DOES NOT EXIST";
            return;
        }
        try {
            JsonNode notificationsTopic = mapper.readTree(notificationsTopicFile);
            if (!notificationsTopic.has("groups")) {
                ((ObjectNode) notificationsTopic).set("groups", mapper.createObjectNode());
            }
            String timestamp = DateUtil.getCurrentDate();
            for (String group : notificationAddGroupsToTopicRequest.getGroups()) {
                File notificationsGroupFile = NotificationsFolderLocator.getGroupFile(group);
                if (!notificationsGroupFile.isFile()) {
                    continue;
                }
                if(notificationsTopic.get("groups").has(group)){
                    continue;
                }
                ((ObjectNode) notificationsTopic.get("groups")).put(group, timestamp);
            }
            FileUtil.editFile(notificationsTopic, notificationsTopicFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(NotificationAddGroupsToTopic.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
