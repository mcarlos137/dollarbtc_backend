/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingReactRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
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
public class BroadcastingReact extends AbstractOperation<String> {

    private final BroadcastingReactRequest broadcastingReactRequest;

    public BroadcastingReact(BroadcastingReactRequest broadcastingReactRequest) {
        super(String.class);
        this.broadcastingReactRequest = broadcastingReactRequest;
    }

    @Override
    public void execute() {
        try {
            ObjectNode reaction = mapper.createObjectNode();
            reaction.put("timestamp", DateUtil.getCurrentDate());
            reaction.put("name", broadcastingReactRequest.getName());
            if (broadcastingReactRequest.getCommentId() == null) {
                File broadcastingFile = BroadcastingFolderLocator.getFile(broadcastingReactRequest.getId());
                JsonNode broadcasting = mapper.readTree(broadcastingFile);
                boolean breakLoop = false;
                Iterator<JsonNode> broadcastingEpisodesTrailersIterator = broadcasting.get("episodes").iterator();
                while (broadcastingEpisodesTrailersIterator.hasNext()) {
                    JsonNode broadcastingEpisodesTrailersIt = broadcastingEpisodesTrailersIterator.next();
                    if (broadcastingEpisodesTrailersIt.get("id").textValue().equals(broadcastingReactRequest.getEpisodeTrailerId())) {
                        if (!broadcastingEpisodesTrailersIt.has("reactions")) {
                            ((ObjectNode) broadcastingEpisodesTrailersIt).set("reactions", mapper.createObjectNode());
                        }
                        if (!broadcastingEpisodesTrailersIt.get("reactions").has(broadcastingReactRequest.getReaction())) {
                            ((ObjectNode) broadcastingEpisodesTrailersIt.get("reactions")).set(broadcastingReactRequest.getReaction(), mapper.createObjectNode());
                        }
                        ((ObjectNode) broadcastingEpisodesTrailersIt.get("reactions").get(broadcastingReactRequest.getReaction())).set(broadcastingReactRequest.getUserName(), reaction);
                        breakLoop = true;
                        break;
                    }
                }
                if (!breakLoop) {
                    broadcastingEpisodesTrailersIterator = broadcasting.get("trailers").iterator();
                    while (broadcastingEpisodesTrailersIterator.hasNext()) {
                        JsonNode broadcastingEpisodesTrailersIt = broadcastingEpisodesTrailersIterator.next();
                        if (broadcastingEpisodesTrailersIt.get("id").textValue().equals(broadcastingReactRequest.getEpisodeTrailerId())) {
                            if (!broadcastingEpisodesTrailersIt.has("reactions")) {
                                ((ObjectNode) broadcastingEpisodesTrailersIt).set("reactions", mapper.createObjectNode());
                            }
                            if (!broadcastingEpisodesTrailersIt.get("reactions").has(broadcastingReactRequest.getReaction())) {
                                ((ObjectNode) broadcastingEpisodesTrailersIt.get("reactions")).set(broadcastingReactRequest.getReaction(), mapper.createObjectNode());
                            }
                            ((ObjectNode) broadcastingEpisodesTrailersIt.get("reactions").get(broadcastingReactRequest.getReaction())).set(broadcastingReactRequest.getUserName(), reaction);
                            break;
                        }
                    }
                }
                FileUtil.editFile(broadcasting, broadcastingFile);
            } else {
                File broadcastingCommentsFile = BroadcastingFolderLocator.getCommentsFile(broadcastingReactRequest.getId(), broadcastingReactRequest.getEpisodeTrailerId(), 0);
                ArrayNode broadcastingComments = (ArrayNode) mapper.readTree(broadcastingCommentsFile);
                Iterator<JsonNode> broadcastingCommentsIterator = broadcastingComments.iterator();
                while (broadcastingCommentsIterator.hasNext()) {
                    JsonNode broadcastingCommentsIt = broadcastingCommentsIterator.next();
                    if (broadcastingReactRequest.getCommentId().equals(broadcastingCommentsIt.get("id").textValue())) {
                        if (!broadcastingCommentsIt.get("reactions").has(broadcastingReactRequest.getReaction())) {
                            ((ObjectNode) broadcastingCommentsIt.get("reactions")).set(broadcastingReactRequest.getReaction(), mapper.createObjectNode());
                        }
                        ((ObjectNode) broadcastingCommentsIt.get("reactions").get(broadcastingReactRequest.getReaction())).set(broadcastingReactRequest.getUserName(), reaction);
                        FileUtil.editFile(broadcastingComments, broadcastingCommentsFile);
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BroadcastingReact.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
