/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.LiveStreamingType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming.LiveStreamingCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
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
public class LiveStreamingCreateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String userName = null;
        String name = null;
        String title = null;
        String description = null;
        String type = null;
        InputStream image = null;
        String imageFileName = null;
        String publishTimestamp = null;
        Double ppvPrice = null;
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
                case "type":
                    type = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "image":
                    image = part.getInputStream();
                    imageFileName = part.getSubmittedFileName();
                    break;
                case "publishTimestamp":
                    publishTimestamp = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "ppvPrice":
                    ppvPrice = Double.parseDouble(IOUtils.toString(part.getInputStream(), "utf8"));
                    break;
            }
        }
        if (userName == null || name == null || title == null || description == null || type == null || image == null) {
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
            FileUtil.writeInFile(new File(LiveStreamingsFolderLocator.getAttachmentsFolder(), imageFileName), image);
        }
        String response = new LiveStreamingCreate(
                new LiveStreamingCreateRequest(
                        userName,
                        name,
                        title,
                        description,
                        imageFileName,
                        publishTimestamp,
                        ppvPrice,
                        LiveStreamingType.valueOf(type)
                )
        ).getResponse();
        if (!response.equals("OK")) {
            resp.sendError(500, response);
        }
    }

}
