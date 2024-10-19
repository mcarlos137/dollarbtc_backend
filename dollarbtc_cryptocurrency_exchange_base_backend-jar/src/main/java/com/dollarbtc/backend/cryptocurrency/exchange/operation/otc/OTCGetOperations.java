/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetOperations extends AbstractOperation<ArrayNode> {

    private final OTCGetOperationsRequest otcGetOperationsRequest;

    public OTCGetOperations(OTCGetOperationsRequest otcGetOperationsRequest) {
        super(ArrayNode.class);
        this.otcGetOperationsRequest = otcGetOperationsRequest;
    }

    @Override
    public void execute() {
        ArrayNode operations = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("UserNames");
        indexes.add("Currencies");
        indexes.add("Types");
        indexes.add("Statuses");
        if (otcGetOperationsRequest.getSpecialIndexes() != null && !otcGetOperationsRequest.getSpecialIndexes().isEmpty()) {
            for (String key : otcGetOperationsRequest.getSpecialIndexes().keySet()) {
                indexes.add(key);
            }
        }
        boolean firstLoop = true;
        for (String index : indexes) {
            String i = null;
            switch (index) {
                case "UserNames":
                    if (otcGetOperationsRequest.getUserName() != null && !otcGetOperationsRequest.getUserName().equals("")) {
                        i = otcGetOperationsRequest.getUserName();
                    }
                    break;
                case "Currencies":
                    if (otcGetOperationsRequest.getCurrency() != null && !otcGetOperationsRequest.getCurrency().equals("")) {
                        i = otcGetOperationsRequest.getCurrency();
                    }
                    break;
                case "Types":
                    if (otcGetOperationsRequest.getOtcOperationType() != null) {
                        i = otcGetOperationsRequest.getOtcOperationType().name();
                    }
                    break;
                case "Statuses":
                    if (otcGetOperationsRequest.getOtcOperationStatus() != null) {
                        i = otcGetOperationsRequest.getOtcOperationStatus().name();
                    }
                    break;
                default:
                    if (otcGetOperationsRequest.getSpecialIndexes().get(index) != null && !otcGetOperationsRequest.getSpecialIndexes().get(index).equals("")) {
                        i = otcGetOperationsRequest.getSpecialIndexes().get(index);
                    }
                    break;
            }
            File otcOperationsIndexesFolder = OTCFolderLocator.getOperationsIndexesSpecificFolder(null, index);
            if (firstLoop) {
                if (i != null && !i.equals("")) {
                    File otcOperationsIndexFolder = new File(otcOperationsIndexesFolder, i);
                    if (!otcOperationsIndexFolder.isDirectory()) {
                        super.response = operations;
                        return;
                    }
                    for (File idFiles : otcOperationsIndexFolder.listFiles()) {
                        ids.add(idFiles.getName().replace(".json", ""));
                    }
                } else {
                    for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
                        if (!otcOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : otcOperationsIndexFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                }
                firstLoop = false;
            } else {
                Set<String> newIds = new HashSet<>();
                if (i != null && !i.equals("")) {
                    File otcOperationsIndexFolder = new File(otcOperationsIndexesFolder, i);
                    if (!otcOperationsIndexFolder.isDirectory()) {
                        super.response = operations;
                        return;
                    }
                    for (String id : ids) {
                        if (new File(otcOperationsIndexFolder, id + ".json").isFile()) {
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
                        if (!otcOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (new File(otcOperationsIndexFolder, id + ".json").isFile()) {
                                newIds.add(id);
                            }
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get operations
        if (otcGetOperationsRequest.getBrokerUserName() != null && !otcGetOperationsRequest.getBrokerUserName().equals("")) {
            File otcOperationsIndexesFolder = OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "BrokerUserNames");
            Set<String> newIds = new HashSet<>();
            for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
                if (!otcOperationsIndexFolder.isDirectory()) {
                    continue;
                }
                for (String id : ids) {
                    if (new File(otcOperationsIndexFolder, id + ".json").isFile()) {
                        newIds.add(id);
                    }
                }
            }
            ids.retainAll(newIds);
        }
        for (String id : ids) {
            File otcOperationIdFolder = OTCFolderLocator.getOperationIdFolder(null, id);
            File operationFile = new File(otcOperationIdFolder, "operation.json");
            try {
                JsonNode operation = mapper.readTree(operationFile);
                String timestamp = operation.get("timestamp").textValue();
                otcGetOperationsRequest.setFinalTimestamp(DateUtil.getDateDaysAfter(DateUtil.getDayStartDate(otcGetOperationsRequest.getFinalTimestamp()), 1));
                if (otcGetOperationsRequest.getInitTimestamp() != null && timestamp.compareTo(otcGetOperationsRequest.getInitTimestamp()) < 0) {
                    continue;
                }
                if (otcGetOperationsRequest.getFinalTimestamp() != null && timestamp.compareTo(otcGetOperationsRequest.getFinalTimestamp()) >= 0) {
                    continue;
                }
                operations.add(operation);
            } catch (IOException ex) {
                Logger.getLogger(OTCGetOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = operations;
    }

}
