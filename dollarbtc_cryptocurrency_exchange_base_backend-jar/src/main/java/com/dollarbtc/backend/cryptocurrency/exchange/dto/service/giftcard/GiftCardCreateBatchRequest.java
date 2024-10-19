/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.giftcard;

import java.util.Map;

/**
 *
 * @author carlosmolina
 */
public class GiftCardCreateBatchRequest {
    
    private String currency, batchName, source, userNameToActivate;
    private Double maxAmount;
    private Map<Double, Integer> valuesAndQuantities;
    private boolean upfrontCommission;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Map<Double, Integer> getValuesAndQuantities() {
        return valuesAndQuantities;
    }

    public void setValuesAndQuantities(Map<Double, Integer> valuesAndQuantities) {
        this.valuesAndQuantities = valuesAndQuantities;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserNameToActivate() {
        return userNameToActivate;
    }

    public void setUserNameToActivate(String userNameToActivate) {
        this.userNameToActivate = userNameToActivate;
    }    

    public boolean isUpfrontCommission() {
        return upfrontCommission;
    }

    public void setUpfrontCommission(boolean upfrontCommission) {
        this.upfrontCommission = upfrontCommission;
    }
                
}
