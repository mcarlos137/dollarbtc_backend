/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification.NotificationAddTokenToUserRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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
public class NotificationAddTokenToUser extends AbstractOperation<String> {

    private final NotificationAddTokenToUserRequest notificationAddTokenToUserRequest;

    public NotificationAddTokenToUser(NotificationAddTokenToUserRequest notificationAddTokenToUserRequest) {
        super(String.class);
        this.notificationAddTokenToUserRequest = notificationAddTokenToUserRequest;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(notificationAddTokenToUserRequest.getUserName());
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (userConfig.has("notificationToken") && userConfig.get("notificationToken").textValue().equals(notificationAddTokenToUserRequest.getToken())) {
                return;
            }
            if (userConfig.has("notificationToken")) {
                if (!userConfig.has("otherNotificationTokens")) {
                    ((ObjectNode) userConfig).set("otherNotificationTokens", mapper.createObjectNode());
                }
                ((ObjectNode) userConfig.get("otherNotificationTokens")).put(userConfig.get("notificationToken").textValue(), DateUtil.getCurrentDate());
            }
            ((ObjectNode) userConfig).put("notificationToken", notificationAddTokenToUserRequest.getToken());
            FileUtil.editFile(userConfig, userConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(NotificationAddTokenToUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
