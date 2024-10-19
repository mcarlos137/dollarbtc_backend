/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccountnew;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountNewGetOperatorNameRandom extends AbstractOperation<String> {
    
    private final String currency;

    public MasterAccountNewGetOperatorNameRandom(String currency) {
        super(String.class);
        this.currency = currency;
    }
    
    @Override
    public void execute() {
        List<String> operatorNames = new ArrayList<>();
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operator = operatorsIt.textValue();
            File masterAccountFile = MasterAccountFolderLocator.getConfigFile(operator);
            if (masterAccountFile.isFile()) {
                try {
                    Iterator<JsonNode> masterAccountIterator = mapper.readTree(masterAccountFile).iterator();
                    while (masterAccountIterator.hasNext()) {
                        JsonNode masterAccountIt = masterAccountIterator.next();
                        Iterator<JsonNode> masterAccountItCurrenciesIterator = masterAccountIt.get("currencies").iterator();
                        while (masterAccountItCurrenciesIterator.hasNext()) {
                            JsonNode masterAccountItCurrenciesIt = masterAccountItCurrenciesIterator.next();
                            if (masterAccountItCurrenciesIt.textValue().equals(currency)) {
                                operatorNames.add(operator);
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MasterAccountNewGetOperatorNameRandom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if(!operatorNames.isEmpty()){
            super.response = operatorNames.get(new Random().nextInt(operatorNames.size()));
            return;
        }
        super.response = "MAIN";
    }
    
}
