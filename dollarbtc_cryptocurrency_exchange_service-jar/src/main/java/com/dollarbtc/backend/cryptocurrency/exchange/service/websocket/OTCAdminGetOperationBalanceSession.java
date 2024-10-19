/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

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
import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class OTCAdminGetOperationBalanceSession {

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
                String fileName = null;
                for (Session session : sessions.keySet()) {
                    JsonNode jsonNode = sessions.get(session);
                    String currency = jsonNode.get("currency").textValue();
                    if (jsonNode.has("amountBand")) {
                        Double amountBand = jsonNode.get("amountBand").doubleValue();
                        String lastFileName = null;
                        if (jsonNode.has("lastFileName")) {
                            lastFileName = jsonNode.get("lastFileName").textValue();
                        }
                        Map<String, String> params = new HashMap<>();
                        params.put("currency", currency);
                        params.put("amountBand", Double.toString(amountBand));
                        if (fileName == null) {
                            fileName = getFileName(currency);
                        }
                        if (fileName == null) {
                            session.getRemote().sendPing(null);
                            continue;
                        }
                        if (fileName.equals(lastFileName)) {
                            session.getRemote().sendPing(null);
                            continue;
                        }
                        session.getRemote().sendString(ServiceUtil.createWSResponseWithData(getOperationBalance(currency, amountBand), "operationBalance", "params", params).toString());
                        ((ObjectNode) sessions.get(session)).put("lastFileName", fileName);
                    }
                    if (jsonNode.has("timeBand")) {
                        Integer timeBand = jsonNode.get("timeBand").intValue();
                        String timeBandBase = jsonNode.get("timeBandBase").textValue();
                        String lastFileName = null;
                        if (jsonNode.has("lastFileName")) {
                            lastFileName = jsonNode.get("lastFileName").textValue();
                        }
                        Map<String, String> params = new HashMap<>();
                        params.put("currency", currency);
                        params.put("timeBand", Integer.toString(timeBand));
                        if (fileName == null) {
                            fileName = getFileName(currency);
                        }
                        if (fileName == null) {
                            session.getRemote().sendPing(null);
                            continue;
                        }
                        if (fileName.equals(lastFileName)) {
                            session.getRemote().sendPing(null);
                            continue;
                        }
                        session.getRemote().sendString(ServiceUtil.createWSResponseWithData(getOperationBalance(currency, timeBand, timeBandBase), "operationBalance", "params", params).toString());
                        ((ObjectNode) sessions.get(session)).put("lastFileName", fileName);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetOperationBalanceSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static JsonNode getOperationBalance(String currency, Double amountBand) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode operationBalance = mapper.createObjectNode();
        File otcCurrencyOperationBalanceFolder = FileUtil.createFolderIfNoExist(new File(OTCFolderLocator.getCurrencyFolder(null, currency), "OperationBalance"));
        File otcCurrencyOperationBalanceOldFolder = FileUtil.createFolderIfNoExist(new File(otcCurrencyOperationBalanceFolder, "Old"));
        Double askPrice = 0.0;
        Double bidPrice = 0.0;
        Double lastBuyPrice = 0.0;
        Double lastSellPrice = 0.0;
        Double balanceAmount = 0.0;
        String lastOperationType = null;
        boolean stop = false;
        for (File otcCurrencyOperationBalanceFile : otcCurrencyOperationBalanceFolder.listFiles()) {
            if (!otcCurrencyOperationBalanceFile.isFile()) {
                continue;
            }
            try {
                JsonNode otcCurrencyOperationBalance = mapper.readTree(otcCurrencyOperationBalanceFile);
                askPrice = otcCurrencyOperationBalance.get("askPrice").doubleValue();
                bidPrice = otcCurrencyOperationBalance.get("bidPrice").doubleValue();
                lastOperationType = otcCurrencyOperationBalance.get("operationType").textValue();
                Double amount = otcCurrencyOperationBalance.get("amount").doubleValue();
                amountBand = amountBand - amount;
                if(amountBand <= 0){
                    stop = true;
                    break;
                }
                if (lastOperationType.equals("BUY")) {
                    balanceAmount = balanceAmount + amount;
                } else if (lastOperationType.equals("SELL")) {
                    balanceAmount = balanceAmount - amount;
                }
                if (otcCurrencyOperationBalance.has("lastBuyPrice")) {
                    lastBuyPrice = otcCurrencyOperationBalance.get("lastBuyPrice").doubleValue();
                }
                if (otcCurrencyOperationBalance.has("lastSellPrice")) {
                    lastSellPrice = otcCurrencyOperationBalance.get("lastSellPrice").doubleValue();
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetOperationBalanceSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(stop){
            return operationBalance;
        }
        ((ObjectNode) operationBalance).put("askPrice", askPrice);
        ((ObjectNode) operationBalance).put("bidPrice", bidPrice);
        ((ObjectNode) operationBalance).put("lastBuyPrice", lastBuyPrice);
        ((ObjectNode) operationBalance).put("lastSellPrice", lastSellPrice);
        if (lastOperationType != null) {
            ((ObjectNode) operationBalance).put("lastOperationType", lastOperationType);
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
                Double amount = operationBalanceData.get("amount").doubleValue();
                amountBand = amountBand - amount;
                if(amountBand <= 0){
                    break;
                }
                String operationType = operationBalanceData.get("operationType").textValue();
                if (operationType.equals("BUY")) {
                    balanceAmount = balanceAmount + amount;
                } else if (operationType.equals("SELL")) {
                    balanceAmount = balanceAmount - amount;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminGetOperationBalanceSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        Double bandBalancePercent = balanceAmount / amountBand * 100;
        ((ObjectNode) operationBalance).put("balanceAmount", balanceAmount);
        ((ObjectNode) operationBalance).put("amountBand", amountBand);
        ((ObjectNode) operationBalance).put("bandBalancePercent", bandBalancePercent);
        return operationBalance;
    }

    private static JsonNode getOperationBalance(String currency, Integer timeBand, String timeBandBase) {
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
        Double askPrice = 0.0;
        Double bidPrice = 0.0;
        Double lastBuyPrice = 0.0;
        Double lastSellPrice = 0.0;
        Double balanceAmount = 0.0;
        Double amountBand = 0.0;
        String lastOperationType = null;
        boolean stop = false;
        for (File otcCurrencyOperationBalanceFile : otcCurrencyOperationBalanceFolder.listFiles()) {
            if (!otcCurrencyOperationBalanceFile.isFile()) {
                continue;
            }
            try {
                JsonNode otcCurrencyOperationBalance = mapper.readTree(otcCurrencyOperationBalanceFile);
                askPrice = otcCurrencyOperationBalance.get("askPrice").doubleValue();
                bidPrice = otcCurrencyOperationBalance.get("bidPrice").doubleValue();
                lastOperationType = otcCurrencyOperationBalance.get("operationType").textValue();
                Double amount = otcCurrencyOperationBalance.get("amount").doubleValue();
                String timestamp = otcCurrencyOperationBalance.get("timestamp").textValue();
                if(DateUtil.parseDate(timestamp).before(DateUtil.parseDate(lastTimestamp))){
                    stop = true;
                    break;
                }
                if (lastOperationType.equals("BUY")) {
                    balanceAmount = balanceAmount + amount;
                } else if (lastOperationType.equals("SELL")) {
                    balanceAmount = balanceAmount - amount;
                }
                amountBand = amountBand + amount;
                if (otcCurrencyOperationBalance.has("lastBuyPrice")) {
                    lastBuyPrice = otcCurrencyOperationBalance.get("lastBuyPrice").doubleValue();
                }
                if (otcCurrencyOperationBalance.has("lastSellPrice")) {
                    lastSellPrice = otcCurrencyOperationBalance.get("lastSellPrice").doubleValue();
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetOperationBalanceSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(stop){
            return operationBalance;
        }
        ((ObjectNode) operationBalance).put("askPrice", askPrice);
        ((ObjectNode) operationBalance).put("bidPrice", bidPrice);
        ((ObjectNode) operationBalance).put("lastBuyPrice", lastBuyPrice);
        ((ObjectNode) operationBalance).put("lastSellPrice", lastSellPrice);
        if (lastOperationType != null) {
            ((ObjectNode) operationBalance).put("lastOperationType", lastOperationType);
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
                Double amount = operationBalanceData.get("amount").doubleValue();
                String operationType = operationBalanceData.get("operationType").textValue();
                String timestamp = operationBalanceData.get("timestamp").textValue();
                if(DateUtil.parseDate(timestamp).before(DateUtil.parseDate(lastTimestamp))){
                    break;
                }
                if (operationType.equals("BUY")) {
                    balanceAmount = balanceAmount + amount;
                } else if (operationType.equals("SELL")) {
                    balanceAmount = balanceAmount - amount;
                }
                amountBand = amountBand + amount;
            }
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminGetOperationBalanceSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(amountBand == 0.0){
            return operationBalance;
        }
        Double bandBalancePercent = balanceAmount / amountBand * 100;
        ((ObjectNode) operationBalance).put("balanceAmount", balanceAmount);
        ((ObjectNode) operationBalance).put("amountBand", amountBand);
        ((ObjectNode) operationBalance).put("bandBalancePercent", bandBalancePercent);
        return operationBalance;
    }

    private static String getFileName(String currency) {
        File otcCurrencyOperationBalanceFolder = FileUtil.createFolderIfNoExist(new File(OTCFolderLocator.getCurrencyFolder(null, currency), "OperationBalance"));
        for (File otcCurrencyOperationBalanceFile : otcCurrencyOperationBalanceFolder.listFiles()) {
            if (!otcCurrencyOperationBalanceFile.isFile()) {
                continue;
            }
            return otcCurrencyOperationBalanceFile.getName();
        }
        return null;
    }

}
