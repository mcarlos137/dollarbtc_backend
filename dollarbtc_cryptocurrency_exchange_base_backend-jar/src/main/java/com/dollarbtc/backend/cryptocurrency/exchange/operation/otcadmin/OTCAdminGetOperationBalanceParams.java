/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class OTCAdminGetOperationBalanceParams extends AbstractOperation<JsonNode> {

    private final String currency;
    private final boolean moneyclick;

    public OTCAdminGetOperationBalanceParams(String currency, boolean moneyclick) {
        super(JsonNode.class);
        this.currency = currency;
        this.moneyclick = moneyclick;
    }

    @Override
    protected void execute() {
        File operationBalanceFile = new File(OTCFolderLocator.getCurrencyFolder(null, currency), "operationBalance.json");
        if (moneyclick) {
            operationBalanceFile = new File(MoneyclickFolderLocator.getOperationBalanceFolder(), currency + ".json");
        }
        JsonNode operationBalance = mapper.createObjectNode();
        if (!operationBalanceFile.isFile()) {
            ((ObjectNode) operationBalance).put("maxSpreadPercent", 0.0);
            ((ObjectNode) operationBalance).put("changePercent", 0.0);
            FileUtil.createFile(operationBalance, operationBalanceFile);
        }
        try {
            super.response = mapper.readTree(operationBalanceFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminGetOperationBalanceParams.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = operationBalance;
    }

}
