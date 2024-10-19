/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminGetOperationsNewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
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
public class OTCAdminGetOperationsNew extends AbstractOperation<ArrayNode> {

    private final OTCAdminGetOperationsNewRequest otcAdminGetOperationsNewRequest;

    public OTCAdminGetOperationsNew(OTCAdminGetOperationsNewRequest otcAdminGetOperationsNewRequest) {
        super(ArrayNode.class);
        this.otcAdminGetOperationsNewRequest = otcAdminGetOperationsNewRequest;
    }

    @Override
    protected void execute() {
        ArrayNode operations = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> userCurrencies = new UserGetCurrencies(otcAdminGetOperationsNewRequest.getUserName()).getResponse();
        Set<String> indexes = new HashSet<>();
        indexes.add("Statuses");
        indexes.add("Types");
        indexes.add("Currencies");
//        indexes.add("UserNames");
        if (otcAdminGetOperationsNewRequest.getSpecialIndexes() != null && !otcAdminGetOperationsNewRequest.getSpecialIndexes().isEmpty()) {
            otcAdminGetOperationsNewRequest.getSpecialIndexes().keySet().stream().forEach((key) -> {
                indexes.add(key);
            });
        }
        boolean firstLoop = true;
        if (otcAdminGetOperationsNewRequest.getFinalTimestamp() != null && !otcAdminGetOperationsNewRequest.getFinalTimestamp().equals("")) {
            otcAdminGetOperationsNewRequest.setFinalTimestamp(DateUtil.getDateDaysAfter(DateUtil.getDayStartDate(otcAdminGetOperationsNewRequest.getFinalTimestamp()), 1));
        }
        for (String index : indexes) {
            String i[] = null;
            switch (index) {
                case "Statuses":
                    if (otcAdminGetOperationsNewRequest.getOtcOperationStatuses() != null) {
                        i = new String[otcAdminGetOperationsNewRequest.getOtcOperationStatuses().length];
                        int ii = 0;
                        for (OTCOperationStatus otcOperationStatus : otcAdminGetOperationsNewRequest.getOtcOperationStatuses()) {
                            i[ii] = otcOperationStatus.name();
                            ii++;
                        }
                    }
                    break;
                case "Types":
                    if (otcAdminGetOperationsNewRequest.getOtcOperationTypes() != null) {
                        i = new String[otcAdminGetOperationsNewRequest.getOtcOperationTypes().length];
                        int ii = 0;
                        for (OTCOperationType otcOperationType : otcAdminGetOperationsNewRequest.getOtcOperationTypes()) {
                            i[ii] = otcOperationType.name();
                            ii++;
                        }
                    }
                    break;
                case "Currencies":
                    i = otcAdminGetOperationsNewRequest.getCurrencies();
                    break;
//                case "UserNames":
//                    i = otcAdminGetOperationsNewRequest.getOperationUserNames();
//                    break;
                default:
                    if (otcAdminGetOperationsNewRequest.getSpecialIndexes().get(index) != null && !otcAdminGetOperationsNewRequest.getSpecialIndexes().get(index).equals("")) {
                        i = otcAdminGetOperationsNewRequest.getSpecialIndexes().get(index);
                    }
                    break;
            }
            File otcOperationsIndexesFolder = OTCFolderLocator.getOperationsIndexesSpecificFolder(null, index);
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
        ids.stream().map((id) -> OTCFolderLocator.getOperationIdFolder(null, id)).map((otcOperationIdFolder) -> new File(otcOperationIdFolder, "operation.json")).forEach((operationFile) -> {
            try {
                JsonNode operation = mapper.readTree(operationFile);
                BaseOperation.putBuyOperationMinutesLeft(operation);
                String timestamp = operation.get("timestamp").textValue();
                if (otcAdminGetOperationsNewRequest.getInitTimestamp() == null && otcAdminGetOperationsNewRequest.getFinalTimestamp() == null) {
                    operations.add(operation);
                } else if (otcAdminGetOperationsNewRequest.getInitTimestamp() != null && otcAdminGetOperationsNewRequest.getFinalTimestamp() == null) {
                    if (timestamp.compareTo(otcAdminGetOperationsNewRequest.getInitTimestamp()) >= 0) {
                        operations.add(operation);
                    }
                } else if (otcAdminGetOperationsNewRequest.getInitTimestamp() == null && otcAdminGetOperationsNewRequest.getFinalTimestamp() != null) {
                    if (timestamp.compareTo(otcAdminGetOperationsNewRequest.getFinalTimestamp()) < 0) {
                        operations.add(operation);
                    }
                } else {
                    if (timestamp.compareTo(otcAdminGetOperationsNewRequest.getInitTimestamp()) >= 0 && timestamp.compareTo(otcAdminGetOperationsNewRequest.getFinalTimestamp()) < 0) {
                        operations.add(operation);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAdminGetOperationsNew.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        super.response = operations;
    }

}
