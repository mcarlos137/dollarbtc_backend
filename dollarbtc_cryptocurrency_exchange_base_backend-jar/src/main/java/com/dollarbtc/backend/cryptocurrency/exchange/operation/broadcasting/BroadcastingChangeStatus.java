/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
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
public class BroadcastingChangeStatus extends AbstractOperation<String> {

    private final BroadcastingChangeStatusRequest broadcastingChangeStatusRequest;

    public BroadcastingChangeStatus(BroadcastingChangeStatusRequest broadcastingChangeStatusRequest) {
        super(String.class);
        this.broadcastingChangeStatusRequest = broadcastingChangeStatusRequest;
    }

    @Override
    public void execute() {
        File broadcastingFile = BroadcastingFolderLocator.getFile(broadcastingChangeStatusRequest.getBroadcastingId());
        try {
            JsonNode broadcasting = mapper.readTree(broadcastingFile);
            String lastStatus = broadcasting.get("status").textValue();
            if (broadcastingChangeStatusRequest.getEpisoseTrailerId() == null) {
                ((ObjectNode) broadcasting).put("status", broadcastingChangeStatusRequest.getStatus());
                ((ObjectNode) broadcasting).put("changeStatusTimestamp", DateUtil.getCurrentDate());
            } else {
                boolean stop = false;
                Iterator<JsonNode> episodesIterator = broadcasting.get("episodes").iterator();
                while (episodesIterator.hasNext()) {
                    JsonNode episodesIt = episodesIterator.next();
                    if (episodesIt.get("id").textValue().equals(broadcastingChangeStatusRequest.getEpisoseTrailerId())) {
                        ((ObjectNode) episodesIt).put("status", broadcastingChangeStatusRequest.getStatus());
                        ((ObjectNode) episodesIt).put("changeStatusTimestamp", DateUtil.getCurrentDate());
                        stop = true;
                        break;
                    }
                }
                if (!stop) {
                    Iterator<JsonNode> trailersIterator = broadcasting.get("trailers").iterator();
                    while (trailersIterator.hasNext()) {
                        JsonNode trailersIt = trailersIterator.next();
                        if (trailersIt.get("id").textValue().equals(broadcastingChangeStatusRequest.getEpisoseTrailerId())) {
                            ((ObjectNode) trailersIt).put("status", broadcastingChangeStatusRequest.getStatus());
                            ((ObjectNode) trailersIt).put("changeStatusTimestamp", DateUtil.getCurrentDate());
                            break;
                        }
                    }
                }
            }
            FileUtil.editFile(broadcasting, broadcastingFile);
            if (broadcastingChangeStatusRequest.getEpisoseTrailerId() == null) {
                FileUtil.moveFileToFolder(new File(BroadcastingFolderLocator.getIndexValueFolder("Statuses", lastStatus), broadcastingChangeStatusRequest.getBroadcastingId() + ".json"), BroadcastingFolderLocator.getIndexValueFolder("Statuses", broadcastingChangeStatusRequest.getStatus()));
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(BroadcastingChangeStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        //createNotificationThread(broadcasting);
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
