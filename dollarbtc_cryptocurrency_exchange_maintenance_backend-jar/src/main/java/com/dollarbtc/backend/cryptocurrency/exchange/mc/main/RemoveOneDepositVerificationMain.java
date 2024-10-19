/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCGetOperationsRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OTCOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.UserVerificationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperations;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersVerificationFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author CarlosDaniel
 */
public class RemoveOneDepositVerificationMain {

    public static void main(String[] args) {
        System.out.println("Starting RemoveOneDepositVerificationMain");
        String baseTimestamp = DateUtil.getDateHoursBefore(DateUtil.getCurrentDate(), 6);
        File usersVerificationOneDepositFolder = UsersVerificationFolderLocator.getStatusFolder(UserVerificationStatus.ONE_DEPOSIT);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(usersVerificationOneDepositFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        String id1 = o1.toFile().getName().replace(".json", "");
                        String id2 = o2.toFile().getName().replace(".json", "");
                        return id2.compareTo(id1);
                    })
                    .iterator();
            ObjectMapper mapper = new ObjectMapper();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                File verificationFile = it.toFile();
                JsonNode verification = mapper.readTree(verificationFile);
                String timestamp = DateUtil.getDate(verificationFile.getName().replace(".json", ""));
                String userName = verification.get("userName").textValue();
                if (timestamp.compareTo(baseTimestamp) > 0) {
                    ArrayNode operations = new OTCGetOperations(new OTCGetOperationsRequest(
                            userName,
                            null,
                            null,
                            OTCOperationType.MC_BUY_BALANCE,
                            OTCOperationStatus.SUCCESS,
                            null
                    )).getResponse();
                    if (operations.size() > 0) {
                        removeFVerification(userName, verificationFile, mapper);
                    }
                } else {
                    removeFVerification(userName, verificationFile, mapper);
                }
            }
        } catch (IOException ex) {
        }
        System.out.println("Finishing RemoveOneDepositVerificationMain");
    }

    private static void removeFVerification(String userName, File verificationFile, ObjectMapper mapper) {
        System.out.println("Removing verification F for: " + userName);
        try {
            File userConfigFile = UsersFolderLocator.getConfigFile(userName);
            JsonNode userConfig = mapper.readTree(userConfigFile);
            if (userConfig.has("verification") && userConfig.get("verification").has("F")) {
                ((ObjectNode) userConfig.get("verification")).remove("F");
                FileUtil.editFile(userConfig, userConfigFile);
            }
            File usersVerificationDeletedFolder = UsersVerificationFolderLocator.getStatusFolder(UserVerificationStatus.DELETED);
            FileUtil.moveFileToFolder(verificationFile, usersVerificationDeletedFolder);
        } catch (IOException ex) {
            Logger.getLogger(RemoveOneDepositVerificationMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
