/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretail.MCRetailGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailGetOperations extends AbstractOperation<ArrayNode> {
    
    private final MCRetailGetOperationsRequest mcRetailGetOperationsRequest;

    public MCRetailGetOperations(MCRetailGetOperationsRequest mcRetailGetOperationsRequest) {
        super(ArrayNode.class);
        this.mcRetailGetOperationsRequest = mcRetailGetOperationsRequest;
    }
    
    @Override
    public void execute() {
        ArrayNode operations = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("UserNames");
        indexes.add("Currencies");
        indexes.add("Retails");
        indexes.add("Types");
        indexes.add("Statuses");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i = null;
            switch (index) {
                case "UserNames":
                    i = mcRetailGetOperationsRequest.getUserName();
                    break;
                case "Currencies":
                    i = mcRetailGetOperationsRequest.getCurrency();
                    break;
                case "Retails":
                    i = mcRetailGetOperationsRequest.getCurrency();
                    break;
                case "Types":
                    if (mcRetailGetOperationsRequest.getMcRetailOperationType() != null) {
                        i = mcRetailGetOperationsRequest.getMcRetailOperationType().name();
                    }
                    break;
                case "Statuses":
                    if (mcRetailGetOperationsRequest.getMcRetailOperationStatus() != null) {
                        i = mcRetailGetOperationsRequest.getMcRetailOperationStatus().name();
                    }
                    break;
            }
            File mcRetailOperationsIndexesFolder = MoneyclickFolderLocator.getOperationsIndexFolder(index);
            if (firstLoop) {
                if (i != null && !i.equals("")) {
                    File mcRetailOperationsIndexFolder = new File(mcRetailOperationsIndexesFolder, i);
                    if (!mcRetailOperationsIndexFolder.isDirectory()) {
                        super.response = operations; 
                        return;
                    }
                    for (File idFiles : mcRetailOperationsIndexFolder.listFiles()) {
                        ids.add(idFiles.getName().replace(".json", ""));
                    }
                } else {
                    for (File mcRetailOperationsIndexFolder : mcRetailOperationsIndexesFolder.listFiles()) {
                        if (!mcRetailOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : mcRetailOperationsIndexFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                }
                firstLoop = false;
            } else {
                Set<String> newIds = new HashSet<>();
                if (i != null && !i.equals("")) {
                    File mcRetailOperationsIndexFolder = new File(mcRetailOperationsIndexesFolder, i);
                    if (!mcRetailOperationsIndexFolder.isDirectory()) {
                        super.response = operations; 
                        return;
                    }
                    for (String id : ids) {
                        if (new File(mcRetailOperationsIndexFolder, id + ".json").isFile()) {
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File mcRetailOperationsIndexFolder : mcRetailOperationsIndexesFolder.listFiles()) {
                        if (!mcRetailOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (new File(mcRetailOperationsIndexFolder, id + ".json").isFile()) {
                                newIds.add(id);
                            }
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get operations
        File moneyclickOperationsFolder = MoneyclickFolderLocator.getOperationsFolder();
        for (String id : ids) {
            File operationFile = new File(moneyclickOperationsFolder, id + ".json");
            try {
                JsonNode operation = mapper.readTree(operationFile);
                operations.add(operation);
            } catch (IOException ex) {
                Logger.getLogger(MCRetailGetOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = operations; 
    }
    
}
