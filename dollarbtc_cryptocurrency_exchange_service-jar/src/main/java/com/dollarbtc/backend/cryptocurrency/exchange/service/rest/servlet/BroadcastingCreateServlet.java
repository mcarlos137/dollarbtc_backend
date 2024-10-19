/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BroadcastingType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
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

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 50)
@WebServlet()
public class BroadcastingCreateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String userName = null;
        String name = null;
        String title = null;
        String description = null;
        InputStream image = null;
        String imageFileName = null;
        Double subscriptionPrice = null;
        BroadcastingType broadcastingType = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "userName":
                    userName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "name":
                    name = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "title":
                    title = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "description":
                    description = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "image":
                    image = part.getInputStream();
                    imageFileName = part.getSubmittedFileName();
                    break;
                case "subscriptionPrice":
                    subscriptionPrice = Double.parseDouble(IOUtils.toString(part.getInputStream(), "utf8"));
                    break;
                case "type":
                    broadcastingType = BroadcastingType.valueOf(IOUtils.toString(part.getInputStream(), "utf8"));
                    break;
            }
        }
        if (userName == null || name == null || title == null || description == null || image == null || subscriptionPrice == null || broadcastingType == null) {
            resp.sendError(500, "wrong number of params");
        }
        if (image != null && imageFileName != null) {
            String imageFileNameExtention;
            if (imageFileName.contains(".")) {
                imageFileNameExtention = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
            } else {
                imageFileNameExtention = "jpeg";
            }
            imageFileName = userName + "__" + new Date().getTime() + "." + imageFileNameExtention;
            FileUtil.writeInFile(new File(BroadcastingFolderLocator.getAttachmentsFolder(), imageFileName), image);
       }
        String response = new BroadcastingCreate(
                new BroadcastingCreateRequest(
                        userName,
                        name,
                        title,
                        description,
                        imageFileName,
                        subscriptionPrice,
                        broadcastingType
                )
        ).getResponse();
        if (!response.equals("OK")) {
            resp.sendError(500, response);
        }
        //createDeliveredThread(userName, chatRoom, time, operationId);
    }

    /*private void createDeliveredThread(String userName, String chatRoom, Long time, String operationId) {
        Thread createDeliveredThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BroadcastingCreateServlet.class.getName()).log(Level.SEVERE, null, ex);
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
                        Logger.getLogger(BroadcastingCreateServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        createDeliveredThread.start();
    }*/

}
