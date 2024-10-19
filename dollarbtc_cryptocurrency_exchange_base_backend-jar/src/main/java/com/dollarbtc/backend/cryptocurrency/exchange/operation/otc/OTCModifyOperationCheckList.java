/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCModifyOperationCheckListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
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
public class OTCModifyOperationCheckList extends AbstractOperation<String> {

    private final OTCModifyOperationCheckListRequest otcModifyOperationCheckListRequest;

    public OTCModifyOperationCheckList(OTCModifyOperationCheckListRequest otcModifyOperationCheckListRequest) {
        super(String.class);
        this.otcModifyOperationCheckListRequest = otcModifyOperationCheckListRequest;
    }

    @Override
    public void execute() {
        File otcOperationIdFile = OTCFolderLocator.getOperationIdFile(null, otcModifyOperationCheckListRequest.getId());
        if (!otcOperationIdFile.isFile()) {
            super.response = "OPERATION ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode otcOperationId = mapper.readTree(otcOperationIdFile);
            ((ObjectNode) otcOperationId).set("checkList", otcModifyOperationCheckListRequest.getCheckList());
            FileUtil.editFile(otcOperationId, otcOperationIdFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCModifyOperationCheckList.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
