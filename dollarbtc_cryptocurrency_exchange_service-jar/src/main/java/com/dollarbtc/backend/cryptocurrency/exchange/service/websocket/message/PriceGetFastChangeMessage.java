/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.message;

import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.PricesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author carlosmolina
 */
public class PriceGetFastChangeMessage implements Runnable {

    private final Session session;
    private final JsonNode jsonNode;

    public PriceGetFastChangeMessage(Session session, JsonNode jsonNode) {
        this.session = session;
        this.jsonNode = jsonNode;
    }

    @Override
    public void run() {
        try {
            String currency = jsonNode.get("currency").textValue();
            Map<String, String> params = new HashMap<>();
            params.put("currency", currency);
            File pricesFastChangeFolder = PricesFolderLocator.getFastChangeFolder(currency);
            File pricesFile = null;
            for (File pricesFastChangeFile : pricesFastChangeFolder.listFiles()) {
                if (pricesFastChangeFile.isFile()) {
                    pricesFile = pricesFastChangeFile;
                    break;
                }
            }
            if (pricesFile == null) {
                session.getRemote().sendPing(null);
            } else {
                if (jsonNode.has("lastFile") && jsonNode.get("lastFile").textValue().equals(pricesFile.getName())) {
                    session.getRemote().sendPing(null);
                } else {
                    ((ObjectNode) jsonNode).put("lastFile", pricesFile.getName());
                    try {
                        session.getRemote().sendString(ServiceUtil.createWSResponseWithData(new ObjectMapper().readTree(pricesFile), "prices", "params", params).toString());
                    } catch (IOException ex) {
                        Logger.getLogger(PriceGetFastChangeMessage.class.getName()).log(Level.SEVERE, null, ex);
                        session.getRemote().sendPing(null);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PriceGetFastChangeMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
