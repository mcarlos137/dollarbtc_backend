/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis.CryptoAPIsGenerateAddress;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis.CryptoAPIsGenerateDepositAddress;
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
public class MCUserAddCryptoAPIsAddress extends AbstractOperation<String> {

    private final String userName, blockchain;
    private final boolean newWallet;

    public MCUserAddCryptoAPIsAddress(String userName, String blockchain, boolean newWallet) {
        super(String.class);
        this.userName = userName;
        this.blockchain = blockchain;
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
            String walletsTag = "mcWallets";
            if (blockchain.equals("ethereum")) {
                walletsTag = "mcWalletsEthereum";
            }
            if (blockchain.equals("tron")) {
                walletsTag = "mcWalletsTron";
            }
            if (!newWallet && user.has(walletsTag) && user.get(walletsTag).has("current")) {
                super.response = "OK";
                return;
            }
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>000 ");
            JsonNode mcUserAddress;
            String address = null;
            JsonNode userWallet = mapper.createObjectNode();
            if (!blockchain.equals("tron")) {
                mcUserAddress = new CryptoAPIsGenerateAddress(blockchain, "mainnet").getResponse();
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>mcUserAddress1 " + mcUserAddress);
                if (!mcUserAddress.has("data") || !mcUserAddress.get("data").has("item") || !mcUserAddress.get("data").get("item").has("addresses")) {
                    super.response = "OK";
                    return;
                }
                userWallet = mcUserAddress.get("data").get("item");
                Iterator<JsonNode> mcUserWalletIterator = userWallet.get("addresses").iterator();
                while (mcUserWalletIterator.hasNext()) {
                    JsonNode mcUserWalletIt = mcUserWalletIterator.next();
                    String format = mcUserWalletIt.get("format").textValue();
                    if (blockchain.equals("bitcoin") && !format.equals("P2PKH")) {
                        continue;
                    }
                    address = mcUserWalletIt.get("address").textValue();
                    break;
                }
                if (address == null) {
                    super.response = "OK";
                    return;
                }
            } else {
                mcUserAddress = new CryptoAPIsGenerateDepositAddress(blockchain, "mainnet", userName).getResponse();
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>mcUserAddress2 " + mcUserAddress);
                if (!mcUserAddress.has("data") || !mcUserAddress.get("data").has("item") || !mcUserAddress.get("data").get("item").has("address")) {
                    super.response = "OK";
                    return;
                }
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>2.1 ");
                address = mcUserAddress.get("data").get("item").get("address").textValue();
                ((ObjectNode) userWallet).put("privateKey", "N/A");
            }
            ((ObjectNode) userWallet).put("address", address);
            File userWalletFile = UsersAddressesFolderLocator.getAddressFile(address);
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
            Logger.getLogger(MCUserAddCryptoAPIsAddress.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
