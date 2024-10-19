/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.otc.main;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author CarlosDaniel
 */
public class DeleteIndexesForOldOperationsMain {

    public static void main(String[] args) {
        System.out.println("starting DeleteIndexesForOldOperationsMain");
        String timestamp = DateUtil.getCurrentDate();
        File otcOperationsIndexesTimestampsFolder = OTCFolderLocator.getOperationsIndexesSpecificFolder(null, "Timestamps");
        Map<String, File> deleteIndexesFileNames = new HashMap<>();
        boolean breakLoop = false;
        for (File otcOperationsIndexesTimestampFolder : otcOperationsIndexesTimestampsFolder.listFiles()) {
            if (!otcOperationsIndexesTimestampFolder.isDirectory()) {
                continue;
            }
            if (DateUtil.getDateDaysBefore(timestamp, 90).compareTo(DateUtil.getDate(otcOperationsIndexesTimestampFolder.getName())) < 0) {
                continue;
            }
            for (File otcOperationsIndexFile : otcOperationsIndexesTimestampFolder.listFiles()) {
                if (!otcOperationsIndexFile.isFile()) {
                    continue;
                }
                deleteIndexesFileNames.put(otcOperationsIndexFile.getName(), otcOperationsIndexesTimestampFolder);
                if(deleteIndexesFileNames.keySet().size() >= 500){
                    breakLoop = true;
                    break;
                }
            }
            if(breakLoop){
                break;
            }
        }
        System.out.println("deleteIndexesFileNames.keySet().size(): " + deleteIndexesFileNames.keySet().size());
        File otcOperationsIndexesFolder = OTCFolderLocator.getOperationsIndexesFolder(null);
        for (String deleteIndexesFileName : deleteIndexesFileNames.keySet()) {
            System.out.println("----------------------------------------------------------");
            for (File otcOperationsIndexFolder : otcOperationsIndexesFolder.listFiles()) {
                if (!otcOperationsIndexFolder.isDirectory()) {
                    continue;
                }
                if (otcOperationsIndexFolder.getName().equals("Timestamps")) {
                    continue;
                }
                if (otcOperationsIndexFolder.getName().equals("UserNames")) {
                    continue;
                }
                for (File otcOperationsIndexValueFolder : otcOperationsIndexFolder.listFiles()) {
                    if (!otcOperationsIndexValueFolder.isDirectory()) {
                        continue;
                    }
                    if (new File(otcOperationsIndexValueFolder, deleteIndexesFileName).isFile()) {
                        System.out.println("deleting index " + deleteIndexesFileName + " " + otcOperationsIndexFolder.getName() + " " + otcOperationsIndexValueFolder.getName());
                        FileUtil.deleteFile(new File(otcOperationsIndexValueFolder, deleteIndexesFileName));
                    }
                }
            }
            System.out.println("deleting timestamps " + deleteIndexesFileNames.get(deleteIndexesFileName).getAbsolutePath());
            FileUtil.deleteFolder(deleteIndexesFileNames.get(deleteIndexesFileName));
            System.out.println("----------------------------------------------------------");
        }
        System.out.println("finishing DeleteIndexesForOldOperationsMain");
    }

}
