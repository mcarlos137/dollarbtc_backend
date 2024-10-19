/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author CarlosDaniel
 */
public class AccountInfo {
    
    private final String username, url, identity_verified_at, trade_volume_text, age_text, confirmed_trade_count_text, created_at;
    private final int feedback_score, feedback_count, real_name_verifications_trusted, trading_partners_count, real_name_verifications_untrusted, trusted_count, feedbacks_unconfirmed_count, blocked_count, real_name_verifications_rejected;
    private final boolean has_feedback, has_common_trades;

    public AccountInfo(JsonNode jsonNode) {
        this.username = jsonNode.get("data").get("username").textValue();
        this.url = jsonNode.get("data").get("url").textValue();
        this.identity_verified_at = jsonNode.get("data").get("identity_verified_at").textValue();
        this.trade_volume_text = jsonNode.get("data").get("trade_volume_text").textValue();
        this.age_text = jsonNode.get("data").get("age_text").textValue();
        this.confirmed_trade_count_text = jsonNode.get("data").get("confirmed_trade_count_text").textValue();
        this.created_at = jsonNode.get("data").get("created_at").textValue();
        this.feedback_score = jsonNode.get("data").get("feedback_score").intValue();
        this.feedback_count = jsonNode.get("data").get("feedback_count").intValue();
        this.real_name_verifications_trusted = jsonNode.get("data").get("real_name_verifications_trusted").intValue();
        this.trading_partners_count = jsonNode.get("data").get("trading_partners_count").intValue();
        this.real_name_verifications_untrusted = jsonNode.get("data").get("real_name_verifications_untrusted").intValue();
        this.trusted_count = jsonNode.get("data").get("trusted_count").intValue();
        this.feedbacks_unconfirmed_count = jsonNode.get("data").get("feedbacks_unconfirmed_count").intValue();
        this.blocked_count = jsonNode.get("data").get("blocked_count").intValue();
        this.real_name_verifications_rejected = jsonNode.get("data").get("real_name_verifications_rejected").intValue();
        this.has_feedback = jsonNode.get("data").get("has_feedback").booleanValue();
        this.has_common_trades = jsonNode.get("data").get("has_common_trades").booleanValue();
    }

    public String getUsername() {
        return username;
    }

    public String getUrl() {
        return url;
    }

    public String getIdentity_verified_at() {
        return identity_verified_at;
    }

    public String getTrade_volume_text() {
        return trade_volume_text;
    }

    public String getAge_text() {
        return age_text;
    }

    public String getConfirmed_trade_count_text() {
        return confirmed_trade_count_text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getFeedback_score() {
        return feedback_score;
    }

    public int getFeedback_count() {
        return feedback_count;
    }

    public int getReal_name_verifications_trusted() {
        return real_name_verifications_trusted;
    }

    public int getTrading_partners_count() {
        return trading_partners_count;
    }

    public int getReal_name_verifications_untrusted() {
        return real_name_verifications_untrusted;
    }

    public int getTrusted_count() {
        return trusted_count;
    }

    public int getFeedbacks_unconfirmed_count() {
        return feedbacks_unconfirmed_count;
    }

    public int getBlocked_count() {
        return blocked_count;
    }

    public int getReal_name_verifications_rejected() {
        return real_name_verifications_rejected;
    }

    public boolean isHas_feedback() {
        return has_feedback;
    }

    public boolean isHas_common_trades() {
        return has_common_trades;
    }

    @Override
    public String toString() {
        return "AccountInfo{" + "username=" + username + ", url=" + url + ", identity_verified_at=" + identity_verified_at + ", trade_volume_text=" + trade_volume_text + ", age_text=" + age_text + ", confirmed_trade_count_text=" + confirmed_trade_count_text + ", created_at=" + created_at + ", feedback_score=" + feedback_score + ", feedback_count=" + feedback_count + ", real_name_verifications_trusted=" + real_name_verifications_trusted + ", trading_partners_count=" + trading_partners_count + ", real_name_verifications_untrusted=" + real_name_verifications_untrusted + ", trusted_count=" + trusted_count + ", feedbacks_unconfirmed_count=" + feedbacks_unconfirmed_count + ", blocked_count=" + blocked_count + ", real_name_verifications_rejected=" + real_name_verifications_rejected + ", has_feedback=" + has_feedback + ", has_common_trades=" + has_common_trades + '}';
    }
        
}
