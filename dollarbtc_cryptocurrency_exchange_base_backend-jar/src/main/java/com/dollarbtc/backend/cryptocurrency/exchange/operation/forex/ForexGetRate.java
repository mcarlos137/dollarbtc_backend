/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.forex;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ForexFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ForexGetRate extends AbstractOperation<JsonNode> {
    
    private final String symbol;

    public ForexGetRate(String symbol) {
        super(JsonNode.class);
        this.symbol = symbol;
    }    
    
    @Override
    protected void execute() {
        File rateFolder = ForexFolderLocator.getRatesSymbolFolder(symbol);
        if(!rateFolder.isDirectory()){
            super.response = mapper.createObjectNode();
            return;
        }
        for (File rateFile : rateFolder.listFiles()) {
            if (!rateFile.isFile()) {
                continue;
            }
            try {
                super.response = mapper.readTree(rateFile);
                return;
            } catch (IOException ex) {
                Logger.getLogger(ForexGetRate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = mapper.createObjectNode();
    }
    
}
