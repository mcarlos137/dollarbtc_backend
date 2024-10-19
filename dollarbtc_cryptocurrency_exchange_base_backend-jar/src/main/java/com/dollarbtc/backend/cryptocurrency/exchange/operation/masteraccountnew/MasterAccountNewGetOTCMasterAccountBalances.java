/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount.MasterAccountGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountNewGetOTCMasterAccountBalances extends AbstractOperation<JsonNode> {
    
    private final Object userNameOrOTCMasterAccounts;

    public MasterAccountNewGetOTCMasterAccountBalances(Object userNameOrOTCMasterAccounts) {
        super(JsonNode.class);
        this.userNameOrOTCMasterAccounts = userNameOrOTCMasterAccounts;
    }
    
    @Override
    protected void execute() {
        if(this.userNameOrOTCMasterAccounts instanceof String){
            super.response = method((String) userNameOrOTCMasterAccounts);
        } else if(this.userNameOrOTCMasterAccounts instanceof List){
            super.response = method((List<String>) userNameOrOTCMasterAccounts);
        }
    }
    
    private JsonNode method(String userName) {
        JsonNode balances = mapper.createObjectNode();
        Iterator<JsonNode> otcMasterAccountNamesIterator = new MasterAccountNewGetOTCMasterAccountNames(userName).getResponse().iterator();
        while (otcMasterAccountNamesIterator.hasNext()) {
            JsonNode otcMasterAccountNamesIt = otcMasterAccountNamesIterator.next();
            File masterAccountSpecificFolder = MasterAccountFolderLocator.getFolderByMasterAccountName(otcMasterAccountNamesIt.get("name").textValue());
            if (!masterAccountSpecificFolder.isDirectory()) {
                continue;
            }
            String masterAccountName = masterAccountSpecificFolder.getName();
            ((ObjectNode) balances).set(masterAccountName, new MasterAccountGetBalance(masterAccountName).getResponse());
        }
        return balances;
    }
    
    private JsonNode method(List<String> otcMasterAccounts) {
        JsonNode balances = mapper.createObjectNode();
        for (String otcMasterAccount : otcMasterAccounts) {
            File masterAccountSpecificFolder = MasterAccountFolderLocator.getFolderByMasterAccountName(otcMasterAccount);
            if (masterAccountSpecificFolder.isDirectory()) {
                ((ObjectNode) balances).set(otcMasterAccount, new MasterAccountGetBalance(otcMasterAccount).getResponse());
            }
        }
        return balances;
    }
    
}
