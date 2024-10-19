/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AttachmentsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStream;

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 50)
@WebServlet()
public class UploadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        InputStream attachment = null;
        String attachmentFileName = null;

        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "attachment":
                    attachment = part.getInputStream();
                    attachmentFileName = part.getSubmittedFileName();
                    break;
            }
        }
        if (attachment != null && attachmentFileName != null) {
            attachmentFileName = attachmentFileName.replace(" ", "_").replaceAll("[^a-zA-Z0-9]", "");
            ObjectMapper mapper = new ObjectMapper();
            String subfolderName = BaseOperation.getId();
            File attachmentSubFolder = FileUtil.createFolderIfNoExist(new File(AttachmentsFolderLocator.getFolder(), subfolderName));
            File attachmentConfigFile = new File(AttachmentsFolderLocator.getFolder(), "config.json");
            JsonNode attachmentConfig = mapper.readTree(attachmentConfigFile);
            String attachmentURL = attachmentConfig.get("serverURL").textValue() + "/" + attachmentSubFolder.getName() + "/" + attachmentFileName;
            FileUtil.writeInFile(new File(attachmentSubFolder, attachmentFileName), attachment);
            resp.setStatus(200);
            resp.getWriter().write(attachmentURL);
            resp.flushBuffer();
        } else {
            resp.sendError(500, "wrong number of params");
        }
    }

}
