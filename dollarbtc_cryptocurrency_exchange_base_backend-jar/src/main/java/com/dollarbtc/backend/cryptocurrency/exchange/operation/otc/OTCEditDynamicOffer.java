/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCEditDynamicOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
public class OTCEditDynamicOffer extends AbstractOperation<String> {

    private final OTCEditDynamicOfferRequest otcEditDynamicOfferRequest;

    public OTCEditDynamicOffer(OTCEditDynamicOfferRequest otcEditDynamicOfferRequest) {
        super(String.class);
        this.otcEditDynamicOfferRequest = otcEditDynamicOfferRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyFolder = OTCFolderLocator.getCurrencyFolder(null, otcEditDynamicOfferRequest.getCurrency());
        if (!otcCurrencyFolder.isDirectory()) {
            super.response = "CURRENCY DOES NOT EXIST";
            return;
        }
        File otcCurrencyOffersFolder = new File(otcCurrencyFolder, "Offers");
        if (!otcCurrencyOffersFolder.isDirectory()) {
            super.response = "OFFERS DO NOT EXIST";
            return;
        }
        String offerFileFolder = otcEditDynamicOfferRequest.getOfferType().name() + "__" + otcEditDynamicOfferRequest.getPaymentId() + "__" + otcEditDynamicOfferRequest.getPaymentType().name();
        File otcCurrencyOffersTypeFolder = new File(otcCurrencyOffersFolder, offerFileFolder);
        if (!otcCurrencyOffersTypeFolder.isDirectory()) {
            super.response = "TYPE DOES NOT EXIST";
            return;
        }
        for (File otcCurrencyOffersTypeFile : otcCurrencyOffersTypeFolder.listFiles()) {
            if (!otcCurrencyOffersTypeFile.isFile()) {
                continue;
            }
            JsonNode otcCurrencyOffersType;
            try {
                otcCurrencyOffersType = mapper.readTree(otcCurrencyOffersTypeFile);
                if (otcEditDynamicOfferRequest.getSource() != null && !otcEditDynamicOfferRequest.getSource().equals("")) {
                    ((ObjectNode) otcCurrencyOffersType).put("source", otcEditDynamicOfferRequest.getSource());
                }
                if (otcEditDynamicOfferRequest.getLimitPrice() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("limitPrice", otcEditDynamicOfferRequest.getLimitPrice());
                }
                if (otcEditDynamicOfferRequest.getMarginPercent() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("marginPercent", otcEditDynamicOfferRequest.getMarginPercent());
                }
                if (otcEditDynamicOfferRequest.getSpreadPercent() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("spreadPercent", otcEditDynamicOfferRequest.getSpreadPercent());
                }
                if (otcEditDynamicOfferRequest.getMinPerOperationAmount() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("minPerOperationAmount", otcEditDynamicOfferRequest.getMinPerOperationAmount());
                }
                if (otcEditDynamicOfferRequest.getMaxPerOperationAmount() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("maxPerOperationAmount", otcEditDynamicOfferRequest.getMaxPerOperationAmount());
                }
                if (otcEditDynamicOfferRequest.getTotalAmount() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("totalAmount", otcEditDynamicOfferRequest.getTotalAmount());
                }
                if (otcEditDynamicOfferRequest.getUseChangePriceByOperationBalance() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("useChangePriceByOperationBalance", otcEditDynamicOfferRequest.getUseChangePriceByOperationBalance());
                }
                FileUtil.editFile(otcCurrencyOffersType, otcCurrencyOffersTypeFile);
                super.response = "OK";
                return;
            } catch (IOException ex) {
                Logger.getLogger(OTCEditDynamicOffer.class.getName()).log(Level.SEVERE, null, ex);
            }
            super.response = "FAIL";
            return;
        }
        super.response = "OFFER DOES NOT EXIST";
    }

}
