/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetReferralCodes;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersCodesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersOperatorFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserCreate extends AbstractOperation<String> {

    private final UserCreateRequest userCreateRequest;

    public UserCreate(UserCreateRequest userCreateRequest) {
        super(String.class);
        this.userCreateRequest = userCreateRequest;
    }

    @Override
    protected void execute() {
        File userFolder = UsersFolderLocator.getFolder(userCreateRequest.getUserName());
        if (userFolder.exists()) {
            super.response = "USER ALREADY EXIST";
            return;
        }
        if (userCreateRequest.getRegistrationCode() != null && !userCreateRequest.getRegistrationCode().equals("")) {
            File usersCodesRegistrationUserFile = UsersCodesFolderLocator.getRegistrationUserFile(userCreateRequest.getUserName());
            if (!usersCodesRegistrationUserFile.isFile()) {
                super.response = "USER HAS NOT REGISTRATION CODE";
                return;
            }
            try {
                JsonNode usersCodesRegistrationUser = mapper.readTree(usersCodesRegistrationUserFile);
                if (!usersCodesRegistrationUser.get("otpCode").textValue().equals(userCreateRequest.getRegistrationCode())) {
                    super.response = "REGISTRATION CODE IS INVALID FOR USER";
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(UserCreate.class.getName()).log(Level.SEVERE, null, ex);
                super.response = "FAIL";
                return;
            }
        }
        JsonNode referralCodes = null;
        if (userCreateRequest.getReferralCode() != null && !userCreateRequest.getReferralCode().equals("")) {
            referralCodes = new MCUserGetReferralCodes().getResponse();
            if (!referralCodes.has(userCreateRequest.getReferralCode()) || !referralCodes.get(userCreateRequest.getReferralCode()).get("active").booleanValue()) {
                super.response = "REFERRAL CODE IS INVALID";
                return;
            }
        }
        userFolder = FileUtil.createFolderIfNoExist(userFolder);
        FileUtil.createFolderIfNoExist(userFolder, "Balance");
        FileUtil.createFolderIfNoExist(userFolder, "Models");
        File userFile = new File(userFolder, "config.json");
        JsonNode user = mapper.createObjectNode();
        JsonNode masterWalletIds = mapper.createObjectNode();
        ((ObjectNode) user).put("name", userCreateRequest.getUserName());
        ((ObjectNode) user).put("email", userCreateRequest.getEmail());
        ((ObjectNode) user).put("active", true);
        ((ObjectNode) user).put("type", userCreateRequest.getUserProfile().getUserType().name());
        ((ObjectNode) user).put("environment", userCreateRequest.getUserProfile().getUserEnvironment().name());
        ((ObjectNode) user).put("operationAccount",
                userCreateRequest.getUserProfile().getUserOperationAccount().name());
        userCreateRequest.getMasterWalletIds().keySet().stream().forEach((key) -> {
            ((ObjectNode) masterWalletIds).put(key, userCreateRequest.getMasterWalletIds().get(key));
        });
        ((ObjectNode) user).set("masterWalletIds", masterWalletIds);
        if (userCreateRequest.getUserProfile().getUserEnvironment().equals(UserEnvironment.PRODUCTION)) {
            addRandomLoginAccounts(user, mapper);
        }
        ((ObjectNode) user).put("operatorName", OPERATOR_NAME);
        if (userCreateRequest.getReferralCode() != null && !userCreateRequest.getReferralCode().equals("")) {
            ((ObjectNode) user).put("referralCode", userCreateRequest.getReferralCode());
        }
        ((ObjectNode) user).put("creationTimestamp", DateUtil.getCurrentDate());
        FileUtil.createFile(user, userFile);
        for (String currency : userCreateRequest.getAmounts().keySet()) {
            BaseOperation.addToBalance(
                    UsersFolderLocator.getBalanceFolder(userCreateRequest.getUserName()),
                    currency,
                    userCreateRequest.getAmounts().get(currency),
                    BalanceOperationType.INITIAL_MOVEMENT,
                    BalanceOperationStatus.OK,
                    "CREATING USER " + userCreateRequest.getUserName(),
                    null,
                    null,
                    false,
                    null
            );
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(UserCreate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JsonNode userOperator = mapper.createObjectNode();
        ((ObjectNode) userOperator).put("userName", userCreateRequest.getUserName());
        ((ObjectNode) userOperator).put("createTimestamp", DateUtil.getCurrentDate());
        FileUtil.createFile(userOperator, new File(UsersOperatorFolderLocator.getFolder(), userCreateRequest.getUserName() + ".json"));
        if(referralCodes != null){
            String bankerUserName = referralCodes.get(userCreateRequest.getReferralCode()).get("userName").textValue();
            if(BankersFolderLocator.getConfigFile(bankerUserName).isFile()){
                try {
                    File bankerReferredUsersFile = BankersFolderLocator.getReferredUsersFile(bankerUserName);
                    ArrayNode bankerReferredUsers = (ArrayNode) mapper.readTree(bankerReferredUsersFile);
                    bankerReferredUsers.add(userCreateRequest.getUserName());
                    FileUtil.editFile(bankerReferredUsers, bankerReferredUsersFile);
                } catch (IOException ex) {
                    Logger.getLogger(UserCreate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        super.response = "OK";
    }
    
    private void addRandomLoginAccounts(JsonNode user, ObjectMapper mapper) {
        File loginAccountsFile = BaseFilesLocator.getLoginAccountsFile();
        Map<String, List<String>> loginAccountsByExchangeId = new HashMap<>();
        try {
            ArrayNode loginAccounts = (ArrayNode) mapper.readTree(loginAccountsFile);
            Iterator<JsonNode> loginAccountsIterator = loginAccounts.elements();
            while (loginAccountsIterator.hasNext()) {
                JsonNode loginAccountsIt = loginAccountsIterator.next();
                boolean loginAccountsItSelectableAutomatically = loginAccountsIt.get("selectableAutomatically")
                        .booleanValue();
                if (!loginAccountsItSelectableAutomatically) {
                    continue;
                }
                String loginAccountsItExchangeId = loginAccountsIt.get("exchangeId").textValue();
                String loginAccountsItName = loginAccountsIt.get("name").textValue();
                if (!loginAccountsByExchangeId.containsKey(loginAccountsItExchangeId)) {
                    loginAccountsByExchangeId.put(loginAccountsItExchangeId, new ArrayList<>());
                }
                loginAccountsByExchangeId.get(loginAccountsItExchangeId).add(loginAccountsItName);
            }
            for (String exchangeId : loginAccountsByExchangeId.keySet()) {
                ((ObjectNode) user).put(exchangeId + "_loginAccount", loginAccountsByExchangeId.get(exchangeId)
                        .get(new Random().nextInt(loginAccountsByExchangeId.get(exchangeId).size())));
            }
        } catch (IOException ex) {
            Logger.getLogger(UserCreate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
