/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewAddAttachmentToCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailCreateStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailNewAddAttachmentToCreate extends AbstractOperation<String> {

    private final MCRetailNewAddAttachmentToCreateRequest mcRetailNewAddAttachmentToCreateRequest;

    public MCRetailNewAddAttachmentToCreate(MCRetailNewAddAttachmentToCreateRequest mcRetailNewAddAttachmentToCreateRequest) {
        super(String.class);
        this.mcRetailNewAddAttachmentToCreateRequest = mcRetailNewAddAttachmentToCreateRequest;
    }

    @Override
    public void execute() {
        File moneyclickRetailConfigFile = MoneyclickFolderLocator.getRetailConfigFile(mcRetailNewAddAttachmentToCreateRequest.getId());
        if (!moneyclickRetailConfigFile.isFile()) {
            this.response = "RETAIL ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
            MCRetailCreateStatus baseMCRetailCreateStatus = MCRetailCreateStatus.valueOf(moneyclickRetailConfig.get("mcRetailCreateStatus").textValue());
            if (!baseMCRetailCreateStatus.equals(MCRetailCreateStatus.ANALYSING)) {
                this.response = "ATTACHMENT CAN BE ADDED AT ANALYSING";
                return;
            }
            ArrayNode moneyclickRetailConfigAttachments = (ArrayNode) moneyclickRetailConfig.get("attachments");
            moneyclickRetailConfigAttachments.add(mcRetailNewAddAttachmentToCreateRequest.getAttachmentUrl());
            FileUtil.editFile(moneyclickRetailConfig, moneyclickRetailConfigFile);
            this.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewAddAttachmentToCreate.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.response = "FAIL";
    }

}
