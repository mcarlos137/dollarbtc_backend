/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otcadmin;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.address.AddressList;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.blockcypher.BlockcypherGetBalance;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.localbitcoins.LocalBitcoinsGetTickersAndUSDPrice;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetDollarBTCPaymentBalances;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BrokersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersAddressesFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCAdminUpdateClientsBalance extends AbstractOperation<Void> {

    private final String[] operationTypes;
    private final int blockchainRequestMaxQuantity;

    public OTCAdminUpdateClientsBalance(String[] operationTypes, int blockchainRequestMaxQuantity) {
        super(Void.class);
        this.operationTypes = operationTypes;
        this.blockchainRequestMaxQuantity = blockchainRequestMaxQuantity;
    }

    @Override
    protected void execute() {
        Map<String, Double[]> localBitcoinsPrices = getLocalBitcoinsPrices();
        Map<String, Double> totalBalance = new HashMap<>();
        totalBalance.put("BTC", 0.0);
        totalBalance.put("USD", 0.0);
        Map<String, Double[]> cryptoBalance = new HashMap<>();
        Map<String, Double[]> fiatBalance = new HashMap<>();
        Map<String, Double[]> moneyclickBalance = new HashMap<>();
        Set<String> currencies = (Set<String>) new OTCAdminGetCurrencies(null).getResponse();
        for (String operationType : operationTypes) {
            switch (operationType) {
                case "+MBB":
                    //
                    // MASTER WALLETS BLOCKCHAIN BALANCES
                    //
                    getMasterWalletsBlockchainBalances(totalBalance, cryptoBalance);
                    Logger.getLogger(OTCAdminUpdateClientsBalance.class.getName()).log(Level.INFO, "---------------------------------------- +MBB {0} ----------------------------------------", totalBalance);
                    break;
                case "+OBB":
                    //
                    // OPERATORS BLOCKCHAIN BALANCES
                    //
                    getOperatorsBlockchainBalances();
                    break;
                case "+CBB":
                    //
                    // CLIENTS BLOCKCHAIN BALANCES
                    //
                    getClientsBlockchainBalances(mapper, totalBalance, cryptoBalance, blockchainRequestMaxQuantity);
                    break;
                case "-CB":
                    //
                    // CLIENTS BALANCES
                    //
                    getClientsBalances(totalBalance, cryptoBalance, fiatBalance, localBitcoinsPrices);
                    break;
                case "+PB":
                    //
                    // PAYMENTS BALANCES
                    //
                    getPaymentsBalances(currencies, totalBalance, fiatBalance, localBitcoinsPrices);
                    break;
                case "-BB":
                    //
                    // BROKERS BALANCES
                    //
                    getBrokersBalances(totalBalance, fiatBalance, localBitcoinsPrices);
                    break;
                case "+MB":
                    //
                    // MONEYCLICK BALANCE
                    //
                    getMoneyclickBalances(totalBalance, cryptoBalance, fiatBalance, localBitcoinsPrices, moneyclickBalance);
                    break;
            }
            Logger.getLogger(OTCAdminUpdateClientsBalance.class.getName()).log(Level.INFO, "---------------------------------------- {0} - {1} ----------------------------------------", new Object[]{operationType, totalBalance});
        }
        //
        // CREATE JSONNODE
        //
        JsonNode updateOperationBalance = mapper.createObjectNode();
        JsonNode btcBalance = mapper.createObjectNode();
        ((ObjectNode) btcBalance).put("totalBalance", totalBalance.get("BTC"));
        ((ObjectNode) btcBalance).put("idealBalance", 0.0);
        ArrayNode btcBalanceValues = mapper.createArrayNode();
        for (String currency : cryptoBalance.keySet()) {
            JsonNode btcBalanceValue = mapper.createObjectNode();
            ((ObjectNode) btcBalanceValue).put("currency", currency);
            ((ObjectNode) btcBalanceValue).put("balance", cryptoBalance.get(currency)[0]);
            ((ObjectNode) btcBalanceValue).put("btcBalance", cryptoBalance.get(currency)[1]);
            btcBalanceValues.add(btcBalanceValue);
        }
        ((ObjectNode) btcBalance).putArray("values").addAll(btcBalanceValues);
        ((ObjectNode) updateOperationBalance).set("BTC", btcBalance);
        JsonNode usdBalance = mapper.createObjectNode();
        ((ObjectNode) usdBalance).put("totalBalance", totalBalance.get("USD"));
        ((ObjectNode) usdBalance).put("idealBalance", 0.0);
        ArrayNode usdBalanceValues = mapper.createArrayNode();
        for (String currency : fiatBalance.keySet()) {
            JsonNode usdBalanceValue = mapper.createObjectNode();
            ((ObjectNode) usdBalanceValue).put("currency", currency);
            ((ObjectNode) usdBalanceValue).put("balance", fiatBalance.get(currency)[0]);
            ((ObjectNode) usdBalanceValue).put("usdBalance", fiatBalance.get(currency)[1]);
            usdBalanceValues.add(usdBalanceValue);
        }
        ((ObjectNode) usdBalance).putArray("values").addAll(usdBalanceValues);
        ((ObjectNode) updateOperationBalance).set("USD", usdBalance);
        JsonNode mcBalance = mapper.createObjectNode();
        ArrayNode moneyclickBalanceValues = mapper.createArrayNode();
        for (String key : moneyclickBalance.keySet()) {
            JsonNode moneyclickBalanceValue = mapper.createObjectNode();
            if (key.contains("__")) {
                ((ObjectNode) moneyclickBalanceValue).put("currency", key.split("__")[0]);
                ((ObjectNode) moneyclickBalanceValue).put("operator", key.split("__")[1]);
            } else {
                ((ObjectNode) moneyclickBalanceValue).put("currency", key);
            }
            ((ObjectNode) moneyclickBalanceValue).put("balance", moneyclickBalance.get(key)[0]);
            ((ObjectNode) moneyclickBalanceValue).put("usdBalance", moneyclickBalance.get(key)[1]);
            moneyclickBalanceValues.add(moneyclickBalanceValue);
        }
        ((ObjectNode) mcBalance).putArray("values").addAll(moneyclickBalanceValues);
        ((ObjectNode) updateOperationBalance).set("MONEYCLICK", mcBalance);
        File clientsBalanceFile = BaseFilesLocator.getClientsBalanceFile();
        FileUtil.editFile(updateOperationBalance, clientsBalanceFile);

    }

    private static Map<String, Double[]> getLocalBitcoinsPrices() {
        Map<String, Double[]> localBitcoinsPrices = new HashMap<>();
        Iterator<JsonNode> tickersAndUSDPriceIterator = new LocalBitcoinsGetTickersAndUSDPrice().getResponse().iterator();
        while (tickersAndUSDPriceIterator.hasNext()) {
            JsonNode tickersAndUSDPriceIt = tickersAndUSDPriceIterator.next();
            Logger.getLogger(OTCAdminUpdateClientsBalance.class.getName()).log(Level.INFO, tickersAndUSDPriceIt.toString());
            String tickersAndUSDPriceItCurrency = tickersAndUSDPriceIt.get("currency").textValue();
            Double tickersAndUSDPriceItBTCPrice = tickersAndUSDPriceIt.get("price").doubleValue();
            Double tickersAndUSDPriceItUSDPrice;
            if (tickersAndUSDPriceItCurrency.equals("USD")) {
                tickersAndUSDPriceItUSDPrice = 1.0;
            } else {
                tickersAndUSDPriceItUSDPrice = tickersAndUSDPriceIt.get("usdPrice").doubleValue();
            }
            localBitcoinsPrices.put(tickersAndUSDPriceItCurrency, new Double[]{tickersAndUSDPriceItBTCPrice, tickersAndUSDPriceItUSDPrice});
        }
        return localBitcoinsPrices;
    }

    private static JsonNode getUserAddressJsonNode(File userAddressFile, ObjectMapper mapper, int blockchainRequestQuantity, int blockchainRequestMaxQuantity) throws IOException {
        JsonNode userAddress = mapper.readTree(userAddressFile);
        String address = userAddress.get("address").textValue();
        String currentTimestamp = DateUtil.getCurrentDate();
        if ((!userAddress.has("lastUpdateBalanceTimestamp") || DateUtil.parseDate(userAddress.get("lastUpdateBalanceTimestamp").textValue()).before(DateUtil.parseDate(DateUtil.getDateDaysBefore(currentTimestamp, 1)))) && blockchainRequestQuantity < blockchainRequestMaxQuantity) {
            ((ObjectNode) userAddress).put("balanceAmount", new BlockcypherGetBalance(address).getResponse());
            ((ObjectNode) userAddress).put("lastUpdateBalanceTimestamp", currentTimestamp);
            FileUtil.editFile(userAddress, userAddressFile);
        }
        return userAddress;
    }

    private static void getMasterWalletsBlockchainBalances(Map<String, Double> totalBalance, Map<String, Double[]> cryptoBalance) {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add("BTC");
        for (String cryptoCurrency : cryptoCurrencies) {
            if (!cryptoCurrency.equals("BTC")) {
                continue;
            }
            Iterator<JsonNode> addressesIterator = new AddressList("BTC").getResponse().iterator();
            while (addressesIterator.hasNext()) {
                JsonNode addressesIt = addressesIterator.next();
                String address = addressesIt.get("address").textValue();
                Logger.getLogger(OTCAdminUpdateClientsBalance.class.getName()).log(Level.INFO, "real master address: {0}", address);
                Double amount = new BlockcypherGetBalance(address).getResponse();
                if (!cryptoBalance.containsKey(cryptoCurrency)) {
                    cryptoBalance.put(cryptoCurrency, new Double[]{0.0, 0.0});
                }
                cryptoBalance.get(cryptoCurrency)[0] = cryptoBalance.get(cryptoCurrency)[0] + amount;
                cryptoBalance.get(cryptoCurrency)[1] = cryptoBalance.get(cryptoCurrency)[1] + amount;
                totalBalance.put("BTC", totalBalance.get("BTC") + amount);
            }
        }
    }

    private static void getOperatorsBlockchainBalances() {

    }

    private static void getClientsBlockchainBalances(ObjectMapper mapper, Map<String, Double> totalBalance, Map<String, Double[]> cryptoBalance, int blockchainRequestMaxQuantity) {
        int blockchainRequestQuantity = 0;
        String[] types = new String[]{"DOLLARBTC", "MONEYCLICK"};
        for (String type : types) {
            if (type.equals("DOLLARBTC") || type.equals("MONEYCLICK")) {
                File usersAddressesFolder = UsersAddressesFolderLocator.getFolder();
                for (File userAddressFile : usersAddressesFolder.listFiles()) {
                    if (!userAddressFile.isFile()) {
                        continue;
                    }
                    try {
                        JsonNode userAddress = getUserAddressJsonNode(userAddressFile, mapper, blockchainRequestQuantity, blockchainRequestMaxQuantity);
                        String address = userAddress.get("address").textValue();

                        String addressType = "DOLLARBTC";
                        if (userAddress.has("type")) {
                            addressType = userAddress.get("type").textValue();
                        }
                        if (!type.equals(addressType)) {
                            continue;
                        }
                        Logger.getLogger(OTCAdminUpdateClientsBalance.class.getName()).log(Level.INFO, "REAL CLIENT ADDRESS: {0}", address);
                        if (!userAddress.has("balanceAmount")) {
                            continue;
                        }
                        Double balanceAmount = userAddress.get("balanceAmount").doubleValue();
                        if (!cryptoBalance.containsKey("BTC")) {
                            cryptoBalance.put("BTC", new Double[]{0.0, 0.0});
                        }
                        cryptoBalance.get("BTC")[0] = cryptoBalance.get("BTC")[0] + balanceAmount;
                        cryptoBalance.get("BTC")[1] = cryptoBalance.get("BTC")[1] + balanceAmount;
                        totalBalance.put("BTC", totalBalance.get("BTC") + balanceAmount);
                    } catch (IOException ex) {
                        Logger.getLogger(OTCAdminUpdateClientsBalance.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private static void getClientsBalances(Map<String, Double> totalBalance, Map<String, Double[]> cryptoBalance, Map<String, Double[]> fiatBalance, Map<String, Double[]> localBitcoinsPrices) {
        String[] types = new String[]{"DOLLARBTC", "MONEYCLICK"};
        for (String type : types) {
            if (type.equals("DOLLARBTC") || type.equals("MONEYCLICK")) {
                File usersFolder = UsersFolderLocator.getFolder();
                for (File userFolder : usersFolder.listFiles()) {
                    if (!userFolder.isDirectory()) {
                        continue;
                    }
                    Logger.getLogger(OTCAdminUpdateClientsBalance.class.getName()).log(Level.INFO, userFolder.getName());
                    File[] userBalanceFolders = new File[]{UsersFolderLocator.getBalanceFolder(userFolder.getName())};
                    if (type.equals("MONEYCLICK")) {
                        userBalanceFolders = new File[]{UsersFolderLocator.getMCBalanceFolder(userFolder.getName())};
                    }
                    for (File userBalanceFolder : userBalanceFolders) {
                        if (!userBalanceFolder.isDirectory()) {
                            continue;
                        }
                        Iterator<JsonNode> userBalanceIterator = BaseOperation.getBalance(userBalanceFolder).iterator();
                        while (userBalanceIterator.hasNext()) {
                            JsonNode userBalanceIt = userBalanceIterator.next();
                            String userBalanceItCurrency = userBalanceIt.get("currency").textValue();
                            if (userBalanceItCurrency.equals("ETH") || userBalanceItCurrency.equals("USDT")) {
                                continue;
                            }
                            Double userBalanceItAmount = userBalanceIt.get("amount").doubleValue();
                            Map<String, Double[]> specificBalance = fiatBalance;
                            if (userBalanceItCurrency.equals("BTC")) {
                                specificBalance = cryptoBalance;
                                if (!specificBalance.containsKey(userBalanceItCurrency)) {
                                    specificBalance.put(userBalanceItCurrency, new Double[]{0.0, 0.0});
                                }
                                specificBalance.get(userBalanceItCurrency)[0] = specificBalance.get(userBalanceItCurrency)[0] - userBalanceItAmount;
                                specificBalance.get(userBalanceItCurrency)[1] = specificBalance.get(userBalanceItCurrency)[1] - userBalanceItAmount;
                                totalBalance.put("BTC", totalBalance.get("BTC") - userBalanceItAmount);
                            } else {
                                if (!specificBalance.containsKey(userBalanceItCurrency)) {
                                    specificBalance.put(userBalanceItCurrency, new Double[]{0.0, 0.0});
                                }
                                specificBalance.get(userBalanceItCurrency)[0] = specificBalance.get(userBalanceItCurrency)[0] - userBalanceItAmount;
                                if (!userBalanceItCurrency.equals("USD")) {
                                    userBalanceItAmount = userBalanceItAmount / localBitcoinsPrices.get(userBalanceItCurrency)[1];
                                }
                                specificBalance.get(userBalanceItCurrency)[1] = specificBalance.get(userBalanceItCurrency)[1] - userBalanceItAmount;
                                totalBalance.put("USD", totalBalance.get("USD") - userBalanceItAmount);
                                if (type.equals("DOLLARBTC")) {
                                    Logger.getLogger(OTCAdminUpdateClientsBalance.class.getName()).log(Level.INFO, "???????????");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void getPaymentsBalances(Set<String> currencies, Map<String, Double> totalBalance, Map<String, Double[]> fiatBalance, Map<String, Double[]> localBitcoinsPrices) {
        for (String currency : currencies) {
            JsonNode dollarBTCPaymentBalances = new OTCGetDollarBTCPaymentBalances(currency, null, null).getResponse();
            Iterator<JsonNode> dollarBTCPaymentBalancesIterator = dollarBTCPaymentBalances.iterator();
            while (dollarBTCPaymentBalancesIterator.hasNext()) {
                JsonNode dollarBTCPaymentBalancesIt = dollarBTCPaymentBalancesIterator.next();
                Iterator<JsonNode> dollarBTCPaymentBalancesItBalanceIterator = dollarBTCPaymentBalancesIt.get("balance").iterator();
                while (dollarBTCPaymentBalancesItBalanceIterator.hasNext()) {
                    JsonNode dollarBTCPaymentBalancesItBalanceIt = dollarBTCPaymentBalancesItBalanceIterator.next();
                    String dollarBTCPaymentBalancesItBalanceItCurrency = dollarBTCPaymentBalancesItBalanceIt.get("currency").textValue();
                    Double dollarBTCPaymentBalancesItBalanceItAmount = dollarBTCPaymentBalancesItBalanceIt.get("amount").doubleValue();
                    if (!fiatBalance.containsKey(dollarBTCPaymentBalancesItBalanceItCurrency)) {
                        fiatBalance.put(dollarBTCPaymentBalancesItBalanceItCurrency, new Double[]{0.0, 0.0});
                    }
                    fiatBalance.get(dollarBTCPaymentBalancesItBalanceItCurrency)[0] = fiatBalance.get(dollarBTCPaymentBalancesItBalanceItCurrency)[0] + dollarBTCPaymentBalancesItBalanceItAmount;
                    if (!dollarBTCPaymentBalancesItBalanceItCurrency.equals("USD")) {
                        dollarBTCPaymentBalancesItBalanceItAmount = dollarBTCPaymentBalancesItBalanceItAmount / localBitcoinsPrices.get(dollarBTCPaymentBalancesItBalanceItCurrency)[1];
                    }
                    fiatBalance.get(dollarBTCPaymentBalancesItBalanceItCurrency)[1] = fiatBalance.get(dollarBTCPaymentBalancesItBalanceItCurrency)[1] + dollarBTCPaymentBalancesItBalanceItAmount;
                    totalBalance.put("USD", totalBalance.get("USD") + dollarBTCPaymentBalancesItBalanceItAmount);
                }
            }
        }
    }

    private static void getBrokersBalances(Map<String, Double> totalBalance, Map<String, Double[]> fiatBalance, Map<String, Double[]> localBitcoinsPrices) {
        File brokersFolder = BrokersFolderLocator.getFolder();
        for (File brokerFolder : brokersFolder.listFiles()) {
            if (!brokerFolder.isDirectory()) {
                continue;
            }
            File brokerBalanceFolder = new File(brokerFolder, "Balance");
            Iterator<JsonNode> brokerBalanceIterator = BaseOperation.getBalance(brokerBalanceFolder).iterator();
            while (brokerBalanceIterator.hasNext()) {
                JsonNode brokerBalanceIt = brokerBalanceIterator.next();
                String brokerBalanceItCurrency = brokerBalanceIt.get("currency").textValue();
                Double brokerBalanceItAmount = brokerBalanceIt.get("amount").doubleValue();
                if (!fiatBalance.containsKey(brokerBalanceItCurrency)) {
                    fiatBalance.put(brokerBalanceItCurrency, new Double[]{0.0, 0.0});
                }
                fiatBalance.get(brokerBalanceItCurrency)[0] = fiatBalance.get(brokerBalanceItCurrency)[0] - brokerBalanceItAmount;
                if (!brokerBalanceItCurrency.equals("USD")) {
                    brokerBalanceItAmount = brokerBalanceItAmount / localBitcoinsPrices.get(brokerBalanceItCurrency)[1];
                }
                fiatBalance.get(brokerBalanceItCurrency)[1] = fiatBalance.get(brokerBalanceItCurrency)[1] - brokerBalanceItAmount;
                totalBalance.put("USD", totalBalance.get("USD") - brokerBalanceItAmount);
            }
        }
    }

    private static void getMoneyclickBalances(Map<String, Double> totalBalance, Map<String, Double[]> cryptoBalance, Map<String, Double[]> fiatBalance, Map<String, Double[]> localBitcoinsPrices, Map<String, Double[]> moneyclickBalance) {
        File moneyclickBalanceFolder = MoneyclickFolderLocator.getBalanceFolder();
        // BUY BALANCE, SEND TO PAYMENT
        for (File moneyclickOperatorBalanceFolder : moneyclickBalanceFolder.listFiles()) {
            if (!OPERATOR_NAME.equals("MAIN") && !moneyclickOperatorBalanceFolder.getName().equals(OPERATOR_NAME)) {
                continue;
            }
            Iterator<JsonNode> moneyclickBalanceIterator = BaseOperation.getBalance(moneyclickOperatorBalanceFolder).iterator();
            while (moneyclickBalanceIterator.hasNext()) {
                JsonNode moneyclickBalanceIt = moneyclickBalanceIterator.next();
                String currency = moneyclickBalanceIt.get("currency").textValue();
                Double amount = moneyclickBalanceIt.get("amount").doubleValue();
                if (OPERATOR_NAME.equals("MAIN") && !moneyclickOperatorBalanceFolder.getName().equals("MAIN")) {
                    if (!moneyclickBalance.containsKey(currency)) {
                        moneyclickBalance.put(currency + "__" + moneyclickOperatorBalanceFolder.getName(), new Double[]{0.0, 0.0});
                    }
                    moneyclickBalance.get(currency + "__" + moneyclickOperatorBalanceFolder.getName())[0] = moneyclickBalance.get(currency + "__" + moneyclickOperatorBalanceFolder.getName())[0] + amount;
                    moneyclickBalance.get(currency + "__" + moneyclickOperatorBalanceFolder.getName())[1] = moneyclickBalance.get(currency + "__" + moneyclickOperatorBalanceFolder.getName())[1] + amount;
                    if (!currency.equals("USD")) {
                        moneyclickBalance.get(currency + "__" + moneyclickOperatorBalanceFolder.getName())[1] = moneyclickBalance.get(currency + "__" + moneyclickOperatorBalanceFolder.getName())[1] + amount / localBitcoinsPrices.get(currency)[1];
                    }
                } else if (!(OPERATOR_NAME.equals("MAIN") && moneyclickOperatorBalanceFolder.getName().equals("MAIN"))) {
                    if (!moneyclickBalance.containsKey(currency)) {
                        moneyclickBalance.put(currency, new Double[]{0.0, 0.0});
                    }
                    moneyclickBalance.get(currency)[0] = moneyclickBalance.get(currency)[0] - amount;
                    moneyclickBalance.get(currency)[1] = moneyclickBalance.get(currency)[1] - amount;
                    if (!currency.equals("USD")) {
                        moneyclickBalance.get(currency)[1] = moneyclickBalance.get(currency)[1] - amount / localBitcoinsPrices.get(currency)[1];
                    }
                    if (currency.equals("BTC")) {
                        cryptoBalance.get(currency)[0] = cryptoBalance.get(currency)[0] - amount;
                        cryptoBalance.get(currency)[1] = cryptoBalance.get(currency)[1] - amount;
                        totalBalance.put("BTC", totalBalance.get("BTC") - amount);
                    } else if (!currency.equals("USDT")) {
                        fiatBalance.get(currency)[0] = fiatBalance.get(currency)[0] - amount;
                        if (!currency.equals("USD")) {
                            amount = amount / localBitcoinsPrices.get(currency)[1];
                        }
                        fiatBalance.get(currency)[1] = fiatBalance.get(currency)[1] - amount;
                        totalBalance.put("USD", totalBalance.get("USD") - amount);
                    }
                }
            }
        }
    }

}
