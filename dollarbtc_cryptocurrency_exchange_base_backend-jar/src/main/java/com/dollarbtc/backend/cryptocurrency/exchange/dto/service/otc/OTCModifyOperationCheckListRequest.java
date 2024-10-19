/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCModifyOperationCheckListRequest implements Serializable, Cloneable {

    private String id;
    private JsonNode checkList;

    public OTCModifyOperationCheckListRequest() {
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonNode getCheckList() {
        return checkList;
    }

    public void setCheckList(JsonNode checkList) {
        this.checkList = checkList;
    }

}
