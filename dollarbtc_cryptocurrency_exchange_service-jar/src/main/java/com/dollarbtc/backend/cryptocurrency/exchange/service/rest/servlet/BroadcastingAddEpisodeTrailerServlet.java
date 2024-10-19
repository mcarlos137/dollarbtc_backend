/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingAddEpisodeTrailerRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting.BroadcastingAddEpisodeTrailer;
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
public class BroadcastingAddEpisodeTrailerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String broadcastingId = null;
        String title = null;
        String description = null;
        String type = null;
        InputStream image = null;
        String imageFileName = null;
        InputStream video = null;
        String videoFileName = null;
        String publishTimestamp = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "broadcastingId":
                    broadcastingId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "title":
                    title = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "description":
                    description = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "type":
                    type = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "image":
                    image = part.getInputStream();
                    imageFileName = part.getSubmittedFileName();
                    break;
                case "video":
                    video = part.getInputStream();
                    videoFileName = part.getSubmittedFileName();
                    break;
                case "publishTimestamp":
                    publishTimestamp = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
            }
        }
        if (broadcastingId == null || type == null || title == null || description == null || image == null || video == null) {
            resp.sendError(500, "wrong number of params");
        }
        if (image != null && imageFileName != null) {
            String imageFileNameExtention;
            if (imageFileName.contains(".")) {
                imageFileNameExtention = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
            } else {
                imageFileNameExtention = "jpeg";
            }
            imageFileName = broadcastingId + "__" + new Date().getTime() + imageFileNameExtention;
            FileUtil.writeInFile(new File(BroadcastingFolderLocator.getAttachmentsFolder(), imageFileName), image);
        }
        if (video != null && videoFileName != null) {
            String videoFileNameExtention;
            if (videoFileName.contains(".")) {
                videoFileNameExtention = videoFileName.substring(videoFileName.lastIndexOf(".") + 1);
            } else {
                videoFileNameExtention = "mpeg";
            }
            videoFileName = broadcastingId + "__" + new Date().getTime() + "." + videoFileNameExtention;
            FileUtil.writeInFile(new File(BroadcastingFolderLocator.getAttachmentsFolder(), videoFileName), video);
        }
        String response = new BroadcastingAddEpisodeTrailer(
                new BroadcastingAddEpisodeTrailerRequest(
                        broadcastingId,
                        title,
                        description,
                        type,
                        imageFileName,
                        videoFileName,
                        publishTimestamp
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
