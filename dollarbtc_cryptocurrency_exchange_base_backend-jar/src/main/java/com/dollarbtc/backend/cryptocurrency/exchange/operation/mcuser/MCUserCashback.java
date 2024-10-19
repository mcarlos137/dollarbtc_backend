/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserCashbackRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class MCUserCashback extends AbstractOperation<String> {

    private final MCUserCashbackRequest mcUserCashbackRequest;

    public MCUserCashback(MCUserCashbackRequest mcUserCashbackRequest) {
        super(String.class);
        this.mcUserCashbackRequest = mcUserCashbackRequest;
    }

    @Override
    public void execute() {
        File userBaseMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(mcUserCashbackRequest.getBaseUserName());
        if (!userBaseMCBalanceFolder.isDirectory()) {
            super.response = "BASE USER DOES NOT EXIST";
            return;
        }
        File userTargetMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(mcUserCashbackRequest.getTargetUserName());
        if (!userTargetMCBalanceFolder.isDirectory()) {
            super.response = "TARGET USER DOES NOT EXIST";
            return;
        }
        JsonNode charges = BaseOperation.getChargesNew(
                mcUserCashbackRequest.getCurrency(),
                mcUserCashbackRequest.getAmount(),
                BalanceOperationType.MC_CASHBACK,
                null,
                "MONEYCLICK",
                null,
                null
        );
        String substractToBalance = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserCashbackRequest.getBaseUserName()),
                mcUserCashbackRequest.getCurrency(),
                mcUserCashbackRequest.getAmount(),
                BalanceOperationType.MC_CASHBACK,
                BalanceOperationStatus.OK,
                null,
                null,
                false,
                charges,
                false,
                null
        );
        if (!substractToBalance.equals("OK")) {
            super.response = substractToBalance;
            return;
        }
        BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserCashbackRequest.getTargetUserName()),
                mcUserCashbackRequest.getCurrency(),
                mcUserCashbackRequest.getAmount(),
                BalanceOperationType.MC_CASHBACK,
                BalanceOperationStatus.OK,
                null,
                null,
                null,
                false,
                null
        );
        if (charges != null) {
            Iterator<JsonNode> chargesIterator = charges.iterator();
            while (chargesIterator.hasNext()) {
                JsonNode chargesIt = chargesIterator.next();
                String currency = chargesIt.get("currency").textValue();
                Double amount = chargesIt.get("amount").doubleValue();
                BaseOperation.addToBalance(
                        UsersFolderLocator.getMCBalanceFolder(mcUserCashbackRequest.getTargetUserName()),
                        currency,
                        amount,
                        BalanceOperationType.MC_CASHBACK,
                        BalanceOperationStatus.OK,
                        "CASHBACK COMMISSION",
                        null,
                        null,
                        false,
                        null
                );
            }
        }
        super.response = "OK";
    }

}
