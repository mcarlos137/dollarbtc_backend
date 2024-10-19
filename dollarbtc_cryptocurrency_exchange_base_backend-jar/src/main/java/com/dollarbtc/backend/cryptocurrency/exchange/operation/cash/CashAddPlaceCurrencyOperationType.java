/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashAddPlaceCurrencyOperationTypeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashAddPlaceCurrencyOperationType extends AbstractOperation<String> {

    private final CashAddPlaceCurrencyOperationTypeRequest cashAddPlaceCurrencyOperationTypeRequest;

    public CashAddPlaceCurrencyOperationType(CashAddPlaceCurrencyOperationTypeRequest cashAddPlaceCurrencyOperationTypeRequest) {
        super(String.class);
        this.cashAddPlaceCurrencyOperationTypeRequest = cashAddPlaceCurrencyOperationTypeRequest;
    }

    @Override
    public void execute() {
        File cashPlaceConfigFile = CashFolderLocator.getPlaceConfigFile(cashAddPlaceCurrencyOperationTypeRequest.getPlaceId());
        if (!cashPlaceConfigFile.isFile()) {
            super.response = "CASH PLACE ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
            ArrayNode cashPlaceConfigCurrencies = (ArrayNode) cashPlaceConfig.get("currencies");
            boolean add = true;
            Iterator<JsonNode> cashPlaceConfigCurrenciesIterator = cashPlaceConfigCurrencies.iterator();
            while (cashPlaceConfigCurrenciesIterator.hasNext()) {
                JsonNode cashPlaceConfigCurrenciesIt = cashPlaceConfigCurrenciesIterator.next();
                if (cashPlaceConfigCurrenciesIt.textValue().equals(cashAddPlaceCurrencyOperationTypeRequest.getCurrency())) {
                    add = false;
                }
            }
            if (add) {
                cashPlaceConfigCurrencies.add(cashAddPlaceCurrencyOperationTypeRequest.getCurrency());
            }
            ArrayNode cashPlaceConfigOperations = (ArrayNode) cashPlaceConfig.get("operations");
            add = true;
            Iterator<JsonNode> cashPlaceConfigOperationsIterator = cashPlaceConfigOperations.iterator();
            while (cashPlaceConfigOperationsIterator.hasNext()) {
                JsonNode cashPlaceConfigOperationsIt = cashPlaceConfigOperationsIterator.next();
                if (cashPlaceConfigOperationsIt.get("currency").textValue().equals(cashAddPlaceCurrencyOperationTypeRequest.getCurrency())
                        && CashOperationType.valueOf(cashPlaceConfigOperationsIt.get("type").textValue()).equals(cashAddPlaceCurrencyOperationTypeRequest.getCashOperationType())) {
                    add = false;
                }
            }
            JsonNode cashPlaceConfigOperation = mapper.createObjectNode();
            ((ObjectNode) cashPlaceConfigOperation).put("currency", cashAddPlaceCurrencyOperationTypeRequest.getCurrency());
            ((ObjectNode) cashPlaceConfigOperation).put("type", cashAddPlaceCurrencyOperationTypeRequest.getCashOperationType().name());
            if (add) {
                cashPlaceConfigOperations.add(cashPlaceConfigOperation);
            }
            ArrayNode cashPlaceConfigEscrowLimits = (ArrayNode) cashPlaceConfig.get("escrowLimits");
            add = true;
            Iterator<JsonNode> cashPlaceConfigEscrowLimitsIterator = cashPlaceConfigEscrowLimits.iterator();
            while (cashPlaceConfigEscrowLimitsIterator.hasNext()) {
                JsonNode cashPlaceConfigEscrowLimitsIt = cashPlaceConfigEscrowLimitsIterator.next();
                if (cashPlaceConfigEscrowLimitsIt.get("currency").textValue().equals(cashAddPlaceCurrencyOperationTypeRequest.getCurrency())) {
                    add = false;
                }
            }
            JsonNode cashPlaceConfigEscrowLimit = mapper.createObjectNode();
            ((ObjectNode) cashPlaceConfigEscrowLimit).put("currency", cashAddPlaceCurrencyOperationTypeRequest.getCurrency());
            try {
                File cashPlaceEscrowLimitsFile = CashFolderLocator.getPlacesEscrowLimitsFile();
                JsonNode cashPlaceEscrowLimits = new ObjectMapper().readTree(cashPlaceEscrowLimitsFile);
                if (cashPlaceEscrowLimits.has(cashAddPlaceCurrencyOperationTypeRequest.getCurrency())) {
                    ((ObjectNode) cashPlaceConfigEscrowLimit).put("amount", cashPlaceEscrowLimits.get(cashAddPlaceCurrencyOperationTypeRequest.getCurrency()).doubleValue());
                } else {
                    super.response = "FAIL";
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(CashAddPlaceCurrencyOperationType.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (add) {
                cashPlaceConfigEscrowLimits.add(cashPlaceConfigEscrowLimit);
            }
            FileUtil.editFile(cashPlaceConfig, cashPlaceConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(CashAddPlaceCurrencyOperationType.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
