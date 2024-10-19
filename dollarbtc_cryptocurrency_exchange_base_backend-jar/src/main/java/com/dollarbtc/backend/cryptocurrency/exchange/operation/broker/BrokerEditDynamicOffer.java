/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerEditDynamicOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BrokerEditDynamicOffer extends AbstractOperation<String> {

    private final BrokerEditDynamicOfferRequest brokerEditDynamicOfferRequest;

    public BrokerEditDynamicOffer(BrokerEditDynamicOfferRequest brokerEditDynamicOfferRequest) {
        super(String.class);
        this.brokerEditDynamicOfferRequest = brokerEditDynamicOfferRequest;
    }

    @Override
    protected void execute() {
        JsonNode offerParams = new BrokerGetOfferParams(brokerEditDynamicOfferRequest.getCurrency()).getResponse();
        if (!offerParams.has("prices") || !offerParams.has("marginPercents") || !offerParams.has("spreadPercents")) {
            super.response = "BASE OFFER PARAMS ARE WRONG, CHECK SERVER CONFIGURATION";
            return;
        }
        if (brokerEditDynamicOfferRequest.getOfferType().equals(OfferType.ASK)) {
            if (offerParams.get("marginPercents").get("askMin").doubleValue() > brokerEditDynamicOfferRequest.getMarginPercent()) {
                super.response = "OFFER MARGIN PERCENT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("marginPercents").get("askMin").doubleValue();
                return;
            }
            if (offerParams.get("spreadPercents").get("askMin").doubleValue() > brokerEditDynamicOfferRequest.getSpreadPercent()) {
                super.response = "OFFER SPREAD PERCENT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("spreadPercents").get("askMin").doubleValue();
                return;
            }
            if (brokerEditDynamicOfferRequest.getMinPerOperationAmount() != null && brokerEditDynamicOfferRequest.getMinPerOperationAmount() < offerParams.get("limits").get("askBottom").doubleValue()) {
                super.response = "OFFER MINIMAL AMOUNT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("limits").get("askBottom").doubleValue();
                return;
            }
            if (brokerEditDynamicOfferRequest.getMaxPerOperationAmount() != null && brokerEditDynamicOfferRequest.getMaxPerOperationAmount() > offerParams.get("limits").get("askTop").doubleValue()) {
                super.response = "OFFER MAXIMAL AMOUNT MUST BE EQUAL OR LOWER THAN " + offerParams.get("limits").get("askTop").doubleValue();
                return;
            }
        } else if (brokerEditDynamicOfferRequest.getOfferType().equals(OfferType.BID)) {
            if (offerParams.get("marginPercents").get("bidMin").doubleValue() > brokerEditDynamicOfferRequest.getMarginPercent()) {
                super.response = "OFFER MARGIN PERCENT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("marginPercents").get("bidMin").doubleValue();
                return;
            }
            if (offerParams.get("spreadPercents").get("bidMin").doubleValue() > brokerEditDynamicOfferRequest.getSpreadPercent()) {
                super.response = "OFFER SPREAD PERCENT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("spreadPercents").get("bidMin").doubleValue();
                return;
            }
            if (brokerEditDynamicOfferRequest.getMinPerOperationAmount() != null && brokerEditDynamicOfferRequest.getMinPerOperationAmount() < offerParams.get("limits").get("bidBottom").doubleValue()) {
                super.response = "OFFER MINIMAL AMOUNT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("limits").get("bidBottom").doubleValue();
                return;
            }
            if (brokerEditDynamicOfferRequest.getMaxPerOperationAmount() != null && brokerEditDynamicOfferRequest.getMaxPerOperationAmount() > offerParams.get("limits").get("bidTop").doubleValue()) {
                super.response = "OFFER MAXIMAL AMOUNT MUST BE EQUAL OR LOWER THAN " + offerParams.get("limits").get("bidTop").doubleValue();
                return;
            }
        }
        File brokerOfferFolder = BrokersFolderLocator.getOfferFolder(brokerEditDynamicOfferRequest.getUserName(), brokerEditDynamicOfferRequest.getCurrency(), brokerEditDynamicOfferRequest.getOfferType(), brokerEditDynamicOfferRequest.getPaymentId(), brokerEditDynamicOfferRequest.getPaymentType());
        if (!brokerOfferFolder.isDirectory()) {
            super.response = "OFFER DOES NOT EXIST";
            return;
        }
        brokerEditDynamicOfferRequest.getOfferType();
        for (File brokerOfferFile : brokerOfferFolder.listFiles()) {
            if (!brokerOfferFile.isFile()) {
                continue;
            }
            try {
                JsonNode brokerOffer = new ObjectMapper().readTree(brokerOfferFile);
                if (brokerEditDynamicOfferRequest.getSource() != null && !brokerEditDynamicOfferRequest.getSource().equals("")) {
                    ((ObjectNode) brokerOffer).put("source", brokerEditDynamicOfferRequest.getSource());
                }
                if (brokerEditDynamicOfferRequest.getLimitPrice() != null) {
                    ((ObjectNode) brokerOffer).put("limitPrice", brokerEditDynamicOfferRequest.getLimitPrice());
                }
                if (brokerEditDynamicOfferRequest.getMarginPercent() != null) {
                    ((ObjectNode) brokerOffer).put("marginPercent", brokerEditDynamicOfferRequest.getMarginPercent());
                }
                if (brokerEditDynamicOfferRequest.getSpreadPercent() != null) {
                    ((ObjectNode) brokerOffer).put("spreadPercent", brokerEditDynamicOfferRequest.getSpreadPercent());
                }
                if (brokerEditDynamicOfferRequest.getMinPerOperationAmount() != null) {
                    ((ObjectNode) brokerOffer).put("minPerOperationAmount", brokerEditDynamicOfferRequest.getMinPerOperationAmount());
                }
                if (brokerEditDynamicOfferRequest.getMaxPerOperationAmount() != null) {
                    ((ObjectNode) brokerOffer).put("maxPerOperationAmount", brokerEditDynamicOfferRequest.getMaxPerOperationAmount());
                }
                if (brokerEditDynamicOfferRequest.getTotalAmount() != null) {
                    ((ObjectNode) brokerOffer).put("totalAmount", brokerEditDynamicOfferRequest.getTotalAmount());
                }
                FileUtil.editFile(brokerOffer, brokerOfferFile);
                super.response = "OK";
                return;
            } catch (IOException ex) {
                Logger.getLogger(BrokerEditDynamicOffer.class.getName()).log(Level.SEVERE, null, ex);
            }
            super.response = "FAIL";
            return;
        }
        super.response = "OFFER DOES NOT EXIST";
    }

}
