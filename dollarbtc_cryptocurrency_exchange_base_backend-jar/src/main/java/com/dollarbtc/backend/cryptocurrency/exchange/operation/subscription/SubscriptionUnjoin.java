/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription.SubscriptionUnjoinRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.SubscriptionsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class SubscriptionUnjoin extends AbstractOperation<String> {

    private final SubscriptionUnjoinRequest subscriptionUnjoinRequest;

    public SubscriptionUnjoin(SubscriptionUnjoinRequest subscriptionUnjoinRequest) {
        super(String.class);
        this.subscriptionUnjoinRequest = subscriptionUnjoinRequest;
    }

    @Override
    protected void execute() {
        File subscriptionFile = SubscriptionsFolderLocator.getFile(subscriptionUnjoinRequest.getId());
        if (!subscriptionFile.isFile()) {
            super.response = "SUBSCRIPTION DOES NOT EXIST";
            return;
        }
        try {
            JsonNode subscription = mapper.readTree(subscriptionFile);
            ((ObjectNode) subscription).put("status", SubscriptionStatus.INACTIVE.name());
            FileUtil.editFile(subscription, subscriptionFile);

        } catch (IOException ex) {
            Logger.getLogger(SubscriptionUnjoin.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonNode index = mapper.createObjectNode();
        ((ObjectNode) index).put("id", subscriptionUnjoinRequest.getId());
        ((ObjectNode) index).put("timestamp", DateUtil.getCurrentDate());
        FileUtil.deleteFile(new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("Statuses", SubscriptionStatus.ACTIVE.name()), subscriptionUnjoinRequest.getId() + ".json"));
        FileUtil.editFile(index, new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("Statuses", SubscriptionStatus.INACTIVE.name()), subscriptionUnjoinRequest.getId() + ".json"));
        super.response = "OK";
    }

}
