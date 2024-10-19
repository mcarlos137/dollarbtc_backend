/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin.OTCAdminVerificationRequest;
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
public class OTCAdminVerification extends AbstractOperation<String> {

    private final OTCAdminVerificationRequest otcAdminVerificationRequest;

    public OTCAdminVerification(OTCAdminVerificationRequest otcAdminVerificationRequest) {
        super(String.class);
        this.otcAdminVerificationRequest = otcAdminVerificationRequest;
    }

    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(otcAdminVerificationRequest.getUserName());
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            ((ObjectNode) userConfig).put("allowVerificationType" + otcAdminVerificationRequest.getType(), otcAdminVerificationRequest.isAllow());
            FileUtil.editFile(userConfig, userConfigFile);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCAdminVerification.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
