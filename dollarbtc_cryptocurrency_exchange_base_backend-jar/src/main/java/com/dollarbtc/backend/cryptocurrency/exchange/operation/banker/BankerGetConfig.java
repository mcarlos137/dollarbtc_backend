/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BankerGetConfig extends AbstractOperation<JsonNode> {
    
    private final String userName;

    public BankerGetConfig(String userName) {
        super(JsonNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        File bankerConfigFile = BankersFolderLocator.getConfigFile(userName);
        if(!bankerConfigFile.isFile()){
            super.response = mapper.createObjectNode();
            return;
        }
        try {
            super.response = mapper.readTree(bankerConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(BankerGetConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }
        
}
