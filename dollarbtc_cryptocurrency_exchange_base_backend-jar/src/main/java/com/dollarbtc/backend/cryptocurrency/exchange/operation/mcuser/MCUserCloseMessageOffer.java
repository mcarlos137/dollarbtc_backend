/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserCloseMessageOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class MCUserCloseMessageOffer extends AbstractOperation<String> {

    private final MCUserCloseMessageOfferRequest mcUserDeleteMessageOfferRequest;

    public MCUserCloseMessageOffer(MCUserCloseMessageOfferRequest mcUserCloseMessageOfferRequest) {
        super(String.class);
        this.mcUserDeleteMessageOfferRequest = mcUserCloseMessageOfferRequest;
    }

    @Override
    protected void execute() {
        File moneyclickMessegeOfferPairTypeIdFile = MoneyclickFolderLocator.getMessageOfferPairTypeIdFile(mcUserDeleteMessageOfferRequest.getPair(), mcUserDeleteMessageOfferRequest.getType().name(), mcUserDeleteMessageOfferRequest.getId());
        if (!moneyclickMessegeOfferPairTypeIdFile.isFile()) {
            super.response = "MESSAGE OFFER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode messageOffer = mapper.readTree(moneyclickMessegeOfferPairTypeIdFile);
            if (!messageOffer.get("postUserName").textValue().equals(mcUserDeleteMessageOfferRequest.getUserName())) {
                super.response = "THIS USER CAN NOT DELETE THIS OFFER";
                return;
            }
            boolean postUserBot = false;
            if (messageOffer.has("bot")) {
                postUserBot = messageOffer.get("bot").booleanValue();
            }
            boolean noEscrow = false;
            if (messageOffer.has("noEscrow")) {
                noEscrow = messageOffer.get("noEscrow").booleanValue();
            }
            String pairBaseCurrency = getCurrency(mcUserDeleteMessageOfferRequest.getPair(), true);
            String pairQuoteCurrency = getCurrency(mcUserDeleteMessageOfferRequest.getPair(), false);
            ((ObjectNode) messageOffer).put("closed", true);
            FileUtil.editFile(messageOffer, moneyclickMessegeOfferPairTypeIdFile);
            File moneyclickMessegeOfferPairTypeOldFolder = MoneyclickFolderLocator.getMessageOfferPairTypeOldFolder(mcUserDeleteMessageOfferRequest.getPair(), mcUserDeleteMessageOfferRequest.getType().name());
            FileUtil.moveFileToFolder(moneyclickMessegeOfferPairTypeIdFile, moneyclickMessegeOfferPairTypeOldFolder);
            Double leftAmount = messageOffer.get("amount").doubleValue();
            if (messageOffer.has("takeUserNames")) {
                Iterator<JsonNode> messageOfferTakeUserNamesIterator = messageOffer.get("takeUserNames").iterator();
                while (messageOfferTakeUserNamesIterator.hasNext()) {
                    JsonNode messageOfferTakeUserNamesIt = messageOfferTakeUserNamesIterator.next();
                    leftAmount = leftAmount - messageOfferTakeUserNamesIt.get("amount").doubleValue();
                }
            }
            switch (OfferType.valueOf(messageOffer.get("type").textValue())) {
                case ASK:
                    if (leftAmount > 0.0 && !postUserBot && !noEscrow) {
                        BaseOperation.addToBalance(
                                UsersFolderLocator.getMCBalanceFolder(messageOffer.get("postUserName").textValue()), //POST USER
                                pairBaseCurrency,
                                leftAmount,
                                BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
                                BalanceOperationStatus.OK,
                                "REFUND OF UNTRADED AMOUNT P2P ORDER " + messageOffer.get("id").textValue(),
                                null,
                                null,
                                false,
                                null
                        );
                    }
                    break;
                case BID:
                    if (leftAmount > 0.0 && !postUserBot && !noEscrow) {
                        BaseOperation.addToBalance(
                                UsersFolderLocator.getMCBalanceFolder(messageOffer.get("postUserName").textValue()), //POST USER
                                pairQuoteCurrency,
                                leftAmount,
                                BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
                                BalanceOperationStatus.OK,
                                "REFUND OF UNTRADED AMOUNT CHAT P2P OFFER " + messageOffer.get("id").textValue(),
                                null,
                                null,
                                false,
                                null
                        );
                    }
                    break;
            }
            if (!postUserBot && !mcUserDeleteMessageOfferRequest.isExcludeMessage()) {
                postMessage(messageOffer.get("postUserName").textValue(), mcUserDeleteMessageOfferRequest.getPair() + "__" + mcUserDeleteMessageOfferRequest.getType().name() + "__" + mcUserDeleteMessageOfferRequest.getId(), messageOffer);
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCUserCloseMessageOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void postMessage(String userName, String chatRoom, JsonNode messageOffer) {
        File mcUserMessagesFolder = UsersFolderLocator.getMCMessagesFolder(userName, chatRoom);
        ObjectNode mcUserMessage = mapper.createObjectNode();
        String timestamp = DateUtil.getCurrentDate();
        String fileName = DateUtil.getFileDate(timestamp) + ".json";
        mcUserMessage.put("id", timestamp);
        mcUserMessage.put("senderUserName", userName);
        mcUserMessage.put("chatRoom", chatRoom);
        mcUserMessage.put("timestamp", timestamp);
        mcUserMessage.set("offer", messageOffer);
        int version = 0;
        if (messageOffer.has("version")) {
            version = messageOffer.get("version").intValue();
        }
        mcUserMessage.put("version", version + 1);
        FileUtil.createFile(mcUserMessage, mcUserMessagesFolder, fileName);
        super.response = "OK";
    }

    private String getCurrency(String pair, Boolean base) throws IOException {
        JsonNode pairs = mapper.readTree(MoneyclickFolderLocator.getPairsFile());
        if (pairs.has(pair)) {
            if (base) {
                return pairs.get(pair).get("base").textValue();
            } else {
                return pairs.get(pair).get("quote").textValue();
            }
        }
        throw new IOException("PAIR DOES NOT EXIST");
    }

}
