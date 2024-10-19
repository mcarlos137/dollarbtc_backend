/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class ChatPostMessageRequest implements Serializable, Cloneable {

    private String userName, name, message, subject, language, adminUserName;
    private boolean privateMessage;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }
    
    public boolean isPrivateMessage() {
        return privateMessage;
    }

    public void setPrivateMessage(boolean privateMessage) {
        this.privateMessage = privateMessage;
    }
    
    public JsonNode toJsonNode(JsonNode jsonNode){
        ((ObjectNode) jsonNode).put("userName", userName);
        if(name != null && !name.equals("")){
            ((ObjectNode) jsonNode).put("name", name);
        }
        ((ObjectNode) jsonNode).put("message", message);
        ((ObjectNode) jsonNode).put("subject", subject);
        ((ObjectNode) jsonNode).put("language", language);
        if(adminUserName != null && !adminUserName.equals("")){
            ((ObjectNode) jsonNode).put("adminUserName", adminUserName);
        }
        ((ObjectNode) jsonNode).put("privateMessage", privateMessage);
        return jsonNode;
    }
    
}
