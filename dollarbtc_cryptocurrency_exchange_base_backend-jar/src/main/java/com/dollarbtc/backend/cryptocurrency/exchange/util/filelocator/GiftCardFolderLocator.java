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
public class GiftCardFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "GiftCard"));
    }
    
    public static File getActivatedFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "ACTIVATED"));
    }
    
    public static File getSubmittedFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "SUBMITTED"));
    }
    
    public static File getRedeemedFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "REDEEMED"));
    }
    
    public static File getDeletedFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "DELETED"));
    }
        
}
