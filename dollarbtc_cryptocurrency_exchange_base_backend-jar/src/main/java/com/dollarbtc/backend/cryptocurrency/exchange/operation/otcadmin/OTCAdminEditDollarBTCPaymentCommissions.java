/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditDollarBTCPaymentCommissionsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
public class OTCAdminEditDollarBTCPaymentCommissions extends AbstractOperation<String> {

    private final OTCAdminEditDollarBTCPaymentCommissionsRequest otcAdminEditDollarBTCPaymentCommissionsRequest;

    public OTCAdminEditDollarBTCPaymentCommissions(OTCAdminEditDollarBTCPaymentCommissionsRequest otcAdminEditDollarBTCPaymentCommissionsRequest) {
        super(String.class);
        this.otcAdminEditDollarBTCPaymentCommissionsRequest = otcAdminEditDollarBTCPaymentCommissionsRequest;
    }

    @Override
    protected void execute() {
        if (!otcAdminEditDollarBTCPaymentCommissionsRequest.getUserName().equals("carlos.molina@mailinator.com")) {
            super.response = "USER CAN NOT DO THIS OPERATION";
            return;
        }
        File currencyPaymentFile = OTCFolderLocator.getCurrencyPaymentFile(ExchangeUtil.OPERATOR_NAME, otcAdminEditDollarBTCPaymentCommissionsRequest.getCurrency(), otcAdminEditDollarBTCPaymentCommissionsRequest.getId());
        if (!currencyPaymentFile.isFile()) {
            super.response = "DOLLARBTC PAYMENT DOES NOT EXIST";
            return;
        }
        try {
            JsonNode currencyPayment = mapper.readTree(currencyPaymentFile);
            if (!currencyPayment.has("commissions")) {
                ((ObjectNode) currencyPayment).set("commissions", mapper.createObjectNode());
            }
            ((ObjectNode) currencyPayment.get("commissions")).put("mcBuyBalancePercent", otcAdminEditDollarBTCPaymentCommissionsRequest.getMcBuyBalancePercent());
            ((ObjectNode) currencyPayment.get("commissions")).put("sendToPaymentPercent", otcAdminEditDollarBTCPaymentCommissionsRequest.getSendToPaymentPercent());
            FileUtil.editFile(currencyPayment, currencyPaymentFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminEditDollarBTCPaymentCommissions.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
