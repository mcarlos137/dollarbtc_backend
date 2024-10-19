/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broker;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class BrokerEditOfferRequest implements Serializable, Cloneable {

    private String userName, currency, paymentId;
    private Double price, minPerOperationAmount, maxPerOperationAmount, totalAmount;
    private OfferType offerType;
    private PaymentType paymentType;

    public BrokerEditOfferRequest() {
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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
}
