
package com.dollarbtc.backend.cryptocurrency.exchange.dto;

import java.util.List;
import java.util.Map;

/**
 *
 * @author CarlosDaniel
 */
public class OrderInterval {

    private final String startTimestamp, endTimestamp, intervalAlgorithmName;
    private final Map<String, Integer> orderSummary; //Order.Type__Order.AlgorithmType__Order.TradingType
    private final List<Order> orders; //without out order

    public OrderInterval(
            String startTimestamp,
            String endTimestamp,
            String intervalAlgorithmName,
            Map<String, Integer> orderSummary, 
            List<Order> orders
    ) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.intervalAlgorithmName = intervalAlgorithmName;
        this.orderSummary = orderSummary;
        this.orders = orders;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public String getIntervalAlgorithmName() {
        return intervalAlgorithmName;
    }
    
    public Map<String, Integer> getOrderSummary() {
        return orderSummary;
    }

    public List<Order> getOrders() {
        if(orders == null || orders.size() <= 1){
            return null;
        }
        return orders.subList(1, orders.size());
    }

    public Order getOutOrder() {
        if(orders == null || orders.isEmpty()){
            return null;
        }
        return orders.get(0);
    }
    
}
