/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketTakeOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OrderType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CandlesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.PricesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyMarketTakeOrder extends AbstractOperation<String> {

    private final MoneyMarketTakeOrderRequest moneyMarketTakeOrderRequest;

    public MoneyMarketTakeOrder(MoneyMarketTakeOrderRequest moneyMarketTakeOrderRequest) {
        super(String.class);
        this.moneyMarketTakeOrderRequest = moneyMarketTakeOrderRequest;
    }

    @Override
    protected void execute() {
        File moneyMarketOrderFile = MoneyMarketFolderLocator.getOrderFile(moneyMarketTakeOrderRequest.getId());
        if (!moneyMarketOrderFile.isFile()) {
            super.response = "ORDER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyMarketOrder = mapper.readTree(moneyMarketOrderFile);
            /*if (moneyMarketOrder.get("userName").textValue().equals(moneyMarketTakeOrderRequest.getUserName())) {
                super.response = "SAME USER CAN NOT TAKE ITS OWN ORDER";
                return;
            }*/
            if (moneyMarketOrder.get("price").doubleValue() != moneyMarketTakeOrderRequest.getPrice()) {
                super.response = "ORDER PRICE CHANGE";
                return;
            }
            String source = moneyMarketOrder.get("source").textValue();
            String pair = moneyMarketOrder.get("pair").textValue();
            String pairBaseCurrency = MoneyMarketUtils.getCurrency(pair, true);
            String pairQuoteCurrency = MoneyMarketUtils.getCurrency(pair, false);
            Double leftAmount = moneyMarketOrder.get("amount").doubleValue();
            if (moneyMarketOrder.has("take")) {
                Iterator<JsonNode> moneyMarketOrderTakeIterator = moneyMarketOrder.get("take").iterator();
                while (moneyMarketOrderTakeIterator.hasNext()) {
                    JsonNode moneyMarketOrderTakeIt = moneyMarketOrderTakeIterator.next();
                    leftAmount = leftAmount - moneyMarketOrderTakeIt.get("amount").doubleValue();
                }
                if (leftAmount < moneyMarketTakeOrderRequest.getAmount()) {
                    super.response = "AMOUNT EXCEEDS ORDER AMOUNT";
                    return;
                }
            }
            boolean postUserBot = false;
            if (moneyMarketOrder.has("bot")) {
                postUserBot = moneyMarketOrder.get("bot").booleanValue();
            }
            boolean takeUserBot = moneyMarketTakeOrderRequest.isBot();
            if (OrderType.valueOf(moneyMarketOrder.get("type").textValue()).equals(OrderType.ASK)) {
                //TAKE USER BUY - SUBSTRACT QUOTE
                if (!takeUserBot && source.equals("EXCHANGE")) {
                    String substractToBalance = BaseOperation.substractToBalance(
                            UsersFolderLocator.getMCBalanceFolder(moneyMarketTakeOrderRequest.getUserName()), //TAKE USER
                            pairQuoteCurrency, //USD
                            moneyMarketTakeOrderRequest.getAmount() * moneyMarketOrder.get("price").doubleValue(), //1 * 52000
                            BalanceOperationType.MONEY_MARKET_TAKE_ORDER,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            false,
                            null,
                            false,
                            null
                    );
                    if (!substractToBalance.equals("OK")) {
                        super.response = substractToBalance;
                        return;
                    }
                }
                //POST USER ASK - ADD QUOTE
                if (!postUserBot && source.equals("EXCHANGE")) {
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(moneyMarketOrder.get("userName").textValue()), //POST USER
                            pairQuoteCurrency, //USD
                            moneyMarketTakeOrderRequest.getAmount() * moneyMarketOrder.get("price").doubleValue(), //1 * 52000
                            BalanceOperationType.MONEY_MARKET_TAKE_ORDER,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            null,
                            false,
                            null
                    );
                }
                //TAKE USER BUY - ADD BASE
                if (!takeUserBot && source.equals("EXCHANGE")) {
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(moneyMarketTakeOrderRequest.getUserName()), //TAKE USER
                            pairBaseCurrency, //BTC
                            moneyMarketTakeOrderRequest.getAmount(), //1
                            BalanceOperationType.MONEY_MARKET_TAKE_ORDER,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            null,
                            false,
                            null
                    );
                }
            } else {
                //TAKE USER SELL - SUBSTRACT BASE
                if (!takeUserBot && source.equals("EXCHANGE")) {
                    String substractToBalance = BaseOperation.substractToBalance(
                            UsersFolderLocator.getMCBalanceFolder(moneyMarketTakeOrderRequest.getUserName()), //TAKE USER
                            pairBaseCurrency, //BTC
                            moneyMarketTakeOrderRequest.getAmount(), //1
                            BalanceOperationType.MONEY_MARKET_TAKE_ORDER,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            false,
                            null,
                            false,
                            null
                    );
                    if (!substractToBalance.equals("OK")) {
                        super.response = substractToBalance;
                        return;
                    }
                }
                //POST USER BID - ADD BASE
                if (!postUserBot && source.equals("EXCHANGE")) {
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(moneyMarketOrder.get("userName").textValue()), //POST USER
                            pairBaseCurrency, //BTC
                            moneyMarketTakeOrderRequest.getAmount(), //1
                            BalanceOperationType.MONEY_MARKET_TAKE_ORDER,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            null,
                            false,
                            null
                    );
                }
                //TAKE USER BUY - ADD QUOTE
                if (!takeUserBot && source.equals("EXCHANGE")) {
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(moneyMarketTakeOrderRequest.getUserName()), //TAKE USER
                            pairQuoteCurrency, //USD
                            moneyMarketTakeOrderRequest.getAmount() * moneyMarketOrder.get("price").doubleValue(), //1 * 48000
                            BalanceOperationType.MONEY_MARKET_TAKE_ORDER,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            null,
                            false,
                            null
                    );
                }
            }
            if (!moneyMarketOrder.has("take")) {
                ((ObjectNode) moneyMarketOrder).set("take", mapper.createArrayNode());
            }
            JsonNode moneyMarketTakeOrder = moneyMarketTakeOrderRequest.toJsonNode();
            String id = BaseOperation.getId();
            String timestamp = DateUtil.getCurrentDate();
            ((ObjectNode) moneyMarketTakeOrder).put("id", id);
            ((ObjectNode) moneyMarketTakeOrder).put("timestamp", timestamp);
            if (source.equals("EXCHANGE")) {
                ((ObjectNode) moneyMarketTakeOrder).put("status", "SUCCESS");
            } else {
                ((ObjectNode) moneyMarketTakeOrder).put("status", "WAITING_FOR_PAYMENT");
            }
            if (moneyMarketOrder.get("type").textValue().equals("ASK")) {
                ((ObjectNode) moneyMarketTakeOrder).put("type", OrderType.BUY.name());
            } else {
                ((ObjectNode) moneyMarketTakeOrder).put("type", OrderType.SELL.name());
            }
            //EDIT ORIGINAL ORDER
            MoneyMarketUtils.addTakeToOrder(moneyMarketOrder, moneyMarketTakeOrder, moneyMarketOrderFile);
            new NotificationSendMessageByUserName(moneyMarketOrder.get("userName").textValue(), "Money Market", "You have new operation for order " + moneyMarketOrder.get("pair").textValue() + " " + moneyMarketOrder.get("type").textValue()).getResponse();
            //CREATE TAKE USER ORDER
            MoneyMarketUtils.createOrder(moneyMarketTakeOrder, MoneyMarketFolderLocator.getOrderFile(id));
            MoneyMarketUtils.createIndex(id, timestamp, MoneyMarketFolderLocator.getPairTypeFolder(moneyMarketOrder.get("pair").textValue(), moneyMarketTakeOrder.get("type").textValue()));
            MoneyMarketUtils.createIndex(id, timestamp, MoneyMarketFolderLocator.getUserNameFolder(moneyMarketTakeOrderRequest.getUserName()));
            //AUTOMATIC CLOSE
            leftAmount = leftAmount - moneyMarketTakeOrderRequest.getAmount();
            if (pairBaseCurrency.equals("BTC") && leftAmount <= 0.00000002 || !pairBaseCurrency.equals("BTC") && leftAmount <= 0.02) {
                ((ObjectNode) moneyMarketOrder).put("closed", true);
                FileUtil.editFile(moneyMarketOrder, moneyMarketOrderFile);
                FileUtil.moveFileToFolder(MoneyMarketFolderLocator.getUserNameIndexFile(moneyMarketOrder.get("userName").textValue(), moneyMarketOrder.get("id").textValue()), MoneyMarketFolderLocator.getUserNameOldFolder(moneyMarketOrder.get("userName").textValue()));
                FileUtil.moveFileToFolder(MoneyMarketFolderLocator.getPairTypeIndexFile(pair, moneyMarketOrder.get("type").textValue(), moneyMarketOrder.get("id").textValue()), MoneyMarketFolderLocator.getPairTypeOldFolder(pair, moneyMarketOrder.get("type").textValue()));
            }
            //PUBLISH PRICE
            publishPrice(moneyMarketOrder.get("pair").textValue(), moneyMarketTakeOrderRequest.getPrice(), moneyMarketTakeOrderRequest.getAmount(), moneyMarketTakeOrder.get("type").textValue(), moneyMarketTakeOrderRequest.getNickName());
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MoneyMarketTakeOrder.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void publishPrice(String pair, Double price, Double amount, String operationType, String nickName) {
        System.out.println("pair " + pair);
        System.out.println("amount " + amount);
        System.out.println("operationType " + operationType);
        System.out.println("nickName " + nickName);
        File pricesChatP2PFolder = PricesFolderLocator.getChatP2PFolder(pair);
        File pricesChatP2POldFolder = PricesFolderLocator.getChatP2POldFolder(pair);
        for (File pricesChatP2PFile : pricesChatP2PFolder.listFiles()) {
            if (!pricesChatP2PFile.isFile()) {
                continue;
            }
            FileUtil.moveFileToFolder(pricesChatP2PFile, pricesChatP2POldFolder);
        }
        ObjectNode prices = mapper.createObjectNode();
        prices.put("pair", pair);
        prices.put("price", price);
        prices.put("amount", amount);
        prices.put("operationType", operationType);
        prices.put("nickName", nickName);
        prices.put("time", new Date().getTime());
        FileUtil.editFile(prices, new File(pricesChatP2PFolder, DateUtil.getFileDate(null) + ".json"));
        System.out.println("prices>>>>>>>> " + prices);
        setCandleValueThread(prices);
    }

    private void setCandleValueThread(ObjectNode value) {
        Map<String, Integer> periods = new HashMap<>();
        //periods.put("1M", 60 * 60 * 24 * 30 * 1000);
        //periods.put("1W", 60 * 60 * 24 * 7 * 1000);
        //periods.put("1D", 60 * 24);
        //periods.put("12H", 60 * 12);
        periods.put("4H", 60 * 4);
        //periods.put("1H", 60);
        //periods.put("30m", 30);
        //periods.put("15m", 15);
        //periods.put("5m", 5);
        Thread setCandleValueThread = new Thread(() -> {
            String pair = value.get("pair").textValue();
            Double price = value.get("price").doubleValue();
            String timestamp = DateUtil.getDate(value.get("time").longValue());
            String startTimestamp = DateUtil.getDayStartDate(timestamp);
            for (String period : periods.keySet()) {
                String[] intervals = new String[]{startTimestamp, DateUtil.getDateMinutesAfter(startTimestamp, periods.get(period))};
                String intervalFound = null;
                while (intervalFound == null) {
                    if (timestamp.compareTo(intervals[0]) >= 0 && timestamp.compareTo(intervals[1]) < 0) {
                        intervalFound = intervals[0];
                    } else {
                        intervals = new String[]{intervals[1], DateUtil.getDateMinutesAfter(intervals[1], periods.get(period))};
                    }
                }
                File candlesChatP2PFile = new File(CandlesFolderLocator.getChatP2PFolder(pair, period), DateUtil.getFileDate(intervalFound) + ".json");
                if (!candlesChatP2PFile.isFile()) {
                    for (File candlesChatP2PFoundedFile : CandlesFolderLocator.getChatP2PFolder(pair, period).listFiles()) {
                        if (!candlesChatP2PFoundedFile.isFile()) {
                            continue;
                        }
                        FileUtil.moveFileToFolder(candlesChatP2PFoundedFile, CandlesFolderLocator.getChatP2POldFolder(pair, period));
                    }
                    ObjectNode candlesChatP2P = mapper.createObjectNode();
                    candlesChatP2P.put("shadowH", price);
                    candlesChatP2P.put("shadowL", price);
                    candlesChatP2P.put("open", price);
                    candlesChatP2P.put("close", price);
                    FileUtil.createFile(candlesChatP2P, candlesChatP2PFile);
                } else {
                    try {
                        JsonNode candlesChatP2P = mapper.readTree(candlesChatP2PFile);
                        Double shadowH = candlesChatP2P.get("shadowH").doubleValue();
                        Double shadowL = candlesChatP2P.get("shadowL").doubleValue();
                        if (shadowH < price) {
                            ((ObjectNode) candlesChatP2P).put("shadowH", price);
                        }
                        if (shadowL > price) {
                            ((ObjectNode) candlesChatP2P).put("shadowL", price);
                        }
                        ((ObjectNode) candlesChatP2P).put("close", price);
                        FileUtil.editFile(candlesChatP2P, candlesChatP2PFile);
                    } catch (IOException ex) {
                        Logger.getLogger(MoneyMarketTakeOrder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        setCandleValueThread.start();
    }

}
