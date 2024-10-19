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
public class MCUserGetBitcoinPrice extends AbstractOperation<JsonNode> {

    private final String currency;

    public MCUserGetBitcoinPrice(String currency) {
        super(JsonNode.class);
        this.currency = currency;
    }

    @Override
    public void execute() {
        JsonNode bitcoinPrice = mapper.createObjectNode();
        Double bidPrice = null;
        Double askPrice = null;
        //SELL
        JsonNode fastChangeFactor = new MCUserGetFastChangeFactor("BTC", currency).getResponse();
        if (fastChangeFactor.has("factor")) {
            bidPrice = fastChangeFactor.get("factor").doubleValue();
        }
        //BUY
        fastChangeFactor = new MCUserGetFastChangeFactor(currency, "BTC").getResponse();
        if (fastChangeFactor.has("factor")) {
            askPrice = 1 / fastChangeFactor.get("factor").doubleValue();
        }
        if (bidPrice != null) {
            ((ObjectNode) bitcoinPrice).put("bid", bidPrice);
        }
        if (askPrice != null) {
            ((ObjectNode) bitcoinPrice).put("ask", askPrice);
        }
        if (bidPrice != null && askPrice != null) {
            ((ObjectNode) bitcoinPrice).put("average", (bidPrice + askPrice) / 2);
        }
        super.response = bitcoinPrice;
    }

}
