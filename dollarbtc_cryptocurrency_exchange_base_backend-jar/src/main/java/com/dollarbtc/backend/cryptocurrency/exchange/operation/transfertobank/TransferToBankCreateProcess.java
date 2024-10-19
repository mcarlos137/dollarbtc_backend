/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCChangeOperationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.transfertobank.TransferToBankCreateProcessRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.TransferToBanksFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class TransferToBankCreateProcess extends AbstractOperation<String> {

    private final TransferToBankCreateProcessRequest transferToBankCreateProcessRequest;

    public TransferToBankCreateProcess(TransferToBankCreateProcessRequest transferToBankCreateProcessRequest) {
        super(String.class);
        this.transferToBankCreateProcessRequest = transferToBankCreateProcessRequest;
    }

    @Override
    protected void execute() {
        ArrayNode operations = mapper.createArrayNode();
        for (String id : transferToBankCreateProcessRequest.getIds()) {
            JsonNode operation = new OTCGetOperation(id).getResponse();
            if (operation.has("id")) {
                ((ObjectNode) operation).put("transferToBankStatus", "PENDING");
                operations.add(operation);
                new OTCChangeOperationStatus(new OTCChangeOperationStatusRequest(id, OTCOperationStatus.IN_BATCH_PROCESS, false)).getResponse();
            }
        }
        JsonNode process = mapper.createObjectNode();
        String creationTimestamp = DateUtil.getCurrentDate();
        String id = Long.toString(DateUtil.parseDate(creationTimestamp).getTime());
        ((ObjectNode) process).put("id", id);
        ((ObjectNode) process).put("currency", transferToBankCreateProcessRequest.getCurrency());
        ((ObjectNode) process).put("creationTimestamp", creationTimestamp);
        ((ObjectNode) process).put("creationUser", transferToBankCreateProcessRequest.getUserName());
        ((ObjectNode) process).put("transferToBankStatus", "PENDING");
        ((ObjectNode) process).putArray("operations").addAll(operations);
        FileUtil.createFile(process, new File(TransferToBanksFolderLocator.getFolder(), id + ".json"));
        super.response = "OK";
    }

}
