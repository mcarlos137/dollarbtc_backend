/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailNewGetBalance extends AbstractOperation<JsonNode> {
    
    private final String retailId; 
    private final boolean onlyAvailable;

    public MCRetailNewGetBalance(String retailId, boolean onlyAvailable) {
        super(JsonNode.class);
        this.retailId = retailId;
        this.onlyAvailable = onlyAvailable;
    }
    
    @Override
    public void execute() {
        JsonNode balance = mapper.createObjectNode();
        Map<String, Double> escrowAvailableAmounts = new HashMap<>();
        File[] moneyclickRetailEscrowBalanceFolders = new File[]{MoneyclickFolderLocator.getRetailEscrowBalanceFolder(retailId), MoneyclickFolderLocator.getRetailEscrowBalanceFromToUserFolder(retailId)};
        for (File moneyclickRetailEscrowBalanceFolder : moneyclickRetailEscrowBalanceFolders) {
            Iterator<JsonNode> moneyclickRetailEscrowBalanceIterator = BaseOperation.getBalance(moneyclickRetailEscrowBalanceFolder).iterator();
            while (moneyclickRetailEscrowBalanceIterator.hasNext()) {
                JsonNode moneyclickRetailEscrowBalanceIt = moneyclickRetailEscrowBalanceIterator.next();
                if (!escrowAvailableAmounts.containsKey(moneyclickRetailEscrowBalanceIt.get("currency").textValue())) {
                    escrowAvailableAmounts.put(moneyclickRetailEscrowBalanceIt.get("currency").textValue(), 0.0);
                }
                escrowAvailableAmounts.put(moneyclickRetailEscrowBalanceIt.get("currency").textValue(), escrowAvailableAmounts.get(moneyclickRetailEscrowBalanceIt.get("currency").textValue()) + moneyclickRetailEscrowBalanceIt.get("amount").doubleValue());
            }
        }
        ArrayNode escrows = mapper.createArrayNode();
        for (String currency : escrowAvailableAmounts.keySet()) {
            JsonNode escrow = mapper.createObjectNode();
            ((ObjectNode) escrow).put("currency", currency);
            ((ObjectNode) escrow).put("availableAmount", escrowAvailableAmounts.get(currency));
            ((ObjectNode) escrow).put("limit", new MCRetailNewGetEscrowBalance(retailId, currency).getResponse());
            escrows.add(escrow);
        }
        ((ObjectNode) balance).putArray("escrows").addAll(escrows);
        ((ObjectNode) balance).set("noCashBalance", BaseOperation.getBalance(MoneyclickFolderLocator.getRetailBalanceNoCashFolder(retailId)));
        ((ObjectNode) balance).set("cashBalance", BaseOperation.getBalance(MoneyclickFolderLocator.getRetailBalanceCashFolder(retailId)));
        super.response = balance; 
    }
    
}
