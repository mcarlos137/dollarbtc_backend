/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
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
public class OTCAdminUpdateFieldValues extends AbstractOperation<String> {

    private final String currency;

    public OTCAdminUpdateFieldValues(String currency) {
        super(String.class);
        this.currency = currency;
    }

    @Override
    protected void execute() {
        File otcCurrencyFolder = OTCFolderLocator.getCurrencyFolder(null, currency);
        if (!otcCurrencyFolder.isDirectory()) {
            super.response = "CURRENCY DOES NOT EXIST";
            return;
        }
        File otcCurrencyFile = OTCFolderLocator.getCurrencyFile(null, currency);
        try {
            JsonNode otcCurrency = mapper.readTree(otcCurrencyFile);
            Iterator<JsonNode> otcCurrencyClientPaymentTypesIterator = otcCurrency.get("clientPaymentTypes").iterator();
            while (otcCurrencyClientPaymentTypesIterator.hasNext()) {
                JsonNode otcCurrencyClientPaymentTypesIt = otcCurrencyClientPaymentTypesIterator.next();
                String otcCurrencyClientPaymentTypesItName = otcCurrencyClientPaymentTypesIt.get("name").textValue();
                Iterator<JsonNode> otcCurrencyClientPaymentTypesItFieldsIterator = otcCurrencyClientPaymentTypesIt.get("fields").iterator();
                while (otcCurrencyClientPaymentTypesItFieldsIterator.hasNext()) {
                    JsonNode otcCurrencyClientPaymentTypesItFieldsIt = otcCurrencyClientPaymentTypesItFieldsIterator.next();
                    String otcCurrencyClientPaymentTypesItFieldsItName = otcCurrencyClientPaymentTypesItFieldsIt.get("name").textValue();
                    File fieldsValuesToUpdateFile = new File(otcCurrencyFolder, otcCurrencyClientPaymentTypesItFieldsItName + "__" + otcCurrencyClientPaymentTypesItName + ".json");
                    if (!fieldsValuesToUpdateFile.isFile()) {
                        continue;
                    }
                    ((ObjectNode) otcCurrencyClientPaymentTypesItFieldsIt).set("values", mapper.readTree(fieldsValuesToUpdateFile));
                }
            }
            FileUtil.editFile(otcCurrency, otcCurrencyFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminUpdateFieldValues.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
