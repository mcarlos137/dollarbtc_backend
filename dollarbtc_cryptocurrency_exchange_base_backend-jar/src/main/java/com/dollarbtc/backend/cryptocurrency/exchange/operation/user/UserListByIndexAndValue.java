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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserListByIndexAndValue extends AbstractOperation<ArrayNode> {

    private final String index, value, page;
    
    public UserListByIndexAndValue(String index, String value, String page) {
        super(ArrayNode.class);
        this.index = index;
        this.value = value;
        this.page = page;
    }

    @Override
    protected void execute() {
        ArrayNode userNames = mapper.createArrayNode();
        File usersIndexFolder = UsersIndexesFolderLocator.getFolder(index);
        if(usersIndexFolder.isDirectory()){
            File usersIndexValuePageFolder = new File(new File(usersIndexFolder, value.toUpperCase()), page);
            if(usersIndexValuePageFolder.isDirectory()){
                for(File usersIndexValuePageFile : usersIndexValuePageFolder.listFiles()){
                    if(!usersIndexValuePageFile.isFile()){
                        continue;
                    }
                    
                    try {
                        userNames.add(mapper.readTree(usersIndexValuePageFile));
                    } catch (IOException ex) {
                        Logger.getLogger(UserListByIndexAndValue.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        super.response = userNames;
    }

}
