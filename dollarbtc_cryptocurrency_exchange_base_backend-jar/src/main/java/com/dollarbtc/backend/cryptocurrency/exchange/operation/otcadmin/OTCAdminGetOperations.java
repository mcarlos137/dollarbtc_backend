/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
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
public class OTCAdminGetOperations extends AbstractOperation<ArrayNode> {

    private final OTCAdminGetOperationsRequest otcAdminGetOperationsRequest;

    public OTCAdminGetOperations(OTCAdminGetOperationsRequest otcAdminGetOperationsRequest) {
        super(ArrayNode.class);
        this.otcAdminGetOperationsRequest = otcAdminGetOperationsRequest;
    }

    @Override
    protected void execute() {
        ArrayNode operations = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> userCurrencies = new UserGetCurrencies(otcAdminGetOperationsRequest.getUserName()).getResponse();
        Set<String> indexes = new HashSet<>();
        indexes.add("UserNames");
        indexes.add("Currencies");
        indexes.add("Types");
        indexes.add("Statuses");
        if (otcAdminGetOperationsRequest.getSpecialIndexes() != null && !otcAdminGetOperationsRequest.getSpecialIndexes().isEmpty()) {
            otcAdminGetOperationsRequest.getSpecialIndexes().keySet().stream().forEach((key) -> {
                indexes.add(key);
            });
        }
        boolean firstLoop = true;
        for (String index : indexes) {
            String i = null;
            switch (index) {
                case "UserNames":
                    i = otcAdminGetOperationsRequest.getOperationUserName();
                    break;
                case "Currencies":
                    i = otcAdminGetOperationsRequest.getCurrency();
                    break;
                case "Types":
                    if (otcAdminGetOperationsRequest.getOtcOperationType() != null) {
                        i = otcAdminGetOperationsRequest.getOtcOperationType().name();
                    }
                    break;
                case "Statuses":
                    if (otcAdminGetOperationsRequest.getOtcOperationStatus() != null) {
                        i = otcAdminGetOperationsRequest.getOtcOperationStatus().name();
                    }
                    break;
                default:
                    if (otcAdminGetOperationsRequest.getSpecialIndexes().get(index) != null && !otcAdminGetOperationsRequest.getSpecialIndexes().get(index).equals("")) {
                        i = otcAdminGetOperationsRequest.getSpecialIndexes().get(index);
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
                        if (index.equals("Currencies")) {
                            if (!userCurrencies.contains(i)) {
                                continue;
                            }
                        }
                        ids.add(idFiles.getName().replace(".json", ""));
                    }
                } else {
                    for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
                        if (!otcOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : otcOperationsIndexFolder.listFiles()) {
                            if (index.equals("Currencies")) {
                                if (!userCurrencies.contains(otcOperationsIndexFolder.getName())) {
                                    continue;
                                }
                            }
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
                        if (!new File(otcOperationsIndexFolder, id + ".json").isFile()) {
                            continue;
                        }
                        if (index.equals("Currencies")) {
                            if (!userCurrencies.contains(i)) {
                                continue;
                            }
                        }
                        newIds.add(id);
                    }
                } else {
                    for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
                        if (!otcOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (!new File(otcOperationsIndexFolder, id + ".json").isFile()) {
                                continue;
                            }
                            if (index.equals("Currencies")) {
                                if (!userCurrencies.contains(otcOperationsIndexFolder.getName())) {
                                    continue;
                                }
                            }
                            newIds.add(id);
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get operations
        ids.stream().map((id) -> OTCFolderLocator.getOperationIdFolder(null, id)).map((otcOperationIdFolder) -> new File(otcOperationIdFolder, "operation.json")).forEach((operationFile) -> {
            try {
                JsonNode operation = mapper.readTree(operationFile);
                BaseOperation.putBuyOperationMinutesLeft(operation);
                String timestamp = operation.get("timestamp").textValue();
                otcAdminGetOperationsRequest.setFinalTimestamp(DateUtil.getDateDaysAfter(DateUtil.getDayStartDate(otcAdminGetOperationsRequest.getFinalTimestamp()), 1));
                if (otcAdminGetOperationsRequest.getInitTimestamp() == null && otcAdminGetOperationsRequest.getFinalTimestamp() == null) {
                    operations.add(operation);
                } else if (otcAdminGetOperationsRequest.getInitTimestamp() != null && otcAdminGetOperationsRequest.getFinalTimestamp() == null) {
                    if (timestamp.compareTo(otcAdminGetOperationsRequest.getInitTimestamp()) >= 0) {
                        operations.add(operation);
                    }
                } else if (otcAdminGetOperationsRequest.getInitTimestamp() == null && otcAdminGetOperationsRequest.getFinalTimestamp() != null) {
                    if (timestamp.compareTo(otcAdminGetOperationsRequest.getFinalTimestamp()) < 0) {
                        operations.add(operation);
                    }
                } else {
                    if (timestamp.compareTo(otcAdminGetOperationsRequest.getInitTimestamp()) >= 0 && timestamp.compareTo(otcAdminGetOperationsRequest.getFinalTimestamp()) < 0) {
                        operations.add(operation);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        super.response = operations;
    }

}
