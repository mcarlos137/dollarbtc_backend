/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddDollarBTCPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
public class OTCAddDollarBTCPayment extends AbstractOperation<String> {

    private final OTCAddDollarBTCPaymentRequest otcAddDollarBTCPaymentRequest;

    public OTCAddDollarBTCPayment(OTCAddDollarBTCPaymentRequest otcAddDollarBTCPaymentRequest) {
        super(String.class);
        this.otcAddDollarBTCPaymentRequest = otcAddDollarBTCPaymentRequest;
    }

    @Override
    public void execute() {
        String id = BaseOperation.getId();
        File otcCurrencyPaymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(null, otcAddDollarBTCPaymentRequest.getCurrency());
        File otcCurrencyPaymentFolder = FileUtil.createFolderIfNoExist(new File(otcCurrencyPaymentsFolder, id));
        JsonNode config = otcAddDollarBTCPaymentRequest.getPayment();
        if(!otcAddDollarBTCPaymentRequest.getPayment().has("types")){
            super.response = "PAYMENT DOES NOT HAVE TYPES";
            return;
        }
        ((ObjectNode) config).put("id", id);
        if (otcAddDollarBTCPaymentRequest.getPayment().has("active")) {
            ((ObjectNode) config).put("active", true);
        } else {
            ((ObjectNode) config).put("active", otcAddDollarBTCPaymentRequest.getPayment().get("active").booleanValue());
        }
        if (otcAddDollarBTCPaymentRequest.getPayment().has("acceptIn")) {
            ((ObjectNode) config).put("acceptIn", true);
        } else {
            ((ObjectNode) config).put("acceptIn", otcAddDollarBTCPaymentRequest.getPayment().get("acceptIn").booleanValue());
        }
        if (otcAddDollarBTCPaymentRequest.getPayment().has("acceptOut")) {
            ((ObjectNode) config).put("acceptOut", true);
        } else {
            ((ObjectNode) config).put("acceptOut", otcAddDollarBTCPaymentRequest.getPayment().get("acceptOut").booleanValue());
        }
        File paymentConfigFile = new File(otcCurrencyPaymentFolder, "config.json");
        FileUtil.createFile(config, paymentConfigFile);
        File otcCurrencyPaymentBalanceFolder = FileUtil.createFolderIfNoExist(new File(otcCurrencyPaymentFolder, "Balance"));
        BaseOperation.addToBalance(
                otcCurrencyPaymentBalanceFolder,
                otcAddDollarBTCPaymentRequest.getCurrency(),
                0.0,
                BalanceOperationType.INITIAL_MOVEMENT,
                BalanceOperationStatus.OK,
                null,
                null,
                null,
                false,
                null
        );
        if (otcAddDollarBTCPaymentRequest.getUserName() != null && !otcAddDollarBTCPaymentRequest.getUserName().equals("")) {
            File userConfigFile = UsersFolderLocator.getConfigFile(otcAddDollarBTCPaymentRequest.getUserName());
            if (!userConfigFile.isFile()) {
                super.response = "USER DOES NOT EXIST";
                return;
            }
            try {
                JsonNode userConfig = mapper.readTree(userConfigFile);
                if (userConfig.has("paymentCommissions") && userConfig.get("paymentCommissions").has(otcAddDollarBTCPaymentRequest.getCurrency())) {
                    ((ObjectNode) config).set("commissions", mapper.createObjectNode());
                    ((ObjectNode) config.get("commissions")).put("mcBuyBalancePercent", userConfig.get("paymentCommissions").get(otcAddDollarBTCPaymentRequest.getCurrency()).get("mcBuyBalancePercent").floatValue());
                    ((ObjectNode) config.get("commissions")).put("sendToPaymentPercent", userConfig.get("paymentCommissions").get(otcAddDollarBTCPaymentRequest.getCurrency()).get("sendToPaymentPercent").floatValue());
                    FileUtil.editFile(config, paymentConfigFile);
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAddDollarBTCPayment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = "OK";
    }

}
