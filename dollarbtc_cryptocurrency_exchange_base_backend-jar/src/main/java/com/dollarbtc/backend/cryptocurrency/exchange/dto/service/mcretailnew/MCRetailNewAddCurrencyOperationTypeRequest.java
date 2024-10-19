/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailOperationType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCRetailNewAddCurrencyOperationTypeRequest implements Serializable, Cloneable {

    private String id, currency;
    private MCRetailOperationType mcRetailOperationType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public MCRetailOperationType getMcRetailOperationType() {
        return mcRetailOperationType;
    }

    public void setMcRetailOperationType(MCRetailOperationType mcRetailOperationType) {
        this.mcRetailOperationType = mcRetailOperationType;
    }
    
}
