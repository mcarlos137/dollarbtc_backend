/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.marketmodulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MarketModulatorModifyManualRulesRequest implements Serializable, Cloneable {

    private Config config;

    public MarketModulatorModifyManualRulesRequest() {
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public JsonNode toJsonNode() {
        return this.config.toJsonNode();
    }

    public static class Config {
        
        private String shutdownMarketsBase, shutdownMarketsSpecific, blockMarketsBase, blockMarketsSpecific, protectionCurrency;
        private boolean shutdownMarketsAll, blockMarketsAll;

        public String getShutdownMarketsBase() {
            return shutdownMarketsBase;
        }

        public void setShutdownMarketsBase(String shutdownMarketsBase) {
            this.shutdownMarketsBase = shutdownMarketsBase;
        }

        public String getShutdownMarketsSpecific() {
            return shutdownMarketsSpecific;
        }

        public void setShutdownMarketsSpecific(String shutdownMarketsSpecific) {
            this.shutdownMarketsSpecific = shutdownMarketsSpecific;
        }

        public String getProtectionCurrency() {
            return protectionCurrency;
        }

        public void setProtectionCurrency(String protectionCurrency) {
            this.protectionCurrency = protectionCurrency;
        }

        public boolean isShutdownMarketsAll() {
            return shutdownMarketsAll;
        }

        public void setShutdownMarketsAll(boolean shutdownMarketsAll) {
            this.shutdownMarketsAll = shutdownMarketsAll;
        }

        public String getBlockMarketsBase() {
            return blockMarketsBase;
        }

        public void setBlockMarketsBase(String blockMarketsBase) {
            this.blockMarketsBase = blockMarketsBase;
        }

        public String getBlockMarketsSpecific() {
            return blockMarketsSpecific;
        }

        public void setBlockMarketsSpecific(String blockMarketsSpecific) {
            this.blockMarketsSpecific = blockMarketsSpecific;
        }

        public boolean isBlockMarketsAll() {
            return blockMarketsAll;
        }

        public void setBlockMarketsAll(boolean blockMarketsAll) {
            this.blockMarketsAll = blockMarketsAll;
        }
        
        public JsonNode toJsonNode() {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.createObjectNode();
            ((ObjectNode) jsonNode).put("shutdownMarketsAll", this.shutdownMarketsAll);
            ((ObjectNode) jsonNode).put("shutdownMarketsBase", this.shutdownMarketsBase);
            ((ObjectNode) jsonNode).put("shutdownMarketsSpecific", this.shutdownMarketsSpecific);
            ((ObjectNode) jsonNode).put("protectionCurrency", this.protectionCurrency);
            ((ObjectNode) jsonNode).put("blockMarketsAll", this.blockMarketsAll);
            ((ObjectNode) jsonNode).put("blockMarketsBase", this.blockMarketsBase);
            ((ObjectNode) jsonNode).put("blockMarketsSpecific", this.blockMarketsSpecific);
            return jsonNode;
        }

    }

}
