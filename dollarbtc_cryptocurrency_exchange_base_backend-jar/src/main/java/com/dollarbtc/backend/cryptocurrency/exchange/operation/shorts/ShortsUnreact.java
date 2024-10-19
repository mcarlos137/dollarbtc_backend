/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsUnreactRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
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
public class ShortsUnreact extends AbstractOperation<String> {

    private final ShortsUnreactRequest shortsUnreactRequest;

    public ShortsUnreact(ShortsUnreactRequest shortsUnreactRequest) {
        super(String.class);
        this.shortsUnreactRequest = shortsUnreactRequest;
    }

    @Override
    public void execute() {
        try {
            if (shortsUnreactRequest.getCommentId() == null) {
                File shortsFile = ShortsFolderLocator.getFile(shortsUnreactRequest.getId());
                JsonNode shorts = mapper.readTree(shortsFile);
                if (shorts.has("reactions") && shorts.get("reactions").has(shortsUnreactRequest.getReaction())) {
                    ((ObjectNode) shorts.get("reactions").get(shortsUnreactRequest.getReaction())).remove(shortsUnreactRequest.getUserName());
                    FileUtil.editFile(shorts, shortsFile);
                }
            } else {
                File shortsCommentsFile = ShortsFolderLocator.getCommentsFile(shortsUnreactRequest.getId(), 0);
                ArrayNode shortsComments = (ArrayNode) mapper.readTree(shortsCommentsFile);
                Iterator<JsonNode> shortsCommentsIterator = shortsComments.iterator();
                while (shortsCommentsIterator.hasNext()) {
                    JsonNode shortsCommentsIt = shortsCommentsIterator.next();
                    if (shortsUnreactRequest.getCommentId().equals(shortsCommentsIt.get("id").textValue())) {
                        if (shortsCommentsIt.get("reactions").has(shortsUnreactRequest.getReaction())) {
                            ((ObjectNode) shortsCommentsIt.get("reactions").get(shortsUnreactRequest.getReaction())).remove(shortsUnreactRequest.getUserName());
                            FileUtil.editFile(shortsComments, shortsCommentsFile);
                            break;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ShortsUnreact.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "OK";
    }

}
