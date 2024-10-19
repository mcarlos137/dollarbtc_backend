/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.review;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.ReviewsFolderLocator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlosmolina
 */
public class ReviewGetLasts extends AbstractOperation<ArrayNode> {
    
    private final String userName; 
    private final Integer quantity;

    public ReviewGetLasts(String userName, Integer quantity) {
        super(ArrayNode.class);
        this.userName = userName;
        this.quantity = quantity;
    }
    
    @Override
    protected void execute() {
        File reviewsFolder = ReviewsFolderLocator.getFolder();
        if(userName != null && !userName.equals("")){
            reviewsFolder = new File(new File(ReviewsFolderLocator.getFolder(), "UserNames"), userName);
        }
        ArrayNode reviews = mapper.createArrayNode();
        if(!reviewsFolder.isDirectory()){
            super.response = reviews;
            return;
        }
        List<String> reviewFileNames = new ArrayList<>();
        for(File reviewFile : reviewsFolder.listFiles()){
            if(!reviewFile.isFile()){
                continue;
            }
            reviewFileNames.add(reviewFile.getName());
        }
        Collections.sort(reviewFileNames, Collections.reverseOrder());
        int i = 0;
        while(reviewFileNames.size() > i){
            try {
                reviews.add(mapper.readTree(new File(reviewsFolder, reviewFileNames.get(i))));
            } catch (IOException ex) {
                Logger.getLogger(ReviewGetLasts.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
            if(i >= quantity){
                break;
            }
        }
        super.response = reviews;
    }
    
}
