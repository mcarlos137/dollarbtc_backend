/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.OfferType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCEditDynamicOfferRequest implements Serializable, Cloneable {

    private String currency, paymentId, source;
    private Double limitPrice, marginPercent, spreadPercent, minPerOperationAmount, maxPerOperationAmount, totalAmount;
    private OfferType offerType;
    private PaymentType paymentType;
    private Boolean useChangePriceByOperationBalance;

    public OTCEditDynamicOfferRequest() {
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public Double getMarginPercent() {
        return marginPercent;
    }

    public void setMarginPercent(Double marginPercent) {
        this.marginPercent = marginPercent;
    }

    public Double getSpreadPercent() {
        return spreadPercent;
    }

    public void setSpreadPercent(Double spreadPercent) {
        this.spreadPercent = spreadPercent;
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

    public Boolean getUseChangePriceByOperationBalance() {
        return useChangePriceByOperationBalance;
    }

    public void setUseChangePriceByOperationBalance(Boolean useChangePriceByOperationBalance) {
        this.useChangePriceByOperationBalance = useChangePriceByOperationBalance;
    }
    
}
