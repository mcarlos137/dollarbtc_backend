/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm;

import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.inout.InOutAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.algorithm.trading.TradingAlgorithm;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition.EarningCondition;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition.InPriceBandCondition;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition.NotEnoughBalanceCondition;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition.OrderBlockingCondition;
import com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition.SwitchBalanceCondition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class IntervalAlgorithm {
    
    public final String name;
    public final TradingAlgorithm[] tradings;
    public final InOutAlgorithm[] ins, outs;
    //EARNING CONDITION
    public EarningCondition earningCondition = new EarningCondition();
    //NOT ENOUGH BALANCE CONDITION
    public NotEnoughBalanceCondition notEnoughBalanceCondition = new NotEnoughBalanceCondition();
    //ORDER BLOCKING CONDITION
    public OrderBlockingCondition orderBlockingCondition = new OrderBlockingCondition();
    //IN PRICE BAND CONDITION
    public InPriceBandCondition inPriceBandCondition = new InPriceBandCondition();
    //SWITCH BALANCE CONDITION
    public SwitchBalanceCondition switchBalanceCondition = new SwitchBalanceCondition();

    public IntervalAlgorithm(String name,
            TradingAlgorithm[] tradings, 
            InOutAlgorithm[] ins, 
            InOutAlgorithm[] outs) {
        this.name = name;
        this.tradings = tradings;
        this.ins = ins;
        this.outs = outs;
    }
    
    @Override
    public String toString() {
        return "ExchangeAccount ["
                + "earningCondition=" + earningCondition
                + ", notEnoughBalanceCondition=" + notEnoughBalanceCondition
                + ", orderBlockingCondition=" + orderBlockingCondition
                + ", inPriceBandCondition=" + inPriceBandCondition
                + "]";
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("earningCondition", this.earningCondition.toJsonNode());
        ((ObjectNode) jsonNode).put("notEnoughBalanceCondition", this.notEnoughBalanceCondition.toJsonNode());
        ((ObjectNode) jsonNode).put("orderBlockingCondition", this.orderBlockingCondition.toJsonNode());
        ((ObjectNode) jsonNode).put("inPriceBandCondition", this.inPriceBandCondition.toJsonNode());
        return jsonNode;
    }
    
}
