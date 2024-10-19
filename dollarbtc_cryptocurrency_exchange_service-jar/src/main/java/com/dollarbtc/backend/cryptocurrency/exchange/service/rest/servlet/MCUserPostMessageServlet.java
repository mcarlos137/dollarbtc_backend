/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.mcuser.MCUserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
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

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 50)
@WebServlet()
public class MCUserPostMessageServlet extends HttpServlet {

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
            }
        }
        if (userName == null || message == null || chatRoom == null) {
            resp.sendError(500, "wrong number of params");
        }
        String id = DateUtil.getFileDate(DateUtil.getCurrentDate());
        boolean hasAttachment = false;
        if (attachment != null && attachmentFileName != null) {
            hasAttachment = true;
            String attachmentFileNameExtention;
            if (attachmentFileName.contains(".")) {
                attachmentFileNameExtention = attachmentFileName.substring(attachmentFileName.lastIndexOf(".") + 1);
            } else {
                attachmentFileNameExtention = "jpeg";
            }
            attachmentFileName = id + "." + attachmentFileNameExtention;
            FileUtil.writeInFile(new File(UsersFolderLocator.getMCMessagesAttachmentsFolder(userName, chatRoom), attachmentFileName), attachment);
        }
        String response = new MCUserPostMessage(userName, chatRoom, id, message, replyId, hasAttachment, publicKey).getResponse();
        if (!response.equals("OK")) {
            resp.sendError(500, response);
        }
    }

}
