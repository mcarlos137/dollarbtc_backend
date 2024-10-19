/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.coinbase;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CoinbaseFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CoinbaseGetUpdatedPrice extends AbstractOperation<JsonNode> {

    private final String symbol;

    public CoinbaseGetUpdatedPrice(String symbol) {
        super(JsonNode.class);
        this.symbol = symbol;
    }

    @Override
    protected void execute() {
        File pricesFolder = CoinbaseFolderLocator.getPricesSymbolFolder(symbol);
        for (File priceFile : pricesFolder.listFiles()) {
            if (!priceFile.isFile()) {
                continue;
            }
            JsonNode price = null;
            try {
                price = mapper.readTree(priceFile);
            } catch (IOException ex) {
                Logger.getLogger(CoinbaseGetUpdatedPrice.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (price == null) {
                price = mapper.createObjectNode();
            }
            super.response = price;
            return;
        }
        super.response = mapper.createObjectNode();
    }

}
