/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingChangeTagsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BroadcastingChangeTags extends AbstractOperation<String> {

    private final BroadcastingChangeTagsRequest broadcastingChangeTagsRequest;

    public BroadcastingChangeTags(BroadcastingChangeTagsRequest broadcastingChangeTagsRequest) {
        super(String.class);
        this.broadcastingChangeTagsRequest = broadcastingChangeTagsRequest;
    }

    @Override
    public void execute() {
        File broadcastingFile = new File(BroadcastingFolderLocator.getFolder(), BroadcastingFolderLocator.getFile(broadcastingChangeTagsRequest.getId()) + ".json");
        try {
            JsonNode broadcasting = mapper.readTree(broadcastingFile);
            FileUtil.editFile(broadcasting, broadcastingFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(BroadcastingChangeTags.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    /*private void createNotificationThread(JsonNode broadcasting) {
        Thread createNotificationThread = new Thread(() -> {
            String notificationUserName = broadcasting.get("receiverUserName").textValue();
            String notificationName = broadcasting.get("receiverName").textValue();
            String notificationVerb = "pay";
            if (notificationUserName.equals(broadcasting.get("createUserName").textValue())) {
                notificationUserName = broadcasting.get("senderUserName").textValue();
                notificationName = broadcasting.get("senderName").textValue();
                notificationVerb = "charge";
            }
            String amount = String.format("%.2f", (broadcasting.get("rate").doubleValue() * broadcasting.get("estimatedTime").intValue()));
            String message = notificationName + " wants a Money Call. This call is going to " + notificationVerb + " you approximate of " + amount + " " + broadcasting.get("currency").textValue() + " please go to Money Call section for more details.";
            new NotificationSendMessageByUserName(notificationUserName, "Money Call", message).getResponse();
        });
        createNotificationThread.start();
    }*/
}
