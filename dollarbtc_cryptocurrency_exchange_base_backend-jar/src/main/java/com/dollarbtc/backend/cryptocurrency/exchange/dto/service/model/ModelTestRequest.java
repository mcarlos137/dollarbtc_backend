/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class ModelTestRequest implements Serializable, Cloneable {
    
    private Config config;

    public ModelTestRequest() {
    }
        
    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public static class Config {

        private String name;
        private boolean active;
        private SymbolRules[] symbolRules;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public SymbolRules[] getSymbolRules() {
            return symbolRules;
        }

        public void setSymbolRules(SymbolRules[] symbolRules) {
            this.symbolRules = symbolRules;
        }

        public JsonNode toJsonNode() {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.createObjectNode();
            ((ObjectNode) jsonNode).put("name", this.name);
            ((ObjectNode) jsonNode).put("active", this.active);
            ArrayNode arrayNode = mapper.createArrayNode();
            for(SymbolRules srs : this.symbolRules){
                arrayNode.add(srs.toJsonNode());
            }
            ((ObjectNode) jsonNode).putArray("symbolRules").addAll(arrayNode);
            return jsonNode;
        }

    }

    public static class SymbolRules {

        private String exchangeId, symbol, periodsDurationInSeconds;
        private boolean active;
        private double minimalAssetOrderAmount, startBasePercent;
        private AlgorithmRules[] algorithmRules;

        public String getExchangeId() {
            return exchangeId;
        }

        public void setExchangeId(String exchangeId) {
            this.exchangeId = exchangeId;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getPeriodsDurationInSeconds() {
            return periodsDurationInSeconds;
        }

        public void setPeriodsDurationInSeconds(String periodsDurationInSeconds) {
            this.periodsDurationInSeconds = periodsDurationInSeconds;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public double getMinimalAssetOrderAmount() {
            return minimalAssetOrderAmount;
        }

        public void setMinimalAssetOrderAmount(double minimalAssetOrderAmount) {
            this.minimalAssetOrderAmount = minimalAssetOrderAmount;
        }

        public double getStartBasePercent() {
            return startBasePercent;
        }

        public void setStartBasePercent(double startBasePercent) {
            this.startBasePercent = startBasePercent;
        }
                
        public AlgorithmRules[] getAlgorithmRules() {
            return algorithmRules;
        }

        public void setAlgorithmRules(AlgorithmRules[] algorithmRules) {
            this.algorithmRules = algorithmRules;
        }

        public JsonNode toJsonNode() {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.createObjectNode();
            ((ObjectNode) jsonNode).put("exchangeId", this.exchangeId);
            ((ObjectNode) jsonNode).put("symbol", this.symbol);
            ((ObjectNode) jsonNode).put("active", this.active);
            ((ObjectNode) jsonNode).put("minimalAssetOrderAmount", this.minimalAssetOrderAmount);
            ((ObjectNode) jsonNode).put("startBasePercent", this.startBasePercent);
            ((ObjectNode) jsonNode).put("periodsDurationInSeconds", this.periodsDurationInSeconds);
            ArrayNode arrayNode = mapper.createArrayNode();
            for(AlgorithmRules ars : this.algorithmRules){
                arrayNode.add(ars.toJsonNode());
            }
            ((ObjectNode) jsonNode).putArray("algorithmRules").addAll(arrayNode);
            return jsonNode;
        }

    }

    public static class AlgorithmRules {

        private String name, tradingParams, inParams, outParams, orderBlockingConditionAction;
        private boolean useEarningCondition, useNotEnoughBalanceCondition, useOrderBlockingCondition, useInPriceBandCondition;
        private double earningConditionMinTransactionFactor, inPriceBandConditionUpPercent, inPriceBandConditionDownPercent;
        private int notEnoughBalanceConditionInARowLimitToOrderQuantity, orderBlockingConditionMaxLossMaxQuantity, orderBlockingConditionMaxQuantity;
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTradingParams() {
            return tradingParams;
        }

        public void setTradingParams(String tradingParams) {
            this.tradingParams = tradingParams;
        }

        public String getInParams() {
            return inParams;
        }

        public void setInParams(String inParams) {
            this.inParams = inParams;
        }

        public String getOutParams() {
            return outParams;
        }

        public void setOutParams(String outParams) {
            this.outParams = outParams;
        }

        public String getOrderBlockingConditionAction() {
            return orderBlockingConditionAction;
        }

        public void setOrderBlockingConditionAction(String orderBlockingConditionAction) {
            this.orderBlockingConditionAction = orderBlockingConditionAction;
        }

        public boolean isUseEarningCondition() {
            return useEarningCondition;
        }

        public void setUseEarningCondition(boolean useEarningCondition) {
            this.useEarningCondition = useEarningCondition;
        }

        public boolean isUseNotEnoughBalanceCondition() {
            return useNotEnoughBalanceCondition;
        }

        public void setUseNotEnoughBalanceCondition(boolean useNotEnoughBalanceCondition) {
            this.useNotEnoughBalanceCondition = useNotEnoughBalanceCondition;
        }

        public boolean isUseOrderBlockingCondition() {
            return useOrderBlockingCondition;
        }

        public void setUseOrderBlockingCondition(boolean useOrderBlockingCondition) {
            this.useOrderBlockingCondition = useOrderBlockingCondition;
        }

        public boolean isUseInPriceBandCondition() {
            return useInPriceBandCondition;
        }

        public void setUseInPriceBandCondition(boolean useInPriceBandCondition) {
            this.useInPriceBandCondition = useInPriceBandCondition;
        }

        public double getEarningConditionMinTransactionFactor() {
            return earningConditionMinTransactionFactor;
        }

        public void setEarningConditionMinTransactionFactor(double earningConditionMinTransactionFactor) {
            this.earningConditionMinTransactionFactor = earningConditionMinTransactionFactor;
        }

        public double getInPriceBandConditionUpPercent() {
            return inPriceBandConditionUpPercent;
        }

        public void setInPriceBandConditionUpPercent(double inPriceBandConditionUpPercent) {
            this.inPriceBandConditionUpPercent = inPriceBandConditionUpPercent;
        }

        public double getInPriceBandConditionDownPercent() {
            return inPriceBandConditionDownPercent;
        }

        public void setInPriceBandConditionDownPercent(double inPriceBandConditionDownPercent) {
            this.inPriceBandConditionDownPercent = inPriceBandConditionDownPercent;
        }

        public int getNotEnoughBalanceConditionInARowLimitToOrderQuantity() {
            return notEnoughBalanceConditionInARowLimitToOrderQuantity;
        }

        public void setNotEnoughBalanceConditionInARowLimitToOrderQuantity(int notEnoughBalanceConditionInARowLimitToOrderQuantity) {
            this.notEnoughBalanceConditionInARowLimitToOrderQuantity = notEnoughBalanceConditionInARowLimitToOrderQuantity;
        }

        public int getOrderBlockingConditionMaxLossMaxQuantity() {
            return orderBlockingConditionMaxLossMaxQuantity;
        }

        public void setOrderBlockingConditionMaxLossMaxQuantity(int orderBlockingConditionMaxLossMaxQuantity) {
            this.orderBlockingConditionMaxLossMaxQuantity = orderBlockingConditionMaxLossMaxQuantity;
        }

        public int getOrderBlockingConditionMaxQuantity() {
            return orderBlockingConditionMaxQuantity;
        }

        public void setOrderBlockingConditionMaxQuantity(int orderBlockingConditionMaxQuantity) {
            this.orderBlockingConditionMaxQuantity = orderBlockingConditionMaxQuantity;
        }
                        
         public JsonNode toJsonNode() {
            JsonNode jsonNode = new ObjectMapper().createObjectNode();
            ((ObjectNode) jsonNode).put("name", this.name);
            ((ObjectNode) jsonNode).put("tradingParams", this.tradingParams);
            ((ObjectNode) jsonNode).put("inParams", this.inParams);
            ((ObjectNode) jsonNode).put("outParams", this.outParams);
            ((ObjectNode) jsonNode).put("useEarningCondition", this.useEarningCondition);
            ((ObjectNode) jsonNode).put("earningConditionMinTransactionFactor", this.earningConditionMinTransactionFactor);
            ((ObjectNode) jsonNode).put("useNotEnoughBalanceCondition", this.useNotEnoughBalanceCondition);
            ((ObjectNode) jsonNode).put("notEnoughBalanceConditionInARowLimitToOrderQuantity", this.notEnoughBalanceConditionInARowLimitToOrderQuantity);
            ((ObjectNode) jsonNode).put("useOrderBlockingCondition", this.useOrderBlockingCondition);
            ((ObjectNode) jsonNode).put("orderBlockingConditionMaxQuantity", this.orderBlockingConditionMaxQuantity);
            ((ObjectNode) jsonNode).put("useInPriceBandCondition", this.useInPriceBandCondition);
            ((ObjectNode) jsonNode).put("inPriceBandConditionUpPercent", this.inPriceBandConditionUpPercent);
            ((ObjectNode) jsonNode).put("inPriceBandConditionDownPercent", this.inPriceBandConditionDownPercent);
            ((ObjectNode) jsonNode).put("orderBlockingConditionAction", this.orderBlockingConditionAction);
            ((ObjectNode) jsonNode).put("orderBlockingConditionMaxLossMaxQuantity", this.orderBlockingConditionMaxLossMaxQuantity);
            return jsonNode;
        }

    }

}
