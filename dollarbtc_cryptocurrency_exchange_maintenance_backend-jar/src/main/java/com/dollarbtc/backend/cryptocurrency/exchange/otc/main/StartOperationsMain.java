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

/**
 *
 * @author CarlosDaniel
 */
public class StartOperationsMain {

    public static void main(String[] args) {
        File otcOperationsIndexesSpecificFolder = OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Statuses");
        if (!otcOperationsIndexesSpecificFolder.isDirectory()) {
            return;
        }
        String[] otcOperationTypes = new String[]{"SELL", "SEND_TO_PAYMENT"};
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
                        JsonNode otcOperation = new OTCGetOperation(id).getResponse();
//                        if(!otcOperation.has("receiverAccepted")){
//                            continue;
//                        }
//                        if(!otcOperation.get("receiverAccepted").booleanValue()){
//                            continue;
//                        }
                        String timestamp = otcOperation.get("timestamp").textValue();
                        Date currentDateMinus30Minutes = DateUtil.parseDate(DateUtil.getDateMinutesBefore(DateUtil.getCurrentDate(), 1));
                        Date operationDate = DateUtil.parseDate(timestamp);
                        if (operationDate.before(currentDateMinus30Minutes)) {
                            System.out.println("Waiting for payment operation id: " + id);
                            new OTCChangeOperationStatus(new OTCChangeOperationStatusRequest(id, OTCOperationStatus.WAITING_FOR_PAYMENT, false)).getResponse();
                        }
                    }
                }
            }
        }

    }

}
