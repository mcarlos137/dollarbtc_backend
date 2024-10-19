/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.bancamiga;

import com.dollarbtc.backend.cryptocurrency.exchange.util.RequestRestType;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author CarlosDaniel
 */
public class TransferirOperation extends AbstractBancamigaRestClient<JsonNode> {

    private static final String ENDPOINT = "/api/v1/transferir";
    
    public TransferirOperation(
            String documento_origen, 
            String cuenta_origen, 
            String documento_destino,
            String cuenta_destino,
            String concepto,
            String monto
    ) {
        super(JsonNode.class);
        super.formData.add("documento_origen", documento_origen);
        super.formData.add("cuenta_origen", cuenta_origen);
        super.formData.add("documento_destino", documento_destino);
        super.formData.add("cuenta_destino", cuenta_destino);
        super.formData.add("concepto", concepto);
        super.formData.add("monto", monto);
        
    }
    
    public JsonNode getResponse() {
        return super.postJsonNode(super.formData, URL + ENDPOINT, RequestRestType.ASYNC, null, null, 30);
    }

}
