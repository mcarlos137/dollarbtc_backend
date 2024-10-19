/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetDollarBTCPaymentBalanceMovementsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author carlosmolina
 */
public class OTCGetDollarBTCPaymentBalanceMovements extends AbstractOperation<JsonNode> {

    private final OTCGetDollarBTCPaymentBalanceMovementsRequest otcGetDollarBTCPaymentBalanceMovementsRequest;

    public OTCGetDollarBTCPaymentBalanceMovements(OTCGetDollarBTCPaymentBalanceMovementsRequest otcGetDollarBTCPaymentBalanceMovementsRequest) {
        super(JsonNode.class);
        this.otcGetDollarBTCPaymentBalanceMovementsRequest = otcGetDollarBTCPaymentBalanceMovementsRequest;
    }

    @Override
    public void execute() {
        JsonNode dollarBTCPaymentBalance = mapper.createObjectNode();
        File otcCurrencyPaymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(null, otcGetDollarBTCPaymentBalanceMovementsRequest.getCurrency());
        Set<String> paymentsIds = new HashSet<>();
        for (String paymentId : otcGetDollarBTCPaymentBalanceMovementsRequest.getPaymentIds()) {
            if (paymentsIds.contains(paymentId)) {
                continue;
            }
            paymentsIds.add(paymentId);
            File otcCurrencyPaymentFolder = new File(otcCurrencyPaymentsFolder, paymentId);
            if (!otcCurrencyPaymentFolder.isDirectory()) {
                continue;
            }
            ((ObjectNode) dollarBTCPaymentBalance).set(paymentId, BaseOperation.getBalanceMovements(new File(otcCurrencyPaymentFolder, "Balance"),
                    otcGetDollarBTCPaymentBalanceMovementsRequest.getInitTimestamp(),
                    otcGetDollarBTCPaymentBalanceMovementsRequest.getEndTimestamp(),
                    otcGetDollarBTCPaymentBalanceMovementsRequest.getBalanceOperationType(),
                    otcGetDollarBTCPaymentBalanceMovementsRequest.getCurrency(),
                    null));
        }
        super.response = dollarBTCPaymentBalance;
    }

}
