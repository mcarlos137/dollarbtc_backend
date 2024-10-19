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
import com.dollarbtc.backend.cryptocurrency.exchange.websocket.WebSocketBasic;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;
import java.io.File;

/**
 *
 * @author CarlosDaniel
 */
public class SubscribeCandlesOperation extends WebSocketBasic {

    private static final String OPERATION = "Candles";
    private final String period;
    protected File periodFolder;

    public SubscribeCandlesOperation(String symbol, String period, int id) {
        super(URI, EXCHANGE_ID, symbol, OPERATION, id);
        this.period = period;
        mainFolderExists = FileUtil.folderExists(operationFolder, period);
        periodFolder = FileUtil.createFolderIfNoExist(operationFolder, period);
        WebsocketClientEndpoint.mainFolder = periodFolder;
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
        ((ObjectNode) jsonNodeParams).put("period", period);
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
                        ExchangeUtil.createFile(dat, new File[]{periodFolder}, "candle");
                        addLogger(EXCHANGE_ID, symbolReal, operation, null, dat);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SubscribeCandlesOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
