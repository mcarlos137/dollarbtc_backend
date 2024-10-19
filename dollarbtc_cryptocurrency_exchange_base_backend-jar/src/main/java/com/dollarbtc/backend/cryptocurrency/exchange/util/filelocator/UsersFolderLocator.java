/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo torres
 */
public class UsersFolderLocator {

    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Users"));
    }

    public static File getFolder(String userName) {
        File userFolder = new File(getFolder(), userName);
        if (userFolder.isDirectory()) {
            return userFolder;
        }
        Logger.getLogger(UsersFolderLocator.class.getName()).log(Level.INFO, "USER {0} WAS DELETED", userName);
        return userFolder;
    }

    public static File getOTCFolder(String userName) {
        File userFolder = getFolder(userName);
        if (userFolder.isDirectory()) {
            return FileUtil.createFolderIfNoExist(userFolder, "OTC");
        }
        return new File(userFolder, "OTC");
    }

    public static File getConfigFile(String userName) {
        return new File(getFolder(userName), "config.json");
    }

    public static File getConfigFile(String userName, String fileName) {
        File configFile = new File(getFolder(userName), fileName + ".json");
        if(!configFile.isFile()){
            FileUtil.createFile(new ObjectMapper().createObjectNode(), configFile);
        }
        return configFile;
    }

    public static File getGoogleAuthenticatorFile(String userName) {
        return new File(getFolder(userName), "googleAuthenticator.json");
    }

    public static File getGAQRCodeFile(String userName) {
        return new File(getFolder(userName), "gaQRCode.png");
    }

    public static File getBalanceFolder(String userName) {
        return new File(getFolder(userName), "Balance");
    }

    public static File getMCBalanceFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "MCBalance"));
    }

    public static File getAttachmentsFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "Attachments"));
    }

    public static File getBalanceNotificationsFile(String userName) {
        return new File(getFolder(userName), "balanceNotifications.json");
    }

    public static File getBalanceNotificationsFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "BalanceNotifications"));
    }

    public static File getAttachmentFile(String userName, String fileName) {
        return new File(getAttachmentsFolder(userName), fileName);
    }

    public static File getMessagesFolder(String userName) {
        return FileUtil.createFolderIfNoExist(getFolder(userName), "Messages");
    }

    public static File getMessagesOldFolder(String userName) {
        return FileUtil.createFolderIfNoExist(getMessagesFolder(userName), "Old");
    }

    public static File getLockFile(String userName) {
        return new File(getFolder(userName), "lock.json");
    }

    public static File getModelsFolder(String userModelName) {
        return new File(getFolder(userModelName.split("__")[0]), "Models");
    }

    public static File getModelFolder(String userModelName) {
        return new File(getModelsFolder(userModelName), userModelName);
    }

    public static File getOTCCurrencyFolder(String userName, String currency) {
        return FileUtil.createFolderIfNoExist(getOTCFolder(userName), currency);
    }

    public static File getOTCCurrencyPaymentsFile(String userName, String currency) {
        return FileUtil.createFolderIfNoExist(getOTCCurrencyFolder(userName, currency), "payments.json");
    }

    public static File getOTCCurrencyOperationTypeFolder(String userName, String currency, String type) {
        return FileUtil.createFolderIfNoExist(getOTCCurrencyFolder(userName, currency), type);
    }

    public static File getModelFile(String userModelName) {
        return new File(getModelFolder(userModelName), "config.json");
    }

    public static File getModelBalanceFile(String userModelName) {
        return new File(getModelFolder(userModelName), "balance.json");
    }

    public static File getModelCommentsFile(String userModelName) {
        return new File(getModelFolder(userModelName), "comments.json");
    }

    public static File getAllowedAddPaymentsFile(String userName) {
        return new File(getFolder(userName), "allowedAddPayments.json");
    }

    public static File getHmacFile(String userName) {
        return new File(getFolder(userName), "hmac.json");
    }

    public static File getHmacNewFile(String userName) {
        return new File(getFolder(userName), "hmacNew.json");
    }

    public static File getGiftCardFile(String userName) {
        File giftCardFile = new File(getFolder(userName), "giftCard.json");
        if (!giftCardFile.isFile()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode giftCard = mapper.createObjectNode();
            FileUtil.createFile(giftCard, giftCardFile);
        }
        return giftCardFile;
    }

    public static File getMCMessagesFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "MCMessages"));
    }

    public static File getMCMessagesLastConnectionFile(String userName) {
        return new File(getMCMessagesFolder(userName), "lastConnection.json");
    }

    public static File getMCMessagesLastWritingFile(String userName) {
        return new File(getMCMessagesFolder(userName), "lastWriting.json");
    }

    public static File getMCMessagesPendingToDeliverFile(String userName) {
        return new File(getMCMessagesFolder(userName), "pendingToDeliver.json");
    }

    public static File getMCMessagesFolder(String userName, String chatRoom) {
        return FileUtil.createFolderIfNoExist(new File(getMCMessagesFolder(userName), chatRoom));
    }

    public static File getMCMessagesOldFolder(String userName, String chatRoom) {
        return FileUtil.createFolderIfNoExist(new File(getMCMessagesFolder(userName, chatRoom), "Old"));
    }

    public static File getMCMessagesAttachmentsFolder(String userName, String chatRoom) {
        return FileUtil.createFolderIfNoExist(new File(getMCMessagesFolder(userName, chatRoom), "Attachments"));
    }

    public static File getNotificationsFolder(String userName) {
        return FileUtil.createFolderIfNoExist(new File(getFolder(userName), "Notifications"));
    }

    public static File getInfoFile() {
        File usersInfoFile = new File(OPERATOR_PATH, "usersInfo.json");
        if (!usersInfoFile.isFile()) {
            FileUtil.createFile(new ObjectMapper().createObjectNode(), usersInfoFile);
        }
        return usersInfoFile;
    }

    public static File getChargesNewFile(String userName) {
        return new File(getFolder(userName), "chargesNew.json");
    }
    
    public static File getBlockedUsersFile(String userName) {
        return new File(getFolder(userName), "blockedUsers.json");
    }

}
