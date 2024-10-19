/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperations;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class DeleteOperationsForDeletedUsersMain {

    public static void main(String[] args) {
        OTCOperationStatus[] otcOperationStatuses = new OTCOperationStatus[]{
            OTCOperationStatus.CLAIM,
            OTCOperationStatus.PAY_VERIFICATION,
            OTCOperationStatus.WAITING_FOR_PAYMENT,
            OTCOperationStatus.WAITING_TO_START_OPERATION
        };
        for (OTCOperationStatus otcOperationStatus : otcOperationStatuses) {
            ArrayNode otcOperations = new OTCGetOperations(
                    new OTCGetOperationsRequest(
                            null,
                            null,
                            null,
                            null,
                            otcOperationStatus,
                            null)).getResponse();
            Iterator<JsonNode> otcOperationsIterator = otcOperations.iterator();
            while (otcOperationsIterator.hasNext()) {
                JsonNode otcOperationsIt = otcOperationsIterator.next();
                String userName = otcOperationsIt.get("userName").textValue();
                String currency = otcOperationsIt.get("currency").textValue();
                String otcOperationType = otcOperationsIt.get("otcOperationType").textValue();
                String id = otcOperationsIt.get("id").textValue();
                if (!new File(new File(new File(new File(UsersFolderLocator.getFolder(userName), "OTC"), currency), otcOperationType), id + ".json").isFile()) {
                    Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "---------------------------------------------");
                    Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "userName {0}", userName);
                    Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "currency {0}", currency);
                    Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "otcOperationType {0}", otcOperationType);
                    Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "id {0}", id);
                    Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "otcOperationStatus {0}", otcOperationStatus);
                    File otcOperationIdFolder = OTCFolderLocator.getOperationIdFolder(null, id);
                    if (otcOperationIdFolder.isDirectory()) {
                        Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "otcOperationIdFolder {0}", otcOperationIdFolder);
                        FileUtil.deleteFolder(otcOperationIdFolder);
                    }
                    File otcOperationsIndexesFolder = OTCFolderLocator.getOperationsIndexesFolder(null);
                    for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
                        if (!otcOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (File otcOperationsIndexValueFolder : otcOperationsIndexFolder.listFiles()) {
                            if (!otcOperationsIndexValueFolder.isDirectory()) {
                                continue;
                            }
                            File otcOperationsIndexValueFile = new File(otcOperationsIndexValueFolder, id + ".json");
                            if (otcOperationsIndexValueFile.isFile()) {
                                Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "otcOperationsIndexValueFile {0}", otcOperationsIndexValueFile);
                                FileUtil.deleteFile(otcOperationsIndexValueFile);
                            }
                        }
                    }
                    Logger.getLogger(DeleteOperationsForDeletedUsersMain.class.getName()).log(Level.INFO, "---------------------------------------------");
                }
            }
        }
    }

}
