/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetDollarBTCPayments extends AbstractOperation<ArrayNode> {

    private final String currency;

    public OTCGetDollarBTCPayments(String currency) {
        super(ArrayNode.class);
        this.currency = currency;
    }

    @Override
    public void execute() {
        ArrayNode dollarBTCPayments = mapper.createArrayNode();
        Iterator<JsonNode> operatorsIterator = BaseOperation.getOperators().iterator();
        while (operatorsIterator.hasNext()) {
            JsonNode operatorsIt = operatorsIterator.next();
            String operator = operatorsIt.textValue();
            if (!OPERATOR_NAME.equals("MAIN") && !OPERATOR_NAME.equals(operator)) {
                continue;
            }
            File paymentsFolder = OTCFolderLocator.getCurrencyPaymentsFolder(operator, currency);
            if (!paymentsFolder.isDirectory()) {
                continue;
            }
            for (File paymentFolder : paymentsFolder.listFiles()) {
                File paymentFile = new File(paymentFolder, "config.json");
                try {
                    dollarBTCPayments.add(mapper.readTree(paymentFile));
                } catch (IOException ex) {
                    Logger.getLogger(OTCGetDollarBTCPayments.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        super.response = dollarBTCPayments;
    }

}
