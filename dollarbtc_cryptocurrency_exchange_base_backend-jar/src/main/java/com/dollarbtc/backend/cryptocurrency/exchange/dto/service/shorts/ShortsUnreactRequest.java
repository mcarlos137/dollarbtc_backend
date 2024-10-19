/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts;

/**
 *
 * @author CarlosDaniel
 */
public class ShortsUnreactRequest {
        
    private String id, userName, reaction, commentId;

    public ShortsUnreactRequest() {
    }

    public ShortsUnreactRequest(String id, String userName, String reaction, String commentId) {
        this.id = id;
        this.userName = userName;
        this.reaction = reaction;
        this.commentId = commentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
