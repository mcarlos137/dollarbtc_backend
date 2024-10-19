/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCRetailNewGetRetail extends AbstractOperation<JsonNode> {
    
    private final String id;

    public MCRetailNewGetRetail(String id) {
        super(JsonNode.class);
        this.id = id;
    }
        
    @Override
    public void execute() {
        JsonNode retailMarker = mapper.createObjectNode();
        File moneyclickRetailConfigFile = MoneyclickFolderLocator.getRetailConfigFile(id);
        if (moneyclickRetailConfigFile.isFile()) {
            try {
                JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
                Iterator<JsonNode> moneyclickRetailConfigOperationsIterator = moneyclickRetailConfig.get("operations").iterator();
                while (moneyclickRetailConfigOperationsIterator.hasNext()) {
                    JsonNode moneyclickRetailConfigOperationsIt = moneyclickRetailConfigOperationsIterator.next();
                    BaseOperation.addFieldsToRetailOperation(moneyclickRetailConfigOperationsIt, mapper);
                }
                BaseOperation.addFieldsToRetail(moneyclickRetailConfig, mapper);
                super.response = moneyclickRetailConfig; 
                return;
            } catch (IOException ex) {
                Logger.getLogger(MCRetailNewGetRetail.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = retailMarker; 
    }
    
}
