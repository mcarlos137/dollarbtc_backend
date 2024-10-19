/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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
public class OTCGetClientPayment extends AbstractOperation<JsonNode> {

    private final String userName, id;

    public OTCGetClientPayment(String userName, String id) {
        super(JsonNode.class);
        this.userName = userName;
        this.id = id;
    }

    @Override
    public void execute() {
        if (!userName.equals("dollarBTC")) {
            System.out.println(userName);
            File baseFolder = UsersFolderLocator.getOTCFolder(userName);
            if (!baseFolder.isDirectory()) {
                super.response = mapper.createObjectNode();
                return;
            }
            for (File currencyFolder : baseFolder.listFiles()) {
                if (!currencyFolder.isDirectory() || currencyFolder.getName().equals("Operations")) {
                    continue;
                }
                File paymentsFile = new File(currencyFolder, "payments.json");
                if (paymentsFile.isFile()) {
                    try {
                        Iterator<JsonNode> paymentsIterator = mapper.readTree(paymentsFile).iterator();
                        while (paymentsIterator.hasNext()) {
                            JsonNode paymentsIt = paymentsIterator.next();
                            if (paymentsIt.get("id").textValue().equals(id)) {
                                ((ObjectNode) paymentsIt).put("currency", currencyFolder.getName());
                                super.response = paymentsIt;
                                return;
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(OTCGetClientPayment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        super.response = mapper.createObjectNode();
    }

}
