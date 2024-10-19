/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BroadcastingCreate extends AbstractOperation<String> {

    private final BroadcastingCreateRequest broadcastingCreateRequest;

    public BroadcastingCreate(BroadcastingCreateRequest broadcastingCreateRequest) {
        super(String.class);
        this.broadcastingCreateRequest = broadcastingCreateRequest;
    }

    @Override
    public void execute() {
        String id = BaseOperation.getId();
        File broadcastingFile = new File(BroadcastingFolderLocator.getFolder(), id + ".json");
        JsonNode broadcasting = broadcastingCreateRequest.toJsonNode();
        ((ObjectNode) broadcasting).put("id", id);
        ((ObjectNode) broadcasting).put("timestamp", DateUtil.getCurrentDate());
        ((ObjectNode) broadcasting).putArray("episodes");
        ((ObjectNode) broadcasting).putArray("trailers");
        ((ObjectNode) broadcasting).putArray("tags");
        ((ObjectNode) broadcasting).put("commentsCount", 0);
        ((ObjectNode) broadcasting).put("status", "CREATED");
        FileUtil.createFile(broadcasting, broadcastingFile);
        createIndexesFolder(broadcasting);
        //createNotificationThread(broadcasting);
        super.response = "OK";
    }

    private static void createIndexesFolder(JsonNode broadcasting) {
        ObjectNode index = new ObjectMapper().createObjectNode();
        String id = broadcasting.get("id").textValue();
        index.put("id", id);
        index.put("timestamp", broadcasting.get("timestamp").textValue());
        //UserNames index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("UserNames", broadcasting.get("userName").textValue()), id + ".json"));
        //Types index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("Types", broadcasting.get("type").textValue()), id + ".json"));
        //Titles index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("Titles", broadcasting.get("title").textValue()), id + ".json"));
        //Tags index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("Tags", "not_defined"), id + ".json"));
        //Timestamp index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("Timestamps", broadcasting.get("timestamp").textValue()), id + ".json"));
        //Subscription Price index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("SubscriptionPrices", Double.toString(broadcasting.get("subscriptionPrice").doubleValue())), id + ".json"));
        //Subscriptors Number index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("SubscriptorsNumbers", Integer.toString(0)), id + ".json"));
        //Position index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("Positions", Integer.toString(0)), id + ".json"));
        //Status index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("Statuses", broadcasting.get("status").textValue()), id + ".json"));
        //Rating index
        FileUtil.createFile(index, new File(BroadcastingFolderLocator.getIndexValueFolder("Ratings", "not_defined"), id + ".json"));
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
