/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.moneymarket;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket.MoneyMarketGetOrdersRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OrderType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyMarketFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MoneyMarketGetOrders extends AbstractOperation<JsonNode> {

    private final MoneyMarketGetOrdersRequest moneyMarketGetOrdersRequest;

    public MoneyMarketGetOrders(MoneyMarketGetOrdersRequest moneyMarketGetOrdersRequest) {
        super(JsonNode.class);
        this.moneyMarketGetOrdersRequest = moneyMarketGetOrdersRequest;
    }

    @Override
    protected void execute() {
        JsonNode orders = mapper.createObjectNode();
        if (moneyMarketGetOrdersRequest.getId() != null) {
            addOrder(moneyMarketGetOrdersRequest.getId(), orders, moneyMarketGetOrdersRequest.isOld(), mapper);
            super.response = orders;
            return;
        }
        if (moneyMarketGetOrdersRequest.getUserName() != null) {
            File moneyMarketUserNameFolder = MoneyMarketFolderLocator.getUserNameFolder(moneyMarketGetOrdersRequest.getUserName());
            for (File moneyMarketUserNameIndexFile : moneyMarketUserNameFolder.listFiles()) {
                if (!moneyMarketUserNameIndexFile.isFile()) {
                    continue;
                }
                JsonNode moneyMarketUserNameIndex = null;
                try {
                    moneyMarketUserNameIndex = mapper.readTree(moneyMarketUserNameIndexFile);
                } catch (IOException ex) {
                    Logger.getLogger(MoneyMarketGetOrders.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (moneyMarketUserNameIndex != null) {
                    String id = moneyMarketUserNameIndex.get("id").textValue();
                    addOrder(id, orders, moneyMarketGetOrdersRequest.isOld(), mapper);
                }
            }
            super.response = orders;
            return;
        }
        if (moneyMarketGetOrdersRequest.getPair() != null && moneyMarketGetOrdersRequest.getTypes() != null) {
            for (OrderType orderType : moneyMarketGetOrdersRequest.getTypes()) {
                File moneyMarketPairTypeFolder = MoneyMarketFolderLocator.getPairTypeFolder(moneyMarketGetOrdersRequest.getPair(), orderType.name());
                for (File moneyMarketPairTypeIndexFile : moneyMarketPairTypeFolder.listFiles()) {
                    if (!moneyMarketPairTypeIndexFile.isFile()) {
                        continue;
                    }
                    JsonNode moneyMarketPairTypeIndex = null;
                    try {
                        moneyMarketPairTypeIndex = mapper.readTree(moneyMarketPairTypeIndexFile);
                    } catch (IOException ex) {
                        Logger.getLogger(MoneyMarketGetOrders.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (moneyMarketPairTypeIndex != null) {
                        String id = moneyMarketPairTypeIndex.get("id").textValue();
                        addOrder(id, orders, moneyMarketGetOrdersRequest.isOld(), mapper);
                    }
                }
            }
            super.response = orders;
            return;
        }
        super.response = orders;
    }

    private static void addOrder(String id, JsonNode orders, boolean isOld, ObjectMapper mapper) {
        File moneyMarketOrderFile = MoneyMarketFolderLocator.getOrderFile(id);
        JsonNode moneyMarketOrder = null;
        if (moneyMarketOrderFile.isFile()) {
            try {
                moneyMarketOrder = mapper.readTree(moneyMarketOrderFile);
                if (isOld && (!moneyMarketOrder.has("closed"))) {
                    return;
                }
                OrderType orderType = OrderType.valueOf(moneyMarketOrder.get("type").textValue());
                if (!orders.has(orderType.name())) {
                    ((ObjectNode) orders).set(orderType.name(), mapper.createArrayNode());
                }
                ((ArrayNode) orders.get(orderType.name())).add(moneyMarketOrder);
            } catch (IOException ex) {
                Logger.getLogger(MoneyMarketGetOrders.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
