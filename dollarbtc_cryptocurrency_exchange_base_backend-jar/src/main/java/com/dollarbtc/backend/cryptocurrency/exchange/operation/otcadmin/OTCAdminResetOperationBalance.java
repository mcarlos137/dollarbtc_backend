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
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminResetOperationBalance extends AbstractOperation<String> {

    private final String currency;
    private final boolean moneyclick;

    public OTCAdminResetOperationBalance(String currency, boolean moneyclick) {
        super(String.class);
        this.currency = currency;
        this.moneyclick = moneyclick;
    }

    @Override
    protected void execute() {
        File operationBalanceFolder = FileUtil.createFolderIfNoExist(new File(OTCFolderLocator.getCurrencyFolder(null, currency), "OperationBalance"));
        File operationBalanceOldFolder = FileUtil.createFolderIfNoExist(new File(operationBalanceFolder, "Old"));
        if (moneyclick) {
            operationBalanceFolder = MoneyclickFolderLocator.getOperationBalanceFolder(currency);
            operationBalanceOldFolder = FileUtil.createFolderIfNoExist(new File(operationBalanceFolder, "Old"));
        }
        for (File otcCurrencyOperationBalanceFile : operationBalanceFolder.listFiles()) {
            if (!otcCurrencyOperationBalanceFile.isFile()) {
                continue;
            }
            FileUtil.moveFileToFolder(otcCurrencyOperationBalanceFile, operationBalanceOldFolder);
        }
        super.response = "OK";
    }

}
