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
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;

/**
 *
 * @author CarlosDaniel
 */
public class CancelOperationsMain {

    public static void main(String[] args) {
        File otcOperationsIndexesSpecificFolder = OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Statuses");
        if (!otcOperationsIndexesSpecificFolder.isDirectory()) {
            return;
        }
        File otcOperationsIndexesBuyFolder = new File(OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Types"), "BUY");
        if (!otcOperationsIndexesBuyFolder.isDirectory()) {
            return;
        }
        for (File otcOperationsIndexesSpecificValueFolder : otcOperationsIndexesSpecificFolder.listFiles()) {
            if (!otcOperationsIndexesSpecificValueFolder.isDirectory()) {
                continue;
            }
            if (otcOperationsIndexesSpecificValueFolder.getName().equals("WAITING_FOR_PAYMENT")) {
                for (File otcOperationIdIndexFile : otcOperationsIndexesSpecificValueFolder.listFiles()) {
                    if (!otcOperationIdIndexFile.isFile()) {
                        continue;
                    }
                    if (!new File(otcOperationsIndexesBuyFolder, otcOperationIdIndexFile.getName()).isFile()) {
                        continue;
                    }
                    String id = otcOperationIdIndexFile.getName().replace(".json", "");
                    JsonNode otcOperation = new OTCGetOperation(id).getResponse();
                    if (otcOperation.has("minutesLeft") && otcOperation.get("minutesLeft").intValue() == 0) {
                        System.out.println("Cancelling operation id: " + id);
                        new OTCChangeOperationStatus(new OTCChangeOperationStatusRequest(id, OTCOperationStatus.CANCELED, false)).getResponse();
                    }
                }
            }
        }
    }

}
