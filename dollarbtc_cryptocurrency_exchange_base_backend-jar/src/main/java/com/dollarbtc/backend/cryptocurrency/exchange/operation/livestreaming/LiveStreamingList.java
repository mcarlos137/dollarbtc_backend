/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
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
public class LiveStreamingList extends AbstractOperation<ArrayNode> {

    private final LiveStreamingListRequest liveStreamingListRequest;

    public LiveStreamingList(LiveStreamingListRequest liveStreamingListRequest) {
        super(ArrayNode.class);
        this.liveStreamingListRequest = liveStreamingListRequest;
    }

    @Override
    protected void execute() {
        ArrayNode liveStreamings = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("Positions");
        indexes.add("Types");
        indexes.add("UserNames");
        indexes.add("SubscriptorsNumbers");
        indexes.add("SubscriptionPrices");
        indexes.add("Statuses");
        indexes.add("Tags");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i[] = null;
            switch (index) {
                case "Positions":
                    i = liveStreamingListRequest.getPositions();
                    break;
                case "Types":
                    i = liveStreamingListRequest.getTypes();
                    break;
                case "UserNames":
                    i = liveStreamingListRequest.getUserNames();
                    break;
                case "SubscriptorsNumbers":
                    i = liveStreamingListRequest.getSubscriptorsNumbers();
                    break;
                case "SubscriptionPrices":
                    i = liveStreamingListRequest.getSubscriptionPrices();
                    break;
                case "Statuses":
                    i = liveStreamingListRequest.getStatuses();
                    break;
                case "Tags":
                    i = liveStreamingListRequest.getTags();
                    break;
                default:
                    break;
            }
            File liveStreamingsIndexFolder = LiveStreamingsFolderLocator.getIndexFolder(index);
            if (firstLoop) {
                if (i != null) {
                    for (String _i : i) {
                        if (_i == null) {
                            continue;
                        }
                        File liveStreamingsIndexValueFolder = new File(liveStreamingsIndexFolder, _i);
                        if (!liveStreamingsIndexValueFolder.isDirectory()) {
                            super.response = liveStreamings;
                            return;
                        }
                        for (File idFiles : liveStreamingsIndexValueFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                } else {
                    for (File liveStreamingsIndexValueFolder : liveStreamingsIndexFolder.listFiles()) {
                        if (!liveStreamingsIndexValueFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : liveStreamingsIndexValueFolder.listFiles()) {
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
                        File liveStreamingsIndexValueFolder = new File(liveStreamingsIndexFolder, _i);
                        if (!liveStreamingsIndexValueFolder.isDirectory()) {
                            super.response = liveStreamings;
                            return;
                        }
                        for (String id : ids) {
                            if (!new File(liveStreamingsIndexValueFolder, id + ".json").isFile()) {
                                continue;
                            }
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File liveStreamingsIndexValueFolder : liveStreamingsIndexFolder.listFiles()) {
                        if (!liveStreamingsIndexValueFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (!new File(liveStreamingsIndexValueFolder, id + ".json").isFile()) {
                                continue;
                            }
                            newIds.add(id);
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get liveStreamings
        for (String id : ids) {
            try {
                JsonNode liveStreaming = mapper.readTree(LiveStreamingsFolderLocator.getFile(id));
                String timestamp = liveStreaming.get("timestamp").textValue();
                if (liveStreamingListRequest.getInitTimestamp() == null && liveStreamingListRequest.getFinalTimestamp() == null) {
                    liveStreamings.add(liveStreaming);
                } else if (liveStreamingListRequest.getInitTimestamp() != null && liveStreamingListRequest.getFinalTimestamp() == null) {
                    if (timestamp.compareTo(liveStreamingListRequest.getInitTimestamp()) >= 0) {
                        liveStreamings.add(liveStreaming);
                    }
                } else if (liveStreamingListRequest.getInitTimestamp() == null && liveStreamingListRequest.getFinalTimestamp() != null) {
                    if (timestamp.compareTo(liveStreamingListRequest.getFinalTimestamp()) < 0) {
                        liveStreamings.add(liveStreaming);
                    }
                } else {
                    if (timestamp.compareTo(liveStreamingListRequest.getInitTimestamp()) >= 0 && timestamp.compareTo(liveStreamingListRequest.getFinalTimestamp()) < 0) {
                        liveStreamings.add(liveStreaming);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(LiveStreamingList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = liveStreamings;
    }

}
