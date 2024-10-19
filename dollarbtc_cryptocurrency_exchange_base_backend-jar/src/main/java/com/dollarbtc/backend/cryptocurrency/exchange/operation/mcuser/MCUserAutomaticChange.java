/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserAutomaticChange extends AbstractOperation<String> {
    
    private final String userName, additionalInfo;
    private final Double amount;

    public MCUserAutomaticChange(String userName, Double amount, String additionalInfo) {
        super(String.class);
        this.userName = userName;
        this.additionalInfo = additionalInfo;
        this.amount = amount;
    }    
    
    @Override
    public void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            boolean automaticChangeActive = false;
            if (userConfig.has("automaticChange") && userConfig.get("automaticChange").has("active")) {
                automaticChangeActive = userConfig.get("automaticChange").get("active").booleanValue();
            }
            if (automaticChangeActive) {
                String currency = userConfig.get("automaticChange").get("currency").textValue();
                String inLimits = BaseOperation.inLimits(userName, currency, amount, BalanceOperationType.MC_AUTOMATIC_CHANGE);
                if (!inLimits.equals("OK")) {
                    super.response = inLimits;
                    return;
                }
                JsonNode offers = new OTCGetOffers(currency, "MONEYCLICK", OfferType.ASK, PaymentType.MAIN, false).getResponse();
                if (offers.has(currency) && offers.get(currency).has("ASK__MONEYCLICK__MAIN") && offers.get(currency).get("ASK__MONEYCLICK__MAIN").has("price")) {
                    Double price = offers.get(currency).get("ASK__MONEYCLICK__MAIN").get("price").doubleValue();
                    String substractToBalance = BaseOperation.substractToBalance(
                            UsersFolderLocator.getMCBalanceFolder(userName),
                            "BTC",
                            amount,
                            BalanceOperationType.MC_AUTOMATIC_CHANGE,
                            BalanceOperationStatus.OK,
                            additionalInfo,
                            price,
                            false,
                            null,
                            false,
                            null
                    );
                    if (!substractToBalance.equals("OK")) {
                        super.response = substractToBalance;
                        return;
                    }
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(userName),
                            currency,
                            amount * price,
                            BalanceOperationType.MC_AUTOMATIC_CHANGE,
                            BalanceOperationStatus.OK,
                            additionalInfo,
                            price,
                            BaseOperation.getChargesNew(currency, amount, BalanceOperationType.MC_AUTOMATIC_CHANGE, null, "MONEYCLICK", null, null),
                            false,
                            null
                    );
                }
            } else {
                super.response = "AUTOMATIC CHANGE NOT PROCESSED";
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(MCUserAutomaticChange.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "AUTOMATIC CHANGE NOT PROCESSED";
    }
    
}
