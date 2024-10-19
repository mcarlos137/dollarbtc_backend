/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.admin;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserOperationAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class AdminGetBalance extends AbstractOperation<JsonNode> {
    
    private final UserType userType; 
    private final UserEnvironment userEnvironment; 
    private final UserOperationAccount userOperationAccount; 
    private final boolean excludeUsersDetails;
    private final UserProfile userProfile; 

    public AdminGetBalance(UserType userType, UserEnvironment userEnvironment, UserOperationAccount userOperationAccount, boolean excludeUsersDetails, UserProfile userProfile) {
        super(JsonNode.class);
        this.userType = userType;
        this.userEnvironment = userEnvironment;
        this.userOperationAccount = userOperationAccount;
        this.excludeUsersDetails = excludeUsersDetails;
        this.userProfile = userProfile;
    }
    
    @Override
    protected void execute() {
        if(userProfile != null){
            super.response = getBalance(userProfile, excludeUsersDetails);
        } else if(userType != null && userEnvironment != null && userOperationAccount != null){
            super.response = getBalance(userType, userEnvironment, userOperationAccount, excludeUsersDetails);
        }
    }

    private JsonNode getBalance(UserType userType, UserEnvironment userEnvironment, UserOperationAccount userOperationAccount, boolean excludeUsersDetails) {
        JsonNode result = mapper.createObjectNode();
        ArrayNode users = mapper.createArrayNode();
        ArrayNode estimatedValues = mapper.createArrayNode();
        ArrayNode availableAmounts = mapper.createArrayNode();
        ArrayNode inSymbols = mapper.createArrayNode();
        ArrayNode usersDetails = mapper.createArrayNode();
        Map<String, Double> estimatedValuesMap = new HashMap<>();
        Map<String, Double> availableAmountsMap = new HashMap<>();
        File usersFolder = UsersFolderLocator.getFolder();
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            File userFile = new File(userFolder, "config.json");
            try {
                JsonNode user = mapper.readTree(userFile);
                UserType userTypee = UserType.NORMAL;
                if(user.has("type")){
                    userTypee = UserType.valueOf(user.get("type").textValue());
                }
                UserEnvironment userEnvironmentt = UserEnvironment.valueOf(user.get("environment").textValue());
                UserOperationAccount userOperationAccountt = UserOperationAccount.valueOf(user.get("operationAccount").textValue());
                if (!userTypee.equals(userType)
                        || !userEnvironmentt.equals(userEnvironment)
                        || !userOperationAccountt.equals(userOperationAccount)) {
                    continue;
                }
            } catch (IOException ex) {
                Logger.getLogger(AdminGetBalance.class.getName()).log(Level.SEVERE, null, ex);
            }
            users.add(userFolder.getName());
            JsonNode userBalance = LocalData.getUserBalance(userFolder.getName(), true, true, false, null, false);
            usersDetails.add(userBalance);
            Iterator<JsonNode> estimatedValuesIterator = userBalance.get("estimatedValues").elements();
            while (estimatedValuesIterator.hasNext()) {
                JsonNode estimatedValuesIt = estimatedValuesIterator.next();
                if (!estimatedValuesMap.containsKey(estimatedValuesIt.get("currency").textValue())) {
                    estimatedValuesMap.put(estimatedValuesIt.get("currency").textValue(), 0.0);
                }
                estimatedValuesMap.put(estimatedValuesIt.get("currency").textValue(), estimatedValuesMap.get(estimatedValuesIt.get("currency").textValue()) + estimatedValuesIt.get("amount").doubleValue());
            }
            Iterator<JsonNode> modelBalancesIterator = userBalance.get("modelBalances").elements();
            while (modelBalancesIterator.hasNext()) {
                JsonNode modelBalancesIt = modelBalancesIterator.next();
                String modelBalancesModelName = modelBalancesIt.get("modelName").textValue();
                Iterator<JsonNode> modelBalancesInSymbolsIterator = modelBalancesIt.get("inSymbols").elements();
                while (modelBalancesInSymbolsIterator.hasNext()) {
                    JsonNode modelBalancesInSymbolsIt = modelBalancesInSymbolsIterator.next();
                    ((ObjectNode) modelBalancesInSymbolsIt).put("modelName", modelBalancesModelName);
                    inSymbols.add(modelBalancesInSymbolsIt);
                }
                Iterator<JsonNode> modelBalancesAvailableAmountsIterator = modelBalancesIt.get("availableAmounts").elements();
                while (modelBalancesAvailableAmountsIterator.hasNext()) {
                    JsonNode modelBalancesAvailableAmountsIt = modelBalancesAvailableAmountsIterator.next();
                    if (!availableAmountsMap.containsKey(modelBalancesAvailableAmountsIt.get("currency").textValue())) {
                        availableAmountsMap.put(modelBalancesAvailableAmountsIt.get("currency").textValue(), 0.0);
                    }
                    availableAmountsMap.put(modelBalancesAvailableAmountsIt.get("currency").textValue(), availableAmountsMap.get(modelBalancesAvailableAmountsIt.get("currency").textValue()) + modelBalancesAvailableAmountsIt.get("amount").doubleValue());
                }
            }
        }
        for (String key : estimatedValuesMap.keySet()) {
            JsonNode estimatedValue = mapper.createObjectNode();
            ((ObjectNode) estimatedValue).put("currency", key);
            ((ObjectNode) estimatedValue).put("amount", estimatedValuesMap.get(key));
            estimatedValues.add(estimatedValue);
        }
        for (String key : availableAmountsMap.keySet()) {
            JsonNode availableAmount = mapper.createObjectNode();
            ((ObjectNode) availableAmount).put("currency", key);
            ((ObjectNode) availableAmount).put("amount", availableAmountsMap.get(key));
            availableAmounts.add(availableAmount);
        }
        ((ObjectNode) result).putArray("users").addAll(users);
        ((ObjectNode) result).putArray("estimatedValues").addAll(estimatedValues);
        ((ObjectNode) result).putArray("availableAmounts").addAll(availableAmounts);
        ((ObjectNode) result).putArray("inSymbols").addAll(inSymbols);
        if (!excludeUsersDetails) {
            ((ObjectNode) result).putArray("usersDetails").addAll(usersDetails);
        }
        return result;
    }
    
    private JsonNode getBalance(UserProfile userProfile, boolean excludeUsersDetails) {
        return getBalance(userProfile.getUserType(), userProfile.getUserEnvironment(), userProfile.getUserOperationAccount(), excludeUsersDetails);
    }
    
}
