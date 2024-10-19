/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerAddOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.EncryptorBASE64;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BrokerAddOffer extends AbstractOperation<String> {

    private final BrokerAddOfferRequest brokerAddOfferRequest;

    public BrokerAddOffer(BrokerAddOfferRequest brokerAddOfferRequest) {
        super(String.class);
        this.brokerAddOfferRequest = brokerAddOfferRequest;
    }

    @Override
    protected void execute() {
        JsonNode offerParams = new BrokerGetOfferParams(brokerAddOfferRequest.getCurrency()).getResponse();
        if (!offerParams.has("prices") || !offerParams.has("marginPercents") || !offerParams.has("spreadPercents")) {
            super.response = "BASE OFFER PARAMS ARE WRONG, CHECK SERVER CONFIGURATION";
            return;
        }
        if (brokerAddOfferRequest.getOfferType().equals(OfferType.ASK)) {
            if (offerParams.get("prices").get("askMin").doubleValue() > brokerAddOfferRequest.getPrice()) {
                super.response = "OFFER PRICE MUST BE EQUAL OR HIGHER THAN " + offerParams.get("prices").get("askMin").doubleValue();
                return;
            }
            if (brokerAddOfferRequest.getMinPerOperationAmount() < offerParams.get("limits").get("askBottom").doubleValue()) {
                super.response = "OFFER MINIMAL AMOUNT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("limits").get("askBottom").doubleValue();
                return;
            }
            if (brokerAddOfferRequest.getMaxPerOperationAmount() > offerParams.get("limits").get("askTop").doubleValue()) {
                super.response = "OFFER MAXIMAL AMOUNT MUST BE EQUAL OR LOWER THAN " + offerParams.get("limits").get("askTop").doubleValue();
                return;
            }
        } else if (brokerAddOfferRequest.getOfferType().equals(OfferType.BID)) {
            if (offerParams.get("prices").get("bidMax").doubleValue() < brokerAddOfferRequest.getPrice()) {
                super.response = "OFFER PRICE MUST BE EQUAL OR LOWER THAN " + offerParams.get("prices").get("bidMax").doubleValue();
                return;
            }
            if (brokerAddOfferRequest.getMinPerOperationAmount() < offerParams.get("limits").get("bidBottom").doubleValue()) {
                super.response = "OFFER MINIMAL AMOUNT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("limits").get("bidBottom").doubleValue();
                return;
            }
            if (brokerAddOfferRequest.getMaxPerOperationAmount() > offerParams.get("limits").get("bidTop").doubleValue()) {
                super.response = "OFFER MAXIMAL AMOUNT MUST BE EQUAL OR LOWER THAN " + offerParams.get("limits").get("bidTop").doubleValue();
                return;
            }
        }
        File brokerOfferFolder = BrokersFolderLocator.getOfferFolder(brokerAddOfferRequest.getUserName(), brokerAddOfferRequest.getCurrency(), brokerAddOfferRequest.getOfferType(), brokerAddOfferRequest.getPaymentId(), brokerAddOfferRequest.getPaymentType());
        File brokerOfferOldFolder = FileUtil.createFolderIfNoExist(new File(brokerOfferFolder, "Old"));
        for (File brokerOfferFile : brokerOfferFolder.listFiles()) {
            if (!brokerOfferFile.isFile()) {
                continue;
            }
            FileUtil.moveFileToFolder(brokerOfferFile, brokerOfferOldFolder);
        }
        String timestamp = DateUtil.getCurrentDate();
        JsonNode brokerOffer = mapper.createObjectNode();
        ((ObjectNode) brokerOffer).put("timestamp", timestamp);
        ((ObjectNode) brokerOffer).put("price", brokerAddOfferRequest.getPrice());
        ((ObjectNode) brokerOffer).put("minPerOperationAmount", brokerAddOfferRequest.getMinPerOperationAmount());
        ((ObjectNode) brokerOffer).put("maxPerOperationAmount", brokerAddOfferRequest.getMaxPerOperationAmount());
        ((ObjectNode) brokerOffer).put("totalAmount", brokerAddOfferRequest.getTotalAmount());
        try {
            String encryptedOfferKey = EncryptorBASE64.encrypt(brokerAddOfferRequest.getUserName() + "__" + brokerAddOfferRequest.getCurrency() + "__" + brokerAddOfferRequest.getOfferType() + "__" + brokerAddOfferRequest.getPaymentId() + "__" + brokerAddOfferRequest.getPaymentType());
            ((ObjectNode) brokerOffer).put("url", "https://www.dollarbtc.com?brokerOfferKey=" + encryptedOfferKey);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BrokerAddOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileUtil.createFile(brokerOffer, new File(brokerOfferFolder, DateUtil.getFileDate(timestamp) + ".json"));
        super.response = "OK";
    }

}
