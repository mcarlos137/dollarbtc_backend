/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bitcoin.main;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

/**
 *
 * @author CarlosDaniel
 */
public class NewMain2 {

    private static Wallet wallet;
    private static File walletFile;
    private static PeerGroup peerGroup;
    private static BlockStore blockStore;
    private final static String FILES_ROOT = "F:\\";

    public static void main(String[] args) throws UnreadableWalletException, NoSuchAlgorithmException, Exception {
        NetworkParameters params = TestNet3Params.get();
//        String filePrefix = "peer2-testnet";
//        WalletAppKit kit = new WalletAppKit(params, new File("F:\\"), filePrefix);
        // Download the block chain and wait until it's done.
//        kit.startAsync();
//        kit.awaitRunning();
        String publicKey = "myJSEXLNB43iXkYUhrny2tnYuQNNg13xyY";
        String privateKey = "91t2GpmXBLQ1iPqzc573xwgtVYRtjtNjU6BTth6f7yEZ1RqGDKF";
        walletFile = new File(FILES_ROOT, privateKey + ".dat");
        if (walletFile.exists()) {
            wallet = Wallet.loadFromFile(walletFile);
        } else {
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
            wallet = new Wallet(params);
            wallet.importKey(key);
        }
        wallet = new Wallet(params);
        wallet.addWatchedAddress(new Address(params, publicKey), 0);
        System.out.println("wallet.getWatchedAddresses()" + wallet.getWatchedAddresses());
        blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, wallet, blockStore);
        peerGroup = new PeerGroup(params, chain);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
        peerGroup.addWallet(wallet);
        peerGroup.startAsync();
        peerGroup.downloadBlockChain();
        Coin balance = wallet.getBalance();
        System.out.println("Wallet balance: " + balance);
//        destroy();

    }

    public static void destroy() throws Exception {
        if (peerGroup == null) {
            return;
        }
        wallet.saveToFile(walletFile);
        peerGroup.stopAsync();
        blockStore.close();
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
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
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

    private void transactionLoop() {
        while (true) {
            //look for new transactions
            
            //look for new addresses
            boolean newAddresses = false;
            if(newAddresses){
                break;
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                Logger.getLogger(NewMain2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
