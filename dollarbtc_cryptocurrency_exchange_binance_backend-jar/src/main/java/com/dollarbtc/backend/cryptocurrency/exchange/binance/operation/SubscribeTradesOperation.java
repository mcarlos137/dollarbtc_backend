/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.operation;

import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.EXCHANGE_ID;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.URI;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasic;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;
import java.io.File;
import java.util.NavigableMap;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeTradesOperation extends WebSocketBasic {

    private static final String OPERATION = "Trades";
    private static final NavigableMap<String, JsonNode> TRADES = new TreeMap<>();
    
    public SubscribeTradesOperation(String symbol, int id) {
        super(URI + "/" + symbol.toLowerCase() + "@trade", EXCHANGE_ID, symbol, OPERATION, id);
    }

    @Override
    public void prepareRequest() {
        if (symbolExchange == null || symbolExchange.equals("")) {
            return;
        }
        String streamName = symbolExchange.toLowerCase() + "@trade";
        System.out.println("streamName: " + streamName);
    }

    @Override
    protected void addListener() {
        clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode incomingMessage = mapper.readTree(message);
                    if (!incomingMessage.has("e") || !incomingMessage.get("e").textValue().equals("trade")) {
                        return;
                    }
                    if (!addOperation("")) {
                        return;
                    }
                    String id = incomingMessage.get("t").textValue();
                    String price = incomingMessage.get("p").textValue();
                    String quantity = incomingMessage.get("q").textValue();
                    String side = "none";
                    String timestamp = DateUtil.getDate(incomingMessage.get("T").longValue());
                    JsonNode dat = mapper.createObjectNode();
                    ((ObjectNode) dat).put("id", id);
                    ((ObjectNode) dat).put("price", price);
                    ((ObjectNode) dat).put("quantity", quantity);
                    ((ObjectNode) dat).put("side", side);
                    ((ObjectNode) dat).put("timestamp", timestamp);
                    Entry<String, JsonNode> lastEntry = TRADES.lastEntry();
                    if(lastEntry == null || DateUtil.getMinuteStartDate(lastEntry.getKey()).equals(DateUtil.getMinuteStartDate(timestamp))){
                        TRADES.put(timestamp, dat);
                    } else {
                        ExchangeUtil.createFile(lastEntry.getValue(), new File[]{operationFolder}, "trade");
                        addLogger(EXCHANGE_ID, symbolReal, operation, null, lastEntry.getValue());
                        TRADES.clear();
                        TRADES.put(timestamp, dat);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SubscribeTradesOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
