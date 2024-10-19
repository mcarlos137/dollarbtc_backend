/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.DebitCardStatus;
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
public class DebitCardChangeStatus extends AbstractOperation<String> {

    private final DebitCardChangeStatusRequest debitCardChangeStatusRequest;

    public DebitCardChangeStatus(DebitCardChangeStatusRequest debitCardChangeStatusRequest) {
        super(String.class);
        this.debitCardChangeStatusRequest = debitCardChangeStatusRequest;
    }

    @Override
    protected void execute() {
        File debitCardConfigFile = DebitCardsFolderLocator.getConfigFile(debitCardChangeStatusRequest.getId());
        if (!debitCardConfigFile.isFile()) {
            super.response = "DEBIT CARD ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode debitCardConfig = mapper.readTree(debitCardConfigFile);
            DebitCardStatus baseDebitCardStatus = DebitCardStatus.valueOf(debitCardConfig.get("debitCardStatus").textValue());
            File debitCardIndexStatusFile = new File(DebitCardsFolderLocator.getIndexesSpecificValueFolder("Statuses", baseDebitCardStatus.name()), debitCardChangeStatusRequest.getId() + ".json");
            if (!debitCardIndexStatusFile.isFile()) {
                super.response = "DEBIT CARD ID IS IN INVALID STATUS";
                return;
            }
            ((ObjectNode) debitCardConfig).put("debitCardStatus", debitCardChangeStatusRequest.getDebitCardStatus().name());
            File debitCardIndexStatusNewFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Statuses", debitCardChangeStatusRequest.getDebitCardStatus().name());
            FileUtil.editFile(debitCardConfig, debitCardConfigFile);
            FileUtil.moveFileToFolder(debitCardIndexStatusFile, debitCardIndexStatusNewFolder);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(DebitCardChangeStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
