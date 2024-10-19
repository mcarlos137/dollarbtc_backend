/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription.SubscriptionListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.SubscriptionsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class SubscriptionList extends AbstractOperation<ArrayNode> {

    private final SubscriptionListRequest subscriptionListRequest;

    public SubscriptionList(SubscriptionListRequest subscriptionListRequest) {
        super(ArrayNode.class);
        this.subscriptionListRequest = subscriptionListRequest;
    }

    @Override
    public void execute() {
        ArrayNode subscriptions = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("ObjectDetailsIds");
        indexes.add("BaseUserNames");
        indexes.add("TargetUserNames");
        indexes.add("Types");
        indexes.add("Statuses");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i = null;
            switch (index) {
                case "ObjectDetailsIds":
                    if (subscriptionListRequest.getObjectDetailsId() != null && !subscriptionListRequest.getObjectDetailsId().equals("")) {
                        i = subscriptionListRequest.getObjectDetailsId();
                    }
                    break;
                case "BaseUserNames":
                    if (subscriptionListRequest.getBaseUserName() != null && !subscriptionListRequest.getBaseUserName().equals("")) {
                        i = subscriptionListRequest.getBaseUserName();
                    }
                    break;
                case "TargetUserNames":
                    if (subscriptionListRequest.getTargetUserName() != null && !subscriptionListRequest.getTargetUserName().equals("")) {
                        i = subscriptionListRequest.getTargetUserName();
                    }
                    break;
                case "Types":
                    if (subscriptionListRequest.getSubscriptionType() != null) {
                        i = subscriptionListRequest.getSubscriptionType().name();
                    }
                    break;
                case "Statuses":
                    if (subscriptionListRequest.getSubscriptionStatus() != null) {
                        i = subscriptionListRequest.getSubscriptionStatus().name();
                    }
                    break;
            }
            File subscriptionsIndexesFolder = SubscriptionsFolderLocator.getIndexesSpecificFolder(index);
            if (firstLoop) {
                if (i != null && !i.equals("")) {
                    File subscriptionsIndexFolder = new File(subscriptionsIndexesFolder, i);
                    if (!subscriptionsIndexFolder.isDirectory()) {
                        super.response = subscriptions;
                        return;
                    }
                    for (File idFiles : subscriptionsIndexFolder.listFiles()) {
                        ids.add(idFiles.getName().replace(".json", ""));
                    }
                } else {
                    for (File subscriptionsIndexFolder : subscriptionsIndexesFolder.listFiles()) {
                        if (!subscriptionsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : subscriptionsIndexFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                }
                firstLoop = false;
            } else {
                Set<String> newIds = new HashSet<>();
                if (i != null && !i.equals("")) {
                    File subscriptionsIndexFolder = new File(subscriptionsIndexesFolder, i);
                    if (!subscriptionsIndexFolder.isDirectory()) {
                        super.response = subscriptions;
                        return;
                    }
                    for (String id : ids) {
                        if (new File(subscriptionsIndexFolder, id + ".json").isFile()) {
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File subscriptionsIndexFolder : subscriptionsIndexesFolder.listFiles()) {
                        if (!subscriptionsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (new File(subscriptionsIndexFolder, id + ".json").isFile()) {
                                newIds.add(id);
                            }
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        for (String id : ids) {
            File subscriptionFile = SubscriptionsFolderLocator.getFile(id);
            try {
                JsonNode subscription = mapper.readTree(subscriptionFile);
                /*if (subscription.get("side").textValue().equals("CREATE")) {
                    String name = subscription.get("name").textValue();
                    ((ObjectNode) subscription).putArray("clients");
                    if (!subscription.has("earnedAmount")) {
                        ((ObjectNode) subscription).put("earnedAmount", 0.0);
                    }
                    String userName = subscription.get("userName").textValue();
                    File subscriptionsIndexValueFolder = new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("CreateUserNames"), userName);
                    if (subscriptionsIndexValueFolder.isDirectory()) {
                        for (File subscriptionNameFile : subscriptionsIndexValueFolder.listFiles()) {
                            String subscriptionName = subscriptionNameFile.getName().replace(".json", "");
                            JsonNode subscriptionClient = mapper.readTree(SubscriptionsFolderLocator.getFile(subscriptionName));
                            String createName = subscriptionClient.get("createName").textValue();
                            if (name.equals(createName)) {
                                ((ArrayNode) subscription.get("clients")).add(subscriptionClient);
                                Double amount = subscriptionClient.get("amount").doubleValue();
                                ((ObjectNode) subscription).put("earnedAmount", subscription.get("earnedAmount").doubleValue() + amount);
                            }
                        }
                    }
                }*/
                subscriptions.add(subscription);
            } catch (IOException ex) {
                Logger.getLogger(SubscriptionList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = subscriptions;
    }

}
