/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsReactRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ShortsReact extends AbstractOperation<String> {

    private final ShortsReactRequest shortsReactRequest;

    public ShortsReact(ShortsReactRequest shortsReactRequest) {
        super(String.class);
        this.shortsReactRequest = shortsReactRequest;
    }

    @Override
    public void execute() {
        try {
            ObjectNode reaction = mapper.createObjectNode();
            reaction.put("timestamp", DateUtil.getCurrentDate());
            reaction.put("name", shortsReactRequest.getName());
            if (shortsReactRequest.getCommentId() == null) {
                File shortsFile = ShortsFolderLocator.getFile(shortsReactRequest.getId());
                JsonNode shorts = mapper.readTree(shortsFile);
                if (!shorts.get("reactions").has(shortsReactRequest.getReaction())) {
                    ((ObjectNode) shorts.get("reactions")).set(shortsReactRequest.getReaction(), mapper.createObjectNode());
                }
                ((ObjectNode) shorts.get("reactions").get(shortsReactRequest.getReaction())).set(shortsReactRequest.getUserName(), reaction);
                FileUtil.editFile(shorts, shortsFile);
            } else {
                File shortsCommentsFile = ShortsFolderLocator.getCommentsFile(shortsReactRequest.getId(), 0);
                ArrayNode shortsComments = (ArrayNode) mapper.readTree(shortsCommentsFile);
                Iterator<JsonNode> shortsCommentsIterator = shortsComments.iterator();
                while (shortsCommentsIterator.hasNext()) {
                    JsonNode shortsCommentsIt = shortsCommentsIterator.next();
                    System.out.println("+>>>>>>>>>>>>>>>>>>> " + shortsCommentsIt.get("id").textValue());
                    System.out.println("->>>>>>>>>>>>>>>>>>> " + shortsReactRequest.getCommentId());
                    System.out.println("1>>>>>>>>>>>>>>>>>>> " + shortsReactRequest.getReaction());
                    System.out.println("2>>>>>>>>>>>>>>>>>>> " + reaction);
                    if (shortsReactRequest.getCommentId().equals(shortsCommentsIt.get("id").textValue())) {
                        if (!shortsCommentsIt.get("reactions").has(shortsReactRequest.getReaction())) {
                            ((ObjectNode) shortsCommentsIt.get("reactions")).set(shortsReactRequest.getReaction(), mapper.createObjectNode());
                        }
                        ((ObjectNode) shortsCommentsIt.get("reactions").get(shortsReactRequest.getReaction())).set(shortsReactRequest.getUserName(), reaction);
                        FileUtil.editFile(shortsComments, shortsCommentsFile);
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ShortsReact.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
