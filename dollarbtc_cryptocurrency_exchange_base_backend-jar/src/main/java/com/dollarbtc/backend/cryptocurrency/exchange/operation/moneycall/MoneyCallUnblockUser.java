/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallBlockUserRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallUnblockUserRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyCallUnblockUser extends AbstractOperation<String> {

    private final MoneyCallUnblockUserRequest moneyCallUnblockUserRequest;

    public MoneyCallUnblockUser(MoneyCallUnblockUserRequest moneyCallUnblockUserRequest) {
        super(String.class);
        this.moneyCallUnblockUserRequest = moneyCallUnblockUserRequest;
    }

    @Override
    public void execute() {
        File blockedUsersFile = UsersFolderLocator.getBlockedUsersFile(moneyCallUnblockUserRequest.getUserName());
        if (blockedUsersFile.isFile()) {
            try {
                JsonNode blockedUsers = mapper.readTree(blockedUsersFile);
                if (blockedUsers.has("moneyCall")) {
                    Iterator<JsonNode> moneyCallBlockedUsersIterator = ((ArrayNode) blockedUsers.get("moneyCall")).iterator();
                    while (moneyCallBlockedUsersIterator.hasNext()) {
                        JsonNode moneyCallBlockedUsersIt = moneyCallBlockedUsersIterator.next();
                        String moneyCallBlockedUser = moneyCallBlockedUsersIt.textValue();
                        if (moneyCallBlockedUser.equals(moneyCallUnblockUserRequest.getOtherUserName())) {
                            moneyCallBlockedUsersIterator.remove();
                            break;
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MoneyCallUnblockUser.class.getName()).log(Level.SEVERE, null, ex);
                super.response = "FAIL";
                return;
            }
        }
        super.response = "OK";
    }

}
