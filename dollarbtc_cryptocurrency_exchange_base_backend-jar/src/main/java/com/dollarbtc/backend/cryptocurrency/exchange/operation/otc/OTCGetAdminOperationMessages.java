/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AdminFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetAdminOperationMessages extends AbstractOperation<ArrayNode> {
    
    private final Integer maxQuantity;

    public OTCGetAdminOperationMessages(Integer maxQuantity) {
        super(ArrayNode.class);
        this.maxQuantity = maxQuantity;
    }
        
    @Override
    public void execute() {
        ArrayNode adminOperationMessages = mapper.createArrayNode();
        File adminOperationMessagesFolder = AdminFolderLocator.getOperationMessagesFolder();
        for (File adminOperationMessageFile : adminOperationMessagesFolder.listFiles()) {
            if (!adminOperationMessageFile.isFile()) {
                continue;
            }
            try {
                JsonNode adminOperationMessage = mapper.readTree(adminOperationMessageFile);
                adminOperationMessages.add(adminOperationMessage);
                File adminOperationMessagesOldFolder = AdminFolderLocator.getOperationMessagesOldFolder();
                FileUtil.moveFileToFolder(adminOperationMessageFile, adminOperationMessagesOldFolder);
                if (adminOperationMessages.size() >= maxQuantity) {
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetAdminOperationMessages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = adminOperationMessages;
    }
    
}
