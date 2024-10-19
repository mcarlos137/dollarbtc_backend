/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingAddEpisodeTrailerRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription.SubscriptionListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscriptionevent.SubscriptionEventCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionEventType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription.SubscriptionList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscriptionevent.SubscriptionEventCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
public class BroadcastingAddEpisodeTrailer extends AbstractOperation<String> {

    private final BroadcastingAddEpisodeTrailerRequest broadcastingAddEpisodeTrailerRequest;

    public BroadcastingAddEpisodeTrailer(BroadcastingAddEpisodeTrailerRequest broadcastingAddEpisodeTrailerRequest) {
        super(String.class);
        this.broadcastingAddEpisodeTrailerRequest = broadcastingAddEpisodeTrailerRequest;
    }

    @Override
    public void execute() {
        File broadcastingFile = BroadcastingFolderLocator.getFile(broadcastingAddEpisodeTrailerRequest.getBroadcastingId());
        try {
            JsonNode broadcasting = mapper.readTree(broadcastingFile);
            if (!broadcasting.get("status").textValue().equals("PUBLISHED")) {
                super.response = "BROADCASTING IS NOT PUBLISHED";
                return;
            }
            String id = BaseOperation.getId();
            JsonNode episodeTrailer = broadcastingAddEpisodeTrailerRequest.toJsonNode();
            ((ObjectNode) episodeTrailer).put("broadcastingType", broadcasting.get("type").textValue());
            ((ObjectNode) episodeTrailer).put("id", id);
            ((ObjectNode) episodeTrailer).put("timestamp", DateUtil.getCurrentDate());
            ((ObjectNode) episodeTrailer).put("status", "CREATED");
            String type = "episodes";
            if (broadcastingAddEpisodeTrailerRequest.getType().equals("TRAILER")) {
                type = "trailers";
            }
            ((ObjectNode) broadcasting).put("status", "PUBLISHED_WITH_EPISODE_TRAILER_PENDING");
            ((ArrayNode) broadcasting.get(type)).add(episodeTrailer);
            FileUtil.editFile(broadcasting, broadcastingFile);
            ((ObjectNode) episodeTrailer).put("title", broadcasting.get("title").textValue() + " " + episodeTrailer.get("title").textValue());
            subscriptionEventThread(episodeTrailer, broadcastingAddEpisodeTrailerRequest.getType());
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(BroadcastingAddEpisodeTrailer.class.getName()).log(Level.SEVERE, null, ex);
        }
        //createNotificationThread(broadcasting);
        super.response = "FAIL";
    }

    private void subscriptionEventThread(JsonNode episodeTrailer, String type) {
        Thread subscriptionEventThread = new Thread(() -> {
            SubscriptionEventType subscriptionEventType = SubscriptionEventType.BROADCASTING__EPISODE;
            if (type.equals("TRAILER")) {
                subscriptionEventType = SubscriptionEventType.BROADCASTING__TRAILER;
            }
            Iterator<JsonNode> subscriptionListIterator = new SubscriptionList(new SubscriptionListRequest(null, null, episodeTrailer.get("broadcastingId").textValue(), SubscriptionType.BROADCASTING, SubscriptionStatus.ACTIVE)).getResponse().iterator();
            while (subscriptionListIterator.hasNext()) {
                JsonNode subscriptionListIt = subscriptionListIterator.next();
                String targetName = null;
                if (subscriptionListIt.has("targetName")) {
                    targetName = subscriptionListIt.get("targetName").textValue();
                }
                new SubscriptionEventCreate(
                        new SubscriptionEventCreateRequest(
                                subscriptionListIt.get("baseUserName").textValue(),
                                subscriptionListIt.get("targetUserName").textValue(),
                                subscriptionListIt.get("baseName").textValue(),
                                targetName,
                                episodeTrailer,
                                subscriptionEventType
                        )
                ).getResponse();
            }
        });
        subscriptionEventThread.start();
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
