/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class OTCNewFolderLocator {

    public static File getFolder(String operatorName) {
        if(operatorName == null){
            return new File(OPERATOR_PATH, "OTC");
        }
        return new File(new File(ExchangeUtil.OPERATOR_PATH, operatorName), "OTC");
    }
    
    public static File getCurrencyFolder(String operatorName, String currecy) {
        return new File(getFolder(operatorName), currecy);
    }

    public static File getCurrencyFile(String operatorName, String currency) {
        return new File(getCurrencyFolder(operatorName, currency), "config.json");
    }

    public static File getCurrencyOffersFolder(String operatorName, String currecy) {
        return new File(getCurrencyFolder(operatorName, currecy), "Offers");
    }

    public static File getCurrencyOffersTypeFolder(String operatorName, String currecy, OfferType offerType, String paymentId, PaymentType paymentType) {
        return new File(getCurrencyOffersFolder(operatorName, currecy), offerType.name() + "__" + paymentId + "__" + paymentType.name());
    }

    public static File getCurrencyOffersTypeOperationsFolder(String operatorName, String currecy, OfferType offerType, String paymentId, PaymentType paymentType) {
        return FileUtil.createFolderIfNoExist(new File(getCurrencyOffersTypeFolder(operatorName, currecy, offerType, paymentId, paymentType), "Operations"));
    }

    public static File getCurrencyOffersTypeOperationsOfferIdFolder(String operatorName, String currecy, OfferType offerType, String paymentId, PaymentType paymentType, String offerTimestamp) {
        return FileUtil.createFolderIfNoExist(new File(getCurrencyOffersTypeOperationsFolder(operatorName, currecy, offerType, paymentId, paymentType), offerTimestamp));
    }
        
}
