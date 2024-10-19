/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
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
public class LiveStreamingChangeStatus extends AbstractOperation<String> {

    private final LiveStreamingChangeStatusRequest liveStreamingChangeStatusRequest;

    public LiveStreamingChangeStatus(LiveStreamingChangeStatusRequest liveStreamingChangeStatusRequest) {
        super(String.class);
        this.liveStreamingChangeStatusRequest = liveStreamingChangeStatusRequest;
    }

    @Override
    public void execute() {
        File liveStreamingFile = LiveStreamingsFolderLocator.getFile(liveStreamingChangeStatusRequest.getId());
        try {
            JsonNode liveStreaming = mapper.readTree(liveStreamingFile);
            String lastStatus = liveStreaming.get("status").textValue();
            if (liveStreamingChangeStatusRequest.getPublicationId() == null) {
                ((ObjectNode) liveStreaming).put("status", liveStreamingChangeStatusRequest.getStatus());
                ((ObjectNode) liveStreaming).put("changeStatusTimestamp", DateUtil.getCurrentDate());
            } else {
                boolean stop = false;
                Iterator<JsonNode> publicationsIterator = liveStreaming.get("publications").iterator();
                while (publicationsIterator.hasNext()) {
                    JsonNode publicationsIt = publicationsIterator.next();
                    if (publicationsIt.get("id").textValue().equals(liveStreamingChangeStatusRequest.getPublicationId())) {
                        ((ObjectNode) publicationsIt).put("status", liveStreamingChangeStatusRequest.getStatus());
                        ((ObjectNode) publicationsIt).put("changeStatusTimestamp", DateUtil.getCurrentDate());
                        stop = true;
                        break;
                    }
                }
            }
            FileUtil.editFile(liveStreaming, liveStreamingFile);
            if (liveStreamingChangeStatusRequest.getPublicationId() == null) {
                FileUtil.moveFileToFolder(new File(LiveStreamingsFolderLocator.getIndexValueFolder("Statuses", lastStatus), liveStreamingChangeStatusRequest.getId() + ".json"), LiveStreamingsFolderLocator.getIndexValueFolder("Statuses", liveStreamingChangeStatusRequest.getStatus()));
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(LiveStreamingChangeStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        //createNotificationThread(liveStreaming);
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
