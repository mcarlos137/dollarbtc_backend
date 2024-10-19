/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerEditOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
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
public class BrokerEditOffer extends AbstractOperation<String> {

    private final BrokerEditOfferRequest brokerEditOfferRequest;

    public BrokerEditOffer(BrokerEditOfferRequest brokerEditOfferRequest) {
        super(String.class);
        this.brokerEditOfferRequest = brokerEditOfferRequest;
    }

    @Override
    protected void execute() {
        JsonNode offerParams = new BrokerGetOfferParams(brokerEditOfferRequest.getCurrency()).getResponse();
        if (!offerParams.has("prices") || !offerParams.has("marginPercents") || !offerParams.has("spreadPercents")) {
            super.response = "BASE OFFER PARAMS ARE WRONG, CHECK SERVER CONFIGURATION";
            return;
        }
        if (brokerEditOfferRequest.getOfferType().equals(OfferType.ASK)) {
            if (brokerEditOfferRequest.getPrice() != null && offerParams.get("prices").get("askMin").doubleValue() > brokerEditOfferRequest.getPrice()) {
                super.response = "OFFER PRICE MUST BE EQUAL OR HIGHER THAN " + offerParams.get("prices").get("askMin").doubleValue();
                return;
            }
            if (brokerEditOfferRequest.getMinPerOperationAmount() != null && brokerEditOfferRequest.getMinPerOperationAmount() < offerParams.get("limits").get("askBottom").doubleValue()) {
                super.response = "OFFER MINIMAL AMOUNT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("limits").get("askBottom").doubleValue();
                return;
            }
            if (brokerEditOfferRequest.getMaxPerOperationAmount() != null && brokerEditOfferRequest.getMaxPerOperationAmount() > offerParams.get("limits").get("askTop").doubleValue()) {
                super.response = "OFFER MAXIMAL AMOUNT MUST BE EQUAL OR LOWER THAN " + offerParams.get("limits").get("askTop").doubleValue();
                return;
            }
        } else if (brokerEditOfferRequest.getOfferType().equals(OfferType.BID)) {
            if (brokerEditOfferRequest.getPrice() != null && offerParams.get("prices").get("bidMax").doubleValue() < brokerEditOfferRequest.getPrice()) {
                super.response = "OFFER PRICE MUST BE EQUAL OR LOWER THAN " + offerParams.get("prices").get("bidMax").doubleValue();
                return;
            }
            if (brokerEditOfferRequest.getMinPerOperationAmount() != null && brokerEditOfferRequest.getMinPerOperationAmount() < offerParams.get("limits").get("bidBottom").doubleValue()) {
                super.response = "OFFER MINIMAL AMOUNT MUST BE EQUAL OR HIGHER THAN " + offerParams.get("limits").get("bidBottom").doubleValue();
                return;
            }
            if (brokerEditOfferRequest.getMaxPerOperationAmount() != null && brokerEditOfferRequest.getMaxPerOperationAmount() > offerParams.get("limits").get("bidTop").doubleValue()) {
                super.response = "OFFER MAXIMAL AMOUNT MUST BE EQUAL OR LOWER THAN " + offerParams.get("limits").get("bidTop").doubleValue();
                return;
            }
        }
        File brokerOfferFolder = BrokersFolderLocator.getOfferFolder(brokerEditOfferRequest.getUserName(), brokerEditOfferRequest.getCurrency(), brokerEditOfferRequest.getOfferType(), brokerEditOfferRequest.getPaymentId(), brokerEditOfferRequest.getPaymentType());
        if (!brokerOfferFolder.isDirectory()) {
            super.response = "OFFER DOES NOT EXIST";
            return;
        }
        for (File brokerOfferFile : brokerOfferFolder.listFiles()) {
            if (!brokerOfferFile.isFile()) {
                continue;
            }
            try {
                JsonNode brokerOffer = mapper.readTree(brokerOfferFile);
                if (brokerEditOfferRequest.getPrice() != null) {
                    ((ObjectNode) brokerOffer).put("price", brokerEditOfferRequest.getPrice());
                }
                if (brokerEditOfferRequest.getMinPerOperationAmount() != null) {
                    ((ObjectNode) brokerOffer).put("minPerOperationAmount", brokerEditOfferRequest.getMinPerOperationAmount());
                }
                if (brokerEditOfferRequest.getMaxPerOperationAmount() != null) {
                    ((ObjectNode) brokerOffer).put("maxPerOperationAmount", brokerEditOfferRequest.getMaxPerOperationAmount());
                }
                if (brokerEditOfferRequest.getTotalAmount() != null) {
                    ((ObjectNode) brokerOffer).put("totalAmount", brokerEditOfferRequest.getTotalAmount());
                }
                FileUtil.editFile(brokerOffer, brokerOfferFile);
                super.response = "OK";
                return;
            } catch (IOException ex) {
                Logger.getLogger(BrokerEditOffer.class.getName()).log(Level.SEVERE, null, ex);
            }
            super.response = "FAIL";
            return;
        }
        super.response = "OFFER DOES NOT EXIST";
    }

}
