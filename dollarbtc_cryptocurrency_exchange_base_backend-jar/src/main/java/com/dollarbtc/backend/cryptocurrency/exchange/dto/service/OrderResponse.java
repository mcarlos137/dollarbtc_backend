/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.Order;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.OrderInterval;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author CarlosDaniel
 */
public class OrderResponse implements Serializable {

    private final List<Order> orders;
    private final List<OrderInterval> orderIntervals;

    public OrderResponse(List<Order> orders, List<OrderInterval> orderIntervals) {
        this.orders = orders;
        this.orderIntervals = orderIntervals;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<OrderInterval> getOrderIntervals() {
        return orderIntervals;
    }

}
