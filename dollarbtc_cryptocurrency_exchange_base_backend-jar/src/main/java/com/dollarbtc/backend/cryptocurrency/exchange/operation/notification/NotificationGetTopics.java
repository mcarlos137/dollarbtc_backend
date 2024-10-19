/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class NotificationGetTopics extends AbstractOperation<ArrayNode> {

    public NotificationGetTopics() {
        super(ArrayNode.class);
    }

    @Override
    protected void execute() {
        ArrayNode topics = mapper.createArrayNode();
        for(File notificationTopicFile : NotificationsFolderLocator.getTopicsFolder().listFiles()){
            if(!notificationTopicFile.isFile()){
                continue;
            }
            try {
                topics.add(mapper.readTree(notificationTopicFile));
            } catch (IOException ex) {
                Logger.getLogger(NotificationGetTopics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = topics;
    }

}
