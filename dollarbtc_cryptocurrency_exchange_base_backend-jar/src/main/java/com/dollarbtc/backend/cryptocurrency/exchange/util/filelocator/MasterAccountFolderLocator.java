/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetOperatorName;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class MasterAccountFolderLocator {

    public static File getFolder(String operatorName) {
        if(operatorName != null){
            return new File(new File(OPERATOR_PATH, operatorName), "MasterAccount");
        }
        return new File(OPERATOR_PATH, "MasterAccount");
    }
    
    public static File getFolderByMasterAccountName(String masterAccountName) {
        return new File(getFolder(new MasterAccountGetOperatorName(masterAccountName).getResponse()), masterAccountName);
    }
    
    public static File getBalanceFolder(String masterAccountName) {
        return new File(getFolderByMasterAccountName(masterAccountName), "Balance");
    }

    public static File getConfigFile(String operatorName) {
        return new File(getFolder(operatorName), "config.json");
    }
    
    public static File getConfigFileByMasterAccountName(String masterAccountName) {
        return new File(getFolderByMasterAccountName(masterAccountName), "config.json");
    }

    public static File getAutomaticRulesFile(String operatorName) {
        File automaticRulesFile = new File(getFolder(operatorName), "automaticRules.json");
        if (!automaticRulesFile.isFile()) {
            FileUtil.createFile("", automaticRulesFile);
        }
        return automaticRulesFile;
    }
    
}
