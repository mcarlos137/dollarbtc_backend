/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class LiveStreamingGet extends AbstractOperation<JsonNode> {

    private final String id;

    public LiveStreamingGet(String id) {
        super(JsonNode.class);
        this.id = id;
    }

    @Override
    protected void execute() {
        try {
            super.response = mapper.readTree(LiveStreamingsFolderLocator.getFile(id));
            return;
        } catch (IOException ex) {
            Logger.getLogger(LiveStreamingGet.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
