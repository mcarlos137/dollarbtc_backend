/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.message;

import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CandlesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class MoneyMarketGetCandlesMessage implements Runnable {

    private final Session session;
    private final JsonNode jsonNode;
    private JsonNode candle;

    public MoneyMarketGetCandlesMessage(Session session, JsonNode jsonNode) {
        this.session = session;
        this.jsonNode = jsonNode;
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        String pair = jsonNode.get("pair").textValue();
        String period = jsonNode.get("period").textValue();
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        params.put("period", period);
        if (candle == null) {
            ArrayNode oldCandles = mapper.createArrayNode();
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(CandlesFolderLocator.getChatP2POldFolder(pair, period).getPath()));) {
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
                    File candlesChatP2POldFile = it.toFile();
                    try {
                        JsonNode candlesChatP2POld = mapper.readTree(candlesChatP2POldFile);
                        ((ObjectNode) candlesChatP2POld).put("time", DateUtil.parseDate(DateUtil.getDate(candlesChatP2POldFile.getName().replace(".json", ""))).getTime());
                        oldCandles.add(candlesChatP2POld);
                        candle = candlesChatP2POld;
                    } catch (IOException ex) {
                        Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (oldCandles.size() >= 20) {
                        break;
                    }
                }
            } catch (IOException ex) {
            }
            if (oldCandles.size() > 0) {
                try {
                    session.getRemote().sendString(ServiceUtil.createWSResponseWithData(oldCandles, "oldCandles", "params", params).toString());
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        File newCandleFile = null;
        for (File candlesChatP2PFile : CandlesFolderLocator.getChatP2PFolder(pair, period).listFiles()) {
            if (!candlesChatP2PFile.isFile()) {
                continue;
            }
            newCandleFile = candlesChatP2PFile;
        }
        if (newCandleFile == null) {
            try {
                session.getRemote().sendPing(null);
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        JsonNode newCandle = null;
        try {
            newCandle = mapper.readTree(newCandleFile);
        } catch (IOException ex) {
            Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (newCandle == null) {
            try {
                session.getRemote().sendPing(null);
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        if (candle == null || !candle.equals(newCandle)) {
            ArrayNode newCandles = mapper.createArrayNode();
            for (File candlesChatP2PFile : CandlesFolderLocator.getChatP2PFolder(pair, period).listFiles()) {
                if (!candlesChatP2PFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode newCandlee = mapper.readTree(candlesChatP2PFile);
                    ((ObjectNode) newCandlee).put("time", DateUtil.parseDate(DateUtil.getDate(candlesChatP2PFile.getName().replace(".json", ""))).getTime());
                    newCandles.add(newCandlee);
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            candle = newCandle;
            if (newCandles.size() == 0) {
                try {
                    session.getRemote().sendPing(null);
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    session.getRemote().sendString(ServiceUtil.createWSResponseWithData(newCandles, "newCandles", "params", params).toString());
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            try {
                session.getRemote().sendPing(null);
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetCandlesMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
