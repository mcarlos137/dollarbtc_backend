/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCGetDollarBTCPaymentBalanceRequest implements Serializable, Cloneable {

    private String userName, currency;
    private String[] paymentIds;
    private String initTimestamp, finalTimestamp;

    public OTCGetDollarBTCPaymentBalanceRequest() {
    }

    public OTCGetDollarBTCPaymentBalanceRequest(String userName, String currency, String[] paymentIds) {
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

    public String getInitTimestamp() {
        return initTimestamp;
    }

    public void setInitTimestamp(String initTimestamp) {
        this.initTimestamp = initTimestamp;
    }

    public String getFinalTimestamp() {
        return finalTimestamp;
    }

    public void setFinalTimestamp(String finalTimestamp) {
        this.finalTimestamp = finalTimestamp;
    }
        
}
