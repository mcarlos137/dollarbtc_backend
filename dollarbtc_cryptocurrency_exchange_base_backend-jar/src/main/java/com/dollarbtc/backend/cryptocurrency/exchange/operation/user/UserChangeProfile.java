/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserChangeProfile extends AbstractOperation<String> {

    private final String userName;
    private final UserProfile userProfile;

    public UserChangeProfile(String userName, UserProfile userProfile) {
        super(String.class);
        this.userName = userName;
        this.userProfile = userProfile;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userName);
        try {
            JsonNode user = mapper.readTree(userFile);
            ((ObjectNode) user).put("type", userProfile.getUserType().name());
            ((ObjectNode) user).put("environment", userProfile.getUserEnvironment().name());
            ((ObjectNode) user).put("operationAccount", userProfile.getUserOperationAccount().name());
            ((ObjectNode) user).remove("production");
            FileUtil.editFile(user, userFile);
            String nickname = user.get("nickname").textValue();
            String referralCode = getReferralCode(userName, nickname);
            if (referralCode == null) {
                super.response = "FAIL";
                return;
            }
            switch (userProfile) {
                case BANKER:
                    File bankerConfigFile = BankersFolderLocator.getConfigFile(userName);
                    if (!bankerConfigFile.isFile()) {
                        JsonNode bankerConfig = mapper.createObjectNode();
                        ((ObjectNode) bankerConfig).put("timestamp", DateUtil.getCurrentDate());
                        ((ObjectNode) bankerConfig).put("userName", userName);
                        ((ObjectNode) bankerConfig).put("nickname", nickname);
                        ((ObjectNode) bankerConfig).put("referralCode", referralCode);
                        FileUtil.createFile(bankerConfig, bankerConfigFile);
                    }
                    break;
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserChangeProfile.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

    private String getReferralCode(String userName, String nickname) {
        File referralCodesFile = BaseFilesLocator.getReferralCodesFile();
        try {
            JsonNode referralCodes = mapper.readTree(referralCodesFile);
            String referralCode = null;
            while (true) {
                referralCode = "B" + (new Random().nextInt(999999 - 100000) + 100000);
                if (!referralCodes.has(referralCode)) {
                    ObjectNode referralCodeValues = mapper.createObjectNode();
                    referralCodeValues.put("active", true);
                    referralCodeValues.put("special", false);
                    referralCodeValues.put("name", nickname);
                    referralCodeValues.put("userName", userName);
                    ((ObjectNode) referralCodes).set(referralCode, referralCodeValues);
                    FileUtil.editFile(referralCodes, referralCodesFile);
                    break;
                }
            }
            return referralCode;
        } catch (IOException ex) {
            Logger.getLogger(UserChangeProfile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
