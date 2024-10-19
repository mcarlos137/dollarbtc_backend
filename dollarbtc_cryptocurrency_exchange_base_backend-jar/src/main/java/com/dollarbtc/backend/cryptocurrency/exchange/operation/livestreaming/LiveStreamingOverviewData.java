/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class LiveStreamingOverviewData extends AbstractOperation<ArrayNode> {

    private final String userName;
    
    public LiveStreamingOverviewData(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    public void execute() {
        File liveStreamingsOverviewFile = LiveStreamingsFolderLocator.getOverviewFile();
        if (!liveStreamingsOverviewFile.isFile()) {
            super.response = mapper.createArrayNode();
            return;
        }
        try {
            JsonNode liveStreamingsOverview = mapper.readTree(liveStreamingsOverviewFile);
            super.response = (ArrayNode) liveStreamingsOverview.get("data");
            return;
        } catch (IOException ex) {
            Logger.getLogger(LiveStreamingOverviewData.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createArrayNode();
    }

}
