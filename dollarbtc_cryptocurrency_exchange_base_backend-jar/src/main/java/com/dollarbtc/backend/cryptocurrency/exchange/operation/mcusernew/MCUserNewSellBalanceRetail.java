/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcusernew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcusernew.MCUserNewSellBalanceRetailRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew.MCRetailNewAddEscrow;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author carlosmolina
 */
public class MCUserNewSellBalanceRetail extends AbstractOperation<String> {
    
    private final MCUserNewSellBalanceRetailRequest mcUserNewSellBalanceRetailRequest;

    public MCUserNewSellBalanceRetail(MCUserNewSellBalanceRetailRequest mcUserNewSellBalanceRetailRequest) {
        super(String.class);
        this.mcUserNewSellBalanceRetailRequest = mcUserNewSellBalanceRetailRequest;
    }
    
    @Override
    public void execute() {
        File userFolder = UsersFolderLocator.getFolder(mcUserNewSellBalanceRetailRequest.getUserName());
        if (!userFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File moneyclickRetailFolder = MoneyclickFolderLocator.getRetailFolder(mcUserNewSellBalanceRetailRequest.getRetailId());
        if (!moneyclickRetailFolder.isDirectory()) {
            super.response = "RETAIL DOES NOT EXIST";
            return;
        }
        String inLimits = BaseOperation.inLimits(mcUserNewSellBalanceRetailRequest.getUserName(), mcUserNewSellBalanceRetailRequest.getCurrency(), mcUserNewSellBalanceRetailRequest.getAmount(), BalanceOperationType.MC_RETAIL_SELL_BALANCE);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        String response1 = new MCRetailNewAddEscrow(mcUserNewSellBalanceRetailRequest.getCurrency(),
                mcUserNewSellBalanceRetailRequest.getAmount(), mcUserNewSellBalanceRetailRequest.getRetailId(),
                "SELL BALANCE USER " + mcUserNewSellBalanceRetailRequest.getUserName()).getResponse();
        if (!response1.contains("OK____")) {
            super.response = response1;
            return;
        }
        String escrowBalanceFileName = response1.split("____")[1];
        JsonNode charges = BaseOperation.getChargesNew(mcUserNewSellBalanceRetailRequest.getCurrency(), mcUserNewSellBalanceRetailRequest.getAmount(), BalanceOperationType.MC_RETAIL_SELL_BALANCE, null, "RETAIL__" + mcUserNewSellBalanceRetailRequest.getRetailId(), null, null);
        Double amount = mcUserNewSellBalanceRetailRequest.getAmount();
        Iterator<JsonNode> chargesIterator = charges.iterator();
        while (chargesIterator.hasNext()) {
            JsonNode chargesIt = chargesIterator.next();
            if (mcUserNewSellBalanceRetailRequest.getCurrency().equals(chargesIt.get("currency").textValue())) {
                amount = amount + chargesIt.get("amount").doubleValue();
            }
        }
        String response2 = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserNewSellBalanceRetailRequest.getUserName()),
                mcUserNewSellBalanceRetailRequest.getCurrency(),
                amount,
                BalanceOperationType.MC_RETAIL_SELL_BALANCE, BalanceOperationStatus.OK,
                "SELL BALANCE RETAIL " + mcUserNewSellBalanceRetailRequest.getRetailId(),
                null,
                false,
                charges,
                true,
                null
        );
        if (!response2.contains("OK____")) {
            BaseOperation.changeBalanceOperationStatus(
                    new File(MoneyclickFolderLocator.getRetailEscrowBalanceFolder(mcUserNewSellBalanceRetailRequest.getRetailId()),
                            escrowBalanceFileName),
                    BalanceOperationStatus.FAIL, null, null, null);
            super.response = response2;
            return;
        }
        String mcUserBalanceFileName = response2.split("____")[1];
        File moneyclickOperationsFolder = MoneyclickFolderLocator.getOperationsFolder();
        JsonNode moneyclickOperation = new ObjectMapper().createObjectNode();
        String id = BaseOperation.getId();
        ((ObjectNode) moneyclickOperation).put("id", id);
        ((ObjectNode) moneyclickOperation).put("securityPin", ((int) (Math.random() * 9000) + 1000));
        String[] securityImageUrlAndName = BaseOperation.getSecurityImageUrlAndName();
        ((ObjectNode) moneyclickOperation).put("securityImageUrl", securityImageUrlAndName[0]);
        ((ObjectNode) moneyclickOperation).put("securityImageNameES", securityImageUrlAndName[1]);
        ((ObjectNode) moneyclickOperation).put("securityImageNameEN", securityImageUrlAndName[2]);
        ((ObjectNode) moneyclickOperation).put("timestamp", DateUtil.getCurrentDate());
        ((ObjectNode) moneyclickOperation).put("mcRetailId", mcUserNewSellBalanceRetailRequest.getRetailId());
        ((ObjectNode) moneyclickOperation).put("mcRetailOperationStatus", MCRetailOperationStatus.PROCESSING.name());
        ((ObjectNode) moneyclickOperation).put("mcRetailOperationType", MCRetailOperationType.SELL_BALANCE.name());
        ((ObjectNode) moneyclickOperation).put("escrowBalanceFileName", escrowBalanceFileName);
        ((ObjectNode) moneyclickOperation).put("mcUserBalanceFileName", mcUserBalanceFileName);
        ((ObjectNode) moneyclickOperation).set("charges", charges);
        FileUtil.createFile(mcUserNewSellBalanceRetailRequest.toJsonNode(moneyclickOperation),
                new File(moneyclickOperationsFolder, id + ".json"));
        addOperationIndexes(id, mcUserNewSellBalanceRetailRequest.getUserName(), mcUserNewSellBalanceRetailRequest.getCurrency(),
                mcUserNewSellBalanceRetailRequest.getRetailId(), MCRetailOperationType.SELL_BALANCE);
        // add commisions to retail 3%
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
