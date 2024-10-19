/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class LiveStreamingOverviewId extends AbstractOperation<String> {

    private final String userName;
    
    public LiveStreamingOverviewId(String userName) {
        super(String.class);
        this.userName = userName;
    }

    @Override
    public void execute() {
        File liveStreamingsOverviewFile = LiveStreamingsFolderLocator.getOverviewFile();
        if (!liveStreamingsOverviewFile.isFile()) {
            super.response = "";
            return;
        }
        try {
            JsonNode liveStreamingsOverview = mapper.readTree(liveStreamingsOverviewFile);
            super.response = liveStreamingsOverview.get("id").textValue();
            return;
        } catch (IOException ex) {
            Logger.getLogger(LiveStreamingOverviewId.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "";
    }

}
