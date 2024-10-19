/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc;

import java.io.Serializable;
import java.io.File;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class OTCPostContactMessage implements Serializable, Cloneable {

    private final String message;
    private final File attachment;

    public OTCPostContactMessage(String message, File attachment) {
        this.message = message;
        this.attachment = attachment;
    }

    public String getMessage() {
        return message;
    }

    public File getAttachment() {
        return attachment;
    }
    
}
