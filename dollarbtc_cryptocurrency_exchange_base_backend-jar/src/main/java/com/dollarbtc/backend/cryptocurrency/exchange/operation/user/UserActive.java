/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserActive extends AbstractOperation<Boolean> {

    private final String userName;

    public UserActive(String userName) {
        super(Boolean.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        File userFile = UsersFolderLocator.getConfigFile(userName);
        if (!userFile.exists()) {
            super.response = false;
            return;
        }
        try {
            JsonNode user = mapper.readTree(userFile);
            super.response = user.get("active").booleanValue();
            return;
        } catch (IOException ex) {
            Logger.getLogger(UserActive.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = false;
    }

}
