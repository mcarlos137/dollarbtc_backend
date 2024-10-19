/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class BankerGetDollarBTCPaymentBalanceMovementsRequest implements Serializable, Cloneable {

    private String userName, currency, initTimestamp, endTimestamp;
    private String[] paymentIds;
    private BalanceOperationType balanceOperationType;
    
    public BankerGetDollarBTCPaymentBalanceMovementsRequest() {
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
    
    public String[] getPaymentIds() {
        return paymentIds;
    }

    public void setPaymentIds(String[] paymentIds) {
        this.paymentIds = paymentIds;
    } 

    public String getInitTimestamp() {
        return initTimestamp;
    }

    public void setInitTimestamp(String initTimestamp) {
        this.initTimestamp = initTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public BalanceOperationType getBalanceOperationType() {
        return balanceOperationType;
    }

    public void setBalanceOperationType(BalanceOperationType balanceOperationType) {
        this.balanceOperationType = balanceOperationType;
    }
        
}
