/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerGetDollarBTCPaymentBalanceRequest;
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
public class BankerGetDollarBTCPaymentBalance extends AbstractOperation<JsonNode> {

    private final BankerGetDollarBTCPaymentBalanceRequest bankerGetDollarBTCPaymentBalanceRequest;

    public BankerGetDollarBTCPaymentBalance(BankerGetDollarBTCPaymentBalanceRequest bankerGetDollarBTCPaymentBalanceRequest) {
        super(JsonNode.class);
        this.bankerGetDollarBTCPaymentBalanceRequest = bankerGetDollarBTCPaymentBalanceRequest;
    }

    @Override
    public void execute() {
        JsonNode dollarBTCPaymentBalance = mapper.createObjectNode();
        File otcCurrencyPaymentsFolder = BankersFolderLocator.getPaymentsCurrencyFolder(bankerGetDollarBTCPaymentBalanceRequest.getUserName(), bankerGetDollarBTCPaymentBalanceRequest.getCurrency());
        for (String paymentId : bankerGetDollarBTCPaymentBalanceRequest.getPaymentIds()) {
            File otcCurrencyPaymentFolder = new File(otcCurrencyPaymentsFolder, paymentId);
            if (!otcCurrencyPaymentFolder.isDirectory()) {
                continue;
            }
            ((ObjectNode) dollarBTCPaymentBalance).set(paymentId, BaseOperation.getBalance(new File(otcCurrencyPaymentFolder, "Balance")));
        }
        super.response = dollarBTCPaymentBalance;
    }

}
