/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service.broadcasting;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosDaniel
 */
@XmlRootElement
public class BroadcastingListRequest implements Serializable, Cloneable {
    
    private String initTimestamp, finalTimestamp;
    private String[] userNames, types, titles, ratings, positions, tags, statuses, subscriptorsNumbers, subscriptionPrices;

    public String getInitTimestamp() {
        return initTimestamp;
    }

    public void setInitTimestamp(String initTimestamp) {
        this.initTimestamp = initTimestamp;
    }

    public String getFinalTimestamp() {
        return finalTimestamp;
    }

    public void setFinalTimestamp(String finalTimestamp) {
        this.finalTimestamp = finalTimestamp;
    }

    public String[] getUserNames() {
        return userNames;
    }

    public void setUserNames(String[] userNames) {
        this.userNames = userNames;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public String[] getRatings() {
        return ratings;
    }

    public void setRatings(String[] ratings) {
        this.ratings = ratings;
    }

    public String[] getPositions() {
        return positions;
    }

    public void setPositions(String[] positions) {
        this.positions = positions;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getStatuses() {
        return statuses;
    }

    public void setStatuses(String[] statuses) {
        this.statuses = statuses;
    }

    public String[] getSubscriptorsNumbers() {
        return subscriptorsNumbers;
    }

    public void setSubscriptorsNumbers(String[] subscriptorsNumbers) {
        this.subscriptorsNumbers = subscriptorsNumbers;
    }

    public String[] getSubscriptionPrices() {
        return subscriptionPrices;
    }

    public void setSubscriptionPrices(String[] subscriptionPrices) {
        this.subscriptionPrices = subscriptionPrices;
    }
        
}