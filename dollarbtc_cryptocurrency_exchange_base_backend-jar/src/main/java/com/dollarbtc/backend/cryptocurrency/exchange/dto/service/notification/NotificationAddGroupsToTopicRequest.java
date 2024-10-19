/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification;

import java.util.Set;


/**
 *
 * @author carlosmolina
 */
public class NotificationAddGroupsToTopicRequest {
    
    private String topicId;
    private Set<String> groups;
    
    public NotificationAddGroupsToTopicRequest() {
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }
    
}
