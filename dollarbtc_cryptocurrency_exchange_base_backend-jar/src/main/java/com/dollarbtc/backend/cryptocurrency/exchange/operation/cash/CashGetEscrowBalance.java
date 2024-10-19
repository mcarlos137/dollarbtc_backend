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
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class CashGetEscrowBalance extends AbstractOperation<Double> {

    private final String placeId, currency;

    public CashGetEscrowBalance(String placeId, String currency) {
        super(Double.class);
        this.placeId = placeId;
        this.currency = currency;
    }
    
    @Override
    public void execute() {
        Double escrowBalance = 0.0;
        File[] cashPlaceEscrowBalanceFolders = new File[]{CashFolderLocator.getPlaceEscrowBalanceFolder(placeId), CashFolderLocator.getPlaceEscrowBalanceFromToUserFolder(placeId)};
        for (File cashPlaceEscrowBalanceFolder : cashPlaceEscrowBalanceFolders) {
            ArrayNode balance = BaseOperation.getBalance(cashPlaceEscrowBalanceFolder);
            Iterator<JsonNode> balanceIterator = balance.elements();
            while (balanceIterator.hasNext()) {
                JsonNode balanceIt = balanceIterator.next();
                if (balanceIt.get("currency").textValue().equals(currency)) {
                    escrowBalance = escrowBalance + balanceIt.get("amount").doubleValue();
                }
            }
        }
        super.response = escrowBalance;
    }

}
