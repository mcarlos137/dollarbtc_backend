/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserSellCryptoRequest;
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
public class MCUserSellCrypto extends AbstractOperation<String> {

    private final MCUserSellCryptoRequest mcUserSellCryptoRequest;

    public MCUserSellCrypto(MCUserSellCryptoRequest mcUserSellCryptoRequest) {
        super(String.class);
        this.mcUserSellCryptoRequest = mcUserSellCryptoRequest;
    }

    @Override
    protected void execute() {
        JsonNode fastChangeFactor = new MCUserGetFastChangeFactor(mcUserSellCryptoRequest.getCryptoCurrency(), mcUserSellCryptoRequest.getFiatCurrency()).getResponse();
        if (!fastChangeFactor.has("factor")) {
            super.response = "THERE IS NO PRICE FOR THIS CURRENCY";
            return;
        }
        Double currentPrice = fastChangeFactor.get("factor").doubleValue();
        Double price = mcUserSellCryptoRequest.getFiatAmount() / mcUserSellCryptoRequest.getCryptoAmount();
        Double factorPercentDiff = (price - currentPrice) / currentPrice;
        if (factorPercentDiff > 0.02 || factorPercentDiff < -0.02) {
            super.response = "CRYPTO PRICE CHANGE";
            return;
        }
        String inLimits = BaseOperation.inLimits(mcUserSellCryptoRequest.getUserName(), mcUserSellCryptoRequest.getCryptoCurrency(), mcUserSellCryptoRequest.getFiatAmount(), BalanceOperationType.MC_SELL_CRYPTO);
        if (!inLimits.equals("OK")) {
            Logger.getLogger(MCUserSellCrypto.class.getName()).log(Level.INFO, "sell crypto result {0}", inLimits);
            super.response = inLimits;
            return;
        }
        JsonNode charges = BaseOperation.getChargesNew(mcUserSellCryptoRequest.getFiatCurrency(), mcUserSellCryptoRequest.getFiatAmount(), BalanceOperationType.MC_SELL_CRYPTO, null, "MONEYCLICK", null, null);
        String operationId = BaseOperation.getId();
        ObjectNode additionals = mapper.createObjectNode();
        additionals.put("operationId", operationId);
        String substractToBalance = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserSellCryptoRequest.getUserName()),
                mcUserSellCryptoRequest.getCryptoCurrency(),
                mcUserSellCryptoRequest.getCryptoAmount(),
                BalanceOperationType.MC_SELL_CRYPTO,
                BalanceOperationStatus.OK,
                null,
                null,
                false,
                null,
                false,
                additionals
        );
        if (!substractToBalance.equals("OK")) {
            super.response = substractToBalance;
            return;
        }
        BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserSellCryptoRequest.getUserName()),
                mcUserSellCryptoRequest.getFiatCurrency(),
                mcUserSellCryptoRequest.getFiatAmount(),
                BalanceOperationType.MC_SELL_CRYPTO,
                BalanceOperationStatus.OK,
                null,
                null,
                charges,
                false,
                additionals
        );
        addCount();
        changePriceThread(mcUserSellCryptoRequest.getCryptoCurrency(), mcUserSellCryptoRequest.getFiatCurrency(), mcUserSellCryptoRequest.getCryptoAmount(), mcUserSellCryptoRequest.getFiatAmount());
        super.response = "OK__" + operationId;
    }

    private void addCount() {
        try {
            String timestamp = DateUtil.getCurrentDate();
            String moneyclickCryptoBuysSellsCount = "";
            File moneyclickCryptoBuysSellsCountFile = MoneyclickFolderLocator.getCryptoBuysSellsCountFile(mcUserSellCryptoRequest.getUserName());
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
            Logger.getLogger(MCUserSellCrypto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void changePriceThread(String cryptoCurrency, String fiatCurrency, Double cryptoAmount, Double fiatAmount) {
        Thread thread = new Thread(() -> {
            if (cryptoCurrency.equals("BTC")) {
                new OTCChangeOperationPrice(fiatCurrency, fiatAmount, "SELL", fiatAmount / cryptoAmount).getResponse();
            } else {
                new OTCChangeOperationPrice(cryptoCurrency, cryptoAmount, "BUY", null).getResponse();
                new OTCChangeOperationPrice(fiatCurrency, fiatAmount, "SELL", null).getResponse();

            }
        });
        thread.start();
    }

}
