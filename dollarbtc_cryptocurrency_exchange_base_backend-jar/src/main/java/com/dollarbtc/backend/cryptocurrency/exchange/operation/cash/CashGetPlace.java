/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashGetPlace extends AbstractOperation<JsonNode> {

    private final String id;

    public CashGetPlace(String id) {
        super(JsonNode.class);
        this.id = id;
    }

    @Override
    public void execute() {
        JsonNode placeMarker = mapper.createObjectNode();
        File cashPlaceConfigFile = CashFolderLocator.getPlaceConfigFile(id);
        if (cashPlaceConfigFile.isFile()) {
            try {
                JsonNode cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
                Iterator<JsonNode> cashPlaceConfigOperationsIterator = cashPlaceConfig.get("operations").iterator();
                while (cashPlaceConfigOperationsIterator.hasNext()) {
                    JsonNode cashPlaceConfigOperationsIt = cashPlaceConfigOperationsIterator.next();
                    addFieldsToCashOperation(cashPlaceConfigOperationsIt, mapper);
                }
                addFieldsToPlace(cashPlaceConfig, mapper);
                super.response = cashPlaceConfig;
                return;
            } catch (IOException ex) {
                Logger.getLogger(CashGetPlace.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = placeMarker;
    }

    private static void addFieldsToPlace(JsonNode place, ObjectMapper mapper) {
        // add escrow balances
        Map<String, Double> escrowBalances = new HashMap<>();
        File[] cashPlaceEscrowBalanceFolders = new File[]{CashFolderLocator.getPlaceEscrowBalanceFolder(place.get("id").textValue()), CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(place.get("id").textValue())};
        for (File cashPlaceEscrowBalanceFolder : cashPlaceEscrowBalanceFolders) {
            ArrayNode balance = BaseOperation.getBalance(cashPlaceEscrowBalanceFolder);
            Iterator<JsonNode> balanceIterator = balance.elements();
            while (balanceIterator.hasNext()) {
                JsonNode balanceIt = balanceIterator.next();
                if (!escrowBalances.containsKey(balanceIt.get("currency").textValue())) {
                    escrowBalances.put(balanceIt.get("currency").textValue(), 0.0);
                }
                escrowBalances.put(balanceIt.get("currency").textValue(), escrowBalances.get(balanceIt.get("currency").textValue()) + balanceIt.get("amount").doubleValue());
            }
        }
        ((ObjectNode) place).set("escrowBalances", mapper.valueToTree(escrowBalances));
    }

    static void addFieldsToCashOperation(JsonNode cashOperation, ObjectMapper mapper) {
        CashOperationType cashOperationType = CashOperationType.valueOf(cashOperation.get("type").textValue());
        try {
            File moneyclickConfigFile = CashFolderLocator.getConfigFile();
            JsonNode moneyclickConfig = mapper.readTree(moneyclickConfigFile);
            if (moneyclickConfig.has(cashOperationType.name()) && moneyclickConfig.get(cashOperationType.name()).has(cashOperation.get("currency").textValue())) {
                JsonNode moneyclickConfigOperationTypeCurrency = moneyclickConfig.get(cashOperationType.name()).get(cashOperation.get("currency").textValue());
                ((ObjectNode) cashOperation).put("bottomLimit", moneyclickConfigOperationTypeCurrency.get("bottomLimit").doubleValue());
                ((ObjectNode) cashOperation).put("topLimit", moneyclickConfigOperationTypeCurrency.get("topLimit").doubleValue());
            }
        } catch (IOException ex) {
            Logger.getLogger(CashGetPlace.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
