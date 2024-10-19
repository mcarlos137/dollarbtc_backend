/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserRecoverDeletedRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DeletedUsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class UserRecoverDeleted extends AbstractOperation<String> {

    private final UserRecoverDeletedRequest userRecoverDeletedRequest;

    public UserRecoverDeleted(UserRecoverDeletedRequest userRecoverDeletedRequest) {
        super(String.class);
        this.userRecoverDeletedRequest = userRecoverDeletedRequest;
    }

    @Override
    protected void execute() {
        File deletedUserFolder = DeletedUsersFolderLocator.getUserFolder(userRecoverDeletedRequest.getUserName());
        if (!deletedUserFolder.isDirectory()) {
            super.response = "DELETED USER DOES NOT EXIST";
            return;
        }
        FileUtil.moveFolderToFolder(deletedUserFolder, UsersFolderLocator.getFolder());
        super.response = "OK";
    }

}
