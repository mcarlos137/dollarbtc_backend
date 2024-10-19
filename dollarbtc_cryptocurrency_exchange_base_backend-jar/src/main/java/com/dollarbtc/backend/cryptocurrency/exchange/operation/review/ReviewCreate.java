/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.review;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.review.ReviewCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ReviewsFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class ReviewCreate extends AbstractOperation<String> {
    
    private final ReviewCreateRequest reviewCreateRequest;

    public ReviewCreate(ReviewCreateRequest reviewCreateRequest) {
        super(String.class);
        this.reviewCreateRequest = reviewCreateRequest;
    }
    
    @Override
    protected void execute() {
        File reviewsFolder = ReviewsFolderLocator.getFolder();
        JsonNode review = mapper.createObjectNode();
        String currentTimestamp = DateUtil.getCurrentDate();
        String fileName = DateUtil.getFileDate(currentTimestamp) + "__" + reviewCreateRequest.getOperationId().substring(reviewCreateRequest.getOperationId().length() - 4) + ".json";
        ((ObjectNode) review).put("timestamp", currentTimestamp);
        ((ObjectNode) review).put("userName", reviewCreateRequest.getUserName());
        ((ObjectNode) review).put("operationId", reviewCreateRequest.getOperationId());
        ((ObjectNode) review).put("operationType", reviewCreateRequest.getOperationType());
        ((ObjectNode) review).put("comment", reviewCreateRequest.getComment());
        ((ObjectNode) review).put("starsQuantity", reviewCreateRequest.getStarsQuantity());
        File reviewOperationIdFile = new File(FileUtil.createFolderIfNoExist(new File(reviewsFolder, "OperationIds")), reviewCreateRequest.getOperationId() + ".json");
        if(reviewOperationIdFile.isFile()){
            super.response = "REVIEW ALREADY EXIST FOR OPERATION";
            return; 
        }
        FileUtil.createFile(review, reviewOperationIdFile);
        FileUtil.createFile(review, new File(reviewsFolder, fileName));
        FileUtil.createFile(review, new File(FileUtil.createFolderIfNoExist(new File(FileUtil.createFolderIfNoExist(new File(reviewsFolder, "UserNames")), reviewCreateRequest.getUserName())), fileName));
        super.response = "OK";
    }
    
}
