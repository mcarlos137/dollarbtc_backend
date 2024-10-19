/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.client;

import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketClient;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class ExchangeGetCurrenciesServiceWSClient extends WebSocketClient {

    private final static String URI = "ws://localhost:8081/exchange";

    public ExchangeGetCurrenciesServiceWSClient() {
        super(URI);
        prepareRequest();
    }

    protected void prepareRequest() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.createObjectNode();
        ((ObjectNode) jsonNode).put("method", "getCurrencies");
        JsonNode params = mapper.createObjectNode();
        ((ObjectNode) jsonNode).put("params", params);
        clientEndPoint.sendMessage(jsonNode.toString());
    }

    public void start() {
        keepAlive();
    }

    private void keepAlive() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ExchangeGetCurrenciesServiceWSClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void addListener() {
        clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println("receive message: " + message);
            }
        });
    }

}
