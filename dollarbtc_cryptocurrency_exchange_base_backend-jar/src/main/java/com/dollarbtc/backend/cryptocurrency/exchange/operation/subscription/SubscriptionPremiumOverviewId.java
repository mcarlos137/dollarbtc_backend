/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.SubscriptionsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class SubscriptionPremiumOverviewId extends AbstractOperation<String> {
    
    private final String userName;

    public SubscriptionPremiumOverviewId(String userName) {
        super(String.class);
        this.userName = userName;
    }

    @Override
    public void execute() {
        File subscriptionsPremiumOverviewFile = SubscriptionsFolderLocator.getPremiumOverviewFile();
        if (!subscriptionsPremiumOverviewFile.isFile()) {
            super.response = "";
            return;
        }
        try {
            JsonNode subscriptionsPremiumOverview = mapper.readTree(subscriptionsPremiumOverviewFile);
            super.response = subscriptionsPremiumOverview.get("id").textValue();
            return;
        } catch (IOException ex) {
            Logger.getLogger(SubscriptionPremiumOverviewId.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "";
    }

}
