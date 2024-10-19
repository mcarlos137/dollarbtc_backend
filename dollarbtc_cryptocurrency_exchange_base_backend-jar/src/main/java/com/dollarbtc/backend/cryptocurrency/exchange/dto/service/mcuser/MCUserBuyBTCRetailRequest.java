/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcuser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCUserBuyBTCRetailRequest implements Serializable, Cloneable {

    private String userName, currency, retailId, description;
    private Double amount, price;

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

    public String getRetailId() {
        return retailId;
    }

    public void setRetailId(String retailId) {
        this.retailId = retailId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public JsonNode toJsonNode(JsonNode jsonNode) {
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("currency", this.currency);
        ((ObjectNode) jsonNode).put("retailId", this.retailId);
        if (this.description != null && !this.description.equals("")) {
            ((ObjectNode) jsonNode).put("description", this.description);
        }
        ((ObjectNode) jsonNode).put("amount", this.amount);
        ((ObjectNode) jsonNode).put("price", this.price);
        return jsonNode;
    }

}
