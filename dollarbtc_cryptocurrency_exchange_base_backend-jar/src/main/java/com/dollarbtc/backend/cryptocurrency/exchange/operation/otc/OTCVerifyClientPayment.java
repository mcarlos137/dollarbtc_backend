/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class OTCVerifyClientPayment extends AbstractOperation<String> {

    private final String userName, id;

    public OTCVerifyClientPayment(String userName, String id) {
        super(String.class);
        this.userName = userName;
        this.id = id;
    }

    @Override
    public void execute() {
        if (!userName.equals("dollarBTC")) {
            File baseFolder = UsersFolderLocator.getOTCFolder(userName);
            for (File currencyFolder : baseFolder.listFiles()) {
                if (!currencyFolder.isDirectory() || currencyFolder.getName().equals("Operations")) {
                    continue;
                }
                File paymentsFile = new File(currencyFolder, "payments.json");
                if (paymentsFile.isFile()) {
                    try {
                        JsonNode payments = mapper.readTree(paymentsFile);
                        Iterator<JsonNode> paymentsIterator = payments.iterator();
                        while (paymentsIterator.hasNext()) {
                            JsonNode paymentsIt = paymentsIterator.next();
                            if (paymentsIt.get("id").textValue().equals(id)) {
                                ((ObjectNode) paymentsIt).put("verified", true);
                                FileUtil.editFile(payments, paymentsFile);
                                String message = "Su medio de pago " + id.substring(id.length() - 4) + " ha sido VERIFICADO";
                                new UserPostMessage(userName, message, null).getResponse();
                                super.response = "OK";
                                return;
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(OTCVerifyClientPayment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        super.response = "FAIL";
    }

}
