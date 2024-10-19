/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class UserExist extends AbstractOperation<Boolean> {

    private final String userName;

    public UserExist(String userName) {
        super(Boolean.class);
        this.userName = userName;
    }

    @Override
    protected void execute() {
        File userBalanceFolder = UsersFolderLocator.getBalanceFolder(userName);
        super.response = userBalanceFolder.isDirectory();
    }

}
