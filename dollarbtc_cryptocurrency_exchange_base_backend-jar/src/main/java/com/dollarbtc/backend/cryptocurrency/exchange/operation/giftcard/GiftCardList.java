/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.giftcard;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class GiftCardList extends AbstractOperation<JsonNode> {
    
    private final String userName;

    public GiftCardList(String userName) {
        super(JsonNode.class);
        this.userName = userName;
    }    

    @Override
    protected void execute() {
        File userGiftCardFile = UsersFolderLocator.getGiftCardFile(userName);
        try {
            super.response = mapper.readTree(userGiftCardFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(GiftCardList.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }
    
}
