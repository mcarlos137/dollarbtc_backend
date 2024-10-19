/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.user;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.subscription.SubscriptionListRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.SubscriptionType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.subscription.SubscriptionList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @author carlosmolina
 */
public class UserGetRelatedConfig extends AbstractOperation<JsonNode> {

    private final String userName, relatedUserName;

    public UserGetRelatedConfig(String userName, String relatedUserName) {
        super(JsonNode.class);
        this.userName = userName;
        this.relatedUserName = relatedUserName;
    }

    @Override
    protected void execute() {
        JsonNode userConfig = new UserGetConfig(userName, "OK").getResponse();
        boolean subscribed = false;
        ArrayNode subscriptionList = new SubscriptionList(
                new SubscriptionListRequest(
                        userName,
                        relatedUserName,
                        SubscriptionType.NORMAL,
                        SubscriptionStatus.ACTIVE
                )
        ).getResponse();
        if (subscriptionList.size() == 1) {
            subscribed = true;
        }
        ((ObjectNode) userConfig).put("subscribedNormal", subscribed);
        subscribed = false;
        subscriptionList = new SubscriptionList(
                new SubscriptionListRequest(
                        userName,
                        relatedUserName,
                        SubscriptionType.PREMIUM,
                        SubscriptionStatus.ACTIVE
                )
        ).getResponse();
        if (subscriptionList.size() == 1) {
            subscribed = true;
        }
        ((ObjectNode) userConfig).put("subscribedPremium", subscribed);
        boolean notification = false;
        subscriptionList = new SubscriptionList(
                new SubscriptionListRequest(
                        userName,
                        relatedUserName,
                        SubscriptionType.NOTIFICATION,
                        SubscriptionStatus.ACTIVE
                )
        ).getResponse();
        if (subscriptionList.size() == 1) {
            notification = true;
        }
        ((ObjectNode) userConfig).put("notification", notification);
        int subscribersCount = 0;
        subscriptionList = new SubscriptionList(
                new SubscriptionListRequest(
                        userName,
                        SubscriptionType.NORMAL,
                        SubscriptionStatus.ACTIVE
                )
        ).getResponse();
        subscribersCount = subscribersCount + subscriptionList.size();
        ((ObjectNode) userConfig).put("subscribersNormalCount", subscribersCount);
        subscribersCount = 0;
        subscriptionList = new SubscriptionList(
                new SubscriptionListRequest(
                        userName,
                        SubscriptionType.PREMIUM,
                        SubscriptionStatus.ACTIVE
                )
        ).getResponse();
        subscribersCount = subscribersCount + subscriptionList.size();
        ((ObjectNode) userConfig).put("subscribersPremiumCount", subscribersCount);
        super.response = userConfig;
    }

}
