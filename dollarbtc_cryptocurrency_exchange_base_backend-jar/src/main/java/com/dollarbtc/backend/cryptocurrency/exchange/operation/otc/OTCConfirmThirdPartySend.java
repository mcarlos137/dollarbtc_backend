/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.otc;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.otc.OTCConfirmThirdPartySendRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;

/**
 *
 * @author carlosmolina
 */
public class OTCConfirmThirdPartySend extends AbstractOperation<String> {
    
    private final OTCConfirmThirdPartySendRequest otcConfirmThirdPartySendRequest;

    public OTCConfirmThirdPartySend(OTCConfirmThirdPartySendRequest otcConfirmThirdPartySendRequest) {
        super(String.class);
        this.otcConfirmThirdPartySendRequest = otcConfirmThirdPartySendRequest;
    }

    @Override
    protected void execute() {
        super.response = "OK";
    }
    
}
