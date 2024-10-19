/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public class PropertyFileReader {
    
    public static final String GENERIC_PROPERTIES_FILE = "generic.properties";
    
    public static final String HITBTC_PROPERTIES_FILE = "hitbtc.properties";
    
    public static String getPropertyFileValue(String propertyFileKey, String path) {
        String[] propertyFileKeyValues = readFile(path).toString().split(";");
        for(String propertyFileKeyValue : propertyFileKeyValues){
            if(propertyFileKeyValue.contains(propertyFileKey)){
                return propertyFileKeyValue.substring(propertyFileKeyValue.indexOf("=") + 1);
            }
        }
        return null;
    }
    
    public static String[] getPropertyFileValues(String path) {
        return readFile(path).toString().split(";");
    }

    private static StringBuilder readFile(String path) {
        File file = new File(path);
        StringBuilder builder = new StringBuilder();
        if (!file.exists()) {
            throw new RuntimeException("file not found in " + path);
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception ex) {
            Logger.getLogger(PropertyFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(PropertyFileReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return builder;
    }
    
}
