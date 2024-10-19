/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class LiveStreamingCreate extends AbstractOperation<String> {

    private final LiveStreamingCreateRequest liveStreamingCreateRequest;

    public LiveStreamingCreate(LiveStreamingCreateRequest liveStreamingCreateRequest) {
        super(String.class);
        this.liveStreamingCreateRequest = liveStreamingCreateRequest;
    }

    @Override
    public void execute() {
        String id = BaseOperation.getId();
        File liveStreamingFile = new File(LiveStreamingsFolderLocator.getFolder(), id + ".json");
        JsonNode liveStreaming = liveStreamingCreateRequest.toJsonNode();
        ((ObjectNode) liveStreaming).put("id", id);
        ((ObjectNode) liveStreaming).put("timestamp", DateUtil.getCurrentDate());
        ((ObjectNode) liveStreaming).put("status", "ACTIVE");
        ((ObjectNode) liveStreaming).putArray("tags");
        ((ObjectNode) liveStreaming).putArray("publications");
        ((ObjectNode) liveStreaming).put("commentsCount", 0);
        FileUtil.createFile(liveStreaming, liveStreamingFile);
        createIndexesFolder(liveStreaming);
        super.response = "OK";
    }

    private static void createIndexesFolder(JsonNode liveStreaming) {
        ObjectNode index = new ObjectMapper().createObjectNode();
        String id = liveStreaming.get("id").textValue();
        index.put("id", id);
        index.put("timestamp", liveStreaming.get("timestamp").textValue());
        //UserName index
        FileUtil.createFile(index, new File(LiveStreamingsFolderLocator.getIndexValueFolder("UserNames", liveStreaming.get("userName").textValue()), id + ".json"));
        //Timestamp index
        FileUtil.createFile(index, new File(LiveStreamingsFolderLocator.getIndexValueFolder("Timestamps", liveStreaming.get("timestamp").textValue()), id + ".json"));
        //Subscription Price index
        FileUtil.createFile(index, new File(LiveStreamingsFolderLocator.getIndexValueFolder("SubscriptionPrices", Double.toString(liveStreaming.get("subscriptionPrice").doubleValue())), id + ".json"));
        //Subscriptors Number index
        FileUtil.createFile(index, new File(LiveStreamingsFolderLocator.getIndexValueFolder("SubscriptorsNumbers", Integer.toString(0)), id + ".json"));
        //Position index
        FileUtil.createFile(index, new File(LiveStreamingsFolderLocator.getIndexValueFolder("Positions", Integer.toString(0)), id + ".json"));
        //Status index
        FileUtil.createFile(index, new File(LiveStreamingsFolderLocator.getIndexValueFolder("Statuses", liveStreaming.get("status").textValue()), id + ".json"));
        //Types index
        FileUtil.createFile(index, new File(LiveStreamingsFolderLocator.getIndexValueFolder("Types", liveStreaming.get("type").textValue()), id + ".json"));
    }

}
