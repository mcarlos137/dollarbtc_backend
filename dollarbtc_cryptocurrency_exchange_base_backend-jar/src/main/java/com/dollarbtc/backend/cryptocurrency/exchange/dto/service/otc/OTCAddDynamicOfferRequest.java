/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc;

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
public class OTCAddDynamicOfferRequest implements Serializable, Cloneable {

    private String currency, paymentId, source;
    private Double limitPrice, marginPercent, spreadPercent, minPerOperationAmount, maxPerOperationAmount, totalAmount;
    private OfferType offerType;
    private PaymentType paymentType;
    private boolean useChangePriceByOperationBalance;
    
    public OTCAddDynamicOfferRequest() {
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

    public boolean isUseChangePriceByOperationBalance() {
        return useChangePriceByOperationBalance;
    }

    public void setUseChangePriceByOperationBalance(boolean useChangePriceByOperationBalance) {
        this.useChangePriceByOperationBalance = useChangePriceByOperationBalance;
    }
                   
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("currency", this.currency);
        ((ObjectNode) jsonNode).put("paymentId", this.paymentId);
        ((ObjectNode) jsonNode).put("source", this.source);
        ((ObjectNode) jsonNode).put("limitPrice", this.limitPrice);
        ((ObjectNode) jsonNode).put("marginPercent", this.marginPercent);
        ((ObjectNode) jsonNode).put("spreadPercent", this.spreadPercent);
        ((ObjectNode) jsonNode).put("minPerOperationAmount", this.minPerOperationAmount);
        ((ObjectNode) jsonNode).put("maxPerOperationAmount", this.maxPerOperationAmount);
        ((ObjectNode) jsonNode).put("totalAmount", this.totalAmount);
        ((ObjectNode) jsonNode).put("offerType", this.offerType.name());
        ((ObjectNode) jsonNode).put("paymentType", this.paymentType.name());
        ((ObjectNode) jsonNode).put("useChangePriceByOperationBalance", this.useChangePriceByOperationBalance);
        return jsonNode;
    }
    
}
