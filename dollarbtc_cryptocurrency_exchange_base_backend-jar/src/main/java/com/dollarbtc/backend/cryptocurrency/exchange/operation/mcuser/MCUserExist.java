/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;

/**
 *
 * @author carlosmolina
 */
public class MCUserExist extends AbstractOperation<Boolean> {

    private final String userName;

    public MCUserExist(String userName) {
        super(Boolean.class);
        this.userName = userName;
    }

    @Override
    public void execute() {
        super.response = UsersFolderLocator.getFolder(userName).isDirectory();
    }

}
