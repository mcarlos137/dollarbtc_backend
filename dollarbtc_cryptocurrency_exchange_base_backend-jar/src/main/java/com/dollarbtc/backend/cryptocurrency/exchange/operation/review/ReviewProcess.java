/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.review;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ReviewsFolderLocator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author carlosmolina
 */
public class ReviewProcess extends AbstractOperation<String> {

    public ReviewProcess() {
        super(String.class);
    }
    
    @Override
    protected void execute() {
        File reviewsFolder = ReviewsFolderLocator.getFolder();
        File reviewsOldFolder = FileUtil.createFolderIfNoExist(new File(reviewsFolder, "Old"));
        if (reviewsFolder.listFiles().length <= 1000) {
            super.response = "OK";
            return;
        }
        List<String> reviewFileNames = new ArrayList<>();
        for (File reviewFile : reviewsFolder.listFiles()) {
            if (!reviewFile.isFile()) {
                continue;
            }
            reviewFileNames.add(reviewFile.getName());
        }
        Collections.sort(reviewFileNames, Collections.reverseOrder());
        int i = 1000;
        while (i < reviewFileNames.size()) {
            FileUtil.moveFileToFolder(new File(reviewsFolder, reviewFileNames.get(i)), reviewsOldFolder);
        }
        super.response = "OK";
    }

}
