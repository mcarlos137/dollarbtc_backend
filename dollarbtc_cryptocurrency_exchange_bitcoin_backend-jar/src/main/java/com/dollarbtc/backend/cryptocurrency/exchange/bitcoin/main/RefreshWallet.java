/*
 * Copyright 2011 Google Inc.
 * Copyright 2014 Andreas Schildbach
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
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.io.File;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.MainNetParams;

/**
 * RefreshWallet loads a wallet, then processes the block chain to update the transaction pools within it.
 * To get a test wallet you can use wallet-tool from the tools subproject.
 */
public class RefreshWallet {
    
//    private static Wallet wallet;
    private static File walletFile;
//    private static PeerGroup peerGroup;
//    private static BlockStore blockStore;
    private final static String FILES_ROOT = "F:\\";
    
    public static void main(String[] args) throws Exception {
//        String publicKey = "myJSEXLNB43iXkYUhrny2tnYuQNNg13xyY";
//        String privateKey = "91t2GpmXBLQ1iPqzc573xwgtVYRtjtNjU6BTth6f7yEZ1RqGDKF";
        walletFile = new File("C:\\Users\\carlosmolina\\Documents\\Workspace\\dollarBTC\\production\\OLD\\MAIN\\wallet.wallet");
        Wallet wallet = Wallet.loadFromFile(walletFile);
        System.out.println(wallet.toString());

        // Set up the components and link them together.
        final NetworkParameters params = MainNetParams.get();
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
        wallet.saveToFile(walletFile);
        System.out.println("\nDone!\n");
        System.out.println(wallet.toString());
    }
}