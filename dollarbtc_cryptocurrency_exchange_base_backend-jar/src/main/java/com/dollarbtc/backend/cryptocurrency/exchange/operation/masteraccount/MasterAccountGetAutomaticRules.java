/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.masteraccount;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MasterAccountFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class MasterAccountGetAutomaticRules extends AbstractOperation<ArrayNode> {

    public MasterAccountGetAutomaticRules() {
        super(ArrayNode.class);
    }

    @Override
    public void execute() {
        ArrayNode automaticRules = mapper.createArrayNode();
        File masterAccountAutomaticRulesFile = MasterAccountFolderLocator.getAutomaticRulesFile(null);
        if (!masterAccountAutomaticRulesFile.isFile()) {
            this.response = automaticRules;
            return;
        }
        try {
            this.response = (ArrayNode) mapper.readTree(masterAccountAutomaticRulesFile);
            return;
        } catch (IOException ex) {
            Logger.getLogger(MasterAccountGetAutomaticRules.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.response = automaticRules;
    }

}
