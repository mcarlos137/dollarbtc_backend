/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetBalanceMovements;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author CarlosDaniel
 */
public class ShowUserMCBalanceMovementsDecoratedMain {

    public static void main(String[] args) {
        JsonNode mcUserGetBalanceMovements = new MCUserGetBalanceMovements(args[0], null, null, null).getResponse();
        Map<String, Double> accumulatedBalance = new HashMap<>();
        int i = 0;
        Iterator<JsonNode> mcUserGetBalanceMovementsIterator = mcUserGetBalanceMovements.iterator();
        while (mcUserGetBalanceMovementsIterator.hasNext()) {
            JsonNode mcUserGetBalanceMovementsIt = mcUserGetBalanceMovementsIterator.next();
            System.out.println("------------------------------------ " + args[0]);
            i++;
            System.out.println(i);
            System.out.println("timestamp: " + mcUserGetBalanceMovementsIt.get("timestamp").textValue());
            System.out.println(mcUserGetBalanceMovementsIt.get("timestamp").textValue());
            if (mcUserGetBalanceMovementsIt.has("substractedAmount")) {
                Double amount = mcUserGetBalanceMovementsIt.get("substractedAmount").get("amount").doubleValue();
                String currency = mcUserGetBalanceMovementsIt.get("substractedAmount").get("currency").textValue();
                System.out.println("amount: -" + amount);
                System.out.println("currency: " + currency);
                if (mcUserGetBalanceMovementsIt.get("balanceOperationStatus").textValue().equals("OK")) {
                    if (!accumulatedBalance.containsKey(currency)) {
                        accumulatedBalance.put(currency, 0.0);
                    }
                    accumulatedBalance.put(currency, accumulatedBalance.get(currency) - amount);
                }
            }
            if (mcUserGetBalanceMovementsIt.has("addedAmount")) {
                Double amount = mcUserGetBalanceMovementsIt.get("addedAmount").get("amount").doubleValue();
                String currency = mcUserGetBalanceMovementsIt.get("addedAmount").get("currency").textValue();
                System.out.println("amount: +" + amount);
                System.out.println("currency: " + currency);
                if (mcUserGetBalanceMovementsIt.get("balanceOperationStatus").textValue().equals("OK")) {
                    if (!accumulatedBalance.containsKey(currency)) {
                        accumulatedBalance.put(currency, 0.0);
                    }
                    accumulatedBalance.put(currency, accumulatedBalance.get(currency) + amount);
                }
            }
            System.out.println("operation type: " + mcUserGetBalanceMovementsIt.get("balanceOperationType").textValue());
            System.out.println("operation status: " + mcUserGetBalanceMovementsIt.get("balanceOperationStatus").textValue());
            if (mcUserGetBalanceMovementsIt.has("senderUserName")) {
                System.out.println("sender userName: " + mcUserGetBalanceMovementsIt.get("senderUserName").textValue());
            }
            if (mcUserGetBalanceMovementsIt.has("clientPayment")) {
                System.out.println("client payment: " + mcUserGetBalanceMovementsIt.get("clientPayment"));
            }
            System.out.println("accumulated balance: " + accumulatedBalance);
            System.out.println("------------------------------------");
        }
    }

}
