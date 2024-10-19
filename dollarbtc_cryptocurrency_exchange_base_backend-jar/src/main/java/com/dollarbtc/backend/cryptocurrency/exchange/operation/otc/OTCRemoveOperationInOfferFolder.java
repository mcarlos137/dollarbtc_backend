/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class OTCRemoveOperationInOfferFolder extends AbstractOperation<Void> {
    
    private final String currency, operationId, brokerUserName;

    public OTCRemoveOperationInOfferFolder(String currency, String operationId, String brokerUserName) {
        super(Void.class);
        this.currency = currency;
        this.operationId = operationId;
        this.brokerUserName = brokerUserName;
    }
        
    @Override
    public void execute() {
        if (brokerUserName == null || brokerUserName.equals("")) {
            File otcCurrencyOffersFolder = OTCFolderLocator.getCurrencyOffersFolder(null, currency);
            for (File otcCurrencyOfferFolder : otcCurrencyOffersFolder.listFiles()) {
                if (!otcCurrencyOfferFolder.isDirectory()) {
                    continue;
                }
                File otcCurrencyOfferOperationsFolder = new File(otcCurrencyOfferFolder, "Operations");
                if (!otcCurrencyOfferOperationsFolder.isDirectory()) {
                    continue;
                }
                for (File otcCurrencyOfferOperationsDateFolder : otcCurrencyOfferOperationsFolder.listFiles()) {
                    if (!otcCurrencyOfferOperationsDateFolder.isDirectory()) {
                        continue;
                    }
                    File otcCurrencyOfferOperationsDateOperationIdFile = new File(otcCurrencyOfferOperationsDateFolder, operationId + ".json");
                    if (otcCurrencyOfferOperationsDateOperationIdFile.isFile()) {
                        FileUtil.deleteFile(otcCurrencyOfferOperationsDateOperationIdFile);
                        return;
                    }
                }
            }
        } else {
            File brokerOffersFolder = BrokersFolderLocator.getOffersFolder(brokerUserName);
            for (File brokerOfferFolder : brokerOffersFolder.listFiles()) {
                if (!brokerOfferFolder.isDirectory()) {
                    continue;
                }
                File brokerOfferOperationsFolder = new File(brokerOfferFolder, "Operations");
                if (!brokerOfferOperationsFolder.isDirectory()) {
                    continue;
                }
                for (File brokerOfferOperationsDateFolder : brokerOfferOperationsFolder.listFiles()) {
                    if (!brokerOfferOperationsDateFolder.isDirectory()) {
                        continue;
                    }
                    File brokerOfferOperationsDateOperationIdFile = new File(brokerOfferOperationsDateFolder, operationId + ".json");
                    if (brokerOfferOperationsDateOperationIdFile.isFile()) {
                        FileUtil.deleteFile(brokerOfferOperationsDateOperationIdFile);
                        return;
                    }
                }
            }
        }
    }
    
}
