/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class ModelActivateRequest implements Serializable, Cloneable {
    
    private String modelName;
    private Map<String, Double> initialAmounts = new HashMap<>();
    
    public ModelActivateRequest() {
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Map<String, Double> getInitialAmounts() {
        return initialAmounts;
    }

    public void setInitialAmounts(Map<String, Double> initialAmounts) {
        this.initialAmounts = initialAmounts;
    }
    
}
