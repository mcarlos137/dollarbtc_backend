/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersReceiveAuthorizationsFolderLocator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetReceiveAuthorizationMessage extends AbstractOperation<String> {

    private final String receiveAuthorizationId, language;

    public UserGetReceiveAuthorizationMessage(String receiveAuthorizationId, String language) {
        super(String.class);
        this.receiveAuthorizationId = receiveAuthorizationId;
        this.language = language;
    }

    @Override
    protected void execute() {
        File usersReceiveAuthorizationsMessageFile = UsersReceiveAuthorizationsFolderLocator.getMessageFile(receiveAuthorizationId);
        if (usersReceiveAuthorizationsMessageFile.isFile()) {
            try {
                super.response = mapper.readTree(usersReceiveAuthorizationsMessageFile).get(language).textValue();
                return;
            } catch (IOException ex) {
                Logger.getLogger(UserGetReceiveAuthorizationMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = "";
    }

}
