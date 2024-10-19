/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketCloseOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketGetOrdersRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketPostOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketTakeOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OrderType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketCloseOrder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketGetOrders;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketPostOrder;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketTakeOrder;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.PricesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class RunBotMoneyMarketOrderMain {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        String currentTimestamp = DateUtil.getCurrentDate();
        File moneyMarketBotsActivityFile = MoneyMarketFolderLocator.getBotsActivityFile();
        JsonNode moneyMarketBotsActivity = null;
        if (moneyMarketBotsActivityFile.isFile()) {
            try {
                moneyMarketBotsActivity = MAPPER.readTree(moneyMarketBotsActivityFile);
                if (moneyMarketBotsActivity != null) {
                    if (moneyMarketBotsActivity.has("timestamp") && moneyMarketBotsActivity.get("timestamp").textValue().compareTo(DateUtil.getDateSecondsBefore(currentTimestamp, 50)) > 0) {
                        System.out.println("Escaping RunBotMoneyMarketOrderMain");
                        return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(RunBotMoneyMarketOrderMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (moneyMarketBotsActivity == null) {
            moneyMarketBotsActivity = MAPPER.createObjectNode();
        }
        System.out.println("Starting RunBotMoneyMarketOrderMain");
        Date startDate = new Date();
        File moneyMarketBotsFile = MoneyMarketFolderLocator.getBotsFile();
        try {
            ArrayNode moneyMarketBots = (ArrayNode) MAPPER.readTree(moneyMarketBotsFile);
            Iterator<JsonNode> moneyMarketBotsIterator = moneyMarketBots.iterator();
            while (moneyMarketBotsIterator.hasNext()) {
                ((ObjectNode) moneyMarketBotsActivity).put("timestamp", DateUtil.getCurrentDate());
                FileUtil.editFile(moneyMarketBotsActivity, moneyMarketBotsActivityFile);
                JsonNode moneyMarketBotsIt = moneyMarketBotsIterator.next();
                String userName = moneyMarketBotsIt.get("userName").textValue();
                String nickName = moneyMarketBotsIt.get("userName").textValue();
                System.out.println("userName: " + userName);
                System.out.println("nickName: " + nickName);
                //1 of 2
                if (ThreadLocalRandom.current().nextInt(100) <= 49) {
                    System.out.println("1 of 2 continue");
                    continue;
                }
                System.out.println("1 of 2 pass");
                String lastActivityTimestamp = null;
                if (moneyMarketBotsIt.has("lastActivityTimestamp")) {
                    lastActivityTimestamp = moneyMarketBotsIt.get("lastActivityTimestamp").textValue();
                }
//                if (lastActivityTimestamp != null && DateUtil.getDateMinutesBefore(currentTimestamp, 15).compareTo(lastActivityTimestamp) < 0) {
//                    continue;
//                }
                ArrayNode allowedPairsOrderTypes = MAPPER.createArrayNode();
                if (moneyMarketBotsIt.has("allowedPairsOrderTypes")) {
                    allowedPairsOrderTypes.addAll((ArrayNode) moneyMarketBotsIt.get("allowedPairsOrderTypes"));
                } else {
                    continue;
                }
                System.out.println("allowedPairsOrderTypes.size(): " + allowedPairsOrderTypes.size());
                if (allowedPairsOrderTypes.size() == 0) {
                    continue;
                }
                ((ObjectNode) moneyMarketBotsIt).put("lastActivityTimestamp", currentTimestamp);
                Iterator<JsonNode> allowedPairsOrderTypesIterator = allowedPairsOrderTypes.iterator();
                while (allowedPairsOrderTypesIterator.hasNext()) {
                    String allowedPairOrderType = allowedPairsOrderTypesIterator.next().textValue();
                    String pair = allowedPairOrderType.split("__")[0];
                    OrderType orderType = OrderType.valueOf(allowedPairOrderType.split("__")[1]);
                    System.out.println("pair: " + pair);
                    System.out.println("orderType: " + orderType);
                    //1 of 2
                    if (ThreadLocalRandom.current().nextInt(100) <= 49) {
                        System.out.println("1 of 2 continue");
                        continue;
                    }
                    System.out.println("1 of 2 pass");
                    ArrayNode moneyMarketGetOrders = getMoneyMarketGetOrders(new MoneyMarketGetOrders(new MoneyMarketGetOrdersRequest(userName)).getResponse());
                    String baseCurrency = getCurrency(pair, true);
                    String targetCurrency = getCurrency(pair, false);
                    if (moneyMarketGetOrders.size() == 0) {
                        System.out.println("POST initiated");
                        //1 of 1
                        if (ThreadLocalRandom.current().nextInt(100) <= -1) {
                            System.out.println("1 of 1 continue");
                            continue;
                        }
                        System.out.println("1 of 1 pass");
                        //POST WITH ASK (-0.9% to -0.2%) BID (+0.2% to +0.9%)
                        File pricesFastChangeFolder = PricesFolderLocator.getFastChangeFolder(baseCurrency);
                        File pricesFile = null;
                        for (File pricesFastChangeFile : pricesFastChangeFolder.listFiles()) {
                            if (pricesFastChangeFile.isFile()) {
                                pricesFile = pricesFastChangeFile;
                                break;
                            }
                        }
                        try {
                            JsonNode prices = MAPPER.readTree(pricesFile);
                            Double amount = ThreadLocalRandom.current().nextDouble(moneyMarketBotsIt.get("minAmount").get(baseCurrency).doubleValue(), moneyMarketBotsIt.get("maxAmount").get(baseCurrency).doubleValue());
                            Double price = prices.get(targetCurrency).get(orderType.name().toLowerCase()).doubleValue();
                            Double pricePercent = -1.0 * ThreadLocalRandom.current().nextDouble(0.2, 0.9);
                            if (allowedPairOrderType.split("__").length == 3) {
                                pricePercent = -1.0 * ThreadLocalRandom.current().nextDouble(Double.parseDouble(allowedPairOrderType.split("__")[2].split("_")[0]), Double.parseDouble(allowedPairOrderType.split("__")[2].split("_")[1]));
                            }
                            if (orderType.equals(OrderType.BID)) {
                                pricePercent = pricePercent * -1;
                            }
                            price = price * (100 + pricePercent) / 100;
                            String source = "BOT";
                            new MoneyMarketPostOrder(new MoneyMarketPostOrderRequest(userName, pair, nickName, source, amount, price, 10, "MINUTES", orderType, true)).getResponse();
                            System.out.println("POST executed");
                        } catch (IOException ex) {
                            Logger.getLogger(RunBotMoneyMarketOrderMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        System.out.println("CLOSE initiated");
                        //1 of 3
                        if (ThreadLocalRandom.current().nextInt(100) <= 64) {
                            System.out.println("1 of 3 continue");
                            continue;
                        }
                        System.out.println("1 of 3 pass");
                        //CLOSE
                        int randomClose = ThreadLocalRandom.current().nextInt(moneyMarketGetOrders.size());
                        int i = 0;
                        Iterator<JsonNode> moneyMarketGetOrdersIterator = moneyMarketGetOrders.iterator();
                        while (moneyMarketGetOrdersIterator.hasNext()) {
                            JsonNode moneyMarketGetOrdersIt = moneyMarketGetOrdersIterator.next();
                            i++;
                            if (randomClose + 1 != i) {
                                continue;
                            }
                            new MoneyMarketCloseOrder(new MoneyMarketCloseOrderRequest(userName, moneyMarketGetOrdersIt.get("id").textValue())).getResponse();
                            System.out.println("CLOSE executed");
                            break;
                        }
                    }
                }
                allowedPairsOrderTypesIterator = allowedPairsOrderTypes.iterator();
                while (allowedPairsOrderTypesIterator.hasNext()) {
                    String allowedPairOrderType = allowedPairsOrderTypesIterator.next().textValue();
                    System.out.println("TAKE initiated");
                    //1 of 3
                    if (ThreadLocalRandom.current().nextInt(100) <= 64) {
                        System.out.println("1 of 3 continue");
                        continue;
                    }
                    System.out.println("1 of 3 pass");
                    //TAKE BOTS ORDERS
                    String pair = allowedPairOrderType.split("__")[0];
                    OrderType orderType = OrderType.valueOf(allowedPairOrderType.split("__")[1]);
                    System.out.println("pair: " + pair);
                    System.out.println("orderType: " + orderType);
                    OrderType[] orderTypes = new OrderType[]{orderType};
                    JsonNode orders = new MoneyMarketGetOrders(new MoneyMarketGetOrdersRequest(pair, orderTypes)).getResponse();
                    boolean breakLoop = false;
                    if(!orders.has(orderType.name())){
                        continue;
                    }
                    Iterator<JsonNode> ordersIterator = orders.get(orderType.name()).iterator();
                    while (ordersIterator.hasNext()) {
                        if (breakLoop) {
                            break;
                        }
                        JsonNode ordersIt = ordersIterator.next();
                        System.out.println("ordersIt>>>>> " + ordersIt);
                        if (ordersIt.get("userName").textValue().equals(userName)) {
                            continue;
                        }
                        boolean bot;
                        int number;
                        if (!ordersIt.has("bot") || !ordersIt.get("bot").booleanValue()) {
                            number = 100;
                            bot = false;
                            System.out.println("not a BOT order");
                        } else {
                            number = -1;
                            bot = true;
                            System.out.println("BOT order");
                        }
                        if (ThreadLocalRandom.current().nextInt(100) <= number) {
                            continue;
                        }
                        Double amount = ordersIt.get("amount").doubleValue();
                        amount = amount * ThreadLocalRandom.current().nextDouble(0.4, 1);
                        new MoneyMarketTakeOrder(new MoneyMarketTakeOrderRequest(userName, ordersIt.get("id").textValue(), nickName, amount, ordersIt.get("price").doubleValue(), bot)).getResponse();
                        System.out.println("TAKE executed");
                        breakLoop = true;
                        break;
                    }
                    //TAKE NORMAL ORDERS
                }
            }
            FileUtil.editFile(moneyMarketBots, moneyMarketBotsFile);
        } catch (IOException ex) {
            Logger.getLogger(RunBotMoneyMarketOrderMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        long timeInSeconds = (new Date().getTime() - startDate.getTime()) / 1000;
        System.out.println("timeInSeconds: " + timeInSeconds);
        System.out.println("Finishing RunBotMoneyMarketOrderMain");
    }

    private static ArrayNode getMoneyMarketGetOrders(JsonNode initialMoneyMarketGetOrders) {
        ArrayNode moneyMarketGetOrders = MAPPER.createArrayNode();
        if (initialMoneyMarketGetOrders.has("ASK")) {
            Iterator<JsonNode> initialMoneyMarketGetOrdersASKIterator = initialMoneyMarketGetOrders.get("ASK").iterator();
            while (initialMoneyMarketGetOrdersASKIterator.hasNext()) {
                JsonNode initialMoneyMarketGetOrdersASKIt = initialMoneyMarketGetOrdersASKIterator.next();
                moneyMarketGetOrders.add(initialMoneyMarketGetOrdersASKIt);
            }
        }
        if (initialMoneyMarketGetOrders.has("BID")) {
            Iterator<JsonNode> initialMoneyMarketGetOrdersBIDIterator = initialMoneyMarketGetOrders.get("BID").iterator();
            while (initialMoneyMarketGetOrdersBIDIterator.hasNext()) {
                JsonNode initialMoneyMarketGetOrdersBIDIt = initialMoneyMarketGetOrdersBIDIterator.next();
                moneyMarketGetOrders.add(initialMoneyMarketGetOrdersBIDIt);
            }
        }
        return moneyMarketGetOrders;
    }

    private static String getCurrency(String pair, Boolean base) throws IOException {
        JsonNode pairs = MAPPER.readTree(MoneyMarketFolderLocator.getPairsFile());
        if (pairs.has(pair)) {
            if (base) {
                return pairs.get(pair).get("base").textValue();
            } else {
                return pairs.get(pair).get("quote").textValue();
            }
        }
        throw new IOException("PAIR DOES NOT EXIST");
    }

}
