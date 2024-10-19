/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashUnlinkPlaceDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
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
public class CashUnlinkPlaceDevice extends AbstractOperation<String> {

    private final CashUnlinkPlaceDeviceRequest cashUnlinkPlaceDeviceRequest;

    public CashUnlinkPlaceDevice(CashUnlinkPlaceDeviceRequest cashUnlinkPlaceDeviceRequest) {
        super(String.class);
        this.cashUnlinkPlaceDeviceRequest = cashUnlinkPlaceDeviceRequest;
    }

    @Override
    public void execute() {
        File cashDeviceFile = new File(CashFolderLocator.getDevicesFolder(), cashUnlinkPlaceDeviceRequest.getDeviceId() + ".json");
        if (!cashDeviceFile.isFile()) {
            super.response = "DEVICE IS NOT LINKED TO ANY CASH PLACE";
            return;
        }
        try {
            JsonNode cashDevice = mapper.readTree(cashDeviceFile);
            String placeId = cashDevice.get("placeId").textValue();
            ((ObjectNode) cashDevice).remove("placeId");
            File cashPlaceFile = new File(CashFolderLocator.getPlaceFolder(placeId), "config.json");
            JsonNode cashPlace = mapper.readTree(cashPlaceFile);
            Iterator<JsonNode> cashPlaceDevicesIterator = cashPlace.get("devices").iterator();
            while (cashPlaceDevicesIterator.hasNext()) {
                JsonNode cashPlaceDevicesIt = cashPlaceDevicesIterator.next();
                if (cashUnlinkPlaceDeviceRequest.getDeviceId().equals(cashPlaceDevicesIt.get("id").textValue())) {
                    cashPlaceDevicesIterator.remove();
                    break;
                }
            }
            FileUtil.editFile(cashDevice, cashDeviceFile);
            FileUtil.editFile(cashPlace, cashPlaceFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(CashUnlinkPlaceDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
