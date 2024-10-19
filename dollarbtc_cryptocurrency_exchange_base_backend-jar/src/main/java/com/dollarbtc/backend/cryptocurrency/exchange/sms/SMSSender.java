/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.sms;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.AddressConfiguration;
import com.amazonaws.services.pinpoint.model.ChannelType;
import com.amazonaws.services.pinpoint.model.DirectMessageConfiguration;
import com.amazonaws.services.pinpoint.model.MessageRequest;
import com.amazonaws.services.pinpoint.model.SMSMessage;
import com.amazonaws.services.pinpoint.model.SendMessagesRequest;
import com.amazonaws.services.pinpoint.model.SendOTPMessageRequest;
import com.amazonaws.services.pinpoint.model.SendOTPMessageRequestParameters;

import com.dollarbtc.backend.cryptocurrency.exchange.smsto.SMSToSendMessage;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BaseFilesLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo torres
 */
public class SMSSender {

    private final AmazonSNSClient snsClient;
    private final Map<String, MessageAttributeValue> smsAttributes;
    
    private final AmazonPinpoint pinpointClient;

    public SMSSender() {
        this.snsClient = new AmazonSNSClient(new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "AKIA6PQX433E3F6GXXXZ";
            }

            @Override
            public String getAWSSecretKey() {
                return "J2b+rwxZep1KP0MRd5UxBVSgAxnmr+R/3UWqIvKU";
            }
        });
        smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("dollarBTC") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.5") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Transactional") //Sets the type to promotional.
                .withDataType("String"));
        String region = "us-east-1";
        this.pinpointClient = AmazonPinpointClientBuilder.standard()
                .withRegion(region).build();
    }
    
    /*
    public SMSSender(String source) {
        String region = "us-east-1";
        String originationNumber = "+12065550199";
        String destinationNumber = "+14255550142";
        String message = "This message was sent through Amazon Pinpoint "
            + "using the AWS SDK for Java. Reply STOP to "
            + "opt out.";
        String appId = "ce796be37f32f178af652b26eexample";
        String messageType = "TRANSACTIONAL";
        String registeredKeyword = "myKeyword";
        Map<String,AddressConfiguration> addressMap = new HashMap<>();       
        addressMap.put(destinationNumber, new AddressConfiguration()
                    .withChannelType(ChannelType.SMS));               
    }*/

    public void publishOTP(String destinationNumber, String referenceId) {
        if (isPhoneNumberInBlackListSMSSend(destinationNumber)) {
            Logger.getLogger(SMSSender.class.getName()).log(Level.INFO, "PHONE NUMBER {0} IS IN BLACK LIST", destinationNumber);
            return;
        }
        if (destinationNumber.startsWith("58424") || destinationNumber.startsWith("58414")) {
            //SMSTO
            //JsonNode response = new SMSToSendMessage(phoneNumber, message).getResponse();
            //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>SMS TO response " + response);
        } else {            
            String originationNumber = "+12065550199";
            //String destinationNumber = "+14255550142";
            String appId = "ce796be37f32f178af652b26eexample";
            SendOTPMessageRequest request = new SendOTPMessageRequest()
                .withApplicationId(appId)
                    .withSendOTPMessageRequestParameters(new SendOTPMessageRequestParameters()
                        .withBrandName("moneyclick")
                        .withChannel("SMS")
                        .withCodeLength(6)
                        .withValidityPeriod(20)
                        .withAllowedAttempts(5)
                        .withOriginationIdentity("+" + originationNumber)
                        .withDestinationIdentity("+" + destinationNumber)
                        .withReferenceId(referenceId)
                    )
                .withRequestCredentialsProvider(new AWSCredentialsProvider() {
                    @Override
                    public AWSCredentials getCredentials() {
                        return new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                return "AKIA6PQX433E3F6GXXXZ";
                            }
                            @Override
                            public String getAWSSecretKey() {
                                return "J2b+rwxZep1KP0MRd5UxBVSgAxnmr+R/3UWqIvKU";
                            }
                        };
                    }
                    @Override
                    public void refresh() {
                        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                    }
            });
            this.pinpointClient.sendOTPMessage(request);
            //String appId = "ce796be37f32f178af652b26eexample";
            //String originationNumber = "+12065550199";
            //String destinationNumber = "+14255550142";
            //String messageType = "TRANSACTIONAL";
            //Map<String,AddressConfiguration> addressMap = new HashMap<>();       
            //addressMap.put(destinationNumber, new AddressConfiguration()
            //    .withChannelType(ChannelType.SMS));      
            /*SendMessagesRequest request = new SendMessagesRequest()
                .withApplicationId(appId)
                .withMessageRequest(new MessageRequest()
                .withAddresses(addressMap)                                   
                .withMessageConfiguration(new DirectMessageConfiguration()
                    .withSMSMessage(new SMSMessage()
                        .withBody(message)
                        .withMessageType(messageType)
                        .withOriginationNumber(originationNumber)
                        .withSenderId(senderId)
                        .withKeyword(registeredKeyword)
                    )
                )
            );*/
            
            //AWS
            /*try {
                PublishResult result = snsClient.publish(new PublishRequest()
                        .withMessage(message)
                        .withPhoneNumber(phoneNumber)
                        .withMessageAttributes(smsAttributes));
                Logger.getLogger(SMSSender.class.getName()).log(Level.INFO, "SENDING MESSAGE TO " + phoneNumber + " " + message);
                Logger.getLogger(SMSSender.class.getName()).log(Level.INFO, "" + result);
            } catch (com.amazonaws.services.sns.model.InvalidParameterException ex) {
                Logger.getLogger(SMSSender.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }
    }
    
    public void publish(String message, String[] phoneNumbers) {
        int i = 0;
        for (String phoneNumber : phoneNumbers) {
            i++;
            if (i > 100) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SMSSender.class.getName()).log(Level.SEVERE, null, ex);
                }
                i = 0;
            }
            if (isPhoneNumberInBlackListSMSSend(phoneNumber)) {
                Logger.getLogger(SMSSender.class.getName()).log(Level.INFO, "PHONE NUMBER " + phoneNumber + " IS IN BLACK LIST");
                continue;
            }
            if (phoneNumber.startsWith("58424") || phoneNumber.startsWith("58414")) {
                //SMSTO
                JsonNode response = new SMSToSendMessage(phoneNumber, message).getResponse();
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>SMS TO response " + response);
            } else {
                //AWS
                try {
                    PublishResult result = snsClient.publish(new PublishRequest()
                            .withMessage(message)
                            .withPhoneNumber(phoneNumber)
                            .withMessageAttributes(smsAttributes));
                    Logger.getLogger(SMSSender.class.getName()).log(Level.INFO, "SENDING MESSAGE TO " + phoneNumber + " " + message);
                    Logger.getLogger(SMSSender.class.getName()).log(Level.INFO, "" + result);
                } catch (com.amazonaws.services.sns.model.InvalidParameterException ex) {
                    Logger.getLogger(SMSSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static boolean isPhoneNumberInBlackListSMSSend(String phoneNumber) {
        try {
            JsonNode blackList = new ObjectMapper().readTree(BaseFilesLocator.getBlackListFile());
            if (blackList.has("SMS_SEND")) {
                Iterator<JsonNode> blackListSMSSendIterator = blackList.get("SMS_SEND").iterator();
                while (blackListSMSSendIterator.hasNext()) {
                    JsonNode blackListSMSSendIt = blackListSMSSendIterator.next();
                    if (blackListSMSSendIt.textValue().equals(phoneNumber)) {
                        return true;
                    }
                    if (blackListSMSSendIt.textValue().contains("+") && phoneNumber.replace("+", "").startsWith(blackListSMSSendIt.textValue().replace("+", ""))) {
                        return true;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SMSSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
