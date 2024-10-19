/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminEditAdminUserCommissionsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminEditAdminUserCommissions extends AbstractOperation<String> {

    private final OTCAdminEditAdminUserCommissionsRequest otcAdminEditAdminUserCommissionsRequest;

    public OTCAdminEditAdminUserCommissions(OTCAdminEditAdminUserCommissionsRequest otcAdminEditAdminUserCommissionsRequest) {
        super(String.class);
        this.otcAdminEditAdminUserCommissionsRequest = otcAdminEditAdminUserCommissionsRequest;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(otcAdminEditAdminUserCommissionsRequest.getUserName());
        if (!userConfigFile.isFile()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (!userConfig.has("type") || !userConfig.get("type").textValue().equals("ADMIN")) {
                super.response = "USER IS NOT ADMIN";
                return;
            }
            if (!userConfig.has("paymentCommissions")) {
                ((ObjectNode) userConfig).set("paymentCommissions", mapper.createObjectNode());
            }
            if (!userConfig.get("paymentCommissions").has(otcAdminEditAdminUserCommissionsRequest.getCurrency())) {
                ((ObjectNode) userConfig.get("paymentCommissions")).set(otcAdminEditAdminUserCommissionsRequest.getCurrency(), mapper.createObjectNode());
            }
            ((ObjectNode) userConfig.get("paymentCommissions").get(otcAdminEditAdminUserCommissionsRequest.getCurrency())).put("mcBuyBalancePercent", otcAdminEditAdminUserCommissionsRequest.getMcBuyBalancePercent());
            ((ObjectNode) userConfig.get("paymentCommissions").get(otcAdminEditAdminUserCommissionsRequest.getCurrency())).put("sendToPaymentPercent", otcAdminEditAdminUserCommissionsRequest.getSendToPaymentPercent());
            FileUtil.editFile(userConfig, userConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminEditAdminUserCommissions.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
