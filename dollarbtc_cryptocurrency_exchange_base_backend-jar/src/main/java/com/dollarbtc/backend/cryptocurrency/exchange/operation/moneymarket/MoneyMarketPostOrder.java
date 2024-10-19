/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketPostOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
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
public class MoneyMarketPostOrder extends AbstractOperation<String> {

    private final MoneyMarketPostOrderRequest moneyMarketPostOrderRequest;

    public MoneyMarketPostOrder(MoneyMarketPostOrderRequest moneyMarketPostOrderRequest) {
        super(String.class);
        this.moneyMarketPostOrderRequest = moneyMarketPostOrderRequest;
    }

    @Override
    protected void execute() {
        File moneyMarketPairTypeFolder = MoneyMarketFolderLocator.getPairTypeFolder(moneyMarketPostOrderRequest.getPair(), moneyMarketPostOrderRequest.getType().name());
        if (moneyMarketPairTypeFolder == null || !moneyMarketPairTypeFolder.isDirectory()) {
            super.response = "PAIR IS NOT ALLOWED";
            return;
        }
        String id = BaseOperation.getId();
        String timestamp = DateUtil.getCurrentDate();
        try {
            if (!moneyMarketPostOrderRequest.isBot()) {
                String pairBaseCurrency = MoneyMarketUtils.getCurrency(moneyMarketPostOrderRequest.getPair(), true);
                String pairQuoteCurrency = MoneyMarketUtils.getCurrency(moneyMarketPostOrderRequest.getPair(), false);
                switch (moneyMarketPostOrderRequest.getType()) {
                    case ASK:
                        //POST USER ASK - SUBSTRACT BASE
                        String substractToBalance = BaseOperation.substractToBalance(
                                UsersFolderLocator.getMCBalanceFolder(moneyMarketPostOrderRequest.getUserName()),
                                pairBaseCurrency,
                                moneyMarketPostOrderRequest.getAmount(),
                                BalanceOperationType.MONEY_MARKET_POST_ORDER,
                                BalanceOperationStatus.OK,
                                "AMOUNT BLOCKED BY MONEY MARKET ORDER " + id,
                                null,
                                false,
                                null,
                                false,
                                null
                        );
                        if (!substractToBalance.equals("OK")) {
                            super.response = substractToBalance;
                            return;
                        }
                        break;
                    case BID:
                        //POST USER BID - SUBSTRACT QUOTE
                        if (moneyMarketPostOrderRequest.getSource().equals("EXCHANGE")) {
                            substractToBalance = BaseOperation.substractToBalance(
                                    UsersFolderLocator.getMCBalanceFolder(moneyMarketPostOrderRequest.getUserName()),
                                    pairQuoteCurrency,
                                    moneyMarketPostOrderRequest.getAmount() * moneyMarketPostOrderRequest.getPrice(),
                                    BalanceOperationType.MONEY_MARKET_POST_ORDER,
                                    BalanceOperationStatus.OK,
                                    "AMOUNT BLOCKED BY MONEY MARKET ORDER " + id,
                                    null,
                                    false,
                                    null,
                                    false,
                                    null
                            );
                            if (!substractToBalance.equals("OK")) {
                                super.response = substractToBalance;
                                return;
                            }
                        }
                        break;
                }
            }
            JsonNode order = moneyMarketPostOrderRequest.toJsonNode();
            ((ObjectNode) order).put("id", id);
            ((ObjectNode) order).put("timestamp", timestamp);
            MoneyMarketUtils.createOrder(order, MoneyMarketFolderLocator.getOrderFile(id));
            MoneyMarketUtils.createIndex(id, timestamp, moneyMarketPairTypeFolder);
            MoneyMarketUtils.createIndex(id, timestamp, MoneyMarketFolderLocator.getUserNameFolder(moneyMarketPostOrderRequest.getUserName()));
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MoneyMarketPostOrder.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
