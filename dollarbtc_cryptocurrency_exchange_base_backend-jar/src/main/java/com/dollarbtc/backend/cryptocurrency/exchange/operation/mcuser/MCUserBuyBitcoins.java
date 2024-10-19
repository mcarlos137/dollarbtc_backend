/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserBuyBitcoinsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MCUserBuyBitcoins extends AbstractOperation<String> {

    private final MCUserBuyBitcoinsRequest mcUserBuyBitcoinsRequest;

    public MCUserBuyBitcoins(MCUserBuyBitcoinsRequest mcUserBuyBitcoinsRequest) {
        super(String.class);
        this.mcUserBuyBitcoinsRequest = mcUserBuyBitcoinsRequest;
    }

    @Override
    protected void execute() {
        JsonNode fastChangeFactor = new MCUserGetFastChangeFactor(mcUserBuyBitcoinsRequest.getCurrency(), "BTC").getResponse();
        if (!fastChangeFactor.has("factor")) {
            super.response = "THERE IS NO PRICE FOR THIS CURRENCY";
            return;
        }
        Double currentPrice = 1 / fastChangeFactor.get("factor").doubleValue();
        Double price = mcUserBuyBitcoinsRequest.getAmount() / mcUserBuyBitcoinsRequest.getBtcAmount();
        Double factorPercentDiff = (price - currentPrice) / currentPrice;
        if (factorPercentDiff > 0.02 || factorPercentDiff < -0.02) {
            super.response = "BTC PRICE CHANGE";
            return;
        }
        String inLimits = BaseOperation.inLimits(mcUserBuyBitcoinsRequest.getUserName(), "BTC", mcUserBuyBitcoinsRequest.getAmount(), BalanceOperationType.MC_BUY_BITCOINS);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        String operationId = BaseOperation.getId();
        ObjectNode additionals = mapper.createObjectNode();
        additionals.put("operationId", operationId);
        JsonNode charges = BaseOperation.getChargesNew(mcUserBuyBitcoinsRequest.getCurrency(), mcUserBuyBitcoinsRequest.getAmount(), BalanceOperationType.MC_BUY_BITCOINS, null, "MONEYCLICK", null, null);
        String substractToBalance = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserBuyBitcoinsRequest.getUserName()),
                mcUserBuyBitcoinsRequest.getCurrency(),
                mcUserBuyBitcoinsRequest.getAmount(),
                BalanceOperationType.MC_BUY_BITCOINS,
                BalanceOperationStatus.OK,
                null,
                null,
                false,
                charges,
                false,
                additionals
        );
        if (!substractToBalance.equals("OK")) {
            super.response = substractToBalance;
            return;
        }
        File moneyclickCryptoBuysFolder = MoneyclickFolderLocator.getCryptoBuysFolder();
        ObjectNode moneyclickCryptoBuy = mapper.createObjectNode();
        moneyclickCryptoBuy.put("operationId", operationId);
        moneyclickCryptoBuy.put("userName", mcUserBuyBitcoinsRequest.getUserName());
        moneyclickCryptoBuy.put("currency", mcUserBuyBitcoinsRequest.getCurrency());
        moneyclickCryptoBuy.put("timestamp", DateUtil.getCurrentDate());
        moneyclickCryptoBuy.put("cryptoCurrency", "BTC");
        moneyclickCryptoBuy.put("cryptoAmount", mcUserBuyBitcoinsRequest.getBtcAmount());
        FileUtil.createFile(moneyclickCryptoBuy, new File(moneyclickCryptoBuysFolder, operationId + ".json"));
        BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserBuyBitcoinsRequest.getUserName()),
                "BTC",
                mcUserBuyBitcoinsRequest.getBtcAmount(),
                BalanceOperationType.MC_BUY_BITCOINS,
                BalanceOperationStatus.PROCESSING,
                null,
                null,
                null,
                false,
                additionals
        );
        addCount();
        super.response = "OK__" + operationId;
    }
    
    private void addCount() {
        try {
            String timestamp = DateUtil.getCurrentDate();
            String moneyclickCryptoBuysSellsCount = "";
            File moneyclickCryptoBuysSellsCountFile = MoneyclickFolderLocator.getCryptoBuysSellsCountFile(mcUserBuyBitcoinsRequest.getUserName());
            if (moneyclickCryptoBuysSellsCountFile.isFile()) {
                moneyclickCryptoBuysSellsCount = Files.readString(moneyclickCryptoBuysSellsCountFile.toPath());
            }
            moneyclickCryptoBuysSellsCount = moneyclickCryptoBuysSellsCount + "__" + timestamp;
            Set<String> timestampsToDelete = new HashSet<>();
            for (String moneyclickCryptoBuysSellsCountTimestamp : moneyclickCryptoBuysSellsCount.split("__")) {
                if (moneyclickCryptoBuysSellsCountTimestamp.equals("")) {
                    continue;
                }
                if(DateUtil.parseDate(moneyclickCryptoBuysSellsCountTimestamp).before(DateUtil.parseDate(DateUtil.getDateMinutesBefore(timestamp, 1440)))){
                    timestampsToDelete.add(moneyclickCryptoBuysSellsCountTimestamp);
                }
            }
            for (String timestampToDelete : timestampsToDelete) {
                moneyclickCryptoBuysSellsCount = moneyclickCryptoBuysSellsCount.replace("__" + timestampToDelete, "");
            }
            FileUtil.editFile(moneyclickCryptoBuysSellsCount, moneyclickCryptoBuysSellsCountFile);
        } catch (IOException ex) {
            Logger.getLogger(MCUserBuyBitcoins.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
