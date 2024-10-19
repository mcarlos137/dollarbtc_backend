/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserCloseMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserGetMessageOffersRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserPostMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserTakeMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserCloseMessageOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetMessageOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetMessageOffersByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserPostMessageOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserTakeMessageOffer;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class RunBotMCUserMessageOfferMain {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        String currentTimestamp = DateUtil.getCurrentDate();
        File moneyclickBotsActivityFile = MoneyclickFolderLocator.getBotsActivityFile();
        JsonNode moneyclickBotsActivity = null;
        if(moneyclickBotsActivityFile.isFile()){
            try {
                moneyclickBotsActivity = MAPPER.readTree(moneyclickBotsActivityFile);
                if(moneyclickBotsActivity!= null){
                    if(moneyclickBotsActivity.has("timestamp") && moneyclickBotsActivity.get("timestamp").textValue().compareTo(DateUtil.getDateSecondsBefore(currentTimestamp, 50)) > 0){
                        System.out.println("Escaping RunBotMCUserMessageOfferMain");
                        return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(RunBotMCUserMessageOfferMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(moneyclickBotsActivity == null){
            moneyclickBotsActivity = MAPPER.createObjectNode();
        }
        System.out.println("Starting RunBotMCUserMessageOfferMain");
        Date startDate = new Date();
        File moneyclickBotsFile = MoneyclickFolderLocator.getBotsFile();
        try {
            ArrayNode moneyclickBots = (ArrayNode) MAPPER.readTree(moneyclickBotsFile);
            Iterator<JsonNode> moneyclickBotsIterator = moneyclickBots.iterator();
            while (moneyclickBotsIterator.hasNext()) {
                ((ObjectNode) moneyclickBotsActivity).put("timestamp", DateUtil.getCurrentDate());
                FileUtil.editFile(moneyclickBotsActivity, moneyclickBotsActivityFile);
                JsonNode moneyclickBotsIt = moneyclickBotsIterator.next();
                String userName = moneyclickBotsIt.get("userName").textValue();
                String nickName = moneyclickBotsIt.get("userName").textValue();
                System.out.println("userName: " + userName);
                System.out.println("nickName: " + nickName);
                //1 of 2
                if (ThreadLocalRandom.current().nextInt(100) <= 49) {
                    System.out.println("1 of 2 continue");
                    continue;
                }
                System.out.println("1 of 2 pass");
                String lastActivityTimestamp = null;
                if (moneyclickBotsIt.has("lastActivityTimestamp")) {
                    lastActivityTimestamp = moneyclickBotsIt.get("lastActivityTimestamp").textValue();
                }
//                if (lastActivityTimestamp != null && DateUtil.getDateMinutesBefore(currentTimestamp, 15).compareTo(lastActivityTimestamp) < 0) {
//                    continue;
//                }
                ArrayNode allowedPairsOfferTypes = MAPPER.createArrayNode();
                if (moneyclickBotsIt.has("allowedPairsOfferTypes")) {
                    allowedPairsOfferTypes.addAll((ArrayNode) moneyclickBotsIt.get("allowedPairsOfferTypes"));
                } else {
                    continue;
                }
                System.out.println("allowedPairsOfferTypes.size(): " + allowedPairsOfferTypes.size());
                if (allowedPairsOfferTypes.size() == 0) {
                    continue;
                }
                ((ObjectNode) moneyclickBotsIt).put("lastActivityTimestamp", currentTimestamp);
                Iterator<JsonNode> allowedPairsOfferTypesIterator = allowedPairsOfferTypes.iterator();
                while (allowedPairsOfferTypesIterator.hasNext()) {
                    String allowedPairOfferType = allowedPairsOfferTypesIterator.next().textValue();
                    String pair = allowedPairOfferType.split("__")[0];
                    OfferType offerType = OfferType.valueOf(allowedPairOfferType.split("__")[1]);
                    System.out.println("pair: " + pair);
                    System.out.println("offerType: " + offerType);
                    //1 of 2
                    if (ThreadLocalRandom.current().nextInt(100) <= 49) {
                        System.out.println("1 of 2 continue");
                        continue;
                    }
                    System.out.println("1 of 2 pass");
                    ArrayNode messageOffersByUserName = new MCUserGetMessageOffersByUserName(userName, false, pair, offerType).getResponse();
                    String baseCurrency = getCurrency(pair, true);
                    String targetCurrency = getCurrency(pair, false);
                    if (messageOffersByUserName.size() == 0) {
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
                            Double amount = ThreadLocalRandom.current().nextDouble(moneyclickBotsIt.get("minAmount").get(baseCurrency).doubleValue(), moneyclickBotsIt.get("maxAmount").get(baseCurrency).doubleValue());
                            Double price = prices.get(targetCurrency).get(offerType.name().toLowerCase()).doubleValue();
                            Double pricePercent = -1.0 * ThreadLocalRandom.current().nextDouble(0.2, 0.9);
                            if(allowedPairOfferType.split("__").length == 3){
                                pricePercent = -1.0 * ThreadLocalRandom.current().nextDouble(Double.parseDouble(allowedPairOfferType.split("__")[2].split("_")[0]), Double.parseDouble(allowedPairOfferType.split("__")[2].split("_")[1]));                                
                            }
                            if (offerType.equals(OfferType.BID)) {
                                pricePercent = pricePercent * -1;
                            }
                            price = price * (100 + pricePercent) / 100;
                            new MCUserPostMessageOffer(new MCUserPostMessageOfferRequest(userName, pair, nickName, amount, price, 10, "MINUTES", offerType, true, true)).getResponse();
                            System.out.println("POST executed");
                        } catch (IOException ex) {
                            Logger.getLogger(RunBotMCUserMessageOfferMain.class.getName()).log(Level.SEVERE, null, ex);
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
                        int randomClose = ThreadLocalRandom.current().nextInt(messageOffersByUserName.size());
                        int i = 0;
                        Iterator<JsonNode> messageOffersByUserNameIterator = messageOffersByUserName.iterator();
                        while (messageOffersByUserNameIterator.hasNext()) {
                            JsonNode messageOffersByUserNameIt = messageOffersByUserNameIterator.next();
                            i++;
                            if (randomClose + 1 != i) {
                                continue;
                            }
                            new MCUserCloseMessageOffer(new MCUserCloseMessageOfferRequest(userName, pair, messageOffersByUserNameIt.get("id").textValue(), offerType, true)).getResponse();
                            System.out.println("CLOSE executed");
                            break;
                        }
                    }
                }
                allowedPairsOfferTypesIterator = allowedPairsOfferTypes.iterator();
                while (allowedPairsOfferTypesIterator.hasNext()) {
                    String allowedPairOfferType = allowedPairsOfferTypesIterator.next().textValue();
                    System.out.println("TAKE initiated");
                    //1 of 3
                    if (ThreadLocalRandom.current().nextInt(100) <= 64) {
                        System.out.println("1 of 3 continue");
                        continue;
                    }
                    System.out.println("1 of 3 pass");
                    //TAKE BOTS MESSAGE OFFERS
                    String pair = allowedPairOfferType.split("__")[0];
                    OfferType offerType = OfferType.valueOf(allowedPairOfferType.split("__")[1]);
                    System.out.println("pair: " + pair);
                    System.out.println("offerType: " + offerType);
                    JsonNode messageOffers = new MCUserGetMessageOffers(new MCUserGetMessageOffersRequest(null, pair, null, offerType, true, false)).getResponse();
                    boolean breakLoop = false;
                    Iterator<JsonNode> messageOffersIterator = messageOffers.iterator();
                    while (messageOffersIterator.hasNext()) {
                        if (breakLoop) {
                            break;
                        }
                        JsonNode messageOffersIt = messageOffersIterator.next();
                        Iterator<JsonNode> messageOffersItIterator = messageOffersIt.iterator();
                        while (messageOffersItIterator.hasNext()) {
                            if (breakLoop) {
                                break;
                            }
                            JsonNode messageOffersItIt = messageOffersItIterator.next();
                            Iterator<JsonNode> messageOffersItItIterator = messageOffersItIt.iterator();
                            while (messageOffersItItIterator.hasNext()) {
                                JsonNode messageOffersItItIt = messageOffersItItIterator.next();
                                if (messageOffersItItIt.get("postUserName").textValue().equals(userName)) {
                                    continue;
                                }
                                boolean bot;
                                int number;
                                if (!messageOffersItItIt.has("bot") || !messageOffersItItIt.get("bot").booleanValue()) {
                                    number = 100;
                                    bot = false;
                                    System.out.println("not a BOT offer");
                                } else {
                                    number = -1;
                                    bot = true;
                                    System.out.println("BOT offer");
                                }
                                if (ThreadLocalRandom.current().nextInt(100) <= number) {
                                    continue;
                                }
                                Double amount = messageOffersItItIt.get("amount").doubleValue();
                                amount = amount * ThreadLocalRandom.current().nextDouble(0.4, 1);
                                new MCUserTakeMessageOffer(new MCUserTakeMessageOfferRequest(userName, pair, messageOffersItItIt.get("id").textValue(), nickName, amount, messageOffersItItIt.get("price").doubleValue(), offerType, bot, true)).getResponse();
                                System.out.println("TAKE executed");
                                breakLoop = true;
                                break;
                            }
                        }
                    }
                    //TAKE NORMAL MESSAGE OFFERS
                }
            }
            FileUtil.editFile(moneyclickBots, moneyclickBotsFile);
        } catch (IOException ex) {
            Logger.getLogger(RunBotMCUserMessageOfferMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        long timeInSeconds = (new Date().getTime() - startDate.getTime()) / 1000;
        System.out.println("timeInSeconds: " + timeInSeconds);
        System.out.println("Finishing RunBotMCUserMessageOfferMain");
    }

    private static String getCurrency(String pair, Boolean base) throws IOException {
        JsonNode pairs = MAPPER.readTree(MoneyclickFolderLocator.getPairsFile());
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
