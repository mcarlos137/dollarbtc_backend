/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bitcoin.main;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.Wallet.SendResult;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

public class BitcoinJ {

//    private final PeerGroup peerGroup;
//    private final AbstractBlockChain blockChain;
//    private final BlockStore blockStore;
    private final NetworkParameters params;
    private final Map<String, Wallet> wallets;

    public BitcoinJ(Context context) throws Exception {
        File bitcoinControllerFile = new File(OPERATOR_PATH, "bitcoinController.json");
        File blockchainFile = new File(OPERATOR_PATH, "blockchain.spvchain");
        if (!bitcoinControllerFile.isFile()) {
            FileUtil.deleteFile(blockchainFile);
            for (File addressesCurrencySpecificFolder : AddressesFolderLocator.getCurrencyFolder("BTC").listFiles()) {
                FileUtil.deleteFile(new File(addressesCurrencySpecificFolder, "wallet.wallet"));
            }
            JsonNode bitcoinController = new ObjectMapper().createObjectNode();
            ((ObjectNode) bitcoinController).put("processActive", true);
            ((ObjectNode) bitcoinController).put("forcedToExit", false);
            FileUtil.createFile(bitcoinController, bitcoinControllerFile);
        } else {
            JsonNode bitcoinController = new ObjectMapper().readTree(bitcoinControllerFile);
            if (bitcoinController.get("processActive").booleanValue()) {
                FileUtil.deleteFile(blockchainFile);
                for (File addressesCurrencySpecificFolder : AddressesFolderLocator.getCurrencyFolder("BTC").listFiles()) {
                    FileUtil.deleteFile(new File(addressesCurrencySpecificFolder, "wallet.wallet"));
                }
            } else {
                ((ObjectNode) bitcoinController).put("processActive", true);
                FileUtil.editFile(bitcoinController, bitcoinControllerFile);
            }
        }
        params = context.getParams();
//        blockStore = new SPVBlockStore(params, blockchainFile);
//        blockChain = new BlockChain(params, blockStore);
//        peerGroup = new PeerGroup(params, blockChain);
        wallets = new HashMap<>();
        Map<String, String> privateKeys = getPrivateKeys();
//        if (privateKeys.isEmpty()) {
//            return;
//        }
        for (String address : privateKeys.keySet()) {
            File walletFile = new File(new File(AddressesFolderLocator.getCurrencyFolder("BTC"), address), "wallet.wallet");
            if (walletFile.isFile()) {
                wallets.put(address, Wallet.loadFromFile(walletFile));
            } else {
                importAddress(address, privateKeys.get(address));
            }
        }
        for (String address : wallets.keySet()) {
            refreshWallet(wallets.get(address), address);
//            blockChain.addWallet(wallets.get(address));
//            peerGroup.addWallet(wallets.get(address));
        }
//        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
//        peerGroup.setFastCatchupTimeSecs(DateUtil.parseDate(2018, 9, 1, 0, 0, 0, 0).getTime() / 1000);
//        peerGroup.startAsync();
//        peerGroup.downloadBlockChain();
    }

    public boolean existNewPrivateKeys() {
        return getPrivateKeys().size() > wallets.keySet().size();
    }

    private Map<String, String> getPrivateKeys() {
        Map<String, String> privateKeys = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        File addressesCurrencyFolder = AddressesFolderLocator.getCurrencyFolder("BTC");
        for (File addressFolder : addressesCurrencyFolder.listFiles()) {
            if (!addressFolder.isDirectory() || addressFolder.getName().equals("Operations") || addressFolder.getName().equals("Transactions")) {
                continue;
            }
            File addressConfigFile = new File(addressFolder, "config.json");
            if (!addressConfigFile.isFile()) {
                continue;
            }
            try {
                JsonNode address = mapper.readTree(addressConfigFile);
                if (!address.has("privateKey") && !address.has("private")) {
                    continue;
                }
                if (address.has("privateKey")) {
                    privateKeys.put(address.get("address").textValue(), address.get("privateKey").textValue());
                }
                if (address.has("private")) {
                    privateKeys.put(address.get("address").textValue(), address.get("private").textValue());
                }
            } catch (IOException ex) {
                Logger.getLogger(BitcoinJ.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return privateKeys;
    }

    private void importAddress(String address, String privateKey) throws NoSuchAlgorithmException {
        ECKey key;
        if (privateKey.length() == 51 || privateKey.length() == 52) {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, privateKey);
            key = dumpedPrivateKey.getKey();
        } else {
            privateKey = importPrivateKey(privateKey);
            BigInteger privKey = Base58.decodeToBigInteger(privateKey);
            key = ECKey.fromPrivate(privKey);
        }
        System.out.println("Address from private key is: " + key.toAddress(params).toString());
        List<ECKey> keys = new ArrayList<>();
        keys.add(key);
        wallets.put(address, Wallet.fromKeys(params, keys));
    }

    private void getBalance(String address) {
//        wallets.get(address).addWatchedAddress(new Address(params, address), 0);
//        System.out.println("wallet.getWatchedAddresses()" + wallets.get(address).getWatchedAddresses());
        System.out.println("BalanceType.AVAILABLE " + wallets.get(address).getBalance(Wallet.BalanceType.AVAILABLE).toFriendlyString());
        System.out.println("BalanceType.AVAILABLE_SPENDABLE " + wallets.get(address).getBalance(Wallet.BalanceType.AVAILABLE_SPENDABLE).toFriendlyString());
        System.out.println("BalanceType.ESTIMATED " + wallets.get(address).getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());
        System.out.println("BalanceType.ESTIMATED_SPENDABLE " + wallets.get(address).getBalance(Wallet.BalanceType.ESTIMATED_SPENDABLE).toFriendlyString());
        System.out.println("Balance " + wallets.get(address).getBalance().toFriendlyString());
    }

    public int getConfirmations(String address, String hash) {
        // Return the amount of confirmations
        return wallets.get(address).getTransaction(new Sha256Hash(hash)).getConfidence().getDepthInBlocks();
    }

    public void checkProcessingOperations() {
        ObjectMapper mapper = new ObjectMapper();
        File addressesCurrencyOperationsProcessingFolder = AddressesFolderLocator.getCurrencyOperationsFolder("BTC", "PROCESSING");
        File addressesCurrencyOperationsOkFolder = AddressesFolderLocator.getCurrencyOperationsFolder("BTC", "OK");
        File addressesCurrencyOperationsFailFolder = AddressesFolderLocator.getCurrencyOperationsFolder("BTC", "FAIL");
        for (File addressesCurrencyOperationsProcessingFile : addressesCurrencyOperationsProcessingFolder.listFiles()) {
            try {
                JsonNode operationProcessing = mapper.readTree(addressesCurrencyOperationsProcessingFile);
                switch (operationProcessing.get("operation").textValue()) {
                    case "SEND_OUT":
                        try {
                        sendBitcoins(operationProcessing.get("amount").doubleValue(), operationProcessing.get("baseAddress").textValue(), operationProcessing.get("targetAddress").textValue());
                        FileUtil.moveFileToFolder(addressesCurrencyOperationsProcessingFile, addressesCurrencyOperationsOkFolder);
                    } catch (Exception ex) {
                        Logger.getLogger(BitcoinJ.class.getName()).log(Level.SEVERE, null, ex);
                        ((ObjectNode) operationProcessing).put("error", ex.getMessage());
                        FileUtil.editFile(operationProcessing, addressesCurrencyOperationsProcessingFile);
                        FileUtil.moveFileToFolder(addressesCurrencyOperationsProcessingFile, addressesCurrencyOperationsFailFolder);
                    }
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(BitcoinJ.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean checkForcedToExit() {
        File bitcoinControllerFile = new File(OPERATOR_PATH, "bitcoinController.json");
        JsonNode bitcoinController;
        try {
            bitcoinController = new ObjectMapper().readTree(bitcoinControllerFile);
            return bitcoinController.get("forcedToExit").booleanValue();
        } catch (IOException ex) {
            Logger.getLogger(BitcoinJ.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void sendBitcoins(double amount, String sendAddress, String receiveAddress) throws Exception {
        System.out.println("Balance " + wallets.get(sendAddress).getBalance().toFriendlyString());
        File bitcoinControllerFile = new File(OPERATOR_PATH, "bitcoinController.json");
        JsonNode bitcoinController = new ObjectMapper().readTree(bitcoinControllerFile);
        System.out.println("-----------------------");
        getBalance(sendAddress);
        System.out.println("-----------------------");
        Transaction transaction = new Transaction(params);
        System.out.println("----------------------- addInput");
        System.out.println("wallets: " + wallets);
        System.out.println("wallets.get(sendAddress): " + wallets.get(sendAddress));
        System.out.println("wallets.get(sendAddress).getUnspents(): " + wallets.get(sendAddress).getUnspents());
//        transaction.addInput(wallets.get(sendAddress).getUnspents().get(0));
        System.out.println("----------------------- addOutput");
        System.out.println("Coin.parseCoin(String.valueOf(amount)): " + Coin.parseCoin(String.valueOf(amount)));
        System.out.println("Address.fromBase58(params, receiveAddress): " + Address.fromBase58(params, receiveAddress));
//        transaction.addOutput(Coin.parseCoin(String.valueOf(amount)), Address.fromBase58(params, receiveAddress));
        System.out.println("------------------------------------------------------");
//        SendRequest sendRequest = SendRequest.forTx(transaction);
//        sendRequest.feePerKb = Coin.valueOf(bitcoinController.get("feeInSatoshi").intValue());
//        SendResult sendResult = wallets.get(sendAddress).sendCoins(peerGroup, sendRequest);
//        sendResult.broadcastComplete.get();
    }

    public void destroy() throws Exception {
        File bitcoinControllerFile = new File(OPERATOR_PATH, "bitcoinController.json");
        JsonNode bitcoinController = new ObjectMapper().readTree(bitcoinControllerFile);
        ((ObjectNode) bitcoinController).put("processActive", false);
        ((ObjectNode) bitcoinController).put("forcedToExit", false);
        FileUtil.editFile(bitcoinController, bitcoinControllerFile);
        for (String address : wallets.keySet()) {
            wallets.get(address).saveToFile(new File(new File(AddressesFolderLocator.getCurrencyFolder("BTC"), address), "wallet.wallet"));
        }
//        if (peerGroup == null) {
//            return;
//        }
//        peerGroup.stopAsync();
//        blockStore.close();
    }

    private static String importPrivateKey(String privateKey) throws NoSuchAlgorithmException {
        String baseString = "80" + privateKey;
        String sha256String = bytesToHex(MessageDigest.getInstance("SHA-256").digest(hexStringToByteArray(baseString)));
        sha256String = bytesToHex(MessageDigest.getInstance("SHA-256").digest(hexStringToByteArray(sha256String)));
        baseString = baseString + sha256String.substring(0, 8);
        return Base58.encode(hexStringToByteArray(baseString));
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
    
    public void refreshWallet(Wallet wallet, String address) throws Exception {
        System.out.println(wallet.toString());
        // Set up the components and link them together.
        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, wallet, blockStore);
        final PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
        peerGroup.startAsync();
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public synchronized void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("\nReceived tx " + tx);
                System.out.println(tx.toString());
            }
        });

        // Now download and process the block chain.
        peerGroup.downloadBlockChain();
        peerGroup.stopAsync();
        wallets.get(address).saveToFile(new File(new File(AddressesFolderLocator.getCurrencyFolder("BTC"), address), "wallet.wallet"));
        System.out.println("\nDone!\n");
        System.out.println(wallet.toString());
    }

}
