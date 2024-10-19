/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.BasicHitBTCOperation.EXCHANGE_ID;
import static com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.BasicHitBTCOperation.URI;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasicUser;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;

/**
 *
 * @author CarlosDaniel
 */
public class UserCancelReplaceOrderOperation extends WebSocketBasicUser {
    
    private static final String OPERATION = "Order";
    private final String requestClientId, price, quantity;
        
    public UserCancelReplaceOrderOperation(String modelName, String clientOrderId, String requestClientId, String price, String quantity, int id) {
        super(URI, EXCHANGE_ID, modelName, null, clientOrderId, id);
        this.requestClientId = requestClientId;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public void prepareRequest() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("method", "cancelReplace" + OPERATION);
        JsonNode jsonNodeParams = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNodeParams).put("clientOrderId", clientOrderId);
        ((ObjectNode) jsonNodeParams).put("requestClientId", requestClientId);
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
