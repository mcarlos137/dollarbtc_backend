/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
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
public class BroadcastingOverviewData extends AbstractOperation<ArrayNode> {

    private final String userName;
    
    public BroadcastingOverviewData(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    public void execute() {
        File broadcastingsOverviewFile = BroadcastingFolderLocator.getOverviewFile();
        if (!broadcastingsOverviewFile.isFile()) {
            super.response = mapper.createArrayNode();
            return;
        }
        try {
            JsonNode broadcastingsOverview = mapper.readTree(broadcastingsOverviewFile);
            super.response = (ArrayNode) broadcastingsOverview.get("data");
            return;
        } catch (IOException ex) {
            Logger.getLogger(BroadcastingOverviewData.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createArrayNode();
    }

}
