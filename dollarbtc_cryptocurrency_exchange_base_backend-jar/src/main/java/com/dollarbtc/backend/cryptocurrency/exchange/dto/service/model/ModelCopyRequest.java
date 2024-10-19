/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class ModelCopyRequest implements Serializable, Cloneable {
    
    private String userName, modelName;
    private Map<String, Double> amounts = new HashMap<>();
    private boolean isMoneyClick;
    
    public ModelCopyRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Map<String, Double> getAmounts() {
        return amounts;
    }

    public void setAmounts(Map<String, Double> amounts) {
        this.amounts = amounts;
    }

    public boolean isIsMoneyClick() {
        return isMoneyClick;
    }

    public void setIsMoneyClick(boolean isMoneyClick) {
        this.isMoneyClick = isMoneyClick;
    }

}
