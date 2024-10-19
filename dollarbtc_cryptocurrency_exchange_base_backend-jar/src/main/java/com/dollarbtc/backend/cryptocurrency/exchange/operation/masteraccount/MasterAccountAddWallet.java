/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserCreateNewWallet;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetNewAddress;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersAddressesFolderLocator;
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
public class MasterAccountAddWallet extends AbstractOperation<String> {

    private final String masterAccountName;
    private final boolean newWallet;

    public MasterAccountAddWallet(String masterAccountName, boolean newWallet) {
        super(String.class);
        this.masterAccountName = masterAccountName;
        this.newWallet = newWallet;
    }

    @Override
    public void execute() {
        File masterAccountFolder = MasterAccountFolderLocator.getFolderByMasterAccountName(masterAccountName);
        if (!masterAccountFolder.isDirectory()) {
            this.response = "MASTER ACCOUNT DOES NOT EXIST";
            return;
        }
        File masterAccountConfigFile = MasterAccountFolderLocator.getConfigFileByMasterAccountName(masterAccountName);
        String masterAccountName25 = masterAccountName;
        if (masterAccountName25.length() > 25) {
            masterAccountName25 = masterAccountName25.substring(0, 25);
        }
        try {
            JsonNode masterAccountConfig = mapper.createObjectNode();
            if (masterAccountConfigFile.isFile()) {
                masterAccountConfig = mapper.readTree(masterAccountConfigFile);
            }
            String walletsTag = "wallets";
            if (!newWallet && masterAccountConfig.has(walletsTag) && masterAccountConfig.get(walletsTag).has("current")) {
                this.response = "OK";
                return;
            }
            JsonNode masterAccountAddress = new UserGetNewAddress(masterAccountName25).getResponse();
            if (masterAccountAddress.has("error")) {
                Logger.getLogger(MasterAccountAddWallet.class.getName()).log(Level.INFO, "{0}", masterAccountAddress.get("error").textValue());
                new UserCreateNewWallet(masterAccountName25).getResponse();
                masterAccountAddress = new UserGetNewAddress(masterAccountName25).getResponse();
            }
            if (!masterAccountAddress.has("address")) {
                this.response = "OK";
                return;
            }
            File userAddressFile = UsersAddressesFolderLocator.getAddressFile(masterAccountAddress.get("address").textValue());
            ((ObjectNode) masterAccountAddress).put("userName", masterAccountName);
            String type = "OTC";
            ((ObjectNode) masterAccountAddress).put("type", type);
            if (!FileUtil.createFile(masterAccountAddress, userAddressFile)) {
                this.response = "OK";
                return;
            }
            String timestamp = DateUtil.getCurrentDate();
            JsonNode wallet = mapper.createObjectNode();
            ((ObjectNode) wallet).put("address", masterAccountAddress.get("address").textValue());
            ((ObjectNode) wallet).put("privateKey", masterAccountAddress.get("private").textValue());
            if (!masterAccountConfig.has(walletsTag)) {
                ((ObjectNode) masterAccountConfig).set(walletsTag, mapper.createObjectNode());
            }
            JsonNode wallets = masterAccountConfig.get(walletsTag);
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
            FileUtil.editFile(masterAccountConfig, masterAccountConfigFile);
            this.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MasterAccountAddWallet.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.response = "FAIL";
    }

}
