/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author CarlosDaniel
 */
public class Ads implements Serializable {
    
    private final List<Ad> ads = new ArrayList<>();

    public Ads(JsonNode jsonNode) {
        ArrayNode arrayNode = (ArrayNode) jsonNode.get("data").get("ad_list");
        Iterator<JsonNode> arrayNodeIterator = arrayNode.elements();
        while (arrayNodeIterator.hasNext()) {
            JsonNode arrayNodeIt = arrayNodeIterator.next().get("data");
            ads.add(new Ad(
                    new Profile(
                            arrayNodeIt.get("profile").get("username").textValue(), 
                            arrayNodeIt.get("profile").get("trade_count").textValue(), 
                            arrayNodeIt.get("profile").get("last_online").textValue(), 
                            arrayNodeIt.get("profile").get("name").textValue(), 
                            arrayNodeIt.get("profile").get("feedback_score").intValue()
                    ), 
                    arrayNodeIt.get("trade_type").textValue(),
                    arrayNodeIt.get("temp_price").textValue(), 
                    arrayNodeIt.get("bank_name").textValue(), 
                    arrayNodeIt.get("min_amount").textValue(), 
                    arrayNodeIt.get("temp_price_usd").textValue(), 
                    arrayNodeIt.get("first_time_limit_btc").textValue(), 
                    arrayNodeIt.get("atm_model").textValue(), 
                    arrayNodeIt.get("city").textValue(), 
                    arrayNodeIt.get("location_string").textValue(), 
                    arrayNodeIt.get("countrycode").textValue(), 
                    arrayNodeIt.get("currency").textValue(), 
                    arrayNodeIt.get("limit_to_fiat_amounts").textValue(), 
                    arrayNodeIt.get("created_at").textValue(), 
                    arrayNodeIt.get("max_amount").textValue(), 
                    arrayNodeIt.get("online_provider").textValue(), 
                    arrayNodeIt.get("max_amount_available").textValue(), 
                    arrayNodeIt.get("msg").textValue(), 
                    arrayNodeIt.get("volume_coefficient_btc").textValue(), 
                    arrayNodeIt.get("require_feedback_score").intValue(), 
                    arrayNodeIt.get("ad_id").intValue(), 
                    arrayNodeIt.get("payment_window_minutes").intValue(), 
                    arrayNodeIt.get("hidden_by_opening_hours").booleanValue(), 
                    arrayNodeIt.get("trusted_required").booleanValue(), 
                    arrayNodeIt.get("visible").booleanValue(), 
                    arrayNodeIt.get("require_trusted_by_advertiser").booleanValue(), 
                    arrayNodeIt.get("is_local_office").booleanValue(), 
                    arrayNodeIt.get("is_low_risk").booleanValue(), 
                    arrayNodeIt.get("sms_verification_required").booleanValue(), 
                    arrayNodeIt.get("require_identification").booleanValue(), 
                    arrayNodeIt.get("lat").doubleValue(), 
                    arrayNodeIt.get("lon").doubleValue(), 
                    arrayNodeIt.get("require_trade_volume").doubleValue()
            ));
        }
        System.out.println("jsonNode: " + jsonNode);
    }

    public List<Ad> getAds() {
        return ads;
    }
    
    public static class Ad {

        private final Profile profile;
        private final String trade_type, temp_price, bank_name, min_amount, temp_price_usd, first_time_limit_btc, atm_model, city, location_string, countrycode, currency, limit_to_fiat_amounts, created_at, max_amount, online_provider, max_amount_available, msg, volume_coefficient_btc;
        private final int require_feedback_score, ad_id, payment_window_minutes;
        private final boolean hidden_by_opening_hours, trusted_required, visible, require_trusted_by_advertiser, is_local_office, is_low_risk, sms_verification_required, require_identification;
        private final double lat, lon, require_trade_volume;

        public Ad(Profile profile, String trade_type, String temp_price, String bank_name, String min_amount, String temp_price_usd, String first_time_limit_btc, String atm_model, String city, String location_string, String countrycode, String currency, String limit_to_fiat_amounts, String created_at, String max_amount, String online_provider, String max_amount_available, String msg, String volume_coefficient_btc, int require_feedback_score, int ad_id, int payment_window_minutes, boolean hidden_by_opening_hours, boolean trusted_required, boolean visible, boolean require_trusted_by_advertiser, boolean is_local_office, boolean is_low_risk, boolean sms_verification_required, boolean require_identification, double lat, double lon, double require_trade_volume) {
            this.profile = profile;
            this.trade_type = trade_type;
            this.temp_price = temp_price;
            this.bank_name = bank_name;
            this.min_amount = min_amount;
            this.temp_price_usd = temp_price_usd;
            this.first_time_limit_btc = first_time_limit_btc;
            this.atm_model = atm_model;
            this.city = city;
            this.location_string = location_string;
            this.countrycode = countrycode;
            this.currency = currency;
            this.limit_to_fiat_amounts = limit_to_fiat_amounts;
            this.created_at = created_at;
            this.max_amount = max_amount;
            this.online_provider = online_provider;
            this.max_amount_available = max_amount_available;
            this.msg = msg;
            this.volume_coefficient_btc = volume_coefficient_btc;
            this.require_feedback_score = require_feedback_score;
            this.ad_id = ad_id;
            this.payment_window_minutes = payment_window_minutes;
            this.hidden_by_opening_hours = hidden_by_opening_hours;
            this.trusted_required = trusted_required;
            this.visible = visible;
            this.require_trusted_by_advertiser = require_trusted_by_advertiser;
            this.is_local_office = is_local_office;
            this.is_low_risk = is_low_risk;
            this.sms_verification_required = sms_verification_required;
            this.require_identification = require_identification;
            this.lat = lat;
            this.lon = lon;
            this.require_trade_volume = require_trade_volume;
        }

        public Profile getProfile() {
            return profile;
        }

        public String getTrade_type() {
            return trade_type;
        }

        public String getTemp_price() {
            return temp_price;
        }

        public String getBank_name() {
            return bank_name;
        }

        public String getMin_amount() {
            return min_amount;
        }

        public String getTemp_price_usd() {
            return temp_price_usd;
        }

        public String getFirst_time_limit_btc() {
            return first_time_limit_btc;
        }

        public String getAtm_model() {
            return atm_model;
        }

        public String getCity() {
            return city;
        }

        public String getLocation_string() {
            return location_string;
        }

        public String getCountrycode() {
            return countrycode;
        }

        public String getCurrency() {
            return currency;
        }

        public String getLimit_to_fiat_amounts() {
            return limit_to_fiat_amounts;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getMax_amount() {
            return max_amount;
        }

        public String getOnline_provider() {
            return online_provider;
        }

        public String getMax_amount_available() {
            return max_amount_available;
        }

        public String getMsg() {
            return msg;
        }

        public String getVolume_coefficient_btc() {
            return volume_coefficient_btc;
        }

        public int getRequire_feedback_score() {
            return require_feedback_score;
        }

        public int getAd_id() {
            return ad_id;
        }

        public int getPayment_window_minutes() {
            return payment_window_minutes;
        }

        public boolean isHidden_by_opening_hours() {
            return hidden_by_opening_hours;
        }

        public boolean isTrusted_required() {
            return trusted_required;
        }

        public boolean isVisible() {
            return visible;
        }

        public boolean isRequire_trusted_by_advertiser() {
            return require_trusted_by_advertiser;
        }

        public boolean isIs_local_office() {
            return is_local_office;
        }

        public boolean isIs_low_risk() {
            return is_low_risk;
        }

        public boolean isSms_verification_required() {
            return sms_verification_required;
        }

        public boolean isRequire_identification() {
            return require_identification;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public double getRequire_trade_volume() {
            return require_trade_volume;
        }

        @Override
        public String toString() {
            return "Ad{" + "profile=" + profile + ", trade_type=" + trade_type + ", temp_price=" + temp_price + ", bank_name=" + bank_name + ", min_amount=" + min_amount + ", temp_price_usd=" + temp_price_usd + ", first_time_limit_btc=" + first_time_limit_btc + ", atm_model=" + atm_model + ", city=" + city + ", location_string=" + location_string + ", countrycode=" + countrycode + ", currency=" + currency + ", limit_to_fiat_amounts=" + limit_to_fiat_amounts + ", created_at=" + created_at + ", max_amount=" + max_amount + ", online_provider=" + online_provider + ", max_amount_available=" + max_amount_available + ", msg=" + msg + ", volume_coefficient_btc=" + volume_coefficient_btc + ", require_feedback_score=" + require_feedback_score + ", ad_id=" + ad_id + ", payment_window_minutes=" + payment_window_minutes + ", hidden_by_opening_hours=" + hidden_by_opening_hours + ", trusted_required=" + trusted_required + ", visible=" + visible + ", require_trusted_by_advertiser=" + require_trusted_by_advertiser + ", is_local_office=" + is_local_office + ", is_low_risk=" + is_low_risk + ", sms_verification_required=" + sms_verification_required + ", require_identification=" + require_identification + ", lat=" + lat + ", lon=" + lon + ", require_trade_volume=" + require_trade_volume + '}';
        }
        
    }

    public static class Profile {

        private final String username, trade_count, last_online, name;
        private final int feedback_score;

        public Profile(String username, String trade_count, String last_online, String name, int feedback_score) {
            this.username = username;
            this.trade_count = trade_count;
            this.last_online = last_online;
            this.name = name;
            this.feedback_score = feedback_score;
        }

        public String getUsername() {
            return username;
        }

        public String getTrade_count() {
            return trade_count;
        }

        public String getLast_online() {
            return last_online;
        }

        public String getName() {
            return name;
        }

        public int getFeedback_score() {
            return feedback_score;
        }

        @Override
        public String toString() {
            return "Profile{" + "username=" + username + ", trade_count=" + trade_count + ", last_online=" + last_online + ", name=" + name + ", feedback_score=" + feedback_score + '}';
        }

    }

}
