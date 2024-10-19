/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardCreateNewPinRequest;
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
public class DebitCardCreateNewPin extends AbstractOperation<String> {

    private final DebitCardCreateNewPinRequest debitCardCreateNewPinRequest;

    public DebitCardCreateNewPin(DebitCardCreateNewPinRequest debitCardCreateNewPinRequest) {
        super(String.class);
        this.debitCardCreateNewPinRequest = debitCardCreateNewPinRequest;
    }

    @Override
    protected void execute() {
        try {
            File debitCardConfigFile = DebitCardsFolderLocator.getConfigFile(this.debitCardCreateNewPinRequest.getId());
            JsonNode debitCardConfig = mapper.readTree(debitCardConfigFile);
            if (debitCardConfig.get("id").textValue().equals(this.debitCardCreateNewPinRequest.getId())
                    && debitCardConfig.get("secretKey").textValue().equals(this.debitCardCreateNewPinRequest.getSecretKey())) {
                ((ObjectNode) debitCardConfig).put("pin", this.debitCardCreateNewPinRequest.getPin());
                FileUtil.editFile(debitCardConfig, debitCardConfigFile);
                super.response = "OK";
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(DebitCardCreateNewPin.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
