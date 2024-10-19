/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankApplyProcessRequest;
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
public class TransferToBankApplyProcess extends AbstractOperation<String> {

    private final TransferToBankApplyProcessRequest transferToBankApplyProcessRequest;

    public TransferToBankApplyProcess(TransferToBankApplyProcessRequest transferToBankApplyProcessRequest) {
        super(String.class);
        this.transferToBankApplyProcessRequest = transferToBankApplyProcessRequest;
    }

    @Override
    protected void execute() {
        File transferToBanksProcessFile = new File(TransferToBanksFolderLocator.getFolder(), transferToBankApplyProcessRequest.getId() + ".json");
        if (!transferToBanksProcessFile.isFile()) {
            super.response = "PROCESS DOES NOT EXIST";
            return;
        }
        try {
            JsonNode transferToBanksProcess = mapper.readTree(transferToBanksProcessFile);
            if (!transferToBanksProcess.has("operations")) {
                ((ObjectNode) transferToBanksProcess).put("applyed", true);
                FileUtil.editFile(transferToBanksProcess, transferToBanksProcessFile);
                super.response = "OK";
                return;
            }

            if (transferToBanksProcess.get("transferToBankStatus").textValue().equals("OK")
                    && !transferToBanksProcess.has("dollarBTCPaymentId")) {
                super.response = "PROCESS DOES NOT HAVE DOLLARBTC PAYMENT";
                return;
            }
            String paymentId = transferToBanksProcess.get("dollarBTCPaymentId").textValue();
            Iterator<JsonNode> transferToBanksProcessOperationsIterator = transferToBanksProcess.get("operations").iterator();
            while (transferToBanksProcessOperationsIterator.hasNext()) {
                JsonNode transferToBanksProcessOperationsIt = transferToBanksProcessOperationsIterator.next();
                String id = transferToBanksProcessOperationsIt.get("id").textValue();
                OTCOperationStatus otcOperationStatus = OTCOperationStatus.valueOf(transferToBanksProcessOperationsIt.get("otcOperationStatus").textValue());
                new OTCChangeOperationStatus(new OTCChangeOperationStatusRequest(id, otcOperationStatus, paymentId)).getResponse();
            }
            ((ObjectNode) transferToBanksProcess).put("applyed", true);
            FileUtil.editFile(transferToBanksProcess, transferToBanksProcessFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(TransferToBankApplyProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
