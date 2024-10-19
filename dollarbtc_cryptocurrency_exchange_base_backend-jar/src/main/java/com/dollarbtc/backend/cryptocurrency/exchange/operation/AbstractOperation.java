/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author CarlosDaniel
 * @param <T>
 */
public abstract class AbstractOperation<T> {

    protected final Class<T> responseClass;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected T response;
    
    public AbstractOperation(Class<T> responseClass) {
        this.responseClass = responseClass;
    }

    protected abstract void execute();
    
    public T getResponse() {
        this.execute();
        return response;
    }
    
}
