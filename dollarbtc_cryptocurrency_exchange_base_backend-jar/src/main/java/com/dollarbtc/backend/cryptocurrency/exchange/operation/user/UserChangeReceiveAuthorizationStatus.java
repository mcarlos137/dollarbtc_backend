/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserReceiveAuthorizationStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.ReceiveAuthorizationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersReceiveAuthorizationsFolderLocator;
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
public class UserChangeReceiveAuthorizationStatus extends AbstractOperation<String> {

    private final UserReceiveAuthorizationStatusRequest userReceiveAuthorizationStatusRequest;

    public UserChangeReceiveAuthorizationStatus(UserReceiveAuthorizationStatusRequest userReceiveAuthorizationStatusRequest) {
        super(String.class);
        this.userReceiveAuthorizationStatusRequest = userReceiveAuthorizationStatusRequest;
    }

    @Override
    protected void execute() {
        File usersReceiveAuthorizationsUserNameFile = UsersReceiveAuthorizationsFolderLocator.getUserNameFile(userReceiveAuthorizationStatusRequest.getUserName());
        if (!usersReceiveAuthorizationsUserNameFile.isFile()) {
            super.response = "RECEIVE AUTHORIZATION DOES NOT EXIST";
            return;
        }
        try {
            JsonNode usersReceiveAuthorizationsUserName = mapper.readTree(usersReceiveAuthorizationsUserNameFile);
            if (!usersReceiveAuthorizationsUserName.has(userReceiveAuthorizationStatusRequest.getReceiveAuthorizationId())) {
                super.response = "RECEIVE AUTHORIZATION DOES NOT EXIST";
                return;
            }
            if (userReceiveAuthorizationStatusRequest.getReceiveAuthorizationStatus().equals(ReceiveAuthorizationStatus.REJECTED)) {
                for (File userBalanceFile : UsersFolderLocator.getMCBalanceFolder(userReceiveAuthorizationStatusRequest.getUserName()).listFiles()) {
                    if (!userBalanceFile.isFile() || !userBalanceFile.getName().contains("add")) {
                        continue;
                    }
                    JsonNode userBalance = mapper.readTree(userBalanceFile);
                    if (userBalance.has("receiveAuthorizationId") && userBalance.get("receiveAuthorizationId").textValue().equals(userReceiveAuthorizationStatusRequest.getReceiveAuthorizationId())) {
                        String senderUserName = userBalance.get("senderUserName").textValue();
                        Double addedAmountAmount = userBalance.get("addedAmount").get("amount").doubleValue();
                        String addedAmountCurrency = userBalance.get("addedAmount").get("currency").textValue();
                        ((ObjectNode) userBalance).put("balanceOperationStatus", BalanceOperationStatus.FAIL.toString());
                        ((ObjectNode) userBalance).put("canceledReason", "REJECTED BY USER");
                        FileUtil.editFile(userBalance, userBalanceFile);
                        BaseOperation.addToBalance(
                                UsersFolderLocator.getMCBalanceFolder(senderUserName),
                                addedAmountCurrency,
                                addedAmountAmount,
                                BalanceOperationType.CREDIT,
                                BalanceOperationStatus.OK,
                                "REJECTED FROM " + userReceiveAuthorizationStatusRequest.getUserName() + " - " + userReceiveAuthorizationStatusRequest.getReceiveAuthorizationId(),
                                null,
                                null,
                                false,
                                null
                        );
                    }
                }
            }
            ((ObjectNode) usersReceiveAuthorizationsUserName).put(userReceiveAuthorizationStatusRequest.getReceiveAuthorizationId(), userReceiveAuthorizationStatusRequest.getReceiveAuthorizationStatus().toString());
            FileUtil.editFile(usersReceiveAuthorizationsUserName, usersReceiveAuthorizationsUserNameFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserChangeReceiveAuthorizationStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
