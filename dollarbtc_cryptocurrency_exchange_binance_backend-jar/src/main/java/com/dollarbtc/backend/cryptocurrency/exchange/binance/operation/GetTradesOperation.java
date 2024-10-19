/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.EXCHANGE_ID;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.URI;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasic;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;

/**
 *
 * @author CarlosDaniel
 */
public class GetTradesOperation extends WebSocketBasic {

    private static final String OPERATION = "Trades";
    private final int limit;
    private final String sort, by;

    public GetTradesOperation(String symbol, int limit, String sort, String by, int id) {
        super(URI, EXCHANGE_ID, symbol, OPERATION, id);
        this.limit = limit;
        this.sort = sort;
        this.by = by;
    }

    @Override
    public void prepareRequest(){
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("method", "get" + OPERATION);
        JsonNode jsonNodeParams = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNodeParams).put("symbol", symbolExchange);
        ((ObjectNode) jsonNodeParams).put("limit", limit);
        ((ObjectNode) jsonNodeParams).put("sort", sort);
        ((ObjectNode) jsonNodeParams).put("by", by);
        ((ObjectNode) jsonNode).put("params", jsonNodeParams);
        ((ObjectNode) jsonNode).put("id", id);
        System.out.println("jsonNode: " + jsonNode);
        clientEndPoint.sendMessage(jsonNode.toString());
    }

    @Override
    protected void addListener() {
        clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                response = message;
            }
        });
    }

}
