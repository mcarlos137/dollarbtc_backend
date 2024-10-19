/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallPayRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyCallsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyCallPay extends AbstractOperation<String> {

    private final MoneyCallPayRequest moneyCallPayRequest;

    public MoneyCallPay(MoneyCallPayRequest moneyCallPayRequest) {
        super(String.class);
        this.moneyCallPayRequest = moneyCallPayRequest;
    }

    @Override
    public void execute() {
        File moneyCallFile = new File(MoneyCallsFolderLocator.getFolder(), moneyCallPayRequest.getId() + ".json");
        if (!moneyCallFile.isFile()) {
            super.response = "MONEY CALL DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyCall = mapper.readTree(moneyCallFile);
            String id = moneyCall.get("id").textValue();
            String currency = moneyCall.get("currency").textValue();
            String senderUserName = moneyCall.get("senderUserName").textValue();
            String receiverUserName = moneyCall.get("receiverUserName").textValue();
            Double rate = moneyCall.get("rate").doubleValue();
            String lastStatus = moneyCall.get("status").textValue();
            JsonNode charges = BaseOperation.getChargesNew(
                    currency,
                    rate * moneyCallPayRequest.getTime(),
                    BalanceOperationType.MONEY_CALL_PAY,
                    null,
                    "MONEYCLICK",
                    null,
                    null
            );
            ObjectNode additionals = mapper.createObjectNode();
            additionals.put("moneyCallId", id);
            String substractToBalance = BaseOperation.substractToBalance(
                    UsersFolderLocator.getMCBalanceFolder(senderUserName),
                    currency,
                    rate * moneyCallPayRequest.getTime(),
                    BalanceOperationType.MONEY_CALL_PAY,
                    BalanceOperationStatus.OK,
                    null,
                    null,
                    true,
                    charges,
                    false,
                    additionals
            );
            if (!substractToBalance.equals("OK")) {
                super.response = substractToBalance;
                return;
            }
            BaseOperation.addToBalance(
                    UsersFolderLocator.getMCBalanceFolder(receiverUserName),
                    currency,
                    rate * moneyCallPayRequest.getTime(),
                    BalanceOperationType.MONEY_CALL_PAY,
                    BalanceOperationStatus.OK,
                    null,
                    null,
                    null,
                    false,
                    additionals
            );
            ((ObjectNode) moneyCall).put("payTimestamp", DateUtil.getCurrentDate());
            ((ObjectNode) moneyCall).put("status", "PAYED");
            ((ObjectNode) moneyCall).put("time", moneyCallPayRequest.getTime());
            ObjectNode rating = null;
            if (moneyCallPayRequest.getStars() != null && moneyCallPayRequest.getStars() > 0) {
                if (rating == null) {
                    rating = mapper.createObjectNode();
                }
                rating.put("stars", moneyCallPayRequest.getStars());
            }
            if (moneyCallPayRequest.getComment() != null && !moneyCallPayRequest.getComment().equals("")) {
                if (rating == null) {
                    rating = mapper.createObjectNode();
                }
                rating.put("comment", moneyCallPayRequest.getComment());
            }
            if (rating != null) {
                rating.put("timestamp", DateUtil.getCurrentDate());
                rating.put("userName", senderUserName);
                ((ObjectNode) moneyCall).set("rating", rating);
            }
            FileUtil.editFile(moneyCall, moneyCallFile);
            FileUtil.moveFileToFolder(new File(MoneyCallsFolderLocator.getIndexValueFolder("Statuses", lastStatus), id + ".json"), MoneyCallsFolderLocator.getIndexValueFolder("Statuses", "PAYED"));
            FileUtil.moveFileToFolder(new File(MoneyCallsFolderLocator.getIndexValueFolder("Times", "not_defined"), id + ".json"), MoneyCallsFolderLocator.getIndexValueFolder("Times", Integer.toString(moneyCallPayRequest.getTime())));
            FileUtil.moveFileToFolder(new File(MoneyCallsFolderLocator.getIndexValueFolder("Stars", "not_defined"), id + ".json"), MoneyCallsFolderLocator.getIndexValueFolder("Stars", Integer.toString(moneyCallPayRequest.getStars())));

            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MoneyCallPay.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
