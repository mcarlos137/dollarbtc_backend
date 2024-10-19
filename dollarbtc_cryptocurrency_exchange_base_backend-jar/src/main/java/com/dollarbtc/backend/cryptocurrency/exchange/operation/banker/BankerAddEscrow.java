/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation.banker;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.banker.BankerAddEscrowRequest;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationStatus;
import com.dollarbtc.backend.cryptocurrency.exchange.enums.BalanceOperationType;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.AbstractOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.operation.BaseOperation;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.BankersFolderLocator;
import com.dollarbtc.backend.cryptocurrency.exchange.util.filelocator.UsersFolderLocator;
import java.io.File;

/**
 *
 * @author carlosmolina
 */
public class BankerAddEscrow extends AbstractOperation<String> {
    
    private final BankerAddEscrowRequest bankerAddEscrowRequest;

    public BankerAddEscrow(BankerAddEscrowRequest bankerAddEscrowRequest) {
        super(String.class);
        this.bankerAddEscrowRequest = bankerAddEscrowRequest;
    }

    @Override
    protected void execute() {
        File userMCBalanceFolder = UsersFolderLocator.getMCBalanceFolder(bankerAddEscrowRequest.getUserName());
        if(!userMCBalanceFolder.isDirectory()){
            super.response = "USER DOES NOT EXIST";
            return;
        }
        File bankerBalanceFolder = BankersFolderLocator.getEscrowFolder(bankerAddEscrowRequest.getUserName());
        if(!bankerBalanceFolder.isDirectory()){
            super.response = "BANKER DOES NOT EXIST";
            return;
        }
        String substract = BaseOperation.substractToBalance(
                userMCBalanceFolder, 
                bankerAddEscrowRequest.getCurrency(), 
                bankerAddEscrowRequest.getAmount(), 
                BalanceOperationType.BANKER_ADD_BALANCE, 
                BalanceOperationStatus.OK, 
                null, 
                null, 
                false, 
                null, 
                false, 
                null
        );
        if(!substract.equals("OK")){
            super.response = substract;
            return;
        }
        BaseOperation.addToBalance(
                bankerBalanceFolder, 
                bankerAddEscrowRequest.getCurrency(), 
                bankerAddEscrowRequest.getAmount(), 
                BalanceOperationType.BANKER_ADD_BALANCE, 
                BalanceOperationStatus.OK,
                null, 
                null, 
                null, 
                false, 
                null
        );
        super.response = "OK";
    }
        
}
