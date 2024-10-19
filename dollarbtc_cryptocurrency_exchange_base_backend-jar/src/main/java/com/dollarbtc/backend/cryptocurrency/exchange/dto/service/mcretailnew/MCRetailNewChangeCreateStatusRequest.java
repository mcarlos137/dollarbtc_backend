/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailCreateStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class MCRetailNewChangeCreateStatusRequest implements Serializable, Cloneable {

    private String id;
    private MCRetailCreateStatus mcRetailCreateStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MCRetailCreateStatus getMcRetailCreateStatus() {
        return mcRetailCreateStatus;
    }

    public void setMcRetailCreateStatus(MCRetailCreateStatus mcRetailCreateStatus) {
        this.mcRetailCreateStatus = mcRetailCreateStatus;
    }
    
}
