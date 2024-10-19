/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.transfertobank;

import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.TransferToBanksFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 *
 * @author carlosmolina
 */
public class TransferToBankGetLastProcesses extends AbstractOperation<ArrayNode> {
    
    private final int size;

    public TransferToBankGetLastProcesses(int size) {
        super(ArrayNode.class);
        this.size = size;
    }
    
    @Override
    protected void execute() {
        ArrayNode processes = mapper.createArrayNode();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(TransferToBanksFolderLocator.getFolder().getPath()));) {
                final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                        .filter(o -> o.toFile().isFile())
                        .sorted((o1, o2) -> {
                            return o1.toFile().getName().compareTo(o2.toFile().getName());
                        })
                        .iterator();
                while (iterator.hasNext()) {
                    Path it = iterator.next();
                    File processFile = it.toFile();
                    JsonNode process = mapper.readTree(processFile);
                    processes.add(process);
                    if(processes.size() == size){
                        break;
                    }
                }
            } catch (IOException ex) {
            }
        super.response = processes;
    }
    
}
