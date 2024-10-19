/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.enums.DebitCardStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import static com.dollarbtc.backend.cryptocurrency.exchange.util.ExchangeUtil.OPERATOR_PATH;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.DebitCardsFolderLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
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
public class DebitCardRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String userName = null;
        String currency = null;
        String holderName = null;
        String phoneNumber = null;
        String model = null;
        InputStream photo = null;
        String photoFileName = null;
        String email = null;
        String type = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "userName":
                    userName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "currency":
                    currency = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "holderName":
                    holderName = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "phoneNumber":
                    phoneNumber = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "model":
                    model = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "photo":
                    photo = part.getInputStream();
                    photoFileName = part.getSubmittedFileName();
                    break;
                case "email":
                    email = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "type":
                    type = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
            }
        }
        if (userName == null
                || currency == null
                || holderName == null
                || phoneNumber == null
                || model == null) {
            resp.sendError(500, "wrong number of params");
        }
        if (type == null) {
            type = "MONEYCLICK";
        }
        String id = BaseOperation.getId();
        File debitCardFolder = DebitCardsFolderLocator.getFolder(id);
        String number = "XXXXXXXXXXXXXXXX";
        if (type.equals("MONEYCLICK")) {
            while (true) {
                number = "6123"
                        + String.format("%04d", new Random().nextInt(10000))
                        + String.format("%04d", new Random().nextInt(10000))
                        + String.format("%04d", new Random().nextInt(10000));
                if (!new File(new File(new File(new File(OPERATOR_PATH, "DebitCards"), "Indexes"), "Numbers"), number).isDirectory()) {
                    break;
                }
            }
        }
        FileUtil.createFolderIfNoExist(debitCardFolder);
        if (photo != null && photoFileName != null) {
            String imageFileNameExtention;
            if (photoFileName.contains(".")) {
                imageFileNameExtention = photoFileName.substring(photoFileName.lastIndexOf(".") + 1);
            } else {
                imageFileNameExtention = "jpeg";
            }
            photoFileName = "photo" + "." + imageFileNameExtention;
            FileUtil.writeInFile(new File(DebitCardsFolderLocator.getFolder(id), photoFileName), photo);
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode debitCardConfig = mapper.createObjectNode();
        String timestamp = DateUtil.getCurrentDate();
        debitCardConfig.put("id", id);
        if (number != null) {
            debitCardConfig.put("number", number);
        }
        debitCardConfig.put("timestamp", timestamp);
        debitCardConfig.put("userName", userName);
        debitCardConfig.put("currency", currency);
        debitCardConfig.put("holderName", holderName);
        debitCardConfig.put("phoneNumber", phoneNumber);
        debitCardConfig.put("model", model);
        debitCardConfig.put("email", email);
        debitCardConfig.put("debitCardStatus", DebitCardStatus.WAITING_TO_PRINT.name());
        debitCardConfig.put("type", type);
        FileUtil.createFile(debitCardConfig, DebitCardsFolderLocator.getConfigFile(id));
        ObjectNode debitCardIndex = mapper.createObjectNode();
        debitCardIndex.put("id", id);
        debitCardIndex.put("timestamp", timestamp);
        File debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Ids", id);
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        if (number != null) {
            debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Numbers", number);
            FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        }
        debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("UserNames", userName);
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Currencies", currency);
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("HolderNames", holderName);
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("PhoneNumbers", phoneNumber);
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Models", model);
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Timestamps", timestamp);
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Statuses", DebitCardStatus.WAITING_TO_PRINT.name());
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
        debitCardIndexFolder = DebitCardsFolderLocator.getIndexesSpecificValueFolder("Types", type);
        FileUtil.createFile(debitCardIndex, new File(debitCardIndexFolder, id + ".json"));
    }

}
