/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_NAME;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersOperatorFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class UserListSize extends AbstractOperation<Integer> {

    public UserListSize() {
        super(Integer.class);
    }

    @Override
    protected void execute() {
        File usersInfoFile = UsersFolderLocator.getInfoFile();
        super.response = 0;
        try {
            JsonNode usersInfo = mapper.readTree(usersInfoFile);
            if (usersInfo.has("listSize")) {
                super.response = usersInfo.get("listSize").intValue();
            }
        } catch (IOException ex) {
            Logger.getLogger(UserListSize.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread usersListSizeThread = new Thread(() -> {
            try {
                JsonNode usersInfo = mapper.readTree(usersInfoFile);
                if (usersInfo.has("lastListSizeRequestTimestamp")) {
                    String lastListSizeRequestTimestamp = usersInfo.get("lastListSizeRequestTimestamp").textValue();
                    if (lastListSizeRequestTimestamp.compareTo(DateUtil.getDateMinutesBefore(null, 10)) > 0) {
                        return;
                    }
                }
                Logger.getLogger(UserListSize.class.getName()).log(Level.INFO, "EXECUTE");
                File usersFolder = UsersFolderLocator.getFolder();
                Integer userListSize = 0;
                if (OPERATOR_NAME.equals("MAIN")) {
                    for (File userFolder : usersFolder.listFiles()) {
                        if (!userFolder.isDirectory()) {
                            continue;
                        }
                        userListSize++;
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
                        userListSize++;
                    }
                }
                ((ObjectNode) usersInfo).put("listSize", userListSize);
                ((ObjectNode) usersInfo).put("lastListSizeRequestTimestamp", DateUtil.getCurrentDate());
                FileUtil.editFile(usersInfo, usersInfoFile);
            } catch (IOException ex) {
                Logger.getLogger(UserListSize.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        usersListSizeThread.start();
    }

}
