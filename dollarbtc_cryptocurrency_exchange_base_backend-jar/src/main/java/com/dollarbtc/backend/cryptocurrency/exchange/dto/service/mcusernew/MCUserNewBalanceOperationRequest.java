/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcusernew;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCUserNewBalanceOperationRequest implements Serializable, Cloneable {
    
    private String userName, address, privateKey, targetAddress, additionalInfo;
    private BalanceOperationType balanceOperationType;
    private PaymentType paymentType;
    private Map<String, Double> amounts = new HashMap<>();
    
    public MCUserNewBalanceOperationRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    public BalanceOperationType getBalanceOperationType() {
        return balanceOperationType;
    }

    public void setBalanceOperationType(BalanceOperationType balanceOperationType) {
        this.balanceOperationType = balanceOperationType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public Map<String, Double> getAmounts() {
        return amounts;
    }

    public void setAmounts(Map<String, Double> amounts) {
        this.amounts = amounts;
    }    
                
}
