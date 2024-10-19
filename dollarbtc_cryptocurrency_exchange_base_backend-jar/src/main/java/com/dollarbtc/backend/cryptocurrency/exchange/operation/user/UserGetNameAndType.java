/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersAddressesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserGetNameAndType extends AbstractOperation<String> {

    private final String address;

    public UserGetNameAndType(String address) {
        super(String.class);
        this.address = address;
    }

    @Override
    protected void execute() {
        File userAddressFile = UsersAddressesFolderLocator.getAddressFile(address);
        if (userAddressFile.isFile()) {
            JsonNode userAddress;
            try {
                userAddress = mapper.readTree(userAddressFile);
                if (userAddress.has("type")) {
                    super.response = userAddress.get("userName").textValue() + "____" + userAddress.get("type").textValue();
                    return;
                }
                super.response = userAddress.get("userName").textValue();
                return;
            } catch (IOException ex) {
                Logger.getLogger(UserGetNameAndType.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = null;
    }

}
