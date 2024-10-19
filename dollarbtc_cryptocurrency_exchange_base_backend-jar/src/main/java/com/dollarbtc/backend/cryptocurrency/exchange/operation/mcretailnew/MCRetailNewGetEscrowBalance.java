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
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class MCRetailNewGetEscrowBalance extends AbstractOperation<Double> {

    private final String retailId, currency;

    public MCRetailNewGetEscrowBalance(String retailId, String currency) {
        super(Double.class);
        this.retailId = retailId;
        this.currency = currency;
    }
    
    @Override
    public void execute() {
        Double escrowBalance = 0.0;
        File[] moneyclickRetailEscrowBalanceFolders = new File[]{MoneyclickFolderLocator.getRetailEscrowBalanceFolder(retailId), MoneyclickFolderLocator.getRetailEscrowBalanceFromToUserFolder(retailId)};
        for (File moneyclickRetailEscrowBalanceFolder : moneyclickRetailEscrowBalanceFolders) {
            ArrayNode balance = BaseOperation.getBalance(moneyclickRetailEscrowBalanceFolder);
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
