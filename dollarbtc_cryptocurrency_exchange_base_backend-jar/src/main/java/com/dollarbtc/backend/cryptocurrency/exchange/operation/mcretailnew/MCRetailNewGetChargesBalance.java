/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.mcretailnew;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.mcretailnew.MCRetailNewGetChargesBalanceRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.MoneyclickFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class MCRetailNewGetChargesBalance extends AbstractOperation<JsonNode> {
    
    private final MCRetailNewGetChargesBalanceRequest mcRetailNewGetChargesBalanceRequest;

    public MCRetailNewGetChargesBalance(MCRetailNewGetChargesBalanceRequest mcRetailNewGetChargesBalanceRequest) {
        super(JsonNode.class);
        this.mcRetailNewGetChargesBalanceRequest = mcRetailNewGetChargesBalanceRequest;
    }
    
    @Override
    public void execute() {
        JsonNode chargesBalances = mapper.createObjectNode();
        File chargesFolder = MoneyclickFolderLocator.getRetailChargesFolder(mcRetailNewGetChargesBalanceRequest.getRetailId());
        if (!chargesFolder.isDirectory()) {
            super.response = chargesBalances;
            return;
        }
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(chargesFolder.getPath()));) {
            final Iterator<Path> iterator = StreamSupport.stream(stream.spliterator(), false)
                    .filter(path -> Files.isRegularFile(path))
                    .sorted((o1, o2) -> {
                        String id1 = o1.toFile().getName().replace(".json", "");
                        String id2 = o2.toFile().getName().replace(".json", "");
                        return id1.compareTo(id2);
                    })
                    .iterator();
            while (iterator.hasNext()) {
                Path it = iterator.next();
                JsonNode charge = mapper.readTree(it.toFile());
                if (charge == null) {
                    continue;
                }
                if (!charge.has("timestamp") || charge.get("timestamp") == null) {
                    continue;
                }
                if (!charge.has("status") || charge.get("status").textValue().equals("FAIL")) {
                    continue;
                }
                String timestamp = charge.get("timestamp").textValue();
                if (timestamp == null || timestamp.equals("")) {
                    continue;
                }
                if (mcRetailNewGetChargesBalanceRequest.getInitTimestamp() != null && !mcRetailNewGetChargesBalanceRequest.getInitTimestamp().equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(mcRetailNewGetChargesBalanceRequest.getInitTimestamp())) < 0) {
                        break;
                    }
                }
                if (mcRetailNewGetChargesBalanceRequest.getFinalTimestamp() != null && !mcRetailNewGetChargesBalanceRequest.getFinalTimestamp().equals("")) {
                    if (DateUtil.parseDate(timestamp).compareTo(DateUtil.parseDate(mcRetailNewGetChargesBalanceRequest.getFinalTimestamp())) > 0) {
                        continue;
                    }
                }
                String type = charge.get("type").textValue();
                String currency = charge.get("currency").textValue();
                Double amount = charge.get("amount").doubleValue();
                if (!chargesBalances.has(currency)) {
                    ((ObjectNode) chargesBalances).set(currency, null);
                }
                if (!chargesBalances.get(currency).has(type)) {
                    ((ObjectNode) chargesBalances.get(currency)).put(type, 0.0);
                }
                ((ObjectNode) chargesBalances.get(currency)).put(type, chargesBalances.get(currency).get(type).doubleValue() + amount);
            }
        } catch (IOException ex) {
        }
        super.response = chargesBalances;
    }
    
}
