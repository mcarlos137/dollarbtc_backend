/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewProcessOperationRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCRetailNewProcessOperation extends AbstractOperation<String> {

    private final MCRetailNewProcessOperationRequest mcRetailNewProcessOperationRequest;

    public MCRetailNewProcessOperation(MCRetailNewProcessOperationRequest mcRetailNewProcessOperationRequest) {
        super(String.class);
        this.mcRetailNewProcessOperationRequest = mcRetailNewProcessOperationRequest;
    }

    @Override
    public void execute() {
        File moneyclickOperationFile = new File(MoneyclickFolderLocator.getOperationsFolder(), mcRetailNewProcessOperationRequest.getOperationId() + ".json");
        if (!moneyclickOperationFile.isFile()) {
            super.response = "OPERATION ID DOES NOT EXIST";
            return;
        }
        File moneyclickOperationsIndexFile = new File(new File(MoneyclickFolderLocator.getOperationsIndexFolder("Statuses"), MCRetailOperationStatus.PROCESSING.name()), mcRetailNewProcessOperationRequest.getOperationId() + ".json");
        if (!moneyclickOperationsIndexFile.isFile()) {
            super.response = "OPERATION ID IS NOT IN PROCESSING STATUS";
            return;
        }
        File moneyclickOperationsIndexesStatusFolder = FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getOperationsIndexFolder("Statuses"), mcRetailNewProcessOperationRequest.getMcRetailOperationStatus().name()));
        try {
            JsonNode moneyclickOperation = mapper.readTree(moneyclickOperationFile);
            String retailId = moneyclickOperation.get("retailId").textValue();
            String userName = moneyclickOperation.get("userName").textValue();
            String currency = moneyclickOperation.get("currency").textValue();
            Double amount = moneyclickOperation.get("amount").doubleValue();
            MCRetailOperationType mcRetailOperationType = MCRetailOperationType.valueOf(moneyclickOperation.get("mcRetailOperationType").textValue());
            if (mcRetailNewProcessOperationRequest.getMcRetailOperationStatus().equals(MCRetailOperationStatus.CANCELED)) {
                String escrowBalanceFileName = moneyclickOperation.get("escrowBalanceFileName").textValue();
                BaseOperation.changeBalanceOperationStatus(new File(MoneyclickFolderLocator.getRetailEscrowBalanceFolder(retailId), escrowBalanceFileName), BalanceOperationStatus.FAIL, null, null, mcRetailNewProcessOperationRequest.getCanceledReason());
                switch (mcRetailOperationType) {
                    case BUY_BALANCE:
                        String userBalanceFileName = moneyclickOperation.get("userBalanceFileName").textValue();
                        BaseOperation.changeBalanceOperationStatus(new File(UsersFolderLocator.getMCBalanceFolder(userName), userBalanceFileName), BalanceOperationStatus.FAIL, null, null, mcRetailNewProcessOperationRequest.getCanceledReason());
                        break;
                    case SELL_BALANCE:
                        String mcUserBalanceFileName = moneyclickOperation.get("mcUserBalanceFileName").textValue();
                        BaseOperation.changeBalanceOperationStatus(new File(UsersFolderLocator.getMCBalanceFolder(userName), mcUserBalanceFileName), BalanceOperationStatus.FAIL, null, null, mcRetailNewProcessOperationRequest.getCanceledReason());
                        break;
                }
            }
            if (mcRetailNewProcessOperationRequest.getMcRetailOperationStatus().equals(MCRetailOperationStatus.SUCCESS)) {
                switch (mcRetailOperationType) {
                    case BUY_BALANCE:
                        File moneyclickRetailBalance;
                        if (mcRetailNewProcessOperationRequest.isCash()) {
                            moneyclickRetailBalance = MoneyclickFolderLocator.getRetailBalanceCashFolder(retailId);
                        } else {
                            moneyclickRetailBalance = MoneyclickFolderLocator.getRetailBalanceNoCashFolder(retailId);
                        }
                        String result1 = BaseOperation.addToBalance(
                                moneyclickRetailBalance,
                                currency,
                                amount,
                                BalanceOperationType.MC_RETAIL_BUY_BALANCE,
                                BalanceOperationStatus.OK,
                                "BUY BALANCE USER " + userName,
                                null,
                                null,
                                true,
                                null
                        );
                        if (!result1.contains("OK____")) {
                            super.response = result1;
                            return;
                        }
                        String moneyclickRetailBalanceFileName = result1.split("____")[1];
                        ((ObjectNode) moneyclickOperation).put("cash", true);
                        ((ObjectNode) moneyclickOperation).put("moneyclickRetailBalanceFileName", moneyclickRetailBalanceFileName);
                        String userBalanceFileName = moneyclickOperation.get("userBalanceFileName").textValue();
                        BaseOperation.changeBalanceOperationStatus(new File(UsersFolderLocator.getMCBalanceFolder(userName), userBalanceFileName), BalanceOperationStatus.OK, null, null, mcRetailNewProcessOperationRequest.getCanceledReason());
                        break;
                    case SELL_BALANCE:
                        String result2 = BaseOperation.substractToBalance(
                                MoneyclickFolderLocator.getRetailBalanceCashFolder(retailId),
                                currency,
                                amount,
                                BalanceOperationType.MC_RETAIL_SELL_BALANCE,
                                BalanceOperationStatus.OK,
                                "SELL BALANCE USER " + userName,
                                null,
                                false,
                                null,
                                true,
                                null
                        );
                        if (!result2.contains("OK____")) {
                            super.response = result2;
                            return;
                        }
                        String moneyclickRetailBalanceCashFileName = result2.split("____")[1];
                        ((ObjectNode) moneyclickOperation).put("cash", true);
                        ((ObjectNode) moneyclickOperation).put("moneyclickRetailBalanceCashFileName", moneyclickRetailBalanceCashFileName);
                        break;
                }
            }
            ((ObjectNode) moneyclickOperation).put("mcRetailOperationStatus", mcRetailNewProcessOperationRequest.getMcRetailOperationStatus().name());
            FileUtil.editFile(moneyclickOperation, moneyclickOperationFile);
            FileUtil.moveFileToFolder(moneyclickOperationsIndexFile, moneyclickOperationsIndexesStatusFolder);
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(MCRetailNewProcessOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
