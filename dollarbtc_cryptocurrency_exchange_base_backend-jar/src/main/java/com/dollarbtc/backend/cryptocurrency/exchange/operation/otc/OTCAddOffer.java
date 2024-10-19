/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddOfferRequest;
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
public class OTCAddOffer extends AbstractOperation<String> {

    private final OTCAddOfferRequest otcAddOfferRequest;

    public OTCAddOffer(OTCAddOfferRequest otcAddOfferRequest) {
        super(String.class);
        this.otcAddOfferRequest = otcAddOfferRequest;
    }

    @Override
    public void execute() {
        File otcCurrencyFolder = OTCFolderLocator.getCurrencyFolder(null, otcAddOfferRequest.getCurrency());
        if (!otcCurrencyFolder.isDirectory()) {
            super.response = "CURRENCY DOES NOT EXIST";
            return;
        }
        File otcCurrencyOffersFolder = FileUtil.createFolderIfNoExist(otcCurrencyFolder, "Offers");
        String offerFolderName = otcAddOfferRequest.getOfferType().name() + "__" + otcAddOfferRequest.getPaymentId() + "__" + otcAddOfferRequest.getPaymentType().name();
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
        ((ObjectNode) otcCurrencyOffer).put("price", otcAddOfferRequest.getPrice());
        ((ObjectNode) otcCurrencyOffer).put("minPerOperationAmount", otcAddOfferRequest.getMinPerOperationAmount());
        ((ObjectNode) otcCurrencyOffer).put("maxPerOperationAmount", otcAddOfferRequest.getMaxPerOperationAmount());
        ((ObjectNode) otcCurrencyOffer).put("totalAmount", otcAddOfferRequest.getTotalAmount());
        ((ObjectNode) otcCurrencyOffer).put("useChangePriceByOperationBalance", otcAddOfferRequest.isUseChangePriceByOperationBalance());
        try {
            String encryptedOfferKey = EncryptorBASE64.encrypt(otcAddOfferRequest.getCurrency() + "__" + otcAddOfferRequest.getOfferType() + "__" + otcAddOfferRequest.getPaymentId() + "__" + otcAddOfferRequest.getPaymentType());
            String subDomain = "";
            if (!OPERATOR_NAME.equals("MAIN")) {
                subDomain = OPERATOR_NAME.toLowerCase() + ".";
            }
            ((ObjectNode) otcCurrencyOffer).put("url", "https://" + subDomain + "dollarbtc.com?offerKey=" + encryptedOfferKey);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OTCAddOffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileUtil.createFile(otcCurrencyOffer, new File(otcCurrencyOffersTypeFolder, DateUtil.getFileDate(timestamp) + ".json"));
        super.response = "OK";
    }

}
