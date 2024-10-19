/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyCallsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyCallList extends AbstractOperation<ArrayNode> {

    private final MoneyCallListRequest moneyCallListRequest;

    public MoneyCallList(MoneyCallListRequest moneyCallListRequest) {
        super(ArrayNode.class);
        this.moneyCallListRequest = moneyCallListRequest;
    }

    @Override
    protected void execute() {
        ArrayNode moneyCalls = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("SenderUserNames");
        indexes.add("ReceiverUserNames");
        indexes.add("Currencies");
        indexes.add("Statuses");
        indexes.add("Rates");
        indexes.add("Times");
        indexes.add("Stars");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i[] = null;
            switch (index) {
                case "Currencies":
                    i = moneyCallListRequest.getCurrencies();
                    break;
                case "SenderUserNames":
                    i = moneyCallListRequest.getSenderUserNames();
                    break;
                case "ReceiverUserNames":
                    i = moneyCallListRequest.getReceiverUserNames();
                    break;
                case "Statuses":
                    i = moneyCallListRequest.getStatuses();
                    break;
                case "Rates":
                    i = moneyCallListRequest.getRates();
                    break;
                case "Times":
                    i = moneyCallListRequest.getTimes();
                    break;
                case "Stars":
                    i = moneyCallListRequest.getStars();
                    break;
                default:
                    break;
            }
            File moneyclickMoneyCallIndexFolder = MoneyCallsFolderLocator.getIndexFolder(index);
            if (firstLoop) {
                if (i != null) {
                    for (String _i : i) {
                        if (_i == null) {
                            continue;
                        }
                        File moneyclickMoneyCallIndexValueFolder = new File(moneyclickMoneyCallIndexFolder, _i);
                        if (!moneyclickMoneyCallIndexValueFolder.isDirectory()) {
                            super.response = moneyCalls;
                            return;
                        }
                        for (File idFiles : moneyclickMoneyCallIndexValueFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                } else {
                    for (File moneyclickMoneyCallIndexValueFolder : moneyclickMoneyCallIndexFolder.listFiles()) {
                        if (!moneyclickMoneyCallIndexValueFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : moneyclickMoneyCallIndexValueFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                }
                firstLoop = false;
            } else {
                Set<String> newIds = new HashSet<>();
                if (i != null) {
                    for (String _i : i) {
                        if (_i == null) {
                            continue;
                        }
                        File moneyclickMoneyCallIndexValueFolder = new File(moneyclickMoneyCallIndexFolder, _i);
                        if (!moneyclickMoneyCallIndexValueFolder.isDirectory()) {
                            super.response = moneyCalls;
                            return;
                        }
                        for (String id : ids) {
                            if (!new File(moneyclickMoneyCallIndexValueFolder, id + ".json").isFile()) {
                                continue;
                            }
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File moneyclickMoneyCallIndexValueFolder : moneyclickMoneyCallIndexFolder.listFiles()) {
                        if (!moneyclickMoneyCallIndexValueFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (!new File(moneyclickMoneyCallIndexValueFolder, id + ".json").isFile()) {
                                continue;
                            }
                            newIds.add(id);
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get money calls
        for (String id : ids) {
            try {
                JsonNode moneyCall = mapper.readTree(MoneyCallsFolderLocator.getFile(id));
                String timestamp = moneyCall.get("createTimestamp").textValue();
                if (moneyCallListRequest.getInitTimestamp() == null && moneyCallListRequest.getFinalTimestamp() == null) {
                    moneyCalls.add(moneyCall);
                } else if (moneyCallListRequest.getInitTimestamp() != null && moneyCallListRequest.getFinalTimestamp() == null) {
                    if (timestamp.compareTo(moneyCallListRequest.getInitTimestamp()) >= 0) {
                        moneyCalls.add(moneyCall);
                    }
                } else if (moneyCallListRequest.getInitTimestamp() == null && moneyCallListRequest.getFinalTimestamp() != null) {
                    if (timestamp.compareTo(moneyCallListRequest.getFinalTimestamp()) < 0) {
                        moneyCalls.add(moneyCall);
                    }
                } else {
                    if (timestamp.compareTo(moneyCallListRequest.getInitTimestamp()) >= 0 && timestamp.compareTo(moneyCallListRequest.getFinalTimestamp()) < 0) {
                        moneyCalls.add(moneyCall);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MoneyCallList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = moneyCalls;
    }

}
