/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneyorder;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneyorder.MoneyOrderProcessRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyOrdersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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
public class MoneyOrderProcess extends AbstractOperation<String> {

    private final MoneyOrderProcessRequest moneyOrderProcessRequest;

    public MoneyOrderProcess(MoneyOrderProcessRequest moneyOrderProcessRequest) {
        super(String.class);
        this.moneyOrderProcessRequest = moneyOrderProcessRequest;
    }

    @Override
    public void execute() {
        File moneyOrderOperationFile = new File(new File(MoneyOrdersFolderLocator.getOperationsFolder(), "PROCESSING"), moneyOrderProcessRequest.getId() + ".json");
        if (!moneyOrderOperationFile.isFile()) {
            super.response = "MONEY ORDER DOES NOT EXIST OR IT IS NOT IN PROCESSING STATUS";
            return;
        }
        try {
            JsonNode moneyOrderOperation = mapper.readTree(moneyOrderOperationFile);
            ((ObjectNode) moneyOrderOperation).put("processUserName", moneyOrderProcessRequest.getUserName());
            ((ObjectNode) moneyOrderOperation).put("processTimestamp", DateUtil.getCurrentDate());
            if (moneyOrderProcessRequest.getAdditionalInfo() != null && !moneyOrderProcessRequest.getAdditionalInfo().equals("")) {
                ((ObjectNode) moneyOrderOperation).put("additionalInfo", moneyOrderProcessRequest.getAdditionalInfo());
            }
            FileUtil.editFile(moneyOrderOperation, moneyOrderOperationFile);
            FileUtil.moveFileToFolder(moneyOrderOperationFile, new File(MoneyOrdersFolderLocator.getOperationsFolder(), moneyOrderProcessRequest.getStatus()));
            String id = moneyOrderOperation.get("id").textValue();
            String userName = moneyOrderOperation.get("userName").textValue();
            BalanceOperationStatus balanceOperationStatus = BalanceOperationStatus.PROCESSING;
            if (moneyOrderProcessRequest.getStatus().equals("OK")) {
                balanceOperationStatus = BalanceOperationStatus.OK;
            } else if (moneyOrderProcessRequest.getStatus().equals("FAILED")) {
                balanceOperationStatus = BalanceOperationStatus.FAIL;
            }
            BaseOperation.changeBalanceOperationStatus(
                    UsersFolderLocator.getMCBalanceFolder(userName),
                    balanceOperationStatus,
                    id,
                    "id",
                    moneyOrderProcessRequest.getAdditionalInfo()
            );
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MoneyOrderProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
