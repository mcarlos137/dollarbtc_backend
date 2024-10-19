/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class UserHasEnoughBalance extends AbstractOperation<String> {

    private final String userName, currency;
    private final Double amount;
    private final boolean isMoneyClick;

    public UserHasEnoughBalance(String userName, String currency, Double amount, boolean isMoneyClick) {
        super(String.class);
        this.userName = userName;
        this.currency = currency;
        this.amount = amount;
        this.isMoneyClick = isMoneyClick;
    }

    @Override
    protected void execute() {
        JsonNode userBalance = LocalData.getUserBalance(userName, false, true, false, null, isMoneyClick);
        Iterator<JsonNode> userBalanceAvailableAmounts = userBalance.get("availableAmounts").elements();
        boolean enoughBalance = false;
        while (userBalanceAvailableAmounts.hasNext()) {
            JsonNode userBalanceAvailableAmount = userBalanceAvailableAmounts.next();
            if (currency.equals(userBalanceAvailableAmount.get("currency").textValue())) {
                if (amount <= userBalanceAvailableAmount.get("amount").doubleValue()) {
                    enoughBalance = true;

                }
            }
        }
        if (enoughBalance) {
            super.response = "OK";
        } else {
            super.response = "USER WITH NOT ENOUGH BALANCE";
        }
    }

}
