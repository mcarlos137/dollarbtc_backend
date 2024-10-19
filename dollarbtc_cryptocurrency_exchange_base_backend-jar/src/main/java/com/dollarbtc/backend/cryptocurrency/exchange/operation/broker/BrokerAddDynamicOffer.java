/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerAddDynamicOfferRequest;
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
public class BrokerAddDynamicOffer extends AbstractOperation<String> {

    private final BrokerAddDynamicOfferRequest brokerAddDynamicOfferRequest;

    public BrokerAddDynamicOffer(BrokerAddDynamicOfferRequest brokerAddDynamicOfferRequest) {
        super(String.class);
        this.brokerAddDynamicOfferRequest = brokerAddDynamicOfferRequest;
    }

    @Override
    protected void execute() {
        JsonNode offerParams = new BrokerGetOfferParams(brokerAddDynamicOfferRequest.getCurrency()).getResponse();
        if (!offerParams.has("prices") || !offerParams.has("marginPercents") || !offerParams.has("spreadPercents")) {
            super.response = "BASE OFFER PARAMS ARE WRONG, CHECK SERVER CONFIGURATION";
            return;
        }
        if (brokerAddDynamicOfferRequest.getOfferType().equals(OfferType.ASK)) {
            if (offerParams.get("marginPercents").get("askMin").doubleValue() > brokerAddDynamicOfferRequest.getMarginPercent()) {
                super.response = "OFFER MARGIN PERCENT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("marginPercents").get("askMin").doubleValue();
                return;
            }
            if (offerParams.get("spreadPercents").get("askMin").doubleValue() > brokerAddDynamicOfferRequest.getSpreadPercent()) {
                super.response = "OFFER SPREAD PERCENT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("spreadPercents").get("askMin").doubleValue();
                return;
            }
            if (brokerAddDynamicOfferRequest.getMinPerOperationAmount() < offerParams.get("limits").get("askBottom").doubleValue()) {
                super.response = "OFFER MINIMAL AMOUNT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("limits").get("askBottom").doubleValue();
                return;
            }
            if (brokerAddDynamicOfferRequest.getMaxPerOperationAmount() > offerParams.get("limits").get("askTop").doubleValue()) {
                super.response = "OFFER MAXIMAL AMOUNT MUST BE EQUAL OR LOWER THAN " + offerParams.get("limits").get("askTop").doubleValue();
                return;
            }
        } else if (brokerAddDynamicOfferRequest.getOfferType().equals(OfferType.BID)) {
            if (offerParams.get("marginPercents").get("bidMin").doubleValue() > brokerAddDynamicOfferRequest.getMarginPercent()) {
                super.response = "OFFER MARGIN PERCENT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("marginPercents").get("bidMin").doubleValue();
                return;
            }
            if (offerParams.get("spreadPercents").get("bidMin").doubleValue() > brokerAddDynamicOfferRequest.getSpreadPercent()) {
                super.response = "OFFER SPREAD PERCENT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("spreadPercents").get("bidMin").doubleValue();
                return;
            }
            if (brokerAddDynamicOfferRequest.getMinPerOperationAmount() < offerParams.get("limits").get("bidBottom").doubleValue()) {
                super.response = "OFFER MINIMAL AMOUNT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("limits").get("bidBottom").doubleValue();
                return;
            }
            if (brokerAddDynamicOfferRequest.getMaxPerOperationAmount() > offerParams.get("limits").get("bidTop").doubleValue()) {
                super.response = "OFFER MAXIMAL AMOUNT MUST BE EQUAL OR LOWER THAN " + offerParams.get("limits").get("bidTop").doubleValue();
                return;
            }
        }
        File brokerOfferFolder = BrokersFolderLocator.getOfferFolder(brokerAddDynamicOfferRequest.getUserName(), brokerAddDynamicOfferRequest.getCurrency(), brokerAddDynamicOfferRequest.getOfferType(), brokerAddDynamicOfferRequest.getPaymentId(), brokerAddDynamicOfferRequest.getPaymentType());
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
        ((ObjectNode) brokerOffer).put("source", brokerAddDynamicOfferRequest.getSource());
        ((ObjectNode) brokerOffer).put("limitPrice", brokerAddDynamicOfferRequest.getLimitPrice());
        ((ObjectNode) brokerOffer).put("marginPercent", brokerAddDynamicOfferRequest.getMarginPercent());
        ((ObjectNode) brokerOffer).put("spreadPercent", brokerAddDynamicOfferRequest.getSpreadPercent());
        ((ObjectNode) brokerOffer).put("minPerOperationAmount", brokerAddDynamicOfferRequest.getMinPerOperationAmount());
        ((ObjectNode) brokerOffer).put("maxPerOperationAmount", brokerAddDynamicOfferRequest.getMaxPerOperationAmount());
        ((ObjectNode) brokerOffer).put("totalAmount", brokerAddDynamicOfferRequest.getTotalAmount());
        try {
            String encryptedOfferKey = EncryptorBASE64.encrypt(brokerAddDynamicOfferRequest.getUserName() + "__" + brokerAddDynamicOfferRequest.getCurrency() + "__" + brokerAddDynamicOfferRequest.getOfferType() + "__" + brokerAddDynamicOfferRequest.getPaymentId() + "__" + brokerAddDynamicOfferRequest.getPaymentType());
            ((ObjectNode) brokerOffer).put("url", "https://www.dollarbtc.com?brokerOfferKey=" + encryptedOfferKey);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BrokerAddDynamicOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileUtil.createFile(brokerOffer, new File(brokerOfferFolder, DateUtil.getFileDate(timestamp) + ".json"));
        super.response = "OK";
    }

}
