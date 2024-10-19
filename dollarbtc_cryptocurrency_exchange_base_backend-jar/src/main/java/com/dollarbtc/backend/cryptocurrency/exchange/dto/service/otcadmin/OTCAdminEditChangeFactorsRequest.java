/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otcadmin;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCAdminEditChangeFactorsRequest implements Serializable, Cloneable {

    private String userName;
    private JsonNode changeFactors;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public JsonNode getChangeFactors() {
        return changeFactors;
    }

    public void setChangeFactors(JsonNode changeFactors) {
        this.changeFactors = changeFactors;
    }
            
}
