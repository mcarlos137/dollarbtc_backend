/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountGetBalance extends AbstractOperation<JsonNode> {
    
    private final String masterAccountName;

    public MasterAccountGetBalance(String masterAccountName) {
        super(JsonNode.class);
        this.masterAccountName = masterAccountName;
    }
    
    @Override
    public void execute() {
        new MasterAccountAddWallet(masterAccountName, false).getResponse();
        super.response = BaseOperation.getBalance(MasterAccountFolderLocator.getBalanceFolder(masterAccountName));
    }
    
}
