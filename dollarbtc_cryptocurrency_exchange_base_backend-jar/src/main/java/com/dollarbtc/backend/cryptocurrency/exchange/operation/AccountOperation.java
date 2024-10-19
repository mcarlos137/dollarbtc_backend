/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.operation;

import com.dollarbtc.backend.cryptocurrency.exchange.data.LocalData;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBase;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.AccountBaseInterval;
import com.dollarbtc.backend.cryptocurrency.exchange.dto.service.CollectionOrderByDate;
import com.dollarbtc.backend.cryptocurrency.exchange.util.DateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Date;

/**
 *
 * @author CarlosDaniel
 */
public class AccountOperation {

    public static List<AccountBase> getAccounts(String exchangeId, String symbol, String modelName, String initDate, String endDate, AccountBase.Retrieve accountBaseRetrieve, CollectionOrderByDate collectionOrderByDate) {
        List<AccountBase> accounts = LocalData.getAccounts(exchangeId, symbol, modelName, initDate, endDate, 2000, accountBaseRetrieve);
        if (collectionOrderByDate.equals(CollectionOrderByDate.ASC)) {
            Collections.reverse(accounts);
        }
        return accounts;
    }
    
    public static List<AccountBaseInterval> getAccountIntervals(String exchangeId, String symbol, String modelName, String initDate, String endDate, AccountBase.Retrieve accountBaseRetrieve, CollectionOrderByDate collectionOrderByDate) {
        List<AccountBaseInterval> accountIntervals = new ArrayList<>();
        accountIntervals.addAll(LocalData.getAccountIntervals(exchangeId, symbol, modelName, initDate, endDate, 2000, accountBaseRetrieve));
        Map<Date, AccountBaseInterval> accountIntervalsMap = new TreeMap<>(
                (Date o1, Date o2) -> o1.compareTo(o2));
        accountIntervals.stream().forEach((accountBaseInterval) -> {
            if(accountBaseInterval.getStartTimestamp() != null && !accountBaseInterval.getStartTimestamp().equals("")){
                accountIntervalsMap.put(DateUtil.parseDate(accountBaseInterval.getStartTimestamp()), accountBaseInterval);
            }
        });
        accountIntervals.clear();
        accountIntervalsMap.keySet().stream().forEach((key) -> {
            accountIntervals.add(accountIntervalsMap.get(key));
        });
        if (collectionOrderByDate.equals(CollectionOrderByDate.ASC)) {
            accountIntervals.stream().filter((accountInterval) -> !(accountInterval.getAccounts() == null)).forEach((accountInterval) -> {
                Collections.reverse(accountInterval.getAccounts());
            });
        }
        return accountIntervals;
    }
    
}
