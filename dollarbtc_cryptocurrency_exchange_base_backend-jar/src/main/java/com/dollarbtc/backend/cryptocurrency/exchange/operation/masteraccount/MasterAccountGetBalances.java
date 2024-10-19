/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountGetBalances extends AbstractOperation<JsonNode> {

    public MasterAccountGetBalances() {
        super(JsonNode.class);
    }
        
    @Override
    public void execute() {
        JsonNode balances = mapper.createObjectNode();
        //REVISAR
        File masterAccountFolder = MasterAccountFolderLocator.getFolder(null);
        for (File masterAccountSpecificFolder : masterAccountFolder.listFiles()) {
            if (!masterAccountSpecificFolder.isDirectory()) {
                continue;
            }
            String masterAccountName = masterAccountSpecificFolder.getName();
            ((ObjectNode) balances).set(masterAccountName, new MasterAccountGetBalance(masterAccountName).getResponse());
        }
        this.response = balances;
    }
    
}
