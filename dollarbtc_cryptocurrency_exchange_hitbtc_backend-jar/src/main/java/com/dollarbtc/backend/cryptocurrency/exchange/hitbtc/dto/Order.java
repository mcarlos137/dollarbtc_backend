package com.dollarbtc.backend.cryptocurrency.exchange.hitbtc.dto;

import java.math.BigDecimal;

/**
 *
 * @author CarlosDaniel
 */
public class Order {
    
    private String clientOrderId, symbol, side, status, type, timeInForce, createdAt, updatedAt;
    private BigDecimal quantity, price, cumQuantity;
    private String id;
    private Error error; 

    public Order(String clientOrderId, String symbol, String side, String type, BigDecimal quantity, BigDecimal price, String id) {
        this.clientOrderId = clientOrderId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCumQuantity() {
        return cumQuantity;
    }

    public void setCumQuantity(BigDecimal cumQuantity) {
        this.cumQuantity = cumQuantity;
    }    

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Order{" 
                + "id=" + id 
                + ", clientOrderId=" + clientOrderId 
                + ", symbol=" + symbol 
                + ", side=" + side 
                + ", status=" + status 
                + ", type=" + type 
                + ", timeInForce=" + timeInForce 
                + ", createdAt=" + createdAt 
                + ", updatedAt=" + updatedAt 
                + ", quantity=" + quantity 
                + ", price=" + price 
                + ", cumQuantity=" + cumQuantity 
                + ", error=" + error 
                + '}';
    }
            
    public static class Error {
        
        private int code;
        private String message, description;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "Error{" 
                    + "code=" + code 
                    + ", message=" + message 
                    + ", description=" + description 
                    + '}';
        }
                        
    }

}
