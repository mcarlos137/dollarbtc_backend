/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import java.io.File;
import com.fasterxml.jackson.databind.JsonNode;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.tradesFileId;
import com.dollarbtc.backend.cryptocurrency.exchange.util.WebsocketClientEndpoint;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CarlosDaniel
 */
public abstract class WebSocketBasic extends WebSocketClient {

    protected final String exchangeId, symbolReal, symbolExchange, operation;
    protected final int id;
    protected File exchangeFolder, symbolFolder, operationFolder;
    protected boolean mainFolderExists;
    protected boolean initLoop = true;

    public WebSocketBasic(String uri, String exchangeId, String symbol, String operation, int id) {
        super(uri);
        this.exchangeId = exchangeId;
        if(exchangeId.equals("HitBTC") && symbol.equals("XRPUSDT")){
           this.symbolExchange = symbol; 
        } else {
            this.symbolExchange = ExchangeUtil.getSymbol(exchangeId, symbol);
        }
        this.symbolReal = symbol;
        this.operation = operation;
        this.id = id;
        exchangeFolder = FileUtil.createFolderIfNoExist(FileUtil.createFolderIfNoExist(new File(System.getProperty("user.dir")).getParent(), "Exchanges").getPath(), exchangeId);
        if (this.symbolReal != null && !this.symbolReal.equals("")) {
            symbolFolder = FileUtil.createFolderIfNoExist(exchangeFolder, this.symbolReal);
        }
        mainFolderExists = FileUtil.folderExists(symbolFolder, operation);
        if (operation != null && !operation.equals("")) {
            operationFolder = FileUtil.createFolderIfNoExist(symbolFolder, operation);
        }
        WebsocketClientEndpoint.mainFolder = operationFolder;
        prepareRequest();
    }

    protected abstract void prepareRequest();

    public void start() {
        if (WebsocketClientEndpoint.isActive()) {
            System.exit(0);
        }
        WebsocketClientEndpoint.activateWebSocket();
        keepAlive();
    }

    private void keepAlive() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WebSocketBasic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected static void addLogger(String exchangeId, String symbol, String operation, String operationType, JsonNode jsonNode) {
        Set<String> labels = new HashSet<>();
        labels.add("exchangeId");
        labels.add("symbol");
        labels.add("operation");
        if (operationType != null && !operationType.equals("")) {
            labels.add("operationType");
        }
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (!labels.contains(fieldName)) {
                System.out.println(fieldName + ": " + jsonNode.get(fieldName));
            }
        }
    }

    protected boolean addOperation(String method) {
        boolean addOperation;
        switch (method) {
            case "snapshotTrades":
            case "snapshotCandles":
                addOperation = !mainFolderExists;
                break;
            default:
                addOperation = true;
                break;
        }
        if (mainFolderExists && initLoop) {
            String firstFileInFolderName = ExchangeUtil.getFirstFileInFolderName(WebsocketClientEndpoint.mainFolder);
            tradesFileId = Long.parseLong(firstFileInFolderName.replace(".json", "")) - 1L;
        }
        if(initLoop){
            initLoop = false;
        }
        return addOperation;
    }

}
