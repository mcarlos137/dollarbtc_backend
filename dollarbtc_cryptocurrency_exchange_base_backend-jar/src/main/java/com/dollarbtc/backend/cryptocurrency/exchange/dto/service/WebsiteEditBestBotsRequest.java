/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class WebsiteEditBestBotsRequest implements Serializable, Cloneable {

    private List<Bot> bots;

    public WebsiteEditBestBotsRequest() {
    }

    public List<Bot> getBots() {
        if (bots == null) {
            bots = new ArrayList<>();
        }
        return bots;
    }

    public void setBots(List<Bot> bots) {
        this.bots = bots;
    }

    public JsonNode toJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.createObjectNode();
        ArrayNode arrayNode = mapper.createArrayNode();
        for(Bot bot : bots){
            arrayNode.add(bot.toJsonNode(mapper));
        }
        ((ObjectNode) jsonNode).putArray("bots").addAll(arrayNode);
        return jsonNode;
    }

    public static class Bot {

        private String title, userModelName, exchangeId, symbol, userModelTestName, startTestTimestamp, endTestTimestamp;

        public Bot() {
        }

        public String getTitle() {
            return title;
        }

        public String getUserModelName() {
            return userModelName;
        }

        public String getExchangeId() {
            return exchangeId;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getUserModelTestName() {
            return userModelTestName;
        }

        public String getStartTestTimestamp() {
            return startTestTimestamp;
        }

        public String getEndTestTimestamp() {
            return endTestTimestamp;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUserModelName(String userModelName) {
            this.userModelName = userModelName;
        }

        public void setExchangeId(String exchangeId) {
            this.exchangeId = exchangeId;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public void setUserModelTestName(String userModelTestName) {
            this.userModelTestName = userModelTestName;
        }

        public void setStartTestTimestamp(String startTestTimestamp) {
            this.startTestTimestamp = startTestTimestamp;
        }

        public void setEndTestTimestamp(String endTestTimestamp) {
            this.endTestTimestamp = endTestTimestamp;
        }

        public JsonNode toJsonNode(ObjectMapper mapper) {
            JsonNode jsonNode = mapper.createObjectNode();
            ((ObjectNode) jsonNode).put("title", this.title);
            ((ObjectNode) jsonNode).put("userModelName", this.userModelName);
            ((ObjectNode) jsonNode).put("exchangeId", this.exchangeId);
            ((ObjectNode) jsonNode).put("symbol", this.symbol);
            ((ObjectNode) jsonNode).put("userModelTestName", this.userModelTestName);
            ((ObjectNode) jsonNode).put("startTestTimestamp", this.startTestTimestamp);
            ((ObjectNode) jsonNode).put("endTestTimestamp", this.endTestTimestamp);
            return jsonNode;
        }

    }

}
