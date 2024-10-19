/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcusernew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcusernew.MCUserNewBuyBalanceRetailRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewSubstractEscrow;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author CarlosDaniel
 */
public class MCUserNewBuyBalanceRetail extends AbstractOperation<String> {

    private final MCUserNewBuyBalanceRetailRequest mcUserNewBuyBalanceRetailRequest;

    public MCUserNewBuyBalanceRetail(MCUserNewBuyBalanceRetailRequest mcUserNewBuyBalanceRetailRequest) {
        super(String.class);
        this.mcUserNewBuyBalanceRetailRequest = mcUserNewBuyBalanceRetailRequest;
    }

    @Override
    public void execute() {
        File userFolder = UsersFolderLocator.getFolder(mcUserNewBuyBalanceRetailRequest.getUserName());
        if (!userFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File moneyclickRetailFolder = MoneyclickFolderLocator.getRetailFolder(mcUserNewBuyBalanceRetailRequest.getRetailId());
        if (!moneyclickRetailFolder.isDirectory()) {
            super.response = "RETAIL DOES NOT EXIST";
            return;
        }
        String inLimits = BaseOperation.inLimits(mcUserNewBuyBalanceRetailRequest.getUserName(), mcUserNewBuyBalanceRetailRequest.getCurrency(), mcUserNewBuyBalanceRetailRequest.getAmount(), BalanceOperationType.MC_RETAIL_BUY_BALANCE);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        String response1 = new MCRetailNewSubstractEscrow(
                mcUserNewBuyBalanceRetailRequest.getCurrency(),
                mcUserNewBuyBalanceRetailRequest.getAmount(),
                mcUserNewBuyBalanceRetailRequest.getRetailId(),
                "BUY BALANCE USER " + mcUserNewBuyBalanceRetailRequest.getUserName()).getResponse();
        if (!response1.contains("OK____")) {
            super.response = response1;
            return;
        }
        String escrowBalanceFileName = response1.split("____")[1];
        String response2 = BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserNewBuyBalanceRetailRequest.getUserName()),
                mcUserNewBuyBalanceRetailRequest.getCurrency(),
                mcUserNewBuyBalanceRetailRequest.getAmount(),
                BalanceOperationType.MC_RETAIL_BUY_BALANCE,
                BalanceOperationStatus.PROCESSING,
                "BUY BALANCE RETAIL " + mcUserNewBuyBalanceRetailRequest.getRetailId(),
                null,
                BaseOperation.getChargesNew(mcUserNewBuyBalanceRetailRequest.getCurrency(), mcUserNewBuyBalanceRetailRequest.getAmount(), BalanceOperationType.MC_RETAIL_BUY_BALANCE, null, "RETAIL__" + mcUserNewBuyBalanceRetailRequest.getRetailId(), null, null),
                true,
                null
        );
        if (!response2.contains("OK____")) {
            BaseOperation.changeBalanceOperationStatus(
                    new File(MoneyclickFolderLocator.getRetailEscrowBalanceFolder(mcUserNewBuyBalanceRetailRequest.getRetailId()), escrowBalanceFileName),
                    BalanceOperationStatus.FAIL, null, null, null);
            super.response = response2;
            return;
        }
        String userBalanceFileName = response2.split("____")[1];
        File moneyclickOperationsFolder = MoneyclickFolderLocator.getOperationsFolder();
        JsonNode moneyclickOperation = new ObjectMapper().createObjectNode();
        String id = BaseOperation.getId();
        ((ObjectNode) moneyclickOperation).put("id", id);
        String[] securityImageUrlAndName = BaseOperation.getSecurityImageUrlAndName();
        ((ObjectNode) moneyclickOperation).put("securityImageUrl", securityImageUrlAndName[0]);
        ((ObjectNode) moneyclickOperation).put("securityImageNameES", securityImageUrlAndName[1]);
        ((ObjectNode) moneyclickOperation).put("securityImageNameEN", securityImageUrlAndName[2]);
        ((ObjectNode) moneyclickOperation).put("timestamp", DateUtil.getCurrentDate());
        ((ObjectNode) moneyclickOperation).put("mcRetailId", mcUserNewBuyBalanceRetailRequest.getRetailId());
        ((ObjectNode) moneyclickOperation).put("mcRetailOperationStatus", MCRetailOperationStatus.PROCESSING.name());
        ((ObjectNode) moneyclickOperation).put("mcRetailOperationType", MCRetailOperationType.BUY_BALANCE.name());
        ((ObjectNode) moneyclickOperation).put("escrowBalanceFileName", escrowBalanceFileName);
        ((ObjectNode) moneyclickOperation).put("userBalanceFileName", userBalanceFileName);
        FileUtil.createFile(mcUserNewBuyBalanceRetailRequest.toJsonNode(moneyclickOperation),
                new File(moneyclickOperationsFolder, id + ".json"));
        addOperationIndexes(id, mcUserNewBuyBalanceRetailRequest.getUserName(), mcUserNewBuyBalanceRetailRequest.getCurrency(),
                mcUserNewBuyBalanceRetailRequest.getRetailId(), MCRetailOperationType.BUY_BALANCE);
        // add commisions to retail 2%
        super.response = "OK";
    }
    
    private static void addOperationIndexes(String id, String userName, String currency, String retailId,
            MCRetailOperationType mcRetailOperationType) {
        JsonNode mcUserRetailOperationIndex = new ObjectMapper().createObjectNode();
        ((ObjectNode) mcUserRetailOperationIndex).put("id", id);
        ((ObjectNode) mcUserRetailOperationIndex).put("timestamp", DateUtil.getCurrentDate());
        File getMoneyclickRetailOperationsIndexFolder = FileUtil.createFolderIfNoExist(
                new File(FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getOperationsIndexesFolder(), "UserNames")),
                        userName));
        FileUtil.createFile(mcUserRetailOperationIndex,
                new File(getMoneyclickRetailOperationsIndexFolder, id + ".json"));
        getMoneyclickRetailOperationsIndexFolder = FileUtil.createFolderIfNoExist(
                new File(FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getOperationsIndexesFolder(), "Currencies")),
                        currency));
        FileUtil.createFile(mcUserRetailOperationIndex,
                new File(getMoneyclickRetailOperationsIndexFolder, id + ".json"));
        getMoneyclickRetailOperationsIndexFolder = FileUtil.createFolderIfNoExist(new File(
                FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getOperationsIndexesFolder(), "Retails")), retailId));
        FileUtil.createFile(mcUserRetailOperationIndex,
                new File(getMoneyclickRetailOperationsIndexFolder, id + ".json"));
        getMoneyclickRetailOperationsIndexFolder = FileUtil.createFolderIfNoExist(
                new File(FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getOperationsIndexesFolder(), "Types")),
                        mcRetailOperationType.name()));
        FileUtil.createFile(mcUserRetailOperationIndex,
                new File(getMoneyclickRetailOperationsIndexFolder, id + ".json"));
        getMoneyclickRetailOperationsIndexFolder = FileUtil.createFolderIfNoExist(
                new File(FileUtil.createFolderIfNoExist(new File(MoneyclickFolderLocator.getOperationsIndexesFolder(), "Statuses")),
                        MCRetailOperationStatus.PROCESSING.name()));
        FileUtil.createFile(mcUserRetailOperationIndex,
                new File(getMoneyclickRetailOperationsIndexFolder, id + ".json"));
    }

}
