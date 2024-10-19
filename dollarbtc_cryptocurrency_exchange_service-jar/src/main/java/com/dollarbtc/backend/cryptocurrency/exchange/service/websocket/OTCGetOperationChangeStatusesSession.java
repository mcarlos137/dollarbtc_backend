/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.websocket;

import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

/**
 *
 * @author CarlosDaniel
 */
public class OTCGetOperationChangeStatusesSession {

    private final static Map<Session, JsonNode> SESSIONS = new HashMap<>();
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static void addSession(Session session, JsonNode jsonNode) {
        SESSIONS.put(session, jsonNode);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Message(SESSIONS), 0, 15, TimeUnit.SECONDS);
    }

    public static void removeSession(Session session) {
        SESSIONS.remove(session);
    }
    
    public static class Message implements Runnable {

        private static Map<Session, JsonNode> sessions;

        public Message(Map<Session, JsonNode> sessions) {
            Message.sessions = sessions;
        }

        @Override
        public void run() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                for (Session session : sessions.keySet()) {
                    JsonNode jsonNode = sessions.get(session);
                    String userName = jsonNode.get("userName").textValue();
                    Map<String, String> params = new HashMap<>();
                    params.put("userName", userName);
                    File otcOperationsChangeStatusesUserNameFolder = OTCFolderLocator.getOperationsChangeStatusesUserNameFolder(null, userName);
                    File otcOperationsChangeStatusesOldFolder = OTCFolderLocator.getOperationsChangeStatusesOldFolder(null);
                    ArrayNode operationChangeStatuses = mapper.createArrayNode();
                    if(otcOperationsChangeStatusesUserNameFolder.listFiles().length == 0){
                        continue;
                    }
                    for(File otcOperationsChangeStatusesUserNameOperationFile : otcOperationsChangeStatusesUserNameFolder.listFiles()){
                        JsonNode operationChangeStatus = mapper.readTree(otcOperationsChangeStatusesUserNameOperationFile);
                        operationChangeStatuses.add(operationChangeStatus);
                        FileUtil.moveFileToFolder(otcOperationsChangeStatusesUserNameOperationFile, otcOperationsChangeStatusesOldFolder);
                    }
                    session.getRemote().sendString(ServiceUtil.createWSResponseWithData(operationChangeStatuses, "operationChangeStatuses", "params", params).toString());
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetOperationChangeStatusesSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
;