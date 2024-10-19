/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.SubscriptionsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class SubscriptionGet extends AbstractOperation<JsonNode> {

    private final String id;
    
    public SubscriptionGet(String id) {
        super(JsonNode.class);
        this.id = id;
    }

    @Override
    protected void execute() {
        File subscriptionFile = SubscriptionsFolderLocator.getFile(id);
        if(!subscriptionFile.isFile()){
            super.response = mapper.createObjectNode();
            return;
        }
        try {
            super.response = mapper.readTree(subscriptionFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(SubscriptionGet.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
