/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OrderType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author CarlosDaniel
 */
public class MoneyMarketPostOrderRequest {

    private String userName, pair, timeUnit, nickName, conditions, paymentType, source;
    private Double amount, price, priceMargin;
    private int time;
    private OrderType type;
    private boolean bot;
    private JsonNode payment;

    public MoneyMarketPostOrderRequest() {
    }

    public MoneyMarketPostOrderRequest(String userName, String pair, String nickName, String source, Double amount, Double price, int time, String timeUnit, OrderType type, boolean bot) {
        this.userName = userName;
        this.pair = pair;
        this.timeUnit = timeUnit;
        this.nickName = nickName;
        this.source = source;
        this.amount = amount;
        this.price = price;
        this.time = time;
        this.type = type;
        this.bot = bot;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPriceMargin() {
        return priceMargin;
    }

    public void setPriceMargin(Double priceMargin) {
        this.priceMargin = priceMargin;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public JsonNode getPayment() {
        return payment;
    }

    public void setPayment(JsonNode payment) {
        this.payment = payment;
    }

    public JsonNode toJsonNode() {
        ObjectNode order = new ObjectMapper().createObjectNode();
        order.put("userName", userName);
        order.put("nickName", nickName);
        order.put("pair", pair);
        order.put("type", type.name());
        order.put("source", source);
        order.put("amount", amount);
        order.put("price", price);
        order.put("time", time);
        order.put("timeUnit", timeUnit);
        order.put("bot", bot);
        if (priceMargin != null) {
            order.put("priceMargin", priceMargin);
        }
        if (conditions != null) {
            order.put("conditions", conditions);
        }
        if (conditions != null) {
            order.put("paymentType", paymentType);
        }
        if (payment != null) {
            order.set("payment", payment);
        }
        return order;
    }
    
}
