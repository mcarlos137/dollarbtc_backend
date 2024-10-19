/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.subscriptionevent;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.SubscriptionsEventsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class SubscriptionEventGet extends AbstractOperation<JsonNode> {

    private final String id;
    
    public SubscriptionEventGet(String id) {
        super(JsonNode.class);
        this.id = id;
    }

    @Override
    protected void execute() {
        File subscriptionEventFile = SubscriptionsEventsFolderLocator.getFile(id);
        if(!subscriptionEventFile.isFile()){
            super.response = mapper.createObjectNode();
            return;
        }
        try {
            super.response = mapper.readTree(subscriptionEventFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(SubscriptionEventGet.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
