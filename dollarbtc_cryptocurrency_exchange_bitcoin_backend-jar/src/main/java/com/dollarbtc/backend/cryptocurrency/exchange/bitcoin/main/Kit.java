/*
 * Copyright by the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.bitcoin.main;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.KeyChainEventListener;
import org.bitcoinj.wallet.listeners.ScriptsChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;

/**
 * The following example shows how to use the by bitcoinj provided WalletAppKit.
 * The WalletAppKit class wraps the boilerplate (Peers, BlockChain,
 * BlockStorage, Wallet) needed to set up a new SPV bitcoinj app.
 *
 * In this example we also define a WalletEventListener class with implementors
 * that are called when the wallet changes (for example sending/receiving money)
 */
public class Kit {

    private final WalletAppKit walletAppKit;
    private final NetworkParameters params;
    private final static String FILES_ROOT = "F:\\";

    public Kit(Context context, String publicKey) throws NoSuchAlgorithmException, InsufficientMoneyException {
        walletAppKit = new WalletAppKit(context.getParams(), new File(FILES_ROOT), "walletappkit");
        params = context.getParams();
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        walletAppKit.wallet().addWatchedAddress(new Address(params, publicKey), 0);
        System.out.println("wallet.getWatchedAddresses()" + walletAppKit.wallet().getWatchedAddresses());
        walletAppKit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("-----> coins resceived: " + tx.getHashAsString());
                System.out.println("received: " + tx.getValue(wallet));
            }
        });
        walletAppKit.wallet().addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("coins sent");
            }
        });
        walletAppKit.wallet().addKeyChainEventListener(new KeyChainEventListener() {
            @Override
            public void onKeysAdded(List<ECKey> keys) {
                System.out.println("new key added");
            }
        });
        walletAppKit.wallet().addScriptsChangeEventListener(new ScriptsChangeEventListener() {
            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                System.out.println("new script added");
            }
        });
        walletAppKit.wallet().addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                System.out.println("-----> confidence changed: " + tx.getHashAsString());
                TransactionConfidence confidence = tx.getConfidence();
                System.out.println("new block depth: " + confidence.getDepthInBlocks());
            }
        });

        // Ready to run. The kit syncs the blockchain and our wallet event listener gets notified when something happens.
        // To test everything we create and print a fresh receiving address. Send some coins to that address and see if everything works.
        System.out.println("send money to: " + walletAppKit.wallet().freshReceiveAddress());
        System.out.println("wallet: " + walletAppKit.wallet());
        System.out.println("wallet balance: " + walletAppKit.wallet().getBalance());
        System.out.println("wallet totalReceived: " + walletAppKit.wallet().getTotalReceived());
        // Make sure to properly shut down all the running services when you manually want to stop the kit. The WalletAppKit registers a runtime ShutdownHook so we actually do not need to worry about that when our application is stopping.
        //System.out.println("shutting down again");
        //kit.stopAsync();
        //kit.awaitTerminated();
    }

    public void importKey(String privateKey) throws NoSuchAlgorithmException {
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
        walletAppKit.wallet().importKey(key);
    }

    public void sendBitcoins(double ammount, String sendAddress, String receiveAddress) throws InsufficientMoneyException {
        // Send bitcoins to an adress
        walletAppKit.wallet().addWatchedAddress(new Address(params, sendAddress), 0);
        System.out.println("Have " + walletAppKit.wallet().getBalance().toFriendlyString());
        walletAppKit.wallet().sendCoins(walletAppKit.peerGroup(), new Address(walletAppKit.params(), receiveAddress), Coin.parseCoin(String.valueOf(ammount)));
    }

    private String importPrivateKey(String privateKey) throws NoSuchAlgorithmException {
        String baseString = "80" + privateKey;
        String sha256String = bytesToHex(MessageDigest.getInstance("SHA-256").digest(hexStringToByteArray(baseString)));
        sha256String = bytesToHex(MessageDigest.getInstance("SHA-256").digest(hexStringToByteArray(sha256String)));
        baseString = baseString + sha256String.substring(0, 8);
        return Base58.encode(hexStringToByteArray(baseString));
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

}
