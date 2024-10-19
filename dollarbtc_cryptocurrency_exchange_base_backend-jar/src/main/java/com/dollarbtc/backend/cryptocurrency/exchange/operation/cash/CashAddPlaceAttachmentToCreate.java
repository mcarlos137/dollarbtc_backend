/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashAddPlaceAttachmentToCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.CashCreatePlaceStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashAddPlaceAttachmentToCreate extends AbstractOperation<String> {

    private final CashAddPlaceAttachmentToCreateRequest cashAddPlaceAttachmentToCreateRequest;

    public CashAddPlaceAttachmentToCreate(CashAddPlaceAttachmentToCreateRequest cashAddPlaceAttachmentToCreateRequest) {
        super(String.class);
        this.cashAddPlaceAttachmentToCreateRequest = cashAddPlaceAttachmentToCreateRequest;
    }

    @Override
    public void execute() {
        File cashPlaceConfigFile = CashFolderLocator.getPlaceConfigFile(cashAddPlaceAttachmentToCreateRequest.getPlaceId());
        if (!cashPlaceConfigFile.isFile()) {
            this.response = "CASH PLACE ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
            CashCreatePlaceStatus baseCashCreatePlaceStatus = CashCreatePlaceStatus.valueOf(cashPlaceConfig.get("status").textValue());
            if (!baseCashCreatePlaceStatus.equals(CashCreatePlaceStatus.ANALYSING)) {
                this.response = "ATTACHMENT CAN BE ADDED AT ANALYSING";
                return;
            }
            ArrayNode cashPlaceConfigAttachments = (ArrayNode) cashPlaceConfig.get("attachments");
            cashPlaceConfigAttachments.add(cashAddPlaceAttachmentToCreateRequest.getAttachmentUrl());
            FileUtil.editFile(cashPlaceConfig, cashPlaceConfigFile);
            this.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(CashAddPlaceAttachmentToCreate.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.response = "FAIL";
    }

}
