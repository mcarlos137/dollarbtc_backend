/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerAddDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BankerAddDollarBTCPayment extends AbstractOperation<String> {
    
    private final BankerAddDollarBTCPaymentRequest bankerAddDollarBTCPaymentRequest;

    public BankerAddDollarBTCPayment(BankerAddDollarBTCPaymentRequest bankerAddDollarBTCPaymentRequest) {
        super(String.class);
        this.bankerAddDollarBTCPaymentRequest = bankerAddDollarBTCPaymentRequest;
    }
        
    @Override
    public void execute() {
        String id = BaseOperation.getId();
        File otcCurrencyPaymentsFolder = BankersFolderLocator.getPaymentsCurrencyFolder(bankerAddDollarBTCPaymentRequest.getUserName(), bankerAddDollarBTCPaymentRequest.getCurrency());
        File otcCurrencyPaymentFolder = FileUtil.createFolderIfNoExist(new File(otcCurrencyPaymentsFolder, id));
        JsonNode config = bankerAddDollarBTCPaymentRequest.getPayment();
        ((ObjectNode) config).put("id", id);
        if (bankerAddDollarBTCPaymentRequest.getPayment().has("active")) {
            ((ObjectNode) config).put("active", true);
        } else {
            ((ObjectNode) config).put("active", bankerAddDollarBTCPaymentRequest.getPayment().get("active").booleanValue());
        }
        if (bankerAddDollarBTCPaymentRequest.getPayment().has("acceptIn")) {
            ((ObjectNode) config).put("acceptIn", true);
        } else {
            ((ObjectNode) config).put("acceptIn", bankerAddDollarBTCPaymentRequest.getPayment().get("acceptIn").booleanValue());
        }
        if (bankerAddDollarBTCPaymentRequest.getPayment().has("acceptOut")) {
            ((ObjectNode) config).put("acceptOut", true);
        } else {
            ((ObjectNode) config).put("acceptOut", bankerAddDollarBTCPaymentRequest.getPayment().get("acceptOut").booleanValue());
        }
        FileUtil.createFile(config, new File(otcCurrencyPaymentFolder, "config.json"));
        File otcCurrencyPaymentBalanceFolder = FileUtil.createFolderIfNoExist(new File(otcCurrencyPaymentFolder, "Balance"));
        BaseOperation.addToBalance(
                otcCurrencyPaymentBalanceFolder,
                bankerAddDollarBTCPaymentRequest.getCurrency(),
                0.0,
                BalanceOperationType.INITIAL_MOVEMENT,
                BalanceOperationStatus.OK,
                null,
                null,
                null,
                false,
                null
        );
        super.response = "OK";
    }
    
}
