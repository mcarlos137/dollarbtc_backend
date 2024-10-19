/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyCallsFolderLocator;
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
public class MoneyCallChangeStatus extends AbstractOperation<String> {

    private final MoneyCallChangeStatusRequest moneyCallChangeStatusRequest;

    public MoneyCallChangeStatus(MoneyCallChangeStatusRequest moneyCallChangeStatusRequest) {
        super(String.class);
        this.moneyCallChangeStatusRequest = moneyCallChangeStatusRequest;
    }

    @Override
    public void execute() {
        File moneyCallFile = new File(MoneyCallsFolderLocator.getFolder(), moneyCallChangeStatusRequest.getId() + ".json");
        if (!moneyCallFile.isFile()) {
            super.response = "MONEY CALL DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyCall = mapper.readTree(moneyCallFile);
            String id = moneyCall.get("id").textValue();
            String lastStatus = moneyCall.get("status").textValue();
            ((ObjectNode) moneyCall).put("status", moneyCallChangeStatusRequest.getStatus());
            if (moneyCallChangeStatusRequest.getStatus().equals("CANCELED")) {
                ((ObjectNode) moneyCall).put("cancelTimestamp", DateUtil.getCurrentDate());
                ((ObjectNode) moneyCall).put("cancelUserName", moneyCallChangeStatusRequest.getUserName());
                ((ObjectNode) moneyCall).put("cancelMessage", moneyCallChangeStatusRequest.getMessage());
            }
            if (moneyCallChangeStatusRequest.getStatus().equals("ACCEPTED")) {
                ((ObjectNode) moneyCall).put("acceptTimestamp", DateUtil.getCurrentDate());
                ((ObjectNode) moneyCall).put("acceptUserName", moneyCallChangeStatusRequest.getUserName());
                ((ObjectNode) moneyCall).put("acceptMessage", moneyCallChangeStatusRequest.getMessage());
            }
            FileUtil.editFile(moneyCall, moneyCallFile);
            FileUtil.moveFileToFolder(new File(MoneyCallsFolderLocator.getIndexValueFolder("Statuses", lastStatus), id + ".json"), MoneyCallsFolderLocator.getIndexValueFolder("Statuses", moneyCallChangeStatusRequest.getStatus()));
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MoneyCallChangeStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
