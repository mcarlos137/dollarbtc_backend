/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.moneymarket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class MoneyMarketTakeOrderRequest {

    private String userName, id, nickName;
    private Double amount, price;
    private boolean bot;
    private JsonNode payment;

    public MoneyMarketTakeOrderRequest() {
    }

    public MoneyMarketTakeOrderRequest(String userName, String id, String nickName, Double amount, Double price, boolean bot) {
        this.userName = userName;
        this.id = id;
        this.nickName = nickName;
        this.amount = amount;
        this.price = price;
        this.bot = bot;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
        order.put("id", id);
        order.put("nickName", nickName);
        order.put("amount", amount);
        order.put("price", price);
        order.put("bot", bot);
        if (payment != null) {
            order.set("payment", payment);
        }
        return order;
    }

}
