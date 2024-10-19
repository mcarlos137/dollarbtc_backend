/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretail;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretail.MCRetailUnlinkDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailUnlinkDevice extends AbstractOperation<String> {

    private final MCRetailUnlinkDeviceRequest mcRetailUnlinkDeviceRequest;

    public MCRetailUnlinkDevice(MCRetailUnlinkDeviceRequest mcRetailUnlinkDeviceRequest) {
        super(String.class);
        this.mcRetailUnlinkDeviceRequest = mcRetailUnlinkDeviceRequest;
    }

    @Override
    public void execute() {
        File moneyclickDeviceFile = new File(MoneyclickFolderLocator.getDevicesFolder(), mcRetailUnlinkDeviceRequest.getDeviceId() + ".json");
        if (!moneyclickDeviceFile.isFile()) {
            super.response = "DEVICE IS NOT LINKED TO ANY RETAIL";
            return;
        }
        try {
            JsonNode moneyclickDevice = mapper.readTree(moneyclickDeviceFile);
            String retailId = moneyclickDevice.get("retailId").textValue();
            ((ObjectNode) moneyclickDevice).remove("retailId");
            File moneyclickRetailFile = new File(MoneyclickFolderLocator.getRetailFolder(retailId), "config.json");
            JsonNode moneyclickRetail = mapper.readTree(moneyclickRetailFile);
            Iterator<JsonNode> moneyclickRetailDevicesIterator = moneyclickRetail.get("devices").iterator();
            while (moneyclickRetailDevicesIterator.hasNext()) {
                JsonNode moneyclickRetailDevicesIt = moneyclickRetailDevicesIterator.next();
                if (mcRetailUnlinkDeviceRequest.getDeviceId().equals(moneyclickRetailDevicesIt.get("id").textValue())) {
                    moneyclickRetailDevicesIterator.remove();
                    break;
                }
            }
            FileUtil.editFile(moneyclickDevice, moneyclickDeviceFile);
            FileUtil.editFile(moneyclickRetail, moneyclickRetailFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCRetailUnlinkDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
