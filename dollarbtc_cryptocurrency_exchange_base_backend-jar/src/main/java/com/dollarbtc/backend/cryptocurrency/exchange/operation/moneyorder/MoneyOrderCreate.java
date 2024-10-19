/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneyorder;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneyorder.MoneyOrderCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyOrdersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class MoneyOrderCreate extends AbstractOperation<String> {

    private final MoneyOrderCreateRequest moneyOrderCreateRequest;

    public MoneyOrderCreate(MoneyOrderCreateRequest moneyOrderCreateRequest) {
        super(String.class);
        this.moneyOrderCreateRequest = moneyOrderCreateRequest;
    }

    @Override
    public void execute() {
        File moneyOrdersOperationsPROCESSINGFolder = new File(MoneyOrdersFolderLocator.getOperationsFolder(), "PROCESSING");
        String id = BaseOperation.getId();
        ObjectNode additionals = mapper.createObjectNode();
        additionals.put("id", id);
        additionals.put("senderName", moneyOrderCreateRequest.getSenderName());
        additionals.put("orderId", moneyOrderCreateRequest.getOrderId());
        JsonNode charges = BaseOperation.getChargesNew(
                moneyOrderCreateRequest.getCurrency(),
                moneyOrderCreateRequest.getAmount(),
                BalanceOperationType.MONEY_ORDER_SEND,
                null,
                "MONEYCLICK",
                null,
                null
        );
        BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(moneyOrderCreateRequest.getUserName()),
                moneyOrderCreateRequest.getCurrency(),
                moneyOrderCreateRequest.getAmount(),
                BalanceOperationType.MONEY_ORDER_SEND,
                BalanceOperationStatus.PROCESSING,
                null,
                null,
                charges,
                false,
                additionals
        );
        JsonNode moneyOrderOperation = moneyOrderCreateRequest.toJsonNode();
        ((ObjectNode) moneyOrderOperation).put("id", id);
        ((ObjectNode) moneyOrderOperation).put("timestamp", DateUtil.getCurrentDate());
        FileUtil.createFile(moneyOrderOperation, new File(moneyOrdersOperationsPROCESSINGFolder, id + ".json"));
        super.response = "OK";
    }

}
