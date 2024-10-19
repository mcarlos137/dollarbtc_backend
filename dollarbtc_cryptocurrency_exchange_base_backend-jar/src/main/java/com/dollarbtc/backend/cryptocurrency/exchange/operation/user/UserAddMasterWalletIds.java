/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddMasterWalletIdsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
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
public class UserAddMasterWalletIds extends AbstractOperation<String> {

    private final UserAddMasterWalletIdsRequest userAddMasterWalletIdsRequest;

    public UserAddMasterWalletIds(UserAddMasterWalletIdsRequest userAddMasterWalletIdsRequest) {
        super(String.class);
        this.userAddMasterWalletIdsRequest = userAddMasterWalletIdsRequest;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userAddMasterWalletIdsRequest.getUserName());
        if (!userFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            JsonNode masterWalletIds = user.get("masterWalletIds");
            userAddMasterWalletIdsRequest.getMasterWalletIds().keySet().stream().forEach((key) -> {
                ((ObjectNode) masterWalletIds).put(key, userAddMasterWalletIdsRequest.getMasterWalletIds().get(key));
            });
            ((ObjectNode) user).set("masterWalletIds", masterWalletIds);
            FileUtil.editFile(user, userFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserAddMasterWalletIds.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
