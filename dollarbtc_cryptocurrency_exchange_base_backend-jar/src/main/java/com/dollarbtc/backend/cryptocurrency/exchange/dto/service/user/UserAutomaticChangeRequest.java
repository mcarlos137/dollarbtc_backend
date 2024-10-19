/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserAutomaticChangeRequest implements Serializable, Cloneable {
        
    private String userName, currency;
    private boolean activate;
    private Double markedPrice, alertBand;
    
    public UserAutomaticChangeRequest() {
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

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public Double getMarkedPrice() {
        return markedPrice;
    }

    public void setMarkedPrice(Double markedPrice) {
        this.markedPrice = markedPrice;
    }

    public Double getAlertBand() {
        return alertBand;
    }

    public void setAlertBand(Double alertBand) {
        this.alertBand = alertBand;
    }
                
    public JsonNode toJsonNode(JsonNode jsonNode){
        ((ObjectNode) jsonNode).put("currency", currency);
        ((ObjectNode) jsonNode).put("markedPrice", markedPrice);
        ((ObjectNode) jsonNode).put("alertBand", alertBand);
        ((ObjectNode) jsonNode).put("active", activate);
        return jsonNode;
    }
    
}
