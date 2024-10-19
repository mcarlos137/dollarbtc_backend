/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserAddFlagRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFlagsFolderLocator;
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
public class UserAddFlag extends AbstractOperation<String> {

    private final UserAddFlagRequest userAddFlagRequest;

    public UserAddFlag(UserAddFlagRequest userAddFlagRequest) {
        super(String.class);
        this.userAddFlagRequest = userAddFlagRequest;
    }

    @Override
    protected void execute() {
        try {
            String timestamp = DateUtil.getCurrentDate();
            ObjectNode flag = mapper.createObjectNode();
            flag.put("timestamp", timestamp);
            flag.put("operatorUserName", userAddFlagRequest.getOperatorUserName());
            flag.put("color", userAddFlagRequest.getFlagColor());
            File userConfigFile = UsersFolderLocator.getConfigFile(userAddFlagRequest.getUserName());
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (userConfig.has("flag")) {
                String flagColor = userConfig.get("flag").get("color").textValue();
                if (flagColor.equals("BLUE")) {
                    super.response = "OK";
                    return;
                }
                if (flagColor.equals("BLACK") && userAddFlagRequest.getFlagColor().equals("BLACK")) {
                    super.response = "OK";
                    return;
                }
                if (flagColor.equals("RED") && (userAddFlagRequest.getFlagColor().equals("RED") || userAddFlagRequest.getFlagColor().equals("YELLOW") || userAddFlagRequest.getFlagColor().equals("ORANGE") || userAddFlagRequest.getFlagColor().equals("PURPLE"))) {
                    super.response = "OK";
                    return;
                }
                if (flagColor.equals("ORANGE") && (userAddFlagRequest.getFlagColor().equals("ORANGE") || userAddFlagRequest.getFlagColor().equals("YELLOW") || userAddFlagRequest.getFlagColor().equals("PURPLE"))) {
                    super.response = "OK";
                    return;
                }
                if (flagColor.equals("GREEN") && (userAddFlagRequest.getFlagColor().equals("GREEN") || userAddFlagRequest.getFlagColor().equals("YELLOW"))) {
                    super.response = "OK";
                    return;
                }
                if (flagColor.equals("YELLOW") && userAddFlagRequest.getFlagColor().equals("YELLOW")) {
                    super.response = "OK";
                    return;
                }
                if (flagColor.equals("PURPLE") && userAddFlagRequest.getFlagColor().equals("PURPLE")) {
                    super.response = "OK";
                    return;
                }
            }
            ((ObjectNode) userConfig).set("flag", flag);
            if(userAddFlagRequest.getFlagColor().equals("BLACK")){
                ((ObjectNode) userConfig).put("active", false);
            }
            File usersFlagsColorFile = UsersFlagsFolderLocator.getColorFile(userAddFlagRequest.getFlagColor());
            JsonNode usersFlagsColor = mapper.readTree(usersFlagsColorFile);
            if (!usersFlagsColor.has(userAddFlagRequest.getUserName())) {
                ((ObjectNode) usersFlagsColor).set(userAddFlagRequest.getUserName(), mapper.createObjectNode());
            }
            ((ObjectNode) usersFlagsColor.get(userAddFlagRequest.getUserName())).put(timestamp, userAddFlagRequest.getOperatorUserName());
            FileUtil.editFile(userConfig, userConfigFile);
            FileUtil.editFile(usersFlagsColor, usersFlagsColorFile);
            for (File usersFlagsColorFilee : UsersFlagsFolderLocator.getFolder().listFiles()) {
                if (!usersFlagsColorFilee.isFile() || usersFlagsColorFilee.getName().equals(userAddFlagRequest.getFlagColor() + ".json")) {
                    continue;
                }
                usersFlagsColor = mapper.readTree(usersFlagsColorFilee);
                if (usersFlagsColor.has(userAddFlagRequest.getUserName())) {
                    ((ObjectNode) usersFlagsColor).remove(userAddFlagRequest.getUserName());
                }
                FileUtil.editFile(usersFlagsColor, usersFlagsColorFilee);
            }
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserAddFlag.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
