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
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author carlosmolina
 */
public class MoneyMarketGetPricesMessage implements Runnable {

    private final Session session;
    private final JsonNode jsonNode;
    private File pricesFile;

    public MoneyMarketGetPricesMessage(Session session, JsonNode jsonNode) {
        this.session = session;
        this.jsonNode = jsonNode;
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        String pair = jsonNode.get("pair").textValue();
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        if (pricesFile == null) {
            ArrayNode oldPrices = mapper.createArrayNode();
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(PricesFolderLocator.getChatP2POldFolder(pair).getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(path -> !Files.isDirectory(path))
                        .sorted((o1, o2) -> {
                            String da1 = o1.toFile().getName();
                            String da2 = o2.toFile().getName();
                            return da2.compareTo(da1);
                        })
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    File pricesChatP2POldFile = it.toFile();
                    try {
                        oldPrices.add(mapper.readTree(pricesChatP2POldFile));
                        pricesFile = pricesChatP2POldFile;
                    } catch (IOException ex) {
                        Logger.getLogger(MoneyMarketGetPricesMessage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (oldPrices.size() >= 300) {
                        break;
                    }
                }
            } catch (IOException ex) {
            }
            if (oldPrices.size() != 0) {
                try {
                    session.getRemote().sendString(ServiceUtil.createWSResponseWithData(oldPrices, "oldPrices", "params", params).toString());
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetPricesMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        File newPricesFile = null;
        for (File pricesChatP2PFile : PricesFolderLocator.getChatP2PFolder(pair).listFiles()) {
            if (!pricesChatP2PFile.isFile()) {
                continue;
            }
            newPricesFile = pricesChatP2PFile;
        }
        if (newPricesFile == null) {
            try {
                session.getRemote().sendPing(null);
                return;
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetPricesMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (pricesFile == null || !pricesFile.equals(newPricesFile)) {
            ArrayNode newPrices = mapper.createArrayNode();
            for (File pricesChatP2PFile : PricesFolderLocator.getChatP2PFolder(pair).listFiles()) {
                if (!pricesChatP2PFile.isFile()) {
                    continue;
                }
                try {
                    newPrices.add(mapper.readTree(pricesChatP2PFile));
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetPricesMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            pricesFile = newPricesFile;
            if (newPrices.size() == 0) {
                try {
                    session.getRemote().sendPing(null);
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetPricesMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    session.getRemote().sendString(ServiceUtil.createWSResponseWithData(newPrices, "newPrices", "params", params).toString());
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetPricesMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            try {
                session.getRemote().sendPing(null);
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetPricesMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
