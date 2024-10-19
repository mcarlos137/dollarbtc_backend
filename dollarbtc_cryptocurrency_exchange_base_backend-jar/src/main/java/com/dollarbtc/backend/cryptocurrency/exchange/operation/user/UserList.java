/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersOperatorFolderLocator;
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
public class UserList extends AbstractOperation<JsonNode> {

    public UserList() {
        super(JsonNode.class);
    }

    @Override
    protected void execute() {
        File usersFolder = UsersFolderLocator.getFolder();
        JsonNode users = mapper.createObjectNode();
        ArrayNode usersArrayNode = mapper.createArrayNode();
        if (OPERATOR_NAME.equals("MAIN")) {
            for (File userFolder : usersFolder.listFiles()) {
                if (!userFolder.isDirectory()) {
                    continue;
                }
                File userFile = new File(userFolder, "config.json");
                JsonNode user = null;
                try {
                    user = mapper.readTree(userFile);
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
                } catch (IOException ex) {
                    Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
                }
                usersArrayNode.add(user);
            }
        } else {
            for (File userOperatorFile : UsersOperatorFolderLocator.getFolder().listFiles()) {
                if (!userOperatorFile.isFile()) {
                    continue;
                }
                File userFile = UsersFolderLocator.getConfigFile(userOperatorFile.getName().replace(".json", ""));
                if (!userFile.isFile()) {
                    continue;
                }
                JsonNode user = null;
                try {
                    user = mapper.readTree(userFile);
                    if (user.has("privateKey")) {
                        ((ObjectNode) user).remove("privateKey");
                    }
                    String[] walletsTags = new String[]{"wallets", "mcWallets"};
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
                } catch (IOException ex) {
                    Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
                }
                usersArrayNode.add(user);
            }
        }
        ((ObjectNode) users).putArray("users").addAll(usersArrayNode);
        super.response = users;
    }

}
