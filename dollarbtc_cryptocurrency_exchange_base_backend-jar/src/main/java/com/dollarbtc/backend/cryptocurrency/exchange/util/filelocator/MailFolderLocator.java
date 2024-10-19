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
 * @author carlos molina
 */
public class MailFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Mail"));
    }
    
    public static File getSendedFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Sended"));
    }
    
    public static File getFailedFolder() {
        return FileUtil.createFolderIfNoExist(new File(getFolder(), "Failed"));
    }
            
}
