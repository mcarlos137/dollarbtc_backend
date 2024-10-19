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
public class PriceGetChatP2PMessage implements Runnable {

    private final Session session;
    private final JsonNode jsonNode;

    public PriceGetChatP2PMessage(Session session, JsonNode jsonNode) {
        this.session = session;
        this.jsonNode = jsonNode;
    }

    @Override
    public void run() {
        try {
            Map<String, String> params = new HashMap<>();
            for (File pricesChatP2PFolder : PricesFolderLocator.getChatP2PFolder().listFiles()) {
                if (!pricesChatP2PFolder.isDirectory()) {
                    continue;
                }
                String pair = pricesChatP2PFolder.getName();
                File pricesFile = null;
                for (File pricesChatP2PFile : pricesChatP2PFolder.listFiles()) {
                    if (pricesChatP2PFile.isFile()) {
                        pricesFile = pricesChatP2PFile;
                        break;
                    }
                }
                if (pricesFile == null) {
                    session.getRemote().sendPing(null);
                } else {
                    if (jsonNode.has(pair) && jsonNode.get(pair).textValue().equals(pricesFile.getName())) {
                        session.getRemote().sendPing(null);
                    } else {
                        ((ObjectNode) jsonNode).put(pair, pricesFile.getName());
                        try {
                            session.getRemote().sendString(ServiceUtil.createWSResponseWithData(new ObjectMapper().readTree(pricesFile), "prices", "params", params).toString());
                        } catch (IOException ex) {
                            Logger.getLogger(PriceGetChatP2PMessage.class.getName()).log(Level.SEVERE, null, ex);
                            session.getRemote().sendPing(null);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PriceGetChatP2PMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
