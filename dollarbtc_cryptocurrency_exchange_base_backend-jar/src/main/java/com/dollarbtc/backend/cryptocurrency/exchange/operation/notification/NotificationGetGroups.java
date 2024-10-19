/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class NotificationGetGroups extends AbstractOperation<ArrayNode> {

    public NotificationGetGroups() {
        super(ArrayNode.class);
    }

    @Override
    protected void execute() {
        ArrayNode groups = mapper.createArrayNode();
        for (File notificationGroupFile : NotificationsFolderLocator.getGroupsFolder().listFiles()) {
            if (!notificationGroupFile.isFile()) {
                continue;
            }
            try {
                JsonNode notificationGroup = mapper.readTree(notificationGroupFile);
                int userNamesCount = notificationGroup.get("userNames").size();
                if (notificationGroup.has("userNames")) {
                    ((ObjectNode) notificationGroup).put("userNamesCount", userNamesCount);
                    ((ObjectNode) notificationGroup).remove("userNames");
                }
                groups.add(notificationGroup);
            } catch (IOException ex) {
                Logger.getLogger(NotificationGetGroups.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = groups;
    }

}
