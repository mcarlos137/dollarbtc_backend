/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCAddPaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.PlaidOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.dwolla.CreateCustomerFundingSourceOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.dwolla.CreateCustomerOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCAddPayment extends AbstractOperation<String> {

    private final OTCAddPaymentRequest otcAddPaymentRequest;

    public OTCAddPayment(OTCAddPaymentRequest otcAddPaymentRequest) {
        super(String.class);
        this.otcAddPaymentRequest = otcAddPaymentRequest;
    }

    @Override
    public void execute() {
        File userOTCFolder = UsersFolderLocator.getOTCFolder(otcAddPaymentRequest.getUserName());
        if (!userOTCFolder.isDirectory()) {
            super.response = "USERNAME DOES NOT EXIST";
            return;
        }
        File userOTCCurrencyFolder = FileUtil.createFolderIfNoExist(userOTCFolder, otcAddPaymentRequest.getCurrency());
        File userOTCCurrencyPaymentsFile = new File(userOTCCurrencyFolder, "payments.json");
        if (!userOTCCurrencyPaymentsFile.isFile()) {
            FileUtil.createFile(mapper.createArrayNode(), userOTCCurrencyPaymentsFile);
        }
        if (otcAddPaymentRequest.getCurrency().equals("USD") && false) {
            String dwollaCustomerId = null;
            String userFirstName = null;
            String userLastName = null;
            String userEmail;
            try {
                //create user if does not exist in DWOLLA
                File userConfigFile = UsersFolderLocator.getConfigFile(otcAddPaymentRequest.getUserName());
                JsonNode userConfig = mapper.readTree(userConfigFile);
                if (userConfig.has("dwollaCustomerId")) {
                    dwollaCustomerId = userConfig.get("dwollaCustomerId").textValue();
                } else {
                    if (!userConfig.has("firstName")
                            || !userConfig.has("lastName")
                            || !userConfig.has("email")
                            || userConfig.get("firstName").textValue() == null
                            || userConfig.get("lastName").textValue() == null
                            || userConfig.get("email").textValue() == null
                            || userConfig.get("firstName").textValue().equals("")
                            || userConfig.get("lastName").textValue().equals("")
                            || userConfig.get("email").textValue().equals("")) {
                        super.response = "USER DOES NOT HAVE ENOUGH DATA TO CREATE DWOLLA CUSTOMER";
                        return;
                    }
                    userFirstName = userConfig.get("firstName").textValue();
                    userLastName = userConfig.get("lastName").textValue();
                    userEmail = userConfig.get("email").textValue();
                    JsonNode dwollaCustomer = new CreateCustomerOperation(userFirstName, userLastName, userEmail, null, null, null).getResponse();
                    if (dwollaCustomer.has("response") && dwollaCustomer.get("response").textValue().equals("OK")) {
                        dwollaCustomerId = dwollaCustomer.get("location").textValue().replace("/customers/", "");
                        ((ObjectNode) userConfig).put("dwollaCustomerId", dwollaCustomerId);
                        FileUtil.editFile(userConfig, userConfigFile);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCAddPayment.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (dwollaCustomerId == null) {
                super.response = "FAILED WITH DWOLLA CREATING CUSTOMER";
                return;
            }
            if (otcAddPaymentRequest.getPayment().has("plaid_publicToken")) {
                String plaidAccessToken;
                String plaidProcessorToken = null;
                String accountId = otcAddPaymentRequest.getPayment().get("account_id").textValue();
                try {
                    String[] accessTokenAndItemId = new PlaidOperation().getAccessTokenAndItemId(otcAddPaymentRequest.getPayment().get("plaid_publicToken").textValue());
                    if (accessTokenAndItemId == null || accessTokenAndItemId.length != 2 || accessTokenAndItemId[0] == null || accessTokenAndItemId[1] == null) {
                        super.response = "FAILED WITH PLAID";
                        return;
                    }
                    plaidAccessToken = accessTokenAndItemId[0];
                    ((ObjectNode) otcAddPaymentRequest.getPayment()).put("plaid_accessToken", accessTokenAndItemId[0]);
                    ((ObjectNode) otcAddPaymentRequest.getPayment()).put("plaid_itemId", accessTokenAndItemId[1]);
                    if (otcAddPaymentRequest.getPayment().has("balances")) {
                        ((ObjectNode) otcAddPaymentRequest.getPayment()).remove("balances");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(OTCAddPayment.class.getName()).log(Level.SEVERE, null, ex);
                    super.response = "FAILED WITH PLAID";
                    return;
                }
                try {
                    plaidProcessorToken = new PlaidOperation().getProcessorToken(plaidAccessToken, accountId);
                } catch (IOException ex) {
                    Logger.getLogger(OTCAddPayment.class.getName()).log(Level.SEVERE, null, ex);
                }
                Logger.getLogger(OTCAddPayment.class.getName()).log(Level.INFO, "plaidProcessorToken {0}", plaidProcessorToken);
                if (plaidProcessorToken != null && accountId != null) {
                    ((ObjectNode) otcAddPaymentRequest.getPayment()).put("plaid_processorToken", plaidProcessorToken);
                    try {
                        JsonNode dwollaCustomerFundingSource = new CreateCustomerFundingSourceOperation(dwollaCustomerId, plaidProcessorToken, DateUtil.getCurrentDate()).getResponse();
                        Logger.getLogger(OTCAddPayment.class.getName()).log(Level.INFO, "dwolla_customerFundingSource {0}", dwollaCustomerFundingSource);
                        if (dwollaCustomerFundingSource.has("response") && dwollaCustomerFundingSource.get("response").textValue().equals("OK")) {
                            String dwollaCustomerFundingSourceId = dwollaCustomerFundingSource.get("location").textValue().replace("/funding-sources/", "");
                            ((ObjectNode) otcAddPaymentRequest.getPayment()).put("dwolla_customerFundingSource", dwollaCustomerFundingSourceId);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(OTCAddPayment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
//                try {
//                    JsonNode dwollaCustomerFundingSource = new CreateCustomerFundingSourceOperation(dwollaCustomerId, "", "", "", DateUtil.getCurrentDate()).getResponse();
//                    Logger.getLogger(OTCOperation.class.getName()).log(Level.INFO, "dwollaCustomerFundingSource " + dwollaCustomerFundingSource);
//                    if (dwollaCustomerFundingSource.has("response") && dwollaCustomerFundingSource.get("response").textValue().equals("OK")) {
//                        String dwollaCustomerFundingSourceId = dwollaCustomerFundingSource.get("location").textValue().replace("/funding-sources/", "");
//                        ((ObjectNode) otcAddPaymentRequest.getPayment()).put("dwollaCustomerFundingSourceId", dwollaCustomerFundingSourceId);
//                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(OTCOperation.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }
        try {
            ArrayNode userOTCCurrencyPayments = (ArrayNode) mapper.readTree(userOTCCurrencyPaymentsFile);
            String id = BaseOperation.getId();
            ((ObjectNode) otcAddPaymentRequest.getPayment()).put("id", id);
            ((ObjectNode) otcAddPaymentRequest.getPayment()).put("currency", otcAddPaymentRequest.getCurrency());
            userOTCCurrencyPayments.add(otcAddPaymentRequest.getPayment());
            FileUtil.editFile(userOTCCurrencyPayments, userOTCCurrencyPaymentsFile);
            super.response = id;
            return;
        } catch (IOException ex) {
            Logger.getLogger(OTCAddPayment.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
