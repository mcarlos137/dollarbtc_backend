/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcmc;

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
public class OTCMCCreateOperationRequest implements Serializable, Cloneable {

    private String userName, currency, message, description;
    private Double amount;
    private JsonNode dollarBTCPayment, clientPayment;

    public OTCMCCreateOperationRequest() {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public JsonNode getDollarBTCPayment() {
        return dollarBTCPayment;
    }

    public void setDollarBTCPayment(JsonNode dollarBTCPayment) {
        this.dollarBTCPayment = dollarBTCPayment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JsonNode getClientPayment() {
        return clientPayment;
    }

    public void setClientPayment(JsonNode clientPayment) {
        this.clientPayment = clientPayment;
    }
                         
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("message", this.message);
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("currency", this.currency);
        ((ObjectNode) jsonNode).put("description", this.description);
        ((ObjectNode) jsonNode).put("amount", this.amount);
        if(this.dollarBTCPayment != null){
            ((ObjectNode) jsonNode).put("dollarBTCPayment", this.dollarBTCPayment);
        }
        if(this.clientPayment != null){
            ((ObjectNode) jsonNode).put("clientPayment", this.clientPayment);
        }
        return jsonNode;
    }
    
}
