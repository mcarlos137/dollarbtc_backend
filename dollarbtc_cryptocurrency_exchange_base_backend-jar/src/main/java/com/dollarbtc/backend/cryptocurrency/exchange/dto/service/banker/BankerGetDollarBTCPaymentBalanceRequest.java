/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class BankerGetDollarBTCPaymentBalanceRequest implements Serializable, Cloneable {

    private String userName, currency;
    private String[] paymentIds;

    public BankerGetDollarBTCPaymentBalanceRequest() {
    }

    public BankerGetDollarBTCPaymentBalanceRequest(String userName, String currency, String[] paymentIds) {
        this.userName = userName;
        this.currency = currency;
        this.paymentIds = paymentIds;
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
    
}
