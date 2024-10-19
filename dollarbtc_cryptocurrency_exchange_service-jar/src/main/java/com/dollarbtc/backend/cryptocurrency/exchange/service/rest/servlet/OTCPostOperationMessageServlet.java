/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.OperationMessageSide;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.otc.OTCGetOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.user.UserPostMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
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
public class OTCPostOperationMessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String id = null;
        String userName = null;
        String message = null;
        OperationMessageSide operationMessageSide = null;
        InputStream attachment = null;
        String attachmentFileName = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "id":
                    id = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "userName":
                    userName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "message":
                    message = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "operationMessageSide":
                    operationMessageSide = OperationMessageSide.valueOf(IOUtils.toString(part.getInputStream(), "utf8"));
                    break;
                case "attachment":
                    attachment = part.getInputStream();
                    attachmentFileName = part.getSubmittedFileName();
                    break;
            }
        }
        if(id == null || userName == null || message == null || operationMessageSide == null){
            resp.sendError(500, "wrong number of params");
        }
        JsonNode otcOperationId = new OTCGetOperation(id).getResponse();
        if(!otcOperationId.has("id")){
            resp.sendError(500, "wrong operation id");
        }
        if(attachment != null && attachmentFileName != null){
            attachmentFileName = attachmentFileName.replace(" ", "_").replaceAll("[^a-zA-Z0-9]", "");
            FileUtil.writeInFile(new File(OTCFolderLocator.getOperationIdAttachmentsFolder(null, id), attachmentFileName), attachment);
        }
        if (otcOperationId.has("otcOperationType") && 
                (operationMessageSide.equals(OperationMessageSide.ADMIN) || operationMessageSide.equals(OperationMessageSide.BOTH))) {
            String otcOperationIdOperationType = otcOperationId.get("otcOperationType").textValue();
            new UserPostMessage(otcOperationId.get("userName").textValue(), "Operation id " + id + " has new message ", otcOperationIdOperationType.toLowerCase() + "?id=" + id).getResponse();
        }
        BaseOperation.postOperationMessage(id, userName, message, operationMessageSide, attachmentFileName);
    }
    
}
