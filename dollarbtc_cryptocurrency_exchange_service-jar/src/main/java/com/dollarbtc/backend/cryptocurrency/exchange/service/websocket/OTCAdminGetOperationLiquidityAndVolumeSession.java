/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class OTCAdminGetOperationLiquidityAndVolumeSession {

    private final static Map<Session, JsonNode> SESSIONS = new HashMap<>();
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void addSession(Session session, JsonNode jsonNode) {
        SESSIONS.put(session, jsonNode);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Message(SESSIONS), 0, 20, TimeUnit.SECONDS);
    }

    public static void removeSession(Session session) {
        SESSIONS.remove(session);
    }

    public static class Message implements Runnable {

        private static Map<Session, JsonNode> sessions;

        public Message(Map<Session, JsonNode> sessions) {
            Message.sessions = sessions;
        }

        @Override
        public void run() {
            try {
                for (Session session : sessions.keySet()) {
                    JsonNode jsonNode = sessions.get(session);
                    /*boolean moneyclick = false;
                    if(jsonNode.has("moneyclick")){
                        moneyclick = jsonNode.get("moneyclick").booleanValue();
                    }*/
                    String websocketKey = jsonNode.get("websocketKey").textValue();
                    String currency = jsonNode.get("currency").textValue();
                    String period = jsonNode.get("period").textValue();
                    Map<String, String> params = new HashMap<>();
                    params.put("currency", currency);
                    params.put("period", period);
                    if (jsonNode.has("amountBand")) {
                        Double amountBand = jsonNode.get("amountBand").doubleValue();
                        params.put("amountBand", Double.toString(amountBand));
                        if (!BaseOperation.websocketKeyAlreadyExist(websocketKey)) {
                            session.getRemote().sendString(ServiceUtil.createWSResponseWithData(getOperationLiquidityAndVolume(currency, period, amountBand), "oldOperationLiquidityAndVolume", "params", params).toString());
                        } else {

                        }
                    } else if (jsonNode.has("timeBand")) {
                        Integer timeBand = jsonNode.get("timeBand").intValue();
                        String timeBandBase = jsonNode.get("timeBandBase").textValue();
                        params.put("timeBand", Integer.toString(timeBand));
                        params.put("timeBandBase", timeBandBase);
                        if (!BaseOperation.websocketKeyAlreadyExist(websocketKey)) {
                            session.getRemote().sendString(ServiceUtil.createWSResponseWithData(getOperationLiquidityAndVolume(currency, period, timeBand, timeBandBase), "oldOperationLiquidityAndVolume", "params", params).toString());
                        } else {

                        }
                    }
                    BaseOperation.createWebsocketKeyFile(websocketKey);
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetOperationLiquidityAndVolumeSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static JsonNode getOperationLiquidityAndVolume(String currency, String period, Double amountBand) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode finalOperationBalance = mapper.createObjectNode();
        File operationBalanceFolder = FileUtil.createFolderIfNoExist(new File(OTCFolderLocator.getCurrencyFolder(null, currency), "OperationBalance"));
        File operationBalanceOldFolder = FileUtil.createFolderIfNoExist(new File(operationBalanceFolder, "Old"));
        String timestamp = null;
        Map<String, Double> liquidity = new TreeMap<>();
        Map<String, Double> volume = new TreeMap<>();
        Map<String, Double> btcLiquidity = new TreeMap<>();
        Map<String, Double> btcVolume = new TreeMap<>();
        boolean stop = false;
        for (File operationBalanceFile : operationBalanceFolder.listFiles()) {
            if (!operationBalanceFile.isFile()) {
                continue;
            }
            try {
                JsonNode operationBalance = mapper.readTree(operationBalanceFile);
                String operationType = operationBalance.get("operationType").textValue();
                Double amount = operationBalance.get("amount").doubleValue();
                amountBand = amountBand - amount;
                if (amountBand <= 0) {
                    stop = true;
                    break;
                }
                Double lastBuyPrice;
                Double lastSellPrice;
                if (operationBalance.has("lastBuyPrice")) {
                    lastBuyPrice = operationBalance.get("lastBuyPrice").doubleValue();
                } else {
                    lastBuyPrice = operationBalance.get("askPrice").doubleValue();
                }
                if (operationBalance.has("lastSellPrice")) {
                    lastSellPrice = operationBalance.get("lastSellPrice").doubleValue();
                } else {
                    lastSellPrice = operationBalance.get("bidPrice").doubleValue();
                }
                String operationTimestamp = operationBalance.get("timestamp").textValue();
                timestamp = getTimestamp(operationTimestamp, period, timestamp);
                if (timestamp == null) {
                    continue;
                }
                if (operationType.equals("BUY")) {
                    if (!liquidity.containsKey(timestamp)) {
                        liquidity.put(timestamp, 0.0);
                    }
                    liquidity.put(timestamp, liquidity.get(timestamp) + amount);
                    if (!volume.containsKey(timestamp)) {
                        volume.put(timestamp, 0.0);
                    }
                    volume.put(timestamp, volume.get(timestamp) + amount);
                    if (!btcLiquidity.containsKey(timestamp)) {
                        btcLiquidity.put(timestamp, 0.0);
                    }
                    btcLiquidity.put(timestamp, btcLiquidity.get(timestamp) - amount / lastBuyPrice);
                    if (!btcVolume.containsKey(timestamp)) {
                        btcVolume.put(timestamp, 0.0);
                    }
                    btcVolume.put(timestamp, btcVolume.get(timestamp) + amount / lastBuyPrice);
                } else if (operationType.equals("SELL")) {
                    if (!liquidity.containsKey(timestamp)) {
                        liquidity.put(timestamp, 0.0);
                    }
                    liquidity.put(timestamp, liquidity.get(timestamp) - amount);
                    if (!volume.containsKey(timestamp)) {
                        volume.put(timestamp, 0.0);
                    }
                    volume.put(timestamp, volume.get(timestamp) + amount);
                    if (!btcLiquidity.containsKey(timestamp)) {
                        btcLiquidity.put(timestamp, 0.0);
                    }
                    btcLiquidity.put(timestamp, btcLiquidity.get(timestamp) + amount / lastSellPrice);
                    if (!btcVolume.containsKey(timestamp)) {
                        btcVolume.put(timestamp, 0.0);
                    }
                    btcVolume.put(timestamp, btcVolume.get(timestamp) + amount / lastSellPrice);
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetOperationLiquidityAndVolumeSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (stop) {
            return finalOperationBalance;
        }
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(operationBalanceOldFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o2, o1) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        return id2.compareTo(id1);
                    })
                    .iterator();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                File file = it.toFile();
                JsonNode operationBalanceData = mapper.readTree(file);
                String operationType = operationBalanceData.get("operationType").textValue();
                Double amount = operationBalanceData.get("amount").doubleValue();
                amountBand = amountBand - amount;
                if (amountBand <= 0) {
                    break;
                }
                Double lastBuyPrice;
                Double lastSellPrice;
                if (operationBalanceData.has("lastBuyPrice")) {
                    lastBuyPrice = operationBalanceData.get("lastBuyPrice").doubleValue();
                } else {
                    lastBuyPrice = operationBalanceData.get("askPrice").doubleValue();
                }
                if (operationBalanceData.has("lastSellPrice")) {
                    lastSellPrice = operationBalanceData.get("lastSellPrice").doubleValue();
                } else {
                    lastSellPrice = operationBalanceData.get("bidPrice").doubleValue();
                }
                String operationTimestamp = operationBalanceData.get("timestamp").textValue();
                timestamp = getTimestamp(operationTimestamp, period, timestamp);
                if (timestamp == null) {
                    continue;
                }
                if (operationType.equals("BUY")) {
                    if (!liquidity.containsKey(timestamp)) {
                        liquidity.put(timestamp, 0.0);
                    }
                    liquidity.put(timestamp, liquidity.get(timestamp) + amount);
                    if (!volume.containsKey(timestamp)) {
                        volume.put(timestamp, 0.0);
                    }
                    volume.put(timestamp, volume.get(timestamp) + amount);
                    if (!btcLiquidity.containsKey(timestamp)) {
                        btcLiquidity.put(timestamp, 0.0);
                    }
                    btcLiquidity.put(timestamp, btcLiquidity.get(timestamp) - amount / lastBuyPrice);
                    if (!btcVolume.containsKey(timestamp)) {
                        btcVolume.put(timestamp, 0.0);
                    }
                    btcVolume.put(timestamp, btcVolume.get(timestamp) + amount / lastBuyPrice);
                } else if (operationType.equals("SELL")) {
                    if (!liquidity.containsKey(timestamp)) {
                        liquidity.put(timestamp, 0.0);
                    }
                    liquidity.put(timestamp, liquidity.get(timestamp) - amount);
                    if (!volume.containsKey(timestamp)) {
                        volume.put(timestamp, 0.0);
                    }
                    volume.put(timestamp, volume.get(timestamp) + amount);
                    if (!btcLiquidity.containsKey(timestamp)) {
                        btcLiquidity.put(timestamp, 0.0);
                    }
                    btcLiquidity.put(timestamp, btcLiquidity.get(timestamp) + amount / lastSellPrice);
                    if (!btcVolume.containsKey(timestamp)) {
                        btcVolume.put(timestamp, 0.0);
                    }
                    btcVolume.put(timestamp, btcVolume.get(timestamp) + amount / lastSellPrice);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminGetOperationLiquidityAndVolumeSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonNode liquidityNode = mapper.createObjectNode();
        for (String key : liquidity.keySet()) {
            ((ObjectNode) liquidityNode).put(key, liquidity.get(key));
        }
        ((ObjectNode) finalOperationBalance).put("liquidity", liquidityNode);
        JsonNode volumeNode = mapper.createObjectNode();
        for (String key : volume.keySet()) {
            ((ObjectNode) volumeNode).put(key, volume.get(key));
        }
        ((ObjectNode) finalOperationBalance).put("volume", volumeNode);
        JsonNode btcLiquidityNode = mapper.createObjectNode();
        for (String key : btcLiquidity.keySet()) {
            ((ObjectNode) btcLiquidityNode).put(key, btcLiquidity.get(key));
        }
        ((ObjectNode) finalOperationBalance).put("btcLiquidity", btcLiquidityNode);
        JsonNode btcVolumeNode = mapper.createObjectNode();
        for (String key : btcVolume.keySet()) {
            ((ObjectNode) btcVolumeNode).put(key, btcVolume.get(key));
        }
        ((ObjectNode) finalOperationBalance).put("btcVolume", btcVolumeNode);
        return finalOperationBalance;
    }

    private static JsonNode getOperationLiquidityAndVolume(String currency, String period, Integer timeBand, String timeBandBase) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode operationBalance = mapper.createObjectNode();
        File otcCurrencyOperationBalanceFolder = FileUtil.createFolderIfNoExist(new File(OTCFolderLocator.getCurrencyFolder(null, currency), "OperationBalance"));
        File otcCurrencyOperationBalanceOldFolder = FileUtil.createFolderIfNoExist(new File(otcCurrencyOperationBalanceFolder, "Old"));
        String lastTimestamp;
        switch (timeBandBase) {
            case "H":
                lastTimestamp = DateUtil.getDateHoursBefore(DateUtil.getCurrentDate(), timeBand);
                break;
            case "m":
                lastTimestamp = DateUtil.getDateMinutesBefore(DateUtil.getCurrentDate(), timeBand);
                break;
            default:
                return operationBalance;
        }
        String timestamp = null;
        Map<String, Double> liquidity = new TreeMap<>();
        Map<String, Double> volume = new TreeMap<>();
        Map<String, Double> btcLiquidity = new TreeMap<>();
        Map<String, Double> btcVolume = new TreeMap<>();
        boolean stop = false;
        for (File otcCurrencyOperationBalanceFile : otcCurrencyOperationBalanceFolder.listFiles()) {
            if (!otcCurrencyOperationBalanceFile.isFile()) {
                continue;
            }
            try {
                JsonNode otcCurrencyOperationBalance = mapper.readTree(otcCurrencyOperationBalanceFile);
                String operationType = otcCurrencyOperationBalance.get("operationType").textValue();
                Double amount = otcCurrencyOperationBalance.get("amount").doubleValue();
                Double lastBuyPrice;
                Double lastSellPrice;
                if (otcCurrencyOperationBalance.has("lastBuyPrice")) {
                    lastBuyPrice = otcCurrencyOperationBalance.get("lastBuyPrice").doubleValue();
                } else {
                    lastBuyPrice = otcCurrencyOperationBalance.get("askPrice").doubleValue();
                }
                if (otcCurrencyOperationBalance.has("lastSellPrice")) {
                    lastSellPrice = otcCurrencyOperationBalance.get("lastSellPrice").doubleValue();
                } else {
                    lastSellPrice = otcCurrencyOperationBalance.get("bidPrice").doubleValue();
                }
                String operationTimestamp = otcCurrencyOperationBalance.get("timestamp").textValue();
                if (DateUtil.parseDate(operationTimestamp).before(DateUtil.parseDate(lastTimestamp))) {
                    stop = true;
                    break;
                }
                timestamp = getTimestamp(operationTimestamp, period, timestamp);
                if (timestamp == null) {
                    continue;
                }
                if (operationType.equals("BUY")) {
                    if (!liquidity.containsKey(timestamp)) {
                        liquidity.put(timestamp, 0.0);
                    }
                    liquidity.put(timestamp, liquidity.get(timestamp) + amount);
                    if (!volume.containsKey(timestamp)) {
                        volume.put(timestamp, 0.0);
                    }
                    volume.put(timestamp, volume.get(timestamp) + amount);
                    if (!btcLiquidity.containsKey(timestamp)) {
                        btcLiquidity.put(timestamp, 0.0);
                    }
                    btcLiquidity.put(timestamp, btcLiquidity.get(timestamp) - amount / lastBuyPrice);
                    if (!btcVolume.containsKey(timestamp)) {
                        btcVolume.put(timestamp, 0.0);
                    }
                    btcVolume.put(timestamp, btcVolume.get(timestamp) + amount / lastBuyPrice);
                } else if (operationType.equals("SELL")) {
                    if (!liquidity.containsKey(timestamp)) {
                        liquidity.put(timestamp, 0.0);
                    }
                    liquidity.put(timestamp, liquidity.get(timestamp) - amount);
                    if (!volume.containsKey(timestamp)) {
                        volume.put(timestamp, 0.0);
                    }
                    volume.put(timestamp, volume.get(timestamp) + amount);
                    if (!btcLiquidity.containsKey(timestamp)) {
                        btcLiquidity.put(timestamp, 0.0);
                    }
                    btcLiquidity.put(timestamp, btcLiquidity.get(timestamp) + amount / lastSellPrice);
                    if (!btcVolume.containsKey(timestamp)) {
                        btcVolume.put(timestamp, 0.0);
                    }
                    btcVolume.put(timestamp, btcVolume.get(timestamp) + amount / lastSellPrice);
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetOperationLiquidityAndVolumeSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (stop) {
            return operationBalance;
        }
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(otcCurrencyOperationBalanceOldFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o2, o1) -> {
                        Long id1 = Long.parseLong(o1.toFile().getName().replace(".json", ""));
                        Long id2 = Long.parseLong(o2.toFile().getName().replace(".json", ""));
                        return id2.compareTo(id1);
                    })
                    .iterator();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                File file = it.toFile();
                JsonNode operationBalanceData = mapper.readTree(file);
                String operationType = operationBalanceData.get("operationType").textValue();
                Double amount = operationBalanceData.get("amount").doubleValue();
                String operationtimestamp = operationBalanceData.get("timestamp").textValue();
                if (DateUtil.parseDate(operationtimestamp).before(DateUtil.parseDate(lastTimestamp))) {
                    break;
                }
                Double lastBuyPrice;
                Double lastSellPrice;
                if (operationBalanceData.has("lastBuyPrice")) {
                    lastBuyPrice = operationBalanceData.get("lastBuyPrice").doubleValue();
                } else {
                    lastBuyPrice = operationBalanceData.get("askPrice").doubleValue();
                }
                if (operationBalanceData.has("lastSellPrice")) {
                    lastSellPrice = operationBalanceData.get("lastSellPrice").doubleValue();
                } else {
                    lastSellPrice = operationBalanceData.get("bidPrice").doubleValue();
                }
                String operationTimestamp = operationBalanceData.get("timestamp").textValue();
                timestamp = getTimestamp(operationTimestamp, period, timestamp);
                if (timestamp == null) {
                    continue;
                }
                if (operationType.equals("BUY")) {
                    if (!liquidity.containsKey(timestamp)) {
                        liquidity.put(timestamp, 0.0);
                    }
                    liquidity.put(timestamp, liquidity.get(timestamp) + amount);
                    if (!volume.containsKey(timestamp)) {
                        volume.put(timestamp, 0.0);
                    }
                    volume.put(timestamp, volume.get(timestamp) + amount);
                    if (!btcLiquidity.containsKey(timestamp)) {
                        btcLiquidity.put(timestamp, 0.0);
                    }
                    btcLiquidity.put(timestamp, btcLiquidity.get(timestamp) - amount / lastBuyPrice);
                    if (!btcVolume.containsKey(timestamp)) {
                        btcVolume.put(timestamp, 0.0);
                    }
                    btcVolume.put(timestamp, btcVolume.get(timestamp) + amount / lastBuyPrice);
                } else if (operationType.equals("SELL")) {
                    if (!liquidity.containsKey(timestamp)) {
                        liquidity.put(timestamp, 0.0);
                    }
                    liquidity.put(timestamp, liquidity.get(timestamp) - amount);
                    if (!volume.containsKey(timestamp)) {
                        volume.put(timestamp, 0.0);
                    }
                    volume.put(timestamp, volume.get(timestamp) + amount);
                    if (!btcLiquidity.containsKey(timestamp)) {
                        btcLiquidity.put(timestamp, 0.0);
                    }
                    btcLiquidity.put(timestamp, btcLiquidity.get(timestamp) + amount / lastSellPrice);
                    if (!btcVolume.containsKey(timestamp)) {
                        btcVolume.put(timestamp, 0.0);
                    }
                    btcVolume.put(timestamp, btcVolume.get(timestamp) + amount / lastSellPrice);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminGetOperationLiquidityAndVolumeSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonNode liquidityNode = mapper.createObjectNode();
        for (String key : liquidity.keySet()) {
            ((ObjectNode) liquidityNode).put(key, liquidity.get(key));
        }
        ((ObjectNode) operationBalance).put("liquidity", liquidityNode);
        JsonNode volumeNode = mapper.createObjectNode();
        for (String key : volume.keySet()) {
            ((ObjectNode) volumeNode).put(key, volume.get(key));
        }
        ((ObjectNode) operationBalance).put("volume", volumeNode);
        JsonNode btcLiquidityNode = mapper.createObjectNode();
        for (String key : btcLiquidity.keySet()) {
            ((ObjectNode) btcLiquidityNode).put(key, btcLiquidity.get(key));
        }
        ((ObjectNode) operationBalance).put("btcLiquidity", btcLiquidityNode);
        JsonNode btcVolumeNode = mapper.createObjectNode();
        for (String key : btcVolume.keySet()) {
            ((ObjectNode) btcVolumeNode).put(key, btcVolume.get(key));
        }
        ((ObjectNode) operationBalance).put("btcVolume", btcVolumeNode);
        return operationBalance;
    }

    private static String getTimestamp(String operationTimestamp, String period, String lastTimestamp) {
        if (lastTimestamp == null) {
            lastTimestamp = DateUtil.getCurrentDate();
        }
        Integer periodNumber = null;
        if (period.contains("m")) {
            lastTimestamp = DateUtil.getMinuteStartDate(lastTimestamp);
            periodNumber = Integer.parseInt(period.replace("m", ""));
            while (true) {
                if (DateUtil.parseDate(DateUtil.getDateMinutesBefore(lastTimestamp, periodNumber)).before(DateUtil.parseDate(operationTimestamp))) {
                    return lastTimestamp;
                } else {
                    lastTimestamp = DateUtil.getDateMinutesBefore(lastTimestamp, periodNumber);
                }
            }
        } else if (period.contains("H")) {
            lastTimestamp = DateUtil.getHourStartDate(lastTimestamp);
            periodNumber = Integer.parseInt(period.replace("H", ""));
            while (true) {
                if (DateUtil.parseDate(DateUtil.getDateHoursBefore(lastTimestamp, periodNumber)).before(DateUtil.parseDate(operationTimestamp))) {
                    return lastTimestamp;
                } else {
                    lastTimestamp = DateUtil.getDateHoursBefore(lastTimestamp, periodNumber);
                }
            }
        } else {
            return null;
        }
    }

}
