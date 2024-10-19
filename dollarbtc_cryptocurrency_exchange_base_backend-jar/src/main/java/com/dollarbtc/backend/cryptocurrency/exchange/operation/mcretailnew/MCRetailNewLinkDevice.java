/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewLinkDeviceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
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
public class MCRetailNewLinkDevice extends AbstractOperation<String> {

    private final MCRetailNewLinkDeviceRequest mcRetailNewLinkDeviceRequest;

    public MCRetailNewLinkDevice(MCRetailNewLinkDeviceRequest mcRetailNewLinkDeviceRequest) {
        super(String.class);
        this.mcRetailNewLinkDeviceRequest = mcRetailNewLinkDeviceRequest;
    }

    @Override
    public void execute() {
        File moneyclickRetailFolder = MoneyclickFolderLocator.getRetailFolder(mcRetailNewLinkDeviceRequest.getRetailId());
        if (!moneyclickRetailFolder.isDirectory()) {
            super.response = "RETAIL DOES NOT EXIST";
            return;
        }
        File moneyclickRetailConfigFile = new File(moneyclickRetailFolder, "config.json");
        JsonNode moneyclickRetailConfig = null;
        try {
            moneyclickRetailConfig = mapper.readTree(moneyclickRetailConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewLinkDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (moneyclickRetailConfig == null) {
            super.response = "RETAIL DOES NOT EXIST";
            return;
        }
        if (!moneyclickRetailConfig.has("devices")) {
            ((ObjectNode) moneyclickRetailConfig).set("devices", mapper.createArrayNode());
        }
        File moneyclickDeviceFile = new File(MoneyclickFolderLocator.getDevicesFolder(), mcRetailNewLinkDeviceRequest.getDeviceId() + ".json");
        JsonNode moneyclickDevice = mapper.createObjectNode();
        if (!moneyclickDeviceFile.isFile()) {
            ((ObjectNode) moneyclickDevice).put("deviceId", mcRetailNewLinkDeviceRequest.getDeviceId());
            ((ObjectNode) moneyclickDevice).put("retailId", mcRetailNewLinkDeviceRequest.getRetailId());
            ((ObjectNode) moneyclickDevice).put("type", mcRetailNewLinkDeviceRequest.getType());
            if (mcRetailNewLinkDeviceRequest.getDeviceName() != null && !mcRetailNewLinkDeviceRequest.getDeviceName().equals("")) {
                ((ObjectNode) moneyclickDevice).put("deviceName", mcRetailNewLinkDeviceRequest.getDeviceName());
            }
            if (mcRetailNewLinkDeviceRequest.getDeviceModel() != null && !mcRetailNewLinkDeviceRequest.getDeviceModel().equals("")) {
                ((ObjectNode) moneyclickDevice).put("deviceModel", mcRetailNewLinkDeviceRequest.getDeviceModel());
            }
            FileUtil.createFile(moneyclickDevice, moneyclickDeviceFile);
            JsonNode device = mapper.createObjectNode();
            ((ObjectNode) device).put("id", mcRetailNewLinkDeviceRequest.getDeviceId());
            if (mcRetailNewLinkDeviceRequest.getDeviceName() != null && !mcRetailNewLinkDeviceRequest.getDeviceName().equals("")) {
                ((ObjectNode) device).put("name", mcRetailNewLinkDeviceRequest.getDeviceName());
            }
            if (mcRetailNewLinkDeviceRequest.getDeviceModel() != null && !mcRetailNewLinkDeviceRequest.getDeviceModel().equals("")) {
                ((ObjectNode) device).put("model", mcRetailNewLinkDeviceRequest.getDeviceModel());
            }
            ((ObjectNode) device).put("active", true);
            ((ArrayNode) moneyclickRetailConfig.get("devices")).add(device);
            FileUtil.editFile(moneyclickRetailConfig, moneyclickRetailConfigFile);
            super.response = "OK";
            return;
        } else {
            try {
                moneyclickDevice = mapper.readTree(moneyclickDeviceFile);
                if (moneyclickDevice.has("retailId")) {
                    String retailId = moneyclickDevice.get("retailId").textValue();
                    if (mcRetailNewLinkDeviceRequest.getRetailId().equals(retailId)) {
                        JsonNode device = mapper.createObjectNode();
                        ((ObjectNode) device).put("id", mcRetailNewLinkDeviceRequest.getDeviceId());
                        if (mcRetailNewLinkDeviceRequest.getDeviceName() != null && !mcRetailNewLinkDeviceRequest.getDeviceName().equals("")) {
                            ((ObjectNode) device).put("name", mcRetailNewLinkDeviceRequest.getDeviceName());
                        }
                        if (mcRetailNewLinkDeviceRequest.getDeviceModel() != null && !mcRetailNewLinkDeviceRequest.getDeviceModel().equals("")) {
                            ((ObjectNode) device).put("model", mcRetailNewLinkDeviceRequest.getDeviceModel());
                        }
                        ((ObjectNode) device).put("active", true);
                        ((ArrayNode) moneyclickRetailConfig.get("devices")).add(device);
                        FileUtil.editFile(moneyclickRetailConfig, moneyclickRetailConfigFile);
                        super.response = "OK";
                        return;
                    } else {
                        super.response = "DEVICE IS LINKED TO OTHER RETAIL WITH ID " + retailId + " YOU MUST UNLINK FIRST";
                        return;
                    }
                } else {
                    ((ObjectNode) moneyclickDevice).put("retailId", mcRetailNewLinkDeviceRequest.getRetailId());
                    FileUtil.editFile(moneyclickDevice, moneyclickDeviceFile);
                    super.response = "OK";
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(MCRetailNewLinkDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = "FAIL";
    }

}
