/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserEnvironment;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserOperationAccount;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserProfile;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserType;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetNames extends AbstractOperation<Set> {

    private final UserProfile userProfile;

    public UserGetNames(UserProfile userProfile) {
        super(Set.class);
        this.userProfile = userProfile;
    }

    @Override
    protected void execute() {
        Set<String> userNames = new TreeSet<>();
        File usersFolder = UsersFolderLocator.getFolder();
        for (File userFolder : usersFolder.listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            try {
                JsonNode user = mapper.readTree(new File(userFolder, "config.json"));
                if (!userProfile.equals(UserType.valueOf(user.get("type").textValue()),
                        UserEnvironment.valueOf(user.get("environment").textValue()),
                        UserOperationAccount.valueOf(user.get("operationAccount").textValue()))) {
                    continue;
                }
                userNames.add(userFolder.getName());
            } catch (IOException ex) {
                Logger.getLogger(UserGetNames.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = userNames;
    }

}
