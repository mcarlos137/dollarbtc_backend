/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountGetDetails extends AbstractOperation<ArrayNode> {

    public MasterAccountGetDetails() {
        super(ArrayNode.class);
    }
    
    @Override
    public void execute() {
        ArrayNode details = mapper.createArrayNode();
        //REVISAR
        File masterAccountFolder = MasterAccountFolderLocator.getFolder(null);
        for (File masterAccountSpecificFolder : masterAccountFolder.listFiles()) {
            if (!masterAccountSpecificFolder.isDirectory()) {
                continue;
            }
            ObjectNode detail = mapper.createObjectNode();
            detail.put("name", masterAccountSpecificFolder.getName());
            if (new File(masterAccountSpecificFolder, "Clients").isDirectory()) {
                detail.put("transferToClients", true);
            } else {
                detail.put("transferToClients", false);
            }
            details.add(detail);
        }
        this.response = details;
    }
    
}
