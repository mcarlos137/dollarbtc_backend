/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.reports.main;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcarlos
 */
public abstract class ReportsListResult extends ReportsBase {

    private final List<List<Object>> result = new ArrayList<>();

    public ReportsListResult(JsonNode conditions, Set<String> collectInfo, double btcusdPrice, int maxIterations) {
        run(conditions, collectInfo, btcusdPrice, maxIterations);
    }

    protected List<List<Object>> getResult() {
        return result;
    }

    private void run(JsonNode conditions, Set<String> collectInfo, double btcusdPrice, int maxIterations) {
        ObjectMapper mapper = new ObjectMapper();
        int i = 0;
        for (File userFolder : UsersFolderLocator.getFolder().listFiles()) {
            if (i >= maxIterations) {
                break;
            }
            i++;
            if (!userFolder.isDirectory()) {
                continue;
            }
            if (BLACK_LISTED_USERS.contains(userFolder.getName())) {
                continue;
            }
            if (userFolder.getName().contains("@mailinator.com")) {
                continue;
            }
            System.out.println("---------------------------------------------------------");
            System.out.println("user: " + userFolder.getName());
            try {
                JsonNode user = mapper.readTree(UsersFolderLocator.getConfigFile(userFolder.getName()));
                String verificationTimestamp = ""; //calculated property
                //Double usdEstimatedBalance = 0.0; //calculated property
                //int balanceMovementsCryptoDepositsQuantity = 0;
                //int balanceMovementsFiatDepositsQuantity = 0;
                //Double balanceMovementsCryptoDepositsAmount = 0.0;
                //Double balanceMovementsFiatDepositsAmount = 0.0;
                //CONDITIONS
                if (!conditionActive(user, conditions)) {
                    continue;
                }
                if (!conditionFlag(user, conditions)) {
                    continue;
                }
                if (!conditionVerificated(user, conditions, verificationTimestamp)) {
                    continue;
                }
                if (!conditionEmail(user, conditions)) {
                    continue;
                }
                Object[] conditionUSDEstimatedBalance = conditionUSDEstimatedBalance(user, conditions, "USD_ESTIMATED_BALANCE");
                if (!(Boolean) conditionUSDEstimatedBalance[0]) {
                    continue;
                }
                Object[] conditionsBalanceMovementsResult = conditionsBalanceMovements(user, conditions);
                if (!(Boolean) conditionsBalanceMovementsResult[0]) {
                    continue;
                }
                //COLLECT INFO
                List<Object> value = new ArrayList<>();
                value.add(user.get("name").textValue());
                for (String info : collectInfo) {
                    Object val = "";
                    if (user.has(info)) {
                        val = user.get(info).textValue();
                    }
                    value.add(val);
                }
                if (collectInfo.contains("verificationTimestamp")) {
                    value.add(verificationTimestamp);
                }
                if (collectInfo.contains("usdEstimatedBalance")) {
                    value.add(conditionUSDEstimatedBalance[1]);
                }
                if (collectInfo.contains("btcEstimatedBalance")) {
                    value.add((Double) conditionUSDEstimatedBalance[1] / btcusdPrice);
                }
                if (collectInfo.contains("balanceMovementsCryptoDepositsQuantity")) {
                    value.add(conditionsBalanceMovementsResult[1]);
                }
                if (collectInfo.contains("balanceMovementsCryptoTransfersInQuantity")) {
                    value.add(conditionsBalanceMovementsResult[2]);
                }
                if (collectInfo.contains("balanceMovementsCryptoTransfersOutQuantity")) {
                    value.add(conditionsBalanceMovementsResult[3]);
                }
                if (collectInfo.contains("balanceMovementsFiatDepositsQuantity")) {
                    value.add(conditionsBalanceMovementsResult[4]);
                }
                if (collectInfo.contains("balanceMovementsFiatTransfersInQuantity")) {
                    value.add(conditionsBalanceMovementsResult[5]);
                }
                if (collectInfo.contains("balanceMovementsFiatTransfersOutQuantity")) {
                    value.add(conditionsBalanceMovementsResult[6]);
                }
                if (collectInfo.contains("balanceMovementsChangesQuantity")) {
                    value.add(conditionsBalanceMovementsResult[7]);
                }
                result.add(value);
            } catch (IOException ex) {
                Logger.getLogger(ReportsListResult.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static Object[] conditionsBalanceMovements(
            JsonNode user,
            JsonNode conditions
    ) {
        int balanceMovementsCryptoDepositsQuantity = 0;
        int balanceMovementsCryptoTransfersInQuantity = 0;
        int balanceMovementsCryptoTransfersOutQuantity = 0;
        int balanceMovementsFiatDepositsQuantity = 0;
        int balanceMovementsFiatTransfersInQuantity = 0;
        int balanceMovementsFiatTransfersOutQuantity = 0;
        int balanceMovementsChangesQuantity = 0;
        ObjectMapper mapper = new ObjectMapper();
        if (conditions.has("BALANCE_MOVEMENTS_CRYPTO_DEPOSITS_QUANTITY")
                || conditions.has("BALANCE_MOVEMENTS_CRYPTO_TRANSFERS_IN_QUANTITY")
                || conditions.has("BALANCE_MOVEMENTS_CRYPTO_TRANSFERS_OUT_QUANTITY")
                || conditions.has("BALANCE_MOVEMENTS_FIAT_DEPOSITS_QUANTITY")
                || conditions.has("BALANCE_MOVEMENTS_FIAT_TRANSFERS_IN_QUANTITY")
                || conditions.has("BALANCE_MOVEMENTS_FIAT_TRANSFERS_OUT_QUANTITY")
                || conditions.has("BALANCE_MOVEMENTS_CHANGES_QUANTITY")
                || conditions.has("BALANCE_MOVEMENTS_STATUS")) {
            File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(user.get("name").textValue());
            for (File userMCBalanceMovementFile : userMCBalanceFolder.listFiles()) {
                if (!userMCBalanceMovementFile.isFile()) {
                    continue;
                }
                try {
                    JsonNode userMCBalanceMovement = mapper.readTree(userMCBalanceMovementFile);
                    String timestamp = userMCBalanceMovement.get("timestamp").textValue();
                    BalanceOperationStatus balanceOperationStatus = BalanceOperationStatus.valueOf(userMCBalanceMovement.get("balanceOperationStatus").textValue());
                    BalanceOperationType balanceOperationType = BalanceOperationType.valueOf(userMCBalanceMovement.get("balanceOperationType").textValue());
                    String currency = null;
                    if (conditions.has("BALANCE_MOVEMENTS_STATUS") && !BalanceOperationStatus.valueOf(conditions.get("BALANCE_MOVEMENTS_STATUS").textValue()).equals(balanceOperationStatus)) {
                        continue;
                    }
                    if (conditions.has("BALANCE_MOVEMENTS_CRYPTO_DEPOSITS_QUANTITY") && balanceOperationType.equals(BalanceOperationType.RECEIVE_OUT)) {
                        balanceMovementsCryptoDepositsQuantity++;
                    }
                    if (conditions.has("BALANCE_MOVEMENTS_CRYPTO_TRANSFERS_IN_QUANTITY") && (balanceOperationType.equals(BalanceOperationType.RECEIVE_IN) || balanceOperationType.equals(BalanceOperationType.SEND_IN))) {
                        balanceMovementsCryptoTransfersInQuantity++;
                    }
                    if (conditions.has("BALANCE_MOVEMENTS_CRYPTO_TRANSFERS_OUT_QUANTITY") && balanceOperationType.equals(BalanceOperationType.SEND_OUT)) {
                        balanceMovementsCryptoTransfersOutQuantity++;
                    }
                    if (conditions.has("BALANCE_MOVEMENTS_FIAT_DEPOSITS_QUANTITY") && balanceOperationType.equals(BalanceOperationType.MC_BUY_BALANCE)) {
                        balanceMovementsFiatDepositsQuantity++;
                    }
                    if (conditions.has("BALANCE_MOVEMENTS_FIAT_TRANSFERS_IN_QUANTITY") && (balanceOperationType.equals(BalanceOperationType.MC_SEND_SMS_NATIONAL) || balanceOperationType.equals(BalanceOperationType.MC_SEND_SMS_INTERNATIONAL))) {
                        balanceMovementsFiatTransfersInQuantity++;
                    }
                    if (conditions.has("BALANCE_MOVEMENTS_FIAT_TRANSFERS_OUT_QUANTITY") && balanceOperationType.equals(BalanceOperationType.SEND_TO_PAYMENT)) {
                        balanceMovementsFiatTransfersOutQuantity++;
                    }
                    if (conditions.has("BALANCE_MOVEMENTS_CHANGES_QUANTITY")
                            && (balanceOperationType.equals(BalanceOperationType.CURRENCY_CHANGE)
                            || balanceOperationType.equals(BalanceOperationType.MC_FAST_CHANGE) 
                            || balanceOperationType.equals(BalanceOperationType.FAST_CHANGE)
                            || balanceOperationType.equals(BalanceOperationType.MC_BUY_BITCOINS)
                            || balanceOperationType.equals(BalanceOperationType.MC_SELL_BITCOINS)
                            || balanceOperationType.equals(BalanceOperationType.MC_BUY_CRYPTO)
                            || balanceOperationType.equals(BalanceOperationType.MC_SELL_CRYPTO)
                            )) {
                        balanceMovementsChangesQuantity++;
                    }

                    /*
    GIFT_CARD_ACTIVATION,
    GIFT_CARD_BUY,
    GIFT_CARD_REDEEM,
    GIFT_CARD_REDEEM_BR,
    GIFT_CARD_REDEEM_FIAT,
    GIFT_CARD_REDEEM_TC,
                    */
                    Double amount = null;
                    if (userMCBalanceMovement.has("addedAmount")) {
                        amount = userMCBalanceMovement.get("addedAmount").get("amount").doubleValue();
                        currency = userMCBalanceMovement.get("addedAmount").get("currency").textValue();
                    }
                    if (userMCBalanceMovement.has("substractedAmount")) {
                        amount = userMCBalanceMovement.get("substractedAmount").get("amount").doubleValue();
                        currency = userMCBalanceMovement.get("substractedAmount").get("currency").textValue();
                    }
                    if (conditions.has("BALANCE_MOVEMENTS_STATUS") && !BalanceOperationStatus.valueOf(conditions.get("BALANCE_MOVEMENTS_STATUS").textValue()).equals(balanceOperationStatus)) {
                        return new Object[]{false, balanceMovementsCryptoDepositsQuantity, balanceMovementsFiatDepositsQuantity};
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ReportsListResult.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Object[] result = new Object[]{
                true, 
                balanceMovementsCryptoDepositsQuantity, 
                balanceMovementsCryptoTransfersInQuantity,
                balanceMovementsCryptoTransfersOutQuantity,
                balanceMovementsFiatDepositsQuantity,
                balanceMovementsFiatTransfersInQuantity,
                balanceMovementsFiatTransfersOutQuantity,
                balanceMovementsChangesQuantity
            };
            if (conditions.has("BALANCE_MOVEMENTS_CRYPTO_DEPOSITS_QUANTITY")) {
                double baseValue = balanceMovementsCryptoDepositsQuantity;
                boolean comparateOperation = comparateOperation(conditions.get("BALANCE_MOVEMENTS_CRYPTO_DEPOSITS_QUANTITY").textValue(), baseValue);
                if (!comparateOperation) {
                    result[0] = false;
                    return result;
                }
            }
            if (conditions.has("BALANCE_MOVEMENTS_CRYPTO_TRANSFERS_IN_QUANTITY")) {
                double baseValue = balanceMovementsCryptoTransfersInQuantity;
                boolean comparateOperation = comparateOperation(conditions.get("BALANCE_MOVEMENTS_CRYPTO_TRANSFERS_IN_QUANTITY").textValue(), baseValue);
                if (!comparateOperation) {
                    result[0] = false;
                    return result;
                }
            }
            if (conditions.has("BALANCE_MOVEMENTS_CRYPTO_TRANSFERS_OUT_QUANTITY")) {
                double baseValue = balanceMovementsCryptoTransfersOutQuantity;
                boolean comparateOperation = comparateOperation(conditions.get("BALANCE_MOVEMENTS_CRYPTO_TRANSFERS_OUT_QUANTITY").textValue(), baseValue);
                if (!comparateOperation) {
                    result[0] = false;
                    return result;
                }
            }
            if (conditions.has("BALANCE_MOVEMENTS_FIAT_DEPOSITS_QUANTITY")) {
                double baseValue = balanceMovementsFiatDepositsQuantity;
                boolean comparateOperation = comparateOperation(conditions.get("BALANCE_MOVEMENTS_FIAT_DEPOSITS_QUANTITY").textValue(), baseValue);
                if (!comparateOperation) {
                    result[0] = false;
                    return result;
                }
            }
            if (conditions.has("BALANCE_MOVEMENTS_FIAT_DEPOSITS_QUANTITY")) {
                double baseValue = balanceMovementsFiatDepositsQuantity;
                boolean comparateOperation = comparateOperation(conditions.get("BALANCE_MOVEMENTS_FIAT_DEPOSITS_QUANTITY").textValue(), baseValue);
                if (!comparateOperation) {
                    result[0] = false;
                    return result;
                }
            }
            if (conditions.has("BALANCE_MOVEMENTS_FIAT_TRANSFERS_IN_QUANTITY")) {
                double baseValue = balanceMovementsFiatTransfersInQuantity;
                boolean comparateOperation = comparateOperation(conditions.get("BALANCE_MOVEMENTS_FIAT_TRANSFERS_IN_QUANTITY").textValue(), baseValue);
                if (!comparateOperation) {
                    result[0] = false;
                    return result;
                }
            }
            if (conditions.has("BALANCE_MOVEMENTS_FIAT_TRANSFERS_OUT_QUANTITY")) {
                double baseValue = balanceMovementsFiatTransfersOutQuantity;
                boolean comparateOperation = comparateOperation(conditions.get("BALANCE_MOVEMENTS_FIAT_TRANSFERS_OUT_QUANTITY").textValue(), baseValue);
                if (!comparateOperation) {
                    result[0] = false;
                    return result;
                }
            }
            if (conditions.has("BALANCE_MOVEMENTS_CHANGES_QUANTITY")) {
                double baseValue = balanceMovementsChangesQuantity;
                boolean comparateOperation = comparateOperation(conditions.get("BALANCE_MOVEMENTS_CHANGES_QUANTITY").textValue(), baseValue);
                if (!comparateOperation) {
                    result[0] = false;
                    return result;
                }
            }
            return result;
        }
        return new Object[]{true, balanceMovementsCryptoDepositsQuantity, balanceMovementsFiatDepositsQuantity};
    }

    private static Object[] conditionUSDEstimatedBalance(JsonNode user, JsonNode conditions, String condition) {
        if (conditions.has(condition)) {
            Double usdEstimatedBalance = getBalance(user.get("name").textValue(), "usdEstimatedBalance");
            return new Object[]{comparateOperation(conditions.get(condition).textValue(), usdEstimatedBalance), usdEstimatedBalance};
        }
        return new Object[]{true, 0.0};
    }

    private static boolean conditionEmail(JsonNode user, JsonNode conditions) {
        if (conditions.has("EMAIL")) {
            switch (conditions.get("EMAIL").textValue()) {
                case "ANY_VALUE":
                    if (!user.has("email") || user.get("email").textValue().equals("")) {
                        return false;
                    }
                    break;
                case "NO_VALUE":
                    if (user.has("email") && !user.get("email").textValue().equals("")) {
                        return false;
                    }
                    break;
                default:
                    if (!conditions.get("EMAIL").textValue().equals(user.get("email").textValue())) {
                        return false;
                    }
            }
        }
        return true;
    }

    private static boolean conditionVerificated(JsonNode user, JsonNode conditions, String verificationTimestamp) {
        if (conditions.has("VERIFICATED")) {
            boolean verificated = false;
            if (user.has("verification")
                    && user.get("verification").has("E")
                    && user.get("verification").get("E").has("userVerificationStatus")
                    && user.get("verification").get("E").get("userVerificationStatus").textValue().equals("OK")) {
                verificationTimestamp = user.get("verification").get("E").get("timestamp").textValue();
                verificated = true;
            }
            if (user.has("verification")
                    && user.get("verification").has("C")
                    && user.get("verification").get("C").has("userVerificationStatus")
                    && user.get("verification").get("C").get("userVerificationStatus").textValue().equals("OK")) {
                verificationTimestamp = user.get("verification").get("C").get("timestamp").textValue();
                verificated = true;
            }
            if (!(conditions.get("VERIFICATED").booleanValue() && verificated)) {
                return false;
            }
        }
        return true;
    }

    private static boolean conditionFlag(JsonNode user, JsonNode conditions) {
        if (conditions.has("FLAG")) {
            if (!(user.has("flag") && user.get("flag").textValue().equals(conditions.get("FLAG").textValue()))) {
                return false;
            }
        }
        return true;
    }

    private static boolean conditionActive(JsonNode user, JsonNode conditions) {
        if (conditions.has("ACTIVE")) {
            if (!(user.get("active").booleanValue() && conditions.get("ACTIVE").booleanValue())) {
                return false;
            }
        }
        return true;
    }

    private static boolean comparateOperation(String operationValue, Double baseValue) {
        String balanceOperation = "EQUAL";
        if (operationValue.contains(">")) {
            balanceOperation = "MAJOR";
        }
        if (operationValue.contains("<")) {
            balanceOperation = "MINOR";
        }
        if (operationValue.contains(">=")) {
            balanceOperation = "MAJOR_EQUAL";
        }
        if (operationValue.contains("<=")) {
            balanceOperation = "MINOR_EQUAL";
        }
        if (operationValue.contains("<>")) {
            balanceOperation = "DIFFERENT";
        }
        Double balanceComparator = Double.parseDouble(operationValue.replace(">", "").replace("<", "").replace("=", ""));
        switch (balanceOperation) {
            case "EQUAL":
                if (!baseValue.equals(balanceComparator)) {
                    return false;
                }
                break;
            case "MAJOR":
                if (baseValue <= balanceComparator) {
                    return false;
                }
                break;
            case "MINOR":
                if (baseValue >= balanceComparator) {
                    return false;
                }
                break;
            case "MAJOR_EQUAL":
                if (baseValue < balanceComparator) {
                    return false;
                }
                break;
            case "MINOR_EQUAL":
                if (baseValue > balanceComparator) {
                    return false;
                }
                break;
            case "DIFFERENT":
                if (baseValue.equals(balanceComparator)) {
                    return false;
                }
                break;
        }
        return true;
    }

    private static Double getBalance(String userName, String tag) {
        JsonNode userMCBalance = new MCUserGetBalance(userName, true, true).getResponse();
        try {
            Thread.sleep(100);

        } catch (InterruptedException ex) {
            Logger.getLogger(ReportsListResult.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal(userMCBalance.get(tag).doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
