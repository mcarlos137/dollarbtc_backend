/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListNames;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UpdateNotificationsGroupsMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        for (File notificationGroupFile : NotificationsFolderLocator.getGroupsFolder().listFiles()) {
            if (!notificationGroupFile.isFile()) {
                continue;
            }
            try {
                JsonNode notificationGroup = mapper.readTree(notificationGroupFile);
                String notificationGroupRules = notificationGroup.get("rules").textValue();
                if (notificationGroupRules.equals("")) {
                    continue;
                }
                Logger.getLogger(UpdateNotificationsGroupsMain.class.getName()).log(Level.INFO, "notificationGroupUserNames.size() {0}", notificationGroup.get("userNames").size());
                String notificationGroupRuleField = notificationGroupRules.split("\\.")[0];
                String notificationGroupRuleFunction = notificationGroupRules.split("\\.")[1].substring(0, notificationGroupRules.split("\\.")[1].indexOf("("));
                String notificationGroupRuleValue = notificationGroupRules.split("\\.")[1].substring(notificationGroupRules.split("\\.")[1].indexOf("(") + 1, notificationGroupRules.split("\\.")[1].indexOf(")"));
                Iterator<JsonNode> userListNamesIterator = new UserListNames().getResponse().iterator();
                while (userListNamesIterator.hasNext()) {
                    String userName = userListNamesIterator.next().textValue();
                    if (notificationGroup.get("userNames").has(userName)) {
                        continue;
                    }
                    try {
                        JsonNode userConfig = mapper.readTree(UsersFolderLocator.getConfigFile(userName));
                        if (!userConfig.has(notificationGroupRuleField)) {
                            continue;
                        }
                        String fieldValue = userConfig.get(notificationGroupRuleField).textValue();
                        if (notificationGroupRuleFunction.startsWith("startsWith")) {
                            if (fieldValue.startsWith(notificationGroupRuleValue)) {
                                Logger.getLogger(UpdateNotificationsGroupsMain.class.getName()).log(Level.INFO, "ADD USER {0}", userName);
                                ((ObjectNode) notificationGroup.get("userNames")).set(userName, mapper.createObjectNode());
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(UpdateNotificationsGroupsMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                FileUtil.editFile(notificationGroup, notificationGroupFile);
            } catch (IOException ex) {
                Logger.getLogger(UpdateNotificationsGroupsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
