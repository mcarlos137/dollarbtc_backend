/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersIndexesFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class UserListNamesByIndexAndValue extends AbstractOperation<ArrayNode> {

    private final String index, value;
    
    public UserListNamesByIndexAndValue(String index, String value) {
        super(ArrayNode.class);
        this.index = index;
        this.value = value;
    }

    @Override
    protected void execute() {
        ArrayNode userNames = mapper.createArrayNode();
        File usersIndexFolder = UsersIndexesFolderLocator.getFolder(index);
        if(usersIndexFolder.isDirectory()){
            File usersIndexValueFolder = new File(usersIndexFolder, value.toUpperCase());
            if(usersIndexValueFolder.isDirectory()){
                for(File usersIndexValueFile : usersIndexValueFolder.listFiles()){
                    if(!usersIndexValueFile.isFile()){
                        continue;
                    }
                    userNames.add(usersIndexValueFile.getName().replace(".json", ""));
                }
            }
        }
        super.response = userNames;
    }

}
