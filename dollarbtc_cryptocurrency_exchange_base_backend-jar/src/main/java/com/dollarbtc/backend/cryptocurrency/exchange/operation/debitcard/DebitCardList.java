/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.debitcard;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.debitcard.DebitCardListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DebitCardsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class DebitCardList extends AbstractOperation<ArrayNode> {

    private final DebitCardListRequest debitCardListRequest;

    public DebitCardList(DebitCardListRequest debitCardListRequest) {
        super(ArrayNode.class);
        this.debitCardListRequest = debitCardListRequest;
    }

    @Override
    public void execute() {
        ArrayNode list = mapper.createArrayNode();
        Set<String> ids = new HashSet<>();
        Set<String> indexes = new HashSet<>();
        indexes.add("Numbers");
        indexes.add("UserNames");
        indexes.add("Currencies");
        indexes.add("HolderNames");
        indexes.add("Statuses");
        indexes.add("Types");
        boolean firstLoop = true;
        for (String index : indexes) {
            String i = null;
            switch (index) {
                case "Numbers":
                    if (debitCardListRequest.getNumber() != null && !debitCardListRequest.getNumber().equals("")) {
                        i = debitCardListRequest.getNumber();
                    }
                    break;
                case "UserNames":
                    if (debitCardListRequest.getUserName() != null && !debitCardListRequest.getUserName().equals("")) {
                        i = debitCardListRequest.getUserName();
                    }
                    break;
                case "Currencies":
                    if (debitCardListRequest.getCurrency() != null && !debitCardListRequest.getCurrency().equals("")) {
                        i = debitCardListRequest.getCurrency();
                    }
                    break;
                case "HolderNames":
                    if (debitCardListRequest.getHolderName() != null && !debitCardListRequest.getHolderName().equals("")) {
                        i = debitCardListRequest.getHolderName();
                    }
                    break;
                case "Statuses":
                    if (debitCardListRequest.getDebitCardStatus() != null) {
                        i = debitCardListRequest.getDebitCardStatus().name();
                    }
                    break;
                case "Types":
                    if (debitCardListRequest.getType() != null && !debitCardListRequest.getType().equals("")) {
                        i = debitCardListRequest.getType();
                    }
                    break;
                default:
                    break;
            }
            File debitCardsIndexesFolder = DebitCardsFolderLocator.getIndexesSpecificFolder(index);
            if (firstLoop) {
                if (i != null && !i.equals("")) {
                    File debitCardsIndexFolder = new File(debitCardsIndexesFolder, i);
                    if (!debitCardsIndexFolder.isDirectory()) {
                        super.response = list;
                        return;
                    }
                    for (File idFiles : debitCardsIndexFolder.listFiles()) {
                        ids.add(idFiles.getName().replace(".json", ""));
                    }
                } else {
                    for (File debitCardsIndexFolder : debitCardsIndexesFolder.listFiles()) {
                        if (!debitCardsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (File idFiles : debitCardsIndexFolder.listFiles()) {
                            ids.add(idFiles.getName().replace(".json", ""));
                        }
                    }
                }
                firstLoop = false;
            } else {
                Set<String> newIds = new HashSet<>();
                if (i != null && !i.equals("")) {
                    File otcOperationsIndexFolder = new File(debitCardsIndexesFolder, i);
                    if (!otcOperationsIndexFolder.isDirectory()) {
                        super.response = list;
                        return;
                    }
                    for (String id : ids) {
                        if (new File(otcOperationsIndexFolder, id + ".json").isFile()) {
                            newIds.add(id);
                        }
                    }
                } else {
                    for (File otcOperationsIndexFolder : debitCardsIndexesFolder.listFiles()) {
                        if (!otcOperationsIndexFolder.isDirectory()) {
                            continue;
                        }
                        for (String id : ids) {
                            if (new File(otcOperationsIndexFolder, id + ".json").isFile()) {
                                newIds.add(id);
                            }
                        }
                    }
                }
                ids.retainAll(newIds);
            }
        }
        //get list
        for (String id : ids) {
            File debitCardConfigFile = DebitCardsFolderLocator.getConfigFile(id);
            try {
                JsonNode debitCardConfig = mapper.readTree(debitCardConfigFile);
                String timestamp = debitCardConfig.get("timestamp").textValue();
                debitCardListRequest.setFinalTimestamp(DateUtil.getDateDaysAfter(DateUtil.getDayStartDate(debitCardListRequest.getFinalTimestamp()), 1));
                if (debitCardListRequest.getInitTimestamp() != null && timestamp.compareTo(debitCardListRequest.getInitTimestamp()) < 0) {
                    continue;
                }
                if (debitCardListRequest.getFinalTimestamp() != null && timestamp.compareTo(debitCardListRequest.getFinalTimestamp()) >= 0) {
                    continue;
                }
                ((ObjectNode) debitCardConfig).set("balance", BaseOperation.getBalance(DebitCardsFolderLocator.getBalanceFolder(id)));
                if (!debitCardConfig.has("secretKey")) {
                    ((ObjectNode) debitCardConfig).put("secretKey", BaseOperation.getId());
                    FileUtil.editFile(debitCardConfig, debitCardConfigFile);
                }
                list.add(debitCardConfig);
            } catch (IOException ex) {
                Logger.getLogger(DebitCardList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = list;
    }

}
