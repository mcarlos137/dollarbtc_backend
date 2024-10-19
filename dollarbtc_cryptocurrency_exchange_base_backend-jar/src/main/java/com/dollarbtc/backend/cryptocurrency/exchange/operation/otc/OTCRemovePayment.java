/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCRemovePayment extends AbstractOperation<String> {
    
    private final String userName, currency, id;

    public OTCRemovePayment(String userName, String currency, String id) {
        super(String.class);
        this.userName = userName;
        this.currency = currency;
        this.id = id;
    }

    @Override
    protected void execute() {
        if(currency == null){
            super.response = method(userName, id);
        } else {
            super.response = method(userName, currency, id);
        }
    }
    
    private String method(String userName, String currency, String id) {
        File userOTCFolder = UsersFolderLocator.getOTCFolder(userName);
        if (!userOTCFolder.isDirectory()) {
            return "USERNAME DOES NOT EXIST";
        }
        File userOTCCurrencyPaymentsFile = new File(FileUtil.createFolderIfNoExist(userOTCFolder, currency), "payments.json");
        if (!userOTCCurrencyPaymentsFile.isFile()) {
            return "PAYMENT ID DOES NOT EXIST";
        }
        try {
            ArrayNode userOTCCurrencyPayments = (ArrayNode) mapper.readTree(userOTCCurrencyPaymentsFile);
            Iterator<JsonNode> userOTCCurrencyPaymentsIterator = userOTCCurrencyPayments.elements();
            while (userOTCCurrencyPaymentsIterator.hasNext()) {
                JsonNode userOTCCurrencyPaymentsIt = userOTCCurrencyPaymentsIterator.next();
                String userOTCCurrencyPaymentsItId = userOTCCurrencyPaymentsIt.get("id").textValue();
                if (userOTCCurrencyPaymentsItId.equals(id)) {
                    userOTCCurrencyPaymentsIterator.remove();
                    break;
                }
            }
            FileUtil.editFile(userOTCCurrencyPayments, userOTCCurrencyPaymentsFile);
            return "OK";
        } catch (IOException ex) {
            Logger.getLogger(OTCRemovePayment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "FAIL";
    }
    
    private String method(String userName, String id) {
        File userOTCFolder = UsersFolderLocator.getOTCFolder(userName);
        if (!userOTCFolder.isDirectory()) {
            return "USERNAME DOES NOT EXIST";
        }
        for (File userOTCCurrencyFolder : userOTCFolder.listFiles()) {
            if (!userOTCCurrencyFolder.isDirectory()) {
                continue;
            }
            File userOTCCurrencyPaymentsFile = new File(userOTCCurrencyFolder, "payments.json");
            if (userOTCCurrencyPaymentsFile.isFile()) {
                try {
                    ArrayNode userOTCCurrencyPayments = (ArrayNode) mapper.readTree(userOTCCurrencyPaymentsFile);
                    Iterator<JsonNode> userOTCCurrencyPaymentsIterator = userOTCCurrencyPayments.elements();
                    while (userOTCCurrencyPaymentsIterator.hasNext()) {
                        JsonNode userOTCCurrencyPaymentsIt = userOTCCurrencyPaymentsIterator.next();
                        String userOTCCurrencyPaymentsItId = userOTCCurrencyPaymentsIt.get("id").textValue();
                        if (userOTCCurrencyPaymentsItId.equals(id)) {
                            userOTCCurrencyPaymentsIterator.remove();
                            break;
                        }
                    }
                    FileUtil.editFile(userOTCCurrencyPayments, userOTCCurrencyPaymentsFile);
                    return "OK";
                } catch (IOException ex) {
                    Logger.getLogger(OTCRemovePayment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return "FAIL";
    }
    
}
