/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
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
public class ShortsList extends AbstractOperation<ArrayNode> {

    private final ShortsListRequest shortsListRequest;

    public ShortsList(ShortsListRequest shortsListRequest) {
        super(ArrayNode.class);
        this.shortsListRequest = shortsListRequest;
    }

    @Override
    protected void execute() {
        ArrayNode shortss = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("Tags");
        indexes.add("Statuses");
        indexes.add("UserNames");
        indexes.add("Titles");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i[] = null;
            switch (index) {
                case "Tags":
                    i = shortsListRequest.getTags();
                    break;
                case "Statuses":
                    i = shortsListRequest.getStatuses();
                    break;
                case "UserNames":
                    i = shortsListRequest.getUserNames();
                    break;
                case "Titles":
                    i = shortsListRequest.getTitles();
                    break;
                default:
                    break;
            }
            File shortsIndexFolder = ShortsFolderLocator.getIndexFolder(index);
            if (firstLoop) {
                if (i != null) {
                    for (String _i : i) {
                        if (_i == null) {
                            continue;
                        }
                        File shortsIndexValueFolder = new File(shortsIndexFolder, _i);
                        if (!shortsIndexValueFolder.isDirectory()) {
                            super.response = shortss;
                            return;
                        }
                        for (File idFiles : shortsIndexValueFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                } else {
                    for (File shortsIndexValueFolder : shortsIndexFolder.listFiles()) {
                        if (!shortsIndexValueFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : shortsIndexValueFolder.listFiles()) {
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
                        File shortsIndexValueFolder = new File(shortsIndexFolder, _i);
                        if (!shortsIndexValueFolder.isDirectory()) {
                            super.response = shortss;
                            return;
                        }
                        for (String id : ids) {
                            if (!new File(shortsIndexValueFolder, id + ".json").isFile()) {
                                continue;
                            }
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File shortsIndexValueFolder : shortsIndexFolder.listFiles()) {
                        if (!shortsIndexValueFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (!new File(shortsIndexValueFolder, id + ".json").isFile()) {
                                continue;
                            }
                            newIds.add(id);
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get shorts
        for (String id : ids) {
            try {
                JsonNode shorts = mapper.readTree(ShortsFolderLocator.getFile(id));
                String timestamp = shorts.get("timestamp").textValue();
                if (shortsListRequest.getInitTimestamp() == null && shortsListRequest.getFinalTimestamp() == null) {
                    shortss.add(shorts);
                } else if (shortsListRequest.getInitTimestamp() != null && shortsListRequest.getFinalTimestamp() == null) {
                    if (timestamp.compareTo(shortsListRequest.getInitTimestamp()) >= 0) {
                        shortss.add(shorts);
                    }
                } else if (shortsListRequest.getInitTimestamp() == null && shortsListRequest.getFinalTimestamp() != null) {
                    if (timestamp.compareTo(shortsListRequest.getFinalTimestamp()) < 0) {
                        shortss.add(shorts);
                    }
                } else {
                    if (timestamp.compareTo(shortsListRequest.getInitTimestamp()) >= 0 && timestamp.compareTo(shortsListRequest.getFinalTimestamp()) < 0) {
                        shortss.add(shorts);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ShortsList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = shortss;
    }

}
