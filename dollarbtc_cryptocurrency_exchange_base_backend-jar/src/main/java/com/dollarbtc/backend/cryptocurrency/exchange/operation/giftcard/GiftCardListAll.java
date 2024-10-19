/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.GiftCardFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class GiftCardListAll extends AbstractOperation<ArrayNode> {
    
    private final String status;

    public GiftCardListAll(String status) {
        super(ArrayNode.class);
        this.status = status;
    }    

    @Override
    protected void execute() {
        File giftCardStatusFolder = new File(GiftCardFolderLocator.getFolder(), status);
        ArrayNode giftCardListAll =  mapper.createArrayNode();
        if(giftCardStatusFolder.isDirectory()){
            for(File giftCardStatusFile : giftCardStatusFolder.listFiles()){
                if(!giftCardStatusFile.isFile()){
                    continue;
                }
                try {
                    giftCardListAll.add(mapper.readTree(giftCardStatusFile));
                } catch (IOException ex) {
                    Logger.getLogger(GiftCardListAll.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        super.response = giftCardListAll;
    }
    
}
