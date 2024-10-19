/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.OTCFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class OTCGetOperationMessages extends AbstractOperation<ArrayNode> {

    private final String id, side;
    private final boolean old;

    public OTCGetOperationMessages(String id, String side, boolean old) {
        super(ArrayNode.class);
        this.id = id;
        this.side = side;
        this.old = old;
    }

    @Override
    public void execute() {
        ArrayNode operationMessages = mapper.createArrayNode();
        File otcOperationIdMessagesSideFolder = OTCFolderLocator.getOperationIdMessagesSideFolder(null, id, side);
        if (old) {
            otcOperationIdMessagesSideFolder = OTCFolderLocator.getOperationIdMessagesSideOldFolder(null, id, side);
        }
        if (!otcOperationIdMessagesSideFolder.isDirectory()) {
            super.response = operationMessages;
            return;
        }
        Map<String, File> orderedFiles = new TreeMap<>();
        for (File otcOperationIdMessageSideFile : otcOperationIdMessagesSideFolder.listFiles()) {
            if (!otcOperationIdMessageSideFile.isFile()) {
                continue;
            }
            orderedFiles.put(otcOperationIdMessageSideFile.getName().replace(".json", ""), otcOperationIdMessageSideFile);
        }
        File otcOperationIdMessagesSideOldFolder = OTCFolderLocator.getOperationIdMessagesSideOldFolder(null, id, side);
        for (String key : orderedFiles.keySet()) {
            try {
                File operationMessageFile = orderedFiles.get(key);
                if (!operationMessageFile.isFile()) {
                    continue;
                }
                JsonNode operationMessage = mapper.readTree(orderedFiles.get(key));
                if (operationMessage == null) {
                    continue;
                }
                operationMessages.add(operationMessage);
                if (!old) {
                    FileUtil.moveFileToFolder(operationMessageFile, otcOperationIdMessagesSideOldFolder);
                }
            } catch (IOException ex) {
                Logger.getLogger(OTCGetOperationMessages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.response = operationMessages;
    }

}
