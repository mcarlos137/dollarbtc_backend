/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.subscriptionevent;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscriptionevent.SubscriptionEventListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.SubscriptionsEventsFolderLocator;
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
public class SubscriptionEventList extends AbstractOperation<ArrayNode> {

    private final SubscriptionEventListRequest subscriptionEventListRequest;

    public SubscriptionEventList(SubscriptionEventListRequest subscriptionEventListRequest) {
        super(ArrayNode.class);
        this.subscriptionEventListRequest = subscriptionEventListRequest;
    }

    @Override
    public void execute() {
        ArrayNode subscriptionsEvents = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("BaseUserNames");
        indexes.add("TargetUserNames");
        indexes.add("Types");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i = null;
            switch (index) {
                case "BaseUserNames":
                    if (subscriptionEventListRequest.getBaseUserName() != null && !subscriptionEventListRequest.getBaseUserName().equals("")) {
                        i = subscriptionEventListRequest.getBaseUserName();
                    }
                    break;
                case "TargetUserNames":
                    if (subscriptionEventListRequest.getTargetUserName() != null && !subscriptionEventListRequest.getTargetUserName().equals("")) {
                        i = subscriptionEventListRequest.getTargetUserName();
                    }
                    break;
                case "Types":
                    if (subscriptionEventListRequest.getSubscriptionEventType() != null) {
                        i = subscriptionEventListRequest.getSubscriptionEventType().name();
                    }
                    break;
            }
            File subscriptionsEventsIndexesFolder = SubscriptionsEventsFolderLocator.getIndexesSpecificFolder(index);
            if (firstLoop) {
                if (i != null && !i.equals("")) {
                    File subscriptionsEventsIndexFolder = new File(subscriptionsEventsIndexesFolder, i);
                    if (!subscriptionsEventsIndexFolder.isDirectory()) {
                        super.response = subscriptionsEvents;
                        return;
                    }
                    for (File idFiles : subscriptionsEventsIndexFolder.listFiles()) {
                        ids.add(idFiles.getName().replace(".json", ""));
                    }
                } else {
                    for (File subscriptionsEventsIndexFolder : subscriptionsEventsIndexesFolder.listFiles()) {
                        if (!subscriptionsEventsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : subscriptionsEventsIndexFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                }
                firstLoop = false;
            } else {
                Set<String> newIds = new HashSet<>();
                if (i != null && !i.equals("")) {
                    File subscriptionsEventsIndexFolder = new File(subscriptionsEventsIndexesFolder, i);
                    if (!subscriptionsEventsIndexFolder.isDirectory()) {
                        super.response = subscriptionsEvents;
                        return;
                    }
                    for (String id : ids) {
                        if (new File(subscriptionsEventsIndexFolder, id + ".json").isFile()) {
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File subscriptionsEventsIndexFolder : subscriptionsEventsIndexesFolder.listFiles()) {
                        if (!subscriptionsEventsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (new File(subscriptionsEventsIndexFolder, id + ".json").isFile()) {
                                newIds.add(id);
                            }
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        System.out.println(">>>>>>>>>>>>12 " + ids);
        for (String id : ids) {
            File subscriptionEventFile = SubscriptionsEventsFolderLocator.getFile(id);
            try {
                JsonNode subscriptionEvent = mapper.readTree(subscriptionEventFile);
                subscriptionsEvents.add(subscriptionEvent);
            } catch (IOException ex) {
                Logger.getLogger(SubscriptionEventList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = subscriptionsEvents;
    }

}
