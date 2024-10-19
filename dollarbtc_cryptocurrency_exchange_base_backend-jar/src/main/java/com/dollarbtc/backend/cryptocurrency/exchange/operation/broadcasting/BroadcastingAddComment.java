/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.broadcasting;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting.BroadcastingAddCommentRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BroadcastingFolderLocator;
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
public class BroadcastingAddComment extends AbstractOperation<String> {

    private final BroadcastingAddCommentRequest broadcastingAddCommentRequest;

    public BroadcastingAddComment(BroadcastingAddCommentRequest broadcastingAddCommentRequest) {
        super(String.class);
        this.broadcastingAddCommentRequest = broadcastingAddCommentRequest;
    }

    @Override
    public void execute() {
        try {
            File broadcastingEpisodeTrailerCommentsFile = BroadcastingFolderLocator.getCommentsFile(broadcastingAddCommentRequest.getId(), broadcastingAddCommentRequest.getEpisodeTrailerId(), 0);
            ArrayNode broadcastingEpisodeTrailerComments = mapper.createArrayNode();
            if (broadcastingEpisodeTrailerCommentsFile.isFile()) {
                broadcastingEpisodeTrailerComments = (ArrayNode) mapper.readTree(broadcastingEpisodeTrailerCommentsFile);
            }
            String timestamp = DateUtil.getCurrentDate();
            JsonNode broadcastingEpisodeTrailerComment = broadcastingAddCommentRequest.toJsonNode();
            ((ObjectNode) broadcastingEpisodeTrailerComment).put("id", BaseOperation.getId());
            ((ObjectNode) broadcastingEpisodeTrailerComment).put("timestamp", timestamp);
            broadcastingEpisodeTrailerComments.add(broadcastingEpisodeTrailerComment);
            /*if(broadcastingAddCommentRequest.getReplyId() == null){
                broadcastingEpisodeTrailerComments.add(broadcastingEpisodeTrailerComment);
            } else {
                Iterator<JsonNode> broadcastingEpisodeTrailerCommentsIterator = broadcastingEpisodeTrailerComments.iterator();
                while (broadcastingEpisodeTrailerCommentsIterator.hasNext()) {
                    JsonNode broadcastingEpisodeTrailerCommentsIt = broadcastingEpisodeTrailerCommentsIterator.next();
                    if(broadcastingEpisodeTrailerCommentsIt.get("id").textValue().equals(broadcastingAddCommentRequest.getReplyId())){
                        if(!broadcastingEpisodeTrailerCommentsIt.has("replies")){
                            ((ObjectNode) broadcastingEpisodeTrailerCommentsIt).putArray("replies").add(broadcastingEpisodeTrailerComment);
                        } else {
                            ((ArrayNode) broadcastingEpisodeTrailerCommentsIt.get("replies")).add(broadcastingEpisodeTrailerComment);
                        }
                        break;
                    }
                }
            }*/
            FileUtil.editFile(broadcastingEpisodeTrailerComments, broadcastingEpisodeTrailerCommentsFile);
            File broadcastingFile = BroadcastingFolderLocator.getFile(broadcastingAddCommentRequest.getId());
            JsonNode broadcasting = mapper.readTree(broadcastingFile);
            ((ObjectNode) broadcasting).put("commentsCount", broadcasting.get("commentsCount").intValue() + 1);
            FileUtil.editFile(broadcasting, broadcastingFile);
        } catch (IOException ex) {
            Logger.getLogger(BroadcastingAddComment.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
