/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class USDAEditParamsRequest implements Serializable, Cloneable {

    private Double bottomBuyChangePercent,
            topBuyChangePercent,
            bottomSellChangePercent,
            topSellChangePercent;

    public Double getBottomBuyChangePercent() {
        return bottomBuyChangePercent;
    }

    public void setBottomBuyChangePercent(Double bottomBuyChangePercent) {
        this.bottomBuyChangePercent = bottomBuyChangePercent;
    }

    public Double getTopBuyChangePercent() {
        return topBuyChangePercent;
    }

    public void setTopBuyChangePercent(Double topBuyChangePercent) {
        this.topBuyChangePercent = topBuyChangePercent;
    }

    public Double getBottomSellChangePercent() {
        return bottomSellChangePercent;
    }

    public void setBottomSellChangePercent(Double bottomSellChangePercent) {
        this.bottomSellChangePercent = bottomSellChangePercent;
    }

    public Double getTopSellChangePercent() {
        return topSellChangePercent;
    }

    public void setTopSellChangePercent(Double topSellChangePercent) {
        this.topSellChangePercent = topSellChangePercent;
    }

    public JsonNode toJsonNode(JsonNode jsonNode) {
        if (bottomBuyChangePercent != null) {
            ((ObjectNode) jsonNode).put("bottomBuyChangePercent", this.bottomBuyChangePercent);
        }
        if (bottomSellChangePercent != null) {
            ((ObjectNode) jsonNode).put("bottomSellChangePercent", this.bottomSellChangePercent);
        }
        if (topBuyChangePercent != null && bottomBuyChangePercent < topBuyChangePercent) {
            ((ObjectNode) jsonNode).put("topBuyChangePercent", this.topBuyChangePercent);
        }
        if (topSellChangePercent != null && bottomSellChangePercent < topSellChangePercent) {
            ((ObjectNode) jsonNode).put("topSellChangePercent", this.topSellChangePercent);
        }
        return jsonNode;
    }

}
