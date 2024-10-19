/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class AddressesFolderLocator {
    
    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Addresses"));
    }
    
    public static File getCurrencyFolder(String currency) {
        return FileUtil.createFolderIfNoExist(getFolder(), currency);
    }
    
    public static File getCurrencyOperationsFolder(String currency, String status) {
        File currencyOperationsFolder = FileUtil.createFolderIfNoExist(getCurrencyFolder(currency), "Operations");
        return FileUtil.createFolderIfNoExist(currencyOperationsFolder, status);
    }
    
    public static File getCurrencyTransactionsTypeFolder(String currency, String type) {
        File currencyTransactionsFolder = FileUtil.createFolderIfNoExist(getCurrencyFolder(currency), "Transactions");
        return FileUtil.createFolderIfNoExist(currencyTransactionsFolder, type);
    }
    
    public static File getCurrencyTransactionsTypeUserFolder(String currency, String type, String userName) {
        return FileUtil.createFolderIfNoExist(getCurrencyTransactionsTypeFolder(currency, type), userName);
    }
    
}
