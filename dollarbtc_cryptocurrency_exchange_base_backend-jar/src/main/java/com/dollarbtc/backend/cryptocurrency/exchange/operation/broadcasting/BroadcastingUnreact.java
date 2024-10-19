/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingUnreactRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
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
public class BroadcastingUnreact extends AbstractOperation<String> {

    private final BroadcastingUnreactRequest broadcastingUnreactRequest;

    public BroadcastingUnreact(BroadcastingUnreactRequest broadcastingUnreactRequest) {
        super(String.class);
        this.broadcastingUnreactRequest = broadcastingUnreactRequest;
    }

    @Override
    public void execute() {
        try {
            if (broadcastingUnreactRequest.getCommentId() == null) {
                File broadcastingFile = BroadcastingFolderLocator.getFile(broadcastingUnreactRequest.getId());
                JsonNode broadcasting = mapper.readTree(broadcastingFile);
                boolean breakLoop = false;
                Iterator<JsonNode> broadcastingEpisodesTrailersIterator = broadcasting.get("episodes").iterator();
                while (broadcastingEpisodesTrailersIterator.hasNext()) {
                    JsonNode broadcastingEpisodesTrailersIt = broadcastingEpisodesTrailersIterator.next();
                    if (broadcastingEpisodesTrailersIt.get("id").textValue().equals(broadcastingUnreactRequest.getEpisodeTrailerId())) {
                        if (broadcastingEpisodesTrailersIt.has("reactions") && broadcastingEpisodesTrailersIt.get("reactions").has(broadcastingUnreactRequest.getReaction())) {
                            ((ObjectNode) broadcastingEpisodesTrailersIt.get("reactions").get(broadcastingUnreactRequest.getReaction())).remove(broadcastingUnreactRequest.getUserName());
                            breakLoop = true;
                            break;
                        }
                    }
                }
                if (!breakLoop) {
                    broadcastingEpisodesTrailersIterator = broadcasting.get("trailers").iterator();
                    while (broadcastingEpisodesTrailersIterator.hasNext()) {
                        JsonNode broadcastingEpisodesTrailersIt = broadcastingEpisodesTrailersIterator.next();
                        if (broadcastingEpisodesTrailersIt.get("id").textValue().equals(broadcastingUnreactRequest.getEpisodeTrailerId())) {
                            if (broadcastingEpisodesTrailersIt.has("reactions") && broadcastingEpisodesTrailersIt.get("reactions").has(broadcastingUnreactRequest.getReaction())) {
                                ((ObjectNode) broadcastingEpisodesTrailersIt.get("reactions").get(broadcastingUnreactRequest.getReaction())).remove(broadcastingUnreactRequest.getUserName());
                                break;
                            }
                        }
                    }
                }
                FileUtil.editFile(broadcasting, broadcastingFile);
            } else {
                File broadcastingCommentsFile = BroadcastingFolderLocator.getCommentsFile(broadcastingUnreactRequest.getId(), broadcastingUnreactRequest.getEpisodeTrailerId(), 0);
                ArrayNode broadcastingComments = (ArrayNode) mapper.readTree(broadcastingCommentsFile);
                Iterator<JsonNode> broadcastingCommentsIterator = broadcastingComments.iterator();
                while (broadcastingCommentsIterator.hasNext()) {
                    JsonNode broadcastingCommentsIt = broadcastingCommentsIterator.next();
                    if (broadcastingUnreactRequest.getCommentId().equals(broadcastingCommentsIt.get("id").textValue())) {
                        if (broadcastingCommentsIt.has("reactions") && broadcastingCommentsIt.get("reactions").has(broadcastingUnreactRequest.getReaction())) {
                            ((ObjectNode) broadcastingCommentsIt.get("reactions").get(broadcastingUnreactRequest.getReaction())).remove(broadcastingUnreactRequest.getUserName());
                            FileUtil.editFile(broadcastingComments, broadcastingCommentsFile);
                            break;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BroadcastingUnreact.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
