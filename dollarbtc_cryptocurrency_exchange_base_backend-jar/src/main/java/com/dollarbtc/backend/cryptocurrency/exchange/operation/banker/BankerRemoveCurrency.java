/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BankerRemoveCurrency extends AbstractOperation<String> {
    
    private final String userName, currency;

    public BankerRemoveCurrency(String userName, String currency) {
        super(String.class);
        this.userName = userName;
        this.currency = currency;
    }

    @Override
    protected void execute() {
        File bankerConfigFile = BankersFolderLocator.getConfigFile(userName);
        try {
            JsonNode bankerConfig = mapper.readTree(bankerConfigFile);
            if(!bankerConfig.has("currencies")){
                ((ObjectNode) bankerConfig).set("currencies", mapper.createObjectNode());
            }
            ((ObjectNode) bankerConfig.get("currencies")).put(currency, false);
            FileUtil.editFile(bankerConfig, bankerConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(BankerRemoveCurrency.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }
        
}
