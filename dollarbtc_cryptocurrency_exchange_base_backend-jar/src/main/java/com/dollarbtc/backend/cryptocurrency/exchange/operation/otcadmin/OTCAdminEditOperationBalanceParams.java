/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditOperationBalanceParamsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminEditOperationBalanceParams extends AbstractOperation<String> {

    private final OTCAdminEditOperationBalanceParamsRequest otcAdminEditOperationBalanceParamsRequest;
    private final boolean moneyclick;

    public OTCAdminEditOperationBalanceParams(OTCAdminEditOperationBalanceParamsRequest otcAdminEditOperationBalanceParamsRequest, boolean moneyclick) {
        super(String.class);
        this.otcAdminEditOperationBalanceParamsRequest = otcAdminEditOperationBalanceParamsRequest;
        this.moneyclick = moneyclick;
    }

    @Override
    protected void execute() {
        File operationBalanceFile = new File(OTCFolderLocator.getCurrencyFolder(null, otcAdminEditOperationBalanceParamsRequest.getCurrency()), "operationBalance.json");
        if (moneyclick) {
            operationBalanceFile = new File(MoneyclickFolderLocator.getOperationBalanceFolder(), otcAdminEditOperationBalanceParamsRequest.getCurrency() + ".json");
        }
        JsonNode operationBalance = mapper.createObjectNode();
        ((ObjectNode) operationBalance).put("maxSpreadPercent", otcAdminEditOperationBalanceParamsRequest.getMaxSpreadPercent());
        ((ObjectNode) operationBalance).put("changePercent", otcAdminEditOperationBalanceParamsRequest.getChangePercent());
        FileUtil.editFile(operationBalance, operationBalanceFile);
        super.response = "OK";
    }

}
