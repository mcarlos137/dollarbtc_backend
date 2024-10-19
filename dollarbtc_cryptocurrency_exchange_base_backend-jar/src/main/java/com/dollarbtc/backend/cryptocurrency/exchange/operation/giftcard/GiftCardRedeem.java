/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardRedeemRequest;
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
public class GiftCardRedeem extends AbstractOperation<String> {

    private final GiftCardRedeemRequest giftCardRedeemRequest;

    public GiftCardRedeem(GiftCardRedeemRequest giftCardRedeemRequest) {
        super(String.class);
        this.giftCardRedeemRequest = giftCardRedeemRequest;
    }

    @Override
    protected void execute() {
        File giftCardFile = new File(GiftCardFolderLocator.getRedeemedFolder(), giftCardRedeemRequest.getId() + ".json");
        if (giftCardFile.isFile()) {
            super.response = "GIFT CARD ID IS ALREADY REDEEMED";
            return;
        }
        giftCardFile = new File(GiftCardFolderLocator.getActivatedFolder(), giftCardRedeemRequest.getId() + ".json");
        if (!giftCardFile.isFile()) {
            giftCardFile = new File(GiftCardFolderLocator.getSubmittedFolder(), giftCardRedeemRequest.getId() + ".json");
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
            if (giftCardRedeemRequest.getSource() != null
                    && !giftCardRedeemRequest.getSource().equals("")
                    && !giftCardRedeemRequest.getSource().equals(source)) {
                super.response = "GIFT CARD SOURCE DOES NOT MATCH";
                return;
            }
            BalanceOperationType balanceOperationType = BalanceOperationType.GIFT_CARD_REDEEM;
            JsonNode fastChangeFactor = null;
            String inLimits = null;
            switch (source) {
                case "BR":
                    if (giftCardRedeemRequest.getTargetAmount() == null) {
                        super.response = "THERE IS NO TARGET AMOUNT FOR BITCOINRECHARGE SOURCE";
                        return;
                    }
                    balanceOperationType = BalanceOperationType.GIFT_CARD_REDEEM_BR;
                    fastChangeFactor = new MCUserGetFastChangeFactor(currency, "BTC").getResponse();
                    if (!fastChangeFactor.has("factor")) {
                        super.response = "THERE IS NO PRICE FOR THIS CURRENCY";
                        return;
                    }
                    inLimits = BaseOperation.inLimits(giftCardRedeemRequest.getUserName(), currency, amount, BalanceOperationType.GIFT_CARD_REDEEM_BR);
                    if (!inLimits.equals("OK")) {
                        super.response = inLimits;
                        return;
                    }
                    break;
                case "MC":
                    inLimits = BaseOperation.inLimits(giftCardRedeemRequest.getUserName(), currency, amount, BalanceOperationType.GIFT_CARD_REDEEM);
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
            additionals.put("giftCardId", giftCardRedeemRequest.getId());
            additionals.put("operationId", giftCardRedeemRequest.getId());
            if (giftCard.has("upfrontCommission") && giftCard.get("upfrontCommission").booleanValue()) {
                charges = null;
            }
            ((ObjectNode) giftCard).put("redeemedTimestamp", timestamp);
            ((ObjectNode) giftCard).put("targetUserName", giftCardRedeemRequest.getUserName());
            ((ObjectNode) giftCard).set("charges", charges);
            ((ObjectNode) giftCard).put("source", source);
            switch (source) {
                case "BR":
                    if (fastChangeFactor == null || !fastChangeFactor.has("factor")) {
                        super.response = "THERE IS NO PRICE FOR THIS CURRENCY";
                        return;
                    }
                    if (giftCardRedeemRequest.getTargetAmount() == null) {
                        super.response = "THERE IS NO TARGET AMOUNT FOR BITCOINRECHARGE SOURCE";
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
                    Double btcAmount = amountMinusCharges / currentPrice;
                    Double factorPercentDiff = (giftCardRedeemRequest.getTargetAmount() - btcAmount) / btcAmount;
                    if (factorPercentDiff > 0.02 || factorPercentDiff < -0.02) {
                        super.response = "TARGET AMOUNT MUST BE CHANGED";
                        return;
                    }
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(giftCardRedeemRequest.getUserName()),
                            currency,
                            amount,
                            BalanceOperationType.GIFT_CARD_REDEEM_BR,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            charges,
                            false,
                            additionals
                    );
                    String substractToBalance = BaseOperation.substractToBalance(
                            UsersFolderLocator.getMCBalanceFolder(giftCardRedeemRequest.getUserName()),
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
                                UsersFolderLocator.getMCBalanceFolder(giftCardRedeemRequest.getUserName()),
                                BalanceOperationStatus.FAIL,
                                giftCardRedeemRequest.getId(),
                                "operationId",
                                "OPERATION FAILED"
                        );
                        super.response = substractToBalance;
                        return;
                    }
                    redeem(giftCard, giftCardFile);
                    File moneyclickCryptoBuysFolder = MoneyclickFolderLocator.getCryptoBuysFolder();
                    ObjectNode moneyclickCryptoBuy = mapper.createObjectNode();
                    moneyclickCryptoBuy.put("operationId", giftCardRedeemRequest.getId());
                    moneyclickCryptoBuy.put("userName", giftCardRedeemRequest.getUserName());
                    moneyclickCryptoBuy.put("currency", currency);
                    moneyclickCryptoBuy.put("timestamp", timestamp);
                    moneyclickCryptoBuy.put("cryptoCurrency", "BTC");
                    moneyclickCryptoBuy.put("cryptoAmount", btcAmount);
                    FileUtil.createFile(moneyclickCryptoBuy, new File(moneyclickCryptoBuysFolder, giftCardRedeemRequest.getId() + ".json"));
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(giftCardRedeemRequest.getUserName()),
                            "BTC",
                            btcAmount,
                            BalanceOperationType.GIFT_CARD_REDEEM_BR,
                            BalanceOperationStatus.OK,
                            null,
                            null,
                            null,
                            false,
                            additionals
                    );
                    break;
                case "MC":
                    redeem(giftCard, giftCardFile);
                    BaseOperation.addToBalance(
                            UsersFolderLocator.getMCBalanceFolder(giftCardRedeemRequest.getUserName()),
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
                    break;
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(GiftCardRedeem.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void redeem(JsonNode giftCard, File giftCardFile) throws IOException {
        FileUtil.editFile(giftCard, giftCardFile);
        FileUtil.moveFileToFolder(giftCardFile, GiftCardFolderLocator.getRedeemedFolder());
        File userGiftCardFile = UsersFolderLocator.getGiftCardFile(giftCardRedeemRequest.getUserName());
        JsonNode userGiftCard = mapper.readTree(userGiftCardFile);
        if (!userGiftCard.has("REDEEMED")) {
            ((ObjectNode) userGiftCard).set("REDEEMED", mapper.createObjectNode());
        }
        ((ObjectNode) giftCard).remove("targetUserName");
        ((ObjectNode) userGiftCard.get("REDEEMED")).set(giftCardRedeemRequest.getId(), giftCard);
        FileUtil.editFile(userGiftCard, userGiftCardFile);
    }

}
