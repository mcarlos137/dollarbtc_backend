/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class BankersFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Bankers"));
    }
    
    public static File getFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), userName));
    }
    
    public static File getConfigFile(String userName) {
        return new File(getFolder(userName), "config.json");
    }
    
    public static File getPaymentsFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "Payments"));
    }

    public static File getPaymentsCurrencyFolder(String userName, String currency) {
        return FileUtil.createFolderIfNoExist(new File(getPaymentsFolder(userName), currency));
    }
    
    public static File getPaymentCurrencyFolder(String operatorName, String currency, String id) {
        return new File(getPaymentsCurrencyFolder(operatorName, currency), id);
    }

    public static File getPaymentCurrencyFile(String operatorName, String currency, String id) {
        return new File(getPaymentCurrencyFolder(operatorName, currency, id), "config.json");
    }
    
    public static File getOperationsFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "Operations"));
    }
    
    public static File getOperationIdFolder(String userName, String id) {
        return FileUtil.createFolderIfNoExist(new File(getOperationsFolder(userName), id));
    }
    
    public static File getOperationsIndexesFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getOperationsFolder(userName), "Indexes"));
    }
    
    public static File getOperationsIndexesSpecificFolder(String userName, String index) {
        return FileUtil.createFolderIfNoExist(getOperationsIndexesFolder(userName), index);
    }
    
    public static File getReferredUsersFile(String userName) {
        File referredUsersFile = new File(getFolder(userName), "referredUsers.json");
        if(!referredUsersFile.isFile()){
            FileUtil.createFile(new ObjectMapper().createArrayNode(), referredUsersFile);
        }
        return referredUsersFile;
    }
    
    public static File getEscrowFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "Escrow"));
    }
    
}
