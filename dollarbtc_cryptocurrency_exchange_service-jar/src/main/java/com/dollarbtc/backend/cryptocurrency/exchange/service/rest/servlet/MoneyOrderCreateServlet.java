/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyOrdersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author carlosmolina
 */
@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 50)
@WebServlet()
public class MoneyOrderCreateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String userName = null;
        String currency = null;
        String senderName = null;
        String orderId = null;
        Double amount = null;
        InputStream image = null;
        String imageFileName = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "userName":
                    userName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "currency":
                    currency = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "senderName":
                    senderName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "orderId":
                    orderId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "amount":
                    amount = Double.parseDouble(IOUtils.toString(part.getInputStream(), "utf8"));
                    break;
                case "image":
                    image = part.getInputStream();
                    imageFileName = part.getSubmittedFileName();
                    break;
            }
        }
        if (userName == null || currency == null || senderName == null || amount == null) {
            resp.sendError(500, "wrong number of params");
        }
        String id = BaseOperation.getId();
        if (image != null && imageFileName != null) {
            String imageFileNameExtention;
            if(imageFileName.contains(".")){
                imageFileNameExtention = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
            } else {
                imageFileNameExtention = "jpeg";
            }
            imageFileName = id + "." + imageFileNameExtention;
            FileUtil.writeInFile(new File(MoneyOrdersFolderLocator.getAttachmentsFolder(), imageFileName), image);
        }
        ObjectMapper mapper = new ObjectMapper();
        File moneyOrdersOperationsPROCESSINGFolder = new File(MoneyOrdersFolderLocator.getOperationsFolder(), "PROCESSING");
        ObjectNode additionals = mapper.createObjectNode();
        additionals.put("id", id);
        additionals.put("senderName", senderName);
        if(orderId != null){
            additionals.put("orderId", orderId);
        }        
        JsonNode charges = BaseOperation.getCharges(
                currency,
                amount,
                BalanceOperationType.MONEY_ORDER_SEND,
                null,
                "MONEYCLICK",
                null
        );
        BaseOperation.addToBalance(
                UsersFolderLocator.getMCBalanceFolder(userName),
                currency,
                amount,
                BalanceOperationType.MONEY_ORDER_SEND,
                BalanceOperationStatus.PROCESSING,
                null,
                null,
                charges,
                false,
                additionals
        );
        JsonNode moneyOrderOperation = mapper.createObjectNode();
        ((ObjectNode) moneyOrderOperation).put("userName", userName);
        ((ObjectNode) moneyOrderOperation).put("currency", currency);
        ((ObjectNode) moneyOrderOperation).put("senderName", senderName);
        if(orderId != null){
            ((ObjectNode) moneyOrderOperation).put("orderId", orderId);
        }
        ((ObjectNode) moneyOrderOperation).put("amount", amount);
        ((ObjectNode) moneyOrderOperation).put("id", id);
        ((ObjectNode) moneyOrderOperation).put("timestamp", DateUtil.getCurrentDate());
        if(imageFileName != null){
            ((ObjectNode) moneyOrderOperation).put("imageFileName", imageFileName);
        }
        FileUtil.createFile(moneyOrderOperation, new File(moneyOrdersOperationsPROCESSINGFolder, id + ".json"));
    }

}
