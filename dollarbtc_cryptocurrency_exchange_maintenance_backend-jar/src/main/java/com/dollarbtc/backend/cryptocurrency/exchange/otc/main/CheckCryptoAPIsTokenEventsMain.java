/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis.CryptoAPIsCheckInEthereumTokenTransaction;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CryptoAPIsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersAddressesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class CheckCryptoAPIsTokenEventsMain {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> currencyBlockchain = new HashMap<>();
        currencyBlockchain.put("USDT", "ethereum");
        for (String currency : currencyBlockchain.keySet()) {
            String blockchain = currencyBlockchain.get(currency);
            File cryptoAPIsEventsFolder = CryptoAPIsFolderLocator.getEventsFolder(blockchain, "mainnet", "ADDRESS_TOKENS_TRANSACTION_UNCONFIRMED");
            File cryptoAPIsEventsOldFolder = CryptoAPIsFolderLocator.getEventsOldFolder(blockchain, "mainnet", "ADDRESS_TOKENS_TRANSACTION_UNCONFIRMED");
            for (File cryptoAPIsEventsFile : cryptoAPIsEventsFolder.listFiles()) {
                if (!cryptoAPIsEventsFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode cryptoAPIsEvents = mapper.readTree(cryptoAPIsEventsFile);
                    String address = cryptoAPIsEvents.get("address").textValue();
                    String transactionId = cryptoAPIsEvents.get("transactionId").textValue();
                    File usersAddressFile = UsersAddressesFolderLocator.getAddressFile(address);
                    if (!usersAddressFile.isFile()) {
                        continue;
                    }
                    JsonNode usersAddress = mapper.readTree(usersAddressFile);
                    String userName = usersAddress.get("userName").textValue();
                    Logger.getLogger(CheckCryptoAPIsTokenEventsMain.class.getName()).log(Level.INFO, "userName {0}", userName);
                    System.out.println("transactionId " + transactionId);
                    System.out.println("address " + address);
                    Boolean allConfirmed = new CryptoAPIsCheckInEthereumTokenTransaction(userName, address, transactionId, currency).getResponse();
                    if (allConfirmed) {
                        FileUtil.moveFileToFolder(cryptoAPIsEventsFile, cryptoAPIsEventsOldFolder);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CheckCryptoAPIsTokenEventsMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CheckCryptoAPIsTokenEventsMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
