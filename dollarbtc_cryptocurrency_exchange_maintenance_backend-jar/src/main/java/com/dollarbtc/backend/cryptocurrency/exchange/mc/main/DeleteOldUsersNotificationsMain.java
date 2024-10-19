/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author CarlosDaniel
 */
public class DeleteOldUsersNotificationsMain {

    public static void main(String[] args) {
        System.out.println("Starting DeleteOldUsersNotificationsMain");
        String baseTimestamp = DateUtil.getDateHoursBefore(DateUtil.getCurrentDate(), 72);
        for (File userFolder : UsersFolderLocator.getFolder().listFiles()) {
            if (!userFolder.isDirectory()) {
                continue;
            }
            String userName = userFolder.getName();
            System.out.println("userName " + userName);
//            if (!userName.equals("584245522788")) {
//                continue;
//            }
            File userNotificationFolder = UsersFolderLocator.getNotificationsFolder(userName);
            Set<String> timestampsToDelete = new HashSet<>();
            for (File userNotificationFile :  userNotificationFolder.listFiles()) {
                String timestamp = DateUtil.getDate(userNotificationFile.getName().replace(".json", ""));
                if(timestamp.compareTo(baseTimestamp) <= 0){
                    timestampsToDelete.add(DateUtil.getFileDate(timestamp) + ".json");
                }
            }
            for(String timestampToDelete : timestampsToDelete){
                File userNotificationToDelete = new File(userNotificationFolder, timestampToDelete);
                System.out.println("" + userNotificationToDelete);
                FileUtil.deleteFile(userNotificationToDelete);
            }
        }
        System.out.println("Finishing DeleteOldUsersNotificationsMain");
    }

}
