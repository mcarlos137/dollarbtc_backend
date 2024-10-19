/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.address;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.address.AddressCreateRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.FileUtil;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.AddressesFolderLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class AddressCreate extends AbstractOperation<String> {
    
    private final AddressCreateRequest addressCreateRequest;

    public AddressCreate(AddressCreateRequest addressCreateRequest) {
        super(String.class);
        this.addressCreateRequest = addressCreateRequest;
    }
    
    @Override
    protected void execute() {
        File addressesCurrencyFolder = AddressesFolderLocator.getCurrencyFolder(addressCreateRequest.getCurrency());
        File addressesCurrencyAddressFolder = new File(addressesCurrencyFolder, addressCreateRequest.getAddress());
        if (addressesCurrencyAddressFolder.isDirectory()) {
            super.response = "ADDRESS ALREADY EXIST";
            return;
        }
        FileUtil.createFolderIfNoExist(addressesCurrencyAddressFolder);
        JsonNode address = mapper.createObjectNode();
        ((ObjectNode) address).put("address", addressCreateRequest.getAddress());
        ((ObjectNode) address).put("privateKey", addressCreateRequest.getPrivateKey());
        if (addressCreateRequest.getOtcMasterAccount() != null && !addressCreateRequest.getOtcMasterAccount().equals("")) {
            ((ObjectNode) address).put("otcMasterAccount", addressCreateRequest.getOtcMasterAccount());
        }
        if (addressCreateRequest.getWif() != null && !addressCreateRequest.getWif().equals("")) {
            ((ObjectNode) address).put("wif", addressCreateRequest.getWif());
        }
        FileUtil.createFile(address, new File(addressesCurrencyAddressFolder, "config.json"));
        super.response =  "OK";
    }

}
