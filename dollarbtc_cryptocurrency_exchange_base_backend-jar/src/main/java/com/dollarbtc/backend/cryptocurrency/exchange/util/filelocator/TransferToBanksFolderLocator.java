/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import java.io.File;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;

/**
 *
 * @author ricardo torres
 */
public class TransferToBanksFolderLocator {
    
    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "TransferToBanks"));
    }
    
    public static File getCSVFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "CSV"));
    }
    
    public static File getEmptyCSVFile() {
        return new File(getCSVFolder(), "empty.csv");
    }
    
    public static File getCSVFile(String fileName) {
        return new File(getCSVFolder(), fileName + ".csv");
    }
            
}
