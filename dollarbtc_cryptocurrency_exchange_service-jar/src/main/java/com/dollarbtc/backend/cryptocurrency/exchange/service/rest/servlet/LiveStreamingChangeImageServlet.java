/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.service.rest.servlet;

import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
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
public class LiveStreamingChangeImageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String id = null;
        String publicationId = null;
        InputStream image = null;
        String imageFileName = null;
        for (Part part : req.getParts()) {
            switch (part.getName()) {
                case "id":
                    id = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "publicationId":
                    publicationId = IOUtils.toString(part.getInputStream(), "utf8");
                    break;
                case "image":
                    image = part.getInputStream();
                    imageFileName = part.getSubmittedFileName();
                    break;
            }
        }
        if (id == null || image == null || imageFileName == null) {
            resp.sendError(500, "wrong number of params");
            return;
        }
        String imageFileNameExtention;
        if (imageFileName.contains(".")) {
            imageFileNameExtention = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
        } else {
            imageFileNameExtention = "jpeg";
        }
        imageFileName = id + "__" + new Date().getTime() + "." + imageFileNameExtention;
        FileUtil.writeInFile(new File(LiveStreamingsFolderLocator.getAttachmentsFolder(), imageFileName), image);
        File liveStreamingFile = LiveStreamingsFolderLocator.getFile(id);
        JsonNode liveStreaming = new ObjectMapper().readTree(liveStreamingFile);
        if (publicationId == null) {
            ((ObjectNode) liveStreaming).put("imageFileName", imageFileName);
        } else {
            Iterator<JsonNode> liveStreamingPublicationsIterator = ((JsonNode) liveStreaming.get("publications")).iterator();
            while (liveStreamingPublicationsIterator.hasNext()) {
                JsonNode liveStreamingPublicationsIt = liveStreamingPublicationsIterator.next();
                if (liveStreamingPublicationsIt.get("id").textValue().equals(publicationId)) {
                    ((ObjectNode) liveStreamingPublicationsIt).put("imageFileName", imageFileName);
                    break;
                }
            }
        }
        FileUtil.editFile(liveStreaming, liveStreamingFile);
    }

}
