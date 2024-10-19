/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardMakePaymentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.sms.SMSSender;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DebitCardsFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class DebitCardMakePayment extends AbstractOperation<String> {

    private final DebitCardMakePaymentRequest debitCardMakePaymentRequest;

    public DebitCardMakePayment(DebitCardMakePaymentRequest debitCardMakePaymentRequest) {
        super(String.class);
        this.debitCardMakePaymentRequest = debitCardMakePaymentRequest;
    }

    @Override
    protected void execute() {
        File debitCardConfigFile = DebitCardsFolderLocator.getConfigFile(debitCardMakePaymentRequest.getId());
        File debitCardBalanceFolder = DebitCardsFolderLocator.getBalanceFolder(debitCardMakePaymentRequest.getId());
        if (!debitCardBalanceFolder.isDirectory() || !debitCardConfigFile.isFile()) {
            super.response = "DEBIT CARD DOES NOT EXIST";
            return;
        }
        if (debitCardMakePaymentRequest.getPinCode() == null || debitCardMakePaymentRequest.getPinCode().equals("")) {
            Double debitCardBalanceUSDAmount = 0.0;
            Iterator<JsonNode> debitCardBalanceIterator = BaseOperation.getBalance(debitCardBalanceFolder).iterator();
            while (debitCardBalanceIterator.hasNext()) {
                JsonNode debitCardBalanceIt = debitCardBalanceIterator.next();
                if (debitCardBalanceIt.get("currency").textValue().equals("USD")) {
                    debitCardBalanceUSDAmount = debitCardBalanceIt.get("amount").doubleValue();
                    break;
                }
            }
            if (debitCardBalanceUSDAmount < debitCardMakePaymentRequest.getAmount()) {
                super.response = "DEBIT CARD DOES NOT HAVE ENOUGH BALANCE";
                return;
            }
            try {
                JsonNode debitCardConfig = mapper.readTree(debitCardConfigFile);
                String pinCode = String.valueOf(pinGenerator(7));
                new SMSSender().publish("MC PIN code " + pinCode, new String[]{debitCardConfig.get("phoneNumber").textValue()});
                ((ObjectNode) debitCardConfig).put("pinCode", pinCode);
                FileUtil.editFile(debitCardConfig, debitCardConfigFile);
                super.response = "PIN CODE SENDED";
                return;
            } catch (IOException ex) {
                Logger.getLogger(DebitCardMakePayment.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                JsonNode debitCardConfig = mapper.readTree(debitCardConfigFile);
                if (!debitCardConfig.has("pinCode")) {
                    super.response = "THERE IS NO PIN CREATED TO THIS DEBIT CARD";
                    return;
                }
                if (!debitCardMakePaymentRequest.getPinCode().equals(debitCardConfig.get("pinCode").textValue())) {
                    super.response = "WRONG PIN CODE FOR DEBIT CARD";
                    return;
                }
                File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(debitCardMakePaymentRequest.getTargetUserName());
                if (!userMCBalanceFolder.isDirectory()) {
                    super.response = "TARGET USER DOES NOT EXIST";
                    return;
                }
                ObjectNode additionals = mapper.createObjectNode();
                String operationId = BaseOperation.getId().substring(0, 7);
                additionals.put("operationId", operationId);
                JsonNode charges = BaseOperation.getChargesNew(
                        "USD",
                        debitCardMakePaymentRequest.getAmount(),
                        BalanceOperationType.PURCHASE_WITH_DEBIT_CARD,
                        null,
                        "MONEYCLICK",
                        null,
                        null);
                String substract = BaseOperation.substractToBalance(
                        debitCardBalanceFolder,
                        "USD",
                        debitCardMakePaymentRequest.getAmount(),
                        BalanceOperationType.PURCHASE_WITH_DEBIT_CARD,
                        BalanceOperationStatus.OK,
                        null,
                        null,
                        false,
                        charges,
                        false,
                        additionals
                );
                if (!substract.equals("OK")) {
                    super.response = substract;
                    return;
                }
                BaseOperation.addToBalance(
                        userMCBalanceFolder,
                        "USD",
                        debitCardMakePaymentRequest.getAmount(),
                        BalanceOperationType.PURCHASE_WITH_DEBIT_CARD,
                        BalanceOperationStatus.OK,
                        null,
                        null,
                        null,
                        false,
                        additionals
                );
                ((ObjectNode) debitCardConfig).remove("pinCode");
                FileUtil.editFile(debitCardConfig, debitCardConfigFile);
                super.response = "OK";
                return;
            } catch (IOException ex) {
                Logger.getLogger(DebitCardMakePayment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = "FAIL";
    }

    private static char[] pinGenerator(int length) {
        Random random = new Random();
        char[] pin = new char[length];
        for (int i = 0; i < length; i++) {
            pin[i] = (char) (random.nextInt(10) + 48);
        }
        return pin;
    }

}
