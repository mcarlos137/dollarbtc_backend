/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashCheckDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashCheckDevice extends AbstractOperation<String> {
    
    private final CashCheckDeviceRequest cashCheckDeviceRequest;

    public CashCheckDevice(CashCheckDeviceRequest cashCheckDeviceRequest) {
        super(String.class);
        this.cashCheckDeviceRequest = cashCheckDeviceRequest;
    }
    
    @Override
    public void execute() {
        File cashDevicesFolder = CashFolderLocator.getDevicesFolder();
        File cashDeviceFile = new File(cashDevicesFolder, cashCheckDeviceRequest.getDeviceId() + ".json");
        if (!cashDeviceFile.isFile()) {
            super.response = "DEVICE IS NOT LINKED TO ANY CASH PLACE";
            return;
        }
        try {
            JsonNode cashDevice = mapper.readTree(cashDeviceFile);
            if(!cashDevice.has("placeId")){
                super.response = "DEVICE IS NOT LINKED TO ANY CASH PLACE";
                return;
            }
            String placeId = cashDevice.get("placeId").textValue();
            if (cashCheckDeviceRequest.getPlaceId().equals(placeId)) {
                super.response = "OK";
                return;
            } else {
                super.response = "DEVICE IS LINKED TO OTHER CASH PLACE WITH ID " + placeId;
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(CashCheckDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }
    
}
