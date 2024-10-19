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
public class ContactInfo {
    
    private final String exchange_rate_updated_at, payment_completed_at, released_at, created_at, reference_code, currency, amount, escrowed_at, amount_btc, canceled_at, closed_at, disputed_at, funded_at, fee_btc;
    private final Advertisement advertisement;
    private final boolean is_buying, is_selling;
    private final int contact_id;
    private final Seller seller;
    private final Buyer buyer;

    public ContactInfo(JsonNode jsonNode) {
        this.exchange_rate_updated_at = jsonNode.get("data").get("exchange_rate_updated_at").textValue();
        this.is_buying = jsonNode.get("data").get("is_buying").booleanValue();
        this.payment_completed_at = jsonNode.get("data").get("payment_completed_at").textValue();
        this.released_at = jsonNode.get("data").get("released_at").textValue();
        this.created_at = jsonNode.get("data").get("created_at").textValue();
        this.reference_code = jsonNode.get("data").get("reference_code").textValue();
        this.contact_id = jsonNode.get("data").get("reference_code").intValue();
        this.currency = jsonNode.get("data").get("currency").textValue();
        this.amount = jsonNode.get("data").get("amount").textValue();
        this.is_selling = jsonNode.get("data").get("is_selling").booleanValue();
        this.escrowed_at = jsonNode.get("data").get("escrowed_at").textValue();
        this.amount_btc = jsonNode.get("data").get("amount_btc").textValue();
        this.canceled_at = jsonNode.get("data").get("canceled_at").textValue();
        this.closed_at = jsonNode.get("data").get("closed_at").textValue();
        this.disputed_at = jsonNode.get("data").get("disputed_at").textValue();
        this.funded_at = jsonNode.get("data").get("funded_at").textValue();
        this.fee_btc = jsonNode.get("data").get("fee_btc").textValue();  
        this.advertisement = new Advertisement(
                jsonNode.get("data").get("advertisement").get("payment_method").textValue(),
                jsonNode.get("data").get("advertisement").get("trade_type").textValue(),
                jsonNode.get("data").get("advertisement").get("id").intValue(),
                new Advertiser(
                        jsonNode.get("data").get("advertisement").get("advertiser").get("username").textValue(),
                        jsonNode.get("data").get("advertisement").get("advertiser").get("trade_count").textValue(), 
                        jsonNode.get("data").get("advertisement").get("advertiser").get("last_online").textValue(), 
                        jsonNode.get("data").get("advertisement").get("advertiser").get("name").textValue(), 
                        jsonNode.get("data").get("advertisement").get("advertiser").get("feedback_score").intValue()
                )
        );
        this.seller = new Seller(
                jsonNode.get("data").get("seller").get("username").textValue(), 
                jsonNode.get("data").get("seller").get("trade_count").textValue(), 
                jsonNode.get("data").get("seller").get("last_online").textValue(), 
                jsonNode.get("data").get("seller").get("name").textValue(), 
                jsonNode.get("data").get("seller").get("feedback_score").intValue()
        );
        this.buyer = new Buyer(
                jsonNode.get("data").get("buyer").get("username").textValue(), 
                jsonNode.get("data").get("buyer").get("countrycode_by_ip").textValue(), 
                jsonNode.get("data").get("buyer").get("name").textValue(), 
                jsonNode.get("data").get("buyer").get("last_online").textValue(), 
                jsonNode.get("data").get("buyer").get("countrycode_by_phone_number").textValue(), 
                jsonNode.get("data").get("buyer").get("trade_count").textValue(), 
                jsonNode.get("data").get("buyer").get("real_name").textValue(), 
                jsonNode.get("data").get("buyer").get("company_name").textValue(), 
                jsonNode.get("data").get("buyer").get("feedback_score").intValue()
        );
    }

    public String getExchange_rate_updated_at() {
        return exchange_rate_updated_at;
    }

    public String getPayment_completed_at() {
        return payment_completed_at;
    }

    public String getReleased_at() {
        return released_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getReference_code() {
        return reference_code;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

    public String getEscrowed_at() {
        return escrowed_at;
    }

    public String getAmount_btc() {
        return amount_btc;
    }

    public String getCanceled_at() {
        return canceled_at;
    }

    public String getClosed_at() {
        return closed_at;
    }

    public String getDisputed_at() {
        return disputed_at;
    }

    public String getFunded_at() {
        return funded_at;
    }

    public String getFee_btc() {
        return fee_btc;
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public boolean isIs_buying() {
        return is_buying;
    }

    public boolean isIs_selling() {
        return is_selling;
    }

    public int getContact_id() {
        return contact_id;
    }

    public Seller getSeller() {
        return seller;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    @Override
    public String toString() {
        return "ContactInfo{" + "exchange_rate_updated_at=" + exchange_rate_updated_at + ", payment_completed_at=" + payment_completed_at + ", released_at=" + released_at + ", created_at=" + created_at + ", reference_code=" + reference_code + ", currency=" + currency + ", amount=" + amount + ", escrowed_at=" + escrowed_at + ", amount_btc=" + amount_btc + ", canceled_at=" + canceled_at + ", closed_at=" + closed_at + ", disputed_at=" + disputed_at + ", funded_at=" + funded_at + ", fee_btc=" + fee_btc + ", advertisement=" + advertisement + ", is_buying=" + is_buying + ", is_selling=" + is_selling + ", contact_id=" + contact_id + ", seller=" + seller + ", buyer=" + buyer + '}';
    }
        
    public static class Advertisement {
        
        private final String payment_method, trade_type;
        private final int id;
        private final Advertiser advertiser;

        public Advertisement(String payment_method, String trade_type, int id, Advertiser advertiser) {
            this.payment_method = payment_method;
            this.trade_type = trade_type;
            this.id = id;
            this.advertiser = advertiser;
        }

        public String getPayment_method() {
            return payment_method;
        }

        public String getTrade_type() {
            return trade_type;
        }

        public int getId() {
            return id;
        }

        public Advertiser getAdvertiser() {
            return advertiser;
        }

        @Override
        public String toString() {
            return "Advertisement{" + "payment_method=" + payment_method + ", trade_type=" + trade_type + ", id=" + id + ", advertiser=" + advertiser + '}';
        }
    
    }
    
    public static class Advertiser {
        
        private final String username, trade_count, last_online, name;
        private final int feedback_score;

        public Advertiser(String username, String trade_count, String last_online, String name, int feedback_score) {
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
            return "Advertiser{" + "username=" + username + ", trade_count=" + trade_count + ", last_online=" + last_online + ", name=" + name + ", feedback_score=" + feedback_score + '}';
        }
                
    }

    public static class Seller {
        
        private final String username, trade_count, last_online, name;
        private final int feedback_score;

        public Seller(String username, String trade_count, String last_online, String name, int feedback_score) {
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
            return "Seller{" + "username=" + username + ", trade_count=" + trade_count + ", last_online=" + last_online + ", name=" + name + ", feedback_score=" + feedback_score + '}';
        }
        
    }
    
    public static class Buyer {
        
        private final String username, countrycode_by_ip, name, last_online, countrycode_by_phone_number, trade_count, real_name, company_name;
        private final int feedback_score;

        public Buyer(String username, String countrycode_by_ip, String name, String last_online, String countrycode_by_phone_number, String trade_count, String real_name, String company_name, int feedback_score) {
            this.username = username;
            this.countrycode_by_ip = countrycode_by_ip;
            this.name = name;
            this.last_online = last_online;
            this.countrycode_by_phone_number = countrycode_by_phone_number;
            this.trade_count = trade_count;
            this.real_name = real_name;
            this.company_name = company_name;
            this.feedback_score = feedback_score;
        }

        public String getUsername() {
            return username;
        }

        public String getCountrycode_by_ip() {
            return countrycode_by_ip;
        }

        public String getName() {
            return name;
        }

        public String getLast_online() {
            return last_online;
        }

        public String getCountrycode_by_phone_number() {
            return countrycode_by_phone_number;
        }

        public String getTrade_count() {
            return trade_count;
        }

        public String getReal_name() {
            return real_name;
        }

        public String getCompany_name() {
            return company_name;
        }

        public int getFeedback_score() {
            return feedback_score;
        }

        @Override
        public String toString() {
            return "Buyer{" + "username=" + username + ", countrycode_by_ip=" + countrycode_by_ip + ", name=" + name + ", last_online=" + last_online + ", countrycode_by_phone_number=" + countrycode_by_phone_number + ", trade_count=" + trade_count + ", real_name=" + real_name + ", company_name=" + company_name + ", feedback_score=" + feedback_score + '}';
        }
                
    }
    
}


