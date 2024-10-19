/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetCryptoPrice extends AbstractOperation<JsonNode> {

    private final String cryptoCurrency, fiatCurrency;

    public MCUserGetCryptoPrice(String cryptoCurrency, String fiatCurrency) {
        super(JsonNode.class);
        this.cryptoCurrency = cryptoCurrency;
        this.fiatCurrency = fiatCurrency;
    }

    @Override
    public void execute() {
        JsonNode cryptoPrice = mapper.createObjectNode();
        Double bidPrice = null;
        Double askPrice = null;
        //SELL
        JsonNode fastChangeFactor = new MCUserGetFastChangeFactor(cryptoCurrency, fiatCurrency).getResponse();
        if (fastChangeFactor.has("factor")) {
            bidPrice = fastChangeFactor.get("factor").doubleValue();
        }
        //BUY
        fastChangeFactor = new MCUserGetFastChangeFactor(fiatCurrency, cryptoCurrency).getResponse();
        if (fastChangeFactor.has("factor")) {
            askPrice = 1 / fastChangeFactor.get("factor").doubleValue();
        }
        if (bidPrice != null) {
            ((ObjectNode) cryptoPrice).put("bid", bidPrice);
        }
        if (askPrice != null) {
            ((ObjectNode) cryptoPrice).put("ask", askPrice);
        }
        if (bidPrice != null && askPrice != null) {
            ((ObjectNode) cryptoPrice).put("average", (bidPrice + askPrice) / 2);
        }
        super.response = cryptoPrice;
    }

}
