/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.binance;

import com.dollarbtc.backend.cryptocurrency.exchange.util.AbstractRestClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author carlosmolina
 */
public class BinanceGetPrices extends AbstractRestClient<JsonNode> {

    private static Client client;
    private final String currency, operation;

    public BinanceGetPrices(String currency, String operation) {
        super(JsonNode.class);
        this.currency = currency;
        this.operation = operation;
    }

    @Override
    public Client getClient() {
        if (client == null) {
            client = ClientBuilder.newClient();
        }
        return client;
    }

    @Override
    public String getMediaType() {
        return MediaType.APPLICATION_JSON;
    }

    public JsonNode getResponse() {
        ObjectNode requestJsonNode = new ObjectMapper().createObjectNode();
        requestJsonNode.put("page", 1);
        requestJsonNode.put("rows", 20);
        requestJsonNode.put("asset", "BTC");
        requestJsonNode.put("tradeType", operation.toUpperCase());
        requestJsonNode.put("fiat", currency);
        //requestJsonNode.put("payTypes", []);
        return super.postJsonNode(requestJsonNode, "https://p2p.binance.com/bapi/c2c/v2/friendly/c2c/adv/search", RequestRestType.ASYNC, null, null, 0);
    }
    
}
