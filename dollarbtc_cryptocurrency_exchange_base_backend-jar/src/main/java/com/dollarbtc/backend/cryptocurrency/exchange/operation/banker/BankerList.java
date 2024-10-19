/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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
public class BankerList extends AbstractOperation<ArrayNode> {
    
    private final String userName;

    public BankerList(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        ArrayNode bankers = mapper.createArrayNode();
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        boolean allowed = false;
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if(userConfig.has("verification") && (userConfig.get("verification").has("C") || userConfig.get("verification").has("E"))){
                allowed = true;
            }
        } catch (IOException ex) {
            Logger.getLogger(BankerList.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!allowed){
            super.response = bankers;
            return;
        }
        for(File bankerFolder : BankersFolderLocator.getFolder().listFiles()){
            if(!bankerFolder.isDirectory()){
                continue;
            }
            File bankerConfigFile = new File(bankerFolder, "config.json");
            try {
                bankers.add(mapper.readTree(bankerConfigFile));
            } catch (IOException ex) {
                Logger.getLogger(BankerList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = bankers;
    }
        
}
