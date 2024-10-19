/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.masteraccount;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MasterAccountTransferBetweenMastersRequest implements Serializable, Cloneable {

    private String masterAccountBaseName, masterAccountTargetName, currency;
    private double amount;
    private boolean compensateMoneyclick;
    private String moneyclickCompensationCurrency;
    private Double moneyclickCompensationAmount;
    
    public MasterAccountTransferBetweenMastersRequest() {
    }

    public String getMasterAccountBaseName() {
        return masterAccountBaseName;
    }

    public void setMasterAccountBaseName(String masterAccountBaseName) {
        this.masterAccountBaseName = masterAccountBaseName;
    }

    public String getMasterAccountTargetName() {
        return masterAccountTargetName;
    }

    public void setMasterAccountTargetName(String masterAccountTargetName) {
        this.masterAccountTargetName = masterAccountTargetName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isCompensateMoneyclick() {
        return compensateMoneyclick;
    }

    public void setCompensateMoneyclick(boolean compensateMoneyclick) {
        this.compensateMoneyclick = compensateMoneyclick;
    }

    public String getMoneyclickCompensationCurrency() {
        return moneyclickCompensationCurrency;
    }

    public void setMoneyclickCompensationCurrency(String moneyclickCompensationCurrency) {
        this.moneyclickCompensationCurrency = moneyclickCompensationCurrency;
    }

    public Double getMoneyclickCompensationAmount() {
        return moneyclickCompensationAmount;
    }

    public void setMoneyclickCompensationAmount(Double moneyclickCompensationAmount) {
        this.moneyclickCompensationAmount = moneyclickCompensationAmount;
    }
    
    
        
}
