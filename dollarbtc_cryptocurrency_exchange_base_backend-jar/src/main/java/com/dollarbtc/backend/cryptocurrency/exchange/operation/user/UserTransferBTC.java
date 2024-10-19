/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserTransferBTCRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class UserTransferBTC extends AbstractOperation<String> {

    private final UserTransferBTCRequest userTransferBTCRequest;

    public UserTransferBTC(UserTransferBTCRequest userTransferBTCRequest) {
        super(String.class);
        this.userTransferBTCRequest = userTransferBTCRequest;
    }

    @Override
    protected void execute() {
        File userBalanceFolder = UsersFolderLocator.getBalanceFolder(userTransferBTCRequest.getUserName());
        if (!userBalanceFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File mcUserBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userTransferBTCRequest.getUserName());
        if (!userBalanceFolder.isDirectory()) {
            super.response = "USER MC BALANCE DOES NOT EXIST";
            return;
        }
        File baseBalanceFolder = null;
        File targetBalanceFolder = null;
        switch (userTransferBTCRequest.getBalanceOperationType()) {
            case TRANSFER_FROM_BALANCE_TO_MCBALANCE:
                baseBalanceFolder = userBalanceFolder;
                targetBalanceFolder = mcUserBalanceFolder;
                break;
            case TRANSFER_FROM_MCBALANCE_TO_BALANCE:
                baseBalanceFolder = mcUserBalanceFolder;
                targetBalanceFolder = userBalanceFolder;
                break;
        }
        if (baseBalanceFolder == null || targetBalanceFolder == null) {
            super.response = "FAIL";
            return;
        }
        String result = BaseOperation.substractToBalance(
                baseBalanceFolder,
                "BTC",
                userTransferBTCRequest.getAmount(),
                userTransferBTCRequest.getBalanceOperationType(),
                BalanceOperationStatus.OK,
                "",
                null,
                false,
                null,
                false,
                null
        );
        if (!result.equals("OK")) {
            super.response = result;
            return;
        }
        BaseOperation.addToBalance(
                targetBalanceFolder,
                "BTC",
                userTransferBTCRequest.getAmount(),
                userTransferBTCRequest.getBalanceOperationType(),
                BalanceOperationStatus.OK,
                "",
                null,
                null,
                false,
                null
        );
        super.response = "OK";
    }

}
