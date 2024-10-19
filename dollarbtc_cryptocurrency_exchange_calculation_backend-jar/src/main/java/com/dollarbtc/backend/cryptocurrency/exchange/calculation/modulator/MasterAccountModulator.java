/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation.addToBalance;
import static com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation.substractToBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.ModelOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserGetNames;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class MasterAccountModulator extends BasicMasterAccountModulator {

    public MasterAccountModulator(String threadName) {
        super(threadName);
    }

    @Override
    protected void runMasterAccountModulator(
            String baseAccount,
            Map<String, Double> targetAccountOrClientModelNamePercent,
            BalanceOperationType balanceOperationType,
            Integer executionPeriodInHours
    ) {
        System.out.println("----------------------------------------------");
        System.out.println("baseAccount: " + baseAccount);
        System.out.println("targetAccountOrClientModelNamePercent: " + targetAccountOrClientModelNamePercent);
        System.out.println("balanceOperationType: " + balanceOperationType);
        System.out.println("executionPeriodInHours: " + executionPeriodInHours);
        ObjectMapper mapper = new ObjectMapper();
        File masterAccountExecutionFile = getMasterAccountExecutionFile();
        ArrayNode baseAccountBalance = BaseOperation.getBalance(MasterAccountFolderLocator.getBalanceFolder(baseAccount));
        for (String targetAccountOrClientModelName : targetAccountOrClientModelNamePercent.keySet()) {
            try {
                String timestamp;
                JsonNode masterAccountExecution = mapper.readTree(masterAccountExecutionFile);
                if (!masterAccountExecution.has(baseAccount + "__" + targetAccountOrClientModelName)) {
                    timestamp = DateUtil.getDayStartDate(null);
                    ((ObjectNode) masterAccountExecution).put(baseAccount + "__" + targetAccountOrClientModelName, timestamp);
                    execute(baseAccount, baseAccountBalance, targetAccountOrClientModelName, targetAccountOrClientModelNamePercent.get(targetAccountOrClientModelName), balanceOperationType, timestamp);
                } else {
                    timestamp = DateUtil.getDateHoursAfter(masterAccountExecution.get(baseAccount + "__" + targetAccountOrClientModelName).textValue(), executionPeriodInHours);
                    if (DateUtil.parseDate(DateUtil.getCurrentDate()).compareTo(DateUtil.parseDate(timestamp)) > 0) {
                        ((ObjectNode) masterAccountExecution).put(baseAccount + "__" + targetAccountOrClientModelName, timestamp);
                        execute(baseAccount, baseAccountBalance, targetAccountOrClientModelName, targetAccountOrClientModelNamePercent.get(targetAccountOrClientModelName), balanceOperationType, timestamp);
                    }
                }
                FileUtil.editFile(masterAccountExecution, masterAccountExecutionFile);
            } catch (IOException ex) {
                Logger.getLogger(MasterAccountModulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void execute(String baseAccount, ArrayNode baseAccountBalance, String targetAccountOrClientModelName, Double percent, BalanceOperationType balanceOperationType, String timestamp) {
        Iterator<JsonNode> baseAccountBalanceIterator = baseAccountBalance.elements();
        while (baseAccountBalanceIterator.hasNext()) {
            JsonNode baseAccountBalanceIt = baseAccountBalanceIterator.next();
            String currency = baseAccountBalanceIt.get("currency").textValue();
            Double amount = baseAccountBalanceIt.get("amount").doubleValue() * percent / 100;
            if (amount == 0.0) {
                return;
            }
            switch (balanceOperationType) {
                case TRANSFER_BETWEEN_MASTERS:
                    addToBalance(
                            MasterAccountFolderLocator.getBalanceFolder(targetAccountOrClientModelName), 
                            currency, 
                            amount, 
                            balanceOperationType, 
                            BalanceOperationStatus.OK, 
                            null, 
                            null,
                            null,
                            false,
                            null
                    );
                    substractToBalance(
                            MasterAccountFolderLocator.getBalanceFolder(baseAccount), 
                            currency, 
                            amount, 
                            balanceOperationType, 
                            BalanceOperationStatus.OK, 
                            null, 
                            null, 
                            false,
                            null,
                            false,
                            null
                    );
                    break;
                case TRANSFER_TO_CLIENTS:
                    ObjectMapper mapper = new ObjectMapper();
                    UserProfile userProfile = UserProfile.valueOf("NORMAL");
                    Iterator<JsonNode> investedAmountsIterator = ModelOperation.getInvestedAmounts(targetAccountOrClientModelName, userProfile).elements();
                    Double generalInvestedAmount = null;
                    while (investedAmountsIterator.hasNext()) {
                        JsonNode investedAmountsIt = investedAmountsIterator.next();
                        if (currency.equals(investedAmountsIt.get("currency").textValue())) {
                            generalInvestedAmount = investedAmountsIt.get("amount").doubleValue();
                            break;
                        }
                    }
                    Double substractedAmount = 0.0;
                    for (String userName : (Set<String>) new UserGetNames(userProfile).getResponse()) {
                        int i = 1;
                        boolean firstLoop = true;
                        while (true) {
                            String userModelName;
                            if (firstLoop) {
                                userModelName = userName + "__" + targetAccountOrClientModelName;
                                firstLoop = false;
                            } else {
                                userModelName = userName + "__" + targetAccountOrClientModelName + "____" + i;
                                i++;
                            }
                            File userModelAdditionalMovementsFile = ModelOperation.getUserModelAdditionalMovementsFile(userModelName);
                            if (!userModelAdditionalMovementsFile.isFile()) {
                                break;
                            }
                            JsonNode userModelConfig = ModelOperation.getConfig(userModelName);
                            if (userModelConfig == null || !userModelConfig.has("initialAmounts")) {
                                continue;
                            }
                            Double userModelInitialAmount = userModelConfig.get("initialAmounts").get(currency).doubleValue();
                            ArrayNode timestampArray = mapper.createArrayNode();
                            ObjectNode amountJson = mapper.createObjectNode();
                            Double userPortion = userModelInitialAmount / generalInvestedAmount;
                            Double userAmount = amount * userPortion;
                            substractedAmount = substractedAmount + userAmount;
                            amountJson.put("amount", userAmount);
                            amountJson.put("currency", currency);
                            timestampArray.add(amountJson);
                            try {
                                JsonNode userModelAdditionalMovements = mapper.readTree(userModelAdditionalMovementsFile);
                                ((ObjectNode) userModelAdditionalMovements).putArray(timestamp).addAll(timestampArray);
                                FileUtil.editFile(userModelAdditionalMovements, userModelAdditionalMovementsFile);
                            } catch (IOException ex) {
                                Logger.getLogger(MasterAccountModulator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    if (substractedAmount > 0) {
                        substractToBalance(
                                MasterAccountFolderLocator.getBalanceFolder(baseAccount), 
                                currency, 
                                substractedAmount, 
                                balanceOperationType, 
                                BalanceOperationStatus.OK, 
                                null, 
                                null, 
                                false,
                                null,
                                false,
                                null
                        );
                    }
                    break;
            }
        }
    }

    private static File getMasterAccountExecutionFile() {
        File masterAccountExecutionFile = new File(new File(OPERATOR_PATH, "MasterAccount"), "execution.json");
        if (!masterAccountExecutionFile.isFile()) {
            FileUtil.createFile(new ObjectMapper().createObjectNode(), masterAccountExecutionFile);
        }
        return masterAccountExecutionFile;
    }

}
