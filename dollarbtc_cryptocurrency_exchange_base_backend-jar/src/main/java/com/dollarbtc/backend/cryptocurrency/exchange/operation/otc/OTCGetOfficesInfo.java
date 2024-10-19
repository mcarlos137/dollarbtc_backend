/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetOfficesInfo extends AbstractOperation<ArrayNode> {

    private final String currency, officesInfoId;

    public OTCGetOfficesInfo(String currency, String officesInfoId) {
        super(ArrayNode.class);
        this.currency = currency;
        this.officesInfoId = officesInfoId;
    }

    @Override
    public void execute() {
        ArrayNode officesInfo = mapper.createArrayNode();
        File otcCurrencyOfficesInfoFile = OTCFolderLocator.getCurrencyOfficesInfoFile(null, currency, officesInfoId);
        if (!otcCurrencyOfficesInfoFile.isFile()) {
            super.response = officesInfo;
            return;
        }
        try {
            super.response = (ArrayNode) mapper.readTree(otcCurrencyOfficesInfoFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCGetOfficesInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = officesInfo;
    }

}
