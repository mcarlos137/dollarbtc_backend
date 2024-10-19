/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.binance.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.EXCHANGE_ID;
import static com.dollarbtc.backend.cryptocurrency.exchange.binance.operation.BasicBinanceOperation.URI;
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasic;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeOrderBookOperation extends WebSocketBasic {

    private static final String OPERATION = "Orderbook";

    public SubscribeOrderBookOperation(String symbol, int id) {
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
                    if (!incomingMessage.get("params").has("ask") || !incomingMessage.get("params").has("bid")) {
                        return;
                    }
                    String method = incomingMessage.get("method").textValue();
                    if (!addOperation(method)) {
                        return;
                    }
                    String sequence = incomingMessage.get("params").get("sequence").textValue();
                    Iterator<String> operationTypes = incomingMessage.get("params").fieldNames();
                    while (operationTypes.hasNext()) {
                        String operationType = operationTypes.next();
                        if (!incomingMessage.get("params").get(operationType).isArray()) {
                            continue;
                        }
                        File operationTypeFolder = FileUtil.createFolderIfNoExist(operationFolder, operationType);
                        Iterator<JsonNode> operationTypeValues = incomingMessage.get("params").get(operationType).elements();
                        while (operationTypeValues.hasNext()) {
                            JsonNode operationTypeValue = operationTypeValues.next();
                            ExchangeUtil.createFile(operationTypeValue, new File[]{operationTypeFolder}, "orderBook");
                            addLogger(EXCHANGE_ID, symbolReal, operation, operationType, operationTypeValue);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SubscribeOrderBookOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
