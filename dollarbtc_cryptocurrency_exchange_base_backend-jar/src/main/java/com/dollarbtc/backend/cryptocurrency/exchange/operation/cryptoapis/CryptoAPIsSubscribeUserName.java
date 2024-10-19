/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CryptoAPIsFolderLocator;
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
public class CryptoAPIsSubscribeUserName extends AbstractOperation<Void> {

    private final String userName, blockchain, network, event;
    private final String[] walletsTags;

    public CryptoAPIsSubscribeUserName(String userName, String blockchain, String network, String event, String[] walletsTags) {
        super(Void.class);
        this.userName = userName;
        this.blockchain = blockchain;
        this.network = network;
        this.event = event;
        this.walletsTags = walletsTags;
    }

    @Override
    protected void execute() {
        Logger.getLogger(CryptoAPIsSubscribeUserName.class.getName()).log(Level.INFO, userName);
        boolean subscribe = false;
        String timestamp = DateUtil.getCurrentDate();
        File cryptoAPIsEventSubscriptionsFile = CryptoAPIsFolderLocator.getEventSubscriptionsFile(blockchain, network, event, userName);
        if (!cryptoAPIsEventSubscriptionsFile.isFile()) {
            subscribe = true;
        } else {
            try {
                JsonNode cryptoAPIsEventSubscription = mapper.readTree(cryptoAPIsEventSubscriptionsFile);
                ((ObjectNode) cryptoAPIsEventSubscription).put("timestamp", timestamp);
                FileUtil.editFile(cryptoAPIsEventSubscription, cryptoAPIsEventSubscriptionsFile);
            } catch (IOException ex) {
                Logger.getLogger(CryptoAPIsSubscribeUserName.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (subscribe) {
            File userConfigFile = UsersFolderLocator.getConfigFile(userName);
            try {
                JsonNode userConfig = mapper.readTree(userConfigFile);
                for (String walletsTag : walletsTags) {
                    if (!userConfig.has(walletsTag)) {
                        continue;
                    }
                    String[] walletsStatusTags = new String[]{"current"};
                    String asset = "coins";
                    if(event.contains("TOKENS")){
                        asset = "tokens";
                    }
                    for (String walletsStatusTag : walletsStatusTags) {
                        if (!userConfig.get(walletsTag).has(walletsStatusTag)) {
                            continue;
                        }
                        Iterator<JsonNode> usersWalletsStatusTagIterator = userConfig.get(walletsTag).get(walletsStatusTag).iterator();
                        while (usersWalletsStatusTagIterator.hasNext()) {
                            JsonNode usersWalletsStatusTagIt = usersWalletsStatusTagIterator.next();
                            JsonNode cryptoAPIsEventSubscription = new CryptoAPIsSubscribeAddress(usersWalletsStatusTagIt.get("address").textValue(), asset, blockchain, network).getResponse();
                            ((ObjectNode) cryptoAPIsEventSubscription).put("timestamp", timestamp);
                            FileUtil.createFile(cryptoAPIsEventSubscription, cryptoAPIsEventSubscriptionsFile);
                            break;
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CryptoAPIsSubscribeUserName.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
