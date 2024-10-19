/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetCurrencies;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.Set;

/**
 *
 * @author carlosmolina
 */
public class BankerGetOperationIndexesAndValues extends AbstractOperation<JsonNode> {
    
    private final String userName;

    public BankerGetOperationIndexesAndValues(String userName) {
        super(JsonNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        JsonNode operationIndexesAndValues = mapper.createObjectNode();
        Set<String> userCurrencies = new UserGetCurrencies(userName).getResponse();
        File otcOperationsIndexesFolder = BankersFolderLocator.getOperationsIndexesFolder(userName);
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
                if (otcOperationsIndexFolder.getName().equals("Currencies")) {
                    if (!userCurrencies.contains(otcOperationsIndexValueFolder.getName())) {
                        continue;
                    }
                }
                ((ArrayNode) operationIndexesAndValues.get(otcOperationsIndexFolder.getName())).add(otcOperationsIndexValueFolder.getName());
                optionsNumber++;
                if (optionsNumber > 20) {
                    ((ObjectNode) operationIndexesAndValues).remove(otcOperationsIndexFolder.getName());
                    break;
                }
            }
        }
        super.response = operationIndexesAndValues; 
    }
        
}
