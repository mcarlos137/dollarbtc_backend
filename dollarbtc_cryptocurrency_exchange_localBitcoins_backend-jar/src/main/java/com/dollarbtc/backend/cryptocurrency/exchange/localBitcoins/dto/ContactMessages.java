/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.localBitcoins.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author CarlosDaniel
 */
public class ContactMessages {

    private final List<ContactMessage> contactMessages = new ArrayList<>();

    public ContactMessages(JsonNode jsonNode) {
        ArrayNode arrayNode = (ArrayNode) jsonNode.get("data").get("message_list");
        Iterator<JsonNode> arrayNodeIterator = arrayNode.elements();
        while (arrayNodeIterator.hasNext()) {
            JsonNode arrayNodeIt = arrayNodeIterator.next();
            ContactMessage contactMessage = new ContactMessage(
                    arrayNodeIt.get("msg").textValue(),
                    arrayNodeIt.get("created_at").textValue(),
                    arrayNodeIt.get("is_admin").booleanValue(),
                    new Sender(arrayNodeIt.get("sender").get("username").textValue(),
                            arrayNodeIt.get("sender").get("trade_count").textValue(),
                            arrayNodeIt.get("sender").get("last_online").textValue(),
                            arrayNodeIt.get("sender").get("name").textValue(),
                            arrayNodeIt.get("sender").get("feedback_score").intValue()
                    )
            );
            if(arrayNodeIt.has("attachment_name")){
                contactMessage.setAttachment_name(arrayNodeIt.get("attachment_name").textValue());
            }
            if(arrayNodeIt.has("attachment_type")){
                contactMessage.setAttachment_type(arrayNodeIt.get("attachment_type").textValue());
            }
            if(arrayNodeIt.has("attachment_url")){
                contactMessage.setAttachment_url(arrayNodeIt.get("attachment_url").textValue());
            }
            contactMessages.add(contactMessage);
        }
    }

    public List<ContactMessage> getContactMessages() {
        return contactMessages;
    }

    public static class ContactMessage {

        private final String msg, created_at;
        private final boolean is_admin;
        private final Sender sender;

        private String attachment_name, attachment_type, attachment_url;

        public ContactMessage(String msg, String created_at, boolean is_admin, Sender sender) {
            this.msg = msg;
            this.created_at = created_at;
            this.is_admin = is_admin;
            this.sender = sender;
        }

        public String getMsg() {
            return msg;
        }

        public String getCreated_at() {
            return created_at;
        }

        public boolean isIs_admin() {
            return is_admin;
        }

        public Sender getSender() {
            return sender;
        }

        public String getAttachment_name() {
            return attachment_name;
        }

        public void setAttachment_name(String attachment_name) {
            this.attachment_name = attachment_name;
        }

        public String getAttachment_type() {
            return attachment_type;
        }

        public void setAttachment_type(String attachment_type) {
            this.attachment_type = attachment_type;
        }

        public String getAttachment_url() {
            return attachment_url;
        }

        public void setAttachment_url(String attachment_url) {
            this.attachment_url = attachment_url;
        }

        @Override
        public String toString() {
            return "ContactMessage{" + "msg=" + msg + ", created_at=" + created_at + ", is_admin=" + is_admin + ", sender=" + sender + ", attachment_name=" + attachment_name + ", attachment_type=" + attachment_type + ", attachment_url=" + attachment_url + '}';
        }

    }

    public static class Sender {

        private final String username, trade_count, last_online, name;
        private final int feedback_score;

        public Sender(String username, String trade_count, String last_online, String name, int feedback_score) {
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
            return "Sender{" + "username=" + username + ", trade_count=" + trade_count + ", last_online=" + last_online + ", name=" + name + ", feedback_score=" + feedback_score + '}';
        }

    }

}
