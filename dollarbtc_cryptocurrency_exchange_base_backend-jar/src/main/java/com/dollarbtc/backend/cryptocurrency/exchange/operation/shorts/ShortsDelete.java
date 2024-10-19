/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.shorts;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.shorts.ShortsDeleteRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ShortsFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class ShortsDelete extends AbstractOperation<String> {

    private final ShortsDeleteRequest shortsDeleteRequest;

    public ShortsDelete(ShortsDeleteRequest shortsDeleteRequest) {
        super(String.class);
        this.shortsDeleteRequest = shortsDeleteRequest;
    }

    @Override
    public void execute() {
        File shortsFile = ShortsFolderLocator.getFile(shortsDeleteRequest.getId());
        if (shortsFile.isFile()) {
            FileUtil.deleteFile(shortsFile);
            for (File shortsIndexFolder : ShortsFolderLocator.getIndexesFolder().listFiles()) {
                if (!shortsIndexFolder.isDirectory()) {
                    continue;
                }
                for (File shortsIndexValueFolder : shortsIndexFolder.listFiles()) {
                    if (!shortsIndexValueFolder.isDirectory()) {
                        continue;
                    }
                    FileUtil.deleteFile(new File(shortsIndexValueFolder, shortsDeleteRequest.getId() + ".json"));
                }
            }
            super.response = "OK";
            return;
        }
        super.response = "FAIL";
    }

}
