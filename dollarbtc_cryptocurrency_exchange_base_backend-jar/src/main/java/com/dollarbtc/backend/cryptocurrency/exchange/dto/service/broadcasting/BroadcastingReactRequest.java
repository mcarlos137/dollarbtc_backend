/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting;

/**
 *
 * @author CarlosDaniel
 */
public class BroadcastingReactRequest {
        
    private String id, episodeTrailerId, userName, name, reaction, commentId;

    public BroadcastingReactRequest() {
    }

    public BroadcastingReactRequest(String id, String episodeTrailerId, String userName, String name, String reaction, String commentId) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.reaction = reaction;
        this.commentId = commentId;
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
    
    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
                               
}
