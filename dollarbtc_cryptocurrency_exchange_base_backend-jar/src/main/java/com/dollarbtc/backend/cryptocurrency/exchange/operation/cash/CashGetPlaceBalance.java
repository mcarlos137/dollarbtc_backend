/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author carlosmolina
 */
public class CashGetPlaceBalance extends AbstractOperation<JsonNode> {
    
    private final String placeId; 
    private final boolean onlyAvailable;

    public CashGetPlaceBalance(String placeId, boolean onlyAvailable) {
        super(JsonNode.class);
        this.placeId = placeId;
        this.onlyAvailable = onlyAvailable;
    }
    
    @Override
    public void execute() {
        JsonNode balance = mapper.createObjectNode();
        Map<String, Double> escrowAvailableAmounts = new HashMap<>();
        File[] cashPlaceEscrowBalanceFolders = new File[]{CashFolderLocator.getPlaceEscrowBalanceFolder(placeId), CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(placeId)};
        for (File cashPlaceEscrowBalanceFolder : cashPlaceEscrowBalanceFolders) {
            Iterator<JsonNode> cashPlaceEscrowBalanceIterator = BaseOperation.getBalance(cashPlaceEscrowBalanceFolder).iterator();
            while (cashPlaceEscrowBalanceIterator.hasNext()) {
                JsonNode cashPlaceEscrowBalanceIt = cashPlaceEscrowBalanceIterator.next();
                if (!escrowAvailableAmounts.containsKey(cashPlaceEscrowBalanceIt.get("currency").textValue())) {
                    escrowAvailableAmounts.put(cashPlaceEscrowBalanceIt.get("currency").textValue(), 0.0);
                }
                escrowAvailableAmounts.put(cashPlaceEscrowBalanceIt.get("currency").textValue(), escrowAvailableAmounts.get(cashPlaceEscrowBalanceIt.get("currency").textValue()) + cashPlaceEscrowBalanceIt.get("amount").doubleValue());
            }
        }
        ArrayNode escrows = mapper.createArrayNode();
        for (String currency : escrowAvailableAmounts.keySet()) {
            JsonNode escrow = mapper.createObjectNode();
            ((ObjectNode) escrow).put("currency", currency);
            ((ObjectNode) escrow).put("availableAmount", escrowAvailableAmounts.get(currency));
            ((ObjectNode) escrow).put("limit", new CashGetEscrowBalance(placeId, currency).getResponse());
            escrows.add(escrow);
        }
        ((ObjectNode) balance).putArray("escrows").addAll(escrows);
        ((ObjectNode) balance).set("noCashBalance", BaseOperation.getBalance(CashFolderLocator.getPlaceBalanceNoCashFolder(placeId)));
        ((ObjectNode) balance).set("cashBalance", BaseOperation.getBalance(CashFolderLocator.getPlaceBalanceCashFolder(placeId)));
        super.response = balance; 
    }
    
}
