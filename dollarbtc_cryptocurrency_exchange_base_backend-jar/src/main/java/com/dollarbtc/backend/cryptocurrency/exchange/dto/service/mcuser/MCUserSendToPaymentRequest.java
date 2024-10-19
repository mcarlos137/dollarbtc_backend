/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author CarlosDaniel
 */
public class MCUserSendToPaymentRequest {
    
    private String userName, currency, description, operatorName;
    private Double amount;
    private JsonNode payment;
    private PaymentType paymentType;
    private boolean multiOperator;
    

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

    public JsonNode getPayment() {
        return payment;
    }

    public void setPayment(JsonNode payment) {
        this.payment = payment;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }    

    public boolean isMultiOperator() {
        return multiOperator;
    }

    public void setMultiOperator(boolean multiOperator) {
        this.multiOperator = multiOperator;
    }
                       
}
