/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.condition;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 *
 * @author CarlosDaniel
 */
public class InPriceBandCondition {

    private boolean used;
    private double downPercent, upPercent;

    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setDownPercent(double downPercent) {
        this.downPercent = downPercent;
    }

    public void setUpPercent(double upPercent) {
        this.upPercent = upPercent;
    }

    private Double getPrice(String intervalAlgorithmName, File algorithmBotNameFolder) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(new File(algorithmBotNameFolder, "inPriceBandCondition.json"));
            if (jsonNode == null || jsonNode instanceof NullNode || jsonNode.get("price") instanceof NullNode || jsonNode.get("intervalAlgorithmName") instanceof NullNode) {
                return null;
            }
            if (!jsonNode.get("intervalAlgorithmName").textValue().equals(intervalAlgorithmName)) {
                return null;
            }
            return jsonNode.get("price").doubleValue();
        } catch (IOException ex) {
        }
        return null;
    }

    private void setIntervalAlgorithmNameAndPrice(String intervalAlgorithmName, Double price, File algorithmBotNameFolder) {
        File file = new File(algorithmBotNameFolder, "inPriceBandCondition.json");
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("intervalAlgorithmName", intervalAlgorithmName);
        ((ObjectNode) jsonNode).put("price", price);
        FileUtil.editFile(jsonNode, file);
    }

    public boolean pass(String intervalAlgorithmName, BigDecimal lastPrice, File algorithmBotNameFolder) {
        if (!used) {
            return true;
        }
        boolean pass = false;
        Double price = getPrice(intervalAlgorithmName, algorithmBotNameFolder);
        if (price != null) {
            if (lastPrice.compareTo(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(downPercent))).divide(BigDecimal.valueOf(100))) <= 0
                    || lastPrice.compareTo(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(100).add(BigDecimal.valueOf(upPercent))).divide(BigDecimal.valueOf(100))) >= 0) {
                pass = true;
            }
        } else {
            setIntervalAlgorithmNameAndPrice(intervalAlgorithmName, lastPrice.doubleValue(), algorithmBotNameFolder);
        }
        return pass;
    }

    public void reset(File algorithmBotNameFolder) {
        setIntervalAlgorithmNameAndPrice(null, null, algorithmBotNameFolder);
    }

    @Override
    public String toString() {
        return "[used=" + used
                + ", downPercent=" + downPercent
                + ", upPercent=" + upPercent
                + "]";
    }

    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("used", used);
        ((ObjectNode) jsonNode).put("downPercent", downPercent);
        ((ObjectNode) jsonNode).put("upPercent", upPercent);
        return jsonNode;
    }

}
