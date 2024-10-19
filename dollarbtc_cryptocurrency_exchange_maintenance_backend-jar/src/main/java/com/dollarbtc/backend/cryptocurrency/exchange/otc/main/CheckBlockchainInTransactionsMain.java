/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.blockchain.BlockchainCheckInTransactions;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserListWithReceivedTransactions;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class CheckBlockchainInTransactionsMain {

    public static void main(String[] args) {
        Iterator<JsonNode> listIterator = new UserListWithReceivedTransactions("BTC").getResponse().iterator();
        while (listIterator.hasNext()) {
            JsonNode listIt = listIterator.next();
            String userName = listIt.textValue();
            Logger.getLogger(CheckBlockchainInTransactionsMain.class.getName()).log(Level.INFO, "userName {0}", userName);
            new BlockchainCheckInTransactions(userName, new String[]{"mcWallets"}).getResponse();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(CheckBlockchainInTransactionsMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }  
    
}
