/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketCloseOrderRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OrderType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyMarketCloseOrder extends AbstractOperation<String> {

    private final MoneyMarketCloseOrderRequest moneyMarketCloseOrderRequest;

    public MoneyMarketCloseOrder(MoneyMarketCloseOrderRequest moneyMarketCloseOrderRequest) {
        super(String.class);
        this.moneyMarketCloseOrderRequest = moneyMarketCloseOrderRequest;
    }

    @Override
    protected void execute() {
        File moneyMarketOrderFile = MoneyMarketFolderLocator.getOrderFile(moneyMarketCloseOrderRequest.getId());
        if (!moneyMarketOrderFile.isFile()) {
            super.response = "ORDER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyMarketOrder = mapper.readTree(moneyMarketOrderFile);
            if (!moneyMarketOrder.get("userName").textValue().equals(moneyMarketCloseOrderRequest.getUserName())) {
                super.response = "THIS USER CAN NOT DELETE THIS ORDER";
                return;
            }
            boolean postUserBot = false;
            if (moneyMarketOrder.has("bot")) {
                postUserBot = moneyMarketOrder.get("bot").booleanValue();
            }
            boolean noEscrow = false;
            if (moneyMarketOrder.has("noEscrow")) {
                noEscrow = moneyMarketOrder.get("noEscrow").booleanValue();
            }
            String pair = moneyMarketOrder.get("pair").textValue();
            String type = moneyMarketOrder.get("type").textValue();
            String pairBaseCurrency = MoneyMarketUtils.getCurrency(pair, true);
            String pairQuoteCurrency = MoneyMarketUtils.getCurrency(pair, false);
            ((ObjectNode) moneyMarketOrder).put("closed", true);
            FileUtil.editFile(moneyMarketOrder, moneyMarketOrderFile);
            FileUtil.moveFileToFolder(MoneyMarketFolderLocator.getUserNameIndexFile(moneyMarketCloseOrderRequest.getUserName(), moneyMarketCloseOrderRequest.getId()), MoneyMarketFolderLocator.getUserNameOldFolder(moneyMarketCloseOrderRequest.getUserName()));
            FileUtil.moveFileToFolder(MoneyMarketFolderLocator.getPairTypeIndexFile(pair, type, moneyMarketCloseOrderRequest.getId()), MoneyMarketFolderLocator.getPairTypeOldFolder(pair, type));
            Double leftAmount = moneyMarketOrder.get("amount").doubleValue();
            if (moneyMarketOrder.has("take")) {
                Iterator<JsonNode> moneyMarketOrderTakeIterator = moneyMarketOrder.get("take").iterator();
                while (moneyMarketOrderTakeIterator.hasNext()) {
                    JsonNode moneyMarketOrderTakeIt = moneyMarketOrderTakeIterator.next();
                    leftAmount = leftAmount - moneyMarketOrderTakeIt.get("amount").doubleValue();
                }
            }
            switch (OrderType.valueOf(moneyMarketOrder.get("type").textValue())) {
                case ASK:
                    if (leftAmount > 0.0 && !postUserBot && !noEscrow) {
                        BaseOperation.addToBalance(
                                UsersFolderLocator.getMCBalanceFolder(moneyMarketOrder.get("userName").textValue()), //POST USER
                                pairBaseCurrency,
                                leftAmount,
                                BalanceOperationType.MONEY_MARKET_CLOSE_ORDER,
                                BalanceOperationStatus.OK,
                                "REFUND OF UNTRADED AMOUNT P2P ORDER " + moneyMarketOrder.get("id").textValue(),
                                null,
                                null,
                                false,
                                null
                        );
                    }
                    break;
                case BID:
                    if (leftAmount * moneyMarketOrder.get("price").doubleValue() > 0.0 && !postUserBot && !noEscrow) {
                        BaseOperation.addToBalance(
                                UsersFolderLocator.getMCBalanceFolder(moneyMarketOrder.get("userName").textValue()), //POST USER
                                pairQuoteCurrency,
                                leftAmount * moneyMarketOrder.get("price").doubleValue(),
                                BalanceOperationType.MONEY_MARKET_CLOSE_ORDER,
                                BalanceOperationStatus.OK,
                                "REFUND OF UNTRADED AMOUNT P2P ORDER " + moneyMarketOrder.get("id").textValue(),
                                null,
                                null,
                                false,
                                null
                        );
                    }
                    break;
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MoneyMarketCloseOrder.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
