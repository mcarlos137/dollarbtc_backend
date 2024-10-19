/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewAddCurrencyOperationTypeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCRetailNewAddCurrencyOperationType extends AbstractOperation<String> {

    private final MCRetailNewAddCurrencyOperationTypeRequest mcRetailNewAddCurrencyOperationTypeRequest;

    public MCRetailNewAddCurrencyOperationType(MCRetailNewAddCurrencyOperationTypeRequest mcRetailNewAddCurrencyOperationTypeRequest) {
        super(String.class);
        this.mcRetailNewAddCurrencyOperationTypeRequest = mcRetailNewAddCurrencyOperationTypeRequest;
    }

    @Override
    public void execute() {
        File moneyclickRetailConfigFile = MoneyclickFolderLocator.getRetailConfigFile(mcRetailNewAddCurrencyOperationTypeRequest.getId());
        if (!moneyclickRetailConfigFile.isFile()) {
            super.response = "RETAIL ID DOES NOT EXIST";
            return;
        }
        try {
            JsonNode moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
            ArrayNode moneyclickRetailConfigCurrencies = (ArrayNode) moneyclickRetailConfig.get("currencies");
            boolean add = true;
            Iterator<JsonNode> moneyclickRetailConfigCurrenciesIterator = moneyclickRetailConfigCurrencies.iterator();
            while (moneyclickRetailConfigCurrenciesIterator.hasNext()) {
                JsonNode moneyclickRetailConfigCurrenciesIt = moneyclickRetailConfigCurrenciesIterator.next();
                if (moneyclickRetailConfigCurrenciesIt.textValue().equals(mcRetailNewAddCurrencyOperationTypeRequest.getCurrency())) {
                    add = false;
                }
            }
            if (add) {
                moneyclickRetailConfigCurrencies.add(mcRetailNewAddCurrencyOperationTypeRequest.getCurrency());
            }
            ArrayNode moneyclickRetailConfigOperations = (ArrayNode) moneyclickRetailConfig.get("operations");
            add = true;
            Iterator<JsonNode> moneyclickRetailConfigOperationsIterator = moneyclickRetailConfigOperations.iterator();
            while (moneyclickRetailConfigOperationsIterator.hasNext()) {
                JsonNode moneyclickRetailConfigOperationsIt = moneyclickRetailConfigOperationsIterator.next();
                if (moneyclickRetailConfigOperationsIt.get("currency").textValue().equals(mcRetailNewAddCurrencyOperationTypeRequest.getCurrency())
                        && MCRetailOperationType.valueOf(moneyclickRetailConfigOperationsIt.get("type").textValue()).equals(mcRetailNewAddCurrencyOperationTypeRequest.getMcRetailOperationType())) {
                    add = false;
                }
            }
            JsonNode moneyclickRetailConfigOperation = mapper.createObjectNode();
            ((ObjectNode) moneyclickRetailConfigOperation).put("currency", mcRetailNewAddCurrencyOperationTypeRequest.getCurrency());
            ((ObjectNode) moneyclickRetailConfigOperation).put("type", mcRetailNewAddCurrencyOperationTypeRequest.getMcRetailOperationType().name());
            if (add) {
                moneyclickRetailConfigOperations.add(moneyclickRetailConfigOperation);
            }
            ArrayNode moneyclickRetailConfigEscrowLimits = (ArrayNode) moneyclickRetailConfig.get("escrowLimits");
            add = true;
            Iterator<JsonNode> moneyclickRetailConfigEscrowLimitsIterator = moneyclickRetailConfigEscrowLimits.iterator();
            while (moneyclickRetailConfigEscrowLimitsIterator.hasNext()) {
                JsonNode moneyclickRetailConfigEscrowLimitsIt = moneyclickRetailConfigEscrowLimitsIterator.next();
                if (moneyclickRetailConfigEscrowLimitsIt.get("currency").textValue().equals(mcRetailNewAddCurrencyOperationTypeRequest.getCurrency())) {
                    add = false;
                }
            }
            JsonNode moneyclickRetailConfigEscrowLimit = mapper.createObjectNode();
            ((ObjectNode) moneyclickRetailConfigEscrowLimit).put("currency", mcRetailNewAddCurrencyOperationTypeRequest.getCurrency());
            try {
                File moneyclickRetailEscrowLimitsFile = MoneyclickFolderLocator.getRetailEscrowLimitsFile();
                JsonNode moneyclickRetailEscrowLimits = new ObjectMapper().readTree(moneyclickRetailEscrowLimitsFile);
                if (moneyclickRetailEscrowLimits.has(mcRetailNewAddCurrencyOperationTypeRequest.getCurrency())) {
                    ((ObjectNode) moneyclickRetailConfigEscrowLimit).put("amount", moneyclickRetailEscrowLimits.get(mcRetailNewAddCurrencyOperationTypeRequest.getCurrency()).doubleValue());
                } else {
                    super.response = "FAIL";
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(MCRetailNewAddCurrencyOperationType.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (add) {
                moneyclickRetailConfigEscrowLimits.add(moneyclickRetailConfigEscrowLimit);
            }
            FileUtil.editFile(moneyclickRetailConfig, moneyclickRetailConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewAddCurrencyOperationType.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
