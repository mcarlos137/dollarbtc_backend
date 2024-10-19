/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cash;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cash.CashLinkPlaceDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CashFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class CashLinkPlaceDevice extends AbstractOperation<String> {

    private final CashLinkPlaceDeviceRequest cashLinkPlaceDeviceRequest;

    public CashLinkPlaceDevice(CashLinkPlaceDeviceRequest cashLinkPlaceDeviceRequest) {
        super(String.class);
        this.cashLinkPlaceDeviceRequest = cashLinkPlaceDeviceRequest;
    }

    @Override
    public void execute() {
        File cashPlaceFolder = CashFolderLocator.getPlaceFolder(cashLinkPlaceDeviceRequest.getPlaceId());
        if (!cashPlaceFolder.isDirectory()) {
            super.response = "CASH PLACE DOES NOT EXIST";
            return;
        }
        File cashPlaceConfigFile = new File(cashPlaceFolder, "config.json");
        JsonNode cashPlaceConfig = null;
        try {
            cashPlaceConfig = mapper.readTree(cashPlaceConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(CashLinkPlaceDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (cashPlaceConfig == null) {
            super.response = "CASH PLACE DOES NOT EXIST";
            return;
        }
        if (!cashPlaceConfig.has("devices")) {
            ((ObjectNode) cashPlaceConfig).set("devices", mapper.createArrayNode());
        }
        File cashDeviceFile = new File(CashFolderLocator.getDevicesFolder(), cashLinkPlaceDeviceRequest.getDeviceId() + ".json");
        JsonNode cashDevice = mapper.createObjectNode();
        if (!cashDeviceFile.isFile()) {
            ((ObjectNode) cashDevice).put("deviceId", cashLinkPlaceDeviceRequest.getDeviceId());
            ((ObjectNode) cashDevice).put("placeId", cashLinkPlaceDeviceRequest.getPlaceId());
            ((ObjectNode) cashDevice).put("type", cashLinkPlaceDeviceRequest.getType());
            if (cashLinkPlaceDeviceRequest.getDeviceName() != null && !cashLinkPlaceDeviceRequest.getDeviceName().equals("")) {
                ((ObjectNode) cashDevice).put("deviceName", cashLinkPlaceDeviceRequest.getDeviceName());
            }
            if (cashLinkPlaceDeviceRequest.getDeviceModel() != null && !cashLinkPlaceDeviceRequest.getDeviceModel().equals("")) {
                ((ObjectNode) cashDevice).put("deviceModel", cashLinkPlaceDeviceRequest.getDeviceModel());
            }
            FileUtil.createFile(cashDevice, cashDeviceFile);
            JsonNode device = mapper.createObjectNode();
            ((ObjectNode) device).put("id", cashLinkPlaceDeviceRequest.getDeviceId());
            if (cashLinkPlaceDeviceRequest.getDeviceName() != null && !cashLinkPlaceDeviceRequest.getDeviceName().equals("")) {
                ((ObjectNode) device).put("name", cashLinkPlaceDeviceRequest.getDeviceName());
            }
            if (cashLinkPlaceDeviceRequest.getDeviceModel() != null && !cashLinkPlaceDeviceRequest.getDeviceModel().equals("")) {
                ((ObjectNode) device).put("model", cashLinkPlaceDeviceRequest.getDeviceModel());
            }
            ((ObjectNode) device).put("active", true);
            ((ArrayNode) cashPlaceConfig.get("devices")).add(device);
            FileUtil.editFile(cashPlaceConfig, cashPlaceConfigFile);
            super.response = "OK";
            return;
        } else {
            try {
                cashDevice = mapper.readTree(cashDeviceFile);
                if (cashDevice.has("placeId")) {
                    String placeId = cashDevice.get("placeId").textValue();
                    if (cashLinkPlaceDeviceRequest.getPlaceId().equals(placeId)) {
                        JsonNode device = mapper.createObjectNode();
                        ((ObjectNode) device).put("id", cashLinkPlaceDeviceRequest.getDeviceId());
                        if (cashLinkPlaceDeviceRequest.getDeviceName() != null && !cashLinkPlaceDeviceRequest.getDeviceName().equals("")) {
                            ((ObjectNode) device).put("name", cashLinkPlaceDeviceRequest.getDeviceName());
                        }
                        if (cashLinkPlaceDeviceRequest.getDeviceModel() != null && !cashLinkPlaceDeviceRequest.getDeviceModel().equals("")) {
                            ((ObjectNode) device).put("model", cashLinkPlaceDeviceRequest.getDeviceModel());
                        }
                        ((ObjectNode) device).put("active", true);
                        ((ArrayNode) cashPlaceConfig.get("devices")).add(device);
                        FileUtil.editFile(cashPlaceConfig, cashPlaceConfigFile);
                        super.response = "OK";
                        return;
                    } else {
                        super.response = "DEVICE IS LINKED TO OTHER CASH PLACE WITH ID " + placeId + " YOU MUST UNLINK FIRST";
                        return;
                    }
                } else {
                    ((ObjectNode) cashDevice).put("placeId", cashLinkPlaceDeviceRequest.getPlaceId());
                    FileUtil.editFile(cashDevice, cashDeviceFile);
                    super.response = "OK";
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(CashLinkPlaceDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = "FAIL";
    }

}
