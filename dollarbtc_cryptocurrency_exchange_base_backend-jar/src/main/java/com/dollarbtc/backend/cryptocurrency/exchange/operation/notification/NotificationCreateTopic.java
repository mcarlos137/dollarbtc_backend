/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.notification;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.notification.NotificationCreateTopicRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractFirebaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.NotificationsFolderLocator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class NotificationCreateTopic extends AbstractFirebaseOperation<String> {

    private final NotificationCreateTopicRequest notificationCreateTopicRequest;

    public NotificationCreateTopic(NotificationCreateTopicRequest notificationCreateTopicRequest) {
        super(String.class, false);
        this.notificationCreateTopicRequest = notificationCreateTopicRequest;
    }

    @Override
    protected void execute() {
        // These registration tokens come from the client FCM SDKs.
        List<String> registrationTokens = Arrays.asList(
                "f8V8HmDNR3SNKsWtgAWPSU:APA91bHPvDUIjV6xe3IBK53iMtKCVRR5LBNfEaSFAka1G1MqaZLgzGkW44ZhWdVAzz1nSYbgK7mqhwkPqYnXbS4bmtzxbFaznKb3Lb1d7_PXL5Bi3l4hoiXN2XgWuaGsj70LO2_niqQY"
        );
        try {
            String serviceName = notificationCreateTopicRequest.getName().replace(" ", "_").toUpperCase(); 
            TopicManagementResponse resp = FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, serviceName);
            Logger.getLogger(NotificationCreateTopic.class.getName()).log(Level.INFO, "--------------------------------------------------");
            for(TopicManagementResponse.Error error : resp.getErrors()){
                Logger.getLogger(NotificationCreateTopic.class.getName()).log(Level.INFO, "" + error.getReason());
            }            
            Logger.getLogger(NotificationCreateTopic.class.getName()).log(Level.INFO, "" + resp.getFailureCount());
            Logger.getLogger(NotificationCreateTopic.class.getName()).log(Level.INFO, "{0} tokens were subscribed successfully", resp.getSuccessCount());
            Logger.getLogger(NotificationCreateTopic.class.getName()).log(Level.INFO, "--------------------------------------------------");
            if (resp.getSuccessCount() == 0) {
                super.response = "FAIL FROM FIREBASE TO CREATE TOPIC";
                return;
            }
            String id = BaseOperation.getId();
            ObjectNode topic = mapper.createObjectNode();
            topic.put("id", id);
            topic.put("name", notificationCreateTopicRequest.getName());
            topic.put("serviceName", serviceName);
            topic.put("operatorUserName", notificationCreateTopicRequest.getOperatorUserName());
            topic.put("timestamp", DateUtil.getCurrentDate());
            FileUtil.createFile(topic, new File(NotificationsFolderLocator.getTopicsFolder(), id + ".json"));
            super.response = "OK";
            return;
        } catch (FirebaseMessagingException ex) {
            Logger.getLogger(NotificationCreateTopic.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
