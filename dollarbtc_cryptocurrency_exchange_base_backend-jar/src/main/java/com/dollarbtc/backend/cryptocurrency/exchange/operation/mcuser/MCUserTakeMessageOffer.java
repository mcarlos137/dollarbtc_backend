/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserTakeMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CandlesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.PricesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
public class MCUserTakeMessageOffer extends AbstractOperation<String> {

    private final MCUserTakeMessageOfferRequest mcUserTakeMessageOfferRequest;

    public MCUserTakeMessageOffer(MCUserTakeMessageOfferRequest mcUserTakeMessageOfferRequest) {
        super(String.class);
        this.mcUserTakeMessageOfferRequest = mcUserTakeMessageOfferRequest;
    }

    @Override
    protected void execute() {
        File moneyclickMessegeOfferPairTypeIdFile = MoneyclickFolderLocator.getMessageOfferPairTypeIdFile(mcUserTakeMessageOfferRequest.getPair(), mcUserTakeMessageOfferRequest.getType().name(), mcUserTakeMessageOfferRequest.getId());
        if (!moneyclickMessegeOfferPairTypeIdFile.isFile()) {
            super.response = "MESSAGE OFFER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode messageOffer = mapper.readTree(moneyclickMessegeOfferPairTypeIdFile);
            if (messageOffer.get("postUserName").textValue().equals(mcUserTakeMessageOfferRequest.getUserName())) {
                super.response = "SAME USER CAN NOT TAKE ITS OWN OFFER";
                return;
            }
            if (messageOffer.get("price").doubleValue() != mcUserTakeMessageOfferRequest.getPrice()) {
                super.response = "MESSAGE OFFER PRICE CHANGE";
                return;
            }
            String pairBaseCurrency = getCurrency(mcUserTakeMessageOfferRequest.getPair(), true);
            String pairQuoteCurrency = getCurrency(mcUserTakeMessageOfferRequest.getPair(), false);
            String timestamp = DateUtil.getCurrentDate();
            Double leftAmount = messageOffer.get("amount").doubleValue();
            if (messageOffer.has("takeUserNames")) {
                Iterator<JsonNode> messageOfferTakeUserNamesIterator = messageOffer.get("takeUserNames").iterator();
                while (messageOfferTakeUserNamesIterator.hasNext()) {
                    JsonNode messageOfferTakeUserNamesIt = messageOfferTakeUserNamesIterator.next();
                    leftAmount = leftAmount - messageOfferTakeUserNamesIt.get("amount").doubleValue();
                }
                if (leftAmount < mcUserTakeMessageOfferRequest.getAmount()) {
                    super.response = "AMOUNT EXCEEDS OFFER AMOUNT";
                    return;
                }
            }
            boolean postUserBot = false;
            if (messageOffer.has("bot")) {
                postUserBot = messageOffer.get("bot").booleanValue();
            }
            boolean takeUserBot = mcUserTakeMessageOfferRequest.isBot();
            if (OfferType.valueOf(messageOffer.get("type").textValue()).equals(OfferType.ASK)) {
                //TAKE USER BUY - SUBSTRACT QUOTE
                if (!takeUserBot) {
                    String substractToBalance = BaseOperation.substractToBalance(
                            UsersFolderLocator.getMCBalanceFolder(mcUserTakeMessageOfferRequest.getUserName()), //TAKE USER
                            pairQuoteCurrency, //USD
                            mcUserTakeMessageOfferRequest.getAmount() * messageOffer.get("price").doubleValue(), //1 * 52000
                            BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
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
                if (!postUserBot) {
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(messageOffer.get("postUserName").textValue()), //POST USER
                            pairQuoteCurrency, //USD
                            mcUserTakeMessageOfferRequest.getAmount() * messageOffer.get("price").doubleValue(), //1 * 52000
                            BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            null,
                            false,
                            null
                    );
                }
                //TAKE USER BUY - ADD BASE
                if (!takeUserBot) {
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(mcUserTakeMessageOfferRequest.getUserName()), //TAKE USER
                            pairBaseCurrency, //BTC
                            mcUserTakeMessageOfferRequest.getAmount(), //1
                            BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
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
                if (!takeUserBot) {
                    String substractToBalance = BaseOperation.substractToBalance(
                            UsersFolderLocator.getMCBalanceFolder(mcUserTakeMessageOfferRequest.getUserName()), //TAKE USER
                            pairBaseCurrency, //BTC
                            mcUserTakeMessageOfferRequest.getAmount(), //1
                            BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
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
                if (!postUserBot) {
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(messageOffer.get("postUserName").textValue()), //POST USER
                            pairBaseCurrency, //BTC
                            mcUserTakeMessageOfferRequest.getAmount(), //1
                            BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            null,
                            false,
                            null
                    );
                }
                //TAKE USER BUY - ADD QUOTE
                if (!takeUserBot) {
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(mcUserTakeMessageOfferRequest.getUserName()), //TAKE USER
                            pairQuoteCurrency, //USD
                            mcUserTakeMessageOfferRequest.getAmount() * messageOffer.get("price").doubleValue(), //1 * 48000
                            BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            null,
                            false,
                            null
                    );
                }
            }
            if (!messageOffer.has("takeUserNames")) {
                ((ObjectNode) messageOffer).set("takeUserNames", mapper.createArrayNode());
            }
            ObjectNode takeUserName = mapper.createObjectNode();
            takeUserName.put("userName", mcUserTakeMessageOfferRequest.getUserName());
            takeUserName.put("nickName", mcUserTakeMessageOfferRequest.getNickName());
            takeUserName.put("timestamp", timestamp);
            takeUserName.put("amount", mcUserTakeMessageOfferRequest.getAmount());
            ((ArrayNode) messageOffer.get("takeUserNames")).add(takeUserName);
            FileUtil.editFile(messageOffer, moneyclickMessegeOfferPairTypeIdFile);
            leftAmount = leftAmount - mcUserTakeMessageOfferRequest.getAmount();

            if (pairBaseCurrency.equals("BTC") && leftAmount <= 0.00000002 || !pairBaseCurrency.equals("BTC") && leftAmount <= 0.02) {
                ((ObjectNode) messageOffer).put("closed", true);
                FileUtil.editFile(messageOffer, moneyclickMessegeOfferPairTypeIdFile);
                File moneyclickMessegeOfferPairTypeOldFolder = MoneyclickFolderLocator.getMessageOfferPairTypeOldFolder(mcUserTakeMessageOfferRequest.getPair(), mcUserTakeMessageOfferRequest.getType().name());
                FileUtil.moveFileToFolder(moneyclickMessegeOfferPairTypeIdFile, moneyclickMessegeOfferPairTypeOldFolder);
            }
            if (!postUserBot && !mcUserTakeMessageOfferRequest.isExcludeMessage()) {
                postMessage(messageOffer.get("postUserName").textValue(), mcUserTakeMessageOfferRequest.getPair() + "__" + mcUserTakeMessageOfferRequest.getType().name() + "__" + mcUserTakeMessageOfferRequest.getId(), messageOffer, timestamp);
            }
            String operationType = "BUY";
            if (mcUserTakeMessageOfferRequest.getType().equals(OfferType.BID)) {
                operationType = "SELL";
            }
            publishPrice(mcUserTakeMessageOfferRequest.getPair(), messageOffer.get("price").doubleValue(), mcUserTakeMessageOfferRequest.getAmount(), operationType, mcUserTakeMessageOfferRequest.getNickName());
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCUserTakeMessageOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void postMessage(String userName, String chatRoom, JsonNode messageOffer, String timestamp) {
        File mcUserMessagesFolder = UsersFolderLocator.getMCMessagesFolder(userName, chatRoom);
        ObjectNode mcUserMessage = mapper.createObjectNode();
        String fileName = DateUtil.getFileDate(timestamp) + ".json";
        mcUserMessage.put("id", timestamp);
        mcUserMessage.put("senderUserName", userName);
        mcUserMessage.put("chatRoom", chatRoom);
        mcUserMessage.put("timestamp", timestamp);
        mcUserMessage.set("offer", messageOffer);
        int version = 0;
        if (messageOffer.has("version")) {
            version = messageOffer.get("version").intValue();
        }
        mcUserMessage.put("version", version + 1);
        File mcMessageFile = new File(mcUserMessagesFolder, fileName);
        FileUtil.createFile(mcUserMessage, mcMessageFile);
        createDeliveredThread(userName, mcMessageFile);
    }

    private void publishPrice(String pair, Double price, Double amount, String operationType, String nickName) {
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
        setCandleValueThread(prices);
    }

    private String getCurrency(String pair, Boolean base) throws IOException {
        JsonNode pairs = mapper.readTree(MoneyclickFolderLocator.getPairsFile());
        if (pairs.has(pair)) {
            if (base) {
                return pairs.get(pair).get("base").textValue();
            } else {
                return pairs.get(pair).get("quote").textValue();
            }
        }
        throw new IOException("PAIR DOES NOT EXIST");
    }

    private void createDeliveredThread(String userName, File mcMessageFile) {
        Thread createDeliveredThread = new Thread(() -> {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MCUserTakeMessageOffer.class.getName()).log(Level.SEVERE, null, ex);
            }
            File usersMCMessagesPendingToDeliverFile = UsersFolderLocator.getMCMessagesPendingToDeliverFile(userName);
            if (mcMessageFile.isFile()) {
                if (!usersMCMessagesPendingToDeliverFile.isFile()) {
                    ObjectNode pendingToDeliver = mapper.createObjectNode();
                    pendingToDeliver.put("notificationSended", true);
                    pendingToDeliver.put("timestamp", DateUtil.getCurrentDate());
                    FileUtil.editFile(pendingToDeliver, usersMCMessagesPendingToDeliverFile);
                    //SEND NOTIFICATION
                    new NotificationSendMessageByUserName(userName, "Chat P2P", "You have new messages").getResponse();
                } else {
                    try {
                        JsonNode pendingToDeliver = mapper.readTree(usersMCMessagesPendingToDeliverFile);
                        String currentTimestamp = DateUtil.getCurrentDate();
                        if (pendingToDeliver.has("notificationSended") && !pendingToDeliver.get("notificationSended").booleanValue()
                                || pendingToDeliver.has("timestamp") && DateUtil.getDateMinutesBefore(currentTimestamp, 10).compareTo(pendingToDeliver.get("timestamp").textValue()) > 0) {
                            ((ObjectNode) pendingToDeliver).put("notificationSended", true);
                            ((ObjectNode) pendingToDeliver).put("timestamp", currentTimestamp);
                            FileUtil.editFile(pendingToDeliver, usersMCMessagesPendingToDeliverFile);
                            //SEND NOTIFICATION
                            new NotificationSendMessageByUserName(userName, "Chat P2P", "You have new messages").getResponse();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MCUserTakeMessageOffer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        createDeliveredThread.start();
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
                        Logger.getLogger(MCUserTakeMessageOffer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        setCandleValueThread.start();
    }

}
