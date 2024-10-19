/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsAddCommentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ShortsAddComment extends AbstractOperation<String> {

    private final ShortsAddCommentRequest shortsAddCommentRequest;

    public ShortsAddComment(ShortsAddCommentRequest shortsAddCommentRequest) {
        super(String.class);
        this.shortsAddCommentRequest = shortsAddCommentRequest;
    }

    @Override
    public void execute() {
        try {
            File shortsCommentsFile = ShortsFolderLocator.getCommentsFile(shortsAddCommentRequest.getId(), 0);
            ArrayNode shortsComments = mapper.createArrayNode();
            if (shortsCommentsFile.isFile()) {
                shortsComments = (ArrayNode) mapper.readTree(shortsCommentsFile);
            }
            String timestamp = DateUtil.getCurrentDate();
            JsonNode shortsComment = shortsAddCommentRequest.toJsonNode();
            ((ObjectNode) shortsComment).put("id", BaseOperation.getId());
            ((ObjectNode) shortsComment).put("timestamp", timestamp);
            shortsComments.add(shortsComment);
            /*if(shortsAddCommentRequest.getReplyId() == null){
                shortsComments.add(shortsComment);
            } else {
                Iterator<JsonNode> shortsCommentsIterator = shortsComments.iterator();
                while (shortsCommentsIterator.hasNext()) {
                    JsonNode shortsCommentsIt = shortsCommentsIterator.next();
                    if(shortsCommentsIt.get("id").textValue().equals(shortsAddCommentRequest.getReplyId())){
                        if(!shortsCommentsIt.has("replies")){
                            ((ObjectNode) shortsCommentsIt).putArray("replies").add(shortsComment);
                        } else {
                            ((ArrayNode) shortsCommentsIt.get("replies")).add(shortsComment);
                        }
                        break;
                    }
                }
            }*/
            FileUtil.editFile(shortsComments, shortsCommentsFile);
            File shortsFile = ShortsFolderLocator.getFile(shortsAddCommentRequest.getId());
            JsonNode shorts = mapper.readTree(shortsFile);
            ((ObjectNode) shorts).put("commentsCount", shorts.get("commentsCount").intValue() + 1);
            FileUtil.editFile(shorts, shortsFile);
        } catch (IOException ex) {
            Logger.getLogger(ShortsAddComment.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
