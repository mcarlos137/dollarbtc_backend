/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserGetMessagesNew;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserPostMessageNew;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.notification.NotificationSendMessageByUserName;
import com.dollarbtc.backend.cryptocurrency.exchange.service.util.ServiceUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.service.websocket.MCUserSessions;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 50)
@WebServlet()
public class MCUserPostMessageNewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String userName = null;
        String chatRoom = null;
        String message = null;
        String replyId = null;
        InputStream attachment = null;
        String attachmentFileName = null;
        String publicKey = null;
        Long time = null;
        String operationId = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "userName":
                    userName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "chatRoom":
                    chatRoom = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "message":
                    message = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "replyId":
                    replyId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "attachment":
                    attachment = part.getInputStream();
                    attachmentFileName = part.getSubmittedFileName();
                    break;
                case "publicKey":
                    publicKey = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "time":
                    time = Long.parseLong(IOUtils.toString(part.getInputStream(), "utf8"));
                    break;
                case "operationId":
                    operationId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
            }
        }
        if (userName == null || message == null || chatRoom == null) {
            resp.sendError(500, "wrong number of params");
        }
        if (time == null) {
            time = new Date().getTime();
        }
        if (attachment != null && attachmentFileName != null) {
            String attachmentFileNameExtention;
            if (attachmentFileName.contains(".")) {
                attachmentFileNameExtention = attachmentFileName.substring(attachmentFileName.lastIndexOf(".") + 1);
            } else {
                attachmentFileNameExtention = "jpeg";
            }
            attachmentFileName = time + "." + attachmentFileNameExtention;
            if (operationId == null) {
                FileUtil.writeInFile(new File(UsersFolderLocator.getMCMessagesAttachmentsFolder(userName, chatRoom), attachmentFileName), attachment);
            } else {
                FileUtil.writeInFile(new File(UsersFolderLocator.getMCMessagesAttachmentsFolder(userName, chatRoom + "__" + operationId), attachmentFileName), attachment);
            }
        }
        String response = new MCUserPostMessageNew(userName, chatRoom, message, replyId, attachmentFileName, publicKey, time, operationId).getResponse();
        if (!response.equals("OK")) {
            resp.sendError(500, response);
        }
        String key = chatRoom;
        if (operationId != null) {
            key = key + "__" + operationId;
        }
        if (MCUserSessions.getInstance().data.containsKey(key)) {
            Map<String, String> params = new HashMap<>();
            params.put("userName", chatRoom);
            params.put("chatRoom", userName);
            if (operationId != null) {
                params.put("operationId", operationId);
            }
            JsonNode messages = new MCUserGetMessagesNew(userName, key, false).getResponse();
            if (messages.has("newMessage") && messages.get("newMessage").booleanValue()) {
                ((ObjectNode) messages).remove("newMessage");
                MCUserSessions.getInstance().data.get(key).getRemote().sendString(ServiceUtil.createWSResponseWithData(messages, "currentMessages", "params", params).toString());
            }
        } else {
            createDeliveredThread(userName, chatRoom, time, operationId);
        }
    }

    private void createDeliveredThread(String userName, String chatRoom, Long time, String operationId) {
        Thread createDeliveredThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MCUserPostMessageNewServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            File usersMCMessagesPendingToDeliverFile = UsersFolderLocator.getMCMessagesPendingToDeliverFile(chatRoom);
            File messageFile;
            if (operationId == null) {
                messageFile = new File(UsersFolderLocator.getMCMessagesFolder(chatRoom, userName), time + ".json");
            } else {
                messageFile = new File(UsersFolderLocator.getMCMessagesFolder(chatRoom, userName + "__" + operationId), time + ".json");
            }
            if (messageFile.isFile()) {
                if (!usersMCMessagesPendingToDeliverFile.isFile()) {
                    ObjectNode pendingToDeliver = new ObjectMapper().createObjectNode();
                    pendingToDeliver.put("notificationSended", true);
                    pendingToDeliver.put("timestamp", DateUtil.getCurrentDate());
                    FileUtil.editFile(pendingToDeliver, usersMCMessagesPendingToDeliverFile);
                    //SEND NOTIFICATION
                    new NotificationSendMessageByUserName(chatRoom, "Chat", "You have new messages").getResponse();
                } else {
                    try {
                        JsonNode pendingToDeliver = new ObjectMapper().readTree(usersMCMessagesPendingToDeliverFile);
                        String currentTimestamp = DateUtil.getCurrentDate();
                        if (pendingToDeliver.has("notificationSended") && !pendingToDeliver.get("notificationSended").booleanValue()
                                || pendingToDeliver.has("timestamp") && DateUtil.getDateMinutesBefore(currentTimestamp, 10).compareTo(pendingToDeliver.get("timestamp").textValue()) > 0) {
                            ((ObjectNode) pendingToDeliver).put("notificationSended", true);
                            ((ObjectNode) pendingToDeliver).put("timestamp", currentTimestamp);
                            FileUtil.editFile(pendingToDeliver, usersMCMessagesPendingToDeliverFile);
                            //SEND NOTIFICATION
                            new NotificationSendMessageByUserName(chatRoom, "Chat", "You have new messages").getResponse();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MCUserPostMessageNewServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        createDeliveredThread.start();
    }

}
