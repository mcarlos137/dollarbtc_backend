/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.EXCHANGE_ID;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.URI;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasic;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import java.io.File;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeTickerOperation extends WebSocketBasic {

    private static final String OPERATION = "Ticker";
    
    public SubscribeTickerOperation(String symbol, int id) {
        super(URI, EXCHANGE_ID, symbol, OPERATION, id);
    }

    @Override
    public void prepareRequest() {
        if (symbolExchange == null || symbolExchange.equals("")) {
            return;
        }
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("method", "subscribe" + OPERATION);
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
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode incomingMessage = mapper.readTree(message);
                    if (!incomingMessage.has("params")) {
                        return;
                    }
                    String method = incomingMessage.get("method").textValue();
                    if (!addOperation(method)) {
                        return;
                    }
                    JsonNode params = incomingMessage.get("params");
                    ExchangeUtil.createFile(params, new File[]{operationFolder}, "ticker");
                    addLogger(EXCHANGE_ID, symbolReal, operation, null, params);
                } catch (IOException ex) {
                    Logger.getLogger(SubscribeTickerOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
