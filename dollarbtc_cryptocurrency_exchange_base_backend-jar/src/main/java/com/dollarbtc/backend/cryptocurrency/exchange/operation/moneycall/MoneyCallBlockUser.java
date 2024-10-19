/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallBlockUserRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyCallBlockUser extends AbstractOperation<String> {

    private final MoneyCallBlockUserRequest moneyCallBlockUserRequest;

    public MoneyCallBlockUser(MoneyCallBlockUserRequest moneyCallBlockUserRequest) {
        super(String.class);
        this.moneyCallBlockUserRequest = moneyCallBlockUserRequest;
    }

    @Override
    public void execute() {
        File blockedUsersFile = UsersFolderLocator.getBlockedUsersFile(moneyCallBlockUserRequest.getUserName());
        JsonNode blockedUsers = mapper.createObjectNode();
        if (blockedUsersFile.isFile()) {
            try {
                blockedUsers = mapper.readTree(blockedUsersFile);
            } catch (IOException ex) {
                Logger.getLogger(MoneyCallBlockUser.class.getName()).log(Level.SEVERE, null, ex);
                super.response = "FAIL";
                return;
            }
        } 
        if (!blockedUsers.has("moneyCall")) {
            ((ObjectNode) blockedUsers).putArray("moneyCall");
        }
        ((ArrayNode) blockedUsers.get("moneyCall")).add(moneyCallBlockUserRequest.getOtherUserName());
        FileUtil.editFile(blockedUsers, blockedUsersFile);
        super.response = "OK";
    }

}
