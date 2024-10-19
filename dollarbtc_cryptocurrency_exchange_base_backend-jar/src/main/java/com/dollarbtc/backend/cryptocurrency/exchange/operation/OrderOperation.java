/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.OrderInterval;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.CollectionOrderByDate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author CarlosDaniel
 */
public class OrderOperation {
    
    public static List<Order> getOrders(String exchangeId, String symbol, String modelName, String initDate, String endDate, CollectionOrderByDate collectionOrderByDate) {
        List<Order> orders = new ArrayList<>();
        orders.addAll(LocalData.getOrders(exchangeId, symbol, modelName, initDate, endDate, 100));
        if (collectionOrderByDate.equals(CollectionOrderByDate.ASC)) {
            Collections.reverse(orders);
        }
        return orders;
    }
    
    public static List<OrderInterval> getOrderIntervals(String exchangeId, String symbol, String modelName, String initDate, String endDate, CollectionOrderByDate collectionOrderByDate) {
        List<OrderInterval> orderIntervals = new ArrayList<>();
        orderIntervals.addAll(LocalData.getOrderIntervals(exchangeId, symbol, modelName, initDate, endDate, 100, 100));
        if (collectionOrderByDate.equals(CollectionOrderByDate.ASC)) {
            Collections.reverse(orderIntervals);
            orderIntervals.stream().filter((orderInterval) -> !(orderInterval.getOrders() == null)).forEach((orderInterval) -> {
                Collections.reverse(orderInterval.getOrders());
            });
        }
        return orderIntervals;
    }
    
    public static JsonNode getOrderAlgorithmTypes(){
        JsonNode result = new ObjectMapper().createObjectNode();
        for(Order.AlgorithmType algorithmType : Order.AlgorithmType.values()){
            ((ObjectNode) result).put(algorithmType.name(), algorithmType.getDescription());
        }
        return result;
    }
    
}
