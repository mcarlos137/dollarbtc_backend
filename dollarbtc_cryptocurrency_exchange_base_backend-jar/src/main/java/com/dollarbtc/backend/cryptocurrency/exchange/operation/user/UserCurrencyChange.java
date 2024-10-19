/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class UserCurrencyChange extends AbstractOperation<String> {

    private final String userName, baseCurrency, targetCurrency;
    private final BigDecimal marketPrice, requestedAmount;
    private final boolean checkBalance;

    public UserCurrencyChange(String userName, String baseCurrency, String targetCurrency, BigDecimal marketPrice, BigDecimal requestedAmount, boolean checkBalance) {
        super(String.class);
        this.userName = userName;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.marketPrice = marketPrice;
        this.requestedAmount = requestedAmount;
        this.checkBalance = checkBalance;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userName);
        if (!userFile.exists()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        JsonNode userBalance = LocalData.getUserBalance(userName, false, true, true, null, false);
        Iterator<JsonNode> userBalanceAvailableAmounts = userBalance.get("availableAmounts").elements();
        BigDecimal substract;
        while (userBalanceAvailableAmounts.hasNext()) {
            JsonNode userBalanceAvailableAmount = userBalanceAvailableAmounts.next();
            if (!userBalanceAvailableAmount.get("currency").textValue().equals(baseCurrency)) {
                continue;
            }
            BigDecimal maxAmount = new BigDecimal(userBalanceAvailableAmount.get("amount").doubleValue())
                    .multiply(marketPrice);
            if (maxAmount.compareTo(requestedAmount) >= 0) {
                substract = requestedAmount.divide(marketPrice, 8, RoundingMode.DOWN);
                if (checkBalance) {
                    super.response = new UserHasEnoughBalance(userName, baseCurrency, substract.doubleValue(), false).getResponse();
                    return;
                }
                String substractToBalanceResponse = BaseOperation.substractToBalance(
                        UsersFolderLocator.getBalanceFolder(userName),
                        baseCurrency,
                        substract.doubleValue(),
                        BalanceOperationType.CURRENCY_CHANGE,
                        BalanceOperationStatus.OK,
                        "CURRENCY CHANGE RATE " + marketPrice,
                        null,
                        false,
                        null,
                        false,
                        null
                );
                if (!substractToBalanceResponse.equals("OK")) {
                    super.response = substractToBalanceResponse;
                    return;
                }
                BaseOperation.addToBalance(
                        UsersFolderLocator.getBalanceFolder(userName),
                        targetCurrency,
                        requestedAmount.doubleValue(),
                        BalanceOperationType.CURRENCY_CHANGE,
                        BalanceOperationStatus.OK,
                        "CURRENCY CHANGE RATE " + marketPrice,
                        null,
                        null,
                        false,
                        null
                );
                super.response = "OK";
                return;
            } else {
                super.response = "ADD MORE BALANCE. ONLY CAN CHANGE " + maxAmount;
                return;
            }
        }
        super.response = "THERE IS NO BALANCE AVAILABLE";
    }

}
