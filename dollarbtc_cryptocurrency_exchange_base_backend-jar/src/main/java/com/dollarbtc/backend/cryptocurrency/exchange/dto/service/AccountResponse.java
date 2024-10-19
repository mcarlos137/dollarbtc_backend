/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.dto.service;

import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBase;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBaseInterval;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author CarlosDaniel
 */
public class AccountResponse implements Serializable {
    
    private final List<AccountBase> accounts;   
    private final List<AccountBaseInterval> accountIntervals;

    public AccountResponse(List<AccountBase> accounts, List<AccountBaseInterval> accountIntervals) {
        this.accounts = accounts;
        this.accountIntervals = accountIntervals;
    }
    
    public List<AccountBase> getAccounts() {
        return accounts;
    }

    public List<AccountBaseInterval> getAccountIntervals() {
        return accountIntervals;
    }
        
}
