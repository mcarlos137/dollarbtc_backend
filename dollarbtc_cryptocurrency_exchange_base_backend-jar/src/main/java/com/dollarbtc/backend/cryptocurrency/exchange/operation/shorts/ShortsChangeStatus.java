/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsChangeStatusRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ShortsChangeStatus extends AbstractOperation<String> {

    private final ShortsChangeStatusRequest shortsChangeStatusRequest;

    public ShortsChangeStatus(ShortsChangeStatusRequest shortsChangeStatusRequest) {
        super(String.class);
        this.shortsChangeStatusRequest = shortsChangeStatusRequest;
    }

    @Override
    public void execute() {
        File shortsFile = ShortsFolderLocator.getFile(shortsChangeStatusRequest.getId());
        try {
            JsonNode shorts = mapper.readTree(shortsFile);
            String lastStatus = shorts.get("status").textValue();
            ((ObjectNode) shorts).put("status", shortsChangeStatusRequest.getStatus());
            ((ObjectNode) shorts).put("changeStatusTimestamp", DateUtil.getCurrentDate());
            FileUtil.editFile(shorts, shortsFile);
            FileUtil.moveFileToFolder(new File(ShortsFolderLocator.getIndexValueFolder("Statuses", lastStatus), shortsChangeStatusRequest.getId() + ".json"), ShortsFolderLocator.getIndexValueFolder("Statuses", shortsChangeStatusRequest.getStatus()));
            super.response = "OK";
            return;
        } catch (IOException ex) {
            Logger.getLogger(ShortsChangeStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.response = "FAIL";
    }

}
