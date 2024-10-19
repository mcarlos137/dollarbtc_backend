/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerGetDollarBTCPaymentBalanceMovementsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BankerGetDollarBTCPaymentBalanceMovements extends AbstractOperation<JsonNode> {
    
    private final BankerGetDollarBTCPaymentBalanceMovementsRequest bankerGetDollarBTCPaymentBalanceMovementsRequest;

    public BankerGetDollarBTCPaymentBalanceMovements(BankerGetDollarBTCPaymentBalanceMovementsRequest bankerGetDollarBTCPaymentBalanceMovementsRequest) {
        super(JsonNode.class);
        this.bankerGetDollarBTCPaymentBalanceMovementsRequest = bankerGetDollarBTCPaymentBalanceMovementsRequest;
    }
        
    @Override
    public void execute() {
        JsonNode dollarBTCPaymentBalance = mapper.createObjectNode();
        File otcCurrencyPaymentsFolder = BankersFolderLocator.getPaymentsCurrencyFolder(bankerGetDollarBTCPaymentBalanceMovementsRequest.getUserName(), bankerGetDollarBTCPaymentBalanceMovementsRequest.getCurrency());
        for (String paymentId : bankerGetDollarBTCPaymentBalanceMovementsRequest.getPaymentIds()) {
            File otcCurrencyPaymentFolder = new File(otcCurrencyPaymentsFolder, paymentId);
            if (!otcCurrencyPaymentFolder.isDirectory()) {
                continue;
            }
            ((ObjectNode) dollarBTCPaymentBalance).set(paymentId, BaseOperation.getBalanceMovements(new File(otcCurrencyPaymentFolder, "Balance"),
                    bankerGetDollarBTCPaymentBalanceMovementsRequest.getInitTimestamp(),
                    bankerGetDollarBTCPaymentBalanceMovementsRequest.getEndTimestamp(),
                    bankerGetDollarBTCPaymentBalanceMovementsRequest.getBalanceOperationType(),
                    bankerGetDollarBTCPaymentBalanceMovementsRequest.getCurrency(),
                    null));
        }
        super.response = dollarBTCPaymentBalance;
    }
    
}
