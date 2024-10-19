/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;

/**
 *
 * @author CarlosDaniel
 */
public class EarningCondition {

    private boolean used;
    private BigDecimal minTransactionFactor;

    public boolean isUsed() {
        return used;
    }

    public BigDecimal getMinTransactionFactor() {
        return minTransactionFactor;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setMinTransactionFactor(BigDecimal minTransactionFactor) {
        this.minTransactionFactor = minTransactionFactor;
    }

    @Override
    public String toString() {
        return "[used=" + used
                + ", minTransactionFactor=" + minTransactionFactor
                + "]";
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("used", used);
        ((ObjectNode) jsonNode).put("minPercent", minTransactionFactor);
        return jsonNode;
    }

}
