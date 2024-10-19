/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.EXCHANGE_ID;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.URI;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasicUser;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;

/**
 *
 * @author CarlosDaniel
 */
public class UserNewOrderOperation extends WebSocketBasicUser {
    
    private static final String OPERATION = "Order";
    private final String side, price, quantity, type;    
        
    public UserNewOrderOperation(String modelName, String symbol, String clientOrderId, String side, String type, String price, String quantity, int id) {
        super(URI, EXCHANGE_ID, modelName, symbol, clientOrderId, id);
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        prepareRequest();
    }

    @Override
    public void prepareRequest() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("method", "new" + OPERATION);
        JsonNode jsonNodeParams = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNodeParams).put("clientOrderId", clientOrderId);
        ((ObjectNode) jsonNodeParams).put("symbol", super.symbolReal);
        ((ObjectNode) jsonNodeParams).put("side", side);
        ((ObjectNode) jsonNodeParams).put("type", type);
        ((ObjectNode) jsonNodeParams).put("price", price);
        ((ObjectNode) jsonNodeParams).put("quantity", quantity);
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
                System.out.println("response: " + message);
                response = message;
            }
        });
    }
    
}
