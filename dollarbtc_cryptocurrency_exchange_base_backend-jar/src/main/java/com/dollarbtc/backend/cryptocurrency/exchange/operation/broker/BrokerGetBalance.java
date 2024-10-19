/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author carlosmolina
 */
public class BrokerGetBalance extends AbstractOperation<JsonNode> {

    private final String userName;

    public BrokerGetBalance(String userName) {
        super(JsonNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        Map<String, Double> currencyAmountsBalance = new HashMap<>();
        ArrayNode[] balances = new ArrayNode[]{BaseOperation.getBalance(UsersFolderLocator.getBalanceFolder(userName)), BaseOperation.getBalance(BrokersFolderLocator.getBalanceFolder(userName))};
        for (ArrayNode balance : balances) {
            Iterator<JsonNode> balanceIterator = balance.iterator();
            while (balanceIterator.hasNext()) {
                JsonNode balanceIt = balanceIterator.next();
                String currency = balanceIt.get("currency").textValue();
                Double amount = balanceIt.get("amount").doubleValue();
                if (currency.equals("USDT") || currency.equals("ETH")) {
                    continue;
                }
                if (!currencyAmountsBalance.containsKey(currency)) {
                    currencyAmountsBalance.put(currency, 0.0);
                }
                currencyAmountsBalance.put(currency, currencyAmountsBalance.get(currency) + amount);
            }
        }
        ArrayNode balance = mapper.createArrayNode();
        for (String currency : currencyAmountsBalance.keySet()) {
            JsonNode b = mapper.createObjectNode();
            ((ObjectNode) b).put("currency", currency);
            ((ObjectNode) b).put("amount", currencyAmountsBalance.get(currency));
            balance.add(b);
        }
        super.response = balance;
    }

}
