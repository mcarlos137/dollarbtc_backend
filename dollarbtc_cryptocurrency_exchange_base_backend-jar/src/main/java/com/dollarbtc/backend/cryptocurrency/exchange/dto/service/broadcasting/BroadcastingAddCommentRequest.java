/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author CarlosDaniel
 */
public class BroadcastingAddCommentRequest {
        
    private String id, episodeTrailerId, userName, name, comment, replyId;
    private boolean privateComment;

    public BroadcastingAddCommentRequest() {
    }

    public BroadcastingAddCommentRequest(String id, String episodeTrailerId, String userName, String name, String comment, String replyId, boolean privateComment) {
        this.id = id;
        this.episodeTrailerId = episodeTrailerId;
        this.userName = userName;
        this.name = name;
        this.comment = comment;
        this.replyId = replyId;
        this.privateComment = privateComment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEpisodeTrailerId() {
        return episodeTrailerId;
    }

    public void setEpisodeTrailerId(String episodeTrailerId) {
        this.episodeTrailerId = episodeTrailerId;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }
    
    public boolean isPrivateComment() {
        return privateComment;
    }

    public void setPrivateComment(boolean privateComment) {
        this.privateComment = privateComment;
    }
                
    public JsonNode toJsonNode() {
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ((ObjectNode) jsonNode).put("userName", this.userName);
        ((ObjectNode) jsonNode).put("name", this.name);
        ((ObjectNode) jsonNode).put("comment", this.comment);
        ((ObjectNode) jsonNode).put("private", this.privateComment);
        if(this.replyId != null){
            ((ObjectNode) jsonNode).put("replyId", this.replyId);
        }
        ((ObjectNode) jsonNode).set("reactions", new ObjectMapper().createObjectNode());
        return jsonNode;
    }
                       
}
