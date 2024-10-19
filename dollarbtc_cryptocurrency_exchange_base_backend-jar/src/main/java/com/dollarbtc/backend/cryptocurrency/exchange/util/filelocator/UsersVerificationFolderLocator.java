/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class UsersVerificationFolderLocator {
    
    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "UsersVerification"));
    }
    
    public static File getStatusFolder(UserVerificationStatus userVerificationStatus) {
        return FileUtil.createFolderIfNoExist(getFolder(), userVerificationStatus.name());
    }
    
}
