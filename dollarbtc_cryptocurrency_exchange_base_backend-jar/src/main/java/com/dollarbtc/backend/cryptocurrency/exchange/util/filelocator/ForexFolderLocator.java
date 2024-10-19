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
public class ForexFolderLocator {
    
    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Forex"));
    }
    
    public static File getRatesSymbolFolder(String symbol) {
        return FileUtil.createFolderIfNoExist(FileUtil.createFolderIfNoExist(getFolder(), "Rates"), symbol);
    }    
    
}