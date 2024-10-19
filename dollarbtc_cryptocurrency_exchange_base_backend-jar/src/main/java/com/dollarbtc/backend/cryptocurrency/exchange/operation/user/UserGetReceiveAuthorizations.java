/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersReceiveAuthorizationsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetReceiveAuthorizations extends AbstractOperation<JsonNode> {

    private final String userName;

    public UserGetReceiveAuthorizations(String userName) {
        super(JsonNode.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        File usersReceiveAuthorizationsUserNameFile = UsersReceiveAuthorizationsFolderLocator.getUserNameFile(userName);
        if (usersReceiveAuthorizationsUserNameFile.isFile()) {
            try {
                super.response = mapper.readTree(usersReceiveAuthorizationsUserNameFile);
                return;
            } catch (IOException ex) {
                Logger.getLogger(UserGetReceiveAuthorizations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = mapper.createObjectNode();
    }

}
