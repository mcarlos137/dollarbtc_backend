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
public class ConsultaDocumentoOperation extends AbstractBancamigaRestClient<JsonNode> {

    private static final String ENDPOINT = "/api/v1/consulta/documento";
    
    public ConsultaDocumentoOperation(String documento) {
        super(JsonNode.class);
        super.formData.add("documento", documento);
    }
    
    public JsonNode getResponse() {
        return super.postJsonNode(super.formData, URL + ENDPOINT, RequestRestType.ASYNC, null, null, 30);
    }

}
