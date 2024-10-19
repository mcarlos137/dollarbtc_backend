/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class BrokersFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Brokers"));
    }
    
    public static File getFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), userName));
    }

    public static File getOfferFile() {
        return new File(getFolder(), "offer.json");
    }

    public static File getOffersFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "Offers"));
    }
    
    public static File getBalanceFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "Balance"));
    }

    public static File getOfferFolder(String userName, String currency, OfferType offerType, String paymentId, PaymentType paymentType) {
        return FileUtil.createFolderIfNoExist(new File(getOffersFolder(userName), currency + "__" + offerType.name() + "__" + paymentId + "__" + paymentType.name()));
    }

    public static File getOfferOperationsFolder(String userName, String currency, OfferType offerType, String paymentId, PaymentType paymentType) {
        return FileUtil.createFolderIfNoExist(new File(getOfferFolder(userName, currency, offerType, paymentId, paymentType), "Operations"));
    }

    public static File getOfferOperationsOfferIdFolder(String userName, String currency, OfferType offerType, String paymentId, PaymentType paymentType, String offerTimestamp) {
        return FileUtil.createFolderIfNoExist(new File(getOfferOperationsFolder(userName, currency, offerType, paymentId, paymentType), offerTimestamp));
    }

}
