/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardAddNumberRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DebitCardsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class DebitCardAddNumber extends AbstractOperation<String> {

    private final DebitCardAddNumberRequest debitCardAddNumberRequest;

    public DebitCardAddNumber(DebitCardAddNumberRequest debitCardAddNumberRequest) {
        super(String.class);
        this.debitCardAddNumberRequest = debitCardAddNumberRequest;
    }

    @Override
    public void execute() {
        File debitCardConfigFile = DebitCardsFolderLocator.getConfigFile(debitCardAddNumberRequest.getId());
        if (!debitCardConfigFile.isFile()) {
            super.response = "DEBIT CARD ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode debitCardConfig = mapper.readTree(debitCardConfigFile);
            if (debitCardConfig.has("number") && !debitCardConfig.get("number").textValue().equals("XXXXXXXXXXXXXXXX")) {
                super.response = "DEBIT CARD ALREADY HAS A NUMBER";
                return;
            }
            ((ObjectNode) debitCardConfig).put("number", debitCardAddNumberRequest.getNumber());
            FileUtil.editFile(debitCardConfig, debitCardConfigFile);
            File debitCardIndexNumberFile = new File(DebitCardsFolderLocator.getIndexesSpecificValueFolder("Numbers", "XXXXXXXXXXXXXXXX"), debitCardAddNumberRequest.getId() + ".json");
            File debitCardIndexNumberNewFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Numbers", debitCardAddNumberRequest.getNumber());
            FileUtil.moveFileToFolder(debitCardIndexNumberFile, debitCardIndexNumberNewFolder);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(DebitCardAddNumber.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
