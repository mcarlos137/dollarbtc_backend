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
public class BaseFilesLocator {

    public static File getLoginAccountsFile() {
        return new File(OPERATOR_PATH, "loginAccounts.json");
    }

    public static File getBaseTestAmountsFile() {
        return new File(OPERATOR_PATH, "baseTestAmounts.json");
    }

    public static File getHmacFile() {
        return new File(OPERATOR_PATH, "hmac.json");
    }

    public static File getClientsBalanceFile() {
        return new File(OPERATOR_PATH, "clientsBalance.json");
    }

    public static File getBlackListFile() {
        return new File(OPERATOR_PATH, "blackList.json");
    }
    
    public static File getAWSFile() {
        return new File(OPERATOR_PATH, "aws.json");
    }

    public static File getJettyServerFile() {
        return new File(OPERATOR_PATH, "jettyServer.json");
    }
    
    public static File getOperatorsFile() {
        return new File(OPERATOR_PATH, "operators.json");
    }
    
    public static File getUserVerificationFieldsFile() {
        return new File(OPERATOR_PATH, "userVerificationFields.json");
    }
    
    public static File getUserVerificationFieldsNewFile() {
        return new File(OPERATOR_PATH, "userVerificationFieldsNew.json");
    }
    
    public static File getPlaidFile() {
        return new File(OPERATOR_PATH, "plaid.json");
    }
    
    public static File getDwollaFile() {
        return new File(OPERATOR_PATH, "dwolla.json");
    }
    
    public static File getNewHmacFile() {
        return new File(OPERATOR_PATH, "hmac.json");
    }
    
    public static File getProcessingBalanceFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "ProcessingBalance"));
    }
    
    public static File getReferralCodesFile() {
        return new File(OPERATOR_PATH, "referralCodes.json");
    }
    
}
