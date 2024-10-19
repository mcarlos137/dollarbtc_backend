/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountGetNames extends AbstractOperation<Set> {

    public MasterAccountGetNames() {
        super(Set.class);
    }
    
    @Override
    protected void execute() {
        Set<String> names = new HashSet<>();
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operator = operatorsIt.textValue();
            if (!OPERATOR_NAME.equals("MAIN") && !OPERATOR_NAME.equals(operator)) {
                continue;
            }
            File masterAccountFolder = MasterAccountFolderLocator.getFolder(operator);
            for (File masterAccountSpecificFolder : masterAccountFolder.listFiles()) {
                if (!masterAccountSpecificFolder.isDirectory()) {
                    continue;
                }
                names.add(masterAccountSpecificFolder.getName());
            }
        }
        super.response = names;
    }

}
