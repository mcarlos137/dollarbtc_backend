/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardActivateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.GiftCardFolderLocator;
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
public class GiftCardActivate extends AbstractOperation<String> {

    private final GiftCardActivateRequest giftCardActivateRequest;

    public GiftCardActivate(GiftCardActivateRequest giftCardActivateRequest) {
        super(String.class);
        this.giftCardActivateRequest = giftCardActivateRequest;
    }

    @Override
    protected void execute() {
        if (new File(GiftCardFolderLocator.getActivatedFolder(), giftCardActivateRequest.getId() + ".json").isFile()) {
            super.response = "GIFT CARD ALREADY ACTIVATED";
            return;
        }
        if (new File(GiftCardFolderLocator.getSubmittedFolder(), giftCardActivateRequest.getId() + ".json").isFile() && giftCardActivateRequest.getEmail() != null) {
            super.response = "GIFT CARD ALREADY SUBMITTED";
            return;
        }
        if (new File(GiftCardFolderLocator.getRedeemedFolder(), giftCardActivateRequest.getId() + ".json").isFile()) {
            super.response = "GIFT CARD ALREADY REDEEMED";
            return;
        }
        ObjectNode additionals = mapper.createObjectNode();
        additionals.put("giftCardId", giftCardActivateRequest.getId());
        String result = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(giftCardActivateRequest.getUserName()),
                giftCardActivateRequest.getCurrency(),
                giftCardActivateRequest.getAmount(),
                BalanceOperationType.GIFT_CARD_ACTIVATION,
                BalanceOperationStatus.OK,
                null,
                null,
                false,
                null,
                false,
                additionals
        );
        if (!result.equals("OK")) {
            super.response = result;
            return;
        }
        if (giftCardActivateRequest.isUpfrontCommission()) {
            JsonNode charges = BaseOperation.getChargesNew(
                    giftCardActivateRequest.getCurrency(),
                    giftCardActivateRequest.getAmount(),
                    BalanceOperationType.GIFT_CARD_ACTIVATION,
                    null,
                    "MONEYCLICK",
                    null,
                    null
            );
            Double amount = 0.0;
            if (charges != null) {
                Iterator<JsonNode> chargesIterator = charges.iterator();
                while (chargesIterator.hasNext()) {
                    JsonNode chargesIt = chargesIterator.next();
                    if (chargesIt.get("currency").textValue().equals(giftCardActivateRequest.getCurrency())) {
                        amount = amount + chargesIt.get("amount").doubleValue();
                    }
                }
            }
            BaseOperation.addToBalance(
                    UsersFolderLocator.getMCBalanceFolder(giftCardActivateRequest.getUserName()),
                    giftCardActivateRequest.getCurrency(),
                    amount,
                    BalanceOperationType.GIFT_CARD_ACTIVATION,
                    BalanceOperationStatus.OK,
                    "SELL GIFT CARD COMMISSION",
                    null,
                    null,
                    false,
                    additionals
            );
        }
        JsonNode giftCard = mapper.createObjectNode();
        ((ObjectNode) giftCard).put("id", giftCardActivateRequest.getId());
        ((ObjectNode) giftCard).put("activationTimestamp", DateUtil.getCurrentDate());
        ((ObjectNode) giftCard).put("baseUserName", giftCardActivateRequest.getUserName());
        ((ObjectNode) giftCard).put("currency", giftCardActivateRequest.getCurrency());
        ((ObjectNode) giftCard).put("amount", giftCardActivateRequest.getAmount());
        if (giftCardActivateRequest.getLanguage() != null && !giftCardActivateRequest.getLanguage().equals("")) {
            ((ObjectNode) giftCard).put("language", giftCardActivateRequest.getLanguage());
        }
        if (giftCardActivateRequest.getEmail() != null && !giftCardActivateRequest.getEmail().equals("")) {
            ((ObjectNode) giftCard).put("email", giftCardActivateRequest.getEmail());
        }
        String source = "MC";
        if (giftCardActivateRequest.getSource() != null && !giftCardActivateRequest.getSource().equals("")) {
            source = giftCardActivateRequest.getSource();
        }
        ((ObjectNode) giftCard).put("source", source);
        String status = "ACTIVATED";
        if ((source.equals("MC")
                || source.equals("BR")) && giftCardActivateRequest.getEmail() != null) {
            status = "SUBMITTED";
        }
        ((ObjectNode) giftCard).put("upfrontCommission", giftCardActivateRequest.isUpfrontCommission());
        FileUtil.createFile(giftCard, new File(GiftCardFolderLocator.getFolder(), status), giftCardActivateRequest.getId() + ".json");
        File userGiftCardFile = UsersFolderLocator.getGiftCardFile(giftCardActivateRequest.getUserName());
        try {
            JsonNode userGiftCard = mapper.readTree(userGiftCardFile);
            if (!userGiftCard.has(status)) {
                ((ObjectNode) userGiftCard).set(status, mapper.createObjectNode());
            }
            ((ObjectNode) giftCard).remove("baseUserName");
            ((ObjectNode) userGiftCard.get(status)).set(giftCardActivateRequest.getId(), giftCard);
            FileUtil.editFile(userGiftCard, userGiftCardFile);
        } catch (IOException ex) {
            Logger.getLogger(GiftCardActivate.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (status.equals("ACTIVATED")) {
            super.response = "OK";
            return;
        }
        super.response = "OK__SEND";
    }

}
