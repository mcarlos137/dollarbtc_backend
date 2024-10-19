/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class UserModifyInfoRequest implements Serializable, Cloneable {
    
    private String userName, fieldName, type;
    private Object fieldValue;
    private ArrayNode fieldValueArray;
    
    public UserModifyInfoRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }
    
    public ArrayNode getFieldValueArray() {
        return fieldValueArray;
    }

    public void setFieldValueArray(ArrayNode fieldValueArray) {
        this.fieldValueArray = fieldValueArray;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
                           
}
