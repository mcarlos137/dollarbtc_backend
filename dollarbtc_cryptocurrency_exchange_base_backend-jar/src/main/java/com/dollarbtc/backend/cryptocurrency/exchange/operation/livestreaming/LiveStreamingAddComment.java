/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.livestreaming;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.livestreaming.LiveStreamingAddCommentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.LiveStreamingsFolderLocator;
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
public class LiveStreamingAddComment extends AbstractOperation<String> {

    private final LiveStreamingAddCommentRequest liveStreamingAddCommentRequest;

    public LiveStreamingAddComment(LiveStreamingAddCommentRequest liveStreamingAddCommentRequest) {
        super(String.class);
        this.liveStreamingAddCommentRequest = liveStreamingAddCommentRequest;
    }

    @Override
    public void execute() {
        try {
            File liveStreamingCommentsFile = LiveStreamingsFolderLocator.getCommentsFile(liveStreamingAddCommentRequest.getId(), liveStreamingAddCommentRequest.getPublicationId(), 0);
            ArrayNode liveStreamingComments = mapper.createArrayNode();
            if (liveStreamingCommentsFile.isFile()) {
                liveStreamingComments = (ArrayNode) mapper.readTree(liveStreamingCommentsFile);
            }
            String timestamp = DateUtil.getCurrentDate();
            JsonNode liveStreamingComment = liveStreamingAddCommentRequest.toJsonNode();
            ((ObjectNode) liveStreamingComment).put("id", BaseOperation.getId());
            ((ObjectNode) liveStreamingComment).put("timestamp", timestamp);
            if(liveStreamingAddCommentRequest.getReplyId() == null){
                liveStreamingComments.add(liveStreamingComment);
            } else {
                Iterator<JsonNode> liveStreamingCommentsIterator = liveStreamingComment.iterator();
                while (liveStreamingCommentsIterator.hasNext()) {
                    JsonNode liveStreamingCommentsIt = liveStreamingCommentsIterator.next();
                    if(liveStreamingCommentsIt.get("id").textValue().equals(liveStreamingAddCommentRequest.getReplyId())){
                        if(!liveStreamingCommentsIt.has("replies")){
                            ((ObjectNode) liveStreamingCommentsIt).putArray("replies").add(liveStreamingComment);
                        } else {
                            ((ArrayNode) liveStreamingCommentsIt.get("replies")).add(liveStreamingComment);
                        }
                        break;
                    }
                }
            }
            FileUtil.editFile(liveStreamingComments, liveStreamingCommentsFile);
            File liveStreamingFile = LiveStreamingsFolderLocator.getFile(liveStreamingAddCommentRequest.getId());
            JsonNode liveStreaming = mapper.readTree(liveStreamingFile);
            ((ObjectNode) liveStreaming).put("commentsCount", liveStreaming.get("commentsCount").intValue() + 1);
            FileUtil.editFile(liveStreaming, liveStreamingFile);
        } catch (IOException ex) {
            Logger.getLogger(LiveStreamingAddComment.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
