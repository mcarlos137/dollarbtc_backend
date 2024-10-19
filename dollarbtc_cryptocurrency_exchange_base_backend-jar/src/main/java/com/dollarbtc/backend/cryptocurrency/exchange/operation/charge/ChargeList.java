/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.charge;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
public class ChargeList extends AbstractOperation<JsonNode> {

    public ChargeList() {
        super(JsonNode.class);
    }
    
    @Override
    protected void execute() {
        JsonNode list = mapper.createObjectNode();
        for (File otcCurrencyFolder : OTCFolderLocator.getFolder(null).listFiles()) {
            if (!otcCurrencyFolder.isDirectory() || otcCurrencyFolder.getName().equals("Operations")) {
                continue;
            }
            try {
                File otcCurrencyChargesFile = new File(otcCurrencyFolder, "charges.json");
                if(!otcCurrencyChargesFile.isFile()){
                    Logger.getLogger(ChargeList.class.getName()).log(Level.INFO, "CHARGES FILE FOR {0} DOES NOT EXIST", otcCurrencyFolder.getName());
                    continue;
                }
                ((ObjectNode) list).set(otcCurrencyFolder.getName(), mapper.readTree(new File(otcCurrencyFolder, "charges.json")));
            } catch (IOException ex) {
                Logger.getLogger(ChargeList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = list;
    }
    
}
