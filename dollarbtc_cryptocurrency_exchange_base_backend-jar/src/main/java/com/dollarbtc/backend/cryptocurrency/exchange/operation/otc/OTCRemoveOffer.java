/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCRemoveOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCRemoveOffer extends AbstractOperation<String> {

    private final OTCRemoveOfferRequest otcRemoveOfferRequest;

    public OTCRemoveOffer(OTCRemoveOfferRequest otcRemoveOfferRequest) {
        super(String.class);
        this.otcRemoveOfferRequest = otcRemoveOfferRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyFolder = OTCFolderLocator.getCurrencyFolder(null, otcRemoveOfferRequest.getCurrency());
        if (!otcCurrencyFolder.isDirectory()) {
            super.response = "CURRENCY DOES NOT EXIST";
            return;
        }
        File otcCurrencyOffersFolder = new File(otcCurrencyFolder, "Offers");
        if (!otcCurrencyOffersFolder.isDirectory()) {
            super.response = "OFFERS DO NOT EXIST";
            return;
        }
        String offerFileFolder = otcRemoveOfferRequest.getOfferType().name() + "__" + otcRemoveOfferRequest.getPaymentId() + "__" + otcRemoveOfferRequest.getPaymentType().name();
        File otcCurrencyOffersTypeFolder = new File(otcCurrencyOffersFolder, offerFileFolder);
        if (!otcCurrencyOffersTypeFolder.isDirectory()) {
            super.response = "TYPE DOES NOT EXIST";
            return;
        }
        File otcCurrencyOffersTypeOldFolder = FileUtil.createFolderIfNoExist(otcCurrencyOffersTypeFolder, "Old");
        for (File otcCurrencyOffersTypeFile : otcCurrencyOffersTypeFolder.listFiles()) {
            if (!otcCurrencyOffersTypeFile.isFile()) {
                continue;
            }
            FileUtil.moveFileToFolder(otcCurrencyOffersTypeFile, otcCurrencyOffersTypeOldFolder);
        }
        super.response = "OK";
    }

}
