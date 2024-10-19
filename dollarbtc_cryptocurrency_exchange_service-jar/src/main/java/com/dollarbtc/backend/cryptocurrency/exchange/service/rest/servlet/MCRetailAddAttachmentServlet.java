/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.MCRetailCreateStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
public class MCRetailAddAttachmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String retailId = null;
        InputStream attachment = null;
        String attachmentFileName = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "retailId":
                    retailId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "attachment":
                    attachment = part.getInputStream();
                    attachmentFileName = part.getSubmittedFileName();
                    break;
            }
        }
        if (retailId == null || attachmentFileName == null) {
            resp.sendError(500, "wrong number of params");
            return;
        }
        File moneyclickRetailConfigFile = MoneyclickFolderLocator.getRetailConfigFile(retailId);
        if (!moneyclickRetailConfigFile.isFile()) {
            resp.sendError(500, "retail does not exist");
            return;
        }
        JsonNode moneyclickRetailConfig = new ObjectMapper().readTree(moneyclickRetailConfigFile);
        MCRetailCreateStatus baseMCRetailCreateStatus = MCRetailCreateStatus.valueOf(moneyclickRetailConfig.get("mcRetailCreateStatus").textValue());
        if (!baseMCRetailCreateStatus.equals(MCRetailCreateStatus.ANALYSING)) {
            resp.sendError(500, "retail is not in analysing status");
            return;
        }
        ArrayNode moneyclickRetailConfigAttachments = (ArrayNode) moneyclickRetailConfig.get("attachments");
        if (attachment != null) {
            attachmentFileName = attachmentFileName.replace(" ", "_").replaceAll("[^a-zA-Z0-9]", "");
            FileUtil.writeInFile(new File(MoneyclickFolderLocator.getRetailAttachmentsFolder(retailId), attachmentFileName), attachment);
        }
        moneyclickRetailConfigAttachments.add(attachmentFileName);
        FileUtil.editFile(moneyclickRetailConfig, moneyclickRetailConfigFile);
    }

}
