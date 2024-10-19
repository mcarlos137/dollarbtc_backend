/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerGetOperationsNewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
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
public class BankerGetOperationsNew extends AbstractOperation<ArrayNode> {
    
    private final BankerGetOperationsNewRequest bankerGetOperationsNewRequest;

    public BankerGetOperationsNew(BankerGetOperationsNewRequest bankerGetOperationsNewRequest) {
        super(ArrayNode.class);
        this.bankerGetOperationsNewRequest = bankerGetOperationsNewRequest;
    }    
    
    @Override
    protected void execute() {
        ArrayNode operations = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> userCurrencies = new UserGetCurrencies(bankerGetOperationsNewRequest.getUserName()).getResponse();
        Set<String> indexes = new HashSet<>();
        indexes.add("UserNames");
        indexes.add("Currencies");
        indexes.add("Types");
        indexes.add("Statuses");
        if (bankerGetOperationsNewRequest.getSpecialIndexes() != null && !bankerGetOperationsNewRequest.getSpecialIndexes().isEmpty()) {
            bankerGetOperationsNewRequest.getSpecialIndexes().keySet().stream().forEach((key) -> {
                indexes.add(key);
            });
        }
        boolean firstLoop = true;
        if (bankerGetOperationsNewRequest.getFinalTimestamp() != null && !bankerGetOperationsNewRequest.getFinalTimestamp().equals("")) {
            bankerGetOperationsNewRequest.setFinalTimestamp(DateUtil.getDateDaysAfter(DateUtil.getDayStartDate(bankerGetOperationsNewRequest.getFinalTimestamp()), 1));
        }
        for (String index : indexes) {
            String i[] = null;
            switch (index) {
                case "UserNames":
                    i = bankerGetOperationsNewRequest.getOperationUserNames();
                    break;
                case "Currencies":
                    i = bankerGetOperationsNewRequest.getCurrencies();
                    break;
                case "Types":
                    if (bankerGetOperationsNewRequest.getOtcOperationTypes() != null) {
                        i = new String[bankerGetOperationsNewRequest.getOtcOperationTypes().length];
                        int ii = 0;
                        for (OTCOperationType otcOperationType : bankerGetOperationsNewRequest.getOtcOperationTypes()) {
                            i[ii] = otcOperationType.name();
                            ii++;
                        }
                    }
                    break;
                case "Statuses":
                    if (bankerGetOperationsNewRequest.getOtcOperationStatuses() != null) {
                        i = new String[bankerGetOperationsNewRequest.getOtcOperationStatuses().length];
                        int ii = 0;
                        for (OTCOperationStatus otcOperationStatus : bankerGetOperationsNewRequest.getOtcOperationStatuses()) {
                            i[ii] = otcOperationStatus.name();
                            ii++;
                        }
                    }
                    break;
                default:
                    if (bankerGetOperationsNewRequest.getSpecialIndexes().get(index) != null && !bankerGetOperationsNewRequest.getSpecialIndexes().get(index).equals("")) {
                        i = bankerGetOperationsNewRequest.getSpecialIndexes().get(index);
                    }
                    break;
            }
            File otcOperationsIndexesFolder = BankersFolderLocator.getOperationsIndexesSpecificFolder(bankerGetOperationsNewRequest.getUserName(), index);
            if (firstLoop) {
                if (i != null) {
                    for (String _i : i) {
                        if (_i == null) {
                            continue;
                        }
                        File otcOperationsIndexFolder = new File(otcOperationsIndexesFolder, _i);
                        if (!otcOperationsIndexFolder.isDirectory()) {
                            super.response = operations;
                            return;
                        }
                        for (File idFiles : otcOperationsIndexFolder.listFiles()) {
                            if (index.equals("Currencies")) {
                                if (!userCurrencies.contains(_i)) {
                                    continue;
                                }
                            }
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
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
                if (i != null) {
                    for (String _i : i) {
                        if (_i == null) {
                            continue;
                        }
                        File otcOperationsIndexFolder = new File(otcOperationsIndexesFolder, _i);
                        if (!otcOperationsIndexFolder.isDirectory()) {
                            super.response = operations;
                            return;
                        }
                        for (String id : ids) {
                            if (!new File(otcOperationsIndexFolder, id + ".json").isFile()) {
                                continue;
                            }
                            if (index.equals("Currencies")) {
                                if (!userCurrencies.contains(_i)) {
                                    continue;
                                }
                            }
                            newIds.add(id);
                        }
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
        ids.stream().map((id) -> BankersFolderLocator.getOperationIdFolder(bankerGetOperationsNewRequest.getUserName(), id)).map((otcOperationIdFolder) -> new File(otcOperationIdFolder, "operation.json")).forEach((operationFile) -> {
            try {
                JsonNode operation = mapper.readTree(operationFile);
                BaseOperation.putBuyOperationMinutesLeft(operation);
                String timestamp = operation.get("timestamp").textValue();
                if (bankerGetOperationsNewRequest.getInitTimestamp() == null && bankerGetOperationsNewRequest.getFinalTimestamp() == null) {
                    operations.add(operation);
                } else if (bankerGetOperationsNewRequest.getInitTimestamp() != null && bankerGetOperationsNewRequest.getFinalTimestamp() == null) {
                    if (timestamp.compareTo(bankerGetOperationsNewRequest.getInitTimestamp()) >= 0) {
                        operations.add(operation);
                    }
                } else if (bankerGetOperationsNewRequest.getInitTimestamp() == null && bankerGetOperationsNewRequest.getFinalTimestamp() != null) {
                    if (timestamp.compareTo(bankerGetOperationsNewRequest.getFinalTimestamp()) < 0) {
                        operations.add(operation);
                    }
                } else {
                    if (timestamp.compareTo(bankerGetOperationsNewRequest.getInitTimestamp()) >= 0 && timestamp.compareTo(bankerGetOperationsNewRequest.getFinalTimestamp()) < 0) {
                        operations.add(operation);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(BankerGetOperationsNew.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        super.response = operations;
    }

    
}
