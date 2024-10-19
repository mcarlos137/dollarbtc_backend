/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneycall;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneycall.MoneyCallCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyCallsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class MoneyCallCreate extends AbstractOperation<String> {

    private final MoneyCallCreateRequest moneyCallCreateRequest;

    public MoneyCallCreate(MoneyCallCreateRequest moneyCallCreateRequest) {
        super(String.class);
        this.moneyCallCreateRequest = moneyCallCreateRequest;
    }

    @Override
    public void execute() {
        String id = BaseOperation.getId();
        File moneyCallFile = new File(MoneyCallsFolderLocator.getFolder(), id + ".json");
        JsonNode moneyCall = moneyCallCreateRequest.toJsonNode();
        ((ObjectNode) moneyCall).put("id", id);
        ((ObjectNode) moneyCall).put("createTimestamp", DateUtil.getCurrentDate());
        ((ObjectNode) moneyCall).put("status", "CREATED");
        FileUtil.createFile(moneyCall, moneyCallFile);
        createIndexesFolder(moneyCall);
        //createNotificationThread(moneyCall);
        super.response = "OK";
    }

    private static void createIndexesFolder(JsonNode moneyCall) {
        ObjectNode index = new ObjectMapper().createObjectNode();
        String id = moneyCall.get("id").textValue();
        index.put("id", id);
        index.put("timestamp", moneyCall.get("createTimestamp").textValue());
        //CreateUserName index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("CreateUserNames", moneyCall.get("createUserName").textValue()), id + ".json"));
        //SenderUserName index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("SenderUserNames", moneyCall.get("senderUserName").textValue()), id + ".json"));
        //ReceiverUserName index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("ReceiverUserNames", moneyCall.get("receiverUserName").textValue()), id + ".json"));
        //Currency index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("Currencies", moneyCall.get("currency").textValue()), id + ".json"));
        //CreateTimestamps index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("CreateTimestamps", moneyCall.get("createTimestamp").textValue()), id + ".json"));
        //Rate index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("Rates", Double.toString(moneyCall.get("rate").doubleValue())), id + ".json"));
        //Status index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("Statuses", moneyCall.get("status").textValue()), id + ".json"));
        //Time index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("Times", "not_defined"), id + ".json"));
        //Stars index
        FileUtil.createFile(index, new File(MoneyCallsFolderLocator.getIndexValueFolder("Stars", "not_defined"), id + ".json"));
    }
    
    private void createNotificationThread(JsonNode moneyCall) {
        Thread createNotificationThread = new Thread(() -> {
            String notificationUserName = moneyCall.get("receiverUserName").textValue();
            String notificationName = moneyCall.get("receiverName").textValue();
            String notificationVerb = "pay";
            if(notificationUserName.equals(moneyCall.get("createUserName").textValue())){
                notificationUserName = moneyCall.get("senderUserName").textValue();
                notificationName = moneyCall.get("senderName").textValue();
                notificationVerb = "charge";
            }
            String amount = String.format("%.2f", (moneyCall.get("rate").doubleValue() * moneyCall.get("estimatedTime").intValue()));
            String message = notificationName + " wants a Money Call. This call is going to " + notificationVerb + " you approximate of " + amount + " " + moneyCall.get("currency").textValue() + " please go to Money Call section for more details.";
            new NotificationSendMessageByUserName(notificationUserName, "Money Call", message).getResponse();
        });
        createNotificationThread.start();
    }

}
