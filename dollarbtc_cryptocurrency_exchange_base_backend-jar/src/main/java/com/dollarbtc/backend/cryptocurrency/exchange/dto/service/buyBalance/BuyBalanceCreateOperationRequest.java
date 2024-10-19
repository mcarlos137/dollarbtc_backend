/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.buyBalance;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class BuyBalanceCreateOperationRequest {
    
    private String userName, currency, message, description;
    private Double amount;
    private JsonNode dollarBTCPayment, clientPayment;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonNode getDollarBTCPayment() {
        return dollarBTCPayment;
    }

    public void setDollarBTCPayment(JsonNode dollarBTCPayment) {
        this.dollarBTCPayment = dollarBTCPayment;
    }

    public JsonNode getClientPayment() {
        return clientPayment;
    }

    public void setClientPayment(JsonNode clientPayment) {
        this.clientPayment = clientPayment;
    }
    
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("message", this.message);
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("currency", this.currency);
        ((ObjectNode) jsonNode).put("description", this.description);
        ((ObjectNode) jsonNode).put("amount", this.amount);
        ((ObjectNode) jsonNode).put("otcOperationType", OTCOperationType.MC_BUY_BALANCE.name());
        if(this.dollarBTCPayment != null){
            ((ObjectNode) jsonNode).put("dollarBTCPayment", this.dollarBTCPayment);
        }
        if(this.clientPayment != null){
            ((ObjectNode) jsonNode).put("clientPayment", this.clientPayment);
        }
        return jsonNode;
    }
                   
}
