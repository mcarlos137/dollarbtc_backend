/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BankerGetOperation extends AbstractOperation<JsonNode> {
    
    private final String userName, id;

    public BankerGetOperation(String userName, String id) {
        super(JsonNode.class);
        this.userName = userName;
        this.id = id;
    }    
    
    @Override
    protected void execute() {
        try {
            super.response = mapper.readTree(BankersFolderLocator.getOperationIdFolder(userName, id));
            return;
        } catch (IOException ex) {
            Logger.getLogger(BankerGetOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }
    
}
