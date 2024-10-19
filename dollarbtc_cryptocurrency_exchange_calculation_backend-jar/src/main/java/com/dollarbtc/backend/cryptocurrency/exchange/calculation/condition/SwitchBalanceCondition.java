/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class SwitchBalanceCondition {

    private boolean used;

    public boolean pass() {
        if (!used) {
            return true;
        }
        boolean pass = false;
        return pass;
    }

    @Override
    public String toString() {
        return "[used=" + used
                + "]";
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("used", used);
        return jsonNode;
    }

    public enum Type {

        AUTO;

    }

}
