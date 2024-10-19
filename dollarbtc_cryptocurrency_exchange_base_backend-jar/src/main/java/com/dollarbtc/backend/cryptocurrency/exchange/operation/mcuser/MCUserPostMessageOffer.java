/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserPostMessageOfferRequest;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserPostMessageOffer extends AbstractOperation<String> {

    private final MCUserPostMessageOfferRequest mcUserPostMessageOfferRequest;

    public MCUserPostMessageOffer(MCUserPostMessageOfferRequest mcUserPostMessageOfferRequest) {
        super(String.class);
        this.mcUserPostMessageOfferRequest = mcUserPostMessageOfferRequest;
    }

    @Override
    protected void execute() {
        File moneyclickMessageOfferPairTypeFolder = MoneyclickFolderLocator.getMessageOfferPairTypeFolder(mcUserPostMessageOfferRequest.getPair(), mcUserPostMessageOfferRequest.getType().name());
        if (moneyclickMessageOfferPairTypeFolder == null || !moneyclickMessageOfferPairTypeFolder.isDirectory()) {
            super.response = "PAIR IS NOT ALLOWED";
            return;
        }
        String id = BaseOperation.getId();
        try {
            if (!mcUserPostMessageOfferRequest.isBot()) {
                String pairBaseCurrency = getCurrency(mcUserPostMessageOfferRequest.getPair(), true);
                String pairQuoteCurrency = getCurrency(mcUserPostMessageOfferRequest.getPair(), false);
                switch (mcUserPostMessageOfferRequest.getType()) {
                    case ASK:
                        //POST USER ASK - SUBSTRACT BASE
                        String substractToBalance = BaseOperation.substractToBalance(
                                UsersFolderLocator.getMCBalanceFolder(mcUserPostMessageOfferRequest.getUserName()),
                                pairBaseCurrency,
                                mcUserPostMessageOfferRequest.getAmount(),
                                BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
                                BalanceOperationStatus.OK,
                                "AMOUNT BLOCKED BY MONEY MARKET OFFER " + id,
                                null,
                                false,
                                null,
                                false,
                                null
                        );
                        if (!substractToBalance.equals("OK")) {
                            super.response = substractToBalance;
                            return;
                        }
                        break;
                    case BID:
                        //POST USER BID - SUBSTRACT QUOTE
                        substractToBalance = BaseOperation.substractToBalance(
                                UsersFolderLocator.getMCBalanceFolder(mcUserPostMessageOfferRequest.getUserName()),
                                pairQuoteCurrency,
                                mcUserPostMessageOfferRequest.getAmount() * mcUserPostMessageOfferRequest.getPrice(),
                                BalanceOperationType.MC_MESSAGE_OFFER_CHANGE,
                                BalanceOperationStatus.OK,
                                "AMOUNT BLOCKED BY MONEY MARKET OFFER " + id,
                                null,
                                false,
                                null,
                                false,
                                null
                        );
                        if (!substractToBalance.equals("OK")) {
                            super.response = substractToBalance;
                            return;
                        }
                        break;
                }
            }
            JsonNode messageOffer = mapper.createObjectNode();
            String timestamp = DateUtil.getCurrentDate();
            ((ObjectNode) messageOffer).put("id", id);
            ((ObjectNode) messageOffer).put("timestamp", timestamp);
            ((ObjectNode) messageOffer).put("postUserName", mcUserPostMessageOfferRequest.getUserName());
            ((ObjectNode) messageOffer).put("postNickName", mcUserPostMessageOfferRequest.getNickName());
            ((ObjectNode) messageOffer).put("pair", mcUserPostMessageOfferRequest.getPair());
            ((ObjectNode) messageOffer).put("type", mcUserPostMessageOfferRequest.getType().name());
            ((ObjectNode) messageOffer).put("amount", mcUserPostMessageOfferRequest.getAmount());
            ((ObjectNode) messageOffer).put("price", mcUserPostMessageOfferRequest.getPrice());
            ((ObjectNode) messageOffer).put("time", mcUserPostMessageOfferRequest.getTime());
            ((ObjectNode) messageOffer).put("timeUnit", mcUserPostMessageOfferRequest.getTimeUnit());
            ((ObjectNode) messageOffer).put("bot", mcUserPostMessageOfferRequest.isBot());
            FileUtil.createFile(messageOffer, new File(moneyclickMessageOfferPairTypeFolder, id + ".json"));
            if (!mcUserPostMessageOfferRequest.isBot() && !mcUserPostMessageOfferRequest.isExcludeMessage()) {
                postMessage(mcUserPostMessageOfferRequest.getUserName(), mcUserPostMessageOfferRequest.getPair() + "__" + mcUserPostMessageOfferRequest.getType().name() + "__" + id, messageOffer, timestamp);
            }
            createUserNameFile(mcUserPostMessageOfferRequest.getUserName(), mcUserPostMessageOfferRequest.getPair(), id, mcUserPostMessageOfferRequest.getType(), timestamp);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCUserPostMessageOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private void postMessage(String userName, String chatRoom, JsonNode messageOffer, String timestamp) {
        File mcUserMessagesFolder = UsersFolderLocator.getMCMessagesFolder(userName, chatRoom);
        ObjectNode mcUserMessage = mapper.createObjectNode();
        String fileName = DateUtil.getFileDate(timestamp) + ".json";
        mcUserMessage.put("id", timestamp);
        mcUserMessage.put("senderUserName", userName);
        mcUserMessage.put("chatRoom", chatRoom);
        mcUserMessage.put("timestamp", timestamp);
        mcUserMessage.set("offer", messageOffer);
        mcUserMessage.put("version", 1);
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

    private void createUserNameFile(String userName, String pair, String id, OfferType offerType, String timestamp) {
        File messageOffersUserNameFile = MoneyclickFolderLocator.getMessageOffersUserNameFile(userName);
        JsonNode messageOffersUserName = null;
        if(messageOffersUserNameFile.isFile()){
            try {
                messageOffersUserName = mapper.readTree(messageOffersUserNameFile);
            } catch (IOException ex) {
                Logger.getLogger(MCUserPostMessageOffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(messageOffersUserName == null){
            messageOffersUserName = mapper.createObjectNode();
        }
        JsonNode userMessageOfferInfo = mapper.createObjectNode();
        ((ObjectNode) userMessageOfferInfo).put("timestamp", timestamp);
        ((ObjectNode) userMessageOfferInfo).put("pair", pair);
        ((ObjectNode) userMessageOfferInfo).put("type", offerType.name());
        ((ObjectNode) messageOffersUserName).set(id, userMessageOfferInfo);
        FileUtil.editFile(messageOffersUserName, messageOffersUserNameFile);
    }

}
