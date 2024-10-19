/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCEditOfferRequest;
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
public class OTCEditOffer extends AbstractOperation<String> {

    private final OTCEditOfferRequest otcEditOfferRequest;

    public OTCEditOffer(OTCEditOfferRequest otcEditOfferRequest) {
        super(String.class);
        this.otcEditOfferRequest = otcEditOfferRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyFolder = OTCFolderLocator.getCurrencyFolder(null, otcEditOfferRequest.getCurrency());
        if (!otcCurrencyFolder.isDirectory()) {
            super.response = "CURRENCY DOES NOT EXIST";
            return;
        }
        File otcCurrencyOffersFolder = new File(otcCurrencyFolder, "Offers");
        if (!otcCurrencyOffersFolder.isDirectory()) {
            super.response = "OFFERS DO NOT EXIST";
            return;
        }
        String offerFileFolder = otcEditOfferRequest.getOfferType().name() + "__" + otcEditOfferRequest.getPaymentId() + "__" + otcEditOfferRequest.getPaymentType().name();
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
                if (otcEditOfferRequest.getPrice() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("price", otcEditOfferRequest.getPrice());
                }
                if (otcEditOfferRequest.getMinPerOperationAmount() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("minPerOperationAmount", otcEditOfferRequest.getMinPerOperationAmount());
                }
                if (otcEditOfferRequest.getMaxPerOperationAmount() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("maxPerOperationAmount", otcEditOfferRequest.getMaxPerOperationAmount());
                }
                if (otcEditOfferRequest.getTotalAmount() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("totalAmount", otcEditOfferRequest.getTotalAmount());
                }
                if (otcEditOfferRequest.getUseChangePriceByOperationBalance() != null) {
                    ((ObjectNode) otcCurrencyOffersType).put("useChangePriceByOperationBalance", otcEditOfferRequest.getUseChangePriceByOperationBalance());
                }
                FileUtil.editFile(otcCurrencyOffersType, otcCurrencyOffersTypeFile);
                super.response = "OK";
                return;
            } catch (IOException ex) {
                Logger.getLogger(OTCEditOffer.class.getName()).log(Level.SEVERE, null, ex);
            }
            super.response = "FAIL";
            return;
        }
        super.response = "OFFER DOES NOT EXIST";
    }

}
