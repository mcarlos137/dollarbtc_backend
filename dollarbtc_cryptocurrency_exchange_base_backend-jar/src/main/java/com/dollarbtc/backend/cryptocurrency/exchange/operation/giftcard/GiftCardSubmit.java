/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard.GiftCardSubmitRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.GiftCardFolderLocator;
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
public class GiftCardSubmit extends AbstractOperation<String> {

    private final GiftCardSubmitRequest giftCardSubmitRequest;

    public GiftCardSubmit(GiftCardSubmitRequest giftCardSubmitRequest) {
        super(String.class);
        this.giftCardSubmitRequest = giftCardSubmitRequest;
    }

    @Override
    protected void execute() {
        File giftCardFile = new File(GiftCardFolderLocator.getRedeemedFolder(), giftCardSubmitRequest.getId() + ".json");
        if (giftCardFile.isFile()) {
            super.response = "GIFT CARD ID IS ALREADY REDEEMED";
            return;
        }
        giftCardFile = new File(GiftCardFolderLocator.getSubmittedFolder(), giftCardSubmitRequest.getId() + ".json");
        if (giftCardFile.isFile()) {
            super.response = "GIFT CARD ID IS ALREADY SUMITTED";
            return;
        }
        giftCardFile = new File(GiftCardFolderLocator.getActivatedFolder(), giftCardSubmitRequest.getId() + ".json");
        if (!giftCardFile.isFile()) {
            super.response = "GIFT CARD ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode giftCard = mapper.readTree(giftCardFile);
            String id = giftCard.get("id").textValue();
            String userName = giftCard.get("baseUserName").textValue();
            String timestamp = DateUtil.getCurrentDate();
            String source = "MC";
            if (giftCard.has("source") && !giftCard.get("source").textValue().equals("")) {
                source = giftCard.get("source").textValue();
            }
            if (giftCardSubmitRequest.getSource() != null
                    && !giftCardSubmitRequest.getSource().equals("")
                    && !giftCardSubmitRequest.getSource().equals(source)) {
                super.response = "GIFT CARD SOURCE DOES NOT MATCH";
                return;
            }

            ((ObjectNode) giftCard).put("submittedTimestamp", timestamp);
            ((ObjectNode) giftCard).put("source", source);
            FileUtil.editFile(giftCard, giftCardFile);
            FileUtil.moveFileToFolder(giftCardFile, GiftCardFolderLocator.getSubmittedFolder());
            File userGiftCardFile = UsersFolderLocator.getGiftCardFile(userName);
            JsonNode userGiftCard = mapper.readTree(userGiftCardFile);
            if (!userGiftCard.has("SUBMITTED")) {
                ((ObjectNode) userGiftCard).set("SUBMITTED", mapper.createObjectNode());
            }
            ((ObjectNode) userGiftCard.get("SUBMITTED")).set(id, giftCard);
            FileUtil.editFile(userGiftCard, userGiftCardFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(GiftCardSubmit.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
