/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DebitCardsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class DebitCardGetIndexesAndValues extends AbstractOperation<JsonNode> {

    public DebitCardGetIndexesAndValues() {
        super(JsonNode.class);
    }
        
    @Override
    public void execute() {
        JsonNode debitCardsIndexesAndValues = mapper.createObjectNode();
        File otcOperationsIndexesFolder = DebitCardsFolderLocator.getIndexesFolder();
        for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
            if (!otcOperationsIndexFolder.isDirectory()) {
                continue;
            }
            int optionsNumber = 0;
            ((ObjectNode) debitCardsIndexesAndValues).putArray(otcOperationsIndexFolder.getName());
            for (File otcOperationsIndexValueFolder : otcOperationsIndexFolder.listFiles()) {
                if (!otcOperationsIndexValueFolder.isDirectory()) {
                    continue;
                }
                ((ArrayNode) debitCardsIndexesAndValues.get(otcOperationsIndexFolder.getName())).add(otcOperationsIndexValueFolder.getName());
                optionsNumber++;
                if (optionsNumber > 20) {
                    ((ObjectNode) debitCardsIndexesAndValues).remove(otcOperationsIndexFolder.getName());
                    break;
                }
            }
        }
        super.response = debitCardsIndexesAndValues;
    }
    
}
