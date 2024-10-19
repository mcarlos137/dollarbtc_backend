/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketCloseOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket.MoneyMarketCloseOrder;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class CloseMoneyMarketOrdersMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Starting MoneyMarketCloseOrderRequest");
        String currentTimestamp = DateUtil.getCurrentDate();
        File moneyMarketFolder = MoneyMarketFolderLocator.getFolder();
        for (File moneyMarketPairFolder : moneyMarketFolder.listFiles()) {
            if (!moneyMarketPairFolder.isDirectory()) {
                continue;
            }
            System.out.println(moneyMarketPairFolder.getName());
            for (File moneyMarketPairTypeFolder : moneyMarketPairFolder.listFiles()) {
                if (!moneyMarketPairTypeFolder.isDirectory()) {
                    continue;
                }
                System.out.println(moneyMarketPairTypeFolder.getName());
                for (File moneyMarketPairTypeIndexFile : moneyMarketPairTypeFolder.listFiles()) {
                    if (!moneyMarketPairTypeIndexFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode moneyMarketPairTypeIndex = mapper.readTree(moneyMarketPairTypeIndexFile);
                        if(moneyMarketPairTypeIndex == null){
                            continue;
                        }
                        String id = moneyMarketPairTypeIndex.get("id").textValue();
                        File moneyMarketOrderFile = MoneyMarketFolderLocator.getOrderFile(id);
                        JsonNode moneyMarketOrder = mapper.readTree(moneyMarketOrderFile);
                        String userName = moneyMarketOrder.get("userName").textValue();
                        String timestamp = moneyMarketOrder.get("timestamp").textValue();
                        Integer time = moneyMarketOrder.get("time").intValue();
                        String timeUnit = moneyMarketOrder.get("timeUnit").textValue();
                        String beforeTimestamp = null;
                        switch(timeUnit){
                            case "MINUTES":
                                beforeTimestamp = DateUtil.getDateMinutesBefore(currentTimestamp, time);
                                break;
                            case "HOURS":
                                beforeTimestamp = DateUtil.getDateHoursBefore(currentTimestamp, time);
                                break;
                            case "DAYS":
                                beforeTimestamp = DateUtil.getDateDaysBefore(currentTimestamp, time);
                                break;
                        }
                        System.out.println("beforeTimestamp " + beforeTimestamp);
                        System.out.println("timestamp " + timestamp);
                        if(beforeTimestamp != null && beforeTimestamp.compareTo(timestamp) > 0){
                            System.out.println("CLOSING ORDER beforeTimestamp " + id);
                            new MoneyMarketCloseOrder(new MoneyMarketCloseOrderRequest(userName, id, true)).getResponse();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(CloseMoneyMarketOrdersMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        System.out.println("Finishing MoneyMarketCloseOrderRequest");
    }

}
