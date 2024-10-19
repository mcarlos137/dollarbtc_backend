/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.calculation.modulator;

import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.TimeZone;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 *
 * @author CarlosDaniel
 */
public abstract class BasicModulator {
    
    protected final String threadName;

    public BasicModulator(String threadName) {
        this.threadName = threadName;
    }
   
    public void start(Integer scanPeriodInMilliseconds) {
        File threadFile = new File(new File(OPERATOR_PATH, "Threads"), threadName + ".json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode thread = null;
        if(!threadFile.isFile()){
            thread = mapper.createObjectNode();
            ((ObjectNode) thread).put("active", true);
            FileUtil.createFile(thread, threadFile);
        } else {
            try {
                thread = mapper.readTree(threadFile);
                boolean threadActive = thread.get("active").booleanValue();
                if(threadActive){
                    System.exit(0);
                }
            } catch (IOException ex) {
                Logger.getLogger(BasicModulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ((ObjectNode) thread).put("active", true);
        FileUtil.editFile(thread, threadFile);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("start date: " + DateUtil.getCurrentDate());
        thread(scanPeriodInMilliseconds);
        ((ObjectNode) thread).put("active", false);
        FileUtil.editFile(thread, threadFile);
        System.exit(0);
    }

    private void thread(Integer scanPeriodInMilliseconds) {
        if(scanPeriodInMilliseconds == null){
            runModulator();
            return;
        }
        int elapsedTime = 0;
        int timePeriod = 1000;
        while (true) {
            try {
                Thread.sleep(timePeriod);
                elapsedTime = elapsedTime + timePeriod;
                if (elapsedTime == scanPeriodInMilliseconds) {
                    runModulator();
                    elapsedTime = 0;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(BasicModulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected abstract void runModulator();
    
}
