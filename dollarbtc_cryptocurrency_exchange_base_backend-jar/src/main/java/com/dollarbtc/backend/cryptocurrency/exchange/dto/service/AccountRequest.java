/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import java.io.Serializable;

/**
 *
 * @author CarlosDaniel
 */
public class AccountRequest implements Serializable, Cloneable {
    
    private final String exchangeId, symbol, modelName, initDate, endDate;
    private final CollectionOrderByDate collectionOrderByDate;

    public AccountRequest(String exchangeId, String symbol, String modelName, String initDate, String endDate, CollectionOrderByDate collectionOrderByDate) {
        this.exchangeId = exchangeId;
        this.symbol = symbol;
        this.modelName = modelName;
        this.initDate = initDate;
        this.endDate = endDate;
        this.collectionOrderByDate = collectionOrderByDate;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getModelName() {
        return modelName;
    }

    public String getInitDate() {
        return initDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public CollectionOrderByDate getCollectionOrderByDate() {
        return collectionOrderByDate;
    }
        
}
