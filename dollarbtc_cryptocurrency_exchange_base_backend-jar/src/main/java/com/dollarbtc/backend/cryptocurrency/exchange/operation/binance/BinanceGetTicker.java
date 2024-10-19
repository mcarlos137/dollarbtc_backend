/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.binance;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BinanceFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BinanceGetTicker extends AbstractOperation<JsonNode> {

    private final String symbol;

    public BinanceGetTicker(String symbol) {
        super(JsonNode.class);
        this.symbol = symbol;
    }

    @Override
    protected void execute() {
        File tickersFolder = BinanceFolderLocator.getTickersSymbolFolder(symbol);
        for (File tickerFile : tickersFolder.listFiles()) {
            if (!tickerFile.isFile()) {
                continue;
            }
            JsonNode ticker = null;
            try {
                ticker = mapper.readTree(tickerFile);
            } catch (IOException ex) {
                Logger.getLogger(BinanceGetTicker.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (ticker == null) {
                ticker = mapper.createObjectNode();
            }
            super.response = ticker;
            return;
        }
        super.response = mapper.createObjectNode();
    }

}
