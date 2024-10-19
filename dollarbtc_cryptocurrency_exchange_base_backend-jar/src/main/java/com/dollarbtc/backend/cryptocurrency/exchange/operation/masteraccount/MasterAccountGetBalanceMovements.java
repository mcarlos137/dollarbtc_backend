/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount.MasterAccountGetBalanceMovementsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountGetBalanceMovements extends AbstractOperation<JsonNode> {

    private final MasterAccountGetBalanceMovementsRequest masterAccountBalanceMovementsRequest;

    public MasterAccountGetBalanceMovements(MasterAccountGetBalanceMovementsRequest masterAccountBalanceMovementsRequest) {
        super(JsonNode.class);
        this.masterAccountBalanceMovementsRequest = masterAccountBalanceMovementsRequest;
    }

    @Override
    public void execute() {
        File masterAccountBalanceFolder = MasterAccountFolderLocator.getBalanceFolder(masterAccountBalanceMovementsRequest.getMasterAccountName());
        super.response = BaseOperation.getBalanceMovements(masterAccountBalanceFolder,
                masterAccountBalanceMovementsRequest.getInitTimestamp(),
                masterAccountBalanceMovementsRequest.getEndTimestamp(),
                masterAccountBalanceMovementsRequest.getBalanceOperationType(),
                null,
                null);
    }

}
