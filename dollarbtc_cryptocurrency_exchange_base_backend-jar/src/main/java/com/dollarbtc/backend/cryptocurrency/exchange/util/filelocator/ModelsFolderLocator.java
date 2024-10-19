/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator;

import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import java.io.File;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;

/**
 *
 * @author ricardo torres
 */
public class ModelsFolderLocator {
    
    public static File getFolder() {
        return FileUtil.createFolderIfNoExist(new File(OPERATOR_PATH, "Models"));
    }
    
    public static File getFolder(String modelName) {
        File modelsFolder = getFolder();
        for (File environmentModelsFolder : modelsFolder.listFiles()) {
            for (File level1ModelsFolder : environmentModelsFolder.listFiles()) {
                if (level1ModelsFolder.isDirectory() && level1ModelsFolder.getName().equals(modelName)) {
                    return level1ModelsFolder;
                }
                for (File level2ModelsFolder : level1ModelsFolder.listFiles()) {
                    if (level2ModelsFolder.isDirectory() && level2ModelsFolder.getName().equals(modelName)) {
                        return level2ModelsFolder;
                    }
                }
            }
        }
        return null;
    }

    public static File getDataFile(String modelName) {
        return new File(getFolder(modelName), "data.json");
    }

    public static File getFile(String modelName) {
        return new File(getFolder(modelName), "config.json");
    }
        
}
