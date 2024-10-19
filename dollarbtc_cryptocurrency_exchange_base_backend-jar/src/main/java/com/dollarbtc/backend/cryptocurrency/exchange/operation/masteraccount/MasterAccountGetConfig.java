/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountGetConfig extends AbstractOperation<JsonNode> {
    
    private final String masterAccountName; 
    private final String SECURITY_CODE = "LocuRaTOatal34F";
    private final String securityCode;

    public MasterAccountGetConfig(String masterAccountName, String securityCode) {
        super(JsonNode.class);
        this.masterAccountName = masterAccountName;
        this.securityCode = securityCode;
    }
        
    @Override
    public void execute() {
        File masterAccountConfigFile = MasterAccountFolderLocator.getConfigFileByMasterAccountName(masterAccountName);
        if (!masterAccountConfigFile.isFile()) {
            FileUtil.createFile(mapper.createObjectNode(), masterAccountConfigFile);
        }
        JsonNode masterAccountConfig = mapper.createObjectNode();
        try {
            masterAccountConfig = mapper.readTree(masterAccountConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(MasterAccountGetConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] walletsTags = new String[]{"wallets"};
        for (String walletsTag : walletsTags) {
            if (masterAccountConfig.has(walletsTag)) {
                JsonNode wallets = masterAccountConfig.get(walletsTag);
                if (wallets.has("current")) {
                    Iterator<JsonNode> currentIterator = wallets.get("current").iterator();
                    while (currentIterator.hasNext()) {
                        JsonNode currentIt = currentIterator.next();
                        if (!securityCode.equals(SECURITY_CODE) && currentIt.has("privateKey")) {
                            ((ObjectNode) currentIt).remove("privateKey");
                        }
                    }
                }
                if (wallets.has("old")) {
                    Iterator<JsonNode> currentIterator = wallets.get("old").iterator();
                    while (currentIterator.hasNext()) {
                        JsonNode currentIt = currentIterator.next();
                        if (!securityCode.equals(SECURITY_CODE) && currentIt.has("privateKey")) {
                            ((ObjectNode) currentIt).remove("privateKey");
                        }
                    }
                }
            }
        }
        this.response = masterAccountConfig;
    }
    
}
