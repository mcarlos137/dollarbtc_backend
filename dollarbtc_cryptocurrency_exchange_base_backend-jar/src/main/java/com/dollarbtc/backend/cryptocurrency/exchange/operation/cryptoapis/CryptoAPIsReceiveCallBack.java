/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.cryptoapis;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.cryptoapis.CryptoAPIsReceiveCallBackRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.CryptoAPIsFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class CryptoAPIsReceiveCallBack extends AbstractOperation<String> {

    private final CryptoAPIsReceiveCallBackRequest cryptoAPIsReceiveCallBackRequest;

    public CryptoAPIsReceiveCallBack(CryptoAPIsReceiveCallBackRequest cryptoAPIsReceiveCallBackRequest) {
        super(String.class);
        this.cryptoAPIsReceiveCallBackRequest = cryptoAPIsReceiveCallBackRequest;
    }

    @Override
    protected void execute() {
        System.out.println("-------------------------------------------------------");
        System.out.println("apiVersion: " + cryptoAPIsReceiveCallBackRequest.getApiVersion());
        System.out.println("referenceId: " + cryptoAPIsReceiveCallBackRequest.getReferenceId());
        System.out.println("idempotencyKey: " + cryptoAPIsReceiveCallBackRequest.getIdempotencyKey());
        System.out.println("data: " + cryptoAPIsReceiveCallBackRequest.getData());
        System.out.println("event: " + cryptoAPIsReceiveCallBackRequest.getData().get("event").textValue());
        System.out.println("item: " + cryptoAPIsReceiveCallBackRequest.getData().get("item"));
        System.out.println("blockchain: " + cryptoAPIsReceiveCallBackRequest.getData().get("item").get("blockchain").textValue());
        System.out.println("network: " + cryptoAPIsReceiveCallBackRequest.getData().get("item").get("network").textValue());
        System.out.println("address: " + cryptoAPIsReceiveCallBackRequest.getData().get("item").get("address").textValue());
        System.out.println("transactionId: " + cryptoAPIsReceiveCallBackRequest.getData().get("item").get("transactionId").textValue());
        System.out.println("-------------------------------------------------------");
        String blockchain = cryptoAPIsReceiveCallBackRequest.getData().get("item").get("blockchain").textValue();
        String network = cryptoAPIsReceiveCallBackRequest.getData().get("item").get("network").textValue();
        String event = cryptoAPIsReceiveCallBackRequest.getData().get("event").textValue();
        File cryptoAPIsEventFile = CryptoAPIsFolderLocator.getEventsFile(blockchain, network, event, cryptoAPIsReceiveCallBackRequest.getReferenceId() + "__" + cryptoAPIsReceiveCallBackRequest.getIdempotencyKey());
        if(cryptoAPIsEventFile.isFile()){
            super.response = "OK";
            return;
        }
        FileUtil.createFile(cryptoAPIsReceiveCallBackRequest.getData().get("item"), cryptoAPIsEventFile);
        super.response = "OK";
    }

}
