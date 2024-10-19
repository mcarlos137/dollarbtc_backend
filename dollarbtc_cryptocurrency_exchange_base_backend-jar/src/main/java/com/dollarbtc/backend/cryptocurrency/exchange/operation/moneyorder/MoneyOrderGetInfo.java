/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneyorder;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyOrdersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyOrderGetInfo extends AbstractOperation<JsonNode> {

    private final String currency, language;

    public MoneyOrderGetInfo(String currency, String language) {
        super(JsonNode.class);
        this.currency = currency;
        this.language = language;
    }

    @Override
    public void execute() {
        try {
            File moneyOrdersConfigFile = MoneyOrdersFolderLocator.getConfigFile();
            super.response = mapper.readTree(moneyOrdersConfigFile).get(currency + "__" + language);
            return;
        } catch (IOException ex) {
            Logger.getLogger(MoneyOrderGetInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = mapper.createObjectNode();
    }

}
