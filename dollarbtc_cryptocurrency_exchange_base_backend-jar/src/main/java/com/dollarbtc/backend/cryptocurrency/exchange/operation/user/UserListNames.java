/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersOperatorFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class UserListNames extends AbstractOperation<ArrayNode> {

    public UserListNames() {
        super(ArrayNode.class);
    }

    @Override
    protected void execute() {
        File usersFolder = UsersFolderLocator.getFolder();
        ArrayNode users = mapper.createArrayNode();
        if (OPERATOR_NAME.equals("MAIN")) {
            for (File userFolder : usersFolder.listFiles()) {
                if (!userFolder.isDirectory()) {
                    continue;
                }
                users.add(userFolder.getName());
            }
        } else {
            for (File userOperatorFile : UsersOperatorFolderLocator.getFolder().listFiles()) {
                if (!userOperatorFile.isFile()) {
                    continue;
                }
                File userFile = UsersFolderLocator.getConfigFile(userOperatorFile.getName().replace(".json", ""));
                if (!userFile.isFile()) {
                    continue;
                }
                users.add(new File(userFile.getParent()).getName());
            }
        }
        super.response = users;
    }

}
