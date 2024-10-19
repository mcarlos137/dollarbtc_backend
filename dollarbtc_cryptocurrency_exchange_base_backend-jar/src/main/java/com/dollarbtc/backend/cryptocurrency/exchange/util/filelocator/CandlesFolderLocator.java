/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class CandlesFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Candles"));
    }

    public static File getFastChangeFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "FastChange"));
    }

    public static File getFastChangeFolder(String baseCurrency) {
        return FileUtil.createFolderIfNoExist(new File(getFastChangeFolder(), baseCurrency));
    }
    
    public static File getFastChangeOldFolder(String baseCurrency) {
        return FileUtil.createFolderIfNoExist(new File(getFastChangeFolder(baseCurrency), "Old"));
    }
    
    public static File getChatP2PFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "ChatP2P"));
    }
    
    public static File getChatP2PFolder(String pair) {
        return FileUtil.createFolderIfNoExist(new File(getChatP2PFolder(), pair));
    }
    
    public static File getChatP2PFolder(String pair, String period) {
        return FileUtil.createFolderIfNoExist(new File(getChatP2PFolder(pair), period));
    }
    
    public static File getChatP2POldFolder(String pair, String period) {
        return FileUtil.createFolderIfNoExist(new File(getChatP2PFolder(pair, period), "Old"));
    }    

}
