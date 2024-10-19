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
public class MarketModulatorFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "MarketModulator"));
    }
    
    public static File getAutomaticRulesFile() {
        return new File(getFolder(), "automaticRules.json");
    }

    public static File getManualRulesFile() {
        return new File(getFolder(), "manualRules.json");
    }

    public static File getActiveSymbolsFile() {
        return new File(getFolder(), "activeSymbols.json");
    }
    
}
