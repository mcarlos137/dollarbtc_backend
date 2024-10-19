/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

/**
 *
 * @author ricardo torres
 */
public class UsersFlagsFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "UsersFlags"));
    }
    
    public static File getColorFile(String flagColor) {
        File colorFile = new File(getFolder(), flagColor + ".json");
        if(!colorFile.isFile()){
            FileUtil.createFile(new ObjectMapper().createObjectNode(), colorFile);
        }
        return colorFile;
    }

}
