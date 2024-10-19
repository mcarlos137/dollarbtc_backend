/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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
public class SetCryptoBuysAvailableMain {

    public static void main(String[] args) {
        System.out.println("Starting SetCryptoBuysAvailableMain");
        ObjectMapper mapper = new ObjectMapper();
        String currentTimestamp = DateUtil.getCurrentDate();
        File moneyclickCryptoBuysFolder = MoneyclickFolderLocator.getCryptoBuysFolder();
        File moneyclickCryptoBuysOldFolder = MoneyclickFolderLocator.getCryptoBuysOldFolder();
        for (File moneyclickCryptoBuyFile : moneyclickCryptoBuysFolder.listFiles()) {
            if (!moneyclickCryptoBuyFile.isFile()) {
                continue;
            }
            try {
                JsonNode moneyclickCryptoBuy = mapper.readTree(moneyclickCryptoBuyFile);
                String operationId = moneyclickCryptoBuy.get("operationId").textValue();
                String userName = moneyclickCryptoBuy.get("userName").textValue();
                String timestamp = moneyclickCryptoBuy.get("timestamp").textValue();
                String cryptoCurrency = null;
                Double cryptoAmount = null;
                if (moneyclickCryptoBuy.has("cryptoCurrency")) {
                    cryptoCurrency = moneyclickCryptoBuy.get("cryptoCurrency").textValue();
                }
                if (moneyclickCryptoBuy.has("cryptoAmount")) {
                    cryptoAmount = moneyclickCryptoBuy.get("cryptoAmount").doubleValue();
                }
                System.out.println("------------------------------");
                System.out.println("operationId " + operationId);
                System.out.println("userName " + userName);
                System.out.println("timestamp " + timestamp);
                System.out.println("cryptoCurrency " + cryptoCurrency);
                System.out.println("cryptoAmount " + cryptoAmount);
                int time = 360;
                if (moneyclickCryptoBuy.has("type") && moneyclickCryptoBuy.get("type").textValue().equals("GIFTCARD")) {
                    time = 1;
                }
                if (timestamp.compareTo(DateUtil.getDateMinutesBefore(currentTimestamp, time)) < 0) {
                    System.out.println("changing to OK");
                    BaseOperation.changeBalanceOperationStatus(UsersFolderLocator.getMCBalanceFolder(userName), BalanceOperationStatus.OK, operationId, "operationId", null);
                    System.out.println("moving to OLD");
                    FileUtil.moveFileToFolder(moneyclickCryptoBuyFile, moneyclickCryptoBuysOldFolder);
//                    new NotificationSendMessageByUserName(userName, "Transaction information", "").getResponse();
                }
                System.out.println("------------------------------");
            } catch (IOException ex) {
                Logger.getLogger(SetCryptoBuysAvailableMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Finishing SetCryptoBuysAvailableMain");
    }

}
