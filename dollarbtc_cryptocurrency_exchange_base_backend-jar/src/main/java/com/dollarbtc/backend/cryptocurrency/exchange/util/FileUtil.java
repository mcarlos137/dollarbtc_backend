/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import java.io.FileWriter;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.InputStream;

/**
 *
 * @author CarlosDaniel
 */
public class FileUtil {

    public static File createFolderIfNoExist(String baseFolderPath, String folderName) {
        File folder = new File(baseFolderPath, folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    public static File createFolderIfNoExist(File baseFolder, String folderName) {
        File folder = new File(baseFolder, folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }
    
    public static File createFolderIfNoExist(File folder) {
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    public static boolean folderExists(File baseFolder, String folderName) {
        return new File(baseFolder, folderName).exists();
    }

    public static void createFile(Object object, File folder, String fileName) {
        try {
            if (object == null) {
                return;
            }
            File jsonNodeFile = new File(folder, fileName);
            if (jsonNodeFile.exists()) {
                return;
            }
            jsonNodeFile.createNewFile();
            FileUtils.writeStringToFile(jsonNodeFile, object.toString(), Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean createFile(Object object, File file) {
        try {
            if (object == null) {
                return false;
            }
            if (file.exists()) {
                return false;
            }
            file.createNewFile();
            FileUtils.writeStringToFile(file, object.toString(), Charset.forName("UTF-8"));
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public synchronized static void editFile(Object object, File folder, String fileName) {
        try {
            if (object == null) {
                return;
            }
            File jsonNodeFile = new File(folder, fileName);
            if (jsonNodeFile.exists()) {
                jsonNodeFile.delete();
            }
            jsonNodeFile.createNewFile();
            FileUtils.writeStringToFile(jsonNodeFile, object.toString(), Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized static void editFile(Object object, File jsonNodeFile) {
        try {
            if (object == null) {
                return;
            }
            if (jsonNodeFile.exists()) {
                jsonNodeFile.delete();
            }
            jsonNodeFile.createNewFile();
            FileUtils.writeStringToFile(jsonNodeFile, object.toString(), Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeInFile(String folderPath, String fileName, String text) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFileInFolder(getFolder(folderPath), fileName), true))) {
            writer.write(text);
            writer.newLine();
        }
    }

    public static void writeInFile(File file, String text) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(text);
            writer.newLine();
        }
    }
    
    public static void writeInFile(File file, InputStream inputStream) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileUtils.copyInputStreamToFile(inputStream, file);
    }

    public static void moveFileToFile(File oldfile, File newFile) {
        if (oldfile.isFile()) {
            try {
                FileUtils.moveFile(oldfile, newFile);
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void moveFileToFolder(File file, File newFolder) {
        if (file.isFile()) {
            try {
                File newFile = new File(newFolder, file.getName());
                if(newFile.isFile()){
                    newFile.delete();
                }
                FileUtils.moveFile(file, newFile);
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void moveFolderToFolder(File oldFolder, File newFolder) {
        if (oldFolder.isDirectory()) {
            try {
                FileUtils.moveDirectoryToDirectory(oldFolder, newFolder, true);
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void copyFileToFolder(File srcfile, File destFile) {
        if (srcfile.isFile()) {
            try {
                FileUtils.copyFile(srcfile, destFile);
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void moveAllFilesToFolder(File oldFolder, File newFolder) {
        for (File file : oldFolder.listFiles()) {
            if (file.isFile()) {
                try {
                    FileUtils.moveFile(file, new File(newFolder, file.getName()));
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static File getFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            return null;
        }
        return folder;
    }

    public static File getFileInFolder(File folder, String fileName) {
        return new File(folder, fileName);
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteFolder(File folder) {
        if (folder.exists()) {
            try {
                FileUtils.deleteDirectory(folder);
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean existFileInFolder(String folderPath, String fileName) {
        return new File(getFolder(folderPath), fileName).exists();
    }

    public static File getFileInFolder(File folder, String fileName, Long maxSize) {
        File file = new File(folder, fileName);
        if (file.length() > maxSize) {
            return null;
        }
        return file;
    }

    public static List<String> readFile(File file, Charset encoding) {
        List<String> result = new ArrayList<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), encoding)) {
            final Iterator<String> iterator = bufferedReader.lines().iterator();
            while (iterator.hasNext()) {
                String it = iterator.next();
                result.add(it);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
