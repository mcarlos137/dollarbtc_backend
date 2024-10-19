/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.tag;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.TagsFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class TagList extends AbstractOperation<ArrayNode> {

    public TagList() {
        super(ArrayNode.class);
    }

    @Override
    public void execute() {
        super.response = mapper.createArrayNode();
        try {
            super.response = (ArrayNode) mapper.readTree(TagsFolderLocator.getConfigFile());
        } catch (IOException ex) {
            Logger.getLogger(TagList.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

}
