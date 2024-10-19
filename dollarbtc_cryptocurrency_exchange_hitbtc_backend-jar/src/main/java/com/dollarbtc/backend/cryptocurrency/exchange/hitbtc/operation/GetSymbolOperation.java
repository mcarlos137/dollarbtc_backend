/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.BasicHitBTCOperation.EXCHANGE_ID;
import static com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.BasicHitBTCOperation.URI;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasic;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;

/**
 *
 * @author CarlosDaniel
 */
public class GetSymbolOperation extends WebSocketBasic {

    private static final String OPERATION = "Symbol";
    
    public GetSymbolOperation(String symbol, int id) {
        super(URI, EXCHANGE_ID, symbol, OPERATION, id);
    }
    
    @Override
    public void prepareRequest(){
        if (symbolExchange == null || symbolExchange.equals("")) {
            return;
        }
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("method", "get" + OPERATION);
        JsonNode jsonNodeParams = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNodeParams).put("symbol", symbolExchange);
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
