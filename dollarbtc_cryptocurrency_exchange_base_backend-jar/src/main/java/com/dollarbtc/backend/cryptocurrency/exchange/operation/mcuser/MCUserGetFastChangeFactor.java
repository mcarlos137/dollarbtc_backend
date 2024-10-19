/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOffers;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin.OTCAdminGetChangeFactors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author carlosmolina
 */
public class MCUserGetFastChangeFactor extends AbstractOperation<JsonNode> {

    private final String baseCurrency, targetCurrency;

    public MCUserGetFastChangeFactor(String baseCurrency, String targetCurrency) {
        super(JsonNode.class);
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
    }

    @Override
    public void execute() {
        JsonNode fastChangeFactor = mapper.createObjectNode();
        ((ObjectNode) fastChangeFactor).put("baseCurrency", baseCurrency);
        ((ObjectNode) fastChangeFactor).put("targetCurrency", targetCurrency);
        Double changeFactorAmount = null;
        String changeFactorType = null;
        JsonNode changeFactors = new OTCAdminGetChangeFactors().getResponse();
        if (changeFactors.has(baseCurrency + "_" + targetCurrency)
                && changeFactors.get(baseCurrency + "_" + targetCurrency).has("amount")
                && changeFactors.get(baseCurrency + "_" + targetCurrency).has("type")
                && changeFactors.get(baseCurrency + "_" + targetCurrency).get("amount").doubleValue() > 0
                && changeFactors.get(baseCurrency + "_" + targetCurrency).get("type").textValue() != null
                && !changeFactors.get(baseCurrency + "_" + targetCurrency).get("type").textValue().equals("")
                && changeFactors.get(baseCurrency + "_" + targetCurrency).get("status").textValue() != null
                && !changeFactors.get(baseCurrency + "_" + targetCurrency).get("status").textValue().equals("")
                && changeFactors.get(baseCurrency + "_" + targetCurrency).get("status").textValue().equals("ACTIVE")) {
            changeFactorAmount = changeFactors.get(baseCurrency + "_" + targetCurrency).get("amount").doubleValue();
            changeFactorType = changeFactors.get(baseCurrency + "_" + targetCurrency).get("type").textValue();
        }
        if (changeFactorType != null && changeFactorType.equals("FIXED")) {
            ((ObjectNode) fastChangeFactor).put("factor", changeFactorAmount);
            super.response = fastChangeFactor;
            return;
        }
        if (baseCurrency.equals("BTC")) {
            JsonNode btcSellOffer = new OTCGetOffers(targetCurrency, "MONEYCLICK", OfferType.BID, PaymentType.MAIN, false).getResponse();
            if (!btcSellOffer.has(targetCurrency) || !btcSellOffer.get(targetCurrency).has("BID__MONEYCLICK__MAIN")) {
                super.response = fastChangeFactor;
                return;
            }
            ((ObjectNode) fastChangeFactor).put("factor", btcSellOffer.get(targetCurrency).get("BID__MONEYCLICK__MAIN").get("price").doubleValue());
            super.response = fastChangeFactor;
            return;
        }
        if (targetCurrency.equals("BTC")) {
            JsonNode btcBuyOffer = new OTCGetOffers(baseCurrency, "MONEYCLICK", OfferType.ASK, PaymentType.MAIN, false).getResponse();
            if (!btcBuyOffer.has(baseCurrency) || !btcBuyOffer.get(baseCurrency).has("ASK__MONEYCLICK__MAIN")) {
                super.response = fastChangeFactor;
                return;
            }
            ((ObjectNode) fastChangeFactor).put("factor", 1 / btcBuyOffer.get(baseCurrency).get("ASK__MONEYCLICK__MAIN").get("price").doubleValue());
            super.response = fastChangeFactor;
            return;
        }
        JsonNode baseCurrencyBuyBTCOffer = new OTCGetOffers(baseCurrency, "MONEYCLICK", OfferType.ASK, PaymentType.MAIN, false).getResponse();
        if (!baseCurrencyBuyBTCOffer.has(baseCurrency) || !baseCurrencyBuyBTCOffer.get(baseCurrency).has("ASK__MONEYCLICK__MAIN")) {
            super.response = fastChangeFactor;
            return;
        }
        JsonNode targetCurrencySellBTCOffer = new OTCGetOffers(targetCurrency, "MONEYCLICK", OfferType.BID, PaymentType.MAIN, false).getResponse();
        if (!targetCurrencySellBTCOffer.has(targetCurrency) || !targetCurrencySellBTCOffer.get(targetCurrency).has("BID__MONEYCLICK__MAIN")) {
            super.response = fastChangeFactor;
            return;
        }
        ((ObjectNode) fastChangeFactor).put("factor", targetCurrencySellBTCOffer.get(targetCurrency).get("BID__MONEYCLICK__MAIN").get("price").doubleValue() / baseCurrencyBuyBTCOffer.get(baseCurrency).get("ASK__MONEYCLICK__MAIN").get("price").doubleValue());
        if (changeFactorType != null && changeFactorType.equals("TOP") && fastChangeFactor.get("factor").doubleValue() > changeFactorAmount) {
            ((ObjectNode) fastChangeFactor).put("factor", changeFactorAmount);
        }
        super.response = fastChangeFactor;
    }

}
