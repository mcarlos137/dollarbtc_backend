/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa.MFAVerifyCodeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MFAFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersCodesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MFAVerifyCode extends AbstractOperation<String> {
    
    private final MFAVerifyCodeRequest mfaVerifyCodeRequest;

    public MFAVerifyCode(MFAVerifyCodeRequest mfaVerifyCodeRequest) {
        super(String.class);
        this.mfaVerifyCodeRequest = mfaVerifyCodeRequest;
    }
    
    @Override
    protected void execute() {
        File mfaUserFile = MFAFolderLocator.getUserFile(mfaVerifyCodeRequest.getUserName());
        if (!mfaUserFile.isFile()) {
            super.response = "FAIL";
            return;
        }
        String currentTimestamp = DateUtil.getCurrentDate();
        try {
            JsonNode mfaUser = mapper.readTree(mfaUserFile);
            String timestamp = mfaUser.get("timestamp").textValue();
            if (DateUtil.parseDate(timestamp).after(DateUtil.parseDate(DateUtil.getDateMinutesBefore(currentTimestamp, 10)))) {
                if (mfaVerifyCodeRequest.getCode().equals(mfaUser.get("otpCode").textValue())) {
                    if (mfaVerifyCodeRequest.isRegistration()) {
                        FileUtil.moveFileToFolder(mfaUserFile, UsersCodesFolderLocator.getRegistrationFolder());
                        super.response = "OK";
                        return;
                    }
                    FileUtil.deleteFile(mfaUserFile);
                    super.response = "OK";
                    return;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MFAVerifyCode.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }
    
}
