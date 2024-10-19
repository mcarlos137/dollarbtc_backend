/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.hitbtc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ExchangesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class HitBTCGetLastTrade extends AbstractOperation<JsonNode> {

    private final String symbol;

    public HitBTCGetLastTrade(String symbol) {
        super(JsonNode.class);
        this.symbol = symbol;
    }

    @Override
    public void execute() {
        File lastTradeFile = null;
        for(File tradeFile : ExchangesFolderLocator.getExchangeSymbolTradesFolder("HitBTC", symbol).listFiles()){
            if(!tradeFile.isFile()){
                continue;
            }
            if(lastTradeFile == null || lastTradeFile.getName().compareTo(tradeFile.getName()) > 0){
                lastTradeFile = tradeFile;
            }
        }
        if(lastTradeFile == null){
            super.response = mapper.createObjectNode();
            return;
        }
        System.out.println("lastTradeFile: " + lastTradeFile.getName());
        try {
            super.response = mapper.readTree(lastTradeFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(HitBTCGetLastTrade.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
