/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.TransferToBanksFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class TransferToBankGetProcessFile extends AbstractOperation<File> {

    private final String userName, id;
    private final String type; //TDBANK

    public TransferToBankGetProcessFile(String userName, String id, String type) {
        super(File.class);
        this.userName = userName;
        this.id = id;
        this.type = type;
    }

    @Override
    protected void execute() {
        File transferToBanksProcessFile = new File(TransferToBanksFolderLocator.getFolder(), id + ".json");
        if (!transferToBanksProcessFile.isFile()) {
            super.response = TransferToBanksFolderLocator.getEmptyCSVFile();
            return;
        }
        File transferToBanksCSVFile = TransferToBanksFolderLocator.getCSVFile(id);
        if (transferToBanksCSVFile.isFile()) {
            super.response = transferToBanksCSVFile;
            return;
        }
        try {
            FileWriter fileWriter = new FileWriter(transferToBanksCSVFile.getAbsolutePath());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            JsonNode transferToBanksProcess = mapper.readTree(transferToBanksProcessFile);
            String currency = transferToBanksProcess.get("currency").textValue();
            String currencyType = currency + "__" + type;
            Iterator<JsonNode> transferToBanksProcessOperationsIterator = transferToBanksProcess.get("operations").iterator();
            while (transferToBanksProcessOperationsIterator.hasNext()) {
                JsonNode transferToBanksProcessOperationsIt = transferToBanksProcessOperationsIterator.next();
                if (!transferToBanksProcessOperationsIt.has("clientPayment")) {
                    continue;
                }
                if (!transferToBanksProcessOperationsIt.get("currency").textValue().equals(currency)) {
                    continue;
                }
                JsonNode clientPayment = transferToBanksProcessOperationsIt.get("clientPayment");
                String bankRoutingNumber = clientPayment.get("bankRoutingNumber").textValue();
                String accountNumber = clientPayment.get("accountNumber").textValue();
                Double amount = transferToBanksProcessOperationsIt.get("amount").doubleValue();
                String operationUserName = transferToBanksProcessOperationsIt.get("userName").textValue();
                switch (currencyType) {
                    case "USD__TDBANK":
                        StringBuilder csv = new StringBuilder("22,");
                        csv.append(bankRoutingNumber);
                        csv.append(",");
                        csv.append(accountNumber);
                        csv.append(",");
                        csv.append(amount);
                        csv.append(",");
                        csv.append("");
                        csv.append(",");
                        csv.append(operationUserName);
                        bufferedWriter.write(csv.toString());
                        bufferedWriter.newLine();
                        System.out.println("csv.toString(): " + csv.toString());
                        break;
                }
            }
            bufferedWriter.close();
            fileWriter.close();
            super.response = TransferToBanksFolderLocator.getCSVFile(id);
            return;
        } catch (IOException ex) {
            Logger.getLogger(TransferToBankGetProcessFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = TransferToBanksFolderLocator.getEmptyCSVFile();
    }

}
