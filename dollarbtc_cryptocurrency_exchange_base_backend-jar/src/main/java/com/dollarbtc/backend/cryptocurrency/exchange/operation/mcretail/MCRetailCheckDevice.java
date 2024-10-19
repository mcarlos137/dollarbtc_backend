/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretail.MCRetailCheckDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCRetailCheckDevice extends AbstractOperation<String> {
    
    private final MCRetailCheckDeviceRequest mcRetailCheckDeviceRequest;

    public MCRetailCheckDevice(MCRetailCheckDeviceRequest mcRetailCheckDeviceRequest) {
        super(String.class);
        this.mcRetailCheckDeviceRequest = mcRetailCheckDeviceRequest;
    }
    
    @Override
    public void execute() {
        File moneyclickDevicesFolder = MoneyclickFolderLocator.getDevicesFolder();
        File moneyclickDeviceFile = new File(moneyclickDevicesFolder, mcRetailCheckDeviceRequest.getDeviceId() + ".json");
        if (!moneyclickDeviceFile.isFile()) {
            super.response = "DEVICE IS NOT LINKED TO ANY RETAIL";
            return;
        }
        try {
            JsonNode moneyclickDevice = mapper.readTree(moneyclickDeviceFile);
            if(!moneyclickDevice.has("retailId")){
                super.response = "DEVICE IS NOT LINKED TO ANY RETAIL";
                return;
            }
            String retailId = moneyclickDevice.get("retailId").textValue();
            if (mcRetailCheckDeviceRequest.getRetailId().equals(retailId)) {
                super.response = "OK";
                return;
            } else {
                super.response = "DEVICE IS LINKED TO OTHER RETAIL WITH ID " + retailId;
                return;
            }
        } catch (IOException ex) {
            Logger.getLogger(MCRetailCheckDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }
    
}
