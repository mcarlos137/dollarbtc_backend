/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.payment;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentBank;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.PaymentType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class PaymentCreateRequest implements Serializable, Cloneable {

    private String userName, currency, bankLogin, bankPassword;
    private PaymentBank paymentBank;
    private PaymentType paymentType;

    public PaymentCreateRequest() {
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
    
    public String getBankLogin() {
        return bankLogin;
    }

    public void setBankLogin(String bankLogin) {
        this.bankLogin = bankLogin;
    }

    public String getBankPassword() {
        return bankPassword;
    }

    public void setBankPassword(String bankPassword) {
        this.bankPassword = bankPassword;
    }
    
    public PaymentBank getPaymentBank() {
        return paymentBank;
    }

    public void setPaymentBank(PaymentBank paymentBank) {
        this.paymentBank = paymentBank;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
            
}
