/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OperationMessageSide;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

/**
 *
 * @author CarlosDaniel
 */
public class SendBuyOperationNotificationsMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Date currentDateMinus10Minutes = DateUtil.parseDate(DateUtil.getDateMinutesBefore(DateUtil.getCurrentDate(), 10));
        Date currentDateMinus90Minutes = DateUtil.parseDate(DateUtil.getDateMinutesBefore(DateUtil.getCurrentDate(), 90));
        File otcOperationsIndexesSpecificFolder = OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Statuses");
        if (!otcOperationsIndexesSpecificFolder.isDirectory()) {
            return;
        }
        File otcOperationsIndexesBuyFolder = new File(OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Types"), "BUY");
        if (!otcOperationsIndexesBuyFolder.isDirectory()) {
            return;
        }
        for (File otcOperationsIndexesSpecificValueFolder : otcOperationsIndexesSpecificFolder.listFiles()) {
            if (!otcOperationsIndexesSpecificValueFolder.isDirectory()
                    || otcOperationsIndexesSpecificValueFolder.getName().equals("CANCELED")
                    || otcOperationsIndexesSpecificValueFolder.getName().equals("SUCCESS")) {
                continue;
            }
            for (File otcOperationIdIndexFile : otcOperationsIndexesSpecificValueFolder.listFiles()) {
                if (!otcOperationIdIndexFile.isFile()) {
                    continue;
                }
                if(!new File(otcOperationsIndexesBuyFolder, otcOperationIdIndexFile.getName()).isFile()){
                    continue;
                }
                String id = otcOperationIdIndexFile.getName().replace(".json", "");
                try {
                    JsonNode otcOperationIdIndex = mapper.readTree(otcOperationIdIndexFile);
                    Date date = DateUtil.parseDate(otcOperationIdIndex.get("timestamp").textValue());
                    boolean change = false;
                    if (!otcOperationIdIndex.has("10MinutesNotification") || !otcOperationIdIndex.get("10MinutesNotification").booleanValue()) {
                        if (date.before(currentDateMinus10Minutes)) {
                            String userName = mapper.readTree(OTCFolderLocator.getOperationIdFile(null, id)).get("userName").textValue();
                            BaseOperation.postOperationMessage(id, userName, "OPERATION 10 MINUTES LEFT", OperationMessageSide.BOTH, null);
                            new UserPostMessage(userName, "OPERATION 10 MINUTES LEFT", "buy?id=" + id).getResponse();
                            ((ObjectNode) otcOperationIdIndex).put("10MinutesNotification", true);
                            change = true;
                        }
                    }
                    if (!change) {
                        if (!otcOperationIdIndex.has("90MinutesNotification") || !otcOperationIdIndex.get("90MinutesNotification").booleanValue()) {
                            if (date.before(currentDateMinus90Minutes)) {
                                String userName = mapper.readTree(OTCFolderLocator.getOperationIdFile(null, id)).get("userName").textValue();
                                BaseOperation.postOperationMessage(id, userName, "OPERATION TIMEOUT", OperationMessageSide.BOTH, null);
                                new UserPostMessage(userName, "OPERATION TIMEOUT", "buy?id=" + id).getResponse();
                                ((ObjectNode) otcOperationIdIndex).put("90MinutesNotification", true);
                                change = true;
                            }
                        }
                    }
                    if (change) {
                        FileUtil.editFile(otcOperationIdIndex, otcOperationIdIndexFile);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SendBuyOperationNotificationsMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
