/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class LiveStreamingGetComments extends AbstractOperation<ArrayNode> {

    private final String id, publicationId;

    public LiveStreamingGetComments(String id, String publicationId) {
        super(ArrayNode.class);
        this.id = id;
        this.publicationId = publicationId;
    }

    @Override
    protected void execute() {
        File liveStreamingCommentsFile = LiveStreamingsFolderLocator.getCommentsFile(id, publicationId, 0);
        ArrayNode liveStreamingComments = mapper.createArrayNode();
        if (liveStreamingCommentsFile.isFile()) {
            try {
                liveStreamingComments = (ArrayNode) mapper.readTree(liveStreamingCommentsFile);
            } catch (IOException ex) {
                Logger.getLogger(LiveStreamingGetComments.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = liveStreamingComments;
    }

}
