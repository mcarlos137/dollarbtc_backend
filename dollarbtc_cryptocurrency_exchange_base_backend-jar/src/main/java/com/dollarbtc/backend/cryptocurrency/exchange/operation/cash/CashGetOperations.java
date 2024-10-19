/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
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
public class CashGetOperations extends AbstractOperation<ArrayNode> {
    
    private final CashGetOperationsRequest cashGetOperationsRequest;

    public CashGetOperations(CashGetOperationsRequest cashGetOperationsRequest) {
        super(ArrayNode.class);
        this.cashGetOperationsRequest = cashGetOperationsRequest;
    }
    
    @Override
    public void execute() {
        ArrayNode operations = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("UserNames");
        indexes.add("Currencies");
        indexes.add("Places");
        indexes.add("Types");
        indexes.add("Statuses");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i = null;
            switch (index) {
                case "UserNames":
                    i = cashGetOperationsRequest.getUserName();
                    break;
                case "Currencies":
                    i = cashGetOperationsRequest.getCurrency();
                    break;
                case "Places":
                    i = cashGetOperationsRequest.getCurrency();
                    break;
                case "Types":
                    if (cashGetOperationsRequest.getCashOperationType() != null) {
                        i = cashGetOperationsRequest.getCashOperationType().name();
                    }
                    break;
                case "Statuses":
                    if (cashGetOperationsRequest.getCashOperationStatus() != null) {
                        i = cashGetOperationsRequest.getCashOperationStatus().name();
                    }
                    break;
            }
            File cashOperationsIndexesFolder = CashFolderLocator.getOperationsIndexFolder(index);
            if (firstLoop) {
                if (i != null && !i.equals("")) {
                    File cashOperationsIndexFolder = new File(cashOperationsIndexesFolder, i);
                    if (!cashOperationsIndexFolder.isDirectory()) {
                        super.response = operations; 
                        return;
                    }
                    for (File idFiles : cashOperationsIndexFolder.listFiles()) {
                        ids.add(idFiles.getName().replace(".json", ""));
                    }
                } else {
                    for (File cashOperationsIndexFolder : cashOperationsIndexesFolder.listFiles()) {
                        if (!cashOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : cashOperationsIndexFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                }
                firstLoop = false;
            } else {
                Set<String> newIds = new HashSet<>();
                if (i != null && !i.equals("")) {
                    File cashOperationsIndexFolder = new File(cashOperationsIndexesFolder, i);
                    if (!cashOperationsIndexFolder.isDirectory()) {
                        super.response = operations; 
                        return;
                    }
                    for (String id : ids) {
                        if (new File(cashOperationsIndexFolder, id + ".json").isFile()) {
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File cashOperationsIndexFolder : cashOperationsIndexesFolder.listFiles()) {
                        if (!cashOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (new File(cashOperationsIndexFolder, id + ".json").isFile()) {
                                newIds.add(id);
                            }
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get operations
        File cashOperationsFolder = CashFolderLocator.getOperationsFolder();
        for (String id : ids) {
            File operationFile = new File(cashOperationsFolder, id + ".json");
            try {
                JsonNode operation = mapper.readTree(operationFile);
                operations.add(operation);
            } catch (IOException ex) {
                Logger.getLogger(CashGetOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = operations; 
    }
    
}
