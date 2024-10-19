/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class BrokerAddOfferRequest implements Serializable, Cloneable {

    private String userName, currency, paymentId;
    private Double price, minPerOperationAmount, maxPerOperationAmount, totalAmount;
    private OfferType offerType;
    private PaymentType paymentType;

    public BrokerAddOfferRequest() {
    }

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

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }    
    
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getMinPerOperationAmount() {
        return minPerOperationAmount;
    }

    public void setMinPerOperationAmount(Double minPerOperationAmount) {
        this.minPerOperationAmount = minPerOperationAmount;
    }

    public Double getMaxPerOperationAmount() {
        return maxPerOperationAmount;
    }

    public void setMaxPerOperationAmount(Double maxPerOperationAmount) {
        this.maxPerOperationAmount = maxPerOperationAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OfferType getOfferType() {
        return offerType;
    }

    public void setOfferType(OfferType offerType) {
        this.offerType = offerType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
                   
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("currency", this.currency);
        ((ObjectNode) jsonNode).put("paymentId", this.paymentId);
        ((ObjectNode) jsonNode).put("price", this.price);
        ((ObjectNode) jsonNode).put("minPerOperationAmount", this.minPerOperationAmount);
        ((ObjectNode) jsonNode).put("maxPerOperationAmount", this.maxPerOperationAmount);
        ((ObjectNode) jsonNode).put("totalAmount", this.totalAmount);
        ((ObjectNode) jsonNode).put("offerType", this.offerType.name());
        ((ObjectNode) jsonNode).put("paymentType", this.paymentType.name());
        return jsonNode;
    }
    
}
