/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyCallGetBlockedUsers extends AbstractOperation<ArrayNode> {

    private final String userName;

    public MoneyCallGetBlockedUsers(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    public void execute() {
        File blockedUsersFile = UsersFolderLocator.getBlockedUsersFile(userName);
        if (blockedUsersFile.isFile()) {
            try {
                super.response = (ArrayNode) mapper.readTree(blockedUsersFile).get("moneyCall");
                return;
            } catch (IOException ex) {
                Logger.getLogger(MoneyCallGetBlockedUsers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = mapper.createArrayNode();
    }

}
