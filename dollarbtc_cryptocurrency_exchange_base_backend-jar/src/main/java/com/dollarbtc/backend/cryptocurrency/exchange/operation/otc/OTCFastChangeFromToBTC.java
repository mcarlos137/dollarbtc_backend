/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew.MasterAccountNewGetOTCMasterAccountName;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCFastChangeFromToBTC extends AbstractOperation<String> {

    private final String userName, currency, type;
    private final Double amount, price;

    public OTCFastChangeFromToBTC(String userName, String currency, Double amount, Double price, String type) {
        super(String.class);
        this.userName = userName;
        this.currency = currency;
        this.type = type;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public void execute() {
        File userBalanceFolder = UsersFolderLocator.getBalanceFolder(userName);
        if (!userBalanceFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        OfferType offerType;
        String addCurrency;
        String substractCurrency;
        Double substractAmount;
        if (type.equals("FROM")) {
            offerType = OfferType.BID;
            addCurrency = currency;
            substractCurrency = "BTC";
            substractAmount = amount / price;
        } else {
            offerType = OfferType.ASK;
            addCurrency = "BTC";
            substractCurrency = currency;
            substractAmount = amount * price;
        }
        PaymentType paymentType = PaymentType.MAIN;
        JsonNode offers = new OTCGetOffers(currency, "DOLLARBTC", offerType, paymentType, false).getResponse();
        if (!offers.has(currency) || !offers.get(currency).has(offerType.name() + "__DOLLARBTC__" + paymentType.name())) {
            super.response = "THERE IS NO OFFERS";
            return;
        }
        JsonNode offer = offers.get(currency).get(offerType.name() + "__DOLLARBTC__" + paymentType.name());
        Double offerPrice = offer.get("price").doubleValue();
        Double offerMinPerOperationAmount = offer.get("minPerOperationAmount").doubleValue();
        Double offerMaxPerOperationAmount = offer.get("maxPerOperationAmount").doubleValue();
        Double offerTotalAmount = offer.get("totalAmount").doubleValue();
        Double offerAccumulatedAmount = offer.get("accumulatedAmount").doubleValue();
        Double diffPercent = 2.0;
        if (price > (100 + diffPercent) * offerPrice / 100 || price < (100 - diffPercent) * offerPrice / 100) {
            super.response = "PRICE CHANGE. NEW PRICE: " + offerPrice;
            return;
        }
        double offerTotalAmountLeft = offerTotalAmount - offerAccumulatedAmount;
        if (offerTotalAmountLeft <= 0) {
            super.response = "OFFER WITH NO ENOUGH AMOUNT";
            return;
        } else {
            if (offerTotalAmountLeft < offerMinPerOperationAmount) {
                offerMinPerOperationAmount = offerTotalAmountLeft;
            }
            if (offerTotalAmountLeft < offerMaxPerOperationAmount) {
                offerMaxPerOperationAmount = offerTotalAmountLeft;
            }
        }
        if (amount < offerMinPerOperationAmount || amount > offerMaxPerOperationAmount) {
            super.response = "AMOUNT IS NOT BETWEEN MIN AND MAX " + offerMinPerOperationAmount + " - " + offerMaxPerOperationAmount;
            return;
        }
        String result = BaseOperation.substractToBalance(
                userBalanceFolder,
                substractCurrency,
                substractAmount,
                BalanceOperationType.FAST_CHANGE,
                BalanceOperationStatus.OK,
                "",
                price,
                false,
                BaseOperation.getChargesNew(currency, amount, BalanceOperationType.FAST_CHANGE, null, "OPERATOR__MASTER_ACCOUNT__" + new MasterAccountNewGetOTCMasterAccountName(null, "USD").getResponse().get("name").textValue(), null, null),
                false,
                null
        );
        if (!result.equals("OK")) {
            super.response = result;
            return;
        }
        BaseOperation.addToBalance(
                userBalanceFolder,
                addCurrency,
                amount,
                BalanceOperationType.FAST_CHANGE,
                BalanceOperationStatus.OK,
                "",
                price,
                null,
                false,
                null
        );
        super.response = "OK";
    }

}
