/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class BankerEditDollarBTCPaymentRequest implements Serializable, Cloneable {

    private String id, userName, currency;
    private JsonNode payment;

    public BankerEditDollarBTCPaymentRequest() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public JsonNode getPayment() {
        return payment;
    }

    public void setPayment(JsonNode payment) {
        this.payment = payment;
    }
    
}
