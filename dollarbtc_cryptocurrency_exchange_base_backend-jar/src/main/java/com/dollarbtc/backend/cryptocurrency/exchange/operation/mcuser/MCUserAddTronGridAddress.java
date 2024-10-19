/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.trongrid.TronGridGenerateAddress;
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
public class MCUserAddTronGridAddress extends AbstractOperation<String> {

    private final String userName;
    private final boolean newWallet;

    public MCUserAddTronGridAddress(String userName, boolean newWallet) {
        super(String.class);
        this.userName = userName;
        this.newWallet = newWallet;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userName);
        if (!userFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            String walletsTag = "mcWalletsTron";
            if (!newWallet && user.has(walletsTag) && user.get(walletsTag).has("current")) {
                super.response = "OK";
                return;
            }
            JsonNode mcUserAddress = new TronGridGenerateAddress().getResponse();
            if (!mcUserAddress.has("address") || !mcUserAddress.has("privateKey") || !mcUserAddress.has("hexAddress")) {
                super.response = "OK";
                return;
            }            
            String address = mcUserAddress.get("address").textValue();
            JsonNode userWallet = mapper.createObjectNode();
            ((ObjectNode) userWallet).put("address", address);
            File userWalletFile = UsersAddressesFolderLocator.getAddressFile(address);
            ((ObjectNode) userWallet).put("privateKey", mcUserAddress.get("privateKey").textValue());
            ((ObjectNode) userWallet).put("hexAddress", mcUserAddress.get("hexAddress").textValue());
            ((ObjectNode) userWallet).put("userName", userName);
            ((ObjectNode) userWallet).put("type", "MONEYCLICK");
            if (!FileUtil.createFile(userWallet, userWalletFile)) {
                super.response = "OK";
                return;
            }
            String timestamp = DateUtil.getCurrentDate();
            JsonNode wallet = mapper.createObjectNode();
            ((ObjectNode) wallet).put("address", userWallet.get("address").textValue());
            ((ObjectNode) wallet).put("privateKey", userWallet.get("privateKey").textValue());
            ((ObjectNode) wallet).put("hexAddress", userWallet.get("hexAddress").textValue());
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
            Logger.getLogger(MCUserAddTronGridAddress.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
