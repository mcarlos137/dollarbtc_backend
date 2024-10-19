/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts.ShortsCreate;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
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
public class ShortsCreateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String userName = null;
        String name = null;
        String title = null;
        String description = null;
        InputStream video = null;
        String videoFileName = null;
        String publishTimestamp = null;
        String assetId = null;
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
                case "video":
                    video = part.getInputStream();
                    videoFileName = part.getSubmittedFileName();
                    break;
                case "publishTimestamp":
                    publishTimestamp = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "assetId":
                    assetId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
            }
        }
        if (userName == null || name == null || title == null || description == null || video == null) {
            resp.sendError(500, "wrong number of params");
        }
        if (video != null && videoFileName != null) {
            String videoFileNameExtention;
            if (videoFileName.contains(".")) {
                videoFileNameExtention = videoFileName.substring(videoFileName.lastIndexOf(".") + 1);
            } else {
                videoFileNameExtention = "mp4";
            }
            videoFileName = userName + "__" + new Date().getTime() + "." + videoFileNameExtention;
            FileUtil.writeInFile(new File(ShortsFolderLocator.getAttachmentsFolder(), videoFileName), video);
        }
        String response = new ShortsCreate(
                new ShortsCreateRequest(
                        userName,
                        name,
                        title,
                        description,
                        videoFileName,
                        publishTimestamp,
                        assetId
                )
        ).getResponse();
        if (!response.equals("OK")) {
            resp.sendError(500, response);
        }
    }
}
