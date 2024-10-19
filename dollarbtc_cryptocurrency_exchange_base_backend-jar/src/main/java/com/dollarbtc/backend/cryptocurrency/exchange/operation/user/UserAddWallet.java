/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersAddressesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
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
public class UserAddWallet extends AbstractOperation<String> {

    private final String userName;
    private final boolean newWallet, moneyclick;

    public UserAddWallet(String userName, boolean newWallet, boolean moneyclick) {
        super(String.class);
        this.userName = userName;
        this.newWallet = newWallet;
        this.moneyclick = moneyclick;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userName);
        if (!userFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        String userName25 = userName;
        if (userName25.length() > 25) {
            userName25 = userName25.substring(0, 25);
        }
        if (userName25.startsWith("1") || userName25.startsWith("3")) {
            userName25 = "A" + userName25.substring(1);
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            String walletsTag = "wallets";
            if (moneyclick) {
                walletsTag = "mcWallets";
            }
            if (!newWallet && user.has(walletsTag) && user.get(walletsTag).has("current")) {
                super.response = "OK";
                return;
            }
            JsonNode userAddress = new UserGetNewAddress(userName25).getResponse();
            if (userAddress.has("error")) {
                Logger.getLogger(UserAddWallet.class.getName()).log(Level.INFO, "{0}", userAddress.get("error").textValue());
                new UserCreateNewWallet(userName25).getResponse();
                userAddress = new UserGetNewAddress(userName25).getResponse();
            }
            if (!userAddress.has("address")) {
                super.response = "OK";
                return;
            }
            File userAddressFile = UsersAddressesFolderLocator.getAddressFile(userAddress.get("address").textValue());
            ((ObjectNode) userAddress).put("userName", userName);
            String type = "DOLLARBTC";
            if (moneyclick) {
                type = "MONEYCLICK";
            }
            ((ObjectNode) userAddress).put("type", type);
            if (!FileUtil.createFile(userAddress, userAddressFile)) {
                super.response = "OK";
                return;
            }
            String timestamp = DateUtil.getCurrentDate();
            JsonNode wallet = mapper.createObjectNode();
            ((ObjectNode) wallet).put("address", userAddress.get("address").textValue());
            ((ObjectNode) wallet).put("privateKey", userAddress.get("private").textValue());
            if (!user.has(walletsTag)) {
                ((ObjectNode) user).set(walletsTag, mapper.createObjectNode());
            }
            JsonNode wallets = user.get(walletsTag);
            if (!wallets.has("old")) {
                ((ObjectNode) wallets).set("old", mapper.createObjectNode());
            }
            JsonNode old = wallets.get("old");
            if (wallets.has("current")) {
                JsonNode current = wallets.get("current");
                Iterator<String> currentFielNamesIterator = current.fieldNames();
                while (currentFielNamesIterator.hasNext()) {
                    String currentFielNamesIt = currentFielNamesIterator.next();
                    ((ObjectNode) old).set(currentFielNamesIt, current.get(currentFielNamesIt));
                    ((ObjectNode) current).remove(currentFielNamesIt);
                    break;
                }
                ((ObjectNode) current).set(timestamp, wallet);
            } else {
                JsonNode current = mapper.createObjectNode();
                ((ObjectNode) current).set(timestamp, wallet);
                ((ObjectNode) wallets).set("current", current);
            }
            FileUtil.editFile(user, userFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserAddWallet.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
