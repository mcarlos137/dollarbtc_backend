/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetProcessingBalanceMovements extends AbstractOperation<JsonNode> {

    public UserGetProcessingBalanceMovements() {
        super(JsonNode.class);
    }

    @Override
    protected void execute() {
        if (!OPERATOR_NAME.equals("MAIN")) {
            super.response = mapper.createObjectNode();
            return;
        }
        File processingBalanceFolder = BaseFilesLocator.getProcessingBalanceFolder();
        Map<String, JsonNode> unconfirmedBalanceMovements = new TreeMap<>();
        for (File processingBalanceFile : processingBalanceFolder.listFiles()) {
            if (!processingBalanceFile.isFile()) {
                continue;
            }
            try {
                JsonNode processingBalance = mapper.readTree(processingBalanceFile);
                String timestamp = processingBalance.get("timestamp").textValue();
                if (unconfirmedBalanceMovements.containsKey(timestamp)) {
                    if (!timestamp.contains("__")) {
                        timestamp = timestamp + "__1";
                    } else {
                        int position = Integer.parseInt(timestamp.split("__")[1]) + 1;
                        timestamp = timestamp.split("__")[0] + "__" + position;
                    }
                }
                unconfirmedBalanceMovements.put(timestamp, processingBalance);
            } catch (IOException ex) {
                Logger.getLogger(UserGetProcessingBalanceMovements.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        super.response = mapper.valueToTree(unconfirmedBalanceMovements);
    }

}
