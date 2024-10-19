/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashRemovePlaceCurrencyOperationTypeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class CashRemovePlaceCurrencyOperationType extends AbstractOperation<String> {
    
    private final CashRemovePlaceCurrencyOperationTypeRequest cashRemovePlaceCurrencyOperationTypeRequest;

    public CashRemovePlaceCurrencyOperationType(CashRemovePlaceCurrencyOperationTypeRequest cashRemovePlaceCurrencyOperationTypeRequest) {
        super(String.class);
        this.cashRemovePlaceCurrencyOperationTypeRequest = cashRemovePlaceCurrencyOperationTypeRequest;
    }
        
    @Override
    public void execute() {
        File cashPlaceConfigFile = CashFolderLocator.getPlaceConfigFile(cashRemovePlaceCurrencyOperationTypeRequest.getPlaceId());
        if (!cashPlaceConfigFile.isFile()) {
            this.response = "CASH PLACE ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
            ArrayNode cashPlaceConfigOperations = (ArrayNode) cashPlaceConfig.get("operations");
            Iterator<JsonNode> cashPlaceConfigOperationsIterator = cashPlaceConfigOperations.iterator();
            while (cashPlaceConfigOperationsIterator.hasNext()) {
                JsonNode cashPlaceConfigOperationsIt = cashPlaceConfigOperationsIterator.next();
                if (cashPlaceConfigOperationsIt.get("currency").textValue().equals(cashRemovePlaceCurrencyOperationTypeRequest.getCurrency())
                        && CashOperationType.valueOf(cashPlaceConfigOperationsIt.get("type").textValue()).equals(cashRemovePlaceCurrencyOperationTypeRequest.getCashOperationType())) {
                    cashPlaceConfigOperationsIterator.remove();
                }
            }
            FileUtil.editFile(cashPlaceConfig, cashPlaceConfigFile);
            this.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(CashRemovePlaceCurrencyOperationType.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.response = "FAIL";
    }
    
}
