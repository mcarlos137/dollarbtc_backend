/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankChangeOperationsOfProcessStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.TransferToBanksFolderLocator;
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
public class TransferToBankChangeOperationsOfProcessStatus extends AbstractOperation<String> {

    private final TransferToBankChangeOperationsOfProcessStatusRequest transferToBankChangeOperationsOfProcessStatusRequest;

    public TransferToBankChangeOperationsOfProcessStatus(TransferToBankChangeOperationsOfProcessStatusRequest transferToBankChangeOperationsOfProcessStatusRequest) {
        super(String.class);
        this.transferToBankChangeOperationsOfProcessStatusRequest = transferToBankChangeOperationsOfProcessStatusRequest;
    }

    @Override
    protected void execute() {
        File transferToBanksProcessFile = new File(TransferToBanksFolderLocator.getFolder(), transferToBankChangeOperationsOfProcessStatusRequest.getProcessId() + ".json");
        if (!transferToBanksProcessFile.isFile()) {
            super.response = "PROCESS DOES NOT EXIST";
            return;
        }
        try {
            JsonNode transferToBanksProcess = mapper.readTree(transferToBanksProcessFile);
            Iterator<JsonNode> transferToBanksProcessOperationsIterator = transferToBanksProcess.get("operations").iterator();
            while (transferToBanksProcessOperationsIterator.hasNext()) {
                JsonNode transferToBanksProcessOperationsIt = transferToBanksProcessOperationsIterator.next();
                String id = transferToBanksProcessOperationsIt.get("id").textValue();
                if(!transferToBankChangeOperationsOfProcessStatusRequest.getId().equals(id)){
                    continue;
                }
                switch(transferToBankChangeOperationsOfProcessStatusRequest.getStatus()){
                    case "PENDING":
                        new OTCChangeOperationStatus(new OTCChangeOperationStatusRequest(id, OTCOperationStatus.IN_BATCH_PROCESS, false)).getResponse();
                        break;
                    case "FAILED":
                        new OTCChangeOperationStatus(new OTCChangeOperationStatusRequest(id, OTCOperationStatus.WAITING_FOR_PAYMENT, false)).getResponse();
                        break;
                    case "OK":
                        new OTCChangeOperationStatus(new OTCChangeOperationStatusRequest(id, OTCOperationStatus.SUCCESS, false)).getResponse();
                        break;
                }
                ((ObjectNode) transferToBanksProcessOperationsIt).put("transferToBankStatus", transferToBankChangeOperationsOfProcessStatusRequest.getStatus());
            }
            FileUtil.editFile(transferToBanksProcess, transferToBanksProcessFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(TransferToBankChangeOperationsOfProcessStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
