/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class BankerGetReferredUsers extends AbstractOperation<ArrayNode> {

    private final String userName;

    public BankerGetReferredUsers(String userName) {
        super(ArrayNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        File referredUsersFile = BankersFolderLocator.getReferredUsersFile(userName);
        ArrayNode referredUsers = mapper.createArrayNode();
        try {
            Iterator<JsonNode> referredUsersIterator = mapper.readTree(referredUsersFile).iterator();
            while (referredUsersIterator.hasNext()) {
                JsonNode referredUsersIt = referredUsersIterator.next();
                String referredUserName = referredUsersIt.textValue();
                try {
                    JsonNode user = mapper.readTree(UsersFolderLocator.getConfigFile(referredUserName));
                    ObjectNode referredUser = mapper.createObjectNode();
                    referredUser.put("name", user.get("name").textValue());
                    if(user.has("nickname")){
                        referredUser.put("nickname", user.get("nickname").textValue());
                    }
                    if(user.has("firstName")){
                        referredUser.put("firstName", user.get("firstName").textValue());
                    }
                    if(user.has("lastName")){
                        referredUser.put("lastName", user.get("lastName").textValue());
                    }
                    if(user.has("email")){
                        referredUser.put("email", user.get("email").textValue());
                    }
                    if(user.has("email")){
                        referredUser.put("email", user.get("email").textValue());
                    }
                    referredUsers.add(referredUser);
                } catch (IOException ex) {
                    Logger.getLogger(BankerGetReferredUsers.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BankerGetReferredUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = referredUsers;
    }

}
