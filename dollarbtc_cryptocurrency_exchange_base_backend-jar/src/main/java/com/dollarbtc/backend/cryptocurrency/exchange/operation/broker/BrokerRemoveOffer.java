/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker.BrokerRemoveOfferRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BrokerRemoveOffer extends AbstractOperation<String> {

    private final BrokerRemoveOfferRequest brokerRemoveOfferRequest;

    public BrokerRemoveOffer(BrokerRemoveOfferRequest brokerRemoveOfferRequest) {
        super(String.class);
        this.brokerRemoveOfferRequest = brokerRemoveOfferRequest;
    }

    @Override
    protected void execute() {
        File brokerOfferFolder = BrokersFolderLocator.getOfferFolder(brokerRemoveOfferRequest.getUserName(), brokerRemoveOfferRequest.getCurrency(), brokerRemoveOfferRequest.getOfferType(), brokerRemoveOfferRequest.getPaymentId(), brokerRemoveOfferRequest.getPaymentType());
        if (!brokerOfferFolder.isDirectory()) {
            super.response = "OFFER DOES NOT EXIST";
            return;
        }
        File brokerOfferOldFolder = FileUtil.createFolderIfNoExist(brokerOfferFolder, "Old");
        for (File brokerOfferFile : brokerOfferFolder.listFiles()) {
            if (!brokerOfferFile.isFile()) {
                continue;
            }
            FileUtil.moveFileToFolder(brokerOfferFile, brokerOfferOldFolder);
        }
        super.response = "OK";
    }

}
