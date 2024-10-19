/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
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
import java.util.Date;
import java.util.Iterator;

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 50)
@WebServlet()
public class BroadcastingChangeImageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String broadcastingId = null;
        String episoseTrailerId = null;
        InputStream image = null;
        String imageFileName = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "broadcastingId":
                    broadcastingId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "episoseTrailerId":
                    episoseTrailerId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "image":
                    image = part.getInputStream();
                    imageFileName = part.getSubmittedFileName();
                    break;
            }
        }
        if (broadcastingId == null || image == null || imageFileName == null) {
            resp.sendError(500, "wrong number of params");
            return;
        }
        String imageFileNameExtention;
        if (imageFileName.contains(".")) {
            imageFileNameExtention = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
        } else {
            imageFileNameExtention = "jpeg";
        }
        imageFileName = broadcastingId + "__" + new Date().getTime() + "." + imageFileNameExtention;
        FileUtil.writeInFile(new File(BroadcastingFolderLocator.getAttachmentsFolder(), imageFileName), image);
        File broadcastingFile = BroadcastingFolderLocator.getFile(broadcastingId);
        JsonNode broadcasting = new ObjectMapper().readTree(broadcastingFile);
        if (episoseTrailerId == null) {
            ((ObjectNode) broadcasting).put("imageFileName", imageFileName);
        } else {
            boolean stop = false;
            Iterator<JsonNode> broadcastingEpisodesIterator = ((JsonNode) broadcasting.get("episodes")).iterator();
            while (broadcastingEpisodesIterator.hasNext()) {
                JsonNode broadcastingEpisodesIt = broadcastingEpisodesIterator.next();
                if (broadcastingEpisodesIt.get("id").textValue().equals(episoseTrailerId)) {
                    ((ObjectNode) broadcastingEpisodesIt).put("imageFileName", imageFileName);
                    stop = true;
                    break;
                }
            }
            if (!stop) {
                Iterator<JsonNode> broadcastingTrailersIterator = ((JsonNode) broadcasting.get("trailers")).iterator();
                while (broadcastingTrailersIterator.hasNext()) {
                    JsonNode broadcastingTrailersIt = broadcastingTrailersIterator.next();
                    if (broadcastingTrailersIt.get("id").textValue().equals(episoseTrailerId)) {
                        ((ObjectNode) broadcastingTrailersIt).put("imageFileName", imageFileName);
                        break;
                    }
                }
            }
        }
        FileUtil.editFile(broadcasting, broadcastingFile);
    }

}
