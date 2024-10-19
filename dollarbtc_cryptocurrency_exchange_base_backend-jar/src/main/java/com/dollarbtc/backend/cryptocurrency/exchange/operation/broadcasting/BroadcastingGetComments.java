/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BroadcastingGetComments extends AbstractOperation<ArrayNode> {

    private final String broadcastingId, episodeTrailerId;

    public BroadcastingGetComments(String broadcastingId, String episodeTrailerId) {
        super(ArrayNode.class);
        this.broadcastingId = broadcastingId;
        this.episodeTrailerId = episodeTrailerId;
    }

    @Override
    protected void execute() {
        File broadcastingEpisodeTrailerCommentsFile = BroadcastingFolderLocator.getCommentsFile(broadcastingId, episodeTrailerId, 0);
        ArrayNode broadcastingEpisodeTrailerComments = mapper.createArrayNode();
        if (broadcastingEpisodeTrailerCommentsFile.isFile()) {
            try {
                broadcastingEpisodeTrailerComments = (ArrayNode) mapper.readTree(broadcastingEpisodeTrailerCommentsFile);
            } catch (IOException ex) {
                Logger.getLogger(BroadcastingGetComments.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = broadcastingEpisodeTrailerComments;
    }

}
