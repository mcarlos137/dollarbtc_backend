/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.cash.CashGetPlace.addFieldsToCashOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashGetPlaces extends AbstractOperation<ArrayNode> {
    
    private final String currency;
    private final CashOperationType cashOperationType;

    public CashGetPlaces(String currency, CashOperationType cashOperationType) {
        super(ArrayNode.class);
        this.currency = currency;
        this.cashOperationType = cashOperationType;
    }
    
    @Override
    public void execute() {
        ArrayNode cashPlaces = mapper.createArrayNode();
        File cashPlacesFolder = CashFolderLocator.getPlacesFolder();
        for (File cashPlaceFolder : cashPlacesFolder.listFiles()) {
            if (!cashPlaceFolder.isDirectory()) {
                continue;
            }
            File cashPlaceFile = new File(cashPlaceFolder, "config.json");
            if (!cashPlaceFile.isFile()) {
                continue;
            }
            boolean addCashPlace = false;
            try {
                JsonNode cashPlace = mapper.readTree(cashPlaceFile);
                if (currency == null) {
                    addCashPlace = true;
                } else {
                    Iterator<JsonNode> cashPlaceOperationsIterator = cashPlace.get("operations").iterator();
                    while (cashPlaceOperationsIterator.hasNext()) {
                        JsonNode cashPlaceOperationsIt = cashPlaceOperationsIterator.next();
                        if (cashOperationType == null) {
                            if (cashPlaceOperationsIt.get("currency").textValue().equals(currency)) {
                                addCashPlace = true;
                            } else {
                                cashPlaceOperationsIterator.remove();
                            }
                        } else {
                            if (cashPlaceOperationsIt.get("currency").textValue().equals(currency)
                                    && CashOperationType.valueOf(cashPlaceOperationsIt.get("type").textValue()).equals(cashOperationType)) {
                                addCashPlace = true;
                            } else {
                                cashPlaceOperationsIterator.remove();
                            }
                        }
                    }
                }
                Iterator<JsonNode> cashPlaceOperationsIterator = cashPlace.get("operations").iterator();
                while (cashPlaceOperationsIterator.hasNext()) {
                    JsonNode cashPlaceOperationsIt = cashPlaceOperationsIterator.next();
                    if (cashPlaceOperationsIt.get("currency").textValue().equals(currency)) {
                        addFieldsToCashOperation(cashPlaceOperationsIt, mapper);
                    }
                }
                if (addCashPlace) {
                    BaseOperation.addFieldsToRetail(cashPlace, mapper);
                    cashPlaces.add(cashPlace);
                }
            } catch (IOException ex) {
                Logger.getLogger(CashGetPlaces.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = cashPlaces;
    }
    
}
