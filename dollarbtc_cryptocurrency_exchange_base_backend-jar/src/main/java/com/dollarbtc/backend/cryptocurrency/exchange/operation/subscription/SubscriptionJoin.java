/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription.SubscriptionJoinRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscriptionevent.SubscriptionEventCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionEventType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscriptionevent.SubscriptionEventCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.SubscriptionsFolderLocator;
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
public class SubscriptionJoin extends AbstractOperation<String> {

    private final SubscriptionJoinRequest subscriptionJoinRequest;

    public SubscriptionJoin(SubscriptionJoinRequest subscriptionJoinRequest) {
        super(String.class);
        this.subscriptionJoinRequest = subscriptionJoinRequest;
    }

    @Override
    protected void execute() {
        File subscriptionFile = SubscriptionsFolderLocator.getFile(subscriptionJoinRequest.getId());
        if (subscriptionFile.isFile()) {
            try {
                JsonNode subscription = mapper.readTree(subscriptionFile);
                if (SubscriptionStatus.valueOf(subscription.get("status").textValue()).equals(SubscriptionStatus.ACTIVE)) {
                    super.response = "SUBSCRIPTION ALREADY JOINED";
                    return;
                } else {
                    if (subscriptionJoinRequest.getAmount() != null && subscriptionJoinRequest.getPeriodInMonths() != null) {
                        String subsctractToBalance = BaseOperation.substractToBalance(
                                UsersFolderLocator.getMCBalanceFolder(subscriptionJoinRequest.getTargetUserName()),
                                "USD",
                                subscriptionJoinRequest.getAmount(),
                                BalanceOperationType.SUBSCRIPTION_JOIN,
                                BalanceOperationStatus.OK,
                                null,
                                null,
                                false,
                                null,
                                false,
                                null
                        );
                        if (!subsctractToBalance.equals("OK")) {
                            super.response = subsctractToBalance;
                            return;
                        }
                        BaseOperation.addToBalance(
                                UsersFolderLocator.getMCBalanceFolder(subscriptionJoinRequest.getBaseUserName()),
                                "USD",
                                subscriptionJoinRequest.getAmount(),
                                BalanceOperationType.SUBSCRIPTION_JOIN,
                                BalanceOperationStatus.OK,
                                null,
                                null,
                                null,
                                true,
                                null
                        );
                    }
                    String initialTimestamp = DateUtil.getCurrentDate();
                    if (subscriptionJoinRequest.getPeriodInMonths() != null) {
                        ((ObjectNode) subscription).put("initialTimestamp", initialTimestamp);
                        ((ObjectNode) subscription).put("finalTimestamp", DateUtil.getDateMonthsAfter(initialTimestamp, subscriptionJoinRequest.getPeriodInMonths()));
                    }
                    ((ObjectNode) subscription).put("status", SubscriptionStatus.ACTIVE.name());
                    FileUtil.editFile(subscription, subscriptionFile);
                    JsonNode index = mapper.createObjectNode();
                    ((ObjectNode) index).put("id", subscriptionJoinRequest.getId());
                    ((ObjectNode) index).put("timestamp", DateUtil.getCurrentDate());
                    FileUtil.deleteFile(new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("Statuses", SubscriptionStatus.INACTIVE.name()), subscriptionJoinRequest.getId() + ".json"));
                    FileUtil.editFile(index, new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("Statuses", SubscriptionStatus.ACTIVE.name()), subscriptionJoinRequest.getId() + ".json"));

                }
            } catch (IOException ex) {
                Logger.getLogger(SubscriptionJoin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (subscriptionJoinRequest.getAmount() != null) {
                String subsctractToBalance = BaseOperation.substractToBalance(
                        UsersFolderLocator.getMCBalanceFolder(subscriptionJoinRequest.getTargetUserName()),
                        "USD",
                        subscriptionJoinRequest.getAmount(),
                        BalanceOperationType.SUBSCRIPTION_JOIN,
                        BalanceOperationStatus.OK,
                        null,
                        null,
                        false,
                        null,
                        false,
                        null
                );
                if (!subsctractToBalance.equals("OK")) {
                    super.response = subsctractToBalance;
                    return;
                }
                BaseOperation.addToBalance(
                        UsersFolderLocator.getMCBalanceFolder(subscriptionJoinRequest.getBaseUserName()),
                        "USD",
                        subscriptionJoinRequest.getAmount(),
                        BalanceOperationType.SUBSCRIPTION_JOIN,
                        BalanceOperationStatus.OK,
                        null,
                        null,
                        null,
                        true,
                        null
                );
            }
            String initialTimestamp = DateUtil.getCurrentDate();
            JsonNode subscription = subscriptionJoinRequest.toJsonNode();
            ((ObjectNode) subscription).put("timestamp", initialTimestamp);
            if (subscriptionJoinRequest.getPeriodInMonths() != null) {
                ((ObjectNode) subscription).put("initialTimestamp", initialTimestamp);
                ((ObjectNode) subscription).put("finalTimestamp", DateUtil.getDateMonthsAfter(initialTimestamp, subscriptionJoinRequest.getPeriodInMonths()));
            }
            ((ObjectNode) subscription).put("status", SubscriptionStatus.ACTIVE.name());
            FileUtil.createFile(subscription, subscriptionFile);
            addIndexes(subscriptionJoinRequest.toJsonNode());
            subscriptionEventThread(
                    subscriptionJoinRequest.getBaseUserName(),
                    subscriptionJoinRequest.getTargetUserName(),
                    subscriptionJoinRequest.getBaseName(),
                    subscriptionJoinRequest.getTargetName(),
                    subscriptionJoinRequest.getObjectDetails(),
                    SubscriptionEventType.valueOf(subscriptionJoinRequest.getSubscriptionType().name() + "__CREATE")
            );
        }
        super.response = "OK";
    }

    private void addIndexes(JsonNode subscription) {
        JsonNode index = mapper.createObjectNode();
        String id = subscription.get("id").textValue();
        ((ObjectNode) index).put("id", id);
        ((ObjectNode) index).put("timestamp", DateUtil.getCurrentDate());
        String objectDetailsId = "not_defined";
        if(subscription.has("objectDetails") && subscription.get("objectDetails").has("id")){
            objectDetailsId = subscription.get("objectDetails").get("id").textValue();
        }
        //BaseUserNames
        FileUtil.createFile(index, new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("BaseUserNames", subscription.get("baseUserName").textValue()), id + ".json"));
        //TargetUserNames
        FileUtil.createFile(index, new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("TargetUserNames", subscription.get("targetUserName").textValue()), id + ".json"));
        //Types
        FileUtil.createFile(index, new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("Types", subscription.get("type").textValue()), id + ".json"));
        //Statuses
        FileUtil.createFile(index, new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("Statuses", SubscriptionStatus.ACTIVE.name()), id + ".json"));
        //ObjectDetailsIds
        FileUtil.createFile(index, new File(SubscriptionsFolderLocator.getIndexesSpecificFolder("ObjectDetailsIds", objectDetailsId), id + ".json"));
    }

    private void subscriptionEventThread(String baseUserName, String targetUserName, String baseName, String targetName, JsonNode objectDetails, SubscriptionEventType subscriptionEventType) {
        Thread subscriptionEventThread = new Thread(() -> {
            new SubscriptionEventCreate(
                    new SubscriptionEventCreateRequest(
                            baseUserName,
                            targetUserName,
                            baseName,
                            targetName,
                            objectDetails,
                            subscriptionEventType
                    )
            ).getResponse();
        });
        subscriptionEventThread.start();
    }

}
