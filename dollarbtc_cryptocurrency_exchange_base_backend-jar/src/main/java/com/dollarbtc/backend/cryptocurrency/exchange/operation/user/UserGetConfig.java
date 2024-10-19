/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserStartVerificationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author carlosmolina
 */
public class UserGetConfig extends AbstractOperation<JsonNode> {

    private final static String SECURITY_CODE = "TodoAquiYaaa";
    private final String userName, type, securityCode;

    public UserGetConfig(String userName, String securityCode) {
        super(JsonNode.class);
        this.userName = userName;
        this.type = null;
        this.securityCode = securityCode;
    }
    
    public UserGetConfig(String userName, String type, String securityCode) {
        super(JsonNode.class);
        this.userName = userName;
        this.type = type;
        this.securityCode = securityCode;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userName);
        if(type != null){
            userFile = UsersFolderLocator.getConfigFile(userName, type);
        }
        if (!userFile.isFile()) {
            createConfigFile(userName);
            ObjectNode additionals = mapper.createObjectNode();
            additionals.put("test", true);
//            BaseOperation.addToBalance(
//                    UsersFolderLocator.getMCBalanceFolder(userName),
//                    "USD",
//                    50.0,
//                    BalanceOperationType.INITIAL_MOVEMENT,
//                    BalanceOperationStatus.OK,
//                    "Test Balance",
//                    null,
//                    null,
//                    false,
//                    additionals
//            );
        }
        JsonNode user = mapper.createObjectNode();
        try {
            user = mapper.readTree(userFile);
        } catch (IOException ex) {
            Logger.getLogger(UserGetConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user.has("email") && (user.get("email").textValue() == null || user.get("email").textValue().equals(""))) {
            ((ObjectNode) user).remove("email");
            FileUtil.editFile(user, userFile);
        }
        if (user.has("name") && !user.has("type")) {
            ((ObjectNode) user).put("type", "NORMAL");
            FileUtil.editFile(user, userFile);
        }
        if (!securityCode.equals(SECURITY_CODE) && user.has("privateKey")) {
            ((ObjectNode) user).remove("privateKey");
        }
        if (StringUtils.isNumeric(userName)) {
            if (user.has("name") && (!user.has("phone") || user.get("phone").textValue().equals(""))
                    || user.has("name") && user.has("phone") && !user.get("phone").textValue().equals(userName)) {
                ((ObjectNode) user).put("phone", userName);
                FileUtil.editFile(user, userFile);
            }
            if (!user.has("verification") || !user.get("verification").has("B")) {
                new UserStartVerification(new UserStartVerificationRequest(
                        userName,
                        "Verification of user's telephone number",
                        new String[]{"phone"},
                        UserVerificationType.B)
                ).getResponse();
            }
        }
        String[] walletsTags = new String[]{"wallets", "mcWallets", "mcWalletsEthereum"};
        for (String walletsTag : walletsTags) {
            if (user.has(walletsTag)) {
                JsonNode wallets = user.get(walletsTag);
                if (wallets.has("current")) {
                    Iterator<JsonNode> currentIterator = wallets.get("current").iterator();
                    while (currentIterator.hasNext()) {
                        JsonNode currentIt = currentIterator.next();
                        if (!securityCode.equals(SECURITY_CODE) && currentIt.has("privateKey")) {
                            ((ObjectNode) currentIt).remove("privateKey");
                        }
                    }
                }
                if (wallets.has("old")) {
                    Iterator<JsonNode> currentIterator = wallets.get("old").iterator();
                    while (currentIterator.hasNext()) {
                        JsonNode currentIt = currentIterator.next();
                        if (!securityCode.equals(SECURITY_CODE) && currentIt.has("privateKey")) {
                            ((ObjectNode) currentIt).remove("privateKey");
                        }
                    }
                }
            }
        }
        super.response = user;
    }

    private void createConfigFile(String userName) {
        Logger.getLogger(UserGetConfig.class.getName()).log(Level.INFO, "{0}", userName);
        if (StringUtils.isNumeric(userName) && new UserExistBushidoUser(userName).getResponse()) {
            File userFolder = FileUtil.createFolderIfNoExist(UsersFolderLocator.getFolder(), userName);
            FileUtil.createFolderIfNoExist(userFolder, "MCBalance");
            FileUtil.createFolderIfNoExist(userFolder, "Balance");
            FileUtil.createFolderIfNoExist(userFolder, "Models");
            FileUtil.createFolderIfNoExist(userFolder, "OTC");
            JsonNode userConfig = mapper.createObjectNode();
            ((ObjectNode) userConfig).put("name", userName);
            ((ObjectNode) userConfig).put("active", true);
            ((ObjectNode) userConfig).put("type", "NORMAL");
            ((ObjectNode) userConfig).put("environment", "NONE");
            ((ObjectNode) userConfig).put("operationAccount", "SELF");
            ((ObjectNode) userConfig).put("phone", userName);
            FileUtil.createFile(userConfig, new File(userFolder, "config.json"));
        }
    }

}
