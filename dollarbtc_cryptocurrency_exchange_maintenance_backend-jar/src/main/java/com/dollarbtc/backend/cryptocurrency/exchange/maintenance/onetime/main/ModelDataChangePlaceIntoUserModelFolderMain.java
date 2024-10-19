/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.maintenance.onetime.main;

/**
 *
 * @author CarlosDaniel
 */
public class ModelDataChangePlaceIntoUserModelFolderMain {

    public static void main(String[] args) {
//        File usersFolder = new File(ROOT_PATH, "Users");
//        List<String> exchangeIdSymbols = ExchangeUtil.getExchangeIdSymbols(null, null);
//        for (File userFolder : usersFolder.listFiles()) {
//            if (!userFolder.isDirectory()) {
//                continue;
//            }
//            String userName = userFolder.getName();
//            for (String modelName : ExchangeUtil.getModelNames(userName, false)) {
//                File userModelFolder =  new File(new File(new File(usersFolder, userName), "Models"), modelName); 
//                if(!userModelFolder.exists() || !userModelFolder.isDirectory()){
//                    continue;
//                }
//                for(String exchangeIdSymbol : exchangeIdSymbols){
//                    if(exchangeIdSymbol.equals("HitBTC__ADAUSD")){
//                        continue;
//                    }
//                    String exchangeId = exchangeIdSymbol.split("__")[0];
//                    String symbol = exchangeIdSymbol.split("__")[1];
//                    File exchangeIdSymbolModelFolder =  new File(new File(new File(new File(ROOT_PATH, "Exchanges"), exchangeId), symbol), modelName); 
//                    if(!exchangeIdSymbolModelFolder.exists() || !exchangeIdSymbolModelFolder.isDirectory()){
//                        continue;
//                    }
//                    File userModelExchangeIdSymbolFolder = FileUtil.createFolderIfNoExist(userModelFolder, exchangeIdSymbol);
//                    for(File oldExchangeIdSymbolModelF : exchangeIdSymbolModelFolder.listFiles()){
//                        if(!oldExchangeIdSymbolModelF.isDirectory()){
//                            continue;
//                        } 
//                        System.out.println("moving: " + oldExchangeIdSymbolModelF + " to " + userModelExchangeIdSymbolFolder);
//                        FileUtil.moveFolderToFolder(oldExchangeIdSymbolModelF, userModelExchangeIdSymbolFolder);
//                    }
//                }
//            }
//        }
    }

}
