/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

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
public class NotificationMarkMessageAsReaded extends AbstractOperation<String> {

    private final String userName, timestamp;

    public NotificationMarkMessageAsReaded(String userName, String timestamp) {
        super(String.class);
        this.userName = userName;
        this.timestamp = timestamp;
    }

    @Override
    protected void execute() {
        File userNotificationFile = new File(UsersFolderLocator.getNotificationsFolder(userName), DateUtil.getFileDate(timestamp) + ".json");
        if(!userNotificationFile.isFile()){
            super.response = "NOTIFICATION DOES NOT EXIST";
        }
        try {
            JsonNode userNotification = mapper.readTree(userNotificationFile);
            ((ObjectNode) userNotification).put("readed", true);
            FileUtil.editFile(userNotification, userNotificationFile);
            super.response = "OK";
        } catch (IOException ex) {
            Logger.getLogger(NotificationMarkMessageAsReaded.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
