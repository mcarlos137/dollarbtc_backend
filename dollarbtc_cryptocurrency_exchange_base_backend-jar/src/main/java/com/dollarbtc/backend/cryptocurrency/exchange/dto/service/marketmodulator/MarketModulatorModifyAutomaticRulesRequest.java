/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.marketmodulator;

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
public class MarketModulatorModifyAutomaticRulesRequest implements Serializable, Cloneable {

    private Config config;

    public MarketModulatorModifyAutomaticRulesRequest() {
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public JsonNode toJsonNode() {
        return getConfig().toJsonNode();
    }

    public static class Config {

        private AutomaticRule[] automaticRules;

        public AutomaticRule[] getAutomaticRules() {
            return automaticRules;
        }

        public void setAutomaticRules(AutomaticRule[] automaticRules) {
            this.automaticRules = automaticRules;
        }

        public JsonNode toJsonNode() {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.createObjectNode();
            ArrayNode arrayNode = mapper.createArrayNode();
            for (AutomaticRule automaticR : this.automaticRules) {
                arrayNode.add(automaticR.toJsonNode());
            }
            ((ObjectNode) jsonNode).putArray("automaticRules").addAll(arrayNode);
            return jsonNode;
        }

    }

    public static class AutomaticRule {

        private String exchangeId, symbol, period;
        private boolean active;
        private AlgorithmRule[] algorithmRules;

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

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public AlgorithmRule[] getAlgorithmRules() {
            return algorithmRules;
        }

        public void setAlgorithmRules(AlgorithmRule[] algorithmRules) {
            this.algorithmRules = algorithmRules;
        }

        public JsonNode toJsonNode() {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.createObjectNode();
            ((ObjectNode) jsonNode).put("exchangeId", this.exchangeId);
            ((ObjectNode) jsonNode).put("symbol", this.symbol);
            ((ObjectNode) jsonNode).put("period", this.period);
            ((ObjectNode) jsonNode).put("active", this.active);
            ArrayNode arrayNode = mapper.createArrayNode();
            for (AlgorithmRule algoR : this.algorithmRules) {
                arrayNode.add(algoR.toJsonNode());
            }
            ((ObjectNode) jsonNode).putArray("algorithmRules").addAll(arrayNode);
            return jsonNode;
        }

    }

    public static class AlgorithmRule {

        private String name, inactivateParams;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getInactivateParams() {
            return inactivateParams;
        }

        public void setInactivateParams(String inactivateParams) {
            this.inactivateParams = inactivateParams;
        }

        public JsonNode toJsonNode() {
            JsonNode jsonNode = new ObjectMapper().createObjectNode();
            ((ObjectNode) jsonNode).put("name", this.name);
            ((ObjectNode) jsonNode).put("inactivateParams", this.inactivateParams);
            return jsonNode;
        }

    }

}
