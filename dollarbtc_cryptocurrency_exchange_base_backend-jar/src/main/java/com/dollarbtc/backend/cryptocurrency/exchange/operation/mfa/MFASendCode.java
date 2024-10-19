/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mfa;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mfa.MFASendCodeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailException;
import com.dollarbtc.backend.cryptocurrency.exchange.mail.MailSMTP;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MFAFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MFASendCode extends AbstractOperation<String> {
    
    private final MFASendCodeRequest mfaSendCodeRequest;

    public MFASendCode(MFASendCodeRequest mfaSendCodeRequest) {
        super(String.class);
        this.mfaSendCodeRequest = mfaSendCodeRequest;
    }
    
    @Override
    protected void execute() {
        File userConfigFile = UsersFolderLocator.getConfigFile(mfaSendCodeRequest.getUserName());
        boolean createMFAUserFile = true;
        String currentTimestamp = DateUtil.getCurrentDate();
        File mfaUserFile = MFAFolderLocator.getUserFile(mfaSendCodeRequest.getUserName());
        String otpCode = String.valueOf(otpGenerator(7));
        if (mfaUserFile.isFile()) {
            try {
                JsonNode mfaUser = mapper.readTree(mfaUserFile);
                String timestamp = mfaUser.get("timestamp").textValue();
                if (DateUtil.parseDate(timestamp).after(DateUtil.parseDate(DateUtil.getDateSecondsBefore(currentTimestamp, 25)))) {
                    super.response = "OK";
                    return;
                }
                if (DateUtil.parseDate(timestamp).after(DateUtil.parseDate(DateUtil.getDateMinutesBefore(currentTimestamp, 10)))) {
                    otpCode = mfaUser.get("otpCode").textValue();
                    createMFAUserFile = false;
                } else {
                    FileUtil.deleteFile(mfaUserFile);
                }
            } catch (IOException ex) {
                Logger.getLogger(MFASendCode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String message;
        String subject;
        String appName = "MC";
        switch (mfaSendCodeRequest.getLanguage()) {
            case "ES":
                message = "Tu codigo de verificacion " + appName + " es " + otpCode;
                subject = "Código de verificación " + appName;
                break;
            case "EN":
                message = "Your " + appName + " verification code is " + otpCode;
                subject = appName + " verification code";
                break;
            default:
                super.response = "LANGUAGE NOT SUPPORTED";
                return;
        }
        try {
            boolean sended = false;
            if (userConfigFile.isFile()) {
                JsonNode userConfig = mapper.readTree(userConfigFile);
                if (mfaSendCodeRequest.isSendSms() && userConfig.has("phone") && !userConfig.get("phone").textValue().equals("")) {
                    new SMSSender().publish(message, new String[]{userConfig.get("phone").textValue()});
                    sended = true;
                }
                if (mfaSendCodeRequest.isSendMail() && userConfig.has("email") && !userConfig.get("email").textValue().equals("")) {
                    Set<String> recipients = new HashSet<>();
                    recipients.add(userConfig.get("email").textValue());
                    try {
                        new MailSMTP("AWS", "AKIAJETL4OMCAJOB4T4Q", "AtHUVh6lyqCfMzkg8Tfaj4yaYrWSSwrbjC8JSRJ2d7bQ").send("admin@dollarbtc.com__DOLLARBTC", subject, message, recipients, null);
                        sended = true;
                    } catch (MailException ex) {
                        Logger.getLogger(MFASendCode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (!sended) {
                if (mfaSendCodeRequest.isMcNewUser()) {
                    new SMSSender().publish(message, new String[]{mfaSendCodeRequest.getUserName()});
                } else {
                    super.response = "THERE IS NO WAY TO SEND CODE";
                    return;
                }
            }
            if (createMFAUserFile) {
                JsonNode mfaUser = mapper.createObjectNode();
                ((ObjectNode) mfaUser).put("timestamp", currentTimestamp);
                ((ObjectNode) mfaUser).put("otpCode", otpCode);
                ((ObjectNode) mfaUser).put("userName", mfaSendCodeRequest.getUserName());
                FileUtil.createFile(mfaUser, MFAFolderLocator.getFolder(), mfaSendCodeRequest.getUserName() + ".json");
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MFASendCode.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }
    
    private static char[] otpGenerator(int length) {
        //Creating object of Random class
        Random random = new Random();
        char[] otp = new char[length];
        for (int i = 0; i < length; i++) {
            otp[i] = (char) (random.nextInt(10) + 48);
        }
        return otp;
    }
    
}
