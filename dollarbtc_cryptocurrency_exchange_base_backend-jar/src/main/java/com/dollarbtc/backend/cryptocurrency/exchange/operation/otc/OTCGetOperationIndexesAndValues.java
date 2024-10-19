/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCGetOperationIndexesAndValues extends AbstractOperation<JsonNode> {

    public OTCGetOperationIndexesAndValues() {
        super(JsonNode.class);
    }
        
    @Override
    public void execute() {
        JsonNode operationIndexesAndValues = mapper.createObjectNode();
        File otcOperationsIndexesFolder = OTCFolderLocator.getOperationsIndexesFolder(null);
        for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
            if (!otcOperationsIndexFolder.isDirectory()) {
                continue;
            }
            int optionsNumber = 0;
            ((ObjectNode) operationIndexesAndValues).putArray(otcOperationsIndexFolder.getName());
            for (File otcOperationsIndexValueFolder : otcOperationsIndexFolder.listFiles()) {
                if (!otcOperationsIndexValueFolder.isDirectory()) {
                    continue;
                }
                ((ArrayNode) operationIndexesAndValues.get(otcOperationsIndexFolder.getName())).add(otcOperationsIndexValueFolder.getName());
                optionsNumber++;
                if (optionsNumber > 50) {
                    ((ObjectNode) operationIndexesAndValues).remove(otcOperationsIndexFolder.getName());
                    break;
                }
            }
        }
        super.response = operationIndexesAndValues;
    }
    
}
