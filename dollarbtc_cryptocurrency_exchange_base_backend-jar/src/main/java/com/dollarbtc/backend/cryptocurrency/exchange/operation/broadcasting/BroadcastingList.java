/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
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
public class BroadcastingList extends AbstractOperation<ArrayNode> {

    private final BroadcastingListRequest broadcastingListRequest;

    public BroadcastingList(BroadcastingListRequest broadcastingListRequest) {
        super(ArrayNode.class);
        this.broadcastingListRequest = broadcastingListRequest;
    }

    @Override
    protected void execute() {
        ArrayNode broadcastings = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("Positions");
        indexes.add("UserNames");
        indexes.add("Types");
        indexes.add("Titles");
        indexes.add("SubscriptorsNumbers");
        indexes.add("SubscriptionPrices");
        indexes.add("Tags");
        indexes.add("Ratings");
        indexes.add("Statuses");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i[] = null;
            switch (index) {
                case "Positions":
                    i = broadcastingListRequest.getPositions();
                    break;
                case "UserNames":
                    i = broadcastingListRequest.getUserNames();
                    break;
                case "Types":
                    i = broadcastingListRequest.getTypes();
                    break;
                case "Titles":
                    i = broadcastingListRequest.getTitles();
                    break;
                case "SubscriptorsNumbers":
                    i = broadcastingListRequest.getSubscriptorsNumbers();
                    break;
                case "SubscriptionPrices":
                    i = broadcastingListRequest.getSubscriptionPrices();
                    break;
                case "Tags":
                    i = broadcastingListRequest.getTags();
                    break;
                case "Ratings":
                    i = broadcastingListRequest.getRatings();
                    break;
                case "Statuses":
                    i = broadcastingListRequest.getStatuses();
                    break;
                default:
                    break;
            }
            File broadcastingsIndexFolder = BroadcastingFolderLocator.getIndexFolder(index);
            if (firstLoop) {
                if (i != null) {
                    for (String _i : i) {
                        if (_i == null) {
                            continue;
                        }
                        File broadcastingsIndexValueFolder = new File(broadcastingsIndexFolder, _i);
                        if (!broadcastingsIndexValueFolder.isDirectory()) {
                            super.response = broadcastings;
                            return;
                        }
                        for (File idFiles : broadcastingsIndexValueFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                } else {
                    for (File broadcastingsIndexValueFolder : broadcastingsIndexFolder.listFiles()) {
                        if (!broadcastingsIndexValueFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : broadcastingsIndexValueFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                }
                firstLoop = false;
            } else {
                Set<String> newIds = new HashSet<>();
                if (i != null) {
                    for (String _i : i) {
                        if (_i == null) {
                            continue;
                        }
                        File broadcastingsIndexValueFolder = new File(broadcastingsIndexFolder, _i);
                        if (!broadcastingsIndexValueFolder.isDirectory()) {
                            super.response = broadcastings;
                            return;
                        }
                        for (String id : ids) {
                            if (!new File(broadcastingsIndexValueFolder, id + ".json").isFile()) {
                                continue;
                            }
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File broadcastingsIndexValueFolder : broadcastingsIndexFolder.listFiles()) {
                        if (!broadcastingsIndexValueFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (!new File(broadcastingsIndexValueFolder, id + ".json").isFile()) {
                                continue;
                            }
                            newIds.add(id);
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get broadcastings
        for (String id : ids) {
            try {
                JsonNode broadcasting = mapper.readTree(BroadcastingFolderLocator.getFile(id));
                String timestamp = broadcasting.get("timestamp").textValue();
                if (broadcastingListRequest.getInitTimestamp() == null && broadcastingListRequest.getFinalTimestamp() == null) {
                    broadcastings.add(broadcasting);
                } else if (broadcastingListRequest.getInitTimestamp() != null && broadcastingListRequest.getFinalTimestamp() == null) {
                    if (timestamp.compareTo(broadcastingListRequest.getInitTimestamp()) >= 0) {
                        broadcastings.add(broadcasting);
                    }
                } else if (broadcastingListRequest.getInitTimestamp() == null && broadcastingListRequest.getFinalTimestamp() != null) {
                    if (timestamp.compareTo(broadcastingListRequest.getFinalTimestamp()) < 0) {
                        broadcastings.add(broadcasting);
                    }
                } else {
                    if (timestamp.compareTo(broadcastingListRequest.getInitTimestamp()) >= 0 && timestamp.compareTo(broadcastingListRequest.getFinalTimestamp()) < 0) {
                        broadcastings.add(broadcasting);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(BroadcastingList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = broadcastings;
    }

}
