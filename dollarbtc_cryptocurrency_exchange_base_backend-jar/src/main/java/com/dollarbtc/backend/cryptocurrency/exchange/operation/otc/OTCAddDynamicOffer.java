/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddDynamicOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.EncryptorBASE64;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
public class OTCAddDynamicOffer extends AbstractOperation<String> {

    private final OTCAddDynamicOfferRequest otcAddDynamicOfferRequest;

    public OTCAddDynamicOffer(OTCAddDynamicOfferRequest otcAddDynamicOfferRequest) {
        super(String.class);
        this.otcAddDynamicOfferRequest = otcAddDynamicOfferRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyFolder = OTCFolderLocator.getCurrencyFolder(null, otcAddDynamicOfferRequest.getCurrency());
        if (!otcCurrencyFolder.isDirectory()) {
            super.response = "CURRENCY DOES NOT EXIST";
            return;
        }
        File otcCurrencyOffersFolder = FileUtil.createFolderIfNoExist(otcCurrencyFolder, "Offers");
        String offerFolderName = otcAddDynamicOfferRequest.getOfferType().name() + "__" + otcAddDynamicOfferRequest.getPaymentId() + "__" + otcAddDynamicOfferRequest.getPaymentType().name();
        File otcCurrencyOffersTypeFolder = FileUtil.createFolderIfNoExist(otcCurrencyOffersFolder, offerFolderName);
        File otcCurrencyOffersTypeOldFolder = FileUtil.createFolderIfNoExist(otcCurrencyOffersTypeFolder, "Old");
        for (File otcCurrencyOfferTypeFile : otcCurrencyOffersTypeFolder.listFiles()) {
            if (!otcCurrencyOfferTypeFile.isFile()) {
                continue;
            }
            FileUtil.moveFileToFolder(otcCurrencyOfferTypeFile, otcCurrencyOffersTypeOldFolder);
        }
        String timestamp = DateUtil.getCurrentDate();
        JsonNode otcCurrencyOffer = mapper.createObjectNode();
        ((ObjectNode) otcCurrencyOffer).put("timestamp", timestamp);
        ((ObjectNode) otcCurrencyOffer).put("source", otcAddDynamicOfferRequest.getSource());
        ((ObjectNode) otcCurrencyOffer).put("limitPrice", otcAddDynamicOfferRequest.getLimitPrice());
        ((ObjectNode) otcCurrencyOffer).put("marginPercent", otcAddDynamicOfferRequest.getMarginPercent());
        ((ObjectNode) otcCurrencyOffer).put("spreadPercent", otcAddDynamicOfferRequest.getSpreadPercent());
        ((ObjectNode) otcCurrencyOffer).put("minPerOperationAmount", otcAddDynamicOfferRequest.getMinPerOperationAmount());
        ((ObjectNode) otcCurrencyOffer).put("maxPerOperationAmount", otcAddDynamicOfferRequest.getMaxPerOperationAmount());
        ((ObjectNode) otcCurrencyOffer).put("totalAmount", otcAddDynamicOfferRequest.getTotalAmount());
        ((ObjectNode) otcCurrencyOffer).put("useChangePriceByOperationBalance", otcAddDynamicOfferRequest.isUseChangePriceByOperationBalance());
        try {
            String encryptedOfferKey = EncryptorBASE64.encrypt(otcAddDynamicOfferRequest.getCurrency() + "__" + otcAddDynamicOfferRequest.getOfferType() + "__" + otcAddDynamicOfferRequest.getPaymentId() + "__" + otcAddDynamicOfferRequest.getPaymentType());
            String subDomain = "";
            if (!OPERATOR_NAME.equals("MAIN")) {
                subDomain = OPERATOR_NAME.toLowerCase() + ".";
            }
            ((ObjectNode) otcCurrencyOffer).put("url", "https://" + subDomain + "dollarbtc.com?offerKey=" + encryptedOfferKey);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OTCAddDynamicOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileUtil.createFile(otcCurrencyOffer, new File(otcCurrencyOffersTypeFolder, DateUtil.getFileDate(timestamp) + ".json"));
        super.response = "OK";
    }

}
