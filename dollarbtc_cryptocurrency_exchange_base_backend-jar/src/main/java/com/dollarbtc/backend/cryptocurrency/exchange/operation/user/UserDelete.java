/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.user.UserDeleteRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DeletedUsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class UserDelete extends AbstractOperation<String> {

    private final UserDeleteRequest userDeleteRequest;

    public UserDelete(UserDeleteRequest userDeleteRequest) {
        super(String.class);
        this.userDeleteRequest = userDeleteRequest;
    }

    @Override
    protected void execute() {
        File userFolder = UsersFolderLocator.getFolder(userDeleteRequest.getUserName());
        if (!userFolder.isDirectory()) {
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File deletedUsersFolder = DeletedUsersFolderLocator.getFolder();
        File deletedUserFolder = DeletedUsersFolderLocator.getUserFolder(userDeleteRequest.getUserName());
        if (deletedUserFolder.isDirectory()) {
            deletedUserFolder.renameTo(new File(deletedUsersFolder, deletedUserFolder.getName() + "__" + DateUtil.getFileDate(null)));
        }
        FileUtil.moveFolderToFolder(userFolder, deletedUsersFolder);
        super.response = "OK";
    }

}
