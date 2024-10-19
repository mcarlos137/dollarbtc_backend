/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser.MCUserFastChangeRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
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
public class MCUserFastChange extends AbstractOperation<String> {

    private final MCUserFastChangeRequest mcUserFastChangeRequest;

    public MCUserFastChange(MCUserFastChangeRequest mcUserFastChangeRequest) {
        super(String.class);
        this.mcUserFastChangeRequest = mcUserFastChangeRequest;
    }

    @Override
    public void execute() {
        JsonNode fastChangeFactor = new MCUserGetFastChangeFactor(mcUserFastChangeRequest.getBaseCurrency(), mcUserFastChangeRequest.getTargetCurrency()).getResponse();
        if (!fastChangeFactor.has("factor")) {
            super.response = "THERE IS NO OFFER FACTOR TO THESE CURRENCIES";
            return;
        }
        Double factorPercentDiff = (mcUserFastChangeRequest.getFactor() - fastChangeFactor.get("factor").doubleValue()) / fastChangeFactor.get("factor").doubleValue();
        if (factorPercentDiff > 0.02 || factorPercentDiff < -0.02) {
            super.response = "OFFER FACTOR CHANGE";
            return;
        }
        String inLimits = BaseOperation.inLimits(mcUserFastChangeRequest.getUserName(), mcUserFastChangeRequest.getBaseCurrency(), mcUserFastChangeRequest.getAmount(), BalanceOperationType.MC_FAST_CHANGE);
        if (!inLimits.equals("OK")) {
            Logger.getLogger(MCUserFastChange.class.getName()).log(Level.INFO, "fastChange result {0}", inLimits);
            super.response = inLimits;
            return;
        }
        JsonNode charges = BaseOperation.getChargesNew(mcUserFastChangeRequest.getBaseCurrency(), mcUserFastChangeRequest.getAmount(), BalanceOperationType.MC_FAST_CHANGE, null, "MONEYCLICK", null, mcUserFastChangeRequest.getTargetCurrency());
        String operationId = BaseOperation.getId();
        ObjectNode additionals = mapper.createObjectNode();
        additionals.put("operationId", operationId);
        String substractToBalance = BaseOperation.substractToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserFastChangeRequest.getUserName()),
                mcUserFastChangeRequest.getBaseCurrency(),
                mcUserFastChangeRequest.getAmount(),
                BalanceOperationType.MC_FAST_CHANGE,
                BalanceOperationStatus.OK,
                "CHANGE FROM " + mcUserFastChangeRequest.getBaseCurrency() + " TO " + mcUserFastChangeRequest.getTargetCurrency() + " - " + mcUserFastChangeRequest.getDescription(),
                null,
                false,
                charges,
                false,
                additionals
        );
        if (!substractToBalance.equals("OK")) {
            Logger.getLogger(MCUserFastChange.class.getName()).log(Level.INFO, "fastChange result {0}", substractToBalance);
            super.response = substractToBalance;
            return;
        }
        BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(mcUserFastChangeRequest.getUserName()),
                mcUserFastChangeRequest.getTargetCurrency(),
                mcUserFastChangeRequest.getAmount() * mcUserFastChangeRequest.getFactor(),
                BalanceOperationType.MC_FAST_CHANGE,
                BalanceOperationStatus.OK,
                "CHANGE FROM " + mcUserFastChangeRequest.getBaseCurrency() + " TO " + mcUserFastChangeRequest.getTargetCurrency() + " - " + mcUserFastChangeRequest.getDescription(),
                null,
                null,
                false,
                additionals
        );
        addCount();
        changePriceThread(mcUserFastChangeRequest.getBaseCurrency(), mcUserFastChangeRequest.getTargetCurrency(), mcUserFastChangeRequest.getAmount(), mcUserFastChangeRequest.getFactor());
        Logger.getLogger(MCUserFastChange.class.getName()).log(Level.INFO, "fastChange result {0}", "OK");
        super.response = "OK";
    }

    private void addCount() {
        try {
            String timestamp = DateUtil.getCurrentDate();
            String moneyclickFastChangesCount = "";
            File moneyclickFastChangesCountFile = MoneyclickFolderLocator.getFastChangesCountFile(mcUserFastChangeRequest.getUserName());
            if (moneyclickFastChangesCountFile.isFile()) {
                moneyclickFastChangesCount = Files.readString(moneyclickFastChangesCountFile.toPath());
            }
            moneyclickFastChangesCount = moneyclickFastChangesCount + "__" + timestamp;
            Set<String> timestampsToDelete = new HashSet<>();
            for (String moneyclickFastChangesCountTimestamp : moneyclickFastChangesCount.split("__")) {
                if (moneyclickFastChangesCountTimestamp.equals("")) {
                    continue;
                }
                if (DateUtil.parseDate(moneyclickFastChangesCountTimestamp).before(DateUtil.parseDate(DateUtil.getDateMinutesBefore(timestamp, 1440)))) {
                    timestampsToDelete.add(moneyclickFastChangesCountTimestamp);
                }
            }
            for (String timestampToDelete : timestampsToDelete) {
                moneyclickFastChangesCount = moneyclickFastChangesCount.replace("__" + timestampToDelete, "");
            }
            FileUtil.editFile(moneyclickFastChangesCount, moneyclickFastChangesCountFile);
        } catch (IOException ex) {
            Logger.getLogger(MCUserFastChange.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void changePriceThread(String baseCurrency, String targetCurrency, Double amount, Double factor) {
        Thread thread = new Thread(() -> {
            new OTCChangeOperationPrice(baseCurrency, amount, "BUY", null).getResponse();
            new OTCChangeOperationPrice(targetCurrency, amount * factor, "SELL", null).getResponse();

        });
        thread.start();
    }

}
