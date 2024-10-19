/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

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

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 50)
@WebServlet()
public class UserAddAttachmentTestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String userName = null;
        String fieldName = null;
        InputStream attachment = null;
        String attachmentFileName = null;
        String type = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "userName":
                    userName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "fieldName":
                    fieldName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "attachment":
                    attachment = part.getInputStream();
                    attachmentFileName = part.getSubmittedFileName();
                    break;
                case "type":
                    type = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
            }
        }
        if (userName == null || fieldName == null) {
            resp.sendError(500, "wrong number of params");
            return;
        }
        File userConfigFile = UsersFolderLocator.getConfigFile(userName);
        if(type != null){
            userConfigFile = UsersFolderLocator.getConfigFile(userName, type);
        }
        if (!userConfigFile.isFile()) {
            resp.sendError(500, "user does not exist");
            return;
        }
        JsonNode userConfig = new ObjectMapper().readTree(userConfigFile);
        if (userConfig.has(fieldName) && !fieldName.equals("profileImage")) {
            resp.sendError(500, "field name can not be used");
            return;
        }
        if (attachment != null && attachmentFileName != null) {
            attachmentFileName = attachmentFileName.replace(" ", "_").replaceAll("[^a-zA-Z0-9]", "");
            int preffix = (int) (Math.random() * 9000) + 1000;
            attachmentFileName = preffix + "_" + attachmentFileName;
            FileUtil.writeInFile(new File(UsersFolderLocator.getAttachmentsFolder(userName), attachmentFileName), attachment);
        }
        ((ObjectNode) userConfig).put(fieldName, attachmentFileName);
        FileUtil.editFile(userConfig, userConfigFile);
    }

}
