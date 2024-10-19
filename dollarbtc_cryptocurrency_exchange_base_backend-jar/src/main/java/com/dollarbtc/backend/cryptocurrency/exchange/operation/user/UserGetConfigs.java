/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserGetConfigsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
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
public class UserGetConfigs extends AbstractOperation<ArrayNode> {

    private final UserGetConfigsRequest userGetConfigsRequest;

    public UserGetConfigs(UserGetConfigsRequest userGetConfigsRequest) {
        super(ArrayNode.class);
        this.userGetConfigsRequest = userGetConfigsRequest;
    }

    @Override
    protected void execute() {
        ArrayNode users = mapper.createArrayNode();
        for (String userName : userGetConfigsRequest.getUserNames()) {
            File userFile = UsersFolderLocator.getConfigFile(userName);
            if(!userFile.isFile()){
                continue;
            }
            try {
                JsonNode user = mapper.readTree(userFile);
                if (user.has("privateKey")) {
                    ((ObjectNode) user).remove("privateKey");
                }
                String[] walletsTags = new String[]{"wallets", "mcWallets", "mcWalletsEthereum"};
                for (String walletsTag : walletsTags) {
                    if (user.has(walletsTag)) {
                        JsonNode wallets = user.get(walletsTag);
                        if (wallets.has("current")) {
                            Iterator<JsonNode> currentIterator = wallets.get("current").iterator();
                            while (currentIterator.hasNext()) {
                                JsonNode currentIt = currentIterator.next();
                                ((ObjectNode) currentIt).remove("privateKey");
                            }
                        }
                        if (wallets.has("old")) {
                            Iterator<JsonNode> currentIterator = wallets.get("old").iterator();
                            while (currentIterator.hasNext()) {
                                JsonNode currentIt = currentIterator.next();
                                ((ObjectNode) currentIt).remove("privateKey");
                            }
                        }
                    }
                }
                users.add(user);
            } catch (IOException ex) {
                Logger.getLogger(UserGetConfigs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = users;
    }

}
