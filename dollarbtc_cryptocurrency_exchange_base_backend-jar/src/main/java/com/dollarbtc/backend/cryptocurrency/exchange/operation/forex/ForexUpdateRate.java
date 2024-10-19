/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.forex;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ForexFolderLocator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLContextBuilder;

/**
 *
 * @author carlosmolina
 */
public class ForexUpdateRate extends AbstractOperation<Void> {

    private final String[] symbols;
    
    public ForexUpdateRate(String[] symbols) {
        super(Void.class);
        this.symbols = symbols;
    }
    
    @Override
    protected void execute() {
        try {
            this.method();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
            Logger.getLogger(ForexUpdateRate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private synchronized void method() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        for (String symbol : symbols) {
            String urlPage = "http://www.freeforexapi.com/api/live?pairs=" + symbol.toUpperCase();
            try {
                JsonNode rate = mapper.readTree(BaseOperation.requestGet(BaseOperation.getCloseableHttpClient(sslContext), urlPage, null));
                String timestamp = DateUtil.getMinuteStartDate(null);
                if (!rate.has("rates")) {
                    continue;
                }
                if (!rate.get("rates").has(symbol)) {
                    continue;
                }
                ((ObjectNode) rate.get("rates").get(symbol)).put("forexDate", rate.get("rates").get(symbol).get("timestamp").longValue());
                ((ObjectNode) rate.get("rates").get(symbol)).put("timestamp", timestamp);
                File forexRatesFolder = ForexFolderLocator.getRatesSymbolFolder(symbol);
                File forexRatesOldFolder = FileUtil.createFolderIfNoExist(forexRatesFolder, "Old");
                for (File forexRateFile : forexRatesFolder.listFiles()) {
                    if (!forexRateFile.isFile()) {
                        continue;
                    }
                    FileUtil.moveFileToFolder(forexRateFile, forexRatesOldFolder);
                }
                FileUtil.createFile(rate.get("rates").get(symbol), new File(forexRatesFolder, DateUtil.getFileDate(timestamp) + ".json"));
            } catch (IOException ex) {
                Logger.getLogger(ForexUpdateRate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
