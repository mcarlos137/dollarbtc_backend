/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.BasicHitBTCOperation.EXCHANGE_ID;
import static com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.operation.BasicHitBTCOperation.URI;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasic;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;
import java.io.File;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeTradesOperation extends WebSocketBasic {

    private static final String OPERATION = "Trades";
    private static final NavigableMap<String, JsonNode> TRADES = new TreeMap<>();

    public SubscribeTradesOperation(String symbol, int id) {
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
                    if (!incomingMessage.get("params").has("data")) {
                        return;
                    }
                    String method = incomingMessage.get("method").textValue();
                    if (!addOperation(method)) {
                        return;
                    }
                    Iterator<JsonNode> data = incomingMessage.get("params").get("data").elements();
                    while (data.hasNext()) {
                        JsonNode dat = data.next();
                        String timestamp = dat.get("timestamp").textValue();
                        Map.Entry<String, JsonNode> lastEntry = TRADES.lastEntry();
                        if (lastEntry == null || DateUtil.getMinuteStartDate(lastEntry.getKey()).equals(DateUtil.getMinuteStartDate(timestamp))) {
                            TRADES.put(timestamp, dat);
                        } else {
                            ExchangeUtil.createFile(lastEntry.getValue(), new File[]{operationFolder}, "trade");
                            addLogger(EXCHANGE_ID, symbolReal, operation, null, lastEntry.getValue());
                            TRADES.clear();
                            TRADES.put(timestamp, dat);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SubscribeTradesOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
