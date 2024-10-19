/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserBuyCryptoRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCChangeOperationPrice;
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
public class MCUserBuyCrypto extends AbstractOperation<String> {

    private final MCUserBuyCryptoRequest mcUserBuyCryptoRequest;

    public MCUserBuyCrypto(MCUserBuyCryptoRequest mcUserBuyCryptoRequest) {
        super(String.class);
        this.mcUserBuyCryptoRequest = mcUserBuyCryptoRequest;
    }

    @Override
    protected void execute() {
        JsonNode fastChangeFactor = new MCUserGetFastChangeFactor(mcUserBuyCryptoRequest.getFiatCurrency(), mcUserBuyCryptoRequest.getCryptoCurrency()).getResponse();
        if (!fastChangeFactor.has("factor")) {
            super.response = "THERE IS NO PRICE FOR THIS CURRENCY";
            return;
        }
        Double currentPrice = 1 / fastChangeFactor.get("factor").doubleValue();
        Double price = mcUserBuyCryptoRequest.getFiatAmount() / mcUserBuyCryptoRequest.getCryptoAmount();
        Double factorPercentDiff = (price - currentPrice) / currentPrice;
        if (factorPercentDiff > 0.02 || factorPercentDiff < -0.02) {
            super.response = "CRYPTO PRICE CHANGE";
            return;
        }
        String inLimits = BaseOperation.inLimits(mcUserBuyCryptoRequest.getUserName(), mcUserBuyCryptoRequest.getCryptoCurrency(), mcUserBuyCryptoRequest.getFiatAmount(), BalanceOperationType.MC_BUY_CRYPTO);
        if (!inLimits.equals("OK")) {
            super.response = inLimits;
            return;
        }
        String operationId = BaseOperation.getId();
        ObjectNode additionals = mapper.createObjectNode();
        additionals.put("operationId", operationId);
        JsonNode charges = BaseOperation.getChargesNew(mcUserBuyCryptoRequest.getFiatCurrency(), mcUserBuyCryptoRequest.getFiatAmount(), BalanceOperationType.MC_BUY_CRYPTO, null, "MONEYCLICK", null, null);
        String substractToBalance = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserBuyCryptoRequest.getUserName()),
                mcUserBuyCryptoRequest.getFiatCurrency(),
                mcUserBuyCryptoRequest.getFiatAmount(),
                BalanceOperationType.MC_BUY_CRYPTO,
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
        moneyclickCryptoBuy.put("userName", mcUserBuyCryptoRequest.getUserName());
        moneyclickCryptoBuy.put("timestamp", DateUtil.getCurrentDate());
        moneyclickCryptoBuy.put("fiatCurrency", mcUserBuyCryptoRequest.getFiatCurrency());
        moneyclickCryptoBuy.put("fiatAmount", mcUserBuyCryptoRequest.getFiatAmount());
        moneyclickCryptoBuy.put("cryptoCurrency", mcUserBuyCryptoRequest.getCryptoCurrency());
        moneyclickCryptoBuy.put("cryptoAmount", mcUserBuyCryptoRequest.getCryptoAmount());
        FileUtil.createFile(moneyclickCryptoBuy, new File(moneyclickCryptoBuysFolder, operationId + ".json"));
        BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserBuyCryptoRequest.getUserName()),
                mcUserBuyCryptoRequest.getCryptoCurrency(),
                mcUserBuyCryptoRequest.getCryptoAmount(),
                BalanceOperationType.MC_BUY_CRYPTO,
                BalanceOperationStatus.OK,
                null,
                null,
                null,
                false,
                additionals
        );
        addCount();
        changePriceThread(mcUserBuyCryptoRequest.getCryptoCurrency(), mcUserBuyCryptoRequest.getFiatCurrency(), mcUserBuyCryptoRequest.getCryptoAmount(), mcUserBuyCryptoRequest.getFiatAmount());
        super.response = "OK__" + operationId;
    }

    private void addCount() {
        try {
            String timestamp = DateUtil.getCurrentDate();
            String moneyclickCryptoBuysSellsCount = "";
            File moneyclickCryptoBuysSellsCountFile = MoneyclickFolderLocator.getCryptoBuysSellsCountFile(mcUserBuyCryptoRequest.getUserName());
            if (moneyclickCryptoBuysSellsCountFile.isFile()) {
                moneyclickCryptoBuysSellsCount = Files.readString(moneyclickCryptoBuysSellsCountFile.toPath());
            }
            moneyclickCryptoBuysSellsCount = moneyclickCryptoBuysSellsCount + "__" + timestamp;
            Set<String> timestampsToDelete = new HashSet<>();
            for (String moneyclickCryptoBuysSellsCountTimestamp : moneyclickCryptoBuysSellsCount.split("__")) {
                if (moneyclickCryptoBuysSellsCountTimestamp.equals("")) {
                    continue;
                }
                if (DateUtil.parseDate(moneyclickCryptoBuysSellsCountTimestamp).before(DateUtil.parseDate(DateUtil.getDateMinutesBefore(timestamp, 1440)))) {
                    timestampsToDelete.add(moneyclickCryptoBuysSellsCountTimestamp);
                }
            }
            for (String timestampToDelete : timestampsToDelete) {
                moneyclickCryptoBuysSellsCount = moneyclickCryptoBuysSellsCount.replace("__" + timestampToDelete, "");
            }
            FileUtil.editFile(moneyclickCryptoBuysSellsCount, moneyclickCryptoBuysSellsCountFile);
        } catch (IOException ex) {
            Logger.getLogger(MCUserBuyCrypto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void changePriceThread(String cryptoCurrency, String fiatCurrency, Double cryptoAmount, Double fiatAmount) {
        Thread thread = new Thread(() -> {
            if (cryptoCurrency.equals("BTC")) {
                new OTCChangeOperationPrice(fiatCurrency, fiatAmount, "BUY", fiatAmount / cryptoAmount).getResponse();
            } else {
                new OTCChangeOperationPrice(fiatCurrency, fiatAmount, "BUY", null).getResponse();
                new OTCChangeOperationPrice(cryptoCurrency, cryptoAmount, "SELL", null).getResponse();
            }
        });
        thread.start();
    }

}
