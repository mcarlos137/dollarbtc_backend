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
 * @author carlos molina
 */
public class OTCFolderLocator {

    public static File getFolder(String operatorName) {
        if(operatorName == null){
            return new File(OPERATOR_PATH, "OTC");
        } else {
            return new File(new File(OPERATOR_PATH, operatorName), "OTC");
        }
    }
    
    public static File getOperationsFolder(String operatorName) {
        return new File(getFolder(operatorName), "Operations");
    }
    
    public static File getOperationsIndexesFolder(String operatorName) {
        return new File(getOperationsFolder(operatorName), "Indexes");
    }

    public static File getOperationsChangeStatusesFolder(String operatorName) {
        return FileUtil.createFolderIfNoExist(new File(getOperationsFolder(operatorName), "ChangeStatuses"));
    }
    
    public static File getOperationsChangeStatusesUserNameFolder(String operatorName, String userName) {
        return FileUtil.createFolderIfNoExist(new File(getOperationsChangeStatusesFolder(operatorName), userName));
    }

    public static File getOperationsChangeStatusesOldFolder(String operatorName) {
        return FileUtil.createFolderIfNoExist(new File(getOperationsChangeStatusesFolder(operatorName), "Old"));
    }

    public static File getOperationsIndexesSpecificFolder(String operatorName, String index) {
        return FileUtil.createFolderIfNoExist(getOperationsIndexesFolder(operatorName), index);
    }
        
    public static File getOperationIdFolder(String operatorName, String id) {
        return new File(getOperationsFolder(operatorName), id);
    }

    public static File getOperationIdFile(String operatorName, String id) {
        return new File(getOperationIdFolder(operatorName, id), "operation.json");
    }

    public static File getOperationIdMessagesFolder(String operatorName, String id) {
        return new File(getOperationIdFolder(operatorName, id), "Messages");
    }
    
    public static File getOperationIdAttachmentsFolder(String operatorName, String id) {
        return FileUtil.createFolderIfNoExist(new File(getOperationIdFolder(operatorName, id), "Attachments"));
    }
    
    public static File getOperationIdAttachmentFile(String operatorName, String id, String fileName) {
        return new File(getOperationIdAttachmentsFolder(operatorName, id), fileName);
    }

    public static File getOperationIdMessagesSideFolder(String operatorName, String id, String side) {
        return new File(getOperationIdMessagesFolder(operatorName, id), side);
    }

    public static File getOperationIdMessagesSideOldFolder(String operatorName, String id, String side) {
        return new File(getOperationIdMessagesSideFolder(operatorName, id, side), "Old");
    }

    public static File getCurrencyFolder(String operatorName, String currency) {
        return new File(getFolder(operatorName), currency);
    }

    public static File getCurrencyFile(String operatorName, String currency) {
        return new File(getCurrencyFolder(operatorName, currency), "config.json");
    }

    public static File getCurrencyOffersFolder(String operatorName, String currency) {
        return new File(getCurrencyFolder(operatorName, currency), "Offers");
    }

    public static File getCurrencyOffersTypeFolder(String operatorName, String currency, OfferType offerType, String paymentId, PaymentType paymentType) {
        return new File(getCurrencyOffersFolder(operatorName, currency), offerType.name() + "__" + paymentId + "__" + paymentType.name());
    }

    public static File getCurrencyOffersTypeOperationsFolder(String operatorName, String currency, OfferType offerType, String paymentId, PaymentType paymentType) {
        return FileUtil.createFolderIfNoExist(new File(getCurrencyOffersTypeFolder(operatorName, currency, offerType, paymentId, paymentType), "Operations"));
    }

    public static File getCurrencyOffersTypeOperationsOfferIdFolder(String operatorName, String currency, OfferType offerType, String paymentId, PaymentType paymentType, String offerTimestamp) {
        return FileUtil.createFolderIfNoExist(new File(getCurrencyOffersTypeOperationsFolder(operatorName, currency, offerType, paymentId, paymentType), offerTimestamp));
    }

    public static File getCurrencyPaymentsFolder(String operatorName, String currency) {
        return FileUtil.createFolderIfNoExist(getCurrencyFolder(operatorName, currency), "Payments");
    }

    public static File getCurrencyPaymentFolder(String operatorName, String currency, String id) {
        return new File(getCurrencyPaymentsFolder(operatorName, currency), id);
    }

    public static File getCurrencyPaymentFile(String operatorName, String currency, String id) {
        return new File(getCurrencyPaymentFolder(operatorName, currency, id), "config.json");
    }

    public static File getCurrencyPaymentBalanceFolder(String operatorName, String currency, String id) {
        return new File(getCurrencyPaymentFolder(operatorName, currency, id), "Balance");
    }
    
    public static File getCurrencyPaymentCommissionsBalanceFolder(String operatorName, String currency, String id) {
        return FileUtil.createFolderIfNoExist(new File(getCurrencyPaymentFolder(operatorName, currency, id), "CommissionsBalance"));
    }

    public static File getCurrencyChargesFile(String operatorName, String currency) {
        if(currency.equals("BTC")){
            return new File(getFolder(null), "charges.json");
        }
        return new File(getCurrencyFolder(operatorName, currency), "charges.json");
    }
    
    public static File getCurrencyChargesNewFile(String operatorName, String currency) {
        if(currency.equals("BTC")){
            return new File(getFolder(null), "chargesNew.json");
        }
        return new File(getCurrencyFolder(operatorName, currency), "chargesNew.json");
    }
    
    public static File getCurrencyLimitsFile(String operatorName, String currency) {
        if(currency.equals("BTC")){
            return new File(getFolder(null), "limits.json");
        }
        return new File(getCurrencyFolder(operatorName, currency), "limits.json");
    }
    
    public static File getCurrencyOfficesInfoFolder(String operatorName, String currency) {
        return new File(getCurrencyFolder(operatorName, currency), "OfficesInfo");
    }
    
    public static File getCurrencySpecialPaymentsFile(String operatorName, String currency) {
        return new File(getCurrencyFolder(operatorName, currency), "specialPayments.json");
    }
    
    public static File getCurrencyOfficesInfoFile(String operatorName, String currency, String officesInfoId) {
        return new File(getCurrencyOfficesInfoFolder(operatorName, currency), officesInfoId + ".json");
    }
    
    public static File getAllowedAddPaymentsFile(String operatorName) {
        return new File(getFolder(operatorName), "allowedAddPayments.json");
    }
    
    public static File getChangeFactorsFile(String operatorName) {
        return new File(getFolder(operatorName), "changeFactors.json");
    }
    
}
