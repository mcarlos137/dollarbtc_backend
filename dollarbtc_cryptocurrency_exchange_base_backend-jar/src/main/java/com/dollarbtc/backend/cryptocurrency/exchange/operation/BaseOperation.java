/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.bancamiga.TransferirOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.ChargeAmountType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.ChargeType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OperationMessageSide;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentBank;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPayment;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AdminFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.WebsocketFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author carlosmolina
 */
public class BaseOperation {

    public static final File EMPTY_IMAGE_FILE = new File(new File(ExchangeUtil.OPERATOR_PATH, "Attachments"), "empty.png");

    private static final Map<File, Boolean> LOCKED_BALANCE_FOLDERS = new ConcurrentHashMap<>();

    public static String getId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static ArrayNode getBalance(File balanceFolder) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode balance = mapper.createArrayNode();
        if (!balanceFolder.isDirectory()) {
            return balance;
        }
        Map<String, Double> balanceMap = new HashMap<>();
        Map<String, Double> balanceMapDeferred = new HashMap<>();
        for (File balanceMovementFile : balanceFolder.listFiles()) {
            if (!balanceMovementFile.isFile() || balanceMovementFile.getName().equals("lock.json")) {
                continue;
            }
            try {
                boolean add = true;
                boolean deferred = false;
                JsonNode balanceMovementAmount = null;
                JsonNode balanceMovement = mapper.readTree(balanceMovementFile);
                if (!balanceMovement.has("balanceOperationStatus")) {
                    continue;
                }
                if (balanceMovement.has("addedAmount")) {
                    if (BalanceOperationStatus.valueOf(balanceMovement.get("balanceOperationStatus").textValue()).equals(BalanceOperationStatus.FAIL)) {
                        continue;
                    }
                    if (BalanceOperationStatus.valueOf(balanceMovement.get("balanceOperationStatus").textValue()).equals(BalanceOperationStatus.PROCESSING)) {
                        deferred = true;
                    }
                    balanceMovementAmount = balanceMovement.get("addedAmount");
                } else if (balanceMovement.has("substractedAmount")) {
                    if (BalanceOperationStatus.valueOf(balanceMovement.get("balanceOperationStatus").textValue()).equals(BalanceOperationStatus.FAIL)) {
                        continue;
                    }
                    balanceMovementAmount = balanceMovement.get("substractedAmount");
                    add = false;
                }
                if (balanceMovementAmount == null) {
                    return balance;
                }
                if (!balanceMovementAmount.has("currency")) {
                    continue;
                }
                if (!balanceMovementAmount.has("amount")) {
                    continue;
                }
                String currency = balanceMovementAmount.get("currency").textValue();
                if (!balanceMap.containsKey(currency)) {
                    balanceMap.put(currency, 0.0);
                }
                if (!balanceMapDeferred.containsKey(currency)) {
                    balanceMapDeferred.put(currency, 0.0);
                }
                if (add) {
                    if (deferred) {
                        balanceMapDeferred.put(currency, balanceMapDeferred.get(currency) + balanceMovementAmount.get("amount").doubleValue());
                    } else {
                        balanceMap.put(currency, balanceMap.get(currency) + balanceMovementAmount.get("amount").doubleValue());
                    }
                } else {
                    balanceMap.put(currency, balanceMap.get(currency) - balanceMovementAmount.get("amount").doubleValue());
                }
            } catch (IOException ex) {
                Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (String key : balanceMap.keySet()) {
            JsonNode currencyBalance = mapper.createObjectNode();
            String currency = key;
            Double amount = balanceMap.get(key);
            if ((currency.equals("BTC") || currency.equals("ETH")) && amount <= 0.00000002 && amount >= -0.00000002) {
                amount = 0.0;
            } else if (!(currency.equals("BTC") || currency.equals("ETH")) && amount <= 0.02 && amount >= -0.02) {
                amount = 0.0;
            }
            ((ObjectNode) currencyBalance).put("currency", currency);
            ((ObjectNode) currencyBalance).put("amount", amount);
            if (balanceMapDeferred.containsKey(key) && balanceMapDeferred.get(key) > 0.0) {
                ((ObjectNode) currencyBalance).put("deferredAmount", balanceMapDeferred.get(key));
            }
            balance.add(currencyBalance);
        }
        return balance;
    }

    public static ArrayNode getBalance(File balanceFolder, String initTimestamp, String endTimestamp) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode balance = mapper.createArrayNode();
        if (!balanceFolder.isDirectory()) {
            return balance;
        }
        Map<String, Double> balanceMap = new HashMap<>();
        Map<String, Double> balanceMapDeferred = new HashMap<>();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(balanceFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        String id1 = o1.toFile().getName();
                        String id2 = o2.toFile().getName();
                        return id1.compareTo(id2);
                    })
                    .iterator();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                File balanceMovementFile = it.toFile();
                if (!balanceMovementFile.isFile() || balanceMovementFile.getName().equals("lock.json")) {
                    continue;
                }
                JsonNode balanceMovement = mapper.readTree(balanceMovementFile);
                if (balanceMovement == null) {
                    continue;
                }
                if (balanceMovement.get("timestamp") == null) {
                    continue;
                }
                String timestamp = balanceMovement.get("timestamp").textValue();
                timestamp = timestamp.substring(0, 10) + timestamp.substring(10).replace("--", ".").replace("-", ":");
                if (timestamp == null || timestamp.equals("")) {
                    continue;
                }
                if (initTimestamp != null && !initTimestamp.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(initTimestamp)) < 0) {
                        continue;
                    }
                }
                if (endTimestamp != null && !endTimestamp.equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(endTimestamp)) > 0) {
                        break;
                    }
                }
                boolean add = true;
                boolean deferred = false;
                JsonNode balanceMovementAmount = null;
                if (!balanceMovement.has("balanceOperationStatus")) {
                    continue;
                }
                if (balanceMovement.has("addedAmount")) {
                    if (BalanceOperationStatus.valueOf(balanceMovement.get("balanceOperationStatus").textValue()).equals(BalanceOperationStatus.FAIL)) {
                        continue;
                    }
                    if (BalanceOperationStatus.valueOf(balanceMovement.get("balanceOperationStatus").textValue()).equals(BalanceOperationStatus.PROCESSING)) {
                        deferred = true;
                    }
                    balanceMovementAmount = balanceMovement.get("addedAmount");
                } else if (balanceMovement.has("substractedAmount")) {
                    if (BalanceOperationStatus.valueOf(balanceMovement.get("balanceOperationStatus").textValue()).equals(BalanceOperationStatus.FAIL)) {
                        continue;
                    }
                    balanceMovementAmount = balanceMovement.get("substractedAmount");
                    add = false;
                }
                if (balanceMovementAmount == null) {
                    return balance;
                }
                String currency = balanceMovementAmount.get("currency").textValue();
                if (!balanceMap.containsKey(currency)) {
                    balanceMap.put(currency, 0.0);
                }
                if (!balanceMapDeferred.containsKey(currency)) {
                    balanceMapDeferred.put(currency, 0.0);
                }
                if (add) {
                    if (deferred) {
                        balanceMapDeferred.put(currency, balanceMapDeferred.get(currency) + balanceMovementAmount.get("amount").doubleValue());
                    } else {
                        balanceMap.put(currency, balanceMap.get(currency) + balanceMovementAmount.get("amount").doubleValue());
                    }
                } else {
                    balanceMap.put(currency, balanceMap.get(currency) - balanceMovementAmount.get("amount").doubleValue());
                }
            }
        } catch (IOException ex) {
        }
        for (String key : balanceMap.keySet()) {
            JsonNode currencyBalance = mapper.createObjectNode();
            String currency = key;
            Double amount = balanceMap.get(key);
            if ((currency.equals("BTC") || currency.equals("ETH")) && amount <= 0.00000002) {
                amount = 0.0;
            } else if (!(currency.equals("BTC") || currency.equals("ETH")) && amount <= 0.02) {
                amount = 0.0;
            }
            ((ObjectNode) currencyBalance).put("currency", currency);
            ((ObjectNode) currencyBalance).put("amount", amount);
            if (balanceMapDeferred.containsKey(key) && balanceMapDeferred.get(key) > 0.0) {
                ((ObjectNode) currencyBalance).put("deferredAmount", balanceMapDeferred.get(key));
            }
            balance.add(currencyBalance);
        }
        return balance;
    }

    public static JsonNode getBalanceMovements(File balanceFolder, String initTimestamp, String endTimestamp, BalanceOperationType balanceOperationType, String currency, List<BalanceOperationStatus> balanceOperationStatuses) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, JsonNode> balanceMovements = new TreeMap<>();
        Map<String, JsonNode> finalBalanceMovements = new TreeMap<>();
        for (File balanceMovementFile : balanceFolder.listFiles()) {
            if (!balanceMovementFile.isFile()) {
                continue;
            }
            try {
                JsonNode balanceMovement = mapper.readTree(balanceMovementFile);
                if (balanceMovementFile.getName().equals("lock.json")) {
                    continue;
                }
                String timestamp = DateUtil.getDate(balanceMovementFile.getName().split("__")[1]);
                String position;
                if (balanceMovementFile.getName().split("__").length == 3) {
                    position = balanceMovementFile.getName().split("__")[2].replace(".json", "");
                } else {
                    timestamp = timestamp.replace(".json", "");
                    position = "1";
                }
                balanceMovements.put(timestamp + "__" + position, balanceMovement);
            } catch (IOException ex) {
                Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (String key : balanceMovements.keySet()) {
            if (currency != null && !currency.equals("")) {
                if (balanceMovements.get(key).has("addedAmount") && balanceMovements.get(key).get("addedAmount").has("currency") && !balanceMovements.get(key).get("addedAmount").get("currency").textValue().equals(currency)) {
                    continue;
                }
                if (balanceMovements.get(key).has("substractedAmount") && balanceMovements.get(key).get("substractedAmount").has("currency") && !balanceMovements.get(key).get("substractedAmount").get("currency").textValue().equals(currency)) {
                    continue;
                }
            }
            if (initTimestamp != null && !initTimestamp.equals("")) {
                if (DateUtil.parseDate(key.split("__")[0]).compareTo(DateUtil.parseDate(initTimestamp)) < 0) {
                    continue;
                }
            }
            if (endTimestamp != null && !endTimestamp.equals("")) {
                if (DateUtil.parseDate(key.split("__")[0]).compareTo(DateUtil.parseDate(endTimestamp)) > 0) {
                    continue;
                }
            }
            if (balanceOperationType != null) {
                if (balanceMovements.get(key).has("balanceOperationType") && !balanceMovements.get(key).get("balanceOperationType").textValue().equals(balanceOperationType.name())) {
                    continue;
                }
            }
            if (balanceOperationStatuses != null && !balanceOperationStatuses.isEmpty()) {
                if (balanceMovements.get(key).has("balanceOperationStatus") && !balanceOperationStatuses.contains(BalanceOperationStatus.valueOf(balanceMovements.get(key).get("balanceOperationStatus").textValue()))) {
                    continue;
                }
            }
            if (balanceMovements.get(key).has("addedAmount") && balanceMovements.get(key).get("addedAmount").get("amount").doubleValue() == 0) {
                continue;
            }
            if (balanceMovements.get(key).has("substractedAmount") && balanceMovements.get(key).get("substractedAmount").get("amount").doubleValue() == 0) {
                continue;
            }
            finalBalanceMovements.put(key, balanceMovements.get(key));
        }
        return mapper.valueToTree(finalBalanceMovements);
    }

    public static String addToBalance(File balanceFolder, String currency, Double amount, BalanceOperationType balanceOperationType, BalanceOperationStatus balanceOperationStatus, String additionalInfo, Double btcPrice, JsonNode charges, boolean returnFileName, ObjectNode additionals) {
        ObjectMapper mapper = new ObjectMapper();
        String timestamp = DateUtil.getFileDate(null);
        JsonNode addToBalance = mapper.createObjectNode();
        ObjectNode amountNode = mapper.createObjectNode();
        Double initialAmount = amount;
        if (charges != null && amount > 0) {
            Iterator<JsonNode> chargesIterator = charges.iterator();
            while (chargesIterator.hasNext()) {
                JsonNode chargesIt = chargesIterator.next();
                if (chargesIt.get("currency").textValue().equals(currency)) {
                    amount = amount - chargesIt.get("amount").doubleValue();
                } else if (chargesIt.get("currency").textValue().equals("BTC") && btcPrice != null) {
                    amount = amount - chargesIt.get("amount").doubleValue() * btcPrice;
                }
            }
        }
        amountNode.put("initialAmount", initialAmount);
        amountNode.put("amount", amount);
        amountNode.put("currency", currency);
        ((ObjectNode) addToBalance).put("timestamp", timestamp);
        ((ObjectNode) addToBalance).set("addedAmount", amountNode);
        ((ObjectNode) addToBalance).put("balanceOperationType", balanceOperationType.name());
        ((ObjectNode) addToBalance).put("balanceOperationStatus", balanceOperationStatus.name());
        if (balanceOperationStatus.equals(BalanceOperationStatus.PROCESSING)) {
            ((ObjectNode) addToBalance).put("balanceOperationProcessId", getId());
        }
        if (additionalInfo != null) {
            ((ObjectNode) addToBalance).put("additionalInfo", additionalInfo);
        }
        if (btcPrice != null) {
            ((ObjectNode) addToBalance).put("btcPrice", btcPrice);
        }
        if (additionals != null) {
            ((ObjectNode) addToBalance).putAll(additionals);
        }
        String fileName = "add__" + timestamp + "__1" + ".json";
        File addToBalanceFile = new File(balanceFolder, fileName);
        ArrayNode chargesFilePaths = processCharges(charges, addToBalanceFile.getAbsolutePath(), timestamp);
        ((ObjectNode) addToBalance).putArray("chargesFilePaths").addAll(chargesFilePaths);
        FileUtil.createFile(addToBalance, addToBalanceFile);
        createBalanceNotification(balanceFolder.getParentFile().getName(), addToBalance);
        if (returnFileName) {
            return "OK____" + fileName;
        }
        return "OK";
    }

    public static String substractToBalance(File balanceFolder, String currency, Double amount, BalanceOperationType balanceOperationType, BalanceOperationStatus balanceOperationStatus, String additionalInfo, Double btcPrice, boolean allowNegativeBalance, JsonNode charges, boolean returnFileName, ObjectNode additionals) {
        if (isLocked(balanceFolder)) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "{0}", "IS LOCKED");
            return "IS LOCKED";
        }
        try {
            lock(balanceFolder);
            Double initialAmount = amount;
            if (charges != null && amount > 0) {
                Iterator<JsonNode> chargesIterator = charges.iterator();
                while (chargesIterator.hasNext()) {
                    JsonNode chargesIt = chargesIterator.next();
                    if (chargesIt.get("currency").textValue().equals(currency)) {
                        amount = amount + chargesIt.get("amount").doubleValue();
                    } else if (chargesIt.get("currency").textValue().equals("BTC") && btcPrice != null) {
                        amount = amount + chargesIt.get("amount").doubleValue() * btcPrice;
                    }
                }
            }
            Double diff = 0.0;
            boolean enoughBalance = false;
            if (!allowNegativeBalance) {
                ArrayNode balance = getBalance(balanceFolder);
                Iterator<JsonNode> balanceIterator = balance.elements();
                while (balanceIterator.hasNext()) {
                    JsonNode balanceIt = balanceIterator.next();
                    if (!balanceIt.has("currency")) {
                        continue;
                    }
                    if (!balanceIt.has("amount")) {
                        continue;
                    }
                    if (balanceIt.get("currency").textValue().equals(currency)) {
                        Double newAmount = amount;
                        if ((currency.equals("BTC") || currency.equals("ETH"))) {
                            newAmount = newAmount - 0.00000001;
                        } else {
                            newAmount = newAmount - 0.01;
                        }
                        if (balanceIt.get("amount").doubleValue() >= newAmount) {
                            enoughBalance = true;
                            diff = balanceIt.get("amount").doubleValue() - amount;
                            break;
                        }
                        diff = balanceIt.get("amount").doubleValue() - amount;
                    }
                }
            } else {
                enoughBalance = true;
            }
            if (enoughBalance) {
                ObjectMapper mapper = new ObjectMapper();
                String timestamp = DateUtil.getFileDate(null);
                JsonNode substractToBalance = mapper.createObjectNode();
                ObjectNode amountNode = mapper.createObjectNode();
                amountNode.put("initialAmount", initialAmount);
                amountNode.put("amount", amount);
                amountNode.put("currency", currency);
                ((ObjectNode) substractToBalance).put("timestamp", timestamp);
                ((ObjectNode) substractToBalance).set("substractedAmount", amountNode);
                ((ObjectNode) substractToBalance).put("balanceOperationType", balanceOperationType.name());
                ((ObjectNode) substractToBalance).put("balanceOperationStatus", balanceOperationStatus.toString());
                if (balanceOperationStatus.equals(BalanceOperationStatus.PROCESSING)) {
                    ((ObjectNode) substractToBalance).put("balanceOperationProcessId", getId());
                }
                if (additionalInfo != null) {
                    ((ObjectNode) substractToBalance).put("additionalInfo", additionalInfo);
                }
                if (btcPrice != null) {
                    ((ObjectNode) substractToBalance).put("btcPrice", btcPrice);
                }
                if (additionals != null) {
                    ((ObjectNode) substractToBalance).putAll(additionals);
                }
                String fileName = "substract__" + timestamp + "__1" + ".json";
                File substractToBalanceFile = new File(balanceFolder, fileName);
                ArrayNode chargesFilePaths = processCharges(charges, substractToBalanceFile.getAbsolutePath(), timestamp);
                ((ObjectNode) substractToBalance).putArray("chargesFilePaths").addAll(chargesFilePaths);
                FileUtil.createFile(substractToBalance, balanceFolder, fileName);
                if (balanceOperationStatus.equals(BalanceOperationStatus.PROCESSING) && balanceOperationType.equals(BalanceOperationType.SEND_OUT)) {
                    ((ObjectNode) substractToBalance).put("userName", balanceFolder.getParentFile().getName());
                    ((ObjectNode) substractToBalance).put("processSendOutIn48Hours", processSendOutIn48Hours(balanceFolder.getParentFile().getName(), amount));
                    FileUtil.createFile(substractToBalance, BaseFilesLocator.getProcessingBalanceFolder(), fileName);
                }
                if (balanceOperationStatus.equals(BalanceOperationStatus.PROCESSING) && balanceOperationType.equals(BalanceOperationType.DEBIT_CARD_SUBSTRACT_BALANCE)) {
                    ((ObjectNode) substractToBalance).put("userName", balanceFolder.getParentFile().getName());
                    FileUtil.createFile(substractToBalance, BaseFilesLocator.getProcessingBalanceFolder(), fileName);
                }
                createBalanceNotification(balanceFolder.getParentFile().getName(), substractToBalance);
                if (returnFileName) {
                    Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "{0}", "OK____" + fileName);
                    return "OK____" + fileName;
                }
                Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "{0}", "OK");
                return "OK";
            }
            Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "DIFF {0}", diff);
            Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "{0}", "DOES NOT HAVE ENOUGH BALANCE");
            return "DOES NOT HAVE ENOUGH BALANCE";
        } finally {
            unlock(balanceFolder);
        }
    }

    public static JsonNode getCharges(String currency, Double amount, BalanceOperationType balanceOperationType, PaymentType paymentType, String target, Double balance) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode charges = mapper.createObjectNode();
        File otcCurrencyChargesFile = OTCFolderLocator.getCurrencyChargesFile(null, currency);
        //if (balanceOperationType.equals(BalanceOperationType.SEND_IN) || balanceOperationType.equals(BalanceOperationType.SEND_OUT) || balanceOperationType.equals(BalanceOperationType.RECEIVE_IN) || balanceOperationType.equals(BalanceOperationType.RECEIVE_OUT)) {
        //otcCurrencyChargesFile = OTCFolderLocator.getCurrencyChargesFile(null, "BTC");
        //}
        if (!otcCurrencyChargesFile.isFile()) {
            return charges;
        }
        // DOLLARBTC, MONEYCLICK, OPERATOR__MASTER_ACCOUNT__OTC_VES_USD, OPERATOR__PAYMENT__DFKDSFKKRO43MKDF, RETAIL__FASDFM4MFDFKFK43
        String chargesFolderPath = null;
        if (target != null) {
            chargesFolderPath = FileUtil.createFolderIfNoExist(new File(new File(ExchangeUtil.OPERATOR_PATH, "Dollarbtc"), "Charges")).getAbsolutePath();
            if (target.contains("MONEYCLICK")) {
                chargesFolderPath = FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getFolder(), "Charges")).getAbsolutePath();
            }
            if (target.contains("OPERATOR")) {
                chargesFolderPath = FileUtil.createFolderIfNoExist(new File(FileUtil.createFolderIfNoExist(new File(ExchangeUtil.OPERATOR_PATH, "Charges")), target.replace("OPERATOR__", ""))).getAbsolutePath();
            }
            if (target.contains("RETAIL")) {
                chargesFolderPath = FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getRetailFolder(target.split("__")[1]), "Charges")).getAbsolutePath();
            }
        }
        try {
            JsonNode otcCurrencyCharges = mapper.readTree(otcCurrencyChargesFile);
            if (balance != null) {
                if ((currency.equals("BTC") || currency.equals("ETH"))) {
                    balance = balance - 0.00000001;
                } else {
                    balance = balance - 0.01;
                }
            }
            for (ChargeType chargeType : ChargeType.values()) {
                if (otcCurrencyCharges.has(balanceOperationType.name() + "__" + chargeType.name())) {
                    JsonNode charge = mapper.createObjectNode();
                    ((ObjectNode) charge).put("currency", otcCurrencyCharges.get(balanceOperationType.name() + "__" + chargeType.name()).get("currency").textValue());
                    if (ChargeAmountType.valueOf(otcCurrencyCharges.get(balanceOperationType.name() + "__" + chargeType.name()).get("type").textValue()).equals(ChargeAmountType.ABSOLUTE)) {
                        Double chargeAmount = otcCurrencyCharges.get(balanceOperationType.name() + "__" + chargeType.name()).get("amount").doubleValue();
                        ((ObjectNode) charge).put("amount", chargeAmount);
                        if (balance != null) {
                            Double maxOperationAmount = balance - chargeAmount;
                            if (maxOperationAmount < 0.0) {
                                maxOperationAmount = 0.0;
                            }
                            ((ObjectNode) charge).put("maxOperationAmount", maxOperationAmount);
                            ((ObjectNode) charge).put("maxOperationAmountCharge", chargeAmount);
                        }
                    } else if (ChargeAmountType.valueOf(otcCurrencyCharges.get(balanceOperationType.name() + "__" + chargeType.name()).get("type").textValue()).equals(ChargeAmountType.PERCENT) && amount != null) {
                        Double chargeAmount = otcCurrencyCharges.get(balanceOperationType.name() + "__" + chargeType.name()).get("amount").doubleValue();
                        ((ObjectNode) charge).put("amount", chargeAmount * amount / 100);
                        if (balance != null) {
                            Double maxOperationAmount = balance / (1 + (chargeAmount / 100));
                            if (maxOperationAmount < 0.0) {
                                maxOperationAmount = 0.0;
                            }
                            ((ObjectNode) charge).put("maxOperationAmount", maxOperationAmount);
                            ((ObjectNode) charge).put("maxOperationAmountCharge", chargeAmount * balance / 100);
                        }
                    }
                    if (target != null) {
                        ((ObjectNode) charge).put("chargesFolderPath", chargesFolderPath);
                    }
                    ((ObjectNode) charges).set(chargeType.name(), charge);
                }
                if (paymentType != null && otcCurrencyCharges.has(balanceOperationType.name() + "__" + paymentType.name() + "__" + chargeType.name())) {
                    JsonNode charge = mapper.createObjectNode();
                    ((ObjectNode) charge).put("currency", otcCurrencyCharges.get(balanceOperationType.name() + "__" + paymentType.name() + "__" + chargeType.name()).get("currency").textValue());
                    if (ChargeAmountType.valueOf(otcCurrencyCharges.get(balanceOperationType.name() + "__" + paymentType.name() + "__" + chargeType.name()).get("type").textValue()).equals(ChargeAmountType.ABSOLUTE)) {
                        Double chargeAmount = otcCurrencyCharges.get(balanceOperationType.name() + "__" + paymentType.name() + "__" + chargeType.name()).get("amount").doubleValue();
                        ((ObjectNode) charge).put("amount", chargeAmount);
                        if (balance != null) {
                            Double maxOperationAmount = balance - chargeAmount;
                            if (maxOperationAmount < 0.0) {
                                maxOperationAmount = 0.0;
                            }
                            ((ObjectNode) charge).put("maxOperationAmount", maxOperationAmount);
                            ((ObjectNode) charge).put("maxOperationAmountCharge", chargeAmount);
                        }
                    } else if (ChargeAmountType.valueOf(otcCurrencyCharges.get(balanceOperationType.name() + "__" + paymentType.name() + "__" + chargeType.name()).get("type").textValue()).equals(ChargeAmountType.PERCENT) && amount != null) {
                        Double chargeAmount = otcCurrencyCharges.get(balanceOperationType.name() + "__" + paymentType.name() + "__" + chargeType.name()).get("amount").doubleValue();
                        ((ObjectNode) charge).put("amount", chargeAmount * amount / 100);
                        if (balance != null) {
                            Double maxOperationAmount = balance / (1 + (chargeAmount / 100));
                            if (maxOperationAmount < 0.0) {
                                maxOperationAmount = 0.0;
                            }
                            ((ObjectNode) charge).put("maxOperationAmount", maxOperationAmount);
                            ((ObjectNode) charge).put("maxOperationAmountCharge", chargeAmount * balance / 100);
                        }
                    }
                    if (target != null) {
                        ((ObjectNode) charge).put("chargesFolderPath", chargesFolderPath);
                    }
                    ((ObjectNode) charges).set(chargeType.name(), charge);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return charges;
    }

    public static void moveCharges(File baseChargesFolder, File targetChargesFolder) {
        if (baseChargesFolder.isDirectory()) {
            FileUtil.createFolderIfNoExist(targetChargesFolder);
            for (File baseChargeFile : baseChargesFolder.listFiles()) {
                if (!baseChargeFile.isFile()) {
                    continue;
                }
                FileUtil.moveFileToFolder(baseChargeFile, targetChargesFolder);
            }
            FileUtil.deleteFolder(baseChargesFolder);
        }
    }

    public static boolean websocketKeyAlreadyExist(String websocketKey) {
        File websocketUsedKeysFolder = WebsocketFolderLocator.getUsedKeysFolder();
        return new File(websocketUsedKeysFolder, websocketKey + ".json").isFile();
    }

    public static void createWebsocketKeyFile(String websocketKey) {
        if (!websocketKeyAlreadyExist(websocketKey)) {
            File websocketUsedKeysFolder = WebsocketFolderLocator.getUsedKeysFolder();
            ObjectNode websocketUsedKey = new ObjectMapper().createObjectNode();
            websocketUsedKey.put("timestamp", DateUtil.getCurrentDate());
            websocketUsedKey.put("websocketKey", websocketKey);
            FileUtil.createFile(websocketUsedKey, new File(websocketUsedKeysFolder, websocketKey + ".json"));
        }
    }

    public static void lock(File balanceFolder) {
        LOCKED_BALANCE_FOLDERS.put(balanceFolder, Boolean.TRUE);
        /*ObjectMapper mapper = new ObjectMapper();
        String timestamp = DateUtil.getCurrentDate();
        ObjectNode lock = mapper.createObjectNode();
        lock.put("timestamp", timestamp);
        FileUtil.createFile(lock, new File(balanceFolder, "lock.json"));*/

    }

    public static void unlock(File balanceFolder) {
        LOCKED_BALANCE_FOLDERS.put(balanceFolder, Boolean.FALSE);
        //FileUtil.deleteFile(new File(balanceFolder, "lock.json"));
    }

    public static boolean isLocked(File balanceFolder) {
        if(!LOCKED_BALANCE_FOLDERS.containsKey(balanceFolder)){
            return Boolean.FALSE;
        }
        return LOCKED_BALANCE_FOLDERS.get(balanceFolder);
        //return new File(balanceFolder, "lock.json").isFile();
    }

    public static ArrayNode getOperators() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode operators = (ArrayNode) mapper.readTree(BaseFilesLocator.getOperatorsFile());
            Iterator<JsonNode> operatorsIterator = operators.iterator();
            while (operatorsIterator.hasNext()) {
                JsonNode operatorsIt = operatorsIterator.next();
                if (operatorsIt.textValue().equals(OPERATOR_NAME)) {
                    operatorsIterator.remove();
                }
            }
            operators.add(OPERATOR_NAME);
            return operators;
        } catch (IOException ex) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapper.createArrayNode();
    }

    public static String inLimits(String userName, String currency, Double amount, BalanceOperationType balanceOperationType) {
        ObjectMapper mapper = new ObjectMapper();
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        try {
            JsonNode userConfig = mapper.readTree(userConfigFile);
            UserType userConfigType;
            if (userConfig.has("type")) {
                userConfigType = UserType.valueOf(userConfig.get("type").textValue());
            } else {
                userConfigType = UserType.NORMAL;
            }
            File otcCurrencyLimitsFile = OTCFolderLocator.getCurrencyLimitsFile(null, currency);
            if (!otcCurrencyLimitsFile.isFile()) {
                return "OK";
            }
            JsonNode otcCurrencyLimits = mapper.readTree(otcCurrencyLimitsFile);
            JsonNode otcCurrencyLimitsDayly = otcCurrencyLimits.get("dayly");
            if (!otcCurrencyLimitsDayly.has(userConfigType.name().toLowerCase())) {
                return "OK";
            }
            JsonNode otcCurrencyLimitsDaylyUserType = otcCurrencyLimitsDayly.get(userConfigType.name().toLowerCase());
            if (!otcCurrencyLimitsDaylyUserType.has(balanceOperationType.name())) {
                return "OK";
            }
            Double otcCurrencyLimitsDaylyUserTypeBalanceOperationTypeLimit = otcCurrencyLimitsDaylyUserType.get(balanceOperationType.name()).doubleValue();
            List<BalanceOperationStatus> balanceOperationStatuses = new ArrayList<>();
            balanceOperationStatuses.add(BalanceOperationStatus.OK);
            balanceOperationStatuses.add(BalanceOperationStatus.PROCESSING);
            File userBalanceFolder = UsersFolderLocator.getBalanceFolder(userName);
            switch (balanceOperationType) {
                case MC_SEND_SMS_NATIONAL:
                case MC_SEND_SMS_INTERNATIONAL:
                case MC_RETAIL_SELL_BALANCE:
                case MC_RETAIL_BUY_BALANCE:
                case MC_FAST_CHANGE:
                case MC_BUY_BALANCE:
                case MC_AUTOMATIC_CHANGE:
                case MC_ADD_ESCROW:
                case MC_SUBSTRACT_ESCROW:
                case TRANSFER_FROM_MCBALANCE_TO_BALANCE:
                    userBalanceFolder = UsersFolderLocator.getMCBalanceFolder(userName);
                    break;
            }
            Iterator<JsonNode> balanceMovementsIterator = getBalanceMovements(userBalanceFolder, DateUtil.getDayStartDate(null), null, balanceOperationType, currency, balanceOperationStatuses).iterator();
            Double totalOperationsAmount = amount;
            Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "{0}", amount);
            while (balanceMovementsIterator.hasNext()) {
                JsonNode balanceMovementsIt = balanceMovementsIterator.next();
//                if (balanceMovementsIt.has("addedAmount")) {
//                    totalOperationsAmount = totalOperationsAmount + balanceMovementsIt.get("addedAmount").get("amount").doubleValue();
//                }
                if (balanceMovementsIt.has("substractedAmount")) {
                    Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "{0}", balanceMovementsIt.get("substractedAmount").get("amount").doubleValue());
                    totalOperationsAmount = totalOperationsAmount + balanceMovementsIt.get("substractedAmount").get("amount").doubleValue();
                }
            }
            Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "totalOperationsAmount {0}", totalOperationsAmount);
            Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "otcCurrencyLimitsDaylyUserTypeBalanceOperationTypeLimit {0}", otcCurrencyLimitsDaylyUserTypeBalanceOperationTypeLimit);
            if (totalOperationsAmount >= otcCurrencyLimitsDaylyUserTypeBalanceOperationTypeLimit) {
                Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "USER DAYLY LIMIT REACHED");
                return "USER DAYLY LIMIT REACHED";
            }
            totalOperationsAmount = amount;
            JsonNode otcCurrencyLimitsMonthly = otcCurrencyLimits.get("monthly");
            if (!otcCurrencyLimitsMonthly.has(userConfigType.name().toLowerCase())) {
                return "OK";
            }
            JsonNode otcCurrencyLimitsMonthlyUserType = otcCurrencyLimitsMonthly.get(userConfigType.name().toLowerCase());
            if (!otcCurrencyLimitsMonthlyUserType.has(balanceOperationType.name())) {
                return "OK";
            }
            Double otcCurrencyLimitsMonthlyUserTypeOperationTypeLimit = otcCurrencyLimitsMonthlyUserType.get(balanceOperationType.name()).doubleValue();
            balanceMovementsIterator = getBalanceMovements(userBalanceFolder, DateUtil.getMonthStartDate(null), null, balanceOperationType, currency, balanceOperationStatuses).iterator();
            while (balanceMovementsIterator.hasNext()) {
                JsonNode balanceMovementsIt = balanceMovementsIterator.next();
                if (balanceMovementsIt.has("addedAmount")) {
                    totalOperationsAmount = totalOperationsAmount + balanceMovementsIt.get("addedAmount").get("amount").doubleValue();
                }
                if (balanceMovementsIt.has("substractedAmount")) {
                    totalOperationsAmount = totalOperationsAmount + balanceMovementsIt.get("substractedAmount").get("amount").doubleValue();
                }
            }
            if (totalOperationsAmount >= otcCurrencyLimitsMonthlyUserTypeOperationTypeLimit) {
                Logger.getLogger(BaseOperation.class.getName()).log(Level.INFO, "USER MONTHLY LIMIT REACHED");
                return "USER MONTHLY LIMIT REACHED";
            }
            return "OK";
        } catch (IOException ex) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "FAIL";
    }

    public static boolean containsBalanceOperationStatus(File balanceOperationFolder, String containsText, String containsField) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            for (File balanceOperationFile : balanceOperationFolder.listFiles()) {
                if (!balanceOperationFile.isFile()) {
                    continue;
                }
                JsonNode balanceOperation = mapper.readTree(balanceOperationFile);
                if (balanceOperation.has(containsField) && !balanceOperation.get(containsField).textValue().contains(containsText)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void changeBalanceOperationStatus(File balanceOperationFileOrFolder, BalanceOperationStatus balanceOperationStatus, String containsText, String containsField, String canceledReason) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (balanceOperationFileOrFolder.isFile()) {
                JsonNode balanceOperation = mapper.readTree(balanceOperationFileOrFolder);
                ((ObjectNode) balanceOperation).put("balanceOperationStatus", balanceOperationStatus.name());
                if (canceledReason != null) {
                    ((ObjectNode) balanceOperation).put("canceledReason", canceledReason);
                }
                FileUtil.editFile(balanceOperation, balanceOperationFileOrFolder);
                if (balanceOperationStatus.equals(BalanceOperationStatus.FAIL)) {
                    if (balanceOperation.has("chargesFilePaths")) {
                        Iterator<JsonNode> balanceOperationChargesFilePathsIterator = balanceOperation.get("chargesFilePaths").iterator();
                        while (balanceOperationChargesFilePathsIterator.hasNext()) {
                            JsonNode balanceOperationChargesFilePathsIt = balanceOperationChargesFilePathsIterator.next();
                            File chargeFile = new File(balanceOperationChargesFilePathsIt.textValue());
                            JsonNode charge = mapper.readTree(chargeFile);
                            ((ObjectNode) charge).put("status", "FAIL");
                            FileUtil.editFile(charge, chargeFile);
                        }
                    }
                }
            }
            if (balanceOperationFileOrFolder.isDirectory() && containsText != null && containsField != null) {
                for (File balanceOperationFile : balanceOperationFileOrFolder.listFiles()) {
                    if (!balanceOperationFile.isFile()) {
                        continue;
                    }
                    JsonNode balanceOperation = mapper.readTree(balanceOperationFile);
                    if (!balanceOperation.has(containsField) || !balanceOperation.get(containsField).textValue().contains(containsText)) {
                        continue;
                    }
                    ((ObjectNode) balanceOperation).put("balanceOperationStatus", balanceOperationStatus.name());
                    if (canceledReason != null) {
                        ((ObjectNode) balanceOperation).put("canceledReason", canceledReason);
                    }
                    FileUtil.editFile(balanceOperation, balanceOperationFile);
                    if (balanceOperation.has("chargesFilePaths")) {
                        Iterator<JsonNode> balanceOperationChargesFilePathsIterator = balanceOperation.get("chargesFilePaths").iterator();
                        while (balanceOperationChargesFilePathsIterator.hasNext()) {
                            JsonNode balanceOperationChargesFilePathsIt = balanceOperationChargesFilePathsIterator.next();
                            File chargeFile = new File(balanceOperationChargesFilePathsIt.textValue());
                            if (!chargeFile.isFile()) {
                                continue;
                            }
                            JsonNode charge = mapper.readTree(chargeFile);
                            ((ObjectNode) charge).put("status", "FAIL");
                            FileUtil.editFile(charge, chargeFile);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static CloseableHttpClient getCloseableHttpClient(SSLContext sslContext) {
        if (sslContext != null) {
            return HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .build();
        }
        return HttpClientBuilder.create().disableAutomaticRetries().build();
    }

    public static String requestGet(CloseableHttpClient httpClient, String url, String proxyUrl) throws IOException {
        String result;
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        httpGet.setConfig(getRequestConfig(proxyUrl));
        try ( CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
            HttpEntity entity = httpResponse.getEntity();
            result = IOUtils.toString(entity.getContent(), "utf8");
            httpGet.releaseConnection();
        }

        return result;
    }

    public static RequestConfig getRequestConfig(String proxyUrl) {
        if (proxyUrl == null || proxyUrl.equals("")) {
            return RequestConfig.custom().setSocketTimeout(180000).setConnectTimeout(60000).setConnectionRequestTimeout(180000).build();
        }
        String[] proxyParams = new String[3];
        proxyParams[0] = proxyUrl.split("://")[0];
        proxyParams[1] = proxyUrl.split("://")[1].substring(0, proxyUrl.split("://")[1].indexOf(":"));
        proxyParams[2] = proxyUrl.split("://")[1].substring(proxyUrl.split("://")[1].indexOf(":") + 1);
        HttpHost proxy = new HttpHost(proxyParams[1], Integer.parseInt(proxyParams[2]), proxyParams[0]);
        return RequestConfig.custom().setProxy(proxy).setSocketTimeout(180000).setConnectTimeout(60000).setConnectionRequestTimeout(180000).build();
    }

    public static ArrayNode processCharges(JsonNode charges, String balanceFilePath, String timestamp) {
        ArrayNode chargesFilePaths = new ObjectMapper().createArrayNode();
        if (charges != null) {
            Iterator<String> chargesFieldNamesIterator = charges.fieldNames();
            while (chargesFieldNamesIterator.hasNext()) {
                String chargesFieldNamesIt = chargesFieldNamesIterator.next();
                String chargeType = "COMMISSION";
                if (chargesFieldNamesIt.equals("VAT")) {
                    chargeType = "VAT";
                }
                String currency = charges.get(chargesFieldNamesIt).get("currency").textValue();
                Double amount = charges.get(chargesFieldNamesIt).get("amount").doubleValue();
                String chargesFolderPath = charges.get(chargesFieldNamesIt).get("chargesFolderPath").textValue();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode charge = mapper.createObjectNode();
                ((ObjectNode) charge).put("currency", currency);
                ((ObjectNode) charge).put("amount", amount);
                ((ObjectNode) charge).put("timestamp", timestamp);
                ((ObjectNode) charge).put("balanceFilePath", balanceFilePath);
                ((ObjectNode) charge).put("type", chargeType);
                ((ObjectNode) charge).put("status", "OK");
                int i = 1;
                File chargeFile;
                while (true) {
                    chargeFile = new File(chargesFolderPath, timestamp + "__" + i + ".json");
                    if (!chargeFile.isFile()) {
                        break;
                    }
                    i++;
                }
                FileUtil.createFile(charge, chargeFile);
                chargesFilePaths.add(chargeFile.getAbsolutePath());
            }
        }
        return chargesFilePaths;
    }

    public static void createOperationInCentralFolder(JsonNode operation, String operatorName) {
        String id = operation.get("id").textValue();
        File otcOperationsFolder = OTCFolderLocator.getOperationsFolder(operatorName);
        File otcOperationIdFolder = FileUtil.createFolderIfNoExist(otcOperationsFolder, id);
        File otcOperationIdMessagesFolder = FileUtil.createFolderIfNoExist(otcOperationIdFolder, "Messages");
        File otcOperationIdMessagesUserFolder = FileUtil.createFolderIfNoExist(otcOperationIdMessagesFolder, "User");
        File otcOperationIdMessagesAdminFolder = FileUtil.createFolderIfNoExist(otcOperationIdMessagesFolder, "Admin");
        FileUtil.createFolderIfNoExist(otcOperationIdMessagesUserFolder, "Old");
        FileUtil.createFolderIfNoExist(otcOperationIdMessagesAdminFolder, "Old");
        FileUtil.createFile(operation, new File(otcOperationIdFolder, "operation.json"));
    }

    public static void createIndexesInCentralFolder(JsonNode operation, String operatorName) {
        ObjectNode indexOperation = new ObjectMapper().createObjectNode();
        String id = operation.get("id").textValue();
        indexOperation.put("id", id);
        indexOperation.put("timestamp", operation.get("timestamp").textValue());
        //UserName index
        if (operation.has("userName")) {
            String userName = operation.get("userName").textValue();
            File otcOperationsIndexesUserNameFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(operatorName, "UserNames"), userName);
            FileUtil.createFile(indexOperation, new File(otcOperationsIndexesUserNameFolder, id + ".json"));
        }
        //BrokerUserName index
        if (operation.has("brokerUserName")) {
            String brokerUserName = operation.get("brokerUserName").textValue();
            File otcOperationsIndexesBrokerUserNameFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(operatorName, "BrokerUserNames"), brokerUserName);
            FileUtil.createFile(indexOperation, new File(otcOperationsIndexesBrokerUserNameFolder, id + ".json"));
        }
        //Currency index
        if (operation.has("currency")) {
            String currency = operation.get("currency").textValue();
            File otcOperationsIndexesCurrencyFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(operatorName, "Currencies"), currency);
            FileUtil.createFile(indexOperation, new File(otcOperationsIndexesCurrencyFolder, id + ".json"));
        }
        //OTCOperationType index
        if (operation.has("otcOperationType")) {
            String otcOperationType = operation.get("otcOperationType").textValue();
            File otcOperationsIndexesOTCOperationTypeFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(operatorName, "Types"), otcOperationType);
            FileUtil.createFile(indexOperation, new File(otcOperationsIndexesOTCOperationTypeFolder, id + ".json"));
        }
        //OTCOperationStatus index
        if (operation.has("otcOperationStatus")) {
            String otcOperationStatus = operation.get("otcOperationStatus").textValue();
            File otcOperationsIndexesOTCOperationStatusFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(operatorName, "Statuses"), otcOperationStatus);
            FileUtil.createFile(indexOperation, new File(otcOperationsIndexesOTCOperationStatusFolder, id + ".json"));
        }
        //Timestamp index
        if (operation.has("timestamp")) {
            String timestamp = operation.get("timestamp").textValue();
            File otcOperationsIndexesTimestampFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(operatorName, "Timestamps"), DateUtil.getFileDate(timestamp));
            FileUtil.createFile(indexOperation, new File(otcOperationsIndexesTimestampFolder, id + ".json"));
        }
        //OTCMasterAccountName index
        if (operation.has("otcMasterAccountName")) {
            String otcMasterAccountName = operation.get("otcMasterAccountName").textValue();
            File otcOperationsIndexesOTCMasterAccountNameFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(operatorName, "OTCMasterAccountName"), otcMasterAccountName);
            FileUtil.createFile(indexOperation, new File(otcOperationsIndexesOTCMasterAccountNameFolder, id + ".json"));
        }
        //Special index - BANK
        if (operation.has("dollarBTCPayment")) {
            JsonNode payment = operation.get("dollarBTCPayment");
            if (payment.has("bank")) {
                String bank = payment.get("bank").textValue();
                File otcOperationsIndexesBankFolder = FileUtil.createFolderIfNoExist(OTCFolderLocator.getOperationsIndexesSpecificFolder(operatorName, "Banks"), bank);
                FileUtil.createFile(indexOperation, new File(otcOperationsIndexesBankFolder, id + ".json"));
            }
        }
    }

    public static String automaticPaymentBalanceOperation(JsonNode sourcePayment, JsonNode targetPayment, Double amount, OTCOperationType otcOperationType) {
        PaymentBank paymentBank = PaymentBank.valueOf(sourcePayment.get("bank").textValue());
        switch (paymentBank) {
            case BANCAMIGA:
                TransferirOperation transferirOperation = new TransferirOperation(sourcePayment.get("accountHolderId").textValue(), sourcePayment.get("accountNumber").textValue(), targetPayment.get("accountHolderId").textValue(), targetPayment.get("accountNumber").textValue(), "API", amount.toString());
                JsonNode response = transferirOperation.getResponse();
                if (!response.has("R") || !response.get("R").booleanValue()) {
                    return "FAIL";
                }
                break;
        }
        return "OK";
    }

    public static String postOperationMessage(String id, String userName, String message, OperationMessageSide operationMessageSide, String attachmentFileName) {
        ObjectMapper mapper = new ObjectMapper();
        String timestamp = DateUtil.getCurrentDate();
        String fileName = DateUtil.getFileDate(timestamp) + ".json";
        ObjectNode messageObject = mapper.createObjectNode();
        messageObject.put("id", id);
        messageObject.put("timestamp", timestamp);
        messageObject.put("userName", userName);
        messageObject.put("message", message.replace("_", " "));
        if (attachmentFileName != null && !attachmentFileName.equals("")) {
            messageObject.put("attachment", attachmentFileName);
        }
        FileUtil.createFile(messageObject, new File(OTCFolderLocator.getOperationIdMessagesSideFolder(null, id, "User"), fileName));
        FileUtil.createFile(messageObject, new File(OTCFolderLocator.getOperationIdMessagesSideFolder(null, id, "Admin"), fileName));
        switch (operationMessageSide) {
            case USER:
            case BOTH:
                FileUtil.createFile(messageObject, new File(AdminFolderLocator.getOperationMessagesFolder(), fileName));
                break;
        }
        return "OK";
    }

    public static String[] getSecurityImageUrlAndName() {
        List<String> givenList = Arrays.asList("oso__bear", "toro__bull", "gato__cat", "perro__dog", "jirafa__giraffe",
                "caballo__horse", "mono__monkey", "loro__parrot", "ballena__whale", "cebra__zebra");
        String randomElement = givenList.get(new Random().nextInt(givenList.size()));
        return new String[]{
            "https://attachment.dollarbtc.com/securityImages/" + randomElement.split("__")[1] + ".png",
            randomElement.split("__")[0], randomElement.split("__")[1]};
    }

    public static void addFieldsToRetail(JsonNode retail, ObjectMapper mapper) {
        // add escrow balances
        Map<String, Double> escrowBalances = new HashMap<>();
        File[] moneyclickRetailEscrowBalanceFolders = new File[]{MoneyclickFolderLocator.getRetailEscrowBalanceFolder(retail.get("id").textValue()), MoneyclickFolderLocator.getRetailEscrowBalanceFromToUserFolder(retail.get("id").textValue())};
        for (File moneyclickRetailEscrowBalanceFolder : moneyclickRetailEscrowBalanceFolders) {
            ArrayNode balance = BaseOperation.getBalance(moneyclickRetailEscrowBalanceFolder);
            Iterator<JsonNode> balanceIterator = balance.elements();
            while (balanceIterator.hasNext()) {
                JsonNode balanceIt = balanceIterator.next();
                if (!escrowBalances.containsKey(balanceIt.get("currency").textValue())) {
                    escrowBalances.put(balanceIt.get("currency").textValue(), 0.0);
                }
                escrowBalances.put(balanceIt.get("currency").textValue(), escrowBalances.get(balanceIt.get("currency").textValue()) + balanceIt.get("amount").doubleValue());
            }
        }
        ((ObjectNode) retail).set("escrowBalances", mapper.valueToTree(escrowBalances));
    }

    public static void addFieldsToRetailOperation(JsonNode retailOperation, ObjectMapper mapper) {
        MCRetailOperationType mcRetailOperationType = MCRetailOperationType.valueOf(retailOperation.get("type").textValue());
        try {
            File moneyclickConfigFile = MoneyclickFolderLocator.getConfigFile();
            JsonNode moneyclickConfig = mapper.readTree(moneyclickConfigFile);
            if (moneyclickConfig.has(mcRetailOperationType.name()) && moneyclickConfig.get(mcRetailOperationType.name()).has(retailOperation.get("currency").textValue())) {
                JsonNode moneyclickConfigOperationTypeCurrency = moneyclickConfig.get(mcRetailOperationType.name()).get(retailOperation.get("currency").textValue());
                ((ObjectNode) retailOperation).put("bottomLimit", moneyclickConfigOperationTypeCurrency.get("bottomLimit").doubleValue());
                ((ObjectNode) retailOperation).put("topLimit", moneyclickConfigOperationTypeCurrency.get("topLimit").doubleValue());
            }
        } catch (IOException ex) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void putBuyOperationMinutesLeft(JsonNode otcOperation) {
        if (!(otcOperation.has("otcOperationType")
                && otcOperation.has("otcOperationStatus")
                && (OTCOperationType.valueOf(otcOperation.get("otcOperationType").textValue()).equals(OTCOperationType.BUY) || OTCOperationType.valueOf(otcOperation.get("otcOperationType").textValue()).equals(OTCOperationType.MC_BUY_BALANCE))
                && OTCOperationStatus.valueOf(otcOperation.get("otcOperationStatus").textValue()).equals(OTCOperationStatus.WAITING_FOR_PAYMENT))) {
            return;
        }
        String otcOperationIdCurrency = otcOperation.get("currency").textValue();
        String otcOperationIdDollarBTCPaymentId = otcOperation.get("dollarBTCPayment").get("id").textValue();
        if (!otcOperation.get("dollarBTCPayment").has("type")) {
            return;
        }
        PaymentType otcOperationIdDollarBTCPaymentType = PaymentType.valueOf(otcOperation.get("dollarBTCPayment").get("type").textValue());
        JsonNode otcOperationIdDollarBTCPayment = new OTCGetDollarBTCPayment(otcOperationIdCurrency, otcOperationIdDollarBTCPaymentId).getResponse();
        if (otcOperationIdDollarBTCPayment != null && otcOperationIdDollarBTCPayment.has("types")) {
            Iterator<JsonNode> otcOperationIdDollarBTCPaymentTypesIterator = otcOperationIdDollarBTCPayment.get("types").iterator();
            while (otcOperationIdDollarBTCPaymentTypesIterator.hasNext()) {
                JsonNode otcOperationIdDollarBTCPaymentTypesIt = otcOperationIdDollarBTCPaymentTypesIterator.next();
                PaymentType otcOperationIdDollarBTCPaymentTypeIt = PaymentType.valueOf(otcOperationIdDollarBTCPaymentTypesIt.get("name").textValue());
                if (otcOperationIdDollarBTCPaymentTypeIt.equals(otcOperationIdDollarBTCPaymentType)) {
                    String payWindow = otcOperationIdDollarBTCPaymentTypesIt.get("payWindow").textValue();
                    Integer payWindowInMinutes = null;
                    if (payWindow.contains("minutes")) {
                        payWindow = payWindow.replace("minutes", "").trim();
                        payWindowInMinutes = Integer.parseInt(payWindow);
                    } else if (payWindow.contains("hours")) {
                        payWindow = payWindow.replace("hours", "").trim();
                        payWindowInMinutes = Integer.parseInt(payWindow) * 60;
                    }
                    if (payWindowInMinutes != null) {
                        Date currentDateMinusMinutes = DateUtil.parseDate(DateUtil.getDateMinutesBefore(DateUtil.getCurrentDate(), payWindowInMinutes));
                        Date otcOperationDate = DateUtil.parseDate(otcOperation.get("timestamp").textValue());
                        Long millisecondsLeft = otcOperationDate.getTime() - currentDateMinusMinutes.getTime();
                        if (millisecondsLeft < 0) {
                            millisecondsLeft = 0L;
                        }
                        ((ObjectNode) otcOperation).put("minutesLeft", millisecondsLeft / (1000 * 60));
                        break;
                    }
                    break;
                }
            }
        }
    }

    public static boolean processSendOutIn48Hours(String userName, Double amount) {
        List<BalanceOperationType> balanceOperationTypes = new ArrayList<>();
        balanceOperationTypes.add(BalanceOperationType.RECEIVE_OUT);
        balanceOperationTypes.add(BalanceOperationType.SEND_OUT);
        JsonNode balanceMovements = new ObjectMapper().createObjectNode();
        for (BalanceOperationType balanceOperationType : balanceOperationTypes) {
            List<BalanceOperationStatus> balanceOperationStatuses = new ArrayList<>();
            balanceOperationStatuses.add(BalanceOperationStatus.OK);
            if (balanceOperationType.equals(BalanceOperationType.SEND_OUT)) {
                balanceOperationStatuses.add(BalanceOperationStatus.PROCESSING);
            }
            File[] userBalanceFolders = new File[]{
                UsersFolderLocator.getBalanceFolder(userName),
                UsersFolderLocator.getMCBalanceFolder(userName)
            };
            for (File userBalanceFolder : userBalanceFolders) {
                ((ObjectNode) balanceMovements).putAll((ObjectNode) BaseOperation.getBalanceMovements(
                        userBalanceFolder,
                        null,
                        null,
                        balanceOperationType,
                        "BTC",
                        balanceOperationStatuses));
            }
        }
        Iterator<JsonNode> balanceMovementsIterator = balanceMovements.iterator();
        Double userMCBalanceMovement = 0.0;
        while (balanceMovementsIterator.hasNext()) {
            JsonNode balanceMovementsIt = balanceMovementsIterator.next();
            if (balanceMovementsIt.has("addedAmount")) {
                userMCBalanceMovement = userMCBalanceMovement + balanceMovementsIt.get("addedAmount").get("amount").doubleValue();
            } else if (balanceMovementsIt.has("substractedAmount")) {
                userMCBalanceMovement = userMCBalanceMovement - balanceMovementsIt.get("substractedAmount").get("amount").doubleValue();
            }
        }
        return userMCBalanceMovement <= amount;
    }

    private static void createBalanceNotification(String userName, JsonNode balanceMovement) {
        File userBalanceNotificationsFile = UsersFolderLocator.getBalanceNotificationsFile(userName);
        if (!userBalanceNotificationsFile.isFile()) {
            return;
        }
        try {
            JsonNode userBalanceNotifications = new ObjectMapper().readTree(userBalanceNotificationsFile);
            if (!balanceMovement.has("clientId")) {
                return;
            }
            String clientId = balanceMovement.get("clientId").textValue();
            File userBalanceNotificationFile = new File(UsersFolderLocator.getBalanceNotificationsFolder(userName), clientId + ".json");
            if ((balanceMovement.has("addedAmount")
                    && userBalanceNotifications.has("add")
                    && userBalanceNotifications.get("add").booleanValue())
                    || (balanceMovement.has("substractedAmount")
                    && userBalanceNotifications.has("substract")
                    && userBalanceNotifications.get("substract").booleanValue())) {
                FileUtil.createFile(balanceMovement, userBalanceNotificationFile);
            }
        } catch (IOException ex) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JsonNode getChargesNew(String currency, Double amount, BalanceOperationType balanceOperationType, PaymentType paymentType, String target, Double balance, String targetCurrency) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode chargesNew = mapper.createObjectNode();
        File otcCurrencyChargesNewFile = OTCFolderLocator.getCurrencyChargesNewFile(null, currency);
//        if (balanceOperationType.equals(BalanceOperationType.SEND_IN) || balanceOperationType.equals(BalanceOperationType.SEND_OUT) || balanceOperationType.equals(BalanceOperationType.RECEIVE_IN) || balanceOperationType.equals(BalanceOperationType.RECEIVE_OUT)) {
//            otcCurrencyChargesNewFile = OTCFolderLocator.getCurrencyChargesNewFile(null, "BTC");
//        }
        if (!otcCurrencyChargesNewFile.isFile()) {
            return chargesNew;
        }
        // DOLLARBTC, MONEYCLICK, OPERATOR__MASTER_ACCOUNT__OTC_VES_USD, OPERATOR__PAYMENT__DFKDSFKKRO43MKDF, RETAIL__FASDFM4MFDFKFK43
        String chargesFolderPath = null;
        if (target != null) {
            chargesFolderPath = FileUtil.createFolderIfNoExist(new File(new File(ExchangeUtil.OPERATOR_PATH, "Dollarbtc"), "Charges")).getAbsolutePath();
            if (target.contains("MONEYCLICK")) {
                chargesFolderPath = FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getFolder(), "Charges")).getAbsolutePath();
            }
            if (target.contains("OPERATOR")) {
                chargesFolderPath = FileUtil.createFolderIfNoExist(new File(FileUtil.createFolderIfNoExist(new File(ExchangeUtil.OPERATOR_PATH, "Charges")), target.replace("OPERATOR__", ""))).getAbsolutePath();
            }
            if (target.contains("RETAIL")) {
                chargesFolderPath = FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getRetailFolder(target.split("__")[1]), "Charges")).getAbsolutePath();
            }
        }
        try {
            JsonNode otcCurrencyChargesNew = mapper.readTree(otcCurrencyChargesNewFile);
            if (balance != null) {
                if ((currency.equals("BTC") || currency.equals("ETH"))) {
                    balance = balance - 0.00000001;
                } else {
                    balance = balance - 0.01;
                }
            }
            for (ChargeType chargeType : ChargeType.values()) {
                JsonNode chargeNew = mapper.createObjectNode();
                JsonNode chargeBase = null;
                if (otcCurrencyChargesNew.has(balanceOperationType.name() + "__" + chargeType.name())) {
                    chargeBase = otcCurrencyChargesNew.get(balanceOperationType.name() + "__" + chargeType.name());
                }
                if (paymentType != null && otcCurrencyChargesNew.has(balanceOperationType.name() + "__" + paymentType.name() + "__" + chargeType.name())) {
                    chargeBase = otcCurrencyChargesNew.get(balanceOperationType.name() + "__" + paymentType.name() + "__" + chargeType.name());
                }
                if (chargeBase == null) {
                    continue;
                }
                // USE DEFAULT
                JsonNode chargeBaseOption = chargeBase.get("DEFAULT");
                ((ObjectNode) chargeNew).put("currency", chargeBaseOption.get("currency").textValue());
                if (chargeBaseOption.has("range")) {
                    if (amount <= chargeBaseOption.get("range").get(0).doubleValue() || amount > chargeBaseOption.get("range").get(1).doubleValue()) {
                        continue;
                    }
                }
                if (ChargeAmountType.valueOf(chargeBaseOption.get("type").textValue()).equals(ChargeAmountType.ABSOLUTE)) {
                    Double chargeAmount = chargeBaseOption.get("amount").doubleValue();
                    ((ObjectNode) chargeNew).put("amount", chargeAmount);
                    if (balance != null) {
                        Double maxOperationAmount = balance - chargeAmount;
                        if (maxOperationAmount < 0.0) {
                            maxOperationAmount = 0.0;
                        }
                        ((ObjectNode) chargeNew).put("maxOperationAmount", maxOperationAmount);
                        ((ObjectNode) chargeNew).put("maxOperationAmountCharge", chargeAmount);
                    }
                } else if (ChargeAmountType.valueOf(chargeBaseOption.get("type").textValue()).equals(ChargeAmountType.PERCENT) && amount != null) {
                    Double chargeAmount = chargeBaseOption.get("amount").doubleValue();
                    ((ObjectNode) chargeNew).put("amount", chargeAmount * amount / 100);
                    if (balance != null) {
                        Double maxOperationAmount = balance / (1 + (chargeAmount / 100));
                        if (maxOperationAmount < 0.0) {
                            maxOperationAmount = 0.0;
                        }
                        ((ObjectNode) chargeNew).put("maxOperationAmount", maxOperationAmount);
                        ((ObjectNode) chargeNew).put("maxOperationAmountCharge", chargeAmount * balance / 100);
                    }
                } else if (ChargeAmountType.valueOf(chargeBaseOption.get("type").textValue()).equals(ChargeAmountType.COMBINE) && amount != null) {
                    Double combineChargeAmount = (chargeBaseOption.get("percent").doubleValue() * amount / 100) + chargeBaseOption.get("absolute").doubleValue();
                    ((ObjectNode) chargeNew).put("amount", combineChargeAmount);
                    if (balance != null) {
                        Double maxOperationAmount = balance - (chargeBaseOption.get("percent").doubleValue() * balance / 100) - chargeBaseOption.get("absolute").doubleValue();
                        if (maxOperationAmount < 0.0) {
                            maxOperationAmount = 0.0;
                        }
                        ((ObjectNode) chargeNew).put("maxOperationAmount", maxOperationAmount);
                        ((ObjectNode) chargeNew).put("maxOperationAmountCharge", (chargeBaseOption.get("percent").doubleValue() * balance / 100) + chargeBaseOption.get("absolute").doubleValue());
                    }
                }
                if (target != null) {
                    ((ObjectNode) chargeNew).put("chargesFolderPath", chargesFolderPath);
                }
                ((ObjectNode) chargesNew).set(chargeType.name(), chargeNew);
                ((ObjectNode) chargeBase).remove("DEFAULT");
                // OVERRIDE VALUES
                Iterator<String> chargeBaseIterator = chargeBase.fieldNames();
                while (chargeBaseIterator.hasNext()) {
                    String option = chargeBaseIterator.next();
                    if (targetCurrency != null && !targetCurrency.equals(option)) {
                        continue;
                    }
                    chargeBaseOption = chargeBase.get(option);
                    ((ObjectNode) chargeNew).put("currency", chargeBaseOption.get("currency").textValue());
                    if (chargeBaseOption.has("range")) {
                        if (amount <= chargeBaseOption.get("range").get(0).doubleValue() || amount > chargeBaseOption.get("range").get(1).doubleValue()) {
                            continue;
                        }
                    }
                    if (ChargeAmountType.valueOf(chargeBaseOption.get("type").textValue()).equals(ChargeAmountType.ABSOLUTE)) {
                        Double chargeAmount = chargeBaseOption.get("amount").doubleValue();
                        ((ObjectNode) chargeNew).put("amount", chargeAmount);
                        if (balance != null) {
                            Double maxOperationAmount = balance - chargeAmount;
                            if (maxOperationAmount < 0.0) {
                                maxOperationAmount = 0.0;
                            }
                            ((ObjectNode) chargeNew).put("maxOperationAmount", maxOperationAmount);
                            ((ObjectNode) chargeNew).put("maxOperationAmountCharge", chargeAmount);
                        }
                    } else if (ChargeAmountType.valueOf(chargeBaseOption.get("type").textValue()).equals(ChargeAmountType.PERCENT) && amount != null) {
                        Double chargeAmount = chargeBaseOption.get("amount").doubleValue();
                        ((ObjectNode) chargeNew).put("amount", chargeAmount * amount / 100);
                        if (balance != null) {
                            Double maxOperationAmount = balance / (1 + (chargeAmount / 100));
                            if (maxOperationAmount < 0.0) {
                                maxOperationAmount = 0.0;
                            }
                            ((ObjectNode) chargeNew).put("maxOperationAmount", maxOperationAmount);
                            ((ObjectNode) chargeNew).put("maxOperationAmountCharge", chargeAmount * balance / 100);
                        }
                    }
                    if (target != null) {
                        ((ObjectNode) chargeNew).put("chargesFolderPath", chargesFolderPath);
                    }
                    ((ObjectNode) chargesNew).set(chargeType.name(), chargeNew);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BaseOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chargesNew;
    }

//    private static JsonNode subscribeSellToPayment(JsonNode sellOperation) {
//        if (sellOperation.has("dollarBTCPayment")) {
//            return sellOperation;
//        }
//        if (!sellOperation.get("clientPayment").has("automaticCharge") || !sellOperation.get("clientPayment").get("automaticCharge").booleanValue()) {
//            return sellOperation;
//        }
//        String id = sellOperation.get("id").textValue();
//        String currency = sellOperation.get("currency").textValue();
//        double amount = sellOperation.get("amount").doubleValue();
//        ArrayNode dollarBTCPayments = (ArrayNode) getPayments("dollarBTC", currency);
//        Iterator<JsonNode> dollarBTCPaymentsIterator = dollarBTCPayments.iterator();
//        while (dollarBTCPaymentsIterator.hasNext()) {
//            JsonNode dollarBTCPaymentsIt = dollarBTCPaymentsIterator.next();
//            if (!dollarBTCPaymentsIt.get("active").booleanValue() || !dollarBTCPaymentsIt.get("acceptOut").booleanValue()) {
//                continue;
//            }
//            String paymentId = dollarBTCPaymentsIt.get("id").textValue();
//            String joinField = dollarBTCPaymentsIt.get("joinField").textValue();
//            String paymentJoinValue = sellOperation.get("clientPayment").get(joinField).textValue();
//            String dollarBTCPaymentJoinValue = dollarBTCPaymentsIt.get(joinField).textValue();
//            if (!paymentJoinValue.equals(dollarBTCPaymentJoinValue)) {
//                continue;
//            }
//            if (substractToBalance(getOTCCurrencyPaymentBalanceFolder(currency, paymentId), currency, amount, BalanceOperationType.DEBIT, BalanceOperationStatus.PROCESSING, "OTC operation id " + id).equals("OK")) {
//                ((ObjectNode) sellOperation).put("dollarBTCPayment", dollarBTCPaymentsIt);
//                return sellOperation;
//            }
//        }
//        return null;
//    }
}
