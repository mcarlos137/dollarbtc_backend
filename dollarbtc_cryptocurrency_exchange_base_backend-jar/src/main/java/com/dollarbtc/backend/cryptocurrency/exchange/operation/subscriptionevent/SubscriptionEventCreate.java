/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.subscriptionevent;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscriptionevent.SubscriptionEventCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.SubscriptionsEventsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class SubscriptionEventCreate extends AbstractOperation<String> {

    private final SubscriptionEventCreateRequest subscriptionEventCreateRequest;

    public SubscriptionEventCreate(SubscriptionEventCreateRequest subscriptionEventCreateRequest) {
        super(String.class);
        this.subscriptionEventCreateRequest = subscriptionEventCreateRequest;
    }

    @Override
    protected void execute() {
        File subscriptionEventFile = SubscriptionsEventsFolderLocator.getFile(subscriptionEventCreateRequest.getId());
        FileUtil.createFile(subscriptionEventCreateRequest.toJsonNode(), subscriptionEventFile);
        addIndexes(subscriptionEventCreateRequest.toJsonNode());
        super.response = "OK";
    }

    private void addIndexes(JsonNode subscriptionEvent) {
        JsonNode index = mapper.createObjectNode();
        String id = subscriptionEvent.get("id").textValue();
        ((ObjectNode) index).put("id", id);
        ((ObjectNode) index).put("timestamp", subscriptionEvent.get("timestamp").textValue());
        //BaseUserNames
        FileUtil.createFile(index, new File(SubscriptionsEventsFolderLocator.getIndexesSpecificFolder("BaseUserNames", subscriptionEvent.get("baseUserName").textValue()), id + ".json"));
        //TargetUserNames
        FileUtil.createFile(index, new File(SubscriptionsEventsFolderLocator.getIndexesSpecificFolder("TargetUserNames", subscriptionEvent.get("targetUserName").textValue()), id + ".json"));
        //Types
        FileUtil.createFile(index, new File(SubscriptionsEventsFolderLocator.getIndexesSpecificFolder("Types", subscriptionEvent.get("type").textValue()), id + ".json"));
    }

}
