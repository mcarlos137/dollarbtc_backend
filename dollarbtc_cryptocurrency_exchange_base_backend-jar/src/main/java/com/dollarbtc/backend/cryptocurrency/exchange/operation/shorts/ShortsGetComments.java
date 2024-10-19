/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ShortsGetComments extends AbstractOperation<ArrayNode> {

    private final String id;

    public ShortsGetComments(String id) {
        super(ArrayNode.class);
        this.id = id;
    }

    @Override
    protected void execute() {
        File shortsCommentsFile = ShortsFolderLocator.getCommentsFile(id, 0);
        ArrayNode shortsComments = mapper.createArrayNode();
        if (shortsCommentsFile.isFile()) {
            try {
                shortsComments = (ArrayNode) mapper.readTree(shortsCommentsFile);
            } catch (IOException ex) {
                Logger.getLogger(ShortsGetComments.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = shortsComments;
    }

}
