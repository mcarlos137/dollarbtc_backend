/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankChangeProcessStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
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
public class TransferToBankChangeProcessStatus extends AbstractOperation<String> {
    
    private final TransferToBankChangeProcessStatusRequest transferToBankChangeProcessStatusRequest;
    
    public TransferToBankChangeProcessStatus(TransferToBankChangeProcessStatusRequest transferToBankChangeProcessStatusRequest) {
        super(String.class);
        this.transferToBankChangeProcessStatusRequest = transferToBankChangeProcessStatusRequest;
    }
    
    @Override
    protected void execute() {
        File transferToBanksProcessFile = new File(TransferToBanksFolderLocator.getFolder(), transferToBankChangeProcessStatusRequest.getId() + ".json");
        if (!transferToBanksProcessFile.isFile()) {
            super.response = "PROCESS DOES NOT EXIST";
            return;
        }
        if (transferToBankChangeProcessStatusRequest.getStatus().equals("OK")
                && (transferToBankChangeProcessStatusRequest.getDollarBTCPaymentId() == null
                || transferToBankChangeProcessStatusRequest.getDollarBTCPaymentId().equals(""))) {
            super.response = "PROCESS DOES NOT HAVE DOLLARBTC PAYMENT";
            return;
        }
        try {
            JsonNode transferToBanksProcess = mapper.readTree(transferToBanksProcessFile);
            ((ObjectNode) transferToBanksProcess).put("changeStatusTimestamp", DateUtil.getCurrentDate());
            ((ObjectNode) transferToBanksProcess).put("changeStatusUser", transferToBankChangeProcessStatusRequest.getUserName());
            ((ObjectNode) transferToBanksProcess).put("transferToBankStatus", transferToBankChangeProcessStatusRequest.getStatus());
            if (transferToBankChangeProcessStatusRequest.getDollarBTCPaymentId() != null && !transferToBankChangeProcessStatusRequest.getDollarBTCPaymentId().equals("")) {
                ((ObjectNode) transferToBanksProcess).put("dollarBTCPaymentId", transferToBankChangeProcessStatusRequest.getDollarBTCPaymentId());
            }
            Iterator<JsonNode> transferToBanksProcessOperationsIterator = transferToBanksProcess.get("operations").iterator();
            while (transferToBanksProcessOperationsIterator.hasNext()) {
                JsonNode transferToBanksProcessOperationsIt = transferToBanksProcessOperationsIterator.next();
                String id = transferToBanksProcessOperationsIt.get("id").textValue();
                switch (transferToBankChangeProcessStatusRequest.getStatus()) {
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
                ((ObjectNode) transferToBanksProcessOperationsIt).put("transferToBankStatus", transferToBankChangeProcessStatusRequest.getStatus());
            }
            FileUtil.editFile(transferToBanksProcess, transferToBanksProcessFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(TransferToBankChangeProcessStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }
    
}
