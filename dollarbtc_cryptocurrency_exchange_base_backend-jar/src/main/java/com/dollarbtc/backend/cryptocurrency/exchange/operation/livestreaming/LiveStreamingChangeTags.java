/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingChangeTagsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class LiveStreamingChangeTags extends AbstractOperation<String> {

    private final LiveStreamingChangeTagsRequest liveStreamingChangeTagsRequest;

    public LiveStreamingChangeTags(LiveStreamingChangeTagsRequest liveStreamingChangeTagsRequest) {
        super(String.class);
        this.liveStreamingChangeTagsRequest = liveStreamingChangeTagsRequest;
    }

    @Override
    public void execute() {
        File liveStreamingFile = new File(LiveStreamingsFolderLocator.getFolder(), LiveStreamingsFolderLocator.getFile(liveStreamingChangeTagsRequest.getId()) + ".json");
        try {
            JsonNode liveStreaming = mapper.readTree(liveStreamingFile);
            FileUtil.editFile(liveStreaming, liveStreamingFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(LiveStreamingChangeTags.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    /*private void createNotificationThread(JsonNode liveStreaming) {
        Thread createNotificationThread = new Thread(() -> {
            String notificationUserName = liveStreaming.get("receiverUserName").textValue();
            String notificationName = liveStreaming.get("receiverName").textValue();
            String notificationVerb = "pay";
            if (notificationUserName.equals(liveStreaming.get("createUserName").textValue())) {
                notificationUserName = liveStreaming.get("senderUserName").textValue();
                notificationName = liveStreaming.get("senderName").textValue();
                notificationVerb = "charge";
            }
            String amount = String.format("%.2f", (liveStreaming.get("rate").doubleValue() * liveStreaming.get("estimatedTime").intValue()));
            String message = notificationName + " wants a Money Call. This call is going to " + notificationVerb + " you approximate of " + amount + " " + liveStreaming.get("currency").textValue() + " please go to Money Call section for more details.";
            new NotificationSendMessageByUserName(notificationUserName, "Money Call", message).getResponse();
        });
        createNotificationThread.start();
    }*/
}
