/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardRedeemNewRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetFastChangeFactor;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.GiftCardFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class GiftCardRedeemNew extends AbstractOperation<String> {

    private final GiftCardRedeemNewRequest giftCardRedeemNewRequest;

    public GiftCardRedeemNew(GiftCardRedeemNewRequest giftCardRedeemNewRequest) {
        super(String.class);
        this.giftCardRedeemNewRequest = giftCardRedeemNewRequest;
    }

    @Override
    protected void execute() {
        File giftCardFile = new File(GiftCardFolderLocator.getRedeemedFolder(), giftCardRedeemNewRequest.getId() + ".json");
        if (giftCardFile.isFile()) {
            super.response = "GIFT CARD ID IS ALREADY REDEEMED";
            return;
        }
        giftCardFile = new File(GiftCardFolderLocator.getActivatedFolder(), giftCardRedeemNewRequest.getId() + ".json");
        if (!giftCardFile.isFile()) {
            giftCardFile = new File(GiftCardFolderLocator.getSubmittedFolder(), giftCardRedeemNewRequest.getId() + ".json");
            if (!giftCardFile.isFile()) {
                super.response = "GIFT CARD ID DOES NOT EXIST";
                return;
            }
        }
        try {
            JsonNode giftCard = mapper.readTree(giftCardFile);
            String currency = giftCard.get("currency").textValue();
            Double amount = giftCard.get("amount").doubleValue();
            String timestamp = DateUtil.getCurrentDate();
            String source = "MC";
            if (giftCard.has("source") && !giftCard.get("source").textValue().equals("")) {
                source = giftCard.get("source").textValue();
            }
            if (giftCardRedeemNewRequest.getSource() != null
                    && !giftCardRedeemNewRequest.getSource().equals("")
                    && !giftCardRedeemNewRequest.getSource().equals(source)) {
                ((ObjectNode) giftCard).put("source", giftCardRedeemNewRequest.getSource());
                source = giftCardRedeemNewRequest.getSource();
            }
            BalanceOperationType balanceOperationType = BalanceOperationType.GIFT_CARD_REDEEM;
            JsonNode fastChangeFactor = null;
            String inLimits;
            String targetCurrency = null;
            switch (source) {
                case "BR":
                case "USD_COP":
                case "USD_CLP":
                case "USD_MXN":
                case "USD_ARS":
                case "USD_EUR":
                case "USD_PEN":
                case "USD_VES":
                case "USD_DOP":
                case "USD_USDT":
                case "USD_ETH":
                    if (source.equals("BR")) {
                        targetCurrency = "BTC";
                        balanceOperationType = BalanceOperationType.GIFT_CARD_REDEEM_BR;
                        inLimits = BaseOperation.inLimits(giftCardRedeemNewRequest.getUserName(), currency, amount, BalanceOperationType.GIFT_CARD_REDEEM_BR);
                    } else if (source.equals("USD_USDT") || source.equals("USD_ETH")) {
                        targetCurrency = source.split("_")[1];
                        balanceOperationType = BalanceOperationType.GIFT_CARD_REDEEM_BR;
                        inLimits = BaseOperation.inLimits(giftCardRedeemNewRequest.getUserName(), currency, amount, BalanceOperationType.GIFT_CARD_REDEEM_BR);
                    } else {
                        targetCurrency = source.split("_")[1];
                        balanceOperationType = BalanceOperationType.GIFT_CARD_REDEEM_FIAT;
                        inLimits = BaseOperation.inLimits(giftCardRedeemNewRequest.getUserName(), currency, amount, BalanceOperationType.GIFT_CARD_REDEEM_FIAT);
                    }
                    fastChangeFactor = new MCUserGetFastChangeFactor(currency, targetCurrency).getResponse();
                    if (!fastChangeFactor.has("factor")) {
                        super.response = "THERE IS NO PRICE FOR THIS CURRENCY";
                        return;
                    }
                    if (!inLimits.equals("OK")) {
                        super.response = inLimits;
                        return;
                    }
                    break;
                case "MC":
                    balanceOperationType = BalanceOperationType.GIFT_CARD_REDEEM;
                    inLimits = BaseOperation.inLimits(giftCardRedeemNewRequest.getUserName(), currency, amount, balanceOperationType);
                    if (!inLimits.equals("OK")) {
                        super.response = inLimits;
                        return;
                    }
                    break;
                case "TC":
                    balanceOperationType = BalanceOperationType.GIFT_CARD_REDEEM_TC;
                    inLimits = BaseOperation.inLimits(giftCardRedeemNewRequest.getUserName(), currency, amount, balanceOperationType);
                    if (!inLimits.equals("OK")) {
                        super.response = inLimits;
                        return;
                    }
                    break;
            }
            JsonNode charges = BaseOperation.getChargesNew(
                    currency,
                    amount,
                    balanceOperationType,
                    null,
                    "MONEYCLICK",
                    null,
                    null
            );
            ObjectNode additionals = mapper.createObjectNode();
            additionals.put("giftCardId", giftCardRedeemNewRequest.getId());
            additionals.put("operationId", giftCardRedeemNewRequest.getId());
            if (giftCard.has("upfrontCommission") && giftCard.get("upfrontCommission").booleanValue()) {
                charges = null;
            }
            ((ObjectNode) giftCard).put("redeemedTimestamp", timestamp);
            ((ObjectNode) giftCard).put("targetUserName", giftCardRedeemNewRequest.getUserName());
            ((ObjectNode) giftCard).set("charges", charges);
            ((ObjectNode) giftCard).put("source", source);
            if (targetCurrency != null && giftCardRedeemNewRequest.getTargetAmount() != null) {
                if (fastChangeFactor == null || !fastChangeFactor.has("factor")) {
                    super.response = "THERE IS NO PRICE FOR THIS CURRENCY";
                    return;
                }
                Double amountMinusCharges = amount;
                if (charges != null) {
                    Iterator<JsonNode> chargesIterator = charges.iterator();
                    while (chargesIterator.hasNext()) {
                        JsonNode chargesIt = chargesIterator.next();
                        if (chargesIt.get("currency").textValue().equals(currency)) {
                            amountMinusCharges = amountMinusCharges - chargesIt.get("amount").doubleValue();
                        }
                    }
                }
                Double currentPrice = 1 / fastChangeFactor.get("factor").doubleValue();
                Double targetAmount = amountMinusCharges / currentPrice;
                if ((targetAmount / giftCardRedeemNewRequest.getTargetAmount()) > 1.02
                        || (targetAmount / giftCardRedeemNewRequest.getTargetAmount()) < 0.98) {
                    super.response = "TARGET AMOUNT MUST BE CHANGED";
                    return;
                }
                targetAmount = giftCardRedeemNewRequest.getTargetAmount();
                BaseOperation.addToBalance(
                        UsersFolderLocator.getMCBalanceFolder(giftCardRedeemNewRequest.getUserName()),
                        currency,
                        amount,
                        balanceOperationType,
                        BalanceOperationStatus.OK,
                        null,
                        null,
                        charges,
                        false,
                        additionals
                );
                String substractToBalance = BaseOperation.substractToBalance(
                        UsersFolderLocator.getMCBalanceFolder(giftCardRedeemNewRequest.getUserName()),
                        currency,
                        amountMinusCharges,
                        balanceOperationType,
                        BalanceOperationStatus.OK,
                        null,
                        null,
                        false,
                        null,
                        false,
                        additionals
                );
                if (!substractToBalance.equals("OK")) {
                    BaseOperation.changeBalanceOperationStatus(
                            UsersFolderLocator.getMCBalanceFolder(giftCardRedeemNewRequest.getUserName()),
                            BalanceOperationStatus.FAIL,
                            giftCardRedeemNewRequest.getId(),
                            "operationId",
                            "OPERATION FAILED"
                    );
                    super.response = substractToBalance;
                    return;
                }
                redeem(giftCard, giftCardFile);
                BalanceOperationStatus balanceOperationStatus = BalanceOperationStatus.OK;
                if (targetCurrency.equals("BTC") || targetCurrency.equals("USDT") || targetCurrency.equals("ETH")) {
                    balanceOperationStatus = BalanceOperationStatus.PROCESSING;
                    File moneyclickCryptoBuysFolder = MoneyclickFolderLocator.getCryptoBuysFolder();
                    ObjectNode moneyclickCryptoBuy = mapper.createObjectNode();
                    moneyclickCryptoBuy.put("operationId", giftCardRedeemNewRequest.getId());
                    moneyclickCryptoBuy.put("userName", giftCardRedeemNewRequest.getUserName());
                    moneyclickCryptoBuy.put("currency", currency);
                    moneyclickCryptoBuy.put("timestamp", timestamp);
                    moneyclickCryptoBuy.put("cryptoCurrency", targetCurrency);
                    moneyclickCryptoBuy.put("cryptoAmount", targetAmount);
                    moneyclickCryptoBuy.put("type", "GIFTCARD");
                    FileUtil.createFile(moneyclickCryptoBuy, new File(moneyclickCryptoBuysFolder, giftCardRedeemNewRequest.getId() + ".json"));
                }
                BaseOperation.addToBalance(
                        UsersFolderLocator.getMCBalanceFolder(giftCardRedeemNewRequest.getUserName()),
                        targetCurrency,
                        targetAmount,
                        balanceOperationType,
                        balanceOperationStatus,
                        null,
                        null,
                        null,
                        false,
                        additionals
                );
            } else if (targetCurrency != null && giftCardRedeemNewRequest.getTargetAmount() == null) {
                super.response = "OPERATION NEEDS A TARGET AMOUNT";
                return;
            } else {
                redeem(giftCard, giftCardFile);
                BaseOperation.addToBalance(
                        UsersFolderLocator.getMCBalanceFolder(giftCardRedeemNewRequest.getUserName()),
                        currency,
                        amount,
                        BalanceOperationType.GIFT_CARD_REDEEM,
                        BalanceOperationStatus.OK,
                        null,
                        null,
                        charges,
                        false,
                        additionals
                );
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(GiftCardRedeemNew.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void redeem(JsonNode giftCard, File giftCardFile) throws IOException {
        FileUtil.editFile(giftCard, giftCardFile);
        FileUtil.moveFileToFolder(giftCardFile, GiftCardFolderLocator.getRedeemedFolder());
        File userGiftCardFile = UsersFolderLocator.getGiftCardFile(giftCardRedeemNewRequest.getUserName());
        JsonNode userGiftCard = mapper.readTree(userGiftCardFile);
        if (!userGiftCard.has("REDEEMED")) {
            ((ObjectNode) userGiftCard).set("REDEEMED", mapper.createObjectNode());
        }
        ((ObjectNode) giftCard).remove("targetUserName");
        ((ObjectNode) userGiftCard.get("REDEEMED")).set(giftCardRedeemNewRequest.getId(), giftCard);
        FileUtil.editFile(userGiftCard, userGiftCardFile);
    }

}
