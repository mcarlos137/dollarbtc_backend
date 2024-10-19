/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetAlerts extends AbstractOperation<JsonNode> {

    private final String currency, language, userName;
    private final PaymentType paymentType;
    private final Double amount;
    private final BalanceOperationType balanceOperationType;

    public MCUserGetAlerts(String currency, PaymentType paymentType, String language, String userName, Double amount, BalanceOperationType balanceOperationType) {
        super(JsonNode.class);
        this.currency = currency;
        this.language = language;
        this.paymentType = paymentType;
        this.userName = userName;
        this.amount = amount;
        this.balanceOperationType = balanceOperationType;
    }

    @Override
    public void execute() {
        JsonNode moneyclickAlerts = mapper.createObjectNode();
        File moneyclickAlertsFile = null;
        String key = null;
        if (language == null) {
            moneyclickAlertsFile = MoneyclickFolderLocator.getAlertsFile();
            key = currency + "__" + paymentType.name();
        } else if (balanceOperationType == null) {
            moneyclickAlertsFile = MoneyclickFolderLocator.getAlertsWithLanguageFile();
            key = currency + "__" + paymentType.name() + "__" + language;
        } else if (balanceOperationType != null) {
            moneyclickAlertsFile = MoneyclickFolderLocator.getAlertsWithLanguageAndOperationFile();
            key = currency + "__" + balanceOperationType.name() + "__" + paymentType.name() + "__" + language;
        }
        if (moneyclickAlertsFile == null || key == null) {
            super.response = moneyclickAlerts;
            return;
        }
        try {
            moneyclickAlerts = mapper.readTree(moneyclickAlertsFile);
            if (moneyclickAlerts.has(key)) {
                super.response = moneyclickAlerts.get(key);
                return;
            } else {
                super.response = mapper.createObjectNode();
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(MCUserGetAlerts.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = moneyclickAlerts;
    }

//    private String getKey() {
//        String key = currency + "__" + paymentType.name() + "__" + language;
//        if (currency.equals("BTC") && paymentType.equals(PaymentType.MAIN)) {
//            if(BaseOperation.processSendOutIn48Hours(userName, amount)){
//                key = currency + "__" + "MAIN" + "__" + language + "__2";
//            } else {
//                key = currency + "__" + "MAIN" + "__" + language + "__1";
//            }
//        }
//        return key;
//    }
}
