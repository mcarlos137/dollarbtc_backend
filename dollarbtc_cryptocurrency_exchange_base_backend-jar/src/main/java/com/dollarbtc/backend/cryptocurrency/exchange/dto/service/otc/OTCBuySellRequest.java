/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc;

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
public class OTCBuySellRequest implements Serializable, Cloneable {

    private final String offerId, message;
    private final Double amount, price;
    private final JsonNode payment;
    private String timestamp, operationId;

    public OTCBuySellRequest(String offerId, String message, Double amount, Double price, JsonNode payment) {
        this.offerId = offerId;
        this.message = message;
        this.amount = amount;
        this.price = price;
        this.payment = payment;
    }

    public String getOfferId() {
        return offerId;
    }

    public String getMessage() {
        return message;
    }
    
    public Double getAmount() {
        return amount;
    }

    public Double getPrice() {
        return price;
    }

    public JsonNode getPayment() {
        return payment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Override
    public String toString() {
        return "OTCBuySellRequest{" + "offerId=" + offerId + ", message=" + message + ", amount=" + amount + ", price=" + price + ", payment=" + payment + ", timestamp=" + timestamp + ", operationId=" + operationId + '}';
    }
    
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("offerId", this.offerId);
        ((ObjectNode) jsonNode).put("message", this.message);
        ((ObjectNode) jsonNode).put("amount", this.amount);
        ((ObjectNode) jsonNode).put("price", this.price);
        ((ObjectNode) jsonNode).put("payment", this.payment);
        ((ObjectNode) jsonNode).put("timestamp", timestamp);
        ((ObjectNode) jsonNode).put("operationId", operationId);
        return jsonNode;
    }

}
