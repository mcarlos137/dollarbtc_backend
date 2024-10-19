/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.data.ExchangeAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.OrderInterval;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CarlosDaniel
 */
public class OrderBlockingCondition {

    private boolean used;
    private boolean blocked;
    private int maxLossMaxQuantity;
    private int maxQuantity;

    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setMaxLossMaxQuantity(int maxLossMaxQuantity) {
        this.maxLossMaxQuantity = maxLossMaxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    private String getLastTimestamp(File algorithmBotNameFolder) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(new File(algorithmBotNameFolder, "orderBlockingCondition.json"));
            if (jsonNode == null || jsonNode.get("lastTimestamp") == null) {
                return null;
            }
            return jsonNode.get("lastTimestamp").textValue();
        } catch (IOException ex) {
        }
        return null;
    }

    private void setLastTimestamp(String orderBlockingConditionLastTimestamp, File algorithmBotNameFolder) {
        File file = new File(algorithmBotNameFolder, "orderBlockingCondition.json");
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("lastTimestamp", orderBlockingConditionLastTimestamp);
        FileUtil.editFile(jsonNode, file);
    }

    public boolean pass(String exchangeId, String symbol, String botName, String intervalAlgorithmName, Map<String, OrderBlockingCondition.Action> orderBlockingConditionActionByIntervalAlgorithmName, File algorithmBotNameFolder) {
        if (!used) {
            return true;
        }
        boolean pass = true;
        Action action = orderBlockingConditionActionByIntervalAlgorithmName.get(intervalAlgorithmName);
        if (action.equals(Action.IN) || action.equals(Action.IN_RESET)) {
            return true;
        } else {
            if (blocked) {
                pass = false;
            } else {
                List<OrderInterval> orderIntervals = new ArrayList<>();
                orderIntervals.addAll(LocalData.getOrderIntervals(exchangeId, symbol, botName, null, null, 100, 100));
                List<Order> outOrders = new ArrayList<>();
                for (OrderInterval orderInterval : orderIntervals) {
                    outOrders.add(orderInterval.getOutOrder());
                }
                outOrders.sort((Order o1, Order o2) -> (int) (DateUtil.parseDate(o2.getTimestamp()).getTime() - DateUtil.parseDate(o1.getTimestamp()).getTime()));
                int maxLossOutOrders = 0;
                String orderBlockingConditionLastTimestamp = getLastTimestamp(algorithmBotNameFolder);
                if (orderBlockingConditionLastTimestamp == null) {
                    maxLossOutOrders++;
                }
                for (Order outOrder : outOrders) {
                    if (orderBlockingConditionLastTimestamp != null && DateUtil.parseDate(outOrder.getTimestamp()).before(DateUtil.parseDate(orderBlockingConditionLastTimestamp))) {
                        continue;
                    }
                    if (outOrder.getAlgorithmType().equals(Order.AlgorithmType.MAX_LOSS_LOSSING) || outOrder.getAlgorithmType().equals(Order.AlgorithmType.MAX_LOSS_RESERVING)) {
                        maxLossOutOrders++;
                    }
                }
                if (maxLossOutOrders > maxLossMaxQuantity) {
                    blocked = true;
                    pass = false;
                }
            }
        }
        return pass;
    }

    public void reset(File algorithmBotNameFolder, String timestamp) {
        blocked = false;
        setLastTimestamp(timestamp, algorithmBotNameFolder);
    }

    @Override
    public String toString() {
        return "[used=" + used
                + ", blocked=" + blocked
                + ", maxLossMaxQuantity=" + maxLossMaxQuantity
                + ", maxQuantity=" + maxQuantity
                + "]";
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("used", used);
        ((ObjectNode) jsonNode).put("blocked", blocked);
        return jsonNode;
    }

    public enum Action {

        NONE,
        IN,
        IN_RESET;

    }

}
