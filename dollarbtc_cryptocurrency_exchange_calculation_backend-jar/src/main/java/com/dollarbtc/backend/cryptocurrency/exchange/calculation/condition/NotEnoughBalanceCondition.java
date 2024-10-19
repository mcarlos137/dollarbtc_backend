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
public class NotEnoughBalanceCondition {

    private boolean used;
    private int inARowLimitToOrderQuantity;
    private int toBuyInARowCounter = 0;
    private BigDecimal toBuyInARowHighestSellPrice;
    private Boolean toBuyInARowConsistentDownTrend;
    private int toSellInARowCounter = 0;
    private BigDecimal toSellInARowLowestBuyPrice;
    private Boolean toSellInARowConsistentUpTrend;

    public BigDecimal getToBuyInARowHighestSellPrice() {
        return toBuyInARowHighestSellPrice;
    }

    public void setToBuyInARowHighestSellPrice(BigDecimal toBuyInARowHighestSellPrice) {
        this.toBuyInARowHighestSellPrice = toBuyInARowHighestSellPrice;
    }
    
    public Boolean getToBuyInARowConsistentDownTrend() {
        return toBuyInARowConsistentDownTrend;
    }

    public void setToBuyInARowConsistentDownTrend(Boolean toBuyInARowConsistentDownTrend) {
        this.toBuyInARowConsistentDownTrend = toBuyInARowConsistentDownTrend;
    }

    public BigDecimal getToSellInARowLowestBuyPrice() {
        return toSellInARowLowestBuyPrice;
    }

    public void setToSellInARowLowestBuyPrice(BigDecimal toSellInARowLowestBuyPrice) {
        this.toSellInARowLowestBuyPrice = toSellInARowLowestBuyPrice;
    }

    public Boolean getToSellInARowConsistentUpTrend() {
        return toSellInARowConsistentUpTrend;
    }

    public void setToSellInARowConsistentUpTrend(Boolean toSellInARowConsistentUpTrend) {
        this.toSellInARowConsistentUpTrend = toSellInARowConsistentUpTrend;
    }
               
    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setInARowLimitToOrderQuantity(int inARowLimitToOrderQuantity) {
        this.inARowLimitToOrderQuantity = inARowLimitToOrderQuantity;
    }

    public void addToBuyInARowCounter() {
        this.toBuyInARowCounter++;
    }

    public void substractToBuyInARowCounter() {
        this.toBuyInARowCounter--;
    }

    public void restartToBuyInARowCounter() {
        this.toBuyInARowHighestSellPrice = null;
        this.toBuyInARowConsistentDownTrend = null;
        this.toBuyInARowCounter = 0;
    }

    public void addToSellInARowCounter() {
        this.toSellInARowCounter++;
    }

    public void substractToSellInARowCounter() {
        this.toSellInARowCounter--;
    }

    public void restartToSellInARowCounter() {
        this.toSellInARowLowestBuyPrice = null;
        this.toSellInARowConsistentUpTrend = null;
        this.toSellInARowCounter = 0;
    }

    public boolean sellToBuy(BigDecimal price) {
        if (!used) {
            return used;
        }
        if (toBuyInARowCounter >= inARowLimitToOrderQuantity && (price.compareTo(toBuyInARowHighestSellPrice) >= 0 || toBuyInARowConsistentDownTrend)) {
            restartToBuyInARowCounter();
            return true;
        }
        return false;
    }

    public boolean buyToSell(BigDecimal price) {
        if (!used) {
            return false;
        }
        if (toSellInARowCounter >= inARowLimitToOrderQuantity && (price.compareTo(toSellInARowLowestBuyPrice) <= 0 || toSellInARowConsistentUpTrend)) {
            restartToSellInARowCounter();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[used=" + used
                + ", inARowLimitToOrderQuantity=" + inARowLimitToOrderQuantity
                + ", toBuyInARowCounter=" + toBuyInARowCounter
                + ", toBuyInARowHighestSellPrice=" + toBuyInARowHighestSellPrice
                + ", toBuyInARowConsistentDownTrend=" + toBuyInARowConsistentDownTrend
                + ", toSellInARowCounter=" + toSellInARowCounter
                + ", toSellInARowLowestBuyPrice=" + toSellInARowLowestBuyPrice
                + ", toSellInARowConsistentUpTrend=" + toSellInARowConsistentUpTrend
                + "]";
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("used", used);
        ((ObjectNode) jsonNode).put("inARowLimitToOrderQuantity", inARowLimitToOrderQuantity);
        ((ObjectNode) jsonNode).put("toBuyInARowCounter", toBuyInARowCounter);
        ((ObjectNode) jsonNode).put("toBuyInARowHighestSellPrice", toBuyInARowHighestSellPrice);
        ((ObjectNode) jsonNode).put("toBuyInARowConsistentDownTrend", toBuyInARowConsistentDownTrend);
        ((ObjectNode) jsonNode).put("toSellInARowCounter", toSellInARowCounter);
        ((ObjectNode) jsonNode).put("toSellInARowLowestBuyPrice", toSellInARowLowestBuyPrice);
        ((ObjectNode) jsonNode).put("toSellInARowConsistentUpTrend", toSellInARowConsistentUpTrend);
        return jsonNode;
    }

}
