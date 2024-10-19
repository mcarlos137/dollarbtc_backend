/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserSendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.ReceiveAuthorizationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserOperationAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserType;
import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersReceiveAuthorizationsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author carlosmolina
 */
public class MCUserSend extends AbstractOperation<String> {

    private final MCUserSendRequest mcUserSendRequest;

    public MCUserSend(MCUserSendRequest mcUserSendRequest) {
        super(String.class);
        this.mcUserSendRequest = mcUserSendRequest;
    }

    @Override
    public void execute() {
        Double btcPrice = null;
        BalanceOperationType balanceOperationType = BalanceOperationType.MC_SEND_SMS_NATIONAL;
        if (mcUserSendRequest.isInternational()) {
            balanceOperationType = BalanceOperationType.MC_SEND_SMS_INTERNATIONAL;
        }
        if (mcUserSendRequest.getReceiveAuthorizationId() != null) {
            File usersReceiveAuthorizationsUserNameFile = UsersReceiveAuthorizationsFolderLocator.getUserNameFile(mcUserSendRequest.getTargetUserName());
            if (usersReceiveAuthorizationsUserNameFile.isFile()) {
                try {
                    JsonNode usersReceiveAuthorizationsUserName = mapper.readTree(usersReceiveAuthorizationsUserNameFile);
                    if (!usersReceiveAuthorizationsUserName.has(mcUserSendRequest.getReceiveAuthorizationId())) {
                        ((ObjectNode) usersReceiveAuthorizationsUserName).put(mcUserSendRequest.getReceiveAuthorizationId(), ReceiveAuthorizationStatus.PENDING.toString());
                        FileUtil.editFile(usersReceiveAuthorizationsUserName, usersReceiveAuthorizationsUserNameFile);
                    } else if (ReceiveAuthorizationStatus.valueOf(usersReceiveAuthorizationsUserName.get(mcUserSendRequest.getReceiveAuthorizationId()).textValue()).equals(ReceiveAuthorizationStatus.REJECTED)) {
                        super.response = "AUTHORIZATION REJECTED";
                        return;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MCUserSend.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JsonNode usersReceiveAuthorizationsUserName = mapper.createObjectNode();
                ((ObjectNode) usersReceiveAuthorizationsUserName).put(mcUserSendRequest.getReceiveAuthorizationId(), ReceiveAuthorizationStatus.PENDING.toString());
                FileUtil.createFile(usersReceiveAuthorizationsUserName, usersReceiveAuthorizationsUserNameFile);
            }
        }
        String inLimits = BaseOperation.inLimits(mcUserSendRequest.getBaseUserName(), mcUserSendRequest.getCurrency(), mcUserSendRequest.getAmount(), balanceOperationType);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        JsonNode charges = BaseOperation.getChargesNew(mcUserSendRequest.getCurrency(), mcUserSendRequest.getAmount(), balanceOperationType, null, "MONEYCLICK", null, null);
        String baseFullName = mcUserSendRequest.getBaseUserName();
        String targetFullName = mcUserSendRequest.getTargetUserName();
        if (mcUserSendRequest.getBaseName() != null && !mcUserSendRequest.getBaseName().equals("")) {
            baseFullName = mcUserSendRequest.getBaseName() + " - " + mcUserSendRequest.getBaseUserName();
        }
        if (mcUserSendRequest.getTargetName() != null && !mcUserSendRequest.getTargetName().equals("")) {
            targetFullName = mcUserSendRequest.getTargetName() + " - " + mcUserSendRequest.getTargetUserName();
        }
        ObjectNode additionals = new ObjectMapper().createObjectNode();
        String operationId = BaseOperation.getId().substring(0, 7);
        additionals.put("operationId", operationId);
        String substractToBalance = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserSendRequest.getBaseUserName()),
                mcUserSendRequest.getCurrency(),
                mcUserSendRequest.getAmount(),
                balanceOperationType,
                BalanceOperationStatus.OK,
                "SEND TO " + targetFullName + " DESCRIPTION " + mcUserSendRequest.getDescription(),
                null,
                false,
                charges,
                true,
                additionals
        );
        if (!substractToBalance.contains("OK")) {
            super.response = substractToBalance;
            return;
        }
        File targetUserFolder = UsersFolderLocator.getFolder(mcUserSendRequest.getTargetUserName());
        if (!targetUserFolder.isDirectory()) {
            createFromMCSend(mcUserSendRequest.getTargetUserName());
        }
        File targetMCUserBalanceFolder = new File(targetUserFolder, "MCBalance");
        if (!targetMCUserBalanceFolder.isDirectory()) {
            targetMCUserBalanceFolder = UsersFolderLocator.getMCBalanceFolder(mcUserSendRequest.getTargetUserName());
        }
        additionals.put("senderUserName", mcUserSendRequest.getBaseUserName());
        additionals.put("receiveAuthorizationId", mcUserSendRequest.getReceiveAuthorizationId());
        additionals.put("senderBalanceFile", substractToBalance.replace("OK____", ""));
        if (mcUserSendRequest.getClientId() != null) {
            additionals.put("clientId", mcUserSendRequest.getClientId());
        }
        BaseOperation.addToBalance(
                targetMCUserBalanceFolder,
                mcUserSendRequest.getCurrency(),
                mcUserSendRequest.getAmount(),
                balanceOperationType,
                BalanceOperationStatus.OK,
                "RECEIVE FROM " + baseFullName,
                btcPrice,
                null,
                false,
                additionals
        );
        File userConfigFile = UsersFolderLocator.getConfigFile(mcUserSendRequest.getTargetUserName());
        String message = null;
        String targetPhone = mcUserSendRequest.getTargetUserName();
        String targetName = mcUserSendRequest.getTargetName();
        String baseName = mcUserSendRequest.getBaseName();
        if (baseName == null || baseName.equals("")) {
            if (mcUserSendRequest.getBaseUserName().contains("@")) {
                baseName = mcUserSendRequest.getBaseUserName().replace(mcUserSendRequest.getBaseUserName().substring(mcUserSendRequest.getBaseUserName().indexOf("@")), "");
            } else {
                baseName = mcUserSendRequest.getBaseUserName();
            }
        }
        String appName = "MC";
        boolean sendDownloadMessages = false;
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (!userConfig.get("active").booleanValue()) {
                message = "Hello " + targetName + ", " + baseName + " has sent you money. Please download " + appName + " app to receive it.";
                sendDownloadMessages = true;
            } else {
                targetPhone = null;
                if (StringUtils.isNumeric(mcUserSendRequest.getTargetUserName())) {
                    targetPhone = mcUserSendRequest.getTargetUserName();
                }
                if (userConfig.has("phone")) {
                    targetPhone = userConfig.get("phone").textValue();
                }
                if (userConfig.has("firstName") && !userConfig.get("firstName").textValue().equals("")) {
                    targetName = userConfig.get("firstName").textValue();
                }
                if (targetName == null || targetName.equals("")) {
                    targetName = targetPhone;
                }
                message = "Hello " + targetName + ", " + baseName + " has sent you " + mcUserSendRequest.getAmount() + " " + mcUserSendRequest.getCurrency() + ". To see your balance, please go to " + appName + " app.";
            }
        } catch (IOException ex) {
            Logger.getLogger(MCUserSend.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (message != null && targetPhone != null && !targetPhone.equals("")) {
            new SMSSender().publish(message, new String[]{targetPhone});
            if (sendDownloadMessages) {
                new SMSSender().publish("Download " + appName + " from Google Play at https://play.google.com/store/apps/details?id=com.dollarbtc.moneyclick&hl=es or from Apple Store at https://apps.apple.com/us/app/moneyclick/id1501864260", new String[]{targetPhone});
//                new SMSSender().publish("Download MoneyClick from Apple Store at ", new String[]{targetPhone});
            }
        }
        super.response = "OK";
    }

    private static String createFromMCSend(String userName) {
        File userFolder = FileUtil.createFolderIfNoExist(UsersFolderLocator.getFolder(userName));
        FileUtil.createFolderIfNoExist(userFolder, "Balance");
        FileUtil.createFolderIfNoExist(userFolder, "MCBalance");
        FileUtil.createFolderIfNoExist(userFolder, "Models");
        File userFile = new File(userFolder, "config.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode user = mapper.createObjectNode();
        ((ObjectNode) user).put("name", userName);
        ((ObjectNode) user).put("active", false);
        ((ObjectNode) user).put("createdFromMCSend", true);
        ((ObjectNode) user).put("type", UserType.NORMAL.name());
        ((ObjectNode) user).put("environment", UserEnvironment.NONE.name());
        ((ObjectNode) user).put("operationAccount", UserOperationAccount.SELF.name());
        FileUtil.createFile(user, userFile);
        return "OK";
    }

}
