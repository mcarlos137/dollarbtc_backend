/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class StartOperationsSendToPaymentMain {
    
    public static void main(String[] args) {
        File otcOperationsIndexesSpecificFolder = OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Statuses");
        if (!otcOperationsIndexesSpecificFolder.isDirectory()) {
            return;
        }
        String[] otcOperationTypes = new String[]{"SEND_TO_PAYMENT"};
        for (String otcOperationType : otcOperationTypes) {
            File otcOperationsIndexesSellFolder = new File(OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Types"), otcOperationType);
            if (!otcOperationsIndexesSellFolder.isDirectory()) {
                return;
            }
            for (File otcOperationsIndexesSpecificValueFolder : otcOperationsIndexesSpecificFolder.listFiles()) {
                if (!otcOperationsIndexesSpecificValueFolder.isDirectory()) {
                    continue;
                }
                if (otcOperationsIndexesSpecificValueFolder.getName().equals("WAITING_TO_START_OPERATION")) {
                    for (File otcOperationIdIndexFile : otcOperationsIndexesSpecificValueFolder.listFiles()) {
                        if (!otcOperationIdIndexFile.isFile()) {
                            continue;
                        }
                        if (!new File(otcOperationsIndexesSellFolder, otcOperationIdIndexFile.getName()).isFile()) {
                            continue;
                        }
                        String id = otcOperationIdIndexFile.getName().replace(".json", "");
                        Logger.getLogger(StartOperationsSendToPaymentMain.class.getName()).log(Level.INFO, "id: {0}", id);
                        JsonNode otcOperation = new OTCGetOperation(id).getResponse();
                        String timestamp = otcOperation.get("timestamp").textValue();
                        Logger.getLogger(StartOperationsSendToPaymentMain.class.getName()).log(Level.INFO, "timestamp: {0}", timestamp);
                        Date currentDateMinus1Minute = DateUtil.parseDate(DateUtil.getDateMinutesBefore(DateUtil.getCurrentDate(), 1));
                        Date operationDate = DateUtil.parseDate(timestamp);
                        if (operationDate.before(currentDateMinus1Minute)) {
                            Logger.getLogger(StartOperationsSendToPaymentMain.class.getName()).log(Level.INFO, "Waiting for payment operation id: {0}", id);
                            OTCChangeOperationStatusRequest otcChangeOperationStatusRequest = new OTCChangeOperationStatusRequest(id, OTCOperationStatus.WAITING_FOR_PAYMENT, false);
                            String result = new OTCChangeOperationStatus(otcChangeOperationStatusRequest).getResponse();
                            Logger.getLogger(StartOperationsSendToPaymentMain.class.getName()).log(Level.INFO, "result1: {0}", result);
                            if (!result.equals("OK")) {
                                otcChangeOperationStatusRequest.setOtcOperationStatus(OTCOperationStatus.CANCELED);
                                otcChangeOperationStatusRequest.setCanceledReason(result);
                                result = new OTCChangeOperationStatus(otcChangeOperationStatusRequest).getResponse();   
                                Logger.getLogger(StartOperationsSendToPaymentMain.class.getName()).log(Level.INFO, "result2: {0}", result);
                            }
                        }
                    }
                }
            }
        }
        
    }
    
}
