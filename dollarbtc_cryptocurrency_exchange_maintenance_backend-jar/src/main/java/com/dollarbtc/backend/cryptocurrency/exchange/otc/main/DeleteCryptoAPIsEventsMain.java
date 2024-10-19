/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis.CryptoAPIsUnsubscribeAddress;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CryptoAPIsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class DeleteCryptoAPIsEventsMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> events = new ArrayList<>();
        events.add("ADDRESS_COINS_TRANSACTION_UNCONFIRMED");
        events.add("ADDRESS_TOKENS_TRANSACTION_UNCONFIRMED");
        List<String> blockchains = new ArrayList<>();
        blockchains.add("bitcoin");
        blockchains.add("ethereum");
        String hoursBeforeCurrentTimestamp = DateUtil.getDateHoursBefore(null, 3);
        for (String blockchain : blockchains) {
            for (String event : events) {
                File cryptoAPIsEventsFolder = CryptoAPIsFolderLocator.getEventSubscriptionsFolder(blockchain, "mainnet", event);
                File cryptoAPIsEventsOldFolder = CryptoAPIsFolderLocator.getEventSubscriptionsOldFolder(blockchain, "mainnet", event);
                File cryptoAPIsEventsErrorFolder = CryptoAPIsFolderLocator.getEventSubscriptionsErrorFolder(blockchain, "mainnet", event);
                for (File cryptoAPIsEventsFile : cryptoAPIsEventsFolder.listFiles()) {
                    if (!cryptoAPIsEventsFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode cryptoAPIsEvents = mapper.readTree(cryptoAPIsEventsFile);
                        String timestamp = cryptoAPIsEvents.get("timestamp").textValue();
                        if(timestamp.compareTo(hoursBeforeCurrentTimestamp) > 0){
                            continue;
                        }
                        if(cryptoAPIsEvents.has("error")){
                            FileUtil.moveFileToFolder(cryptoAPIsEventsFile, cryptoAPIsEventsErrorFolder);
                            continue;
                        }
                        if(!(cryptoAPIsEvents.has("data") && 
                                cryptoAPIsEvents.get("data").has("item") &&
                                cryptoAPIsEvents.get("data").get("item").has("referenceId"))
                                ){
                            continue;
                        }
                        String referenceId = cryptoAPIsEvents.get("data").get("item").get("referenceId").textValue();   
                        JsonNode response = new CryptoAPIsUnsubscribeAddress(referenceId, blockchain, "mainnet").getResponse();
                        if(response.has("response") && response.get("response").textValue().equals("OK")){
                            FileUtil.moveFileToFolder(cryptoAPIsEventsFile, cryptoAPIsEventsOldFolder);
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(DeleteCryptoAPIsEventsMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DeleteCryptoAPIsEventsMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

}
